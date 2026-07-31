package com.coolerpromc.ancientcreature.client.entity.model;

import com.coolerpromc.ancientcreature.client.entity.animation.FossilIdentificationChamberAnimation;
import com.coolerpromc.ancientcreature.client.entity.state.FossilIdentificationChamberRenderState;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class FossilIdentificationChamberModel extends EntityModel<FossilIdentificationChamberRenderState> {
    private final ModelPart fossil_identifying_chamber;
    private final ModelPart frame;
    private final ModelPart glass;
    private final ModelPart specimen;
    private final ModelPart scanner_carriage;
    private final ModelPart scanner_ring;
    private final ModelPart control_panel;
    private final ModelPart status_lights;
    private final KeyframeAnimation identifyAnimation;

    public FossilIdentificationChamberModel(ModelPart root) {
        super(root);
        this.fossil_identifying_chamber = root.getChild("fossil_identifying_chamber");
        this.frame = this.fossil_identifying_chamber.getChild("frame");
        this.glass = this.fossil_identifying_chamber.getChild("glass");
        this.specimen = this.fossil_identifying_chamber.getChild("specimen");
        this.scanner_carriage = this.fossil_identifying_chamber.getChild("scanner_carriage");
        this.scanner_ring = this.scanner_carriage.getChild("scanner_ring");
        this.control_panel = this.fossil_identifying_chamber.getChild("control_panel");
        this.status_lights = this.fossil_identifying_chamber.getChild("status_lights");
        this.identifyAnimation = FossilIdentificationChamberAnimation.IDENTIFY_LOOP.bake(root);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition fossil_identifying_chamber = partdefinition.addOrReplaceChild("fossil_identifying_chamber", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition frame = fossil_identifying_chamber.addOrReplaceChild("frame", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, 21.5F, -8.0F, 16.0F, 2.5F, 16.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0).addBox(-7.0F, 20.5F, -7.0F, 14.0F, 1.0F, 14.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0).addBox(-7.0F, 14.0F, 5.5F, 14.0F, 6.5F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0).addBox(5.5F, 3.0F, -6.0F, 2.0F, 17.5F, 12.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0).addBox(-7.5F, 3.0F, -6.0F, 2.0F, 17.5F, 12.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0).addBox(-5.5F, 2.0F, 4.8F, 11.0F, 2.5F, 2.2F, new CubeDeformation(0.0F))
            .texOffs(0, 0).addBox(4.5F, 23.0F, -8.5F, 3.5F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0).addBox(-8.0F, 23.0F, -8.5F, 3.5F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(64, 0).addBox(-6.5F, 20.8F, -7.2F, 13.0F, 0.6F, 0.5F, new CubeDeformation(0.0F))
            .texOffs(64, 0).addBox(6.7F, 20.8F, -6.5F, 0.5F, 0.6F, 13.0F, new CubeDeformation(0.0F))
            .texOffs(64, 0).addBox(-7.2F, 20.8F, -6.5F, 0.5F, 0.6F, 13.0F, new CubeDeformation(0.0F))
            .texOffs(64, 0).addBox(5.05F, 5.0F, -4.8F, 0.4F, 14.0F, 9.6F, new CubeDeformation(0.0F))
            .texOffs(64, 0).addBox(-5.45F, 5.0F, -4.8F, 0.4F, 14.0F, 9.6F, new CubeDeformation(0.0F))
            .texOffs(64, 0).addBox(-5.0F, 4.0F, 4.5F, 10.0F, 0.7F, 0.7F, new CubeDeformation(0.0F))
            .texOffs(192, 64).addBox(7.4F, 6.0F, 5.8F, 0.4F, 10.0F, 0.4F, new CubeDeformation(0.0F))
            .texOffs(192, 64).addBox(-7.8F, 6.0F, 5.8F, 0.4F, 10.0F, 0.4F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition glass = fossil_identifying_chamber.addOrReplaceChild("glass", CubeListBuilder.create().texOffs(128, 0).addBox(-4.9F, 4.8F, 5.15F, 9.8F, 13.2F, 0.2F, new CubeDeformation(0.0F))
            .texOffs(128, 0).addBox(4.85F, 4.8F, -4.8F, 0.2F, 13.2F, 9.9F, new CubeDeformation(0.0F))
            .texOffs(128, 0).addBox(-5.05F, 4.8F, -4.8F, 0.2F, 13.2F, 9.9F, new CubeDeformation(0.0F))
            .texOffs(128, 0).addBox(-4.8F, 4.8F, -4.8F, 9.6F, 0.2F, 9.8F, new CubeDeformation(0.0F))
            .texOffs(128, 0).addBox(5.03F, 7.0F, -3.7F, 0.05F, 9.0F, 0.35F, new CubeDeformation(0.0F))
            .texOffs(128, 0).addBox(-5.08F, 7.0F, 1.8F, 0.05F, 7.0F, 0.35F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition specimen = fossil_identifying_chamber.addOrReplaceChild("specimen", CubeListBuilder.create().texOffs(192, 0).addBox(-4.2F, 0.8F, -4.2F, 8.4F, 1.0F, 8.4F, new CubeDeformation(0.0F))
            .texOffs(64, 0).addBox(-3.6F, 0.3F, -3.6F, 7.2F, 0.5F, 7.2F, new CubeDeformation(0.0F))
            .texOffs(64, 64).addBox(-3.1F, 0.05F, -3.1F, 6.2F, 0.25F, 6.2F, new CubeDeformation(0.0F))
            .texOffs(0, 64).addBox(-1.5F, -0.25F, 2.2F, 3.0F, 0.25F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 18.5F, 0.0F));

        PartDefinition bone_tail_r1 = specimen.addOrReplaceChild("bone_tail_r1", CubeListBuilder.create().texOffs(0, 64).addBox(-1.4F, -0.25F, -0.35F, 1.7F, 0.5F, 0.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.4F, -0.6F, 0.0F, 0.0F, 0.3142F, 0.0F));

        PartDefinition bone_rib_back_right_r1 = specimen.addOrReplaceChild("bone_rib_back_right_r1", CubeListBuilder.create().texOffs(0, 64).addBox(-0.2F, -0.75F, 0.0F, 0.45F, 1.1F, 1.15F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, -0.6F, 0.2F, 0.0F, 0.0F, 0.3491F));

        PartDefinition bone_rib_back_left_r1 = specimen.addOrReplaceChild("bone_rib_back_left_r1", CubeListBuilder.create().texOffs(0, 64).addBox(-0.2F, -0.75F, -1.25F, 0.45F, 1.1F, 1.2F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.35F, -0.6F, -0.25F, 0.0F, 0.0F, -0.3491F));

        PartDefinition bone_rib_front_right_r1 = specimen.addOrReplaceChild("bone_rib_front_right_r1", CubeListBuilder.create().texOffs(0, 64).addBox(-0.2F, -0.85F, 0.0F, 0.5F, 1.2F, 1.2F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.4F, -0.6F, 0.2F, 0.0F, 0.0F, 0.4189F));

        PartDefinition bone_rib_front_left_r1 = specimen.addOrReplaceChild("bone_rib_front_left_r1", CubeListBuilder.create().texOffs(0, 64).addBox(-0.2F, -0.85F, -1.25F, 0.5F, 1.2F, 1.2F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.1F, -0.6F, -0.3F, 0.0F, 0.0F, -0.4189F));

        PartDefinition rubber_orbit_r1 = specimen.addOrReplaceChild("rubber_orbit_r1", CubeListBuilder.create().texOffs(192, 64).addBox(-0.25F, -0.25F, -0.03F, 0.5F, 0.4F, 0.08F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.3F, -0.8F, -1.2F, 0.0F, -0.2094F, 0.0F));

        PartDefinition bone_skull_snout_r1 = specimen.addOrReplaceChild("bone_skull_snout_r1", CubeListBuilder.create().texOffs(0, 64).addBox(-0.15F, -0.35F, -0.8F, 0.8F, 0.65F, 1.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.7F, -0.6F, 0.0F, 0.0F, -0.2094F, 0.0F));

        PartDefinition bone_skull_main_r1 = specimen.addOrReplaceChild("bone_skull_main_r1", CubeListBuilder.create().texOffs(0, 64).addBox(-0.6F, -0.7F, -1.2F, 1.4F, 1.1F, 1.75F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.0F, -0.6F, 0.0F, 0.0F, -0.2094F, 0.0F));

        PartDefinition bone_spine_r1 = specimen.addOrReplaceChild("bone_spine_r1", CubeListBuilder.create().texOffs(0, 64).addBox(-2.5F, -0.45F, -0.45F, 5.3F, 0.65F, 0.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.5F, 0.0F, 0.0F, -0.2094F, 0.0F));

        PartDefinition scanner_carriage = fossil_identifying_chamber.addOrReplaceChild("scanner_carriage", CubeListBuilder.create().texOffs(192, 0).addBox(4.65F, -1.4F, -1.5F, 1.0F, 2.8F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(192, 0).addBox(-5.65F, -1.4F, -1.5F, 1.0F, 2.8F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(128, 64).addBox(4.45F, -0.8F, -0.8F, 0.25F, 1.6F, 1.6F, new CubeDeformation(0.0F))
            .texOffs(128, 64).addBox(-4.7F, -0.8F, -0.8F, 0.25F, 1.6F, 1.6F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 11.0F, 0.0F));

        PartDefinition scanner_ring = scanner_carriage.addOrReplaceChild("scanner_ring", CubeListBuilder.create().texOffs(192, 0).addBox(-4.7F, -0.45F, -4.7F, 9.4F, 0.9F, 0.65F, new CubeDeformation(0.0F))
            .texOffs(192, 0).addBox(-4.7F, -0.45F, 4.05F, 9.4F, 0.9F, 0.65F, new CubeDeformation(0.0F))
            .texOffs(192, 0).addBox(4.05F, -0.45F, -4.05F, 0.65F, 0.9F, 8.1F, new CubeDeformation(0.0F))
            .texOffs(192, 0).addBox(-4.7F, -0.45F, -4.05F, 0.65F, 0.9F, 8.1F, new CubeDeformation(0.0F))
            .texOffs(192, 0).addBox(-1.2F, -0.9F, -4.95F, 2.4F, 1.8F, 0.35F, new CubeDeformation(0.0F))
            .texOffs(192, 0).addBox(-1.2F, -0.9F, 4.6F, 2.4F, 1.8F, 0.35F, new CubeDeformation(0.0F))
            .texOffs(128, 64).addBox(-0.8F, -0.55F, -5.0F, 1.6F, 1.1F, 0.04F, new CubeDeformation(0.0F))
            .texOffs(128, 64).addBox(-0.8F, -0.55F, 4.96F, 1.6F, 1.1F, 0.04F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition control_panel = fossil_identifying_chamber.addOrReplaceChild("control_panel", CubeListBuilder.create().texOffs(0, 0).addBox(-7.8F, 12.0F, -7.0F, 2.3F, 5.0F, 1.5F, new CubeDeformation(0.0F))
            .texOffs(64, 64).addBox(-7.25F, 13.2F, -7.1F, 2.0F, 2.6F, 0.08F, new CubeDeformation(0.0F))
            .texOffs(128, 64).addBox(-7.7F, 14.9F, -7.12F, 0.35F, 0.7F, 0.1F, new CubeDeformation(0.0F))
            .texOffs(64, 0).addBox(-0.8F, 19.65F, -8.15F, 4.0F, 0.65F, 0.65F, new CubeDeformation(0.0F))
            .texOffs(64, 64).addBox(-3.8F, 6.5F, 5.05F, 7.6F, 6.0F, 0.4F, new CubeDeformation(0.0F))
            .texOffs(128, 64).addBox(-1.5F, 11.0F, 4.98F, 4.3F, 0.5F, 0.04F, new CubeDeformation(0.0F))
            .texOffs(128, 64).addBox(-2.8F, 9.3F, 4.98F, 4.0F, 0.5F, 0.04F, new CubeDeformation(0.0F))
            .texOffs(128, 64).addBox(-0.8F, 7.6F, 4.98F, 3.0F, 0.5F, 0.04F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition status_lights = fossil_identifying_chamber.addOrReplaceChild("status_lights", CubeListBuilder.create().texOffs(128, 64).addBox(7.3F, 7.0F, -6.15F, 0.3F, 5.0F, 0.35F, new CubeDeformation(0.0F))
            .texOffs(128, 64).addBox(-7.6F, 7.0F, -6.15F, 0.3F, 5.0F, 0.35F, new CubeDeformation(0.0F))
            .texOffs(128, 64).addBox(-3.0F, 21.55F, -8.1F, 6.0F, 0.55F, 0.3F, new CubeDeformation(0.0F))
            .texOffs(128, 64).addBox(-2.5F, 3.0F, 4.45F, 5.0F, 0.8F, 0.35F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    @Override
    public void setupAnim(FossilIdentificationChamberRenderState state) {
        super.setupAnim(state);
        this.scanner_carriage.resetPose();
        this.scanner_ring.resetPose();
        this.specimen.resetPose();
        this.status_lights.resetPose();
        this.identifyAnimation.apply(state.identifyingState, state.ageInTicks);
    }
}
