package com.coolerpromc.ancientcreature.client.entity.animation.block;

import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;

public final class IncubatorAnimation {
    public static final AnimationDefinition PROCESSING = AnimationDefinition.Builder.withLength(4.0F).looping()
        .addAnimation("fan_rotor", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            rotation(0, 0), rotation(1, -90), rotation(2, -180), rotation(3, -270), rotation(4, -360)))
        .addAnimation("egg_tray", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            tilt(0, -0.8F), tilt(1, 0.8F), tilt(2, -0.8F), tilt(3, 0.8F), tilt(4, -0.8F)))
        .addAnimation("eggs", new AnimationChannel(AnimationChannel.Targets.POSITION,
            position(0, 0), position(1, 0.12F), position(2, 0), position(3, 0.12F), position(4, 0)))
        .addAnimation("heater_coils", new AnimationChannel(AnimationChannel.Targets.SCALE,
            scale(0, 1.0F, 0.92F), scale(0.5F, 1.03F, 1.08F), scale(1, 1.0F, 0.92F),
            scale(1.5F, 1.03F, 1.08F), scale(2, 1.0F, 0.92F), scale(2.5F, 1.03F, 1.08F),
            scale(3, 1.0F, 0.92F), scale(3.5F, 1.03F, 1.08F), scale(4, 1.0F, 0.92F)))
        .build();

    private static Keyframe rotation(float time, float z) {
        return new Keyframe(time, KeyframeAnimations.degreeVec(0, 0, z), AnimationChannel.Interpolations.LINEAR);
    }

    private static Keyframe tilt(float time, float z) {
        return new Keyframe(time, KeyframeAnimations.degreeVec(0, 0, z), AnimationChannel.Interpolations.CATMULLROM);
    }

    private static Keyframe position(float time, float y) {
        return new Keyframe(time, KeyframeAnimations.posVec(0, y, 0), AnimationChannel.Interpolations.CATMULLROM);
    }

    private static Keyframe scale(float time, float xz, float y) {
        return new Keyframe(time, KeyframeAnimations.scaleVec(xz, y, xz), AnimationChannel.Interpolations.LINEAR);
    }

    private static Keyframe uniformScale(float time, float value) {
        return new Keyframe(time, KeyframeAnimations.scaleVec(value, value, value), AnimationChannel.Interpolations.LINEAR);
    }

    private IncubatorAnimation() {}
}
