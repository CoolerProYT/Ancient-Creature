package com.coolerpromc.ancientcreature.client.entity.model;

import com.coolerpromc.ancientcreature.client.entity.state.AncientCreatureRenderState;
import com.coolerpromc.ancientcreature.client.model.bedrock.BakedBedrockModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.rendertype.RenderTypes;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Draws a baked Bedrock geometry. Posing is done by the animator at extract time and stored on the
 * render state; {@link #setupAnim} only loads that stored pose, so the shared part tree can be drawn
 * for many entities in any order.
 */
public class AncientCreatureModel extends EntityModel<AncientCreatureRenderState> {
    public static final int POSE_STRIDE = 10;

    private static final Map<BakedBedrockModel, AncientCreatureModel> MODELS = new WeakHashMap<>();

    private final BakedBedrockModel baked;

    private AncientCreatureModel(BakedBedrockModel baked) {
        super(baked.root(), RenderTypes::entityCutout);
        this.baked = baked;
    }

    /** An empty model, used as the renderer's initial model before any species is drawn. */
    public AncientCreatureModel(ModelPart root) {
        super(root, RenderTypes::entityCutout);
        this.baked = null;
    }

    public static ModelPart emptyRoot() {
        return new ModelPart(List.of(), Map.of());
    }

    public static synchronized AncientCreatureModel of(BakedBedrockModel baked) {
        return MODELS.computeIfAbsent(baked, AncientCreatureModel::new);
    }

    public BakedBedrockModel baked() {
        return this.baked;
    }

    /** Copies the current pose of {@code model}'s parts. */
    public static float[] snapshot(BakedBedrockModel model) {
        List<ModelPart> parts = of(model).allParts();
        float[] pose = new float[parts.size() * POSE_STRIDE];
        int i = 0;
        for (ModelPart part : parts) {
            pose[i] = part.x;
            pose[i + 1] = part.y;
            pose[i + 2] = part.z;
            pose[i + 3] = part.xRot;
            pose[i + 4] = part.yRot;
            pose[i + 5] = part.zRot;
            pose[i + 6] = part.xScale;
            pose[i + 7] = part.yScale;
            pose[i + 8] = part.zScale;
            pose[i + 9] = part.visible ? 1.0F : 0.0F;
            i += POSE_STRIDE;
        }
        return pose;
    }

    @Override
    public void setupAnim(AncientCreatureRenderState state) {
        float[] pose = state.bonePose;
        List<ModelPart> parts = this.allParts();
        if (pose == null || pose.length != parts.size() * POSE_STRIDE) {
            this.resetPose();
            for (ModelPart part : parts) {
                part.visible = true;
            }
            return;
        }
        int i = 0;
        for (ModelPart part : parts) {
            part.x = pose[i];
            part.y = pose[i + 1];
            part.z = pose[i + 2];
            part.xRot = pose[i + 3];
            part.yRot = pose[i + 4];
            part.zRot = pose[i + 5];
            part.xScale = pose[i + 6];
            part.yScale = pose[i + 7];
            part.zScale = pose[i + 8];
            part.visible = pose[i + 9] != 0.0F;
            i += POSE_STRIDE;
        }
    }
}
