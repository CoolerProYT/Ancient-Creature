package com.coolerpromc.ancientcreature.client.block.renderer;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.block.custom.IncubatorBlock;
import com.coolerpromc.ancientcreature.block.entity.custom.IncubatorBlockEntity;
import com.coolerpromc.ancientcreature.client.block.state.IncubatorBlockEntityRenderState;
import com.coolerpromc.ancientcreature.client.entity.model.ModModelLayers;
import com.coolerpromc.ancientcreature.client.entity.model.block.IncubatorModel;
import com.coolerpromc.ancientcreature.client.entity.state.block.IncubatorRenderState;
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

public class IncubatorBlockEntityRenderer implements BlockEntityRenderer<IncubatorBlockEntity, IncubatorBlockEntityRenderState> {
    private final IncubatorModel model;

    public IncubatorBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        model = new IncubatorModel(context.bakeLayer(ModModelLayers.INCUBATOR));
    }

    @Override
    public IncubatorBlockEntityRenderState createRenderState() {
        IncubatorBlockEntityRenderState state = new IncubatorBlockEntityRenderState();
        state.entityRenderState = new IncubatorRenderState();
        return state;
    }

    @Override
    public void extractRenderState(IncubatorBlockEntity blockEntity, IncubatorBlockEntityRenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        state.isProcessing = blockEntity.isProcessing();
        state.facing = blockEntity.getBlockState().getValue(IncubatorBlock.FACING);
        int animationTick = blockEntity.getLevel() == null ? 0 : (int) blockEntity.getLevel().getGameTime();
        blockEntity.getProcessingAnimationState().animateWhen(state.isProcessing, animationTick);
        state.entityRenderState.ageInTicks = animationTick + partialTicks;
        state.entityRenderState.processingState.copyFrom(blockEntity.getProcessingAnimationState());
    }

    @Override
    public void submit(IncubatorBlockEntityRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        poseStack.pushPose();
        poseStack.translate(0.5, 1.5, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - state.facing.toYRot()));
        poseStack.scale(-1, -1, 1);
        collector.submitModel(model, state.entityRenderState, poseStack, RenderTypes.entityCutout(Constants.id("textures/entity/block/incubator.png")), state.lightCoords, OverlayTexture.NO_OVERLAY, 0, state.breakProgress);
        poseStack.popPose();
    }
}
