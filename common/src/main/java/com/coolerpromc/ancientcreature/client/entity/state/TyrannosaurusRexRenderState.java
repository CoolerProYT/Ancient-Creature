package com.coolerpromc.ancientcreature.client.entity.state;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.AnimationState;

public class TyrannosaurusRexRenderState extends LivingEntityRenderState {
    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState roarAnimationState = new AnimationState();
    public final AnimationState biteAnimationState = new AnimationState();
    public boolean isRunning;
}
