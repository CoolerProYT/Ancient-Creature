package com.coolerpromc.ancientcreature.datagen;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.tag.ModBiomeTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraft.world.level.biome.Biomes;

import java.util.concurrent.CompletableFuture;

public class ModBiomeTagsProvider extends BiomeTagsProvider {
    public ModBiomeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, Constants.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(ModBiomeTags.HAS_PLAINS_DIG_SITE)
                .add(Biomes.PLAINS)
                .add(Biomes.SUNFLOWER_PLAINS)
                .add(Biomes.FOREST)
                .add(Biomes.FLOWER_FOREST)
                .add(Biomes.BIRCH_FOREST)
                .add(Biomes.OLD_GROWTH_BIRCH_FOREST)
                .add(Biomes.DARK_FOREST)
                .add(Biomes.TAIGA)
                .add(Biomes.OLD_GROWTH_PINE_TAIGA)
                .add(Biomes.OLD_GROWTH_SPRUCE_TAIGA);

        tag(ModBiomeTags.HAS_ARID_DIG_SITE)
                .add(Biomes.DESERT);

        tag(ModBiomeTags.HAS_SAVANNA_DIG_SITE)
                .add(Biomes.SAVANNA)
                .add(Biomes.SAVANNA_PLATEAU)
                .add(Biomes.WINDSWEPT_SAVANNA);

        tag(ModBiomeTags.HAS_BADLANDS_DIG_SITE)
                .add(Biomes.BADLANDS)
                .add(Biomes.WOODED_BADLANDS)
                .add(Biomes.ERODED_BADLANDS);

        tag(ModBiomeTags.HAS_JUNGLE_DIG_SITE)
                .add(Biomes.JUNGLE)
                .add(Biomes.SPARSE_JUNGLE)
                .add(Biomes.BAMBOO_JUNGLE);

        tag(ModBiomeTags.HAS_COLD_DIG_SITE)
                .add(Biomes.SNOWY_PLAINS)
                .add(Biomes.ICE_SPIKES)
                .add(Biomes.SNOWY_TAIGA)
                .add(Biomes.GROVE)
                .add(Biomes.SNOWY_BEACH);

        tag(ModBiomeTags.HAS_SWAMP_DIG_SITE)
                .add(Biomes.SWAMP)
                .add(Biomes.MANGROVE_SWAMP);

        tag(ModBiomeTags.HAS_HIGHLAND_DIG_SITE)
                .add(Biomes.MEADOW)
                .add(Biomes.CHERRY_GROVE)
                .add(Biomes.WINDSWEPT_HILLS)
                .add(Biomes.WINDSWEPT_FOREST)
                .add(Biomes.WINDSWEPT_GRAVELLY_HILLS)
                .add(Biomes.STONY_SHORE);

        tag(ModBiomeTags.HAS_COASTAL_DIG_SITE)
                .add(Biomes.BEACH);
    }
}
