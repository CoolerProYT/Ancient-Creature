package com.coolerpromc.ancientcreature.datagen;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.tag.ModStructureTags;
import com.coolerpromc.ancientcreature.worldgen.structure.ModStructures;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.StructureTagsProvider;

import java.util.concurrent.CompletableFuture;

public class ModStructureTagsProvider extends StructureTagsProvider {
    public ModStructureTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, Constants.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(ModStructureTags.DIG_SITES).add(
            ModStructures.PLAINS_DIG_SITE,
            ModStructures.ARID_DIG_SITE,
            ModStructures.SAVANNA_DIG_SITE,
            ModStructures.BADLANDS_DIG_SITE,
            ModStructures.JUNGLE_DIG_SITE,
            ModStructures.COLD_DIG_SITE,
            ModStructures.SWAMP_DIG_SITE,
            ModStructures.HIGHLAND_DIG_SITE,
            ModStructures.COASTAL_DIG_SITE
        );
    }
}
