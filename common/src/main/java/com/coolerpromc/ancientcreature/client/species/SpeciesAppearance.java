package com.coolerpromc.ancientcreature.client.species;

import net.minecraft.resources.Identifier;

import java.util.List;

/**
 * Everything needed to draw one species: its Bedrock client entity (real, or converted from a
 * format-1 species file), the render controllers it uses, and the mod-specific settings.
 */
public record SpeciesAppearance(Identifier species, BedrockClientEntity entity, List<ActiveRenderController> renderControllers, ClientSpeciesDefinition settings) {
    public record ActiveRenderController(BedrockRenderController controller, com.coolerpromc.ancientcreature.molang.MolangExpression condition) {
    }
}
