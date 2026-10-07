package com.coolerpromc.ancientcreature.client.animation.bedrock;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.client.model.bedrock.BakedBedrockModel;
import com.coolerpromc.ancientcreature.client.model.bedrock.BedrockFormatException;
import com.coolerpromc.ancientcreature.client.model.bedrock.BedrockJson;
import com.coolerpromc.ancientcreature.molang.Molang;
import com.coolerpromc.ancientcreature.molang.MolangContext;
import com.coolerpromc.ancientcreature.molang.MolangExpression;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import org.joml.Vector3f;

import java.util.*;

/**
 * A Bedrock animation ({@code "animation.name": {...}} in an {@code .animation.json} file).
 *
 * <p>Supports {@code animation_length}, {@code loop} ({@code true}, {@code false} or
 * {@code "hold_on_last_frame"}), {@code start_delay}, {@code loop_delay}, {@code anim_time_update},
 * {@code blend_weight}, {@code override_previous_animation}, bone rotation/position/scale channels with
 * Molang keyframes, {@code relative_to: {rotation: "entity"}}, and the {@code sound_effects},
 * {@code particle_effects} and {@code timeline} event tracks. Per-instance timing (delays, time update,
 * event firing) lives in {@link AnimationPlayer}; this class is immutable and shared.
 *
 * <p>Keyframe values are applied to vanilla model parts as Blockbench's Java exporter does: rotation is
 * added as-is (degrees), position as {@code (x, -y, z)} and scale multiplies.
 */
public final class BedrockAnimation {
    public enum LoopMode { ONCE, LOOP, HOLD_ON_LAST_FRAME }

    private final String name;
    private final float length;
    private final LoopMode loop;
    private final MolangExpression startDelay;
    private final MolangExpression loopDelay;
    private final Optional<MolangExpression> animTimeUpdate;
    private final MolangExpression blendWeight;
    private final boolean overridePrevious;
    private final Map<String, BoneAnimation> bones;
    private final List<TimedEffect> soundEffects;
    private final List<TimedEffect> particleEffects;
    private final List<TimedScript> timeline;

    public record BoneAnimation(BedrockAnimationChannel rotation, BedrockAnimationChannel position, BedrockAnimationChannel scale, boolean rotationRelativeToEntity) {
        float lastTime() {
            return Math.max(this.rotation.lastTime(), Math.max(this.position.lastTime(), this.scale.lastTime()));
        }
    }

    /** A sound or particle event: {@code effect} is a short name resolved through the client entity. */
    public record TimedEffect(float time, String effect, Optional<String> locator, Optional<MolangExpression> preEffectScript) {
    }

    public record TimedScript(float time, List<MolangExpression> scripts) {
    }

    public BedrockAnimation(String name, float length, LoopMode loop, MolangExpression startDelay, MolangExpression loopDelay, Optional<MolangExpression> animTimeUpdate, MolangExpression blendWeight, boolean overridePrevious, Map<String, BoneAnimation> bones, List<TimedEffect> soundEffects, List<TimedEffect> particleEffects, List<TimedScript> timeline) {
        this.name = name;
        this.loop = loop;
        this.startDelay = startDelay;
        this.loopDelay = loopDelay;
        this.animTimeUpdate = animTimeUpdate;
        this.blendWeight = blendWeight;
        this.overridePrevious = overridePrevious;
        this.bones = Map.copyOf(bones);
        this.soundEffects = List.copyOf(soundEffects);
        this.particleEffects = List.copyOf(particleEffects);
        this.timeline = List.copyOf(timeline);
        float derived = length;
        if (derived <= 0.0F) {
            for (BoneAnimation bone : this.bones.values()) {
                derived = Math.max(derived, bone.lastTime());
            }
            for (TimedEffect effect : this.soundEffects) {
                derived = Math.max(derived, effect.time());
            }
            for (TimedEffect effect : this.particleEffects) {
                derived = Math.max(derived, effect.time());
            }
            for (TimedScript script : this.timeline) {
                derived = Math.max(derived, script.time());
            }
        }
        this.length = derived;
    }

