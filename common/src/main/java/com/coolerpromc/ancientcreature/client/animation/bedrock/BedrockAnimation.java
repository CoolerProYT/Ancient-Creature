package com.coolerpromc.ancientcreature.client.animation.bedrock;

import com.coolerpromc.ancientcreature.Constants;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import org.joml.Vector3f;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public record BedrockAnimation(String name, float length, boolean loop, boolean holdOnLastFrame, Map<String, BoneAnimation> bones) {
    public static final Codec<BedrockAnimation> CODEC = RecordCodecBuilder.create(i -> i.group(
        Codec.FLOAT.optionalFieldOf("animation_length", 0.0F).forGetter(BedrockAnimation::length),
        LoopMode.CODEC.optionalFieldOf("loop", LoopMode.ONCE).forGetter(BedrockAnimation::loopMode),
        Codec.unboundedMap(Codec.STRING, BoneAnimation.CODEC).optionalFieldOf("bones", Map.of()).forGetter(BedrockAnimation::bones)
    ).apply(i, (length, loop, bones) -> new BedrockAnimation("", length, loop == LoopMode.LOOP, loop == LoopMode.HOLD_ON_LAST_FRAME, bones)));

    public static final Codec<Map<String, BedrockAnimation>> FILE_CODEC = RecordCodecBuilder.create(i -> i.group(
        Codec.STRING.optionalFieldOf("format_version", "1.8.0").forGetter(map -> "1.8.0"),
        Codec.unboundedMap(Codec.STRING, CODEC).fieldOf("animations").forGetter(map -> map)
    ).apply(i, (formatVersion, animations) -> animations));

    public enum LoopMode {
        ONCE,
        LOOP,
        HOLD_ON_LAST_FRAME;

        public static final Codec<LoopMode> CODEC = Codec.either(Codec.BOOL, Codec.STRING).xmap(
            either -> either.map(
                flag -> flag ? LOOP : ONCE,
                text -> "hold_on_last_frame".equalsIgnoreCase(text) ? HOLD_ON_LAST_FRAME : ONCE),
            mode -> mode == HOLD_ON_LAST_FRAME ? Either.right("hold_on_last_frame") : Either.left(mode == LOOP)
        );
    }

    private LoopMode loopMode() {
        if (this.loop) {
            return LoopMode.LOOP;
        }
        return this.holdOnLastFrame ? LoopMode.HOLD_ON_LAST_FRAME : LoopMode.ONCE;
    }

    public BedrockAnimation withName(String name) {
        return new BedrockAnimation(name, this.length, this.loop, this.holdOnLastFrame, this.bones);
    }

    public float effectiveLength() {
        if (this.length > 0.0F) {
            return this.length;
        }
        float max = 0.0F;
        for (BoneAnimation bone : this.bones.values()) {
            max = Math.max(max, bone.lastKeyframeTime());
        }
        return Math.max(max, 0.05F);
    }

    public void apply(ModelPart root, float seconds, float weight, Set<String> missingBoneWarnings) {
        if (weight <= 0.0F) {
            return;
        }

        float length = this.effectiveLength();
        float time;
        if (this.loop) {
            time = length <= 0.0F ? 0.0F : seconds % length;
        } else if (this.holdOnLastFrame) {
            time = Math.min(seconds, length);
        } else {
            if (seconds > length) {
                return;
            }
            time = seconds;
        }

        Vector3f scratch = new Vector3f();

        for (Map.Entry<String, BoneAnimation> entry : this.bones.entrySet()) {
            ModelPart part = findPart(root, entry.getKey());
            if (part == null) {
                if (missingBoneWarnings.add(this.name + "#" + entry.getKey())) {
                    Constants.LOG.warn("Animation '{}' targets bone '{}', which the geometry does not have; that channel is ignored.", this.name, entry.getKey());
                }
                continue;
            }
            entry.getValue().apply(part, time, weight, scratch);
        }
    }

    private static ModelPart findPart(ModelPart root, String name) {
        if (root.hasChild(name)) {
            return root.getChild(name);
        }
        for (ModelPart child : root.getAllParts()) {
            if (child != root && child.hasChild(name)) {
                return child.getChild(name);
            }
        }
        return null;
    }

    public record BoneAnimation(BedrockAnimationChannel rotation, BedrockAnimationChannel position, BedrockAnimationChannel scale) {
        public static final Codec<BoneAnimation> CODEC = RecordCodecBuilder.create(i -> i.group(
            BedrockAnimationChannel.CODEC.optionalFieldOf("rotation", BedrockAnimationChannel.EMPTY).forGetter(BoneAnimation::rotation),
            BedrockAnimationChannel.CODEC.optionalFieldOf("position", BedrockAnimationChannel.EMPTY).forGetter(BoneAnimation::position),
            BedrockAnimationChannel.CODEC.optionalFieldOf("scale", BedrockAnimationChannel.EMPTY).forGetter(BoneAnimation::scale)
        ).apply(i, BoneAnimation::new));

        void apply(ModelPart part, float time, float weight, Vector3f scratch) {
            if (!this.rotation.isEmpty()) {
                this.rotation.sample(time, scratch);
                // X is NOT negated here, unlike the bone rest rotations in BedrockModelBaker.
                //
                // Blockbench writes animation keyframes with the opposite X handedness to geometry bone
                // rotations: authoring "head up" produces a negative keyframe, and Minecraft's own
                // convention is that a negative part xRot looks up (vanilla sets head.xRot = entity
                // pitch, where positive is downward). Negating here inverted every clip — heads pitched
                // down instead of up, and jaws, which must swing positive to open, clamped shut.
                part.xRot += scratch.x * Mth.DEG_TO_RAD * weight;
                part.yRot += -scratch.y * Mth.DEG_TO_RAD * weight;
                part.zRot += scratch.z * Mth.DEG_TO_RAD * weight;
            }
            if (!this.position.isEmpty()) {
                this.position.sample(time, scratch);
                part.x += -scratch.x * weight;
                part.y += -scratch.y * weight;
                part.z += scratch.z * weight;
            }
            if (!this.scale.isEmpty()) {
                this.scale.sample(time, scratch);
                // Bedrock scale is multiplicative around 1; blend towards it by the weight.
                part.xScale *= Mth.lerp(weight, 1.0F, scratch.x);
                part.yScale *= Mth.lerp(weight, 1.0F, scratch.y);
                part.zScale *= Mth.lerp(weight, 1.0F, scratch.z);
            }
        }

        float lastKeyframeTime() {
            float max = 0.0F;
            for (BedrockAnimationChannel channel : new BedrockAnimationChannel[]{this.rotation, this.position, this.scale}) {
                if (!channel.isEmpty()) {
                    max = Math.max(max, channel.keyframes().getLast().time());
                }
            }
            return max;
        }
    }

    public static Set<String> newWarningSet() {
        return new HashSet<>();
    }
}
