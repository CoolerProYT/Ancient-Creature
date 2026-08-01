package com.coolerpromc.ancientcreature.client.block.renderer;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.block.custom.FossilIdentificationChamberBlock;
import com.coolerpromc.ancientcreature.block.entity.custom.DNAExtractorBlockEntity;
import com.coolerpromc.ancientcreature.client.block.state.DNAExtractorBlockEntityRenderState;
import com.coolerpromc.ancientcreature.client.entity.model.ModModelLayers;
import com.coolerpromc.ancientcreature.client.entity.model.block.DNAExtractorModel;
import com.coolerpromc.ancientcreature.client.entity.state.block.DNAExtractorRenderState;
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

public class DNAExtractorBlockEntityRenderer implements BlockEntityRenderer<DNAExtractorBlockEntity, DNAExtractorBlockEntityRenderState> {
    private final DNAExtractorModel model;

    public DNAExtractorBlockEntityRenderer(BlockEntityRendererProvider.Context context){
        this.model = new DNAExtractorModel(context.bakeLayer(ModModelLayers.DNA_EXTRACTOR));
    }

    @Override
    public DNAExtractorBlockEntityRenderState createRenderState() {
        DNAExtractorBlockEntityRenderState state = new DNAExtractorBlockEntityRenderState();
        state.entityRenderState = new DNAExtractorRenderState();
        return state;
    }

    @Override
    public void extractRenderState(DNAExtractorBlockEntity blockEntity, DNAExtractorBlockEntityRenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        state.isExtracting = blockEntity.isExtracting();
        state.facing = blockEntity.getBlockState().getValue(FossilIdentificationChamberBlock.FACING);

        int animationTick = blockEntity.getLevel() == null ? 0 : (int)blockEntity.getLevel().getGameTime();
        blockEntity.getExtractingAnimationState().animateWhen(state.isExtracting, animationTick);
        state.entityRenderState.ageInTicks = animationTick + partialTicks;
        state.entityRenderState.extractingState.copyFrom(blockEntity.getExtractingAnimationState());
    }

    @Override
    public void submit(DNAExtractorBlockEntityRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        poseStack.pushPose();
        poseStack.translate(0.5, 1.5, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - state.facing.toYRot()));
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        submitNodeCollector.submitModel(model, state.entityRenderState, poseStack, RenderTypes.entityCutout(Constants.id("textures/entity/block/dna_extractor.png")), state.lightCoords, OverlayTexture.NO_OVERLAY, 0, state.breakProgress);
        poseStack.popPose();
    }
}
