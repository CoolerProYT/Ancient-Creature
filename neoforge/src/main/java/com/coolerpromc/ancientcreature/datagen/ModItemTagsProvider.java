package com.coolerpromc.ancientcreature.datagen;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.item.ModItems;
import com.coolerpromc.ancientcreature.tag.ModItemTags;
import net.minecraft.tags.ItemTags;
import net.neoforged.neoforge.common.Tags;
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

        tag(ModItemTags.EXTRACTION_FLUIDS).add(
            ModItems.EXTRACTION_FLUID.get()
        );

        // Wolves, cats and other meat eaters accept prehistoric meat like any other.
        tag(ItemTags.MEAT).add(ModItems.RAW_PREHISTORIC_MEAT.get(), ModItems.COOKED_PREHISTORIC_MEAT.get());
        tag(Tags.Items.FOODS_RAW_MEAT).add(ModItems.RAW_PREHISTORIC_MEAT.get());
        tag(Tags.Items.FOODS_COOKED_MEAT).add(ModItems.COOKED_PREHISTORIC_MEAT.get());
        tag(Tags.Items.FEATHERS).add(ModItems.PREHISTORIC_FEATHER.get());
        tag(Tags.Items.LEATHERS).add(ModItems.THICK_HIDE.get());
    }
}
