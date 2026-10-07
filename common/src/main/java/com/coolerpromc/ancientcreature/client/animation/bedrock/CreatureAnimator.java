package com.coolerpromc.ancientcreature.client.animation.bedrock;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.client.model.bedrock.BakedBedrockModel;
import com.coolerpromc.ancientcreature.client.model.bedrock.BedrockGeometryManager;
import com.coolerpromc.ancientcreature.client.species.BedrockClientEntity;
import com.coolerpromc.ancientcreature.client.species.BedrockRenderController;
import com.coolerpromc.ancientcreature.client.species.SpeciesAppearance;
import com.coolerpromc.ancientcreature.molang.MolangContext;
import com.coolerpromc.ancientcreature.molang.MolangExpression;
import com.coolerpromc.ancientcreature.molang.MolangResources;
import com.coolerpromc.ancientcreature.molang.MolangVariables;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

import java.util.*;

/**
 * Runs one entity's Bedrock animation stack, frame by frame, the way the Bedrock client does:
 * {@code initialize} once, then each frame {@code pre_animation}, the {@code animate} list (plain
 * animations and controllers, each with a blend weight), and the render controllers that pick
 * geometry, texture layers, part visibility and tint.
 *
 * <p>One animator exists per rendered entity and owns that entity's {@code variable.*} storage. It is
 * used from the render thread only.
 */
public final class CreatureAnimator {
    private static final Set<String> WARNINGS = Collections.synchronizedSet(new HashSet<>());

    private final MolangVariables variables = new MolangVariables();
    private final CreatureQueryHost host = new CreatureQueryHost();
    private final MolangContext ctx = new MolangContext(this.variables, this.host);

    private @Nullable SpeciesAppearance appearance;
    private boolean initialized;
    private float lastTime = Float.NaN;

    /** Players for plain animations in the animate list, by short name. */
    private final Map<String, AnimationPlayer> direct = new HashMap<>();
    private final Set<String> directActive = new HashSet<>();
    private final Map<String, ControllerRun> controllers = new HashMap<>();

    /** Queued during advance, emitted after posing so locators are known. */
    private final List<PendingEffect> pending = new ArrayList<>();

    public record PendingEffect(boolean sound, String effect, Optional<String> locator) {
    }

    /** What the render controllers chose for this frame. */
    public record RenderChoice(@Nullable BakedBedrockModel model, List<Identifier> textures, String material, int tint, boolean ignoreLighting, boolean hurtOverlay, Set<String> hiddenBones) {
    }

    record Weighted(AnimationPlayer player, float weight) {
    }

    public CreatureQueryHost host() {
        return this.host;
    }

    public MolangContext context() {
        return this.ctx;
    }

    public List<PendingEffect> drainEffects() {
        if (this.pending.isEmpty()) {
            return List.of();
        }
        List<PendingEffect> out = List.copyOf(this.pending);
        this.pending.clear();
        return out;
    }

    private void reset(SpeciesAppearance appearance) {
        this.appearance = appearance;
        this.initialized = false;
        this.direct.clear();
        this.directActive.clear();
        this.controllers.clear();
        this.variables.clear();
        this.pending.clear();
    }

