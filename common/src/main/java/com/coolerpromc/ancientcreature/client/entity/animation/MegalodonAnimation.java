package com.coolerpromc.ancientcreature.client.entity.animation;

import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;

public final class MegalodonAnimation {
    private MegalodonAnimation() {
    }

    public static final AnimationDefinition SWIM = AnimationDefinition.Builder.withLength(1.2F)
            .looping()
            .addAnimation("body", rotation(0.0F, 0.0F, -2.0F, 0.0F, 0.3F, 0.0F, 2.0F, 0.0F,
                    0.6F, 0.0F, -2.0F, 0.0F, 0.9F, 0.0F, 2.0F, 0.0F, 1.2F, 0.0F, -2.0F, 0.0F))
            .addAnimation("head", rotation(0.0F, 0.0F, 1.0F, 0.0F, 0.3F, 0.0F, -1.0F, 0.0F,
                    0.6F, 0.0F, 1.0F, 0.0F, 0.9F, 0.0F, -1.0F, 0.0F, 1.2F, 0.0F, 1.0F, 0.0F))
            .addAnimation("tail_base", rotation(0.0F, 0.0F, 9.0F, 0.0F, 0.3F, 0.0F, -9.0F, 0.0F,
                    0.6F, 0.0F, 9.0F, 0.0F, 0.9F, 0.0F, -9.0F, 0.0F, 1.2F, 0.0F, 9.0F, 0.0F))
            .addAnimation("tail_mid", rotation(0.0F, 0.0F, 17.0F, 0.0F, 0.3F, 0.0F, -17.0F, 0.0F,
                    0.6F, 0.0F, 17.0F, 0.0F, 0.9F, 0.0F, -17.0F, 0.0F, 1.2F, 0.0F, 17.0F, 0.0F))
            .addAnimation("tail_tip", rotation(0.0F, 0.0F, 25.0F, 0.0F, 0.3F, 0.0F, -25.0F, 0.0F,
                    0.6F, 0.0F, 25.0F, 0.0F, 0.9F, 0.0F, -25.0F, 0.0F, 1.2F, 0.0F, 25.0F, 0.0F))
            .addAnimation("pectoral_left", rotation(0.0F, 0.0F, 0.0F, -2.0F, 0.6F, 0.0F, 0.0F, 2.0F,
                    1.2F, 0.0F, 0.0F, -2.0F))
            .addAnimation("pectoral_right", rotation(0.0F, 0.0F, 0.0F, 2.0F, 0.6F, 0.0F, 0.0F, -2.0F,
                    1.2F, 0.0F, 0.0F, 2.0F))
            .build();

    public static final AnimationDefinition IDLE = AnimationDefinition.Builder.withLength(3.0F)
            .looping()
            .addAnimation("body", rotation(0.0F, 0.0F, -1.0F, 0.0F, 0.75F, 0.0F, 1.0F, 0.0F,
                    1.5F, 0.0F, -1.0F, 0.0F, 2.25F, 0.0F, 1.0F, 0.0F, 3.0F, 0.0F, -1.0F, 0.0F))
            .addAnimation("tail_base", rotation(0.0F, 0.0F, 4.0F, 0.0F, 0.75F, 0.0F, -4.0F, 0.0F,
                    1.5F, 0.0F, 4.0F, 0.0F, 2.25F, 0.0F, -4.0F, 0.0F, 3.0F, 0.0F, 4.0F, 0.0F))
            .addAnimation("tail_mid", rotation(0.0F, 0.0F, 7.0F, 0.0F, 0.75F, 0.0F, -7.0F, 0.0F,
                    1.5F, 0.0F, 7.0F, 0.0F, 2.25F, 0.0F, -7.0F, 0.0F, 3.0F, 0.0F, 7.0F, 0.0F))
            .addAnimation("tail_tip", rotation(0.0F, 0.0F, 10.0F, 0.0F, 0.75F, 0.0F, -10.0F, 0.0F,
                    1.5F, 0.0F, 10.0F, 0.0F, 2.25F, 0.0F, -10.0F, 0.0F, 3.0F, 0.0F, 10.0F, 0.0F))
            .build();

    public static final AnimationDefinition BITE = AnimationDefinition.Builder.withLength(0.8F)
            .addAnimation("head", rotation(0.0F, 0.0F, 0.0F, 0.0F, 0.18F, -4.0F, 0.0F, 0.0F,
                    0.42F, 3.0F, 0.0F, 0.0F, 0.8F, 0.0F, 0.0F, 0.0F))
            .addAnimation("jaw", rotation(0.0F, 0.0F, 0.0F, 0.0F, 0.18F, 28.0F, 0.0F, 0.0F,
                    0.42F, -4.0F, 0.0F, 0.0F, 0.58F, 2.0F, 0.0F, 0.0F, 0.8F, 0.0F, 0.0F, 0.0F))
            .addAnimation("body", new AnimationChannel(AnimationChannel.Targets.POSITION,
                    new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.18F, KeyframeAnimations.posVec(0.0F, 0.0F, -1.5F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.42F, KeyframeAnimations.posVec(0.0F, 0.0F, 1.2F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.8F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)))
            .build();

    private static AnimationChannel rotation(float... values) {
        Keyframe[] keyframes = new Keyframe[values.length / 4];
        for (int i = 0; i < keyframes.length; ++i) {
            int offset = i * 4;
            keyframes[i] = new Keyframe(values[offset],
                    KeyframeAnimations.degreeVec(values[offset + 1], values[offset + 2], values[offset + 3]),
                    AnimationChannel.Interpolations.LINEAR);
        }
        return new AnimationChannel(AnimationChannel.Targets.ROTATION, keyframes);
    }
}
