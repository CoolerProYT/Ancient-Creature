package com.coolerpromc.ancientcreature.client.entity.renderer;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.client.entity.model.ModModelLayers;
import com.coolerpromc.ancientcreature.client.entity.model.TyrannosaurusRexModel;
import com.coolerpromc.ancientcreature.client.entity.state.TyrannosaurusRexRenderState;
import com.coolerpromc.ancientcreature.entity.custom.TyrannosaurusRex;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;

public class TyrannosaurusRexRenderer extends MobRenderer<TyrannosaurusRex, TyrannosaurusRexRenderState, TyrannosaurusRexModel> {
    private static final Identifier TEXTURE = Constants.id("textures/entity/tyrannosaurus_rex.png");

    public TyrannosaurusRexRenderer(EntityRendererProvider.Context context) {
        super(context, new TyrannosaurusRexModel(context.bakeLayer(ModModelLayers.TYRANNOSAURUS_REX)), 1.1F);
    }

    @Override
    public Identifier getTextureLocation(TyrannosaurusRexRenderState state) {
        return TEXTURE;
    }

    @Override
    public TyrannosaurusRexRenderState createRenderState() {
        return new TyrannosaurusRexRenderState();
    }

    @Override
    public void extractRenderState(TyrannosaurusRex entity, TyrannosaurusRexRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.idleAnimationState.copyFrom(entity.idleAnimationState);
        state.roarAnimationState.copyFrom(entity.roarAnimationState);
        state.biteAnimationState.copyFrom(entity.biteAnimationState);
        state.isRunning = entity.isSprinting();
    }

    @Override
    protected void scale(TyrannosaurusRexRenderState state, PoseStack poseStack) {
        poseStack.scale(state.ageScale, state.ageScale, state.ageScale);
    }
}
