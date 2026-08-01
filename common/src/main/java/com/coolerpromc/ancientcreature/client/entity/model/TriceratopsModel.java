package com.coolerpromc.ancientcreature.client.entity.model;

import com.coolerpromc.ancientcreature.client.entity.animation.TriceratopsAnimation;
import com.coolerpromc.ancientcreature.client.entity.state.TriceratopsRenderState;
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

public class TriceratopsModel extends EntityModel<TriceratopsRenderState> {
	private final ModelPart body;
	private final ModelPart chest;
	private final ModelPart neck;
	private final ModelPart head;
	private final ModelPart jaw;
	private final ModelPart frill;
	private final ModelPart foreleg_left;
	private final ModelPart foreleg_lower_left;
	private final ModelPart forefoot_left;
	private final ModelPart foreleg_right;
	private final ModelPart foreleg_lower_right;
	private final ModelPart forefoot_right;
	private final ModelPart tail_1;
	private final ModelPart tail_2;
	private final ModelPart tail_3;
	private final ModelPart hindleg_left;
	private final ModelPart hindleg_lower_left;
	private final ModelPart hindfoot_left;
	private final ModelPart hindleg_right;
	private final ModelPart hindleg_lower_right;
	private final ModelPart hindfoot_right;
	private final KeyframeAnimation idleAnimation;
	private final KeyframeAnimation walkAnimation;
	private final KeyframeAnimation chargeAnimation;
	private final KeyframeAnimation grazeAnimation;
	private final KeyframeAnimation bellowAnimation;

	public TriceratopsModel(ModelPart root) {
		super(root.getChild("root"));
		this.body = this.root.getChild("body");
		this.chest = this.body.getChild("chest");
		this.neck = this.chest.getChild("neck");
		this.head = this.neck.getChild("head");
		this.jaw = this.head.getChild("jaw");
		this.frill = this.head.getChild("frill");
		this.foreleg_left = this.chest.getChild("foreleg_left");
		this.foreleg_lower_left = this.foreleg_left.getChild("foreleg_lower_left");
		this.forefoot_left = this.foreleg_lower_left.getChild("forefoot_left");
		this.foreleg_right = this.chest.getChild("foreleg_right");
		this.foreleg_lower_right = this.foreleg_right.getChild("foreleg_lower_right");
		this.forefoot_right = this.foreleg_lower_right.getChild("forefoot_right");
		this.tail_1 = this.body.getChild("tail_1");
		this.tail_2 = this.tail_1.getChild("tail_2");
		this.tail_3 = this.tail_2.getChild("tail_3");
		this.hindleg_left = this.body.getChild("hindleg_left");
		this.hindleg_lower_left = this.hindleg_left.getChild("hindleg_lower_left");
		this.hindfoot_left = this.hindleg_lower_left.getChild("hindfoot_left");
		this.hindleg_right = this.body.getChild("hindleg_right");
		this.hindleg_lower_right = this.hindleg_right.getChild("hindleg_lower_right");
		this.hindfoot_right = this.hindleg_lower_right.getChild("hindfoot_right");
		this.idleAnimation = TriceratopsAnimation.IDLE.bake(root);
		this.walkAnimation = TriceratopsAnimation.WALK.bake(root);
		this.chargeAnimation = TriceratopsAnimation.CHARGE.bake(root);
		this.grazeAnimation = TriceratopsAnimation.GRAZE.bake(root);
		this.bellowAnimation = TriceratopsAnimation.BELLOW.bake(root);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(2, 2).addBox(-8.0F, -8.0F, -7.0F, 16.0F, 13.0F, 16.0F, new CubeDeformation(0.0F))
		.texOffs(69, 2).addBox(-7.0F, 1.0F, -6.0F, 14.0F, 5.5F, 14.0F, new CubeDeformation(0.0F))
		.texOffs(128, 2).addBox(-7.5F, -7.0F, 5.0F, 15.0F, 11.0F, 9.0F, new CubeDeformation(0.0F))
		.texOffs(179, 2).addBox(-6.0F, -10.0F, -6.0F, 12.0F, 4.0F, 17.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -18.0F, 2.0F));

