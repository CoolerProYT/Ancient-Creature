package com.coolerpromc.ancientcreature.client.entity.renderer;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.client.entity.model.ModModelLayers;
import com.coolerpromc.ancientcreature.client.entity.model.TriceratopsModel;
import com.coolerpromc.ancientcreature.client.entity.state.TriceratopsRenderState;
import com.coolerpromc.ancientcreature.entity.custom.Triceratops;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;

public class TriceratopsRenderer extends MobRenderer<Triceratops, TriceratopsRenderState, TriceratopsModel> {
    private static final Identifier TEXTURE = Constants.id("textures/entity/triceratops.png");

    public TriceratopsRenderer(EntityRendererProvider.Context context) {
        super(context, new TriceratopsModel(context.bakeLayer(ModModelLayers.TRICERATOPS)), 0.9F);
    }

    @Override
    public Identifier getTextureLocation(TriceratopsRenderState state) {
        return TEXTURE;
    }

    @Override
    public TriceratopsRenderState createRenderState() {
        return new TriceratopsRenderState();
    }

    @Override
    public void extractRenderState(Triceratops entity, TriceratopsRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.idleAnimationState.copyFrom(entity.idleAnimationState);
        state.chargeAnimationState.copyFrom(entity.chargeAnimationState);
        state.grazeAnimationState.copyFrom(entity.grazeAnimationState);
        state.bellowAnimationState.copyFrom(entity.bellowAnimationState);
    }

    @Override
    protected void scale(TriceratopsRenderState state, PoseStack poseStack) {
        poseStack.scale(state.ageScale, state.ageScale, state.ageScale);
    }
}
