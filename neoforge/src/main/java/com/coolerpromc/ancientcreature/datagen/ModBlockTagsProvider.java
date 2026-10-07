package com.coolerpromc.ancientcreature.datagen;

import net.minecraft.world.level.block.Blocks;
import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.block.ModBlocks;
import com.coolerpromc.ancientcreature.tag.ModBlockTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagsProvider extends BlockTagsProvider {
    public ModBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, Constants.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(ModBlockTags.MINEABLE_WITH_CHISEL)
            .add(ModBlocks.ROCK_PILE.getBlock())
            .add(ModBlocks.FOSSIL_ORE.getBlock())
            .add(ModBlocks.DEEPSLATE_FOSSIL_ORE.getBlock())
            .add(ModBlocks.AMBER_ORE.getBlock())
            .add(ModBlocks.FROZEN_FOSSIL.getBlock());

        tag(BlockTags.MINEABLE_WITH_AXE)
            .add(ModBlocks.SIFTER.getBlock());

        tag(ModBlockTags.FOSSIL_SEAM_REPLACEABLES)
            .addTag(BlockTags.TERRACOTTA)
            .add(Blocks.STONE, Blocks.ANDESITE, Blocks.DIORITE,
                Blocks.GRANITE, Blocks.TUFF, Blocks.CALCITE,
                Blocks.SANDSTONE, Blocks.RED_SANDSTONE);

        tag(ModBlockTags.FROZEN_FOSSIL_REPLACEABLES)
            .add(Blocks.PACKED_ICE, Blocks.BLUE_ICE);

        tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .add(ModBlocks.FOSSIL_CLEANING_TABLE.getBlock())
            .add(ModBlocks.FOSSIL_IDENTIFICATION_CHAMBER.getBlock())
            .add(ModBlocks.DNA_EXTRACTOR.getBlock())
            .add(ModBlocks.GENOME_SEQUENCER.getBlock())
            .add(ModBlocks.EMBRYOGENESIS_CHAMBER.getBlock())
            .add(ModBlocks.INCUBATOR.getBlock())
            .add(ModBlocks.REINFORCED_FENCE.getBlock())
            .add(ModBlocks.REINFORCED_FENCE_GATE.getBlock())
            .add(ModBlocks.ELECTRIC_FENCE.getBlock());

        tag(BlockTags.NEEDS_IRON_TOOL)
            .add(ModBlocks.REINFORCED_FENCE.getBlock())
            .add(ModBlocks.REINFORCED_FENCE_GATE.getBlock());

        // Fence tags make the pathfinder treat these as impassable, like any fence.
        tag(BlockTags.FENCES)
            .add(ModBlocks.REINFORCED_FENCE.getBlock())
            .add(ModBlocks.ELECTRIC_FENCE.getBlock());
        tag(BlockTags.FENCE_GATES)
            .add(ModBlocks.REINFORCED_FENCE_GATE.getBlock());

        // Wooden fences and gates, walls and glass; the reinforced and electric fences are never in here.
        tag(ModBlockTags.ENCLOSURE_BREAKABLE)
            .addTag(BlockTags.WOODEN_FENCES)
            .addTag(BlockTags.WALLS)
            .addTag(BlockTags.IMPERMEABLE)
            .add(net.minecraft.world.level.block.Blocks.GLASS_PANE)
            .add(net.minecraft.world.level.block.Blocks.OAK_FENCE_GATE,
                net.minecraft.world.level.block.Blocks.SPRUCE_FENCE_GATE,
                net.minecraft.world.level.block.Blocks.BIRCH_FENCE_GATE,
                net.minecraft.world.level.block.Blocks.JUNGLE_FENCE_GATE,
                net.minecraft.world.level.block.Blocks.ACACIA_FENCE_GATE,
                net.minecraft.world.level.block.Blocks.DARK_OAK_FENCE_GATE,
                net.minecraft.world.level.block.Blocks.MANGROVE_FENCE_GATE,
                net.minecraft.world.level.block.Blocks.CHERRY_FENCE_GATE,
                net.minecraft.world.level.block.Blocks.BAMBOO_FENCE_GATE,
                net.minecraft.world.level.block.Blocks.PALE_OAK_FENCE_GATE);

        tag(ModBlockTags.CREATURE_DESTROYABLE)
            .addTag(BlockTags.LOGS)
            .addTag(BlockTags.LEAVES);
    }
}
