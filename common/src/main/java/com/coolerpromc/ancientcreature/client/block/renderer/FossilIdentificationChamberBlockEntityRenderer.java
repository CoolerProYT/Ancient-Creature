package com.coolerpromc.ancientcreature.client.block.renderer;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.block.custom.FossilIdentificationChamberBlock;
import com.coolerpromc.ancientcreature.block.entity.custom.FossilIdentificationChamberBlockEntity;
import com.coolerpromc.ancientcreature.client.block.state.FossilIdentificationChamberBlockEntityRenderState;
import com.coolerpromc.ancientcreature.client.entity.model.FossilIdentificationChamberModel;
import com.coolerpromc.ancientcreature.client.entity.model.ModModelLayers;
import com.coolerpromc.ancientcreature.client.entity.state.FossilIdentificationChamberRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class FossilIdentificationChamberBlockEntityRenderer implements BlockEntityRenderer<FossilIdentificationChamberBlockEntity, FossilIdentificationChamberBlockEntityRenderState> {
    private final FossilIdentificationChamberModel model;

    public FossilIdentificationChamberBlockEntityRenderer(BlockEntityRendererProvider.Context context){
        this.model = new FossilIdentificationChamberModel(context.bakeLayer(ModModelLayers.FOSSIL_IDENTIFYING_CHAMBER));
    }

    @Override
    public FossilIdentificationChamberBlockEntityRenderState createRenderState() {
        FossilIdentificationChamberBlockEntityRenderState state = new FossilIdentificationChamberBlockEntityRenderState();
        state.entityRenderState = new FossilIdentificationChamberRenderState();
        return state;
    }

    @Override
    public void extractRenderState(FossilIdentificationChamberBlockEntity blockEntity, FossilIdentificationChamberBlockEntityRenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        state.isIdentifying = blockEntity.isIdentifying();
        state.facing = blockEntity.getBlockState().getValue(FossilIdentificationChamberBlock.FACING);

        int animationTick = blockEntity.getLevel() == null ? 0 : (int)blockEntity.getLevel().getGameTime();
        blockEntity.getIdentifyingAnimationState().animateWhen(state.isIdentifying, animationTick);
        state.entityRenderState.ageInTicks = animationTick + partialTicks;
        state.entityRenderState.identifyingState.copyFrom(blockEntity.getIdentifyingAnimationState());
    }

    @Override
    public void submit(FossilIdentificationChamberBlockEntityRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        poseStack.pushPose();
        poseStack.translate(0.5, 1.5, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - state.facing.toYRot()));
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        submitNodeCollector.submitModel(model, state.entityRenderState, poseStack, RenderTypes.entityCutout(Constants.id("textures/entity/block/fossil_identification_chamber.png")), state.lightCoords, OverlayTexture.NO_OVERLAY, 0, state.breakProgress);
        poseStack.popPose();
    }
}
