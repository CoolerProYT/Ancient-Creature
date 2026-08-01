package com.coolerpromc.ancientcreature.client.entity.model;

import com.coolerpromc.ancientcreature.client.entity.animation.TyrannosaurusRexAnimation;
import com.coolerpromc.ancientcreature.client.entity.state.TyrannosaurusRexRenderState;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;

public class TyrannosaurusRexModel extends EntityModel<TyrannosaurusRexRenderState> {
	private final ModelPart pelvis;
	private final ModelPart torso;
	private final ModelPart chest;
	private final ModelPart neck;
	private final ModelPart head;
	private final ModelPart jaw;
	private final ModelPart arm_left_upper;
	private final ModelPart arm_left_lower;
	private final ModelPart arm_right_upper;
	private final ModelPart arm_right_lower;
	private final ModelPart tail_base;
	private final ModelPart tail_mid;
	private final ModelPart tail_tip;
	private final ModelPart leg_left_upper;
	private final ModelPart leg_left_lower;
	private final ModelPart foot_left;
	private final ModelPart leg_right_upper;
	private final ModelPart leg_right_lower;
	private final ModelPart foot_right;
	private final KeyframeAnimation idleAnimation;
	private final KeyframeAnimation walkAnimation;
	private final KeyframeAnimation roarAnimation;
	private final KeyframeAnimation biteAnimation;

	public TyrannosaurusRexModel(ModelPart root) {
		super(root);
		this.pelvis = this.root.getChild("pelvis");
		this.torso = this.pelvis.getChild("torso");
		this.chest = this.torso.getChild("chest");
		this.neck = this.chest.getChild("neck");
		this.head = this.neck.getChild("head");
		this.jaw = this.head.getChild("jaw");
		this.arm_left_upper = this.chest.getChild("arm_left_upper");
		this.arm_left_lower = this.arm_left_upper.getChild("arm_left_lower");
		this.arm_right_upper = this.chest.getChild("arm_right_upper");
		this.arm_right_lower = this.arm_right_upper.getChild("arm_right_lower");
		this.tail_base = this.pelvis.getChild("tail_base");
		this.tail_mid = this.tail_base.getChild("tail_mid");
		this.tail_tip = this.tail_mid.getChild("tail_tip");
		this.leg_left_upper = this.pelvis.getChild("leg_left_upper");
		this.leg_left_lower = this.leg_left_upper.getChild("leg_left_lower");
		this.foot_left = this.leg_left_lower.getChild("foot_left");
		this.leg_right_upper = this.pelvis.getChild("leg_right_upper");
		this.leg_right_lower = this.leg_right_upper.getChild("leg_right_lower");
		this.foot_right = this.leg_right_lower.getChild("foot_right");
		this.idleAnimation = TyrannosaurusRexAnimation.IDLE.bake(root);
		this.walkAnimation = TyrannosaurusRexAnimation.WALK.bake(root);
		this.roarAnimation = TyrannosaurusRexAnimation.ROAR.bake(root);
		this.biteAnimation = TyrannosaurusRexAnimation.BITE.bake(root);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition pelvis = partdefinition.addOrReplaceChild("pelvis", CubeListBuilder.create().texOffs(2, 2).addBox(-6.5F, -7.0F, -5.0F, 13.0F, 10.0F, 11.0F, new CubeDeformation(0.0F))
			.texOffs(245, 25).addBox(-5.5F, -9.0F, -4.0F, 11.0F, 4.0F, 9.0F, new CubeDeformation(0.0F))
			.texOffs(287, 25).addBox(-5.5F, 0.0F, -4.0F, 11.0F, 5.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 6.0F, 4.0F));

		PartDefinition torso = pelvis.addOrReplaceChild("torso", CubeListBuilder.create(), PartPose.offset(0.0F, -2.0F, 0.0F));

		PartDefinition lower_belly_r1 = torso.addOrReplaceChild("lower_belly_r1", CubeListBuilder.create().texOffs(327, 25).addBox(-5.0F, 2.0F, -10.0F, 10.0F, 5.0F, 8.0F, new CubeDeformation(0.0F))
			.texOffs(88, 25).addBox(-4.5F, -8.0F, -11.0F, 9.0F, 4.0F, 10.0F, new CubeDeformation(0.0F))
			.texOffs(52, 2).addBox(-6.0F, -5.0F, -11.0F, 12.0F, 10.0F, 11.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0698F, 0.0F, 0.0F));

		PartDefinition chest = torso.addOrReplaceChild("chest", CubeListBuilder.create(), PartPose.offset(0.0F, -3.0F, -10.0F));

		PartDefinition back_saddle_r1 = chest.addOrReplaceChild("back_saddle_r1", CubeListBuilder.create().texOffs(154, 43).addBox(-5.0F, -6.0F, -6.0F, 10.0F, 3.0F, 7.0F, new CubeDeformation(0.0F))
			.texOffs(365, 25).addBox(-5.5F, 3.0F, -6.0F, 11.0F, 6.0F, 7.0F, new CubeDeformation(0.0F))
			.texOffs(128, 25).addBox(-7.5F, -5.0F, -6.0F, 15.0F, 8.0F, 6.0F, new CubeDeformation(0.0F))
			.texOffs(148, 2).addBox(-7.0F, -4.0F, -7.0F, 14.0F, 11.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.0873F, 0.0F, 0.0F));

		PartDefinition neck = chest.addOrReplaceChild("neck", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -2.0F, -6.0F, 0.1396F, 0.0F, 0.0F));

