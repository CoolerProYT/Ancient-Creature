package com.coolerpromc.ancientcreature.client.entity.renderer;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.client.animation.bedrock.CreatureAnimator;
import com.coolerpromc.ancientcreature.client.entity.model.AncientCreatureModel;
import com.coolerpromc.ancientcreature.client.entity.state.AncientCreatureRenderState;
import com.coolerpromc.ancientcreature.client.model.bedrock.BakedBedrockModel;
import com.coolerpromc.ancientcreature.client.species.BedrockClientEntity;
import com.coolerpromc.ancientcreature.client.species.ClientSpeciesManager;
import com.coolerpromc.ancientcreature.client.species.SpeciesAppearance;
import com.coolerpromc.ancientcreature.entity.custom.AncientCreatureEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

import java.util.*;

/**
 * Renders every species through its Bedrock client entity. Animation, render-controller choices and
 * events are computed in {@link #extractRenderState}; drawing only replays the stored pose.
 */
public class AncientCreatureRenderer extends MobRenderer<AncientCreatureEntity, AncientCreatureRenderState, AncientCreatureModel> {
    private static final Identifier MISSING_TEXTURE = Identifier.withDefaultNamespace("textures/misc/unknown_pack.png");
    private static final AncientCreatureModel EMPTY = new AncientCreatureModel(AncientCreatureModel.emptyRoot());
    private static final Set<String> WARNED = Collections.synchronizedSet(new HashSet<>());

    /** Bedrock's built-in particle names that have a direct Java counterpart. */
    private static final Map<String, String> BEDROCK_PARTICLES = Map.ofEntries(
        Map.entry("minecraft:basic_smoke_particle", "minecraft:smoke"),
        Map.entry("minecraft:large_explosion", "minecraft:explosion"),
        Map.entry("minecraft:basic_flame_particle", "minecraft:flame"),
        Map.entry("minecraft:critical_hit_emitter", "minecraft:crit"),
        Map.entry("minecraft:water_splash_particle", "minecraft:splash"),
        Map.entry("minecraft:water_wake_particle", "minecraft:fishing"),
        Map.entry("minecraft:villager_happy", "minecraft:happy_villager"),
        Map.entry("minecraft:villager_angry", "minecraft:angry_villager"),
        Map.entry("minecraft:heart_particle", "minecraft:heart"),
        Map.entry("minecraft:basic_bubble_particle", "minecraft:bubble"),
        Map.entry("minecraft:note_particle", "minecraft:note"),
        Map.entry("minecraft:evaporation_elephant_toothpaste_vapor_particle", "minecraft:cloud"));

    /** One animator per entity; weak keys so removed entities are forgotten. */
    private final Map<AncientCreatureEntity, CreatureAnimator> animators = new WeakHashMap<>();

    public AncientCreatureRenderer(EntityRendererProvider.Context context) {
        super(context, EMPTY, 0.5F);
        this.addLayer(new TextureLayers(this));
    }

    @Override
    public AncientCreatureRenderState createRenderState() {
        return new AncientCreatureRenderState();
    }

    @Override
    public void extractRenderState(AncientCreatureEntity entity, AncientCreatureRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);

        state.species = entity.getSpecies();
        state.variant = entity.getVariant();
        state.variantIndex = Math.max(0, entity.speciesDefinition().variantIndex(state.variant));
        state.action = entity.getAction();
        state.isSprinting = entity.isSprinting();
        state.isAggressive = entity.isAggressive();
        state.isOnGround = entity.onGround();
        state.hunger = entity.getHunger();
        state.isHungry = entity.hungerProperties().isHungryAt(entity.getHunger());
        state.creatureHealth = entity.getHealth();
        state.creatureMaxHealth = entity.getMaxHealth();
        state.hurtTimeRemaining = entity.hurtTime;
        state.hasOwner = entity.hasOwnerClientSide();
        state.isSaddled = entity.isSaddled();
        state.armorTier = entity.getArmorTier();
        state.isSitting = entity.isSitting();

