package com.coolerpromc.ancientcreature.client.block.renderer;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.block.custom.FossilCleaningTableBlock;
import com.coolerpromc.ancientcreature.block.entity.custom.FossilCleaningTableBlockEntity;
import com.coolerpromc.ancientcreature.client.block.state.FossilCleaningBlockEntityRenderState;
import com.coolerpromc.ancientcreature.client.entity.model.FossilCleaningTableModel;
import com.coolerpromc.ancientcreature.client.entity.model.ModModelLayers;
import com.coolerpromc.ancientcreature.client.entity.state.FossilCleaningTableRenderState;
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

public class FossilCleaningTableBlockEntityRenderer implements BlockEntityRenderer<FossilCleaningTableBlockEntity, FossilCleaningBlockEntityRenderState> {
    private final FossilCleaningTableModel model;

    public FossilCleaningTableBlockEntityRenderer(BlockEntityRendererProvider.Context context){
        this.model = new FossilCleaningTableModel(context.bakeLayer(ModModelLayers.FOSSIL_CLEANING_TABLE));
    }

    @Override
    public FossilCleaningBlockEntityRenderState createRenderState() {
        FossilCleaningBlockEntityRenderState state = new FossilCleaningBlockEntityRenderState();
        state.entityRenderState = new FossilCleaningTableRenderState();
        return state;
    }

    @Override
    public void extractRenderState(FossilCleaningTableBlockEntity blockEntity, FossilCleaningBlockEntityRenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        state.isBrushing = blockEntity.isCleaning();
        state.facing = blockEntity.getBlockState().getValue(FossilCleaningTableBlock.FACING);

        int animationTick = blockEntity.getLevel() == null ? 0 : (int)blockEntity.getLevel().getGameTime();
        blockEntity.getCleaningAnimationState().animateWhen(state.isBrushing, animationTick);
        state.entityRenderState.ageInTicks = animationTick + partialTicks;
        state.entityRenderState.cleaningState.copyFrom(blockEntity.getCleaningAnimationState());
    }

    @Override
    public void submit(FossilCleaningBlockEntityRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        poseStack.pushPose();
        poseStack.translate(0.5, 1.5, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - state.facing.toYRot()));
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        submitNodeCollector.submitModel(model, state.entityRenderState, poseStack, RenderTypes.entityCutout(Constants.id("textures/entity/block/fossil_cleaning_table.png")), state.lightCoords, OverlayTexture.NO_OVERLAY, 0, state.breakProgress);
        poseStack.popPose();
    }
}
