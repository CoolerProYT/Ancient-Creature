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
            ModItems.STONE_CHISEL.key(),
            ModItems.COPPER_CHISEL.key(),
            ModItems.IRON_CHISEL.key(),
            ModItems.GOLDEN_CHISEL.key(),
            ModItems.DIAMOND_CHISEL.key(),
            ModItems.NETHERITE_CHISEL.key()
        );

        tag(ModItemTags.EXTRACTION_FLUIDS).add(
            ModItems.EXTRACTION_FLUID.key()
        );

        // Wolves, cats and other meat eaters accept prehistoric meat like any other.
        tag(ItemTags.MEAT).add(ModItems.RAW_PREHISTORIC_MEAT.key(), ModItems.COOKED_PREHISTORIC_MEAT.key());
        tag(Tags.Items.FOODS_RAW_MEAT).add(ModItems.RAW_PREHISTORIC_MEAT.key());
        tag(Tags.Items.FOODS_COOKED_MEAT).add(ModItems.COOKED_PREHISTORIC_MEAT.key());
        tag(Tags.Items.FEATHERS).add(ModItems.PREHISTORIC_FEATHER.key());
        tag(Tags.Items.LEATHERS).add(ModItems.THICK_HIDE.key());
    }
}
