package com.coolerpromc.ancientcreature.client.entity.animation.block;

import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;

public final class EmbryogenesisChamberAnimation {
    public static final AnimationDefinition PROCESSING = AnimationDefinition.Builder.withLength(3.2F).looping()
        .addAnimation("fluid_ring", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            frame(0.0F, 0, 0, 0, true), frame(0.8F, 0, -90, 0, true),
            frame(1.6F, 0, -180, 0, true), frame(2.4F, 0, -270, 0, true), frame(3.2F, 0, -360, 0, true)))
        .addAnimation("cradle", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            frame(0.0F, 0, -3, 0, false), frame(0.8F, 0, 3, 0, false),
            frame(1.6F, 0, -3, 0, false), frame(2.4F, 0, 3, 0, false), frame(3.2F, 0, -3, 0, false)))
        .addAnimation("embryo_core", new AnimationChannel(AnimationChannel.Targets.POSITION,
            pos(0.0F, 0), pos(0.8F, 0.2F), pos(1.6F, 0), pos(2.4F, 0.2F), pos(3.2F, 0)))
        .addAnimation("embryo_core", new AnimationChannel(AnimationChannel.Targets.SCALE,
            scale(0.0F, 1, 1), scale(0.8F, 1.025F, 1.04F), scale(1.6F, 1, 1),
            scale(2.4F, 1.025F, 1.04F), scale(3.2F, 1, 1)))
        .addAnimation("injector_left", new AnimationChannel(AnimationChannel.Targets.POSITION,
            xPos(0.0F, 0), xPos(0.6F, 0), xPos(1.0F, 0.9F), xPos(1.4F, 0.9F), xPos(1.8F, 0), xPos(3.2F, 0)))
        .addAnimation("injector_right", new AnimationChannel(AnimationChannel.Targets.POSITION,
            xPos(0.0F, 0), xPos(1.4F, 0), xPos(1.8F, -0.9F), xPos(2.2F, -0.9F), xPos(2.6F, 0), xPos(3.2F, 0)))
        .addAnimation("status_lights", new AnimationChannel(AnimationChannel.Targets.SCALE,
            uniformScale(0.0F, 0.85F), uniformScale(0.4F, 1.12F), uniformScale(0.8F, 0.85F),
            uniformScale(1.2F, 1.12F), uniformScale(1.6F, 0.85F), uniformScale(2.0F, 1.12F),
            uniformScale(2.4F, 0.85F), uniformScale(2.8F, 1.12F), uniformScale(3.2F, 0.85F)))
        .build();

    private static Keyframe frame(float time, float x, float y, float z, boolean linear) {
        return new Keyframe(time, KeyframeAnimations.degreeVec(x, y, z), linear ? AnimationChannel.Interpolations.LINEAR : AnimationChannel.Interpolations.CATMULLROM);
    }

    private static Keyframe pos(float time, float y) {
        return new Keyframe(time, KeyframeAnimations.posVec(0, y, 0), AnimationChannel.Interpolations.CATMULLROM);
    }

    private static Keyframe xPos(float time, float x) {
        return new Keyframe(time, KeyframeAnimations.posVec(x, 0, 0), AnimationChannel.Interpolations.LINEAR);
    }

    private static Keyframe scale(float time, float xz, float y) {
        return new Keyframe(time, KeyframeAnimations.scaleVec(xz, y, xz), AnimationChannel.Interpolations.CATMULLROM);
    }

    private static Keyframe uniformScale(float time, float scale) {
        return new Keyframe(time, KeyframeAnimations.scaleVec(scale, scale, scale), AnimationChannel.Interpolations.LINEAR);
    }

    private EmbryogenesisChamberAnimation() {}
}