        SpeciesAppearance appearance = ClientSpeciesManager.INSTANCE.appearance(state.species.id());
        state.appearance = appearance;
        if (appearance == null) {
            state.bakedModel = null;
            state.bonePose = null;
            state.textures = List.of();
            state.renderScale = 1.0F;
            state.shadowRadius = 0.5F;
            return;
        }
        state.renderScale = appearance.settings().renderScale();
        state.shadowRadius = appearance.settings().shadowRadius();

        CreatureAnimator animator = this.animators.computeIfAbsent(entity, e -> new CreatureAnimator());
        animator.host().bind(entity, state, partialTicks, animator);
        CreatureAnimator.RenderChoice choice;
        try {
            choice = animator.update(appearance, (entity.tickCount + partialTicks) / 20.0F, state.variant);
        } catch (RuntimeException e) {
            if (WARNED.add("crash:" + state.species.id())) {
                Constants.LOG.error("Animating species '{}' failed; it will render unanimated", state.species.id(), e);
            }
            state.bakedModel = null;
            state.bonePose = null;
            animator.host().unbind();
            return;
        }

        state.bakedModel = choice.model();
        state.bonePose = choice.model() == null ? null : AncientCreatureModel.snapshot(choice.model());
        state.textures = choice.textures();
        state.material = choice.material();
        state.tint = choice.tint();
        state.ignoreLighting = choice.ignoreLighting();
        state.hurtOverlay = choice.hurtOverlay();
        if (!state.hurtOverlay) {
            state.hasRedOverlay = false;
        }
        if (state.ignoreLighting || state.material.contains("emissive")) {
            state.lightCoords = LightCoordsUtil.FULL_BRIGHT;
        }

