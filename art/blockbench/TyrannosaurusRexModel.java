// Made with Blockbench 5.1.6
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


public class TyrannosaurusRexModel<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("ancientcreature", "tyrannosaurus_rex"), "main");
	private final ModelPart root;
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

	public TyrannosaurusRexModel(ModelPart root) {
		this.root = root.getChild("root");
		this.pelvis = root.getChild("pelvis");
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
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition pelvis = partdefinition.addOrReplaceChild("pelvis", CubeListBuilder.create().texOffs(0, 0).addBox(-6.5F, -7.0F, -5.0F, 13.0F, 10.0F, 11.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-5.5F, -9.0F, -4.0F, 11.0F, 4.0F, 9.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-5.5F, 0.0F, -4.0F, 11.0F, 5.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 6.0F, 4.0F));

		PartDefinition torso = pelvis.addOrReplaceChild("torso", CubeListBuilder.create(), PartPose.offset(0.0F, -2.0F, 0.0F));

		PartDefinition lower_belly_r1 = torso.addOrReplaceChild("lower_belly_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, 2.0F, -10.0F, 10.0F, 5.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-4.5F, -8.0F, -11.0F, 9.0F, 4.0F, 10.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-6.0F, -5.0F, -11.0F, 12.0F, 10.0F, 11.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0698F, 0.0F, 0.0F));

		PartDefinition chest = torso.addOrReplaceChild("chest", CubeListBuilder.create(), PartPose.offset(0.0F, -3.0F, -10.0F));

		PartDefinition back_saddle_r1 = chest.addOrReplaceChild("back_saddle_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -6.0F, -6.0F, 10.0F, 3.0F, 7.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-5.5F, 3.0F, -6.0F, 11.0F, 6.0F, 7.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-7.5F, -5.0F, -6.0F, 15.0F, 8.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-7.0F, -4.0F, -7.0F, 14.0F, 11.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.0873F, 0.0F, 0.0F));

		PartDefinition neck = chest.addOrReplaceChild("neck", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -2.0F, -6.0F, 0.1396F, 0.0F, 0.0F));

		PartDefinition neck_throat_r1 = neck.addOrReplaceChild("neck_throat_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-4.5F, 2.0F, -7.0F, 9.0F, 6.0F, 7.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-5.0F, -4.0F, -8.0F, 10.0F, 9.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1396F, 0.0F, 0.0F));

		PartDefinition neck_base_r1 = neck.addOrReplaceChild("neck_base_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-5.5F, -4.0F, -5.0F, 11.0F, 10.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.2094F, 0.0F, 0.0F));

		PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-5.5F, -5.0F, -6.0F, 11.0F, 9.0F, 7.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-6.0F, -5.0F, -7.0F, 12.0F, 5.0F, 5.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-5.0F, -2.0F, -13.0F, 10.0F, 6.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-4.5F, -1.0F, -16.0F, 9.0F, 5.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-6.0F, -1.0F, -8.0F, 2.2F, 7.0F, 7.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(3.8F, -1.0F, -8.0F, 2.2F, 7.0F, 7.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-3.5F, -3.0F, -15.0F, 7.0F, 2.0F, 11.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-6.15F, -1.6F, -5.4F, 0.3F, 1.6F, 1.6F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(5.85F, -1.6F, -5.4F, 0.3F, 1.6F, 1.6F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-6.25F, -1.35F, -4.95F, 0.13F, 1.1F, 0.5F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(6.12F, -1.35F, -4.95F, 0.13F, 1.1F, 0.5F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(3.3F, 3.6F, -13.5F, 0.9F, 1.8F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(2.2F, 3.5F, -15.0F, 0.9F, 2.3F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(0.9F, 3.5F, -15.2F, 0.9F, 2.4F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(0.0F, 3.5F, -15.3F, 0.6F, 2.1F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-4.2F, 3.6F, -13.5F, 0.9F, 1.8F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-3.1F, 3.5F, -15.0F, 0.9F, 2.3F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-1.8F, 3.5F, -15.2F, 0.9F, 2.4F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-0.6F, 3.5F, -15.3F, 0.6F, 2.1F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.0F, -6.0F));

		PartDefinition jaw = head.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(0, 0).addBox(-4.7F, 1.0F, -11.0F, 9.4F, 3.5F, 12.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-5.3F, 0.0F, -3.0F, 10.6F, 5.0F, 7.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-4.0F, 3.0F, -9.0F, 8.0F, 2.5F, 9.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(3.2F, 0.0F, -8.8F, 0.8F, 1.8F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(2.1F, -0.2F, -10.0F, 0.8F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(0.9F, -0.3F, -10.5F, 0.8F, 2.1F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(0.0F, -0.1F, -10.7F, 0.6F, 1.9F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-4.0F, 0.0F, -8.8F, 0.8F, 1.8F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-2.9F, -0.2F, -10.0F, 0.8F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-1.7F, -0.3F, -10.5F, 0.8F, 2.1F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-0.6F, -0.1F, -10.7F, 0.6F, 1.9F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 3.0F, -4.0F));

		PartDefinition arm_left_upper = chest.addOrReplaceChild("arm_left_upper", CubeListBuilder.create(), PartPose.offsetAndRotation(-6.5F, -1.0F, -4.0F, 0.0F, 0.0F, -0.1396F));

		PartDefinition left_upper_arm_r1 = arm_left_upper.addOrReplaceChild("left_upper_arm_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-1.3F, 0.0F, -2.0F, 2.2F, 6.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.3142F, 0.0F, -0.1396F));

		PartDefinition arm_left_lower = arm_left_upper.addOrReplaceChild("arm_left_lower", CubeListBuilder.create(), PartPose.offset(-0.7F, 5.0F, -1.0F));

		PartDefinition left_finger_two_r1 = arm_left_lower.addOrReplaceChild("left_finger_two_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, 3.0F, -5.5F, 0.8F, 2.0F, 3.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1222F, 0.0F, 0.0F));

		PartDefinition left_finger_one_r1 = arm_left_lower.addOrReplaceChild("left_finger_one_r1", CubeListBuilder.create().texOffs(0, 0).addBox(0.1F, 3.0F, -6.0F, 0.8F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.2094F, 0.0F, 0.0F));

		PartDefinition left_forearm_r1 = arm_left_lower.addOrReplaceChild("left_forearm_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -1.0F, -3.0F, 1.8F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.5585F, 0.0F, 0.0F));

		PartDefinition arm_right_upper = chest.addOrReplaceChild("arm_right_upper", CubeListBuilder.create(), PartPose.offsetAndRotation(6.5F, -1.0F, -4.0F, 0.0F, 0.0F, 0.1396F));

		PartDefinition right_upper_arm_r1 = arm_right_upper.addOrReplaceChild("right_upper_arm_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-0.9F, 0.0F, -2.0F, 2.2F, 6.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.3142F, 0.0F, 0.1396F));

		PartDefinition arm_right_lower = arm_right_upper.addOrReplaceChild("arm_right_lower", CubeListBuilder.create(), PartPose.offset(0.7F, 5.0F, -1.0F));

		PartDefinition right_finger_two_r1 = arm_right_lower.addOrReplaceChild("right_finger_two_r1", CubeListBuilder.create().texOffs(0, 0).addBox(0.2F, 3.0F, -5.5F, 0.8F, 2.0F, 3.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1222F, 0.0F, 0.0F));

		PartDefinition right_finger_one_r1 = arm_right_lower.addOrReplaceChild("right_finger_one_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-0.9F, 3.0F, -6.0F, 0.8F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.2094F, 0.0F, 0.0F));

		PartDefinition right_forearm_r1 = arm_right_lower.addOrReplaceChild("right_forearm_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-0.8F, -1.0F, -3.0F, 1.8F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.5585F, 0.0F, 0.0F));

		PartDefinition tail_base = pelvis.addOrReplaceChild("tail_base", CubeListBuilder.create(), PartPose.offset(0.0F, -2.0F, 6.0F));

		PartDefinition tail_base_top_r1 = tail_base.addOrReplaceChild("tail_base_top_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-4.5F, -6.0F, -1.0F, 9.0F, 4.0F, 10.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-5.5F, -4.0F, -2.0F, 11.0F, 9.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.0698F, 0.0F, 0.0F));

		PartDefinition tail_mid = tail_base.addOrReplaceChild("tail_mid", CubeListBuilder.create(), PartPose.offset(0.0F, 1.0F, 11.0F));

		PartDefinition tail_mid_taper_r1 = tail_mid.addOrReplaceChild("tail_mid_taper_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-3.4F, -1.8F, 8.0F, 6.8F, 5.6F, 11.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.1047F, 0.0F, 0.0F));

		PartDefinition tail_mid_mass_r1 = tail_mid.addOrReplaceChild("tail_mid_mass_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-4.2F, -3.5F, -3.0F, 8.4F, 7.0F, 13.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.0873F, 0.0F, 0.0F));

		PartDefinition tail_tip = tail_mid.addOrReplaceChild("tail_tip", CubeListBuilder.create(), PartPose.offset(0.0F, 1.0F, 11.0F));

		PartDefinition tail_tip_end_r1 = tail_tip.addOrReplaceChild("tail_tip_end_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-1.8F, 0.2F, 17.0F, 3.6F, 3.6F, 11.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.1745F, 0.0F, 0.0F));

		PartDefinition tail_tip_long_r1 = tail_tip.addOrReplaceChild("tail_tip_long_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-2.6F, -1.5F, 6.0F, 5.2F, 4.5F, 13.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.1396F, 0.0F, 0.0F));

		PartDefinition leg_left_upper = pelvis.addOrReplaceChild("leg_left_upper", CubeListBuilder.create(), PartPose.offset(-5.0F, 0.0F, 0.0F));

		PartDefinition left_thigh_muscle_r1 = leg_left_upper.addOrReplaceChild("left_thigh_muscle_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-3.5F, -4.0F, -4.5F, 7.0F, 9.0F, 9.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-3.0F, -2.0F, -4.0F, 6.0F, 12.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1396F, 0.0F, 0.0F));

		PartDefinition leg_left_lower = leg_left_upper.addOrReplaceChild("leg_left_lower", CubeListBuilder.create(), PartPose.offset(0.0F, 8.0F, -1.0F));

		PartDefinition left_ankle_r1 = leg_left_lower.addOrReplaceChild("left_ankle_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-1.6F, 3.0F, -3.0F, 3.2F, 6.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-2.0F, -2.0F, -3.5F, 4.0F, 10.5F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2443F, 0.0F, 0.0F));

		PartDefinition foot_left = leg_left_lower.addOrReplaceChild("foot_left", CubeListBuilder.create().texOffs(0, 0).addBox(-2.5F, -0.5F, -6.0F, 5.0F, 3.5F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-0.8F, 0.8F, -12.0F, 1.5F, 2.2F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 7.0F, -1.0F));

		PartDefinition left_toe_outer_r1 = foot_left.addOrReplaceChild("left_toe_outer_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-2.5F, 1.0F, -11.0F, 1.7F, 2.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.1396F, 0.0F));

		PartDefinition left_toe_inner_r1 = foot_left.addOrReplaceChild("left_toe_inner_r1", CubeListBuilder.create().texOffs(0, 0).addBox(0.8F, 1.0F, -11.0F, 1.7F, 2.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.1396F, 0.0F));

		PartDefinition leg_right_upper = pelvis.addOrReplaceChild("leg_right_upper", CubeListBuilder.create(), PartPose.offset(5.0F, 0.0F, 0.0F));

		PartDefinition right_thigh_muscle_r1 = leg_right_upper.addOrReplaceChild("right_thigh_muscle_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-3.5F, -4.0F, -4.5F, 7.0F, 9.0F, 9.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-3.0F, -2.0F, -4.0F, 6.0F, 12.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1396F, 0.0F, 0.0F));

		PartDefinition leg_right_lower = leg_right_upper.addOrReplaceChild("leg_right_lower", CubeListBuilder.create(), PartPose.offset(0.0F, 8.0F, -1.0F));

		PartDefinition right_ankle_r1 = leg_right_lower.addOrReplaceChild("right_ankle_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-1.6F, 3.0F, -3.0F, 3.2F, 6.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-2.0F, -2.0F, -3.5F, 4.0F, 10.5F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2443F, 0.0F, 0.0F));

		PartDefinition foot_right = leg_right_lower.addOrReplaceChild("foot_right", CubeListBuilder.create().texOffs(0, 0).addBox(-2.5F, -0.5F, -6.0F, 5.0F, 3.5F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-0.7F, 0.8F, -12.0F, 1.5F, 2.2F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 7.0F, -1.0F));

		PartDefinition right_toe_outer_r1 = foot_right.addOrReplaceChild("right_toe_outer_r1", CubeListBuilder.create().texOffs(0, 0).addBox(0.8F, 1.0F, -11.0F, 1.7F, 2.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.1396F, 0.0F));

		PartDefinition right_toe_inner_r1 = foot_right.addOrReplaceChild("right_toe_inner_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-2.5F, 1.0F, -11.0F, 1.7F, 2.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.1396F, 0.0F));

		return LayerDefinition.create(meshdefinition, 1024, 1024);
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		pelvis.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}
