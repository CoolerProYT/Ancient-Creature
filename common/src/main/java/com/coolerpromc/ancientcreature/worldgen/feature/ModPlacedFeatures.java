package com.coolerpromc.ancientcreature.worldgen.feature;

import com.coolerpromc.ancientcreature.Constants;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.*;

public final class ModPlacedFeatures {
    public static final ResourceKey<PlacedFeature> FOREST_ROCKS = ResourceKey.create(Registries.PLACED_FEATURE, Constants.id("forest_rocks"));

    private ModPlacedFeatures() {
    }

    public static void bootstrap(BootstrapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);
        Holder<ConfiguredFeature<?, ?>> forestRocks = configuredFeatures.getOrThrow(ModConfiguredFeatures.FOREST_ROCKS);

        PlacementUtils.register(
            context,
            FOREST_ROCKS,
            forestRocks,
            CountPlacement.of(1),
            InSquarePlacement.spread(),
            PlacementUtils.HEIGHTMAP_TOP_SOLID,
            BiomeFilter.biome()
        );
    }
}
