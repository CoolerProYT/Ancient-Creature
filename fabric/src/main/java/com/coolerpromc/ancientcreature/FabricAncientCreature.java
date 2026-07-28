package com.coolerpromc.ancientcreature;

import com.coolerpromc.ancientcreature.event.ItemEvents;
import com.coolerpromc.ancientcreature.platform.Services;
import com.coolerpromc.ancientcreature.worldgen.feature.ModPlacedFeatures;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.levelgen.GenerationStep;

public class FabricAncientCreature implements ModInitializer {
    @Override
    public void onInitialize() {
        AncientCreature.init();

        Services.REGISTRY.applyEntityAttributeRegistrations(FabricDefaultAttributeRegistry::register);
        BiomeModifications.addFeature(
            BiomeSelectors.tag(BiomeTags.IS_FOREST),
            GenerationStep.Decoration.VEGETAL_DECORATION,
            ModPlacedFeatures.FOREST_ROCKS
        );

        ItemTooltipCallback.EVENT.register(ItemEvents::onItemTooltip);
    }
}
