package com.coolerpromc.ancientcreature.client.entity.model.block;

import com.coolerpromc.ancientcreature.client.entity.animation.block.DNAExtractorAnimation;
import com.coolerpromc.ancientcreature.client.entity.state.block.DNAExtractorRenderState;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class DNAExtractorModel extends EntityModel<DNAExtractorRenderState> {
	private final ModelPart dna_extractor;
	private final ModelPart housing;
	private final ModelPart chamber;
	private final ModelPart rotor;
	private final ModelPart left_clamp;
	private final ModelPart right_clamp;
	private final ModelPart scanner;
	private final ModelPart controls;
	private final KeyframeAnimation extractingAnimation;

	public DNAExtractorModel(ModelPart root) {
        super(root);
        this.dna_extractor = root.getChild("dna_extractor");
		this.housing = this.dna_extractor.getChild("housing");
		this.chamber = this.dna_extractor.getChild("chamber");
		this.rotor = this.chamber.getChild("rotor");
		this.left_clamp = this.chamber.getChild("left_clamp");
		this.right_clamp = this.chamber.getChild("right_clamp");
		this.scanner = this.chamber.getChild("scanner");
		this.controls = this.dna_extractor.getChild("controls");
		this.extractingAnimation = DNAExtractorAnimation.PROCESSING.bake(root);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition dna_extractor = partdefinition.addOrReplaceChild("dna_extractor", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition housing = dna_extractor.addOrReplaceChild("housing", CubeListBuilder.create().texOffs(0, 64).addBox(-8.0F, 20.5F, -8.0F, 16.0F, 3.5F, 16.0F, new CubeDeformation(0.0F))
		.texOffs(64, 96).addBox(-7.5F, 19.25F, -7.5F, 15.0F, 1.25F, 15.0F, new CubeDeformation(0.0F))
		.texOffs(64, 0).addBox(5.5F, 23.0F, -7.5F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(64, 0).addBox(-7.5F, 23.0F, -7.5F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(64, 0).addBox(5.5F, 23.0F, 5.5F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(64, 0).addBox(-7.5F, 23.0F, 5.5F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(64, 0).addBox(5.5F, 4.25F, -7.5F, 2.0F, 11.75F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(64, 0).addBox(-7.5F, 4.25F, -7.5F, 2.0F, 11.75F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 96).addBox(-8.0F, 1.0F, -8.0F, 16.0F, 2.0F, 16.0F, new CubeDeformation(0.0F))
		.texOffs(64, 96).addBox(-7.5F, 3.0F, -7.5F, 15.0F, 1.25F, 15.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-7.5F, 4.0F, 6.0F, 15.0F, 14.5F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(64, 64).addBox(-7.0F, 16.0F, -8.25F, 14.0F, 4.0F, 0.75F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-6.5F, 0.0F, -6.5F, 13.0F, 1.0F, 13.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(7.25F, 4.25F, -4.75F, 0.75F, 15.0F, 10.25F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-8.0F, 4.25F, -4.75F, 0.75F, 15.0F, 10.25F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(5.25F, 3.0F, -7.0F, 2.75F, 2.5F, 13.5F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-8.0F, 3.0F, -7.0F, 2.75F, 2.5F, 13.5F, new CubeDeformation(0.0F))
		.texOffs(64, 96).addBox(3.0F, 0.75F, -8.15F, 1.5F, 0.75F, 0.4F, new CubeDeformation(0.0F))
		.texOffs(64, 96).addBox(0.75F, 0.75F, -8.15F, 1.5F, 0.75F, 0.4F, new CubeDeformation(0.0F))
		.texOffs(64, 96).addBox(-2.25F, 0.75F, -8.15F, 1.5F, 0.75F, 0.4F, new CubeDeformation(0.0F))
		.texOffs(64, 96).addBox(-4.5F, 0.75F, -8.15F, 1.5F, 0.75F, 0.4F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition chamber = dna_extractor.addOrReplaceChild("chamber", CubeListBuilder.create().texOffs(0, 32).addBox(-5.5F, 1.5F, -5.5F, 11.0F, 1.5F, 11.0F, new CubeDeformation(0.0F))
		.texOffs(0, 32).addBox(-5.5F, -8.5F, -5.5F, 11.0F, 1.5F, 11.0F, new CubeDeformation(0.0F))
		.texOffs(64, 32).addBox(4.75F, -6.0F, -5.5F, 0.75F, 8.0F, 11.0F, new CubeDeformation(0.0F))
		.texOffs(64, 32).addBox(-5.5F, -6.0F, -5.5F, 0.75F, 8.0F, 11.0F, new CubeDeformation(0.0F))
		.texOffs(64, 32).addBox(-4.75F, -6.0F, 4.75F, 9.5F, 8.0F, 0.75F, new CubeDeformation(0.0F))
		.texOffs(64, 0).addBox(4.25F, -6.0F, -5.5F, 1.25F, 8.0F, 0.75F, new CubeDeformation(0.0F))
		.texOffs(64, 0).addBox(-5.5F, -6.0F, -5.5F, 1.25F, 8.0F, 0.75F, new CubeDeformation(0.0F))
		.texOffs(64, 0).addBox(-5.5F, -7.5F, -5.75F, 11.0F, 1.25F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(64, 0).addBox(-5.5F, 0.75F, -5.75F, 11.0F, 1.25F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(96, 0).addBox(2.8F, -6.75F, 5.45F, 0.9F, 7.75F, 0.7F, new CubeDeformation(0.0F))
		.texOffs(96, 0).addBox(-3.7F, -6.75F, 5.45F, 0.9F, 7.75F, 0.7F, new CubeDeformation(0.0F))
		.texOffs(96, 0).addBox(-3.7F, -6.75F, 5.45F, 7.4F, 0.95F, 0.7F, new CubeDeformation(0.0F))
		.texOffs(96, 32).addBox(4.2F, -5.75F, -5.85F, 0.5F, 6.0F, 0.25F, new CubeDeformation(0.0F))
		.texOffs(96, 32).addBox(-4.7F, -5.75F, -5.85F, 0.5F, 6.0F, 0.25F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 13.0F, 0.0F));

		PartDefinition right_diagonal_brace_r1 = chamber.addOrReplaceChild("right_diagonal_brace_r1", CubeListBuilder.create().texOffs(64, 0).addBox(-0.45F, -3.75F, -0.5F, 0.9F, 7.5F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.9F, -2.75F, -5.35F, 0.0F, 0.0F, 0.1309F));

		PartDefinition left_diagonal_brace_r1 = chamber.addOrReplaceChild("left_diagonal_brace_r1", CubeListBuilder.create().texOffs(64, 0).addBox(-0.45F, -3.75F, -0.5F, 0.9F, 7.5F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.9F, -2.75F, -5.35F, 0.0F, 0.0F, -0.1309F));

		PartDefinition rotor = chamber.addOrReplaceChild("rotor", CubeListBuilder.create().texOffs(64, 0).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(64, 0).addBox(-4.25F, -0.75F, -0.75F, 8.5F, 1.5F, 1.5F, new CubeDeformation(0.0F))
		.texOffs(64, 0).addBox(-0.75F, -0.75F, -4.25F, 1.5F, 1.5F, 8.5F, new CubeDeformation(0.0F))
		.texOffs(96, 32).addBox(-1.0F, -3.0F, -1.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(96, 0).addBox(-1.4F, -4.0F, -1.4F, 2.8F, 1.5F, 2.8F, new CubeDeformation(0.0F))
		.texOffs(96, 32).addBox(-0.65F, -1.5F, -1.05F, 1.3F, 3.5F, 0.25F, new CubeDeformation(0.0F))
		.texOffs(64, 0).addBox(-1.15F, -1.2F, -4.9F, 2.3F, 2.4F, 1.4F, new CubeDeformation(0.0F))
		.texOffs(64, 0).addBox(-1.15F, -1.2F, 3.5F, 2.3F, 2.4F, 1.4F, new CubeDeformation(0.0F))
		.texOffs(64, 0).addBox(3.5F, -1.2F, -1.15F, 1.4F, 2.4F, 2.3F, new CubeDeformation(0.0F))
		.texOffs(64, 0).addBox(-4.9F, -1.2F, -1.15F, 1.4F, 2.4F, 2.3F, new CubeDeformation(0.0F))
		.texOffs(96, 0).addBox(-2.5F, 1.5F, -2.5F, 5.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.0F, 0.0F));

		PartDefinition rotor_arm_diag_b_r1 = rotor.addOrReplaceChild("rotor_arm_diag_b_r1", CubeListBuilder.create().texOffs(96, 0).addBox(-0.7F, -0.7F, -4.4F, 1.4F, 1.4F, 8.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.7854F, 0.0F));

		PartDefinition rotor_arm_diag_a_r1 = rotor.addOrReplaceChild("rotor_arm_diag_a_r1", CubeListBuilder.create().texOffs(96, 0).addBox(-0.7F, -0.7F, -4.4F, 1.4F, 1.4F, 8.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.7854F, 0.0F));

		PartDefinition left_clamp = chamber.addOrReplaceChild("left_clamp", CubeListBuilder.create().texOffs(64, 0).addBox(-2.75F, -1.0F, -1.0F, 2.75F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(64, 0).addBox(-3.25F, -2.5F, -1.5F, 1.0F, 1.5F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(64, 0).addBox(-3.25F, 1.0F, -1.5F, 1.0F, 1.5F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(5.0F, -1.0F, 0.0F));

		PartDefinition right_clamp = chamber.addOrReplaceChild("right_clamp", CubeListBuilder.create().texOffs(64, 0).addBox(0.0F, -1.0F, -1.0F, 2.75F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(64, 0).addBox(2.25F, -2.5F, -1.5F, 1.0F, 1.5F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(64, 0).addBox(2.25F, 1.0F, -1.5F, 1.0F, 1.5F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, -1.0F, 0.0F));

		PartDefinition scanner = chamber.addOrReplaceChild("scanner", CubeListBuilder.create().texOffs(64, 0).addBox(-3.0F, -1.0F, -3.0F, 6.0F, 1.5F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(64, 96).addBox(-2.0F, 0.5F, -2.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(96, 32).addBox(-1.25F, 2.25F, -1.25F, 2.5F, 0.5F, 2.5F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -7.0F, 0.0F));

		PartDefinition controls = dna_extractor.addOrReplaceChild("controls", CubeListBuilder.create().texOffs(0, 0).addBox(4.6F, 8.5F, -7.9F, 2.1F, 5.5F, 1.5F, new CubeDeformation(0.0F))
		.texOffs(96, 0).addBox(4.4F, 8.0F, -8.05F, 2.5F, 1.5F, 1.8F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-6.7F, 8.5F, -7.9F, 2.1F, 5.5F, 1.5F, new CubeDeformation(0.0F))
		.texOffs(64, 0).addBox(-6.9F, 8.0F, -8.05F, 2.5F, 1.5F, 1.8F, new CubeDeformation(0.0F))
		.texOffs(96, 32).addBox(-6.1F, 9.8F, -8.15F, 0.9F, 3.4F, 0.3F, new CubeDeformation(0.0F))
		.texOffs(64, 0).addBox(7.7F, 17.4F, -3.5F, 0.5F, 0.6F, 7.0F, new CubeDeformation(0.0F))
		.texOffs(64, 0).addBox(7.7F, 16.1F, -3.5F, 0.5F, 0.6F, 7.0F, new CubeDeformation(0.0F))
		.texOffs(64, 0).addBox(-8.2F, 17.4F, -3.5F, 0.5F, 0.6F, 7.0F, new CubeDeformation(0.0F))
		.texOffs(64, 0).addBox(-8.2F, 16.1F, -3.5F, 0.5F, 0.6F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition screen_wave_5_r1 = controls.addOrReplaceChild("screen_wave_5_r1", CubeListBuilder.create().texOffs(96, 32).addBox(-1.15F, -0.3F, -1.59F, 0.85F, 0.25F, 0.16F, new CubeDeformation(0.0F))
		.texOffs(96, 32).addBox(-0.3F, -0.1F, -1.59F, 0.85F, 0.25F, 0.16F, new CubeDeformation(0.0F))
		.texOffs(96, 32).addBox(0.55F, -0.5F, -1.59F, 0.85F, 0.25F, 0.16F, new CubeDeformation(0.0F))
		.texOffs(96, 32).addBox(1.4F, 0.05F, -1.59F, 0.85F, 0.25F, 0.16F, new CubeDeformation(0.0F))
		.texOffs(96, 32).addBox(2.25F, -0.2F, -1.59F, 0.85F, 0.25F, 0.16F, new CubeDeformation(0.0F))
		.texOffs(96, 32).addBox(-1.25F, 0.2F, -1.5F, 0.65F, 0.65F, 0.4F, new CubeDeformation(0.0F))
		.texOffs(96, 0).addBox(-0.1F, 0.2F, -1.5F, 0.65F, 0.65F, 0.4F, new CubeDeformation(0.0F))
		.texOffs(96, 32).addBox(1.05F, 0.2F, -1.5F, 0.65F, 0.65F, 0.4F, new CubeDeformation(0.0F))
		.texOffs(96, 32).addBox(-3.5F, -0.65F, -1.5F, 0.9F, 0.9F, 0.4F, new CubeDeformation(0.0F))
		.texOffs(64, 64).addBox(-1.8F, -1.0F, -1.45F, 5.4F, 1.5F, 0.35F, new CubeDeformation(0.0F))
		.texOffs(64, 64).addBox(-4.75F, -1.5F, -0.95F, 9.5F, 3.0F, 1.45F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 17.5F, -7.25F, 0.2182F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void setupAnim(DNAExtractorRenderState state) {
		this.housing.resetPose();
		this.chamber.resetPose();
		this.rotor.resetPose();
		this.left_clamp.resetPose();
		this.right_clamp.resetPose();
		this.scanner.resetPose();
		this.controls.resetPose();
		this.extractingAnimation.apply(state.extractingState, state.ageInTicks);
	}
}