		PartDefinition neck_throat_r1 = neck.addOrReplaceChild("neck_throat_r1", CubeListBuilder.create().texOffs(403, 25).addBox(-4.5F, 2.0F, -7.0F, 9.0F, 6.0F, 7.0F, new CubeDeformation(0.0F))
			.texOffs(22, 25).addBox(-5.0F, -4.0F, -8.0F, 10.0F, 9.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1396F, 0.0F, 0.0F));

		PartDefinition neck_base_r1 = neck.addOrReplaceChild("neck_base_r1", CubeListBuilder.create().texOffs(408, 2).addBox(-5.5F, -4.0F, -5.0F, 11.0F, 10.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.2094F, 0.0F, 0.0F));

		PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(212, 25).addBox(-4.73F, -4.5F, -4.8F, 9.46F, 8.1F, 5.6F, new CubeDeformation(0.0F))
			.texOffs(319, 43).addBox(-5.16F, -4.5F, -5.6F, 10.32F, 4.5F, 4.0F, new CubeDeformation(0.0F))
			.texOffs(2, 43).addBox(-4.3F, -1.8F, -10.4F, 8.6F, 5.4F, 6.4F, new CubeDeformation(0.0F))
			.texOffs(446, 43).addBox(-3.87F, -0.9F, -12.8F, 7.74F, 4.5F, 3.2F, new CubeDeformation(0.0F))
			.texOffs(475, 25).addBox(-5.16F, -0.9F, -6.4F, 1.892F, 6.3F, 5.6F, new CubeDeformation(0.0F))
			.texOffs(492, 25).addBox(3.268F, -0.9F, -6.4F, 1.892F, 6.3F, 5.6F, new CubeDeformation(0.0F))
			.texOffs(90, 43).addBox(-3.01F, -2.7F, -12.0F, 6.02F, 1.8F, 8.8F, new CubeDeformation(0.0F))
			.texOffs(37, 57).addBox(-5.289F, -1.44F, -4.32F, 0.258F, 1.44F, 1.28F, new CubeDeformation(0.0F))
			.texOffs(43, 57).addBox(5.031F, -1.44F, -4.32F, 0.258F, 1.44F, 1.28F, new CubeDeformation(0.0F))
			.texOffs(111, 57).addBox(-5.375F, -1.215F, -3.96F, 0.1118F, 0.99F, 0.4F, new CubeDeformation(0.0F))
			.texOffs(115, 57).addBox(5.2632F, -1.215F, -3.96F, 0.1118F, 0.99F, 0.4F, new CubeDeformation(0.0F))
			.texOffs(99, 57).addBox(2.838F, 3.24F, -10.8F, 0.774F, 1.62F, 0.8F, new CubeDeformation(0.0F))
			.texOffs(25, 57).addBox(1.892F, 3.15F, -12.0F, 0.774F, 2.07F, 0.8F, new CubeDeformation(0.0F))
			.texOffs(13, 57).addBox(0.774F, 3.15F, -12.16F, 0.774F, 2.16F, 0.8F, new CubeDeformation(0.0F))
			.texOffs(49, 57).addBox(0.0F, 3.15F, -12.24F, 0.516F, 1.89F, 0.8F, new CubeDeformation(0.0F))
			.texOffs(105, 57).addBox(-3.612F, 3.24F, -10.8F, 0.774F, 1.62F, 0.8F, new CubeDeformation(0.0F))
			.texOffs(31, 57).addBox(-2.666F, 3.15F, -12.0F, 0.774F, 2.07F, 0.8F, new CubeDeformation(0.0F))
			.texOffs(19, 57).addBox(-1.548F, 3.15F, -12.16F, 0.774F, 2.16F, 0.8F, new CubeDeformation(0.0F))
			.texOffs(54, 57).addBox(-0.516F, 3.15F, -12.24F, 0.516F, 1.89F, 0.8F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.0F, -6.0F));

		PartDefinition jaw = head.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(437, 25).addBox(-4.042F, 0.9F, -8.8F, 8.084F, 3.15F, 9.6F, new CubeDeformation(0.0F))
			.texOffs(122, 43).addBox(-4.558F, 0.0F, -2.4F, 9.116F, 4.5F, 5.6F, new CubeDeformation(0.0F))
			.texOffs(224, 43).addBox(-3.44F, 2.7F, -7.2F, 6.88F, 2.25F, 7.2F, new CubeDeformation(0.0F))
			.texOffs(89, 57).addBox(2.752F, 0.0F, -7.04F, 0.688F, 1.62F, 0.8F, new CubeDeformation(0.0F))
			.texOffs(69, 57).addBox(1.806F, -0.18F, -8.0F, 0.688F, 1.8F, 0.8F, new CubeDeformation(0.0F))
			.texOffs(59, 57).addBox(0.774F, -0.27F, -8.4F, 0.688F, 1.89F, 0.8F, new CubeDeformation(0.0F))
			.texOffs(79, 57).addBox(0.0F, -0.09F, -8.56F, 0.516F, 1.71F, 0.8F, new CubeDeformation(0.0F))
			.texOffs(94, 57).addBox(-3.44F, 0.0F, -7.04F, 0.688F, 1.62F, 0.8F, new CubeDeformation(0.0F))
			.texOffs(74, 57).addBox(-2.494F, -0.18F, -8.0F, 0.688F, 1.8F, 0.8F, new CubeDeformation(0.0F))
			.texOffs(64, 57).addBox(-1.462F, -0.27F, -8.4F, 0.688F, 1.89F, 0.8F, new CubeDeformation(0.0F))
			.texOffs(84, 57).addBox(-0.516F, -0.09F, -8.56F, 0.516F, 1.71F, 0.8F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 2.7F, -3.2F));

		PartDefinition arm_left_upper = chest.addOrReplaceChild("arm_left_upper", CubeListBuilder.create(), PartPose.offsetAndRotation(-6.5F, -1.0F, -4.0F, 0.0F, 0.0F, -0.1396F));

		PartDefinition left_upper_arm_r1 = arm_left_upper.addOrReplaceChild("left_upper_arm_r1", CubeListBuilder.create().texOffs(293, 43).addBox(-1.3F, 0.0F, -2.0F, 2.2F, 6.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.3142F, 0.0F, -0.1396F));

		PartDefinition arm_left_lower = arm_left_upper.addOrReplaceChild("arm_left_lower", CubeListBuilder.create(), PartPose.offset(-0.7F, 5.0F, -1.0F));

		PartDefinition left_finger_two_r1 = arm_left_lower.addOrReplaceChild("left_finger_two_r1", CubeListBuilder.create().texOffs(494, 43).addBox(-1.0F, 3.0F, -5.5F, 0.8F, 2.0F, 3.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1222F, 0.0F, 0.0F));

		PartDefinition left_finger_one_r1 = arm_left_lower.addOrReplaceChild("left_finger_one_r1", CubeListBuilder.create().texOffs(470, 43).addBox(0.1F, 3.0F, -6.0F, 0.8F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.2094F, 0.0F, 0.0F));

		PartDefinition left_forearm_r1 = arm_left_lower.addOrReplaceChild("left_forearm_r1", CubeListBuilder.create().texOffs(422, 43).addBox(-1.0F, -1.0F, -3.0F, 1.8F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.5585F, 0.0F, 0.0F));

		PartDefinition arm_right_upper = chest.addOrReplaceChild("arm_right_upper", CubeListBuilder.create(), PartPose.offsetAndRotation(6.5F, -1.0F, -4.0F, 0.0F, 0.0F, 0.1396F));

		PartDefinition right_upper_arm_r1 = arm_right_upper.addOrReplaceChild("right_upper_arm_r1", CubeListBuilder.create().texOffs(306, 43).addBox(-0.9F, 0.0F, -2.0F, 2.2F, 6.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.3142F, 0.0F, 0.1396F));

		PartDefinition arm_right_lower = arm_right_upper.addOrReplaceChild("arm_right_lower", CubeListBuilder.create(), PartPose.offset(0.7F, 5.0F, -1.0F));

		PartDefinition right_finger_two_r1 = arm_right_lower.addOrReplaceChild("right_finger_two_r1", CubeListBuilder.create().texOffs(2, 57).addBox(0.2F, 3.0F, -5.5F, 0.8F, 2.0F, 3.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1222F, 0.0F, 0.0F));

		PartDefinition right_finger_one_r1 = arm_right_lower.addOrReplaceChild("right_finger_one_r1", CubeListBuilder.create().texOffs(482, 43).addBox(-0.9F, 3.0F, -6.0F, 0.8F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.2094F, 0.0F, 0.0F));

		PartDefinition right_forearm_r1 = arm_right_lower.addOrReplaceChild("right_forearm_r1", CubeListBuilder.create().texOffs(434, 43).addBox(-0.8F, -1.0F, -3.0F, 1.8F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.5585F, 0.0F, 0.0F));

		PartDefinition tail_base = pelvis.addOrReplaceChild("tail_base", CubeListBuilder.create(), PartPose.offset(0.0F, -2.0F, 6.0F));

		PartDefinition tail_base_top_r1 = tail_base.addOrReplaceChild("tail_base_top_r1", CubeListBuilder.create().texOffs(172, 25).addBox(-4.5F, -6.0F, -1.0F, 9.0F, 4.0F, 10.0F, new CubeDeformation(0.0F))
			.texOffs(100, 2).addBox(-5.5F, -4.0F, -2.0F, 11.0F, 9.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.0698F, 0.0F, 0.0F));

		PartDefinition tail_mid = tail_base.addOrReplaceChild("tail_mid", CubeListBuilder.create(), PartPose.offset(0.0F, 1.0F, 11.0F));

		PartDefinition tail_mid_taper_r1 = tail_mid.addOrReplaceChild("tail_mid_taper_r1", CubeListBuilder.create().texOffs(446, 2).addBox(-3.4F, -1.8F, 8.0F, 6.8F, 5.6F, 11.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.1047F, 0.0F, 0.0F));

		PartDefinition tail_mid_mass_r1 = tail_mid.addOrReplaceChild("tail_mid_mass_r1", CubeListBuilder.create().texOffs(196, 2).addBox(-4.2F, -3.5F, -3.0F, 8.4F, 7.0F, 13.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.0873F, 0.0F, 0.0F));

		PartDefinition tail_tip = tail_mid.addOrReplaceChild("tail_tip", CubeListBuilder.create(), PartPose.offset(0.0F, 1.0F, 11.0F));

		PartDefinition tail_tip_end_r1 = tail_tip.addOrReplaceChild("tail_tip_end_r1", CubeListBuilder.create().texOffs(56, 25).addBox(-1.8F, 0.2F, 17.0F, 3.6F, 3.6F, 11.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.1745F, 0.0F, 0.0F));

		PartDefinition tail_tip_long_r1 = tail_tip.addOrReplaceChild("tail_tip_long_r1", CubeListBuilder.create().texOffs(369, 2).addBox(-2.6F, -1.5F, 6.0F, 5.2F, 4.5F, 13.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.1396F, 0.0F, 0.0F));

		PartDefinition leg_left_upper = pelvis.addOrReplaceChild("leg_left_upper", CubeListBuilder.create(), PartPose.offset(-5.0F, 0.0F, 0.0F));

		PartDefinition left_thigh_muscle_r1 = leg_left_upper.addOrReplaceChild("left_thigh_muscle_r1", CubeListBuilder.create().texOffs(301, 2).addBox(-3.5F, -4.0F, -4.5F, 7.0F, 9.0F, 9.0F, new CubeDeformation(0.0F))
			.texOffs(241, 2).addBox(-3.0F, -2.0F, -4.0F, 6.0F, 12.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1396F, 0.0F, 0.0F));

		PartDefinition leg_left_lower = leg_left_upper.addOrReplaceChild("leg_left_lower", CubeListBuilder.create(), PartPose.offset(0.0F, 8.0F, -1.0F));

		PartDefinition left_ankle_r1 = leg_left_lower.addOrReplaceChild("left_ankle_r1", CubeListBuilder.create().texOffs(190, 43).addBox(-1.6F, 3.0F, -3.0F, 3.2F, 6.0F, 4.0F, new CubeDeformation(0.0F))
			.texOffs(484, 2).addBox(-2.0F, -2.0F, -3.5F, 4.0F, 10.5F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2443F, 0.0F, 0.0F));

		PartDefinition foot_left = leg_left_lower.addOrReplaceChild("foot_left", CubeListBuilder.create().texOffs(34, 43).addBox(-2.5F, -0.5F, -6.0F, 5.0F, 3.5F, 8.0F, new CubeDeformation(0.0F))
			.texOffs(255, 43).addBox(-0.8F, 0.8F, -12.0F, 1.5F, 2.2F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 7.0F, -1.0F));

		PartDefinition left_toe_outer_r1 = foot_left.addOrReplaceChild("left_toe_outer_r1", CubeListBuilder.create().texOffs(368, 43).addBox(-2.5F, 1.0F, -11.0F, 1.7F, 2.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.1396F, 0.0F));

		PartDefinition left_toe_inner_r1 = foot_left.addOrReplaceChild("left_toe_inner_r1", CubeListBuilder.create().texOffs(350, 43).addBox(0.8F, 1.0F, -11.0F, 1.7F, 2.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.1396F, 0.0F));

		PartDefinition leg_right_upper = pelvis.addOrReplaceChild("leg_right_upper", CubeListBuilder.create(), PartPose.offset(5.0F, 0.0F, 0.0F));

		PartDefinition right_thigh_muscle_r1 = leg_right_upper.addOrReplaceChild("right_thigh_muscle_r1", CubeListBuilder.create().texOffs(335, 2).addBox(-3.5F, -4.0F, -4.5F, 7.0F, 9.0F, 9.0F, new CubeDeformation(0.0F))
			.texOffs(271, 2).addBox(-3.0F, -2.0F, -4.0F, 6.0F, 12.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1396F, 0.0F, 0.0F));

		PartDefinition leg_right_lower = leg_right_upper.addOrReplaceChild("leg_right_lower", CubeListBuilder.create(), PartPose.offset(0.0F, 8.0F, -1.0F));

		PartDefinition right_ankle_r1 = leg_right_lower.addOrReplaceChild("right_ankle_r1", CubeListBuilder.create().texOffs(207, 43).addBox(-1.6F, 3.0F, -3.0F, 3.2F, 6.0F, 4.0F, new CubeDeformation(0.0F))
			.texOffs(2, 25).addBox(-2.0F, -2.0F, -3.5F, 4.0F, 10.5F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2443F, 0.0F, 0.0F));

		PartDefinition foot_right = leg_right_lower.addOrReplaceChild("foot_right", CubeListBuilder.create().texOffs(62, 43).addBox(-2.5F, -0.5F, -6.0F, 5.0F, 3.5F, 8.0F, new CubeDeformation(0.0F))
			.texOffs(274, 43).addBox(-0.7F, 0.8F, -12.0F, 1.5F, 2.2F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 7.0F, -1.0F));

		PartDefinition right_toe_outer_r1 = foot_right.addOrReplaceChild("right_toe_outer_r1", CubeListBuilder.create().texOffs(404, 43).addBox(0.8F, 1.0F, -11.0F, 1.7F, 2.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.1396F, 0.0F));

		PartDefinition right_toe_inner_r1 = foot_right.addOrReplaceChild("right_toe_inner_r1", CubeListBuilder.create().texOffs(386, 43).addBox(-2.5F, 1.0F, -11.0F, 1.7F, 2.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.1396F, 0.0F));

		return LayerDefinition.create(meshdefinition, 512, 512);
	}

	@Override
	public void setupAnim(TyrannosaurusRexRenderState state) {
		super.setupAnim(state);
		this.idleAnimation.apply(state.idleAnimationState, state.ageInTicks);
		this.walkAnimation.applyWalk(state.walkAnimationPos, state.walkAnimationSpeed, state.isRunning ? 2.2F : 1.4F, 2.5F);
		this.roarAnimation.apply(state.roarAnimationState, state.ageInTicks);
		this.biteAnimation.apply(state.biteAnimationState, state.ageInTicks);
		if (!state.roarAnimationState.isStarted() && !state.biteAnimationState.isStarted()) {
			this.head.yRot += Mth.clamp(state.yRot, -28.0F, 28.0F) * Mth.DEG_TO_RAD;
			this.head.xRot += Mth.clamp(state.xRot, -20.0F, 30.0F) * Mth.DEG_TO_RAD;
		}
	}
}
