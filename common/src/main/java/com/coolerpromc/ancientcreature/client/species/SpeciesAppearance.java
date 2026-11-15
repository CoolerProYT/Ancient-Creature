package com.coolerpromc.ancientcreature.client.species;

import net.minecraft.resources.Identifier;

import java.util.List;

public record SpeciesAppearance(Identifier species, BedrockClientEntity entity, List<ActiveRenderController> renderControllers, ClientSpeciesDefinition settings) {
    public record ActiveRenderController(BedrockRenderController controller, com.coolerpromc.ancientcreature.molang.MolangExpression condition) {
    }
}
