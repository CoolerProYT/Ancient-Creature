package com.coolerpromc.ancientcreature.client.entity.state;

import com.coolerpromc.ancientcreature.client.animation.bedrock.AnimationController;
import com.coolerpromc.ancientcreature.client.species.ClientSpeciesDefinition;
import com.coolerpromc.ancientcreature.entity.Species;
import com.coolerpromc.ancientcreature.entity.custom.AncientCreatureAction;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

public class AncientCreatureRenderState extends LivingEntityRenderState {
    public Species species = Species.TRICERATOPS;
    public String variant = "default";
    public AncientCreatureAction action = AncientCreatureAction.NONE;

    public boolean isSprinting;
    public boolean isAggressive;
    public float creatureHealth = 20.0F;
    public float creatureMaxHealth = 20.0F;
    public int hurtTimeRemaining;

    public @Nullable ClientSpeciesDefinition clientDefinition;
    public @Nullable ModelPart bakedModel;
    public @Nullable AnimationController controller;
    public Identifier animationFile = Identifier.withDefaultNamespace("empty");

    public String controllerState = "idle";
    public float controllerStateSeconds;

    public float blendWeight = 1.0F;
    public @Nullable String previousControllerState;
    public float previousStateSeconds;

    public float renderScale = 1.0F;
    public float shadowRadius = 0.5F;
}
