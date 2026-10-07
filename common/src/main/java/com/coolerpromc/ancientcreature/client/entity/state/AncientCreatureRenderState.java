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
    /** Position of {@link #variant} in the species' variant list; {@code query.variant}. */
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

    public @Nullable SpeciesAppearance appearance;
    /** The geometry chosen this frame, already posed into {@link #bonePose}. */
    public @Nullable BakedBedrockModel bakedModel;
    /**
     * This entity's pose, one record per part in {@link BakedBedrockModel#root()}'s
     * {@code getAllParts()} order: x, y, z, xRot, yRot, zRot, xScale, yScale, zScale, visible.
     * Geometry is shared between entities, so the pose is copied out at extract time and loaded
     * back when the model is drawn.
     */
    public float @Nullable [] bonePose;
    /** Texture layers, bottom first. The first is the body; the rest are drawn over it. */
    public List<Identifier> textures = List.of();
    public String material = "entity_alphatest";
    public int tint = -1;
    public boolean ignoreLighting;
    public boolean hurtOverlay = true;

    public float renderScale = 1.0F;
    public float shadowRadius = 0.5F;
}
