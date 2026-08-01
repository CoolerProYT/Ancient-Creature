package com.coolerpromc.ancientcreature.client.entity.model.block;

import com.coolerpromc.ancientcreature.client.entity.animation.block.EmbryogenesisChamberAnimation;
import com.coolerpromc.ancientcreature.client.entity.state.block.EmbryogenesisChamberRenderState;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class EmbryogenesisChamberModel extends EntityModel<EmbryogenesisChamberRenderState> {
    private final ModelPart chassis;
    private final ModelPart incubationVessel;
    private final ModelPart embryoCore;
    private final ModelPart cradle;
    private final ModelPart injectorLeft;
    private final ModelPart injectorRight;
    private final ModelPart fluidRing;
    private final ModelPart controls;
    private final ModelPart statusLights;
    private final ModelPart nutrientTank;
    private final KeyframeAnimation processingAnimation;

    public EmbryogenesisChamberModel(ModelPart root) {
        super(root);
        ModelPart chamber = root.getChild("embryogenesis_chamber");
        chassis = chamber.getChild("chassis");
        incubationVessel = chamber.getChild("incubation_vessel");
        embryoCore = incubationVessel.getChild("embryo_core");
        cradle = chamber.getChild("cradle");
        injectorLeft = chamber.getChild("injector_left");
        injectorRight = chamber.getChild("injector_right");
        fluidRing = chamber.getChild("fluid_ring");
        controls = chamber.getChild("controls");
        statusLights = controls.getChild("status_lights");
        nutrientTank = controls.getChild("nutrient_tank");
        processingAnimation = EmbryogenesisChamberAnimation.PROCESSING.bake(root);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition chamber = root.addOrReplaceChild("embryogenesis_chamber", CubeListBuilder.create(), PartPose.offset(0, 24, 0));

        chamber.addOrReplaceChild("chassis", CubeListBuilder.create()
            .texOffs(0, 0).addBox(-8, -3, -8, 16, 3, 16, deform())
            .texOffs(0, 0).addBox(-7, -22, 5.5F, 14, 19, 2.5F, deform())
            .texOffs(0, 0).addBox(6, -22, -6.5F, 2, 19, 12.5F, deform())
            .texOffs(0, 0).addBox(-8, -22, -6.5F, 2, 19, 12.5F, deform())
            .texOffs(0, 0).addBox(-8, -24, -6.5F, 16, 2, 14.5F, deform())
            .texOffs(0, 0).addBox(4.5F, 0, -8.5F, 3, .5F, 2, deform())
            .texOffs(0, 0).addBox(-7.5F, 0, -8.5F, 3, .5F, 2, deform())
            .texOffs(64, 0).addBox(-7.5F, -4, -7.5F, 15, 1, 15, deform())
            .texOffs(64, 0).addBox(-6.5F, -22, -7, 13, 1.5F, 1.5F, deform())
            .texOffs(128, 0).addBox(-7.2F, -3.6F, -7.8F, 14.4F, .6F, .6F, deform())
            .texOffs(128, 0).addBox(7.2F, -21.5F, -7.2F, .6F, 18.5F, .6F, deform())
            .texOffs(128, 0).addBox(-7.8F, -21.5F, -7.2F, .6F, 18.5F, .6F, deform())
            .texOffs(128, 0).addBox(-7.2F, -21.9F, -7.2F, 14.4F, .6F, .6F, deform()), PartPose.ZERO);

        PartDefinition vessel = chamber.addOrReplaceChild("incubation_vessel", CubeListBuilder.create()
            .texOffs(192, 0).addBox(4.2F, -7, -4.5F, .5F, 14, 9, deform())
            .texOffs(192, 0).addBox(-4.7F, -7, -4.5F, .5F, 14, 9, deform())
            .texOffs(192, 0).addBox(-4.2F, -7, 4.2F, 8.4F, 14, .4F, deform())
            .texOffs(64, 0).addBox(-4.2F, -7.2F, -4.2F, 8.4F, .7F, 8.4F, deform())
            .texOffs(64, 0).addBox(-4.2F, 6.5F, -4.2F, 8.4F, .7F, 8.4F, deform())
            .texOffs(0, 64).addBox(-3.9F, -5.8F, 3.9F, 7.8F, 12.2F, .25F, deform())
            .texOffs(0, 64).addBox(3.9F, -5.8F, -3.9F, .25F, 12.2F, 7.8F, deform())
            .texOffs(0, 64).addBox(-4.15F, -5.8F, -3.9F, .25F, 12.2F, 7.8F, deform()), PartPose.offset(0, -13, 0));

        vessel.addOrReplaceChild("embryo_core", CubeListBuilder.create()
            .texOffs(64, 64).addBox(-2, -2, -1.5F, 4, 5, 3, deform())
            .texOffs(64, 64).addBox(-1.5F, -4.2F, -1.2F, 3, 2.2F, 2.4F, deform())
            .texOffs(64, 64).addBox(-.8F, -5.2F, -.7F, 1.6F, 1, 1.4F, deform())
            .texOffs(64, 64).addBox(-1.6F, 3, -1.2F, 3.2F, 1.8F, 2.4F, deform())
            .texOffs(64, 64).addBox(-.9F, 4.8F, -.7F, 1.8F, .7F, 1.4F, deform())
            .texOffs(128, 0).addBox(-2.2F, 0, -1.7F, 4.4F, 1, 3.4F, deform()), PartPose.ZERO);

        chamber.addOrReplaceChild("cradle", CubeListBuilder.create()
            .texOffs(64, 0).addBox(-3.8F, .8F, -3.8F, 7.6F, .8F, 7.6F, deform())
            .texOffs(64, 0).addBox(3.4F, -3, -2.5F, .7F, 4, 5, deform())
            .texOffs(64, 0).addBox(-4.1F, -3, -2.5F, .7F, 4, 5, deform())
            .texOffs(64, 0).addBox(-3.4F, 0, -4.1F, 6.8F, 1, .7F, deform())
            .texOffs(64, 0).addBox(-3.4F, 0, 3.4F, 6.8F, 1, .7F, deform()), PartPose.offset(0, -8, 0));

        chamber.addOrReplaceChild("injector_left", CubeListBuilder.create()
            .texOffs(64, 0).addBox(-.1F, -5, -.4F, .7F, 10, .7F, deform())
            .texOffs(64, 0).addBox(-1.3F, -1.5F, -.8F, 1.7F, 2.5F, 1.4F, deform())
            .texOffs(64, 0).addBox(-2.9F, -.6F, -.3F, 1.6F, .9F, .6F, deform())
            .texOffs(128, 0).addBox(-4, -.35F, -.15F, 1.1F, .4F, .3F, deform()), PartPose.offset(4.6F, -14, -4.4F));

        chamber.addOrReplaceChild("injector_right", CubeListBuilder.create()
            .texOffs(64, 0).addBox(-.6F, -5, -.4F, .7F, 10, .7F, deform())
            .texOffs(64, 0).addBox(-.4F, -1.5F, -.8F, 1.7F, 2.5F, 1.4F, deform())
            .texOffs(64, 0).addBox(1.3F, -.6F, -.3F, 1.6F, .9F, .6F, deform())
            .texOffs(128, 0).addBox(2.9F, -.35F, -.15F, 1.1F, .4F, .3F, deform()), PartPose.offset(-4.6F, -14, -4.4F));

        chamber.addOrReplaceChild("fluid_ring", CubeListBuilder.create()
            .texOffs(128, 0).addBox(-4.6F, -.2F, -4.9F, 9.2F, .8F, .7F, deform())
            .texOffs(128, 0).addBox(-4.6F, -.2F, 4.2F, 9.2F, .8F, .7F, deform())
            .texOffs(128, 0).addBox(4.2F, -.2F, -4.2F, .7F, .8F, 8.4F, deform())
            .texOffs(128, 0).addBox(-4.9F, -.2F, -4.2F, .7F, .8F, 8.4F, deform())
            .texOffs(0, 64).addBox(-3.6F, -.4F, -5, 7.2F, 1.2F, .15F, deform())
            .texOffs(0, 64).addBox(-3.6F, -.4F, 4.85F, 7.2F, 1.2F, .15F, deform()), PartPose.offset(0, -14, 0));

        PartDefinition controls = chamber.addOrReplaceChild("controls", CubeListBuilder.create()
            .texOffs(0, 0).addBox(3.5F, -15, -8.2F, 3.5F, 7, 2.1F, deform())
            .texOffs(128, 64).addBox(3.9F, -13.2F, -8.4F, 2.7F, 3.2F, .25F, deform())
            .texOffs(0, 64).addBox(4.3F, -12.7F, -8.55F, 1.9F, 2.2F, .17F, deform())
            .texOffs(0, 0).addBox(-6.8F, -15, -8.25F, 3.2F, 5, 2.05F, deform())
            .texOffs(128, 64).addBox(-6.4F, -14.4F, -8.5F, 2.4F, 3.7F, .22F, deform())
            .texOffs(0, 0).addBox(-4.8F, -19, -7.3F, 9.6F, 1.8F, .9F, deform()), PartPose.ZERO);

        controls.addOrReplaceChild("status_lights", CubeListBuilder.create()
            .texOffs(0, 64).addBox(2.5F, -.5F, 0, 1.4F, .8F, .22F, deform())
            .texOffs(0, 64).addBox(.5F, -.5F, 0, 1.4F, .8F, .22F, deform())
            .texOffs(0, 64).addBox(-1.9F, -.5F, 0, 1.4F, .8F, .22F, deform())
            .texOffs(0, 64).addBox(-3.9F, -.5F, 0, 1.4F, .8F, .22F, deform()), PartPose.offset(0, -18, -7.5F));

        controls.addOrReplaceChild("nutrient_tank", CubeListBuilder.create()
            .texOffs(0, 0).addBox(-1.2F, 1, -.8F, 2.5F, 4, 1.8F, deform())
            .texOffs(192, 0).addBox(-.8F, 1.6F, -1.05F, 1.7F, 2.8F, .25F, deform())
            .texOffs(64, 0).addBox(-.9F, .3F, -.5F, 1.9F, .7F, 1.2F, deform())
            .texOffs(192, 64).addBox(-.2F, -5, 0, .5F, 5.5F, .5F, deform()), PartPose.offset(5.6F, -11, -5.6F));

        return LayerDefinition.create(mesh, 256, 256);
    }

    private static CubeDeformation deform() {
        return new CubeDeformation(0);
    }

    @Override
    public void setupAnim(EmbryogenesisChamberRenderState state) {
        chassis.resetPose();
        incubationVessel.resetPose();
        embryoCore.resetPose();
        cradle.resetPose();
        injectorLeft.resetPose();
        injectorRight.resetPose();
        fluidRing.resetPose();
        controls.resetPose();
        statusLights.resetPose();
        nutrientTank.resetPose();
        processingAnimation.apply(state.processingState, state.ageInTicks);
    }
}
