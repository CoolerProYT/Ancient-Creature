package com.coolerpromc.ancientcreature.datagen;

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
            .add(ModBlocks.FOSSIL_ORE.getBlock());

        tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .add(ModBlocks.FOSSIL_CLEANING_TABLE.getBlock())
            .add(ModBlocks.FOSSIL_IDENTIFICATION_CHAMBER.getBlock())
            .add(ModBlocks.DNA_EXTRACTOR.getBlock())
            .add(ModBlocks.GENOME_SEQUENCER.getBlock())
            .add(ModBlocks.EMBRYOGENESIS_CHAMBER.getBlock())
            .add(ModBlocks.INCUBATOR.getBlock());
    }
}
