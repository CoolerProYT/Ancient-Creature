package com.coolerpromc.ancientcreature.client.entity.model.block;

import com.coolerpromc.ancientcreature.client.entity.animation.block.IncubatorAnimation;
import com.coolerpromc.ancientcreature.client.entity.state.block.IncubatorRenderState;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class IncubatorModel extends EntityModel<IncubatorRenderState> {
	private final ModelPart incubator;
	private final ModelPart cabinet;
	private final ModelPart door;
	private final ModelPart egg_tray;
	private final ModelPart eggs;
	private final ModelPart fan_rotor;
	private final ModelPart heater_coils;
	private final ModelPart controls;
	private final ModelPart status_lights;
	private final KeyframeAnimation processingAnimation;

	public IncubatorModel(ModelPart root) {
		super(root);
		this.incubator = root.getChild("incubator");
		this.cabinet = this.incubator.getChild("cabinet");
		this.door = this.incubator.getChild("door");
		this.egg_tray = this.incubator.getChild("egg_tray");
		this.eggs = this.egg_tray.getChild("eggs");
		this.fan_rotor = this.incubator.getChild("fan_rotor");
		this.heater_coils = this.incubator.getChild("heater_coils");
		this.controls = this.incubator.getChild("controls");
		this.status_lights = this.controls.getChild("status_lights");
		this.processingAnimation = IncubatorAnimation.PROCESSING.bake(root);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition incubator = partdefinition.addOrReplaceChild("incubator", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition cabinet = incubator.addOrReplaceChild("cabinet", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -3.0F, -8.0F, 16.0F, 3.0F, 16.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-7.0F, -22.0F, 5.5F, 14.0F, 19.0F, 2.5F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(6.5F, -22.0F, -6.5F, 1.5F, 19.0F, 12.5F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-8.0F, -22.0F, -6.5F, 1.5F, 19.0F, 12.5F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-8.0F, -24.0F, -6.5F, 16.0F, 2.0F, 14.5F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-6.0F, -5.0F, -6.0F, 12.0F, 1.0F, 12.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-5.8F, -20.5F, 4.8F, 11.6F, 15.5F, 0.6F, new CubeDeformation(0.0F))
		.texOffs(64, 0).addBox(-6.5F, -22.0F, -7.2F, 13.0F, 2.5F, 1.4F, new CubeDeformation(0.0F))
		.texOffs(64, 0).addBox(4.4F, 0.0F, -7.5F, 2.9F, 0.5F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(64, 0).addBox(-7.3F, 0.0F, -7.5F, 2.9F, 0.5F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(64, 0).addBox(5.8F, -21.0F, -6.8F, 0.7F, 17.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(64, 0).addBox(-6.5F, -21.0F, -6.8F, 0.7F, 17.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(64, 0).addBox(-5.8F, -4.7F, -6.8F, 11.6F, 0.7F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(64, 0).addBox(-5.8F, -21.0F, -6.8F, 11.6F, 0.7F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(64, 0).addBox(-4.0F, -24.3F, -3.0F, 8.0F, 0.5F, 7.0F, new CubeDeformation(0.0F))
		.texOffs(64, 0).addBox(2.7F, -24.45F, -2.4F, 0.7F, 0.2F, 5.8F, new CubeDeformation(0.0F))
		.texOffs(64, 0).addBox(0.5F, -24.45F, -2.4F, 0.7F, 0.2F, 5.8F, new CubeDeformation(0.0F))
		.texOffs(64, 0).addBox(-1.7F, -24.45F, -2.4F, 0.7F, 0.2F, 5.8F, new CubeDeformation(0.0F))
		.texOffs(64, 0).addBox(-3.9F, -24.45F, -2.4F, 0.7F, 0.2F, 5.8F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition door = incubator.addOrReplaceChild("door", CubeListBuilder.create().texOffs(64, 0).addBox(5.5F, -7.5F, -0.4F, 0.9F, 16.0F, 0.8F, new CubeDeformation(0.0F))
		.texOffs(64, 0).addBox(-6.4F, -7.5F, -0.4F, 0.9F, 16.0F, 0.8F, new CubeDeformation(0.0F))
		.texOffs(64, 0).addBox(-5.5F, 7.6F, -0.4F, 11.0F, 0.9F, 0.8F, new CubeDeformation(0.0F))
		.texOffs(64, 0).addBox(-5.5F, -7.5F, -0.4F, 11.0F, 0.9F, 0.8F, new CubeDeformation(0.0F))
		.texOffs(64, 0).addBox(-6.7F, -3.0F, -1.0F, 0.8F, 5.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(64, 0).addBox(-6.9F, -1.4F, -1.4F, 1.2F, 1.8F, 0.6F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -13.0F, -7.6F));

		PartDefinition egg_tray = incubator.addOrReplaceChild("egg_tray", CubeListBuilder.create().texOffs(64, 0).addBox(-5.2F, 6.5F, -4.7F, 10.4F, 0.9F, 9.4F, new CubeDeformation(0.0F))
		.texOffs(64, 0).addBox(-5.4F, 5.9F, -5.0F, 10.8F, 0.8F, 0.7F, new CubeDeformation(0.0F))
		.texOffs(64, 0).addBox(-5.4F, 5.9F, 4.3F, 10.8F, 0.8F, 0.7F, new CubeDeformation(0.0F))
		.texOffs(64, 0).addBox(4.7F, 5.9F, -4.3F, 0.7F, 0.8F, 8.6F, new CubeDeformation(0.0F))
		.texOffs(64, 0).addBox(-5.4F, 5.9F, -4.3F, 0.7F, 0.8F, 8.6F, new CubeDeformation(0.0F))
		.texOffs(64, 0).addBox(4.0F, 7.1F, -5.5F, 0.8F, 0.9F, 11.0F, new CubeDeformation(0.0F))
		.texOffs(64, 0).addBox(-4.8F, 7.1F, -5.5F, 0.8F, 0.9F, 11.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -7.0F, 0.0F));

		PartDefinition eggs = egg_tray.addOrReplaceChild("eggs", CubeListBuilder.create().texOffs(64, 64).addBox(1.4F, -2.5F, -1.5F, 2.6F, 2.6F, 2.7F, new CubeDeformation(0.0F))
		.texOffs(64, 64).addBox(1.7F, -4.5F, -1.3F, 2.0F, 2.0F, 2.3F, new CubeDeformation(0.0F))
		.texOffs(64, 64).addBox(2.2F, -5.5F, -0.9F, 1.0F, 1.0F, 1.5F, new CubeDeformation(0.0F))
		.texOffs(64, 64).addBox(-1.3F, -2.8F, -1.0F, 2.6F, 2.9F, 2.7F, new CubeDeformation(0.0F))
		.texOffs(64, 64).addBox(-1.0F, -5.0F, -0.8F, 2.0F, 2.2F, 2.3F, new CubeDeformation(0.0F))
		.texOffs(64, 64).addBox(-0.5F, -6.1F, -0.4F, 1.0F, 1.1F, 1.5F, new CubeDeformation(0.0F))
		.texOffs(64, 64).addBox(-4.1F, -2.4F, -1.5F, 2.6F, 2.5F, 2.6F, new CubeDeformation(0.0F))
		.texOffs(64, 64).addBox(-3.8F, -4.3F, -1.3F, 2.0F, 1.9F, 2.2F, new CubeDeformation(0.0F))
		.texOffs(64, 64).addBox(-3.3F, -5.3F, -0.9F, 1.0F, 1.0F, 1.4F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.0F, 0.0F));

		PartDefinition fan_rotor = incubator.addOrReplaceChild("fan_rotor", CubeListBuilder.create().texOffs(64, 0).addBox(-1.0F, -1.0F, -0.5F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(64, 0).addBox(-0.7F, -4.2F, -0.35F, 1.4F, 3.2F, 0.7F, new CubeDeformation(0.0F))
		.texOffs(64, 0).addBox(-0.7F, 1.0F, -0.35F, 1.4F, 3.2F, 0.7F, new CubeDeformation(0.0F))
		.texOffs(64, 0).addBox(1.0F, -0.7F, -0.35F, 3.2F, 1.4F, 0.7F, new CubeDeformation(0.0F))
		.texOffs(64, 0).addBox(-4.2F, -0.7F, -0.35F, 3.2F, 1.4F, 0.7F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -16.0F, 5.2F));

		PartDefinition heater_coils = incubator.addOrReplaceChild("heater_coils", CubeListBuilder.create().texOffs(96, 64).addBox(-4.8F, -0.2F, -3.8F, 9.6F, 0.5F, 0.6F, new CubeDeformation(0.0F))
		.texOffs(96, 64).addBox(-4.8F, -0.2F, -1.4F, 9.6F, 0.5F, 0.6F, new CubeDeformation(0.0F))
		.texOffs(96, 64).addBox(-4.8F, -0.2F, 1.0F, 9.6F, 0.5F, 0.6F, new CubeDeformation(0.0F))
		.texOffs(96, 64).addBox(-4.8F, -0.2F, 3.4F, 9.6F, 0.5F, 0.6F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -5.0F, 0.0F));

		PartDefinition controls = incubator.addOrReplaceChild("controls", CubeListBuilder.create().texOffs(0, 0).addBox(-6.5F, -20.2F, -8.3F, 9.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 64).addBox(-5.8F, -19.3F, -8.55F, 2.9F, 2.1F, 0.3F, new CubeDeformation(0.0F))
		.texOffs(0, 64).addBox(-5.5F, -19.0F, -8.75F, 2.3F, 1.5F, 0.25F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition status_lights = controls.addOrReplaceChild("status_lights", CubeListBuilder.create().texOffs(64, 96).addBox(0.6F, -0.45F, -0.2F, 1.2F, 0.9F, 0.4F, new CubeDeformation(0.0F))
		.texOffs(72, 96).addBox(-0.8F, -0.45F, -0.2F, 1.2F, 0.9F, 0.4F, new CubeDeformation(0.0F))
		.texOffs(80, 96).addBox(-2.2F, -0.45F, -0.2F, 1.2F, 0.9F, 0.4F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -18.0F, -8.2F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void setupAnim(IncubatorRenderState state) {
		cabinet.resetPose();
		door.resetPose();
		egg_tray.resetPose();
		eggs.resetPose();
		fan_rotor.resetPose();
		heater_coils.resetPose();
		controls.resetPose();
		status_lights.resetPose();
		processingAnimation.apply(state.processingState, state.ageInTicks);
	}
}