    /**
     * Advances to {@code nowSeconds}, then picks geometry, poses it and decides how to draw it.
     * The returned model has been reset and posed for this entity and must be snapshotted before any
     * other entity uses it.
     */
    public RenderChoice update(SpeciesAppearance appearance, float nowSeconds, String variant) {
        if (this.appearance != appearance) {
            this.reset(appearance);
        }
        BedrockClientEntity entity = appearance.entity();
        this.ctx.resources(MolangResources.NONE);

        float delta = Float.isNaN(this.lastTime) ? 0.0F : Mth.clamp(nowSeconds - this.lastTime, 0.0F, 0.5F);
        this.lastTime = nowSeconds;
        this.host.deltaTime(delta);

        if (!this.initialized) {
            this.initialized = true;
            for (MolangExpression script : entity.scripts().initialize()) {
                script.execute(this.ctx);
            }
        }
        for (MolangExpression script : entity.scripts().preAnimation()) {
            script.execute(this.ctx);
        }

        // 1. Render controllers decide the geometry before anything is posed.
        RenderSelection selection = this.selectRender(appearance, variant);
        BakedBedrockModel model = selection.model;

        // 2. Advance the animate list and collect what poses the model.
        List<Weighted> posing = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        for (BedrockClientEntity.AnimateEntry entry : entity.scripts().animate()) {
            String shortName = entry.name().toLowerCase(Locale.ROOT);
            seen.add(shortName);
            float weight = (float) entry.weight().evaluate(this.ctx);
            String target = entity.animations().getOrDefault(shortName, entry.name());

            AnimationController controller = AnimationControllerManager.INSTANCE.get(target);
            if (controller != null) {
                ControllerRun run = this.controllers.computeIfAbsent(shortName, k -> new ControllerRun(controller));
                if (run.controller != controller) {
                    run = new ControllerRun(controller);
                    this.controllers.put(shortName, run);
                }
                run.update(delta, entity, posing, Math.max(0.0F, weight));
                continue;
            }

            BedrockAnimation animation = this.resolveAnimation(entity, entry.name());
            if (animation == null) {
                warnOnce("animate:" + entity.identifier() + "#" + entry.name(), "Client entity '{}' animates '{}', which is not a loaded animation or controller", entity.identifier(), entry.name());
                continue;
            }
            if (weight <= 0.0F) {
                this.directActive.remove(shortName);
                continue;
            }
            AnimationPlayer player = this.direct.get(shortName);
            if (player == null || player.animation() != animation) {
                player = new AnimationPlayer(animation);
                this.direct.put(shortName, player);
                this.directActive.remove(shortName);
            }
            if (this.directActive.add(shortName)) {
                player.restart(this.ctx);
            }
            this.advance(player, delta);
            posing.add(new Weighted(player, weight));
        }
        this.direct.keySet().retainAll(seen);
        this.controllers.keySet().retainAll(seen);

        // 3. Pose.
        if (model != null) {
            model.resetPose();
            Set<String> missing = WARNINGS;
            List<BedrockAnimation.EntityRelativeRotation> relative = new ArrayList<>();
            for (Weighted weighted : posing) {
                if (!weighted.player.isPosing()) {
                    continue;
                }
                this.host.animTime(weighted.player.animTime());
                float blend = weighted.weight * (float) weighted.player.animation().blendWeight().evaluate(this.ctx);
                weighted.player.animation().apply(model, weighted.player.animTime(), blend, this.ctx, missing, relative);
            }
            resolveEntityRelative(model, relative);
            for (String hidden : selection.hiddenBones) {
                ModelPart part = model.bone(hidden);
                if (part != null) {
                    part.visible = false;
                }
            }
        }
        return new RenderChoice(model, selection.textures, selection.material, selection.tint, selection.ignoreLighting, selection.hurtOverlay, selection.hiddenBones);
    }

    private void advance(AnimationPlayer player, float delta) {
        player.advance(delta, this.ctx, this.host, new AnimationPlayer.EventSink() {
            @Override
            public void sound(BedrockAnimation.TimedEffect effect) {
                CreatureAnimator.this.pending.add(new PendingEffect(true, effect.effect(), effect.locator()));
            }

            @Override
            public void particle(BedrockAnimation.TimedEffect effect) {
                CreatureAnimator.this.pending.add(new PendingEffect(false, effect.effect(), effect.locator()));
            }
        });
    }

    private @Nullable BedrockAnimation resolveAnimation(BedrockClientEntity entity, String name) {
        String target = entity.animations().get(name.toLowerCase(Locale.ROOT));
        if (target != null) {
            BedrockAnimation animation = BedrockAnimationManager.INSTANCE.get(target);
            if (animation != null) {
                return animation;
            }
        }
        return BedrockAnimationManager.INSTANCE.get(name);
    }

    boolean allAnimationsFinished() {
        for (ControllerRun run : this.controllers.values()) {
            if (!run.allFinished()) {
                return false;
            }
        }
        return true;
    }

    boolean anyAnimationFinished() {
        for (ControllerRun run : this.controllers.values()) {
            if (run.anyFinished()) {
                return true;
            }
        }
        return false;
    }

    // ------------------------------------------------------------------ controllers

    private final class ControllerRun {
        final AnimationController controller;
        String state;
        float stateTime;
        boolean entered;
        final Map<String, AnimationPlayer> players = new LinkedHashMap<>();

        @Nullable String previous;
        float sinceTransition;
        AnimationController.BlendCurve blend = AnimationController.BlendCurve.NONE;
        final Map<String, AnimationPlayer> previousPlayers = new LinkedHashMap<>();
        float previousWeight;

        ControllerRun(AnimationController controller) {
            this.controller = controller;
            this.state = controller.initialState();
        }