		PartDefinition chest = body.addOrReplaceChild("chest", CubeListBuilder.create().texOffs(350, 2).addBox(-6.5F, 2.0F, -3.0F, 13.0F, 7.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.0F, -12.0F));

		PartDefinition shoulder_ridge_r1 = chest.addOrReplaceChild("shoulder_ridge_r1", CubeListBuilder.create().texOffs(301, 2).addBox(-7.0F, -9.0F, -3.0F, 14.0F, 4.0F, 9.0F, new CubeDeformation(0.0F))
		.texOffs(240, 2).addBox(-9.0F, -7.0F, -4.0F, 18.0F, 13.0F, 11.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.0349F, 0.0F, 0.0F));

		PartDefinition neck = chest.addOrReplaceChild("neck", CubeListBuilder.create(), PartPose.offset(0.0F, -2.0F, -7.0F));

		PartDefinition neck_throat_r1 = neck.addOrReplaceChild("neck_throat_r1", CubeListBuilder.create().texOffs(442, 2).addBox(-5.5F, 3.0F, -3.0F, 11.0F, 6.0F, 7.0F, new CubeDeformation(0.0F))
		.texOffs(397, 2).addBox(-6.5F, -5.0F, -3.0F, 13.0F, 12.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1396F, 0.0F, 0.0F));

		PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(2, 34).addBox(-6.5F, -5.0F, -6.0F, 13.0F, 10.0F, 9.0F, new CubeDeformation(0.0F))
		.texOffs(49, 34).addBox(-7.0F, -6.0F, -7.0F, 14.0F, 6.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(99, 56).addBox(6.35F, -2.6F, -7.1F, 1.1F, 4.7F, 3.7F, new CubeDeformation(0.0F))
		.texOffs(114, 56).addBox(6.65F, -1.6F, -6.3F, 1.05F, 2.6F, 2.3F, new CubeDeformation(0.0F))
		.texOffs(127, 56).addBox(7.7F, -1.15F, -5.85F, 0.16F, 1.7F, 1.4F, new CubeDeformation(0.0F))
		.texOffs(136, 56).addBox(6.0F, 0.0F, -5.0F, 2.0F, 6.0F, 7.0F, new CubeDeformation(0.0F))
		.texOffs(157, 56).addBox(-7.45F, -2.6F, -7.1F, 1.1F, 4.7F, 3.7F, new CubeDeformation(0.0F))
		.texOffs(172, 56).addBox(-7.7F, -1.6F, -6.3F, 1.05F, 2.6F, 2.3F, new CubeDeformation(0.0F))
		.texOffs(185, 56).addBox(-7.86F, -1.15F, -5.85F, 0.16F, 1.7F, 1.4F, new CubeDeformation(0.0F))
		.texOffs(194, 56).addBox(-8.0F, 0.0F, -5.0F, 2.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.0F, -5.0F));

		PartDefinition nasal_horn_tip_r1 = head.addOrReplaceChild("nasal_horn_tip_r1", CubeListBuilder.create().texOffs(82, 56).addBox(-0.65F, -4.0F, -5.0F, 1.3F, 3.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.0F, -14.0F, 0.5236F, 0.0F, 0.0F));

		PartDefinition nasal_horn_base_r1 = head.addOrReplaceChild("nasal_horn_base_r1", CubeListBuilder.create().texOffs(65, 56).addBox(-1.5F, -4.0F, -3.0F, 3.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, -12.0F, 0.384F, 0.0F, 0.0F));

