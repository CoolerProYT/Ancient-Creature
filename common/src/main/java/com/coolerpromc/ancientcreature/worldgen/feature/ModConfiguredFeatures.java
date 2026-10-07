package com.coolerpromc.ancientcreature.worldgen.feature;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.block.ModBlocks;
import com.coolerpromc.ancientcreature.block.custom.RockPileBlock;
import com.coolerpromc.ancientcreature.tag.ModBlockTags;
import com.coolerpromc.ancientcreature.worldgen.feature.cusom.FossilSeamFeature;
import com.coolerpromc.ancientcreature.worldgen.feature.cusom.SimpleWaterloggableBlockFeature;
import com.coolerpromc.ancientcreature.worldgen.structure.ModProcessorLists;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.resources.Identifier;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.FossilFeature;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.BlockReplacement;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.OreFeature;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;

import java.util.ArrayList;
import java.util.List;

public final class ModConfiguredFeatures {
    public static final ResourceKey<Feature> FOREST_ROCKS = ResourceKey.create(Registries.FEATURE, Constants.id("forest_rocks"));
    public static final ResourceKey<Feature> FOSSIL_ORE = ResourceKey.create(Registries.FEATURE, Constants.id("fossil_ore"));
    public static final ResourceKey<Feature> DEEPSLATE_FOSSIL_ORE = ResourceKey.create(Registries.FEATURE, Constants.id("deepslate_fossil_ore"));
    public static final ResourceKey<Feature> AMBER_ORE = ResourceKey.create(Registries.FEATURE, Constants.id("amber_ore"));
    public static final ResourceKey<Feature> FROZEN_FOSSIL = ResourceKey.create(Registries.FEATURE, Constants.id("frozen_fossil"));
    public static final ResourceKey<Feature> FOSSIL_SEAM = ResourceKey.create(Registries.FEATURE, Constants.id("fossil_seam"));
    public static final ResourceKey<Feature> FOSSIL_BED = ResourceKey.create(Registries.FEATURE, Constants.id("fossil_bed"));

    private ModConfiguredFeatures() {
    }

    public static void bootstrap(BootstrapContext<Feature> context) {
        WeightedList.Builder<BlockState> rocks = WeightedList.<BlockState>builder()
            .add(ModBlocks.ROCK_PILE.defaultBlockState(), 100)
            .add(ModBlocks.ROCK_PILE.defaultBlockState().setValue(RockPileBlock.HAS_FOSSIL, true), 30)
            .add(ModBlocks.ROCK_PILE.defaultBlockState().setValue(RockPileBlock.HAS_EGG, true), 4)
            .add(ModBlocks.ROCK_PILE.defaultBlockState().setValue(RockPileBlock.HAS_EGG, true).setValue(RockPileBlock.HAS_FOSSIL, true), 1);

        context.register(FOREST_ROCKS, new SimpleWaterloggableBlockFeature(new WeightedStateProvider(rocks)));

        RuleTest stoneReplaceable = new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES);
        List<BlockReplacement> fossilOres = List.of(BlockReplacement.replace(stoneReplaceable, ModBlocks.FOSSIL_ORE.defaultBlockState()));
        // Ore veins below size 4 almost never place a block, so every fossil vein uses at least 4.
        context.register(FOSSIL_ORE, new OreFeature(fossilOres, 4));

        RuleTest deepslateReplaceable = new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);
        context.register(DEEPSLATE_FOSSIL_ORE, new OreFeature(List.of(BlockReplacement.replace(deepslateReplaceable, ModBlocks.DEEPSLATE_FOSSIL_ORE.defaultBlockState())), 4));
        context.register(AMBER_ORE, new OreFeature(List.of(BlockReplacement.replace(stoneReplaceable, ModBlocks.AMBER_ORE.defaultBlockState())), 5));
        context.register(FROZEN_FOSSIL, new OreFeature(List.of(BlockReplacement.replace(new TagMatchTest(ModBlockTags.FROZEN_FOSSIL_REPLACEABLES), ModBlocks.FROZEN_FOSSIL.defaultBlockState())), 4));

        WeightedList<BlockState> seam = WeightedList.<BlockState>builder()
            .add(ModBlocks.FOSSIL_ORE.defaultBlockState(), 5)
            .add(Blocks.BONE_BLOCK.defaultBlockState(), 1)
            .build();
        context.register(FOSSIL_SEAM, new FossilSeamFeature(Holder.direct(new WeightedStateProvider(seam)), ModBlockTags.FOSSIL_SEAM_REPLACEABLES, UniformInt.of(4, 9), 24, 3));

        // Vanilla's own fossil skeletons, buried with fossil ore where vanilla puts coal.
        HolderGetter<StructureProcessorList> processorLists = context.lookup(Registries.PROCESSOR_LIST);
        List<Identifier> bones = new ArrayList<>();
        List<Identifier> overlays = new ArrayList<>();
        for (String name : List.of("spine_1", "spine_2", "spine_3", "spine_4", "skull_1", "skull_2", "skull_3", "skull_4")) {
            bones.add(Identifier.withDefaultNamespace("fossil/" + name));
            overlays.add(Identifier.withDefaultNamespace("fossil/" + name + "_coal"));
        }
        context.register(FOSSIL_BED, new FossilFeature(bones, overlays, processorLists.getOrThrow(ModProcessorLists.FOSSIL_BED_BONES), processorLists.getOrThrow(ModProcessorLists.FOSSIL_BED_ORE), 4));
    }
}
