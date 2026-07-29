package com.coolerpromc.ancientcreature.worldgen.feature;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.block.ModBlocks;
import com.coolerpromc.ancientcreature.block.custom.RockPileBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;

public final class ModConfiguredFeatures {
    public static final ResourceKey<ConfiguredFeature<?, ?>> FOREST_ROCKS = ResourceKey.create(Registries.CONFIGURED_FEATURE, Constants.id("forest_rocks"));

    private ModConfiguredFeatures() {
    }

    public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        WeightedList.Builder<BlockState> rocks = WeightedList.<BlockState>builder()
            .add(ModBlocks.ROCK_PILE.defaultBlockState(), 100)
            .add(ModBlocks.ROCK_PILE.defaultBlockState().setValue(RockPileBlock.HAS_FOSSIL, true), 30)
            .add(ModBlocks.ROCK_PILE.defaultBlockState().setValue(RockPileBlock.HAS_EGG, true), 4)
            .add(ModBlocks.ROCK_PILE.defaultBlockState().setValue(RockPileBlock.HAS_EGG, true).setValue(RockPileBlock.HAS_FOSSIL, true), 1);

        FeatureUtils.register(
            context,
            FOREST_ROCKS,
            Feature.SIMPLE_BLOCK,
            new SimpleBlockConfiguration(new WeightedStateProvider(rocks))
        );
    }
}
