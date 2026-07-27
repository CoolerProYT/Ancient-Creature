package com.coolerpromc.ancientcreature.tag;

import com.coolerpromc.ancientcreature.Constants;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

public class ModBiomeTags {
    public static TagKey<Biome> HAS_PLAINS_DIG_SITE = create("has_structure/plains_dig_site");

    public static TagKey<Biome> create(String name){
        return TagKey.create(Registries.BIOME, Constants.id(name));
    }
}