        void update(float delta, BedrockClientEntity entity, List<Weighted> out, float weight) {
            if (!this.entered) {
                this.entered = true;
                this.enter(entity);
            }

            // One transition per frame, checked with q.anim_time = time in the current state.
            CreatureAnimator.this.host.animTime(this.stateTime);
            for (AnimationController.Transition transition : this.controller.state(this.state).transitions()) {
                if (transition.condition().test(CreatureAnimator.this.ctx)) {
                    if (!transition.target().equals(this.state)) {
                        this.transition(transition.target(), entity);
                    }
                    break;
                }
            }

            this.stateTime += delta;
            for (AnimationPlayer player : this.players.values()) {
                CreatureAnimator.this.advance(player, delta);
            }

            float outgoing = 0.0F;
            if (this.previous != null) {
                this.sinceTransition += delta;
                outgoing = this.blend.outgoingWeight(this.sinceTransition);
                if (outgoing <= 0.0F) {
                    this.previous = null;
                    this.previousPlayers.clear();
                } else {
                    for (AnimationPlayer player : this.previousPlayers.values()) {
                        CreatureAnimator.this.advance(player, delta);
                    }
                }
            }

            AnimationController.State current = this.controller.state(this.state);
            for (AnimationController.StateAnimation animation : current.animations()) {
                AnimationPlayer player = this.players.get(animation.name().toLowerCase(Locale.ROOT));
                if (player != null) {
                    CreatureAnimator.this.host.animTime(player.animTime());
                    float w = (float) animation.weight().evaluate(CreatureAnimator.this.ctx);
                    out.add(new Weighted(player, weight * w * (1.0F - outgoing)));
                }
            }
            if (this.previous != null && outgoing > 0.0F) {
                AnimationController.State old = this.controller.state(this.previous);
                for (AnimationController.StateAnimation animation : old.animations()) {
                    AnimationPlayer player = this.previousPlayers.get(animation.name().toLowerCase(Locale.ROOT));
                    if (player != null) {
                        CreatureAnimator.this.host.animTime(player.animTime());
                        float w = (float) animation.weight().evaluate(CreatureAnimator.this.ctx);
                        out.add(new Weighted(player, weight * w * outgoing));
                    }
                }
            }
        }

        private void transition(String target, BedrockClientEntity entity) {
            AnimationController.State leaving = this.controller.state(this.state);
            for (MolangExpression script : leaving.onExit()) {
                script.execute(CreatureAnimator.this.ctx);
            }
            this.blend = leaving.blend();
            if (this.blend.duration() > 0.0F) {
                this.previous = this.state;
                this.previousPlayers.clear();
                this.previousPlayers.putAll(this.players);
                this.sinceTransition = 0.0F;
            } else {
                this.previous = null;
                this.previousPlayers.clear();
            }
            this.state = target;
            this.enter(entity);
        }

        private void enter(BedrockClientEntity entity) {
            AnimationController.State state = this.controller.state(this.state);
            this.stateTime = 0.0F;
            this.players.clear();
            for (AnimationController.StateAnimation animation : state.animations()) {
                String key = animation.name().toLowerCase(Locale.ROOT);
                BedrockAnimation resolved = CreatureAnimator.this.resolveAnimation(entity, animation.name());
                if (resolved == null) {
                    warnOnce("state:" + this.controller.name() + "#" + animation.name(), "Animation controller '{}' state '{}' plays '{}', which the client entity '{}' does not define", this.controller.name(), this.state, animation.name(), entity.identifier());
                    continue;
                }
                AnimationPlayer player = new AnimationPlayer(resolved);
                player.restart(CreatureAnimator.this.ctx);
                this.players.put(key, player);
            }
            for (MolangExpression script : state.onEntry()) {
                script.execute(CreatureAnimator.this.ctx);
            }
            for (String sound : state.soundEffects()) {
                CreatureAnimator.this.pending.add(new PendingEffect(true, sound, Optional.empty()));
            }
            for (AnimationController.StateParticle particle : state.particleEffects()) {
                particle.preEffectScript().ifPresent(script -> script.execute(CreatureAnimator.this.ctx));
                CreatureAnimator.this.pending.add(new PendingEffect(false, particle.effect(), particle.locator()));
            }
        }

        boolean allFinished() {
            for (AnimationPlayer player : this.players.values()) {
                if (!player.isFinished()) {
                    return false;
                }
            }
            return true;
        }

        boolean anyFinished() {
            for (AnimationPlayer player : this.players.values()) {
                if (player.isFinished()) {
                    return true;
                }
            }
            return false;
        }
    }

    // ------------------------------------------------------------------ render controllers

    private record RenderSelection(@Nullable BakedBedrockModel model, List<Identifier> textures, String material, int tint, boolean ignoreLighting, boolean hurtOverlay, Set<String> hiddenBones) {
    }