		PartDefinition brow_horn_left_tip_r1 = head.addOrReplaceChild("brow_horn_left_tip_r1", CubeListBuilder.create().texOffs(44, 56).addBox(-0.55F, -3.0F, -7.0F, 1.1F, 2.5F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.1F, -7.0F, -15.0F, 0.733F, 0.0F, 0.1047F));

		PartDefinition brow_horn_left_mid_r1 = head.addOrReplaceChild("brow_horn_left_mid_r1", CubeListBuilder.create().texOffs(21, 56).addBox(-1.05F, -4.5F, -7.0F, 2.1F, 4.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.1F, -5.0F, -9.0F, 0.6109F, 0.0F, 0.0873F));

		PartDefinition brow_horn_left_base_r1 = head.addOrReplaceChild("brow_horn_left_base_r1", CubeListBuilder.create().texOffs(2, 56).addBox(-1.5F, -4.0F, -3.0F, 3.0F, 5.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.1F, -4.0F, -7.0F, 0.4887F, 0.0F, 0.0698F));

		PartDefinition brow_horn_right_tip_r1 = head.addOrReplaceChild("brow_horn_right_tip_r1", CubeListBuilder.create().texOffs(481, 34).addBox(-0.55F, -3.0F, -7.0F, 1.1F, 2.5F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.1F, -7.0F, -15.0F, 0.733F, 0.0F, -0.1047F));

		PartDefinition brow_horn_right_mid_r1 = head.addOrReplaceChild("brow_horn_right_mid_r1", CubeListBuilder.create().texOffs(458, 34).addBox(-1.05F, -4.5F, -7.0F, 2.1F, 4.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.1F, -5.0F, -9.0F, 0.6109F, 0.0F, -0.0873F));

		PartDefinition brow_horn_right_base_r1 = head.addOrReplaceChild("brow_horn_right_base_r1", CubeListBuilder.create().texOffs(439, 34).addBox(-1.5F, -4.0F, -3.0F, 3.0F, 5.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.1F, -4.0F, -7.0F, 0.4887F, 0.0F, -0.0698F));

		PartDefinition nasal_bridge_r1 = head.addOrReplaceChild("nasal_bridge_r1", CubeListBuilder.create().texOffs(168, 34).addBox(-3.5F, -3.0F, -14.0F, 7.0F, 4.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(96, 34).addBox(-5.5F, -1.0F, -13.0F, 11.0F, 6.5F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.0698F, 0.0F, 0.0F));

		PartDefinition upper_beak_r1 = head.addOrReplaceChild("upper_beak_r1", CubeListBuilder.create().texOffs(137, 34).addBox(-4.5F, 1.0F, -17.0F, 9.0F, 5.5F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.1396F, 0.0F, 0.0F));

		PartDefinition jaw = head.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(201, 34).addBox(-5.2F, 1.0F, -9.0F, 10.4F, 4.5F, 9.0F, new CubeDeformation(0.0F))
		.texOffs(244, 34).addBox(-4.0F, 1.5F, -12.0F, 8.0F, 3.5F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 3.0F, -5.0F));

		PartDefinition frill = head.addOrReplaceChild("frill", CubeListBuilder.create(), PartPose.offset(0.0F, -3.0F, 3.0F));

		PartDefinition frill_lower_right_r1 = frill.addOrReplaceChild("frill_lower_right_r1", CubeListBuilder.create().texOffs(420, 34).addBox(7.5F, -1.0F, -1.0F, 4.5F, 6.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0873F, 0.0F, 0.3142F));

		PartDefinition frill_lower_left_r1 = frill.addOrReplaceChild("frill_lower_left_r1", CubeListBuilder.create().texOffs(401, 34).addBox(-12.0F, -1.0F, -1.0F, 4.5F, 6.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0873F, 0.0F, -0.3142F));

