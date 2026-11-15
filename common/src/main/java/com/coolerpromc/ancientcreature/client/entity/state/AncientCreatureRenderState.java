package com.coolerpromc.ancientcreature.client.entity.state;

import com.coolerpromc.ancientcreature.client.model.bedrock.BakedBedrockModel;
import com.coolerpromc.ancientcreature.client.species.SpeciesAppearance;
import com.coolerpromc.ancientcreature.entity.Species;
import com.coolerpromc.ancientcreature.entity.custom.AncientCreatureAction;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class AncientCreatureRenderState extends LivingEntityRenderState {
    public Species species = Species.TRICERATOPS;
    public String variant = "default";

    public int variantIndex;
    public AncientCreatureAction action = AncientCreatureAction.NONE;

    public boolean isSprinting;
    public boolean isAggressive;
    public boolean isOnGround = true;
    public float hunger = 20.0F;
    public boolean isHungry;
    public float creatureHealth = 20.0F;
    public float creatureMaxHealth = 20.0F;
    public int hurtTimeRemaining;
    public boolean hasOwner;
    public boolean isSaddled;
    public int armorTier;
    public boolean isSitting;

    public boolean hasDeathAnimation;

    public @Nullable SpeciesAppearance appearance;

    public @Nullable BakedBedrockModel bakedModel;

    public float @Nullable [] bonePose;

    public List<Identifier> textures = List.of();
    public String material = "entity_alphatest";
    public int tint = -1;
    public boolean ignoreLighting;
    public boolean hurtOverlay = true;

    public float renderScale = 1.0F;
    public float shadowRadius = 0.5F;
}
