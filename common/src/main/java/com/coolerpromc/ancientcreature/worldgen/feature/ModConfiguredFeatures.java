package com.coolerpromc.ancientcreature.worldgen.feature;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.block.ModBlocks;
import com.coolerpromc.ancientcreature.block.custom.RockPileBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;

import java.util.List;

public final class ModConfiguredFeatures {
    public static final ResourceKey<ConfiguredFeature<?, ?>> FOREST_ROCKS = ResourceKey.create(Registries.CONFIGURED_FEATURE, Constants.id("forest_rocks"));
    public static final ResourceKey<ConfiguredFeature<?, ?>> FOSSIL_ORE = ResourceKey.create(Registries.CONFIGURED_FEATURE, Constants.id("fossil_ore"));

    private ModConfiguredFeatures() {
    }

    public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        WeightedList.Builder<BlockState> rocks = WeightedList.<BlockState>builder()
            .add(ModBlocks.ROCK_PILE.defaultBlockState(), 100)
            .add(ModBlocks.ROCK_PILE.defaultBlockState().setValue(RockPileBlock.HAS_FOSSIL, true), 30)
            .add(ModBlocks.ROCK_PILE.defaultBlockState().setValue(RockPileBlock.HAS_EGG, true), 4)
            .add(ModBlocks.ROCK_PILE.defaultBlockState().setValue(RockPileBlock.HAS_EGG, true).setValue(RockPileBlock.HAS_FOSSIL, true), 1);

        FeatureUtils.register(context, FOREST_ROCKS, Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(new WeightedStateProvider(rocks)));

        RuleTest stoneReplaceable = new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES);
        List<OreConfiguration.TargetBlockState> fossilOres = List.of(OreConfiguration.target(stoneReplaceable, ModBlocks.FOSSIL_ORE.defaultBlockState()));
        FeatureUtils.register(context, FOSSIL_ORE, Feature.ORE, new OreConfiguration(fossilOres, 1));
    }
}
