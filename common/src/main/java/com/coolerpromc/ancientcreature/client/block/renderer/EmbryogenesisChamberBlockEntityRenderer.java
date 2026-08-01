package com.coolerpromc.ancientcreature.client.block.renderer;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.block.custom.EmbryogenesisChamberBlock;
import com.coolerpromc.ancientcreature.block.entity.custom.EmbryogenesisChamberBlockEntity;
import com.coolerpromc.ancientcreature.client.block.state.EmbryogenesisChamberBlockEntityRenderState;
import com.coolerpromc.ancientcreature.client.entity.model.ModModelLayers;
import com.coolerpromc.ancientcreature.client.entity.model.block.EmbryogenesisChamberModel;
import com.coolerpromc.ancientcreature.client.entity.state.block.EmbryogenesisChamberRenderState;
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

public class EmbryogenesisChamberBlockEntityRenderer implements BlockEntityRenderer<EmbryogenesisChamberBlockEntity, EmbryogenesisChamberBlockEntityRenderState> {
    private final EmbryogenesisChamberModel model;

    public EmbryogenesisChamberBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        model = new EmbryogenesisChamberModel(context.bakeLayer(ModModelLayers.EMBRYOGENESIS_CHAMBER));
    }

    @Override
    public EmbryogenesisChamberBlockEntityRenderState createRenderState() {
        EmbryogenesisChamberBlockEntityRenderState state = new EmbryogenesisChamberBlockEntityRenderState();
        state.entityRenderState = new EmbryogenesisChamberRenderState();
        return state;
    }

    @Override
    public void extractRenderState(EmbryogenesisChamberBlockEntity blockEntity, EmbryogenesisChamberBlockEntityRenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        state.isProcessing = blockEntity.isProcessing();
        state.facing = blockEntity.getBlockState().getValue(EmbryogenesisChamberBlock.FACING);
        int animationTick = blockEntity.getLevel() == null ? 0 : (int) blockEntity.getLevel().getGameTime();
        blockEntity.getProcessingAnimationState().animateWhen(state.isProcessing, animationTick);
        state.entityRenderState.ageInTicks = animationTick + partialTicks;
        state.entityRenderState.processingState.copyFrom(blockEntity.getProcessingAnimationState());
    }

    @Override
    public void submit(EmbryogenesisChamberBlockEntityRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        poseStack.pushPose();
        poseStack.translate(0.5, 1.5, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - state.facing.toYRot()));
        poseStack.scale(-1, -1, 1);
        collector.submitModel(model, state.entityRenderState, poseStack, RenderTypes.entityCutout(Constants.id("textures/entity/block/embryogenesis_chamber.png")), state.lightCoords, OverlayTexture.NO_OVERLAY, 0, state.breakProgress);
        poseStack.popPose();
    }
}