    public String name() {
        return this.name;
    }

    /** {@code animation_length}, or the time of the last keyframe/event when it is absent. */
    public float length() {
        return this.length;
    }

    public LoopMode loop() {
        return this.loop;
    }

    public MolangExpression startDelay() {
        return this.startDelay;
    }

    public MolangExpression loopDelay() {
        return this.loopDelay;
    }

    public Optional<MolangExpression> animTimeUpdate() {
        return this.animTimeUpdate;
    }

    public MolangExpression blendWeight() {
        return this.blendWeight;
    }

    public boolean overridePrevious() {
        return this.overridePrevious;
    }

    public Map<String, BoneAnimation> bones() {
        return this.bones;
    }

    public List<TimedEffect> soundEffects() {
        return this.soundEffects;
    }

    public List<TimedEffect> particleEffects() {
        return this.particleEffects;
    }

    public List<TimedScript> timeline() {
        return this.timeline;
    }

    // ------------------------------------------------------------------ applying

    /**
     * Poses {@code model} at {@code time} seconds with {@code weight}. Bones the geometry lacks are
     * reported once through {@code missingBones}. Bones animated relative to the entity are queued in
     * {@code entityRelative} so the caller can resolve them after every animation has posed the parents.
     */
    public void apply(BakedBedrockModel model, float time, float weight, MolangContext ctx, Set<String> missingBones, List<EntityRelativeRotation> entityRelative) {
        if (weight <= 0.0F) {
            return;
        }
        Vector3f value = new Vector3f();
        for (Map.Entry<String, BoneAnimation> entry : this.bones.entrySet()) {
            ModelPart part = model.bone(entry.getKey());
            if (part == null) {
                if (missingBones.add(this.name + "#" + entry.getKey())) {
                    Constants.LOG.warn("Animation '{}' targets bone '{}', which geometry '{}' does not have; that channel is ignored.", this.name, entry.getKey(), model.geometry().identifier());
                }
                continue;
            }
            BoneAnimation bone = entry.getValue();
            if (this.overridePrevious) {
                part.resetPose();
            }
            if (!bone.rotation().isEmpty()) {
                ctx.thisValue = 0.0;
                bone.rotation().sample(time, ctx, value);
                if (bone.rotationRelativeToEntity()) {
                    entityRelative.add(new EntityRelativeRotation(entry.getKey(), new Vector3f(value), weight));
                } else {
                    part.xRot += value.x * Mth.DEG_TO_RAD * weight;
                    part.yRot += value.y * Mth.DEG_TO_RAD * weight;
                    part.zRot += value.z * Mth.DEG_TO_RAD * weight;
                }
            }
            if (!bone.position().isEmpty()) {
                ctx.thisValue = 0.0;
                bone.position().sample(time, ctx, value);
                part.x += value.x * weight;
                part.y -= value.y * weight;
                part.z += value.z * weight;
            }
            if (!bone.scale().isEmpty()) {
                ctx.thisValue = 1.0;
                bone.scale().sample(time, ctx, value);
                part.xScale *= Mth.lerp(weight, 1.0F, value.x);
                part.yScale *= Mth.lerp(weight, 1.0F, value.y);
                part.zScale *= Mth.lerp(weight, 1.0F, value.z);
            }
        }
    }

    /**
     * This animation as it must be written today for a species pack made before Ancient Creature
     * followed Bedrock's axes: rotation Y and position X keyframes are negated, which reproduces exactly
     * how such a file used to play. See {@code BedrockGeometry#fromLegacyAxes()}.
     */
    public BedrockAnimation fromLegacyAxes() {
        Map<String, BoneAnimation> converted = new LinkedHashMap<>();
        this.bones.forEach((name, bone) -> converted.put(name, new BoneAnimation(
            convert(bone.rotation(), 1), convert(bone.position(), 0), bone.scale(), bone.rotationRelativeToEntity())));
        return new BedrockAnimation(this.name, this.length, this.loop, this.startDelay, this.loopDelay, this.animTimeUpdate, this.blendWeight,
            this.overridePrevious, converted, this.soundEffects, this.particleEffects, this.timeline);
    }

