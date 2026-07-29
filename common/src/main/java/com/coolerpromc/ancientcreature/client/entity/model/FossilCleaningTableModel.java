package com.coolerpromc.ancientcreature.client.entity.model;

import com.coolerpromc.ancientcreature.client.entity.animation.FossilCleaningTableAnimation;
import com.coolerpromc.ancientcreature.client.entity.state.FossilCleaningTableRenderState;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class FossilCleaningTableModel extends EntityModel<FossilCleaningTableRenderState> {
    private final ModelPart fossilCleaningTable;
    private final ModelPart frame;
    private final ModelPart cleaningSurface;
    private final ModelPart toolsAndFossil;
    private final ModelPart brush;
    private final KeyframeAnimation cleaningAnimation;

    public FossilCleaningTableModel(ModelPart root) {
        super(root);
        this.fossilCleaningTable = root.getChild("fossil_cleaning_table");
        this.frame = this.fossilCleaningTable.getChild("frame");
        this.cleaningSurface = this.fossilCleaningTable.getChild("cleaning_surface");
        this.toolsAndFossil = this.fossilCleaningTable.getChild("tools_and_fossil");
        this.brush = this.toolsAndFossil.getChild("brush");
        this.cleaningAnimation = FossilCleaningTableAnimation.BRUSH_FOSSIL.bake(root);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition fossilCleaningTable = partdefinition.addOrReplaceChild("fossil_cleaning_table", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition frame = fossilCleaningTable.addOrReplaceChild("frame", CubeListBuilder.create().texOffs(51, 19).addBox(5.0F, -11.0F, -7.0F, 2.0F, 11.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(60, 19).addBox(-7.0F, -11.0F, -7.0F, 2.0F, 11.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(33, 19).addBox(5.0F, -11.0F, 5.0F, 2.0F, 11.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(42, 19).addBox(-7.0F, -11.0F, 5.0F, 2.0F, 11.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(90, 48).addBox(-5.0F, -5.0F, -6.75F, 10.0F, 2.0F, 1.5F, new CubeDeformation(0.0F))
            .texOffs(66, 48).addBox(-5.0F, -5.0F, 5.25F, 10.0F, 2.0F, 1.5F, new CubeDeformation(0.0F))
            .texOffs(69, 19).addBox(5.25F, -5.0F, -5.0F, 1.5F, 2.0F, 10.0F, new CubeDeformation(0.0F))
            .texOffs(93, 19).addBox(-6.75F, -5.0F, -5.0F, 1.5F, 2.0F, 10.0F, new CubeDeformation(0.0F))
            .texOffs(102, 36).addBox(-4.0F, -10.0F, -6.75F, 8.0F, 4.0F, 2.75F, new CubeDeformation(0.0F))
            .texOffs(45, 48).addBox(-4.5F, -10.5F, -7.5F, 9.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(32, 60).addBox(-1.0F, -9.0F, -7.85F, 2.0F, 1.0F, 0.5F, new CubeDeformation(0.0F))
            .texOffs(114, 48).addBox(4.75F, -0.75F, -7.25F, 2.5F, 0.75F, 2.5F, new CubeDeformation(0.0F))
            .texOffs(0, 55).addBox(-7.25F, -0.75F, -7.25F, 2.5F, 0.75F, 2.5F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition cleaningSurface = fossilCleaningTable.addOrReplaceChild("cleaning_surface", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, 0.0F, -8.0F, 16.0F, 2.0F, 16.0F, new CubeDeformation(0.0F))
            .texOffs(11, 48).addBox(-7.5F, -3.5F, 6.5F, 15.0F, 3.5F, 1.5F, new CubeDeformation(0.0F))
            .texOffs(65, 0).addBox(6.5F, -1.0F, -8.0F, 1.5F, 1.0F, 14.5F, new CubeDeformation(0.0F))
            .texOffs(0, 19).addBox(-8.0F, -1.0F, -8.0F, 1.5F, 1.0F, 14.5F, new CubeDeformation(0.0F))
            .texOffs(0, 36).addBox(-3.5F, -0.6F, -5.0F, 9.5F, 0.6F, 10.0F, new CubeDeformation(0.0F))
            .texOffs(90, 55).addBox(-4.0F, -1.5F, -5.5F, 10.5F, 1.0F, 0.75F, new CubeDeformation(0.0F))
            .texOffs(66, 55).addBox(-4.0F, -1.5F, 4.75F, 10.5F, 1.0F, 0.75F, new CubeDeformation(0.0F))
            .texOffs(40, 36).addBox(5.75F, -1.5F, -4.75F, 0.75F, 1.0F, 9.5F, new CubeDeformation(0.0F))
            .texOffs(62, 36).addBox(-4.0F, -1.5F, -4.75F, 0.75F, 1.0F, 9.5F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -12.0F, 0.0F));

        PartDefinition toolsAndFossil = fossilCleaningTable.addOrReplaceChild("tools_and_fossil", CubeListBuilder.create().texOffs(0, 60).addBox(-1.5F, -1.0F, 6.0F, 7.5F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(18, 60).addBox(-3.0F, -2.0F, 6.35F, 6.0F, 0.9F, 0.35F, new CubeDeformation(0.0F))
            .texOffs(0, 48).addBox(-7.0F, -2.0F, 3.5F, 2.5F, 3.0F, 2.5F, new CubeDeformation(0.0F))
            .texOffs(60, 55).addBox(-5.8F, -3.0F, 4.3F, 0.5F, 2.0F, 0.5F, new CubeDeformation(0.0F))
            .texOffs(63, 55).addBox(-6.55F, -3.0F, 4.7F, 0.45F, 2.0F, 0.45F, new CubeDeformation(0.0F))
            .texOffs(52, 60).addBox(4.4F, -2.2F, 5.6F, 0.6F, 1.2F, 0.7F, new CubeDeformation(0.0F))
            .texOffs(56, 60).addBox(-0.6F, -2.2F, 5.6F, 0.6F, 1.2F, 0.7F, new CubeDeformation(0.0F))
            .texOffs(35, 55).addBox(-6.7F, -2.1F, 3.8F, 1.9F, 0.15F, 1.9F, new CubeDeformation(0.0F))
            .texOffs(64, 60).addBox(4.35F, 0.1F, -3.8F, 0.45F, 0.25F, 0.45F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -13.0F, 0.0F));

        PartDefinition dustChipTwoR1 = toolsAndFossil.addOrReplaceChild("dust_chip_two_r1", CubeListBuilder.create().texOffs(60, 60).addBox(-0.3F, -0.15F, -0.3F, 0.6F, 0.3F, 0.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.9F, 0.2F, -3.4F, 0.0F, 0.3927F, 0.0F));

        PartDefinition dustChipOneR1 = toolsAndFossil.addOrReplaceChild("dust_chip_one_r1", CubeListBuilder.create().texOffs(48, 60).addBox(-0.35F, -0.2F, -0.35F, 0.7F, 0.35F, 0.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.65F, 0.2F, 2.85F, 0.0F, -0.3927F, 0.0F));

        PartDefinition fossilRidgeTwoR1 = toolsAndFossil.addOrReplaceChild("fossil_ridge_two_r1", CubeListBuilder.create().texOffs(43, 60).addBox(-0.4F, -0.4F, -0.45F, 0.8F, 0.7F, 0.9F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.4F, -0.9F, -0.75F, 0.0F, 0.3927F, 0.0F));

        PartDefinition fossilRidgeOneR1 = toolsAndFossil.addOrReplaceChild("fossil_ridge_one_r1", CubeListBuilder.create().texOffs(38, 60).addBox(-0.4F, -0.4F, -0.45F, 0.8F, 0.7F, 0.9F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.1F, -0.9F, -1.35F, 0.0F, 0.3927F, 0.0F));

        PartDefinition fossilJointRightR1 = toolsAndFossil.addOrReplaceChild("fossil_joint_right_r1", CubeListBuilder.create().texOffs(52, 55).addBox(-0.75F, -0.5F, -0.8F, 1.5F, 1.0F, 1.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.45F, -0.5F, -0.2F, 0.0F, 0.3927F, 0.0F));

        PartDefinition fossilJointLeftR1 = toolsAndFossil.addOrReplaceChild("fossil_joint_left_r1", CubeListBuilder.create().texOffs(44, 55).addBox(-0.75F, -0.5F, -0.8F, 1.5F, 1.0F, 1.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.75F, -0.5F, -1.1F, 0.0F, 0.3927F, 0.0F));

        PartDefinition fossilSpineR1 = toolsAndFossil.addOrReplaceChild("fossil_spine_r1", CubeListBuilder.create().texOffs(11, 55).addBox(-2.75F, -0.35F, -0.75F, 5.5F, 0.7F, 1.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.25F, -0.4F, -0.75F, 0.0F, 0.3927F, 0.0F));

        PartDefinition brush = toolsAndFossil.addOrReplaceChild("brush", CubeListBuilder.create(), PartPose.offset(-5.05F, -0.5F, -3.5F));

        PartDefinition brushBristlesR1 = brush.addOrReplaceChild("brush_bristles_r1", CubeListBuilder.create().texOffs(26, 55).addBox(-1.05F, -0.5F, -0.95F, 2.05F, 1.1F, 1.9F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -0.45F, 0.0F, 0.3927F, 0.0F));

        PartDefinition brushHandleR1 = brush.addOrReplaceChild("brush_handle_r1", CubeListBuilder.create().texOffs(84, 36).addBox(-0.45F, -0.4F, -3.75F, 0.9F, 0.8F, 7.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 3.25F, 0.0F, 0.3927F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(FossilCleaningTableRenderState state) {
        super.setupAnim(state);
        this.frame.resetPose();
        this.cleaningSurface.resetPose();
        this.toolsAndFossil.resetPose();
        this.brush.resetPose();

        this.cleaningAnimation.apply(state.cleaningState, state.ageInTicks);
    }
}
