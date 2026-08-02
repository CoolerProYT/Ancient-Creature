package com.coolerpromc.ancientcreature.client.entity.renderer;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.client.entity.model.MegalodonModel;
import com.coolerpromc.ancientcreature.client.entity.model.ModModelLayers;
import com.coolerpromc.ancientcreature.client.entity.state.MegalodonRenderState;
import com.coolerpromc.ancientcreature.entity.custom.Megalodon;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;

public class MegalodonRenderer extends MobRenderer<Megalodon, MegalodonRenderState, MegalodonModel> {
    private static final Identifier TEXTURE = Constants.id("textures/entity/megalodon.png");

    public MegalodonRenderer(EntityRendererProvider.Context context) {
        super(context, new MegalodonModel(context.bakeLayer(ModModelLayers.MEGALODON)), 1.2F);
    }

    @Override
    public Identifier getTextureLocation(MegalodonRenderState state) {
        return TEXTURE;
    }

    @Override
    public MegalodonRenderState createRenderState() {
        return new MegalodonRenderState();
    }

    @Override
    public void extractRenderState(Megalodon entity, MegalodonRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.idleAnimationState.copyFrom(entity.idleAnimationState);
        state.swimAnimationState.copyFrom(entity.swimAnimationState);
        state.biteAnimationState.copyFrom(entity.biteAnimationState);
    }

    @Override
    protected void scale(MegalodonRenderState state, PoseStack poseStack) {
        poseStack.scale(state.ageScale, state.ageScale, state.ageScale);
    }
}
