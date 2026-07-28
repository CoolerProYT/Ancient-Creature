package com.coolerpromc.ancientcreature.datagen;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.item.ModItems;
import com.coolerpromc.ancientcreature.tag.ModItemTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ItemTagsProvider;

import java.util.concurrent.CompletableFuture;

public class ModItemTagsProvider extends ItemTagsProvider {
    public ModItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, Constants.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(ModItemTags.CHISELS).add(
            ModItems.STONE_CHISEL.get(),
            ModItems.COPPER_CHISEL.get(),
            ModItems.IRON_CHISEL.get(),
            ModItems.GOLDEN_CHISEL.get(),
            ModItems.DIAMOND_CHISEL.get(),
            ModItems.NETHERITE_CHISEL.get()
        );
    }
}