    private static BedrockAnimationChannel convert(BedrockAnimationChannel channel, int negatedAxis) {
        if (channel.isEmpty()) {
            return channel;
        }
        List<BedrockKeyframe> frames = new ArrayList<>();
        for (BedrockKeyframe frame : channel.keyframes()) {
            MolangExpression[] pre = negateAxis(frame.pre(), negatedAxis);
            MolangExpression[] post = frame.pre() == frame.post() ? pre : negateAxis(frame.post(), negatedAxis);
            frames.add(new BedrockKeyframe(frame.time(), pre, post, frame.interpolation()));
        }
        return new BedrockAnimationChannel(List.copyOf(frames));
    }

    private static MolangExpression[] negateAxis(MolangExpression[] value, int axis) {
        MolangExpression[] out = value.clone();
        out[axis] = Molang.negate(value[axis]);
        return out;
    }

    /** A bone whose rotation is given in entity space rather than relative to its parent. */
    public record EntityRelativeRotation(String bone, Vector3f degrees, float weight) {
    }

    // ------------------------------------------------------------------ parsing

    /** Parses an {@code .animation.json} file into its animations, keyed by full name. */
    public static Map<String, BedrockAnimation> parseFile(JsonElement json) throws BedrockFormatException {
        JsonObject root = BedrockJson.object(json, "$");
        JsonElement animations = BedrockJson.get(root, "animations");
        if (animations == null) {
            throw new BedrockFormatException("$.animations is required");
        }
        Map<String, BedrockAnimation> out = new LinkedHashMap<>();
        for (Map.Entry<String, JsonElement> entry : BedrockJson.object(animations, "$.animations").entrySet()) {
            out.put(entry.getKey(), parse(entry.getKey(), entry.getValue(), "$.animations." + entry.getKey()));
        }
        return out;
    }

    static BedrockAnimation parse(String name, JsonElement element, String path) throws BedrockFormatException {
        JsonObject json = BedrockJson.object(element, path);

        LoopMode loop = LoopMode.ONCE;
        JsonElement loopJson = BedrockJson.get(json, "loop");
        if (loopJson != null && loopJson.isJsonPrimitive()) {
            if (loopJson.getAsJsonPrimitive().isBoolean()) {
                loop = loopJson.getAsBoolean() ? LoopMode.LOOP : LoopMode.ONCE;
            } else if ("hold_on_last_frame".equalsIgnoreCase(loopJson.getAsString())) {
                loop = LoopMode.HOLD_ON_LAST_FRAME;
            } else if ("true".equalsIgnoreCase(loopJson.getAsString())) {
                loop = LoopMode.LOOP;
            }
        }

        Map<String, BoneAnimation> bones = new LinkedHashMap<>();
        JsonElement bonesJson = BedrockJson.get(json, "bones");
        if (bonesJson != null) {
            for (Map.Entry<String, JsonElement> entry : BedrockJson.object(bonesJson, path + ".bones").entrySet()) {
                String bonePath = path + ".bones." + entry.getKey();
                JsonObject bone = BedrockJson.object(entry.getValue(), bonePath);
                boolean relative = false;
                JsonElement relativeTo = BedrockJson.get(bone, "relative_to");
                if (relativeTo != null && relativeTo.isJsonObject()) {
                    relative = "entity".equalsIgnoreCase(BedrockJson.string(relativeTo.getAsJsonObject(), "rotation", ""));
                }
                bones.put(entry.getKey().toLowerCase(Locale.ROOT), new BoneAnimation(
                    BedrockAnimationChannel.parse(BedrockJson.get(bone, "rotation"), false, bonePath + ".rotation"),
                    BedrockAnimationChannel.parse(BedrockJson.get(bone, "position"), false, bonePath + ".position"),
                    BedrockAnimationChannel.parse(BedrockJson.get(bone, "scale"), true, bonePath + ".scale"),
                    relative));
            }
        }

        JsonElement update = BedrockJson.get(json, "anim_time_update");
        return new BedrockAnimation(name,
            BedrockJson.number(json, "animation_length", 0.0F, path),
            loop,
            molang(json, "start_delay", MolangExpression.ZERO, path),
            molang(json, "loop_delay", MolangExpression.ZERO, path),
            update == null ? Optional.empty() : Optional.of(Molang.of(BedrockJson.molangSource(update), path + ".anim_time_update")),
            molang(json, "blend_weight", MolangExpression.ONE, path),
            BedrockJson.bool(json, "override_previous_animation", false),
            bones,
            effects(BedrockJson.get(json, "sound_effects"), path + ".sound_effects"),
            effects(BedrockJson.get(json, "particle_effects"), path + ".particle_effects"),
            timeline(BedrockJson.get(json, "timeline"), path + ".timeline"));
    }

