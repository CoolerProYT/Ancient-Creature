package com.coolerpromc.ancientcreature.reference;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

/**
 * Frozen copies of the hand-written {@code createBodyLayer()} methods that defined Triceratops,
 * Tyrannosaurus Rex and Megalodon before they became data-driven.
 *
 * <p>The production classes are gone; these remain as test fixtures so
 * {@code BedrockModelBakerTest} can keep proving that the shipped {@code .geo.json} files bake into
 * exactly the same geometry. Without them, a future change to the Bedrock coordinate conversion could
 * silently deform every creature with nothing to catch it.
 *
 * <p>Do not "fix" anything here. It is a reference, not live code.
 */
public final class ReferenceModels {
    private ReferenceModels() {
    }

    public static LayerDefinition createTriceratopsLayer() {
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

    public static LayerDefinition createTyrannosaurusRexLayer() {
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

    public static LayerDefinition createMegalodonLayer() {
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
}