    private RenderSelection selectRender(SpeciesAppearance appearance, String variant) {
        BedrockClientEntity entity = appearance.entity();
        String geometryKey = "default";
        List<String> textureKeys = new ArrayList<>();
        String material = entity.materials().getOrDefault("default", "entity_alphatest");
        int tint = -1;
        boolean ignoreLighting = false;
        boolean hurtOverlay = true;
        Set<String> hidden = new HashSet<>();
        boolean anyController = false;
        BakedBedrockModel model = null;

        for (SpeciesAppearance.ActiveRenderController active : appearance.renderControllers()) {
            this.ctx.resources(MolangResources.NONE);
            if (!active.condition().test(this.ctx)) {
                continue;
            }
            anyController = true;
            BedrockRenderController rc = active.controller();
            this.ctx.resources(rc);

            Object geometry = rc.geometry().evaluateObject(this.ctx);
            if (geometry instanceof MolangResources.ResourceRef ref && ref.category().equals("geometry")) {
                geometryKey = ref.name();
            }
            for (MolangExpression texture : rc.textures()) {
                Object value = texture.evaluateObject(this.ctx);
                if (value instanceof MolangResources.ResourceRef ref && ref.category().equals("texture")) {
                    textureKeys.add(ref.name());
                }
            }
            for (BedrockRenderController.PatternRule rule : rc.materials()) {
                if (rule.pattern().equals("*")) {
                    Object value = rule.value().evaluateObject(this.ctx);
                    if (value instanceof MolangResources.ResourceRef ref && ref.category().equals("material")) {
                        material = entity.materials().getOrDefault(ref.name(), material);
                    }
                }
            }
            String geometryReference = entity.geometry().get(geometryKey);
            model = geometryReference == null ? null : BedrockGeometryManager.INSTANCE.getBaked(geometryReference);
            if (model != null && !rc.partVisibility().isEmpty()) {
                for (String bone : model.bones().keySet()) {
                    Boolean visible = null;
                    for (BedrockRenderController.PatternRule rule : rc.partVisibility()) {
                        if (rule.matches(bone)) {
                            visible = rule.value().test(this.ctx);
                        }
                    }
                    if (visible != null && !visible) {
                        hidden.add(bone);
                    }
                }
            }
            if (rc.color().isPresent()) {
                tint = rc.color().get().argb(this.ctx);
            }
            if (rc.hurtColor().isPresent() && rc.hurtColor().get().alpha(this.ctx) <= 0.0F) {
                hurtOverlay = false;
            }
            ignoreLighting |= rc.ignoreLighting();
        }
        this.ctx.resources(MolangResources.NONE);

        if (!anyController) {
            // No render controller: Bedrock would draw nothing; draw the default geometry with the
            // texture named after the variant, falling back to "default", which is what species
            // files from before render controllers expect.
            String reference = entity.geometry().get("default");
            model = reference == null ? null : BedrockGeometryManager.INSTANCE.getBaked(reference);
            textureKeys.add(entity.textures().containsKey(variant.toLowerCase(Locale.ROOT)) ? variant : "default");
        }

        List<Identifier> textures = new ArrayList<>();
        for (String key : textureKeys) {
            Identifier texture = entity.textures().get(key.toLowerCase(Locale.ROOT));
            if (texture != null) {
                textures.add(texture);
            } else {
                warnOnce("texture:" + entity.identifier() + "#" + key, "Client entity '{}' has no texture '{}'", entity.identifier(), key);
            }
        }
        if (model == null) {
            warnOnce("geometry:" + entity.identifier() + "#" + geometryKey, "Client entity '{}' has no loaded geometry for '{}'", entity.identifier(), geometryKey);
        }
        return new RenderSelection(model, textures, material, tint, ignoreLighting, hurtOverlay, hidden);
    }

    // ------------------------------------------------------------------ helpers

    /**
     * Applies rotations authored in entity space ({@code relative_to: {rotation: "entity"}}): the bone's
     * final local rotation is the requested rotation with its parents' accumulated rotation removed,
     * which is what Blockbench's preview does.
     */
    private static void resolveEntityRelative(BakedBedrockModel model, List<BedrockAnimation.EntityRelativeRotation> relative) {
        for (BedrockAnimation.EntityRelativeRotation rotation : relative) {
            ModelPart part = model.bone(rotation.bone());
            if (part == null) {
                continue;
            }
            Quaternionf parents = new Quaternionf();
            List<ModelPart> chain = model.chain(rotation.bone());
            for (int i = 0; i < chain.size() - 1; i++) {
                ModelPart p = chain.get(i);
                parents.mul(new Quaternionf().rotationZYX(p.zRot, p.yRot, p.xRot));
            }
            Vector3f d = rotation.degrees();
            Quaternionf desired = new Quaternionf().rotationZYX(d.z * Mth.DEG_TO_RAD, d.y * Mth.DEG_TO_RAD, d.x * Mth.DEG_TO_RAD);
            Quaternionf local = parents.conjugate().mul(desired);
            Quaternionf current = new Quaternionf().rotationZYX(part.zRot, part.yRot, part.xRot);
            Quaternionf blended = current.slerp(local, Mth.clamp(rotation.weight(), 0.0F, 1.0F));
            Vector3f euler = blended.getEulerAnglesZYX(new Vector3f());
            part.setRotation(euler.x, euler.y, euler.z);
        }
    }

    private static void warnOnce(String key, String message, Object... args) {
        if (WARNINGS.add(key)) {
            Constants.LOG.warn(message, args);
        }
    }
}
