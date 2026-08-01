package com.coolerpromc.ancientcreature.client.entity.model.block;

import com.coolerpromc.ancientcreature.client.entity.animation.block.GenomeSequencerAnimation;
import com.coolerpromc.ancientcreature.client.entity.state.block.GenomeSequencerRenderState;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class GenomeSequencerModel extends EntityModel<GenomeSequencerRenderState> {
    private final ModelPart genome_sequencer;
    private final ModelPart chassis;
    private final ModelPart chamber;
    private final ModelPart analysis_ring;
    private final ModelPart scan_head;
    private final ModelPart mounts;
    private final ModelPart cartridge_tray;
    private final ModelPart controls;
    private final ModelPart status_lights;
    private final KeyframeAnimation processingAnimation;

    public GenomeSequencerModel(ModelPart root) {
        super(root);
        this.genome_sequencer = root.getChild("genome_sequencer");
        this.chassis = this.genome_sequencer.getChild("chassis");
        this.chamber = this.genome_sequencer.getChild("chamber");
        this.analysis_ring = this.chamber.getChild("analysis_ring");
        this.scan_head = this.chamber.getChild("scan_head");
        this.mounts = this.chamber.getChild("mounts");
        this.cartridge_tray = this.genome_sequencer.getChild("cartridge_tray");
        this.controls = this.genome_sequencer.getChild("controls");
        this.status_lights = this.controls.getChild("status_lights");
        this.processingAnimation = GenomeSequencerAnimation.PROCESSING.bake(root);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition genome_sequencer = partdefinition.addOrReplaceChild("genome_sequencer", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition chassis = genome_sequencer.addOrReplaceChild("chassis", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -3.0F, -8.0F, 16.0F, 3.0F, 16.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0).addBox(-7.0F, -22.0F, 5.5F, 14.0F, 18.0F, 2.5F, new CubeDeformation(0.0F))
            .texOffs(0, 0).addBox(6.0F, -22.0F, -6.5F, 2.0F, 18.0F, 12.5F, new CubeDeformation(0.0F))
            .texOffs(0, 0).addBox(-8.0F, -22.0F, -6.5F, 2.0F, 18.0F, 12.5F, new CubeDeformation(0.0F))
            .texOffs(0, 0).addBox(-8.0F, -24.0F, -6.5F, 16.0F, 2.0F, 14.5F, new CubeDeformation(0.0F))
            .texOffs(64, 0).addBox(-7.5F, -4.0F, -7.5F, 15.0F, 1.0F, 15.0F, new CubeDeformation(0.0F))
            .texOffs(64, 0).addBox(-5.7F, -5.5F, -5.2F, 11.4F, 1.5F, 10.4F, new CubeDeformation(0.0F))
            .texOffs(64, 0).addBox(-6.2F, -22.0F, -6.7F, 12.4F, 1.5F, 1.4F, new CubeDeformation(0.0F))
            .texOffs(64, 0).addBox(-5.5F, -20.5F, 5.1F, 11.0F, 13.5F, 0.5F, new CubeDeformation(0.0F))
            .texOffs(128, 0).addBox(-7.2F, -3.6F, -7.8F, 14.4F, 0.6F, 0.6F, new CubeDeformation(0.0F))
            .texOffs(128, 0).addBox(7.2F, -21.5F, -7.2F, 0.6F, 18.5F, 0.6F, new CubeDeformation(0.0F))
            .texOffs(128, 0).addBox(-7.8F, -21.5F, -7.2F, 0.6F, 18.5F, 0.6F, new CubeDeformation(0.0F))
            .texOffs(128, 0).addBox(-7.2F, -21.9F, -7.2F, 14.4F, 0.6F, 0.6F, new CubeDeformation(0.0F))
            .texOffs(64, 0).addBox(-1.2F, -5.3F, -8.3F, 2.4F, 1.3F, 3.4F, new CubeDeformation(0.0F))
            .texOffs(64, 0).addBox(3.35F, -6.8F, -8.1F, 0.85F, 2.3F, 3.1F, new CubeDeformation(0.0F))
            .texOffs(64, 0).addBox(-4.2F, -6.8F, -8.1F, 0.85F, 2.3F, 3.1F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition chamber = genome_sequencer.addOrReplaceChild("chamber", CubeListBuilder.create().texOffs(192, 0).addBox(5.15F, -8.5F, -5.0F, 0.4F, 15.0F, 10.0F, new CubeDeformation(0.0F))
            .texOffs(192, 0).addBox(-5.55F, -8.5F, -5.0F, 0.4F, 15.0F, 10.0F, new CubeDeformation(0.0F))
            .texOffs(192, 0).addBox(-5.15F, -8.5F, 4.85F, 10.3F, 15.0F, 0.3F, new CubeDeformation(0.0F))
            .texOffs(192, 0).addBox(-5.15F, -8.5F, -5.0F, 10.3F, 0.3F, 10.0F, new CubeDeformation(0.0F))
            .texOffs(0, 64).addBox(4.4F, -8.0F, -4.8F, 0.5F, 14.0F, 0.5F, new CubeDeformation(0.0F))
            .texOffs(0, 64).addBox(-4.9F, -8.0F, -4.8F, 0.5F, 14.0F, 0.5F, new CubeDeformation(0.0F))
            .texOffs(0, 64).addBox(-0.25F, -8.0F, 4.3F, 0.5F, 14.0F, 0.5F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -12.0F, 0.0F));

        PartDefinition analysis_ring = chamber.addOrReplaceChild("analysis_ring", CubeListBuilder.create().texOffs(128, 0).addBox(-4.8F, -0.4F, -5.2F, 9.6F, 0.8F, 0.8F, new CubeDeformation(0.0F))
            .texOffs(128, 0).addBox(-4.8F, -0.4F, 4.4F, 9.6F, 0.8F, 0.8F, new CubeDeformation(0.0F))
            .texOffs(128, 0).addBox(4.4F, -0.4F, -4.4F, 0.8F, 0.8F, 8.8F, new CubeDeformation(0.0F))
            .texOffs(128, 0).addBox(-5.2F, -0.4F, -4.4F, 0.8F, 0.8F, 8.8F, new CubeDeformation(0.0F))
            .texOffs(0, 64).addBox(-3.8F, -0.65F, -5.25F, 7.6F, 1.3F, 0.2F, new CubeDeformation(0.0F))
            .texOffs(0, 64).addBox(-3.8F, -0.65F, 5.05F, 7.6F, 1.3F, 0.2F, new CubeDeformation(0.0F))
            .texOffs(0, 64).addBox(5.05F, -0.65F, -3.8F, 0.2F, 1.3F, 7.6F, new CubeDeformation(0.0F))
            .texOffs(0, 64).addBox(-5.25F, -0.65F, -3.8F, 0.2F, 1.3F, 7.6F, new CubeDeformation(0.0F))
            .texOffs(128, 0).addBox(-1.15F, -0.65F, -1.15F, 2.3F, 1.3F, 2.3F, new CubeDeformation(0.0F))
            .texOffs(128, 0).addBox(-0.35F, -0.3F, -4.4F, 0.7F, 0.6F, 3.4F, new CubeDeformation(0.0F))
            .texOffs(128, 0).addBox(-0.35F, -0.3F, 1.0F, 0.7F, 0.6F, 3.4F, new CubeDeformation(0.0F))
            .texOffs(128, 0).addBox(1.0F, -0.3F, -0.35F, 3.4F, 0.6F, 0.7F, new CubeDeformation(0.0F))
            .texOffs(128, 0).addBox(-4.4F, -0.3F, -0.35F, 3.4F, 0.6F, 0.7F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.0F, 0.0F));

        PartDefinition scan_head = chamber.addOrReplaceChild("scan_head", CubeListBuilder.create().texOffs(64, 0).addBox(-3.5F, -1.3F, -3.5F, 7.0F, 1.6F, 7.0F, new CubeDeformation(0.0F))
            .texOffs(64, 0).addBox(-2.0F, 0.3F, -2.0F, 4.0F, 0.9F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(0, 64).addBox(-1.4F, 1.2F, -1.4F, 2.8F, 0.4F, 2.8F, new CubeDeformation(0.0F))
            .texOffs(0, 64).addBox(-3.0F, 0.35F, -0.2F, 6.0F, 0.4F, 0.4F, new CubeDeformation(0.0F))
            .texOffs(0, 64).addBox(-0.2F, 0.35F, -3.0F, 0.4F, 0.4F, 6.0F, new CubeDeformation(0.0F))
            .texOffs(64, 0).addBox(2.85F, -1.55F, 2.45F, 1.2F, 2.1F, 1.2F, new CubeDeformation(0.0F))
            .texOffs(64, 0).addBox(-4.05F, -1.55F, 2.45F, 1.2F, 2.1F, 1.2F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -7.0F, 0.0F));

        PartDefinition mounts = chamber.addOrReplaceChild("mounts", CubeListBuilder.create().texOffs(192, 64).addBox(3.1F, -21.2F, 2.7F, 0.7F, 15.7F, 0.7F, new CubeDeformation(0.0F))
            .texOffs(192, 64).addBox(-3.8F, -21.2F, 2.7F, 0.7F, 15.7F, 0.7F, new CubeDeformation(0.0F))
            .texOffs(192, 64).addBox(-3.8F, -21.5F, 2.7F, 7.6F, 1.0F, 0.7F, new CubeDeformation(0.0F))
            .texOffs(192, 64).addBox(-3.8F, -6.3F, 2.7F, 7.6F, 0.8F, 0.7F, new CubeDeformation(0.0F))
            .texOffs(192, 64).addBox(-0.55F, -12.8F, 3.7F, 1.1F, 7.3F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(128, 0).addBox(-1.2F, -13.8F, 3.7F, 2.4F, 2.0F, 1.3F, new CubeDeformation(0.0F))
            .texOffs(192, 64).addBox(-0.55F, -12.6F, -0.55F, 1.1F, 7.1F, 1.1F, new CubeDeformation(0.0F))
            .texOffs(128, 0).addBox(-1.35F, -6.4F, -1.35F, 2.7F, 0.9F, 2.7F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 12.0F, 0.0F));

        PartDefinition cartridge_tray = genome_sequencer.addOrReplaceChild("cartridge_tray", CubeListBuilder.create().texOffs(64, 0).addBox(-3.6F, -0.2F, -1.8F, 7.2F, 1.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(64, 0).addBox(2.9F, -1.6F, -1.8F, 0.7F, 1.4F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(64, 0).addBox(-3.6F, -1.6F, -1.8F, 0.7F, 1.4F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(128, 64).addBox(-2.2F, -3.7F, -0.6F, 4.4F, 3.5F, 2.3F, new CubeDeformation(0.0F))
            .texOffs(128, 64).addBox(-1.2F, -3.0F, -0.75F, 2.4F, 2.0F, 0.2F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -6.0F, -7.0F));

        PartDefinition controls = genome_sequencer.addOrReplaceChild("controls", CubeListBuilder.create().texOffs(0, 0).addBox(-7.0F, -14.5F, -8.2F, 3.4F, 7.5F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0).addBox(6.1F, -16.0F, -6.4F, 1.8F, 8.0F, 11.2F, new CubeDeformation(0.0F))
            .texOffs(128, 0).addBox(-7.15F, -9.1F, -8.3F, 3.7F, 0.6F, 2.2F, new CubeDeformation(0.0F))
            .texOffs(128, 0).addBox(7.8F, -14.0F, -2.0F, 0.4F, 4.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0).addBox(-4.6F, -19.2F, -7.25F, 9.2F, 1.8F, 0.9F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition screen_glow_r1 = controls.addOrReplaceChild("screen_glow_r1", CubeListBuilder.create().texOffs(64, 64).addBox(-1.1F, -1.8F, -0.35F, 2.2F, 3.4F, 0.23F, new CubeDeformation(0.0F))
            .texOffs(64, 64).addBox(-1.45F, -2.3F, -0.15F, 2.9F, 4.3F, 0.3F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.3F, -11.0F, -8.2F, 0.1396F, 0.0F, 0.0F));

        PartDefinition status_lights = controls.addOrReplaceChild("status_lights", CubeListBuilder.create().texOffs(0, 64).addBox(2.2F, -0.8F, 0.73F, 1.6F, 0.8F, 0.22F, new CubeDeformation(0.0F))
            .texOffs(0, 64).addBox(0.1F, -0.8F, 0.73F, 1.6F, 0.8F, 0.22F, new CubeDeformation(0.0F))
            .texOffs(0, 64).addBox(-2.0F, -0.8F, 0.73F, 1.6F, 0.8F, 0.22F, new CubeDeformation(0.0F))
            .texOffs(0, 64).addBox(-4.1F, -0.8F, 0.73F, 1.6F, 0.8F, 0.22F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -18.0F, -8.15F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    @Override
    public void setupAnim(GenomeSequencerRenderState state) {
        this.chassis.resetPose();
        this.chamber.resetPose();
        this.analysis_ring.resetPose();
        this.scan_head.resetPose();
        this.mounts.resetPose();
        this.cartridge_tray.resetPose();
        this.controls.resetPose();
        this.status_lights.resetPose();
        this.processingAnimation.apply(state.processingState, state.ageInTicks);
    }
}