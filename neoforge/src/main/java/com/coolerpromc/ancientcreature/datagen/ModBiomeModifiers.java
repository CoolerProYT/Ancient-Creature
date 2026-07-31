package com.coolerpromc.ancientcreature.datagen;

import com.coolerpromc.ancientcreature.AncientCreature;
import com.coolerpromc.ancientcreature.platform.Services;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public final class ModBiomeModifiers {
    private ModBiomeModifiers() {
    }

    public static void bootstrap(BootstrapContext<BiomeModifier> context) {
        AncientCreature.initBiomeModifier();

        HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);
        HolderGetter<PlacedFeature> placedFeatures = context.lookup(Registries.PLACED_FEATURE);

        Services.REGISTRY.applyBiomeModifierRegistrations((biomeTagKey, step, placedFeatureKey) -> {
            ResourceKey<BiomeModifier> key = ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, placedFeatureKey.identifier());
            context.register(key, new BiomeModifiers.AddFeaturesBiomeModifier(biomes.getOrThrow(biomeTagKey), HolderSet.direct(placedFeatures.getOrThrow(placedFeatureKey)), step));
        });
    }
}