        List<CreatureAnimator.PendingEffect> effects = animator.drainEffects();
        if (!effects.isEmpty() && choice.model() != null && entity.level().getEntity(entity.getId()) == entity) {
            this.playEffects(entity, state, appearance.entity(), choice.model(), effects, partialTicks);
        }
        animator.host().unbind();
    }

    private void playEffects(AncientCreatureEntity entity, AncientCreatureRenderState state, BedrockClientEntity clientEntity, BakedBedrockModel model, List<CreatureAnimator.PendingEffect> effects, float partialTicks) {
        for (CreatureAnimator.PendingEffect effect : effects) {
            Vector3f at = this.effectPosition(entity, state, model, effect.locator().orElse(null), partialTicks);
            if (effect.sound()) {
                String sound = clientEntity.soundEffects().get(effect.effect());
                SoundEvent event = sound == null ? null : resolveSound(sound, clientEntity.identifier().getNamespace());
                if (event == null) {
                    if (WARNED.add("sound:" + clientEntity.identifier() + "#" + effect.effect())) {
                        Constants.LOG.warn("Client entity '{}' plays sound effect '{}', which is not mapped to a registered sound event", clientEntity.identifier(), effect.effect());
                    }
                    continue;
                }
                entity.level().playLocalSound(at.x, at.y, at.z, event, entity.getSoundSource(), 1.0F, 1.0F, false);
            } else {
                String particle = clientEntity.particleEffects().get(effect.effect());
                ParticleOptions options = particle == null ? null : resolveParticle(particle);
                if (options == null) {
                    if (WARNED.add("particle:" + clientEntity.identifier() + "#" + effect.effect())) {
                        Constants.LOG.warn("Client entity '{}' spawns particle effect '{}', which has no simple Java particle equivalent", clientEntity.identifier(), effect.effect());
                    }
                    continue;
                }
                entity.level().addParticle(options, at.x, at.y, at.z, 0.0, 0.0, 0.0);
            }
        }
    }

    /** World position of a locator on the current pose, or the entity's eye position. */
    private Vector3f effectPosition(AncientCreatureEntity entity, AncientCreatureRenderState state, BakedBedrockModel model, @Nullable String locator, float partialTicks) {
        double x = Mth.lerp(partialTicks, entity.xo, entity.getX());
        double y = Mth.lerp(partialTicks, entity.yo, entity.getY());
        double z = Mth.lerp(partialTicks, entity.zo, entity.getZ());
        if (locator != null && model.locator(locator) != null) {
            PoseStack pose = new PoseStack();
            pose.scale(state.scale, state.scale, state.scale);
            pose.rotateDegrees(Axis.YP, 180.0F - state.bodyRot);
            pose.scale(-1.0F, -1.0F, 1.0F);
            float scale = state.ageScale * state.renderScale;
            pose.scale(scale, scale, scale);
            pose.translate(0.0F, -1.501F, 0.0F);
            model.transformToLocator(locator, pose);
            Vector3f local = pose.last().pose().transformPosition(new Vector3f());
            return new Vector3f((float) x + local.x, (float) y + local.y, (float) z + local.z);
        }
        return new Vector3f((float) x, (float) y + entity.getEyeHeight(), (float) z);
    }

    private static @Nullable SoundEvent resolveSound(String name, String namespace) {
        Identifier id = name.contains(":") ? Identifier.tryParse(name) : Identifier.tryBuild(namespace, name);
        if (id != null && BuiltInRegistries.SOUND_EVENT.containsKey(id)) {
            return BuiltInRegistries.SOUND_EVENT.getValue(id);
        }
        if (!name.contains(":")) {
            Identifier vanilla = Identifier.tryParse(name);
            if (vanilla != null && BuiltInRegistries.SOUND_EVENT.containsKey(vanilla)) {
                return BuiltInRegistries.SOUND_EVENT.getValue(vanilla);
            }
        }
        // Sounds defined only in a resource pack's sounds.json still play through a direct event.
        return id == null ? null : SoundEvent.createVariableRangeEvent(id);
    }

    private static @Nullable ParticleOptions resolveParticle(String name) {
        Identifier id = Identifier.tryParse(BEDROCK_PARTICLES.getOrDefault(name, name));
        if (id == null) {
            return null;
        }
        ParticleType<?> type = BuiltInRegistries.PARTICLE_TYPE.getValue(id);
        return type instanceof SimpleParticleType simple ? simple : null;
    }

    @Override
    public void submit(AncientCreatureRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        if (state.bakedModel == null) {
            return;
        }
        this.model = AncientCreatureModel.of(state.bakedModel);
        super.submit(state, poseStack, collector, camera);
    }

    @Override
    protected @Nullable RenderType getRenderType(AncientCreatureRenderState state, boolean isBodyVisible, boolean forceTransparent, boolean appearGlowing) {
        if (isBodyVisible && !forceTransparent) {
            Identifier texture = this.getTextureLocation(state);
            return translucent(state.material) ? RenderTypes.entityTranslucent(texture) : RenderTypes.entityCutout(texture);
        }
        return super.getRenderType(state, isBodyVisible, forceTransparent, appearGlowing);
    }

    private static boolean translucent(String material) {
        return material.contains("alphablend") || material.contains("translucent") || material.contains("blend");
    }

    @Override
    protected int getModelTint(AncientCreatureRenderState state) {
        return state.tint;
    }

    @Override
    protected float getShadowRadius(AncientCreatureRenderState state) {
        return state.shadowRadius * state.ageScale;
    }

    @Override
    protected void scale(AncientCreatureRenderState state, PoseStack poseStack) {
        float scale = state.ageScale * state.renderScale;
        poseStack.scale(scale, scale, scale);
    }

    @Override
    public Identifier getTextureLocation(AncientCreatureRenderState state) {
        return state.textures.isEmpty() ? MISSING_TEXTURE : state.textures.getFirst();
    }

    /** Draws the second and later render-controller textures over the body, like Bedrock texture layering. */
    private static final class TextureLayers extends RenderLayer<AncientCreatureRenderState, AncientCreatureModel> {
        TextureLayers(RenderLayerParent<AncientCreatureRenderState, AncientCreatureModel> parent) {
            super(parent);
        }

        @Override
        public void submit(PoseStack poseStack, SubmitNodeCollector collector, int lightCoords, AncientCreatureRenderState state, float yRot, float xRot) {
            if (state.isInvisible || state.bakedModel == null || state.textures.size() < 2) {
                return;
            }
            AncientCreatureModel model = AncientCreatureModel.of(state.bakedModel);
            for (int i = 1; i < state.textures.size(); i++) {
                collector.order(i).submitModel(model, state, poseStack, RenderTypes.entityTranslucent(state.textures.get(i)), lightCoords,
                    LivingEntityRenderer.getOverlayCoords(state, 0.0F), state.tint, null, state.outlineColor);
            }
        }
    }
}