    private static MolangExpression molang(JsonObject json, String key, MolangExpression fallback, String path) {
        JsonElement element = BedrockJson.get(json, key);
        return element == null ? fallback : Molang.of(BedrockJson.molangSource(element), path + "." + key);
    }

    private static List<TimedEffect> effects(JsonElement element, String path) throws BedrockFormatException {
        List<TimedEffect> out = new ArrayList<>();
        if (element == null) {
            return out;
        }
        for (Map.Entry<String, JsonElement> entry : BedrockJson.object(element, path).entrySet()) {
            float time = parseTime(entry.getKey(), path);
            List<JsonElement> events = new ArrayList<>();
            if (entry.getValue().isJsonArray()) {
                entry.getValue().getAsJsonArray().forEach(events::add);
            } else {
                events.add(entry.getValue());
            }
            for (JsonElement event : events) {
                JsonObject effect = BedrockJson.object(event, path + "." + entry.getKey());
                String name = BedrockJson.string(effect, "effect", "");
                if (name.isEmpty()) {
                    continue;
                }
                String locator = BedrockJson.string(effect, "locator", "");
                JsonElement script = BedrockJson.get(effect, "pre_effect_script");
                out.add(new TimedEffect(time, name.toLowerCase(Locale.ROOT),
                    locator.isEmpty() ? Optional.empty() : Optional.of(locator.toLowerCase(Locale.ROOT)),
                    script == null ? Optional.empty() : Optional.of(Molang.of(BedrockJson.molangSource(script), path + "." + entry.getKey() + ".pre_effect_script"))));
            }
        }
        out.sort(Comparator.comparingDouble(TimedEffect::time));
        return out;
    }

    private static List<TimedScript> timeline(JsonElement element, String path) throws BedrockFormatException {
        List<TimedScript> out = new ArrayList<>();
        if (element == null) {
            return out;
        }
        for (Map.Entry<String, JsonElement> entry : BedrockJson.object(element, path).entrySet()) {
            List<MolangExpression> scripts = new ArrayList<>();
            for (String source : BedrockJson.strings(entry.getValue())) {
                scripts.add(Molang.compileOr(source, MolangExpression.ZERO, path + "." + entry.getKey()));
            }
            out.add(new TimedScript(parseTime(entry.getKey(), path), List.copyOf(scripts)));
        }
        out.sort(Comparator.comparingDouble(TimedScript::time));
        return out;
    }

    private static float parseTime(String key, String path) throws BedrockFormatException {
        try {
            return Float.parseFloat(key.trim());
        } catch (NumberFormatException e) {
            throw new BedrockFormatException(path + " has a non-numeric time '" + key + "'");
        }
    }
}
