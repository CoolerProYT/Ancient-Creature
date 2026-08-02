package com.coolerpromc.ancientcreature.client.entity.model;

import com.coolerpromc.ancientcreature.client.entity.animation.MegalodonAnimation;
import com.coolerpromc.ancientcreature.client.entity.state.MegalodonRenderState;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

public class MegalodonModel extends EntityModel<MegalodonRenderState> {
	private final ModelPart body;
	private final ModelPart head;
	private final ModelPart jaw;
	private final ModelPart tail_base;
	private final ModelPart tail_mid;
	private final ModelPart tail_tip;
	private final ModelPart pectoral_left;
	private final ModelPart pectoral_right;
	private final ModelPart dorsal_fin;
	private final KeyframeAnimation idleAnimation;
	private final KeyframeAnimation swimAnimation;
	private final KeyframeAnimation biteAnimation;

	public MegalodonModel(ModelPart root) {
		super(root.getChild("root"));
		this.body = this.root.getChild("body");
		this.head = this.body.getChild("head");
		this.jaw = this.head.getChild("jaw");
		this.tail_base = this.body.getChild("tail_base");
		this.tail_mid = this.tail_base.getChild("tail_mid");
		this.tail_tip = this.tail_mid.getChild("tail_tip");
		this.pectoral_left = this.body.getChild("pectoral_left");
		this.pectoral_right = this.body.getChild("pectoral_right");
		this.dorsal_fin = this.body.getChild("dorsal_fin");
		this.idleAnimation = MegalodonAnimation.IDLE.bake(root);
		this.swimAnimation = MegalodonAnimation.SWIM.bake(root);
		this.biteAnimation = MegalodonAnimation.BITE.bake(root);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -7.0F, -16.0F, 16.0F, 13.0F, 30.0F, new CubeDeformation(0.0F))
		.texOffs(96, 0).addBox(-9.0F, -5.0F, -11.0F, 18.0F, 9.0F, 23.0F, new CubeDeformation(0.0F))
		.texOffs(0, 176).addBox(-7.0F, 3.0F, -12.0F, 14.0F, 4.0F, 23.0F, new CubeDeformation(0.0F))
		.texOffs(0, 48).addBox(-8.5F, -6.0F, -19.0F, 17.0F, 11.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 22.0F, 0.0F));

		PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(56, 48).addBox(-8.5F, -6.0F, -8.0F, 17.0F, 11.0F, 10.0F, new CubeDeformation(0.0F))
		.texOffs(112, 48).addBox(-7.5F, -5.0F, -15.0F, 15.0F, 9.0F, 9.0F, new CubeDeformation(0.0F))
		.texOffs(164, 48).addBox(-6.5F, -4.0F, -20.0F, 13.0F, 7.0F, 7.0F, new CubeDeformation(0.0F))
		.texOffs(0, 72).addBox(-7.0F, -6.0F, -16.0F, 14.0F, 4.0F, 9.0F, new CubeDeformation(0.0F))
		.texOffs(224, 0).addBox(-8.15F, -2.5F, -13.0F, 0.9F, 1.0F, 1.2F, new CubeDeformation(0.0F))
		.texOffs(224, 0).addBox(7.25F, -2.5F, -13.0F, 0.9F, 1.0F, 1.2F, new CubeDeformation(0.0F))
		.texOffs(224, 0).addBox(-8.3F, -4.0F, -5.0F, 0.25F, 7.0F, 0.8F, new CubeDeformation(0.0F))
		.texOffs(224, 0).addBox(-8.35F, -4.0F, -3.5F, 0.25F, 7.0F, 0.8F, new CubeDeformation(0.0F))
		.texOffs(224, 0).addBox(-8.35F, -4.0F, -2.0F, 0.25F, 7.0F, 0.8F, new CubeDeformation(0.0F))
		.texOffs(224, 0).addBox(8.05F, -4.0F, -5.0F, 0.25F, 7.0F, 0.8F, new CubeDeformation(0.0F))
		.texOffs(224, 0).addBox(8.1F, -4.0F, -3.5F, 0.25F, 7.0F, 0.8F, new CubeDeformation(0.0F))
		.texOffs(224, 0).addBox(8.1F, -4.0F, -2.0F, 0.25F, 7.0F, 0.8F, new CubeDeformation(0.0F))
		.texOffs(224, 24).addBox(-6.3F, 3.8F, -18.0F, 12.6F, 0.9F, 11.5F, new CubeDeformation(0.0F))
		.texOffs(224, 56).addBox(4.62F, 2.7F, -17.8F, 0.76F, 2.9F, 0.9F, new CubeDeformation(0.0F))
		.texOffs(224, 56).addBox(2.62F, 2.7F, -17.8F, 0.76F, 2.9F, 0.9F, new CubeDeformation(0.0F))
		.texOffs(224, 56).addBox(0.62F, 2.7F, -17.8F, 0.76F, 2.9F, 0.9F, new CubeDeformation(0.0F))
		.texOffs(224, 56).addBox(-1.38F, 2.7F, -17.8F, 0.76F, 2.9F, 0.9F, new CubeDeformation(0.0F))
		.texOffs(224, 56).addBox(-3.38F, 2.7F, -17.8F, 0.76F, 2.9F, 0.9F, new CubeDeformation(0.0F))
		.texOffs(224, 56).addBox(-5.38F, 2.7F, -17.8F, 0.76F, 2.9F, 0.9F, new CubeDeformation(0.0F))
		.texOffs(224, 56).addBox(5.6F, 2.5F, -14.4F, 0.8F, 2.8F, 0.9F, new CubeDeformation(0.0F))
		.texOffs(224, 56).addBox(5.6F, 2.5F, -11.4F, 0.8F, 2.8F, 0.9F, new CubeDeformation(0.0F))
		.texOffs(224, 56).addBox(5.6F, 2.5F, -8.4F, 0.8F, 2.8F, 0.9F, new CubeDeformation(0.0F))
		.texOffs(224, 56).addBox(-6.4F, 2.5F, -14.4F, 0.8F, 2.8F, 0.9F, new CubeDeformation(0.0F))
		.texOffs(224, 56).addBox(-6.4F, 2.5F, -11.4F, 0.8F, 2.8F, 0.9F, new CubeDeformation(0.0F))
		.texOffs(224, 56).addBox(-6.4F, 2.5F, -8.4F, 0.8F, 2.8F, 0.9F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -18.0F));

		PartDefinition jaw = head.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(50, 72).addBox(-6.8F, 2.5F, -7.0F, 13.6F, 4.5F, 12.0F, new CubeDeformation(0.0F))
		.texOffs(104, 72).addBox(-5.8F, 5.5F, -5.0F, 11.6F, 2.0F, 9.0F, new CubeDeformation(0.0F))
		.texOffs(224, 24).addBox(-6.2F, 2.2F, -6.5F, 12.4F, 0.7F, 10.7F, new CubeDeformation(0.0F))
		.texOffs(224, 56).addBox(4.62F, 1.0F, -6.4F, 0.76F, 2.8F, 0.9F, new CubeDeformation(0.0F))
		.texOffs(224, 56).addBox(2.62F, 1.0F, -6.4F, 0.76F, 2.8F, 0.9F, new CubeDeformation(0.0F))
		.texOffs(224, 56).addBox(0.62F, 1.0F, -6.4F, 0.76F, 2.8F, 0.9F, new CubeDeformation(0.0F))
		.texOffs(224, 56).addBox(-1.38F, 1.0F, -6.4F, 0.76F, 2.8F, 0.9F, new CubeDeformation(0.0F))
		.texOffs(224, 56).addBox(-3.38F, 1.0F, -6.4F, 0.76F, 2.8F, 0.9F, new CubeDeformation(0.0F))
		.texOffs(224, 56).addBox(-5.38F, 1.0F, -6.4F, 0.76F, 2.8F, 0.9F, new CubeDeformation(0.0F))
		.texOffs(224, 56).addBox(5.6F, 1.2F, -3.4F, 0.8F, 2.8F, 0.9F, new CubeDeformation(0.0F))
		.texOffs(224, 56).addBox(5.6F, 1.2F, -0.4F, 0.8F, 2.8F, 0.9F, new CubeDeformation(0.0F))
		.texOffs(224, 56).addBox(5.6F, 1.2F, 2.6F, 0.8F, 2.8F, 0.9F, new CubeDeformation(0.0F))
		.texOffs(224, 56).addBox(-6.4F, 1.2F, -3.4F, 0.8F, 2.8F, 0.9F, new CubeDeformation(0.0F))
		.texOffs(224, 56).addBox(-6.4F, 1.2F, -0.4F, 0.8F, 2.8F, 0.9F, new CubeDeformation(0.0F))
		.texOffs(224, 56).addBox(-6.4F, 1.2F, 2.6F, 0.8F, 2.8F, 0.9F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -11.0F));

		PartDefinition tail_base = body.addOrReplaceChild("tail_base", CubeListBuilder.create().texOffs(150, 72).addBox(-6.5F, -6.0F, -3.0F, 13.0F, 11.0F, 13.0F, new CubeDeformation(0.0F))
		.texOffs(0, 100).addBox(-5.5F, -5.0F, 7.0F, 11.0F, 9.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 15.0F));

		PartDefinition tail_mid = tail_base.addOrReplaceChild("tail_mid", CubeListBuilder.create().texOffs(40, 100).addBox(-4.5F, -4.5F, -1.0F, 9.0F, 8.0F, 11.0F, new CubeDeformation(0.0F))
		.texOffs(82, 100).addBox(-3.5F, -3.5F, 8.0F, 7.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 13.0F));

		PartDefinition tail_tip = tail_mid.addOrReplaceChild("tail_tip", CubeListBuilder.create().texOffs(112, 100).addBox(-2.2F, -3.5F, 0.0F, 4.4F, 6.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 12.0F));

		PartDefinition tail_lower_2_r1 = tail_tip.addOrReplaceChild("tail_lower_2_r1", CubeListBuilder.create().texOffs(198, 100).addBox(-1.1F, 2.0F, -1.0F, 2.2F, 10.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 8.0F, 8.0F, -0.4014F, 0.0F, 0.0F));

		PartDefinition tail_lower_1_r1 = tail_tip.addOrReplaceChild("tail_lower_1_r1", CubeListBuilder.create().texOffs(178, 100).addBox(-1.4F, 1.5F, 0.0F, 2.8F, 11.5F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 4.0F, -0.2443F, 0.0F, 0.0F));

		PartDefinition tail_upper_2_r1 = tail_tip.addOrReplaceChild("tail_upper_2_r1", CubeListBuilder.create().texOffs(162, 100).addBox(-1.1F, -12.0F, -1.0F, 2.2F, 10.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -9.0F, 8.0F, 0.4189F, 0.0F, 0.0F));

		PartDefinition tail_upper_1_r1 = tail_tip.addOrReplaceChild("tail_upper_1_r1", CubeListBuilder.create().texOffs(140, 100).addBox(-1.4F, -13.0F, -1.0F, 2.8F, 12.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.0F, 4.0F, 0.2618F, 0.0F, 0.0F));

		PartDefinition pectoral_left = body.addOrReplaceChild("pectoral_left", CubeListBuilder.create(), PartPose.offset(-7.0F, 1.0F, -7.0F));

		PartDefinition pectoral_left_tip_r1 = pectoral_left.addOrReplaceChild("pectoral_left_tip_r1", CubeListBuilder.create().texOffs(68, 122).addBox(-11.0F, -0.5F, -2.0F, 11.0F, 1.9F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-14.0F, 2.0F, 4.0F, 0.0F, 0.2094F, -0.384F));

		PartDefinition pectoral_left_main_r1 = pectoral_left.addOrReplaceChild("pectoral_left_main_r1", CubeListBuilder.create().texOffs(0, 122).addBox(-18.0F, -0.2F, -3.0F, 18.0F, 2.2F, 14.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 0.0F, 0.1396F, -0.2443F));

		PartDefinition pectoral_right = body.addOrReplaceChild("pectoral_right", CubeListBuilder.create(), PartPose.offset(7.0F, 1.0F, -7.0F));

		PartDefinition pectoral_right_tip_r1 = pectoral_right.addOrReplaceChild("pectoral_right_tip_r1", CubeListBuilder.create().texOffs(68, 122).addBox(0.0F, -0.5F, -2.0F, 11.0F, 1.9F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(14.0F, 2.0F, 4.0F, 0.0F, -0.2094F, 0.384F));

		PartDefinition pectoral_right_main_r1 = pectoral_right.addOrReplaceChild("pectoral_right_main_r1", CubeListBuilder.create().texOffs(0, 122).addBox(0.0F, -0.2F, -3.0F, 18.0F, 2.2F, 14.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 0.0F, -0.1396F, 0.2443F));

		PartDefinition dorsal_fin = body.addOrReplaceChild("dorsal_fin", CubeListBuilder.create(), PartPose.offset(0.0F, -6.0F, 0.0F));

		PartDefinition dorsal_tip_r1 = dorsal_fin.addOrReplaceChild("dorsal_tip_r1", CubeListBuilder.create().texOffs(160, 122).addBox(-0.9F, -7.0F, -2.0F, 1.8F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -9.0F, 5.0F, 0.6109F, 0.0F, 0.0F));

		PartDefinition dorsal_mid_r1 = dorsal_fin.addOrReplaceChild("dorsal_mid_r1", CubeListBuilder.create().texOffs(140, 122).addBox(-1.25F, -9.0F, -4.0F, 2.5F, 8.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.0F, 5.0F, 0.4887F, 0.0F, 0.0F));

		PartDefinition dorsal_base_r1 = dorsal_fin.addOrReplaceChild("dorsal_base_r1", CubeListBuilder.create().texOffs(108, 122).addBox(-1.6F, -8.0F, -7.0F, 3.2F, 9.0F, 11.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 4.0F, 0.3491F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 256, 256);
	}

	@Override
	public void setupAnim(MegalodonRenderState state) {
		super.setupAnim(state);
		this.idleAnimation.apply(state.idleAnimationState, state.ageInTicks);
		this.swimAnimation.apply(state.swimAnimationState, state.ageInTicks);
		this.biteAnimation.apply(state.biteAnimationState, state.ageInTicks);
		if (!state.biteAnimationState.isStarted()) {
			this.head.yRot += Mth.clamp(state.yRot, -20.0F, 20.0F) * Mth.DEG_TO_RAD;
			this.head.xRot += Mth.clamp(state.xRot, -15.0F, 15.0F) * Mth.DEG_TO_RAD;
		}
	}
}
