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
            .add(ModBlocks.ROCK_PILE.block().key())
            .add(ModBlocks.FOSSIL_ORE.block().key())
            .add(ModBlocks.DEEPSLATE_FOSSIL_ORE.block().key())
            .add(ModBlocks.AMBER_ORE.block().key())
            .add(ModBlocks.FROZEN_FOSSIL.block().key());

        tag(BlockTags.MINEABLE_WITH_AXE)
            .add(ModBlocks.SIFTER.block().key());

        tag(ModBlockTags.FOSSIL_SEAM_REPLACEABLES)
            .addTag(BlockTags.TERRACOTTA)
            .add(Blocks.STONE.builtInRegistryHolder().key(), Blocks.ANDESITE.builtInRegistryHolder().key(), Blocks.DIORITE.builtInRegistryHolder().key(),
                Blocks.GRANITE.builtInRegistryHolder().key(), Blocks.TUFF.builtInRegistryHolder().key(), Blocks.CALCITE.builtInRegistryHolder().key(),
                Blocks.SANDSTONE.builtInRegistryHolder().key(), Blocks.RED_SANDSTONE.builtInRegistryHolder().key());

        tag(ModBlockTags.FROZEN_FOSSIL_REPLACEABLES)
            .add(Blocks.PACKED_ICE.builtInRegistryHolder().key(), Blocks.BLUE_ICE.builtInRegistryHolder().key());

        tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .add(ModBlocks.FOSSIL_CLEANING_TABLE.block().key())
            .add(ModBlocks.FOSSIL_IDENTIFICATION_CHAMBER.block().key())
            .add(ModBlocks.DNA_EXTRACTOR.block().key())
            .add(ModBlocks.GENOME_SEQUENCER.block().key())
            .add(ModBlocks.EMBRYOGENESIS_CHAMBER.block().key())
            .add(ModBlocks.INCUBATOR.block().key())
            .add(ModBlocks.REINFORCED_FENCE.block().key())
            .add(ModBlocks.REINFORCED_FENCE_GATE.block().key())
            .add(ModBlocks.ELECTRIC_FENCE.block().key());

        tag(BlockTags.NEEDS_IRON_TOOL)
            .add(ModBlocks.REINFORCED_FENCE.block().key())
            .add(ModBlocks.REINFORCED_FENCE_GATE.block().key());

        // Fence tags make the pathfinder treat these as impassable, like any fence.
        tag(BlockTags.FENCES)
            .add(ModBlocks.REINFORCED_FENCE.block().key())
            .add(ModBlocks.ELECTRIC_FENCE.block().key());
        tag(BlockTags.FENCE_GATES)
            .add(ModBlocks.REINFORCED_FENCE_GATE.block().key());

        // Wooden fences and gates, walls and glass; the reinforced and electric fences are never in here.
        tag(ModBlockTags.ENCLOSURE_BREAKABLE)
            .addTag(BlockTags.WOODEN_FENCES)
            .addTag(BlockTags.WALLS)
            .addTag(BlockTags.IMPERMEABLE)
            .add(net.minecraft.world.level.block.Blocks.GLASS_PANE.builtInRegistryHolder().key())
            .add(net.minecraft.world.level.block.Blocks.OAK_FENCE_GATE.builtInRegistryHolder().key(),
                net.minecraft.world.level.block.Blocks.SPRUCE_FENCE_GATE.builtInRegistryHolder().key(),
                net.minecraft.world.level.block.Blocks.BIRCH_FENCE_GATE.builtInRegistryHolder().key(),
                net.minecraft.world.level.block.Blocks.JUNGLE_FENCE_GATE.builtInRegistryHolder().key(),
                net.minecraft.world.level.block.Blocks.ACACIA_FENCE_GATE.builtInRegistryHolder().key(),
                net.minecraft.world.level.block.Blocks.DARK_OAK_FENCE_GATE.builtInRegistryHolder().key(),
                net.minecraft.world.level.block.Blocks.MANGROVE_FENCE_GATE.builtInRegistryHolder().key(),
                net.minecraft.world.level.block.Blocks.CHERRY_FENCE_GATE.builtInRegistryHolder().key(),
                net.minecraft.world.level.block.Blocks.BAMBOO_FENCE_GATE.builtInRegistryHolder().key(),
                net.minecraft.world.level.block.Blocks.PALE_OAK_FENCE_GATE.builtInRegistryHolder().key());

        tag(ModBlockTags.CREATURE_DESTROYABLE)
            .addTag(BlockTags.LOGS)
            .addTag(BlockTags.LEAVES);
    }
}
