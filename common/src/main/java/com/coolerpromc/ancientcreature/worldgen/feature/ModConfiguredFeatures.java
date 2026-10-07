package com.coolerpromc.ancientcreature.worldgen.feature;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.block.ModBlocks;
import com.coolerpromc.ancientcreature.block.custom.RockPileBlock;
import com.coolerpromc.ancientcreature.tag.ModBlockTags;
import com.coolerpromc.ancientcreature.worldgen.feature.cusom.FossilSeamFeature;
import com.coolerpromc.ancientcreature.worldgen.structure.ModProcessorLists;
import net.minecraft.core.HolderGetter;
import net.minecraft.resources.Identifier;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.FossilFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
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

import java.util.ArrayList;
import java.util.List;

public final class ModConfiguredFeatures {
    public static final ResourceKey<ConfiguredFeature<?, ?>> FOREST_ROCKS = key("forest_rocks");
    public static final ResourceKey<ConfiguredFeature<?, ?>> FOSSIL_ORE = key("fossil_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> DEEPSLATE_FOSSIL_ORE = key("deepslate_fossil_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> AMBER_ORE = key("amber_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> FROZEN_FOSSIL = key("frozen_fossil");
    public static final ResourceKey<ConfiguredFeature<?, ?>> FOSSIL_SEAM = key("fossil_seam");
    public static final ResourceKey<ConfiguredFeature<?, ?>> FOSSIL_BED = key("fossil_bed");

    private ModConfiguredFeatures() {
    }

    public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        WeightedList.Builder<BlockState> rocks = WeightedList.<BlockState>builder()
            .add(ModBlocks.ROCK_PILE.defaultBlockState(), 100)
            .add(ModBlocks.ROCK_PILE.defaultBlockState().setValue(RockPileBlock.HAS_FOSSIL, true), 30)
            .add(ModBlocks.ROCK_PILE.defaultBlockState().setValue(RockPileBlock.HAS_EGG, true), 4)
            .add(ModBlocks.ROCK_PILE.defaultBlockState().setValue(RockPileBlock.HAS_EGG, true).setValue(RockPileBlock.HAS_FOSSIL, true), 1);

        FeatureUtils.register(context, FOREST_ROCKS, ModFeatures.WATERLOGGABLE_BLOCK.get(), new SimpleBlockConfiguration(new WeightedStateProvider(rocks)));

        RuleTest stoneReplaceable = new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES);
        List<OreConfiguration.TargetBlockState> fossilOres = List.of(OreConfiguration.target(stoneReplaceable, ModBlocks.FOSSIL_ORE.defaultBlockState()));
        // Ore veins below size 4 almost never place a block, so every fossil vein uses at least 4.
        FeatureUtils.register(context, FOSSIL_ORE, Feature.ORE, new OreConfiguration(fossilOres, 4));

        RuleTest deepslateReplaceable = new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);
        FeatureUtils.register(context, DEEPSLATE_FOSSIL_ORE, Feature.ORE, new OreConfiguration(List.of(OreConfiguration.target(deepslateReplaceable, ModBlocks.DEEPSLATE_FOSSIL_ORE.defaultBlockState())), 4));
        FeatureUtils.register(context, AMBER_ORE, Feature.ORE, new OreConfiguration(List.of(OreConfiguration.target(stoneReplaceable, ModBlocks.AMBER_ORE.defaultBlockState())), 5));
        FeatureUtils.register(context, FROZEN_FOSSIL, Feature.ORE, new OreConfiguration(List.of(OreConfiguration.target(new TagMatchTest(ModBlockTags.FROZEN_FOSSIL_REPLACEABLES), ModBlocks.FROZEN_FOSSIL.defaultBlockState())), 4));

        WeightedList<BlockState> seam = WeightedList.<BlockState>builder()
            .add(ModBlocks.FOSSIL_ORE.defaultBlockState(), 5)
            .add(Blocks.BONE_BLOCK.defaultBlockState(), 1)
            .build();
        FeatureUtils.register(context, FOSSIL_SEAM, ModFeatures.FOSSIL_SEAM.get(), new FossilSeamFeature.Configuration(new WeightedStateProvider(seam), ModBlockTags.FOSSIL_SEAM_REPLACEABLES, UniformInt.of(4, 9), 24, 3));

        // Vanilla's own fossil skeletons, buried with fossil ore where vanilla puts coal.
        HolderGetter<StructureProcessorList> processorLists = context.lookup(Registries.PROCESSOR_LIST);
        List<Identifier> bones = new ArrayList<>();
        List<Identifier> overlays = new ArrayList<>();
        for (String name : List.of("spine_1", "spine_2", "spine_3", "spine_4", "skull_1", "skull_2", "skull_3", "skull_4")) {
            bones.add(Identifier.withDefaultNamespace("fossil/" + name));
            overlays.add(Identifier.withDefaultNamespace("fossil/" + name + "_coal"));
        }
        FeatureUtils.register(context, FOSSIL_BED, Feature.FOSSIL, new FossilFeatureConfiguration(bones, overlays, processorLists.getOrThrow(ModProcessorLists.FOSSIL_BED_BONES), processorLists.getOrThrow(ModProcessorLists.FOSSIL_BED_ORE), 4));
    }

    private static ResourceKey<ConfiguredFeature<?, ?>> key(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, Constants.id(name));
    }
}
