package com.coolerpromc.ancientcreature.worldgen.feature;

import com.coolerpromc.ancientcreature.Constants;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.*;

public final class ModPlacedFeatures {
    public static final ResourceKey<PlacedFeature> FOREST_ROCKS = ResourceKey.create(Registries.PLACED_FEATURE, Constants.id("forest_rocks"));
    public static final ResourceKey<PlacedFeature> FOSSIL_ORE = ResourceKey.create(Registries.PLACED_FEATURE, Constants.id("fossil_ore"));
    public static final ResourceKey<PlacedFeature> DEEPSLATE_FOSSIL_ORE = ResourceKey.create(Registries.PLACED_FEATURE, Constants.id("deepslate_fossil_ore"));
    public static final ResourceKey<PlacedFeature> AMBER_ORE = ResourceKey.create(Registries.PLACED_FEATURE, Constants.id("amber_ore"));
    public static final ResourceKey<PlacedFeature> FROZEN_FOSSIL = ResourceKey.create(Registries.PLACED_FEATURE, Constants.id("frozen_fossil"));
    public static final ResourceKey<PlacedFeature> FOSSIL_SEAM = ResourceKey.create(Registries.PLACED_FEATURE, Constants.id("fossil_seam"));
    public static final ResourceKey<PlacedFeature> FOSSIL_BED = ResourceKey.create(Registries.PLACED_FEATURE, Constants.id("fossil_bed"));

    private ModPlacedFeatures() {
    }

    public static void bootstrap(BootstrapContext<PlacedFeature> context) {
        HolderGetter<Feature> configuredFeatures = context.lookup(Registries.FEATURE);
        Holder<Feature> forestRocks = configuredFeatures.getOrThrow(ModConfiguredFeatures.FOREST_ROCKS);
        Holder<Feature> fossilOre = configuredFeatures.getOrThrow(ModConfiguredFeatures.FOSSIL_ORE);

        PlacementUtils.register(context, FOREST_ROCKS, forestRocks, CountPlacement.of(1), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_TOP_SOLID, BiomeFilter.biome());
        PlacementUtils.register(context, DEEPSLATE_FOSSIL_ORE, configuredFeatures.getOrThrow(ModConfiguredFeatures.DEEPSLATE_FOSSIL_ORE), RarityFilter.onAverageOnceEvery(12), InSquarePlacement.spread(), HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(0)), BiomeFilter.biome());
        PlacementUtils.register(context, AMBER_ORE, configuredFeatures.getOrThrow(ModConfiguredFeatures.AMBER_ORE), RarityFilter.onAverageOnceEvery(6), InSquarePlacement.spread(), HeightRangePlacement.triangle(VerticalAnchor.absolute(8), VerticalAnchor.absolute(96)), BiomeFilter.biome());
        // Ice sits at the surface, so search from the top down rather than through the whole column.
        PlacementUtils.register(context, FROZEN_FOSSIL, configuredFeatures.getOrThrow(ModConfiguredFeatures.FROZEN_FOSSIL), CountPlacement.of(4), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, OffsetPlacement.vertical(UniformInt.of(-14, -1)), BiomeFilter.biome());
        PlacementUtils.register(context, FOSSIL_SEAM, configuredFeatures.getOrThrow(ModConfiguredFeatures.FOSSIL_SEAM), CountPlacement.of(3), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome());
        PlacementUtils.register(context, FOSSIL_BED, configuredFeatures.getOrThrow(ModConfiguredFeatures.FOSSIL_BED), RarityFilter.onAverageOnceEvery(32), InSquarePlacement.spread(), HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.top()), BiomeFilter.biome());
        PlacementUtils.register(context, FOSSIL_ORE, fossilOre, RarityFilter.onAverageOnceEvery(64), InSquarePlacement.spread(), HeightRangePlacement.uniform(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(50)), BiomeFilter.biome());
    }
}
