package com.coolerpromc.ancientcreature.client.entity.animation;

import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;

public class FossilCleaningTableAnimation {
    public static final AnimationDefinition BRUSH_FOSSIL = AnimationDefinition.Builder.withLength(5.0F).looping()
        .addAnimation("brush", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0F, KeyframeAnimations.degreeVec(6.9097F, -21.9614F, 88.9194F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(5.0F, KeyframeAnimations.degreeVec(6.9097F, -21.9614F, 88.9194F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("brush", new AnimationChannel(AnimationChannel.Targets.POSITION,
            new Keyframe(0.0F, KeyframeAnimations.posVec(9.0F, 1.2F, 2.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(2.5F, KeyframeAnimations.posVec(3.0F, 1.2F, 4.4F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(5.0F, KeyframeAnimations.posVec(9.0F, 1.2F, 2.0F), AnimationChannel.Interpolations.LINEAR)
        ))
        .build();
}