		PartDefinition frill_top_r1 = frill.addOrReplaceChild("frill_top_r1", CubeListBuilder.create().texOffs(364, 34).addBox(-7.5F, -15.0F, 0.0F, 15.0F, 5.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(271, 34).addBox(-9.0F, -12.0F, -1.0F, 18.0F, 15.0F, 3.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1222F, 0.0F, 0.0F));

		PartDefinition frill_right_r1 = frill.addOrReplaceChild("frill_right_r1", CubeListBuilder.create().texOffs(341, 34).addBox(7.0F, -9.0F, -0.5F, 7.0F, 12.0F, 2.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1222F, 0.0F, 0.2094F));

		PartDefinition frill_left_r1 = frill.addOrReplaceChild("frill_left_r1", CubeListBuilder.create().texOffs(318, 34).addBox(-14.0F, -9.0F, -0.5F, 7.0F, 12.0F, 2.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1222F, 0.0F, -0.2094F));

		PartDefinition foreleg_left = chest.addOrReplaceChild("foreleg_left", CubeListBuilder.create(), PartPose.offset(-6.0F, 2.0F, 0.0F));

		PartDefinition fore_upper_left_r1 = foreleg_left.addOrReplaceChild("fore_upper_left_r1", CubeListBuilder.create().texOffs(155, 81).addBox(-3.0F, -2.0F, -3.0F, 6.0F, 12.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.0698F, 0.0F, -0.0349F));

		PartDefinition foreleg_lower_left = foreleg_left.addOrReplaceChild("foreleg_lower_left", CubeListBuilder.create(), PartPose.offset(0.0F, 9.0F, 0.0F));

		PartDefinition fore_lower_left_r1 = foreleg_lower_left.addOrReplaceChild("fore_lower_left_r1", CubeListBuilder.create().texOffs(182, 81).addBox(-2.3F, -2.0F, -2.5F, 4.6F, 9.5F, 4.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0524F, 0.0F, 0.0349F));

		PartDefinition forefoot_left = foreleg_lower_left.addOrReplaceChild("forefoot_left", CubeListBuilder.create().texOffs(205, 81).addBox(-3.2F, -1.5F, -5.0F, 6.4F, 3.5F, 8.5F, new CubeDeformation(0.0F))
		.texOffs(255, 81).addBox(-0.7F, 0.2F, -7.0F, 1.4F, 1.8F, 3.5F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 7.0F, -1.0F));

		PartDefinition fore_toe_left_3_r1 = forefoot_left.addOrReplaceChild("fore_toe_left_3_r1", CubeListBuilder.create().texOffs(270, 81).addBox(-2.7F, 0.2F, -7.0F, 1.4F, 1.8F, 3.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.1047F, 0.0F));

		PartDefinition fore_toe_left_1_r1 = forefoot_left.addOrReplaceChild("fore_toe_left_1_r1", CubeListBuilder.create().texOffs(240, 81).addBox(1.3F, 0.2F, -7.0F, 1.4F, 1.8F, 3.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.1047F, 0.0F));

		PartDefinition foreleg_right = chest.addOrReplaceChild("foreleg_right", CubeListBuilder.create(), PartPose.offset(6.0F, 2.0F, 0.0F));

		PartDefinition fore_upper_right_r1 = foreleg_right.addOrReplaceChild("fore_upper_right_r1", CubeListBuilder.create().texOffs(393, 56).addBox(-3.0F, -2.0F, -3.0F, 6.0F, 12.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.0698F, 0.0F, 0.0349F));

		PartDefinition foreleg_lower_right = foreleg_right.addOrReplaceChild("foreleg_lower_right", CubeListBuilder.create(), PartPose.offset(0.0F, 9.0F, 0.0F));

		PartDefinition fore_lower_right_r1 = foreleg_lower_right.addOrReplaceChild("fore_lower_right_r1", CubeListBuilder.create().texOffs(420, 56).addBox(-2.3F, -2.0F, -2.5F, 4.6F, 9.5F, 4.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0524F, 0.0F, -0.0349F));

		PartDefinition forefoot_right = foreleg_lower_right.addOrReplaceChild("forefoot_right", CubeListBuilder.create().texOffs(443, 56).addBox(-3.2F, -1.5F, -5.0F, 6.4F, 3.5F, 8.5F, new CubeDeformation(0.0F))
		.texOffs(493, 56).addBox(-0.7F, 0.2F, -7.0F, 1.4F, 1.8F, 3.5F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 7.0F, -1.0F));

		PartDefinition fore_toe_right_3_r1 = forefoot_right.addOrReplaceChild("fore_toe_right_3_r1", CubeListBuilder.create().texOffs(2, 81).addBox(-2.7F, 0.2F, -7.0F, 1.4F, 1.8F, 3.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.1047F, 0.0F));

		PartDefinition fore_toe_right_1_r1 = forefoot_right.addOrReplaceChild("fore_toe_right_1_r1", CubeListBuilder.create().texOffs(478, 56).addBox(1.3F, 0.2F, -7.0F, 1.4F, 1.8F, 3.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.1047F, 0.0F));

		PartDefinition tail_1 = body.addOrReplaceChild("tail_1", CubeListBuilder.create(), PartPose.offset(0.0F, -1.0F, 11.0F));

		PartDefinition tail_base_r1 = tail_1.addOrReplaceChild("tail_base_r1", CubeListBuilder.create().texOffs(215, 56).addBox(-5.5F, -5.0F, -1.0F, 11.0F, 9.0F, 13.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.0524F, 0.0F, 0.0F));

		PartDefinition tail_2 = tail_1.addOrReplaceChild("tail_2", CubeListBuilder.create(), PartPose.offset(0.0F, 1.0F, 12.0F));

		PartDefinition tail_mid_r1 = tail_2.addOrReplaceChild("tail_mid_r1", CubeListBuilder.create().texOffs(266, 56).addBox(-4.0F, -3.5F, -2.0F, 8.0F, 7.0F, 15.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.0873F, 0.0F, 0.0F));

		PartDefinition tail_3 = tail_2.addOrReplaceChild("tail_3", CubeListBuilder.create(), PartPose.offset(0.0F, 1.0F, 12.0F));

		PartDefinition tail_end_r1 = tail_3.addOrReplaceChild("tail_end_r1", CubeListBuilder.create().texOffs(360, 56).addBox(-1.3F, 0.5F, 12.0F, 2.6F, 3.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.1571F, 0.0F, 0.0F));

		PartDefinition tail_tip_r1 = tail_3.addOrReplaceChild("tail_tip_r1", CubeListBuilder.create().texOffs(315, 56).addBox(-2.6F, -1.5F, -1.0F, 5.2F, 4.5F, 15.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.1222F, 0.0F, 0.0F));

		PartDefinition hindleg_left = body.addOrReplaceChild("hindleg_left", CubeListBuilder.create(), PartPose.offset(-6.0F, 0.0F, 4.0F));

		PartDefinition hind_thigh_left_r1 = hindleg_left.addOrReplaceChild("hind_thigh_left_r1", CubeListBuilder.create().texOffs(285, 81).addBox(-3.8F, -5.0F, -3.0F, 7.6F, 15.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1222F, 0.0F, -0.0349F));

		PartDefinition hindleg_lower_left = hindleg_left.addOrReplaceChild("hindleg_lower_left", CubeListBuilder.create(), PartPose.offset(0.0F, 9.0F, 0.0F));

		PartDefinition hind_calf_left_r1 = hindleg_lower_left.addOrReplaceChild("hind_calf_left_r1", CubeListBuilder.create().texOffs(320, 81).addBox(-2.5F, -3.0F, -2.0F, 5.0F, 10.5F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.1047F, 0.0F, 0.0349F));

		PartDefinition hindfoot_left = hindleg_lower_left.addOrReplaceChild("hindfoot_left", CubeListBuilder.create().texOffs(343, 81).addBox(-3.5F, -1.8F, -4.0F, 7.0F, 3.8F, 9.0F, new CubeDeformation(0.0F))
		.texOffs(393, 81).addBox(-0.75F, 0.0F, -6.0F, 1.5F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 7.0F, -2.0F));

		PartDefinition hind_toe_left_3_r1 = hindfoot_left.addOrReplaceChild("hind_toe_left_3_r1", CubeListBuilder.create().texOffs(408, 81).addBox(-2.95F, 0.0F, -6.0F, 1.5F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.1222F, 0.0F));

		PartDefinition hind_toe_left_1_r1 = hindfoot_left.addOrReplaceChild("hind_toe_left_1_r1", CubeListBuilder.create().texOffs(378, 81).addBox(1.45F, 0.0F, -6.0F, 1.5F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.1222F, 0.0F));

		PartDefinition hindleg_right = body.addOrReplaceChild("hindleg_right", CubeListBuilder.create(), PartPose.offset(6.0F, 0.0F, 4.0F));

		PartDefinition hind_thigh_right_r1 = hindleg_right.addOrReplaceChild("hind_thigh_right_r1", CubeListBuilder.create().texOffs(17, 81).addBox(-3.8F, -5.0F, -3.0F, 7.6F, 15.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1222F, 0.0F, 0.0349F));

		PartDefinition hindleg_lower_right = hindleg_right.addOrReplaceChild("hindleg_lower_right", CubeListBuilder.create(), PartPose.offset(0.0F, 9.0F, 0.0F));

		PartDefinition hind_calf_right_r1 = hindleg_lower_right.addOrReplaceChild("hind_calf_right_r1", CubeListBuilder.create().texOffs(52, 81).addBox(-2.5F, -3.0F, -2.0F, 5.0F, 10.5F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.1047F, 0.0F, -0.0349F));

		PartDefinition hindfoot_right = hindleg_lower_right.addOrReplaceChild("hindfoot_right", CubeListBuilder.create().texOffs(75, 81).addBox(-3.5F, -1.8F, -4.0F, 7.0F, 3.8F, 9.0F, new CubeDeformation(0.0F))
		.texOffs(125, 81).addBox(-0.75F, 0.0F, -6.0F, 1.5F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 7.0F, -2.0F));

		PartDefinition hind_toe_right_3_r1 = hindfoot_right.addOrReplaceChild("hind_toe_right_3_r1", CubeListBuilder.create().texOffs(140, 81).addBox(-2.95F, 0.0F, -6.0F, 1.5F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.1222F, 0.0F));

		PartDefinition hind_toe_right_1_r1 = hindfoot_right.addOrReplaceChild("hind_toe_right_1_r1", CubeListBuilder.create().texOffs(110, 81).addBox(1.45F, 0.0F, -6.0F, 1.5F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.1222F, 0.0F));

		return LayerDefinition.create(meshdefinition, 512, 512);
	}

	@Override
	public void setupAnim(TriceratopsRenderState state) {
		super.setupAnim(state);
		this.idleAnimation.apply(state.idleAnimationState, state.ageInTicks);
		if (state.chargeAnimationState.isStarted()) {
			this.chargeAnimation.apply(state.chargeAnimationState, state.ageInTicks);
		} else {
			this.walkAnimation.applyWalk(state.walkAnimationPos, state.walkAnimationSpeed, 1.5F, 2.5F);
		}
		this.grazeAnimation.apply(state.grazeAnimationState, state.ageInTicks);
		this.bellowAnimation.apply(state.bellowAnimationState, state.ageInTicks);
		this.head.yRot += Mth.clamp(state.yRot, -30.0F, 30.0F) * Mth.DEG_TO_RAD;
		this.head.xRot += Mth.clamp(state.xRot, -25.0F, 35.0F) * Mth.DEG_TO_RAD;
	}
}
