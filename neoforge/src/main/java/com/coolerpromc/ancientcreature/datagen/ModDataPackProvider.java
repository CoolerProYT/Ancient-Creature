package com.coolerpromc.ancientcreature.datagen;

import com.coolerpromc.ancientcreature.datagen.advancement.ModProgressAdvancement;
import com.coolerpromc.ancientcreature.item.FossilPart;
import com.coolerpromc.ancientcreature.registry.ModRegistries;
import com.coolerpromc.ancientcreature.worldgen.feature.ModConfiguredFeatures;
import com.coolerpromc.ancientcreature.worldgen.feature.ModPlacedFeatures;
import com.coolerpromc.ancientcreature.worldgen.structure.ModStructureSets;
import com.coolerpromc.ancientcreature.worldgen.structure.ModStructureTemplatePools;
import com.coolerpromc.ancientcreature.worldgen.structure.ModStructures;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.advancements.AdvancementProvider;
import net.minecraft.data.recipes.RecipeProvider;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.List;

public final class ModDataPackProvider {
    // World-layer registries: worldgen content resolved once at world creation.
    public static final RegistrySetBuilder WORLD_BUILDER = new RegistrySetBuilder()
        .add(Registries.FEATURE, ModConfiguredFeatures::bootstrap)
        .add(Registries.PLACED_FEATURE, ModPlacedFeatures::bootstrap)
        .add(Registries.TEMPLATE_POOL, ModStructureTemplatePools::boostrap)
        .add(Registries.STRUCTURE, ModStructures::boostrap)
        .add(Registries.STRUCTURE_SET, ModStructureSets::boostrap)
        .add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, ModBiomeModifiers::bootstrap)
        .add(ModRegistries.FOSSIL_PART, FossilPart::bootstrap);

    // Reloadable-layer registries: refreshed on datapack reload, built on top of the world layer.
    public static final RegistrySetBuilder RELOADABLE_BUILDER = new RegistrySetBuilder()
        .add(Registries.LOOT_TABLE, new ModLootTableProvider())
        .add(Registries.ADVANCEMENT, new AdvancementProvider(List.of(ModProgressAdvancement::new)))
        .add(RecipeProvider.asBootstrap(ModRecipeProvider::new));

    private ModDataPackProvider() {
    }
}
