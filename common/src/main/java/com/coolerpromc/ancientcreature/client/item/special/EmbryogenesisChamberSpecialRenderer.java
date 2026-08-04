package com.coolerpromc.ancientcreature.client.item.special;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.client.entity.model.ModModelLayers;
import com.coolerpromc.ancientcreature.client.entity.model.block.EmbryogenesisChamberModel;
import com.coolerpromc.ancientcreature.client.entity.state.block.EmbryogenesisChamberRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.special.NoDataSpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.core.Direction;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

public record EmbryogenesisChamberSpecialRenderer(EmbryogenesisChamberModel model) implements NoDataSpecialModelRenderer {
    private static final EmbryogenesisChamberRenderState ITEM_STATE = new EmbryogenesisChamberRenderState();

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector collector, int light, int overlay, boolean foil, int outlineColor) {
        poseStack.pushPose();
        applyTransform(poseStack);
        collector.submitModel(model, ITEM_STATE, poseStack, RenderTypes.entityCutout(Constants.id("textures/entity/block/embryogenesis_chamber.png")), light, overlay, outlineColor, null);
        poseStack.popPose();
    }

    @Override
    public void getExtents(Consumer<Vector3fc> output) {
        PoseStack poseStack = new PoseStack();
        model.setupAnim(ITEM_STATE);
        applyTransform(poseStack);
        model.root().getExtentsForGui(poseStack, output);
    }

    public static void applyTransform(PoseStack poseStack) {
        poseStack.translate(0.5, 1.5, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - Direction.NORTH.toYRot()));
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.scale(0.75f, 0.75f, 0.75f);
        poseStack.translate(0, 0.575, 0);
    }

    public record Unbaked() implements NoDataSpecialModelRenderer.Unbaked {
        public static final MapCodec<Unbaked> CODEC = MapCodec.unit(Unbaked::new);

        @Override
        public @Nullable SpecialModelRenderer<Void> bake(BakingContext context) {
            return new EmbryogenesisChamberSpecialRenderer(new EmbryogenesisChamberModel(context.entityModelSet().bakeLayer(ModModelLayers.EMBRYOGENESIS_CHAMBER)));
        }

        @Override
        public MapCodec<? extends NoDataSpecialModelRenderer.Unbaked> type() {
            return CODEC;
        }
    }
}
