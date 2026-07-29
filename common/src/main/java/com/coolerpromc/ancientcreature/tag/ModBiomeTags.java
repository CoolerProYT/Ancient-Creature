package com.coolerpromc.ancientcreature.tag;

import com.coolerpromc.ancientcreature.Constants;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

public class ModBiomeTags {
    public static final TagKey<Biome> HAS_PLAINS_DIG_SITE = create("has_structure/plains_dig_site");
    public static final TagKey<Biome> HAS_ARID_DIG_SITE = create("has_structure/arid_dig_site");
    public static final TagKey<Biome> HAS_SAVANNA_DIG_SITE = create("has_structure/savanna_dig_site");
    public static final TagKey<Biome> HAS_BADLANDS_DIG_SITE = create("has_structure/badlands_dig_site");
    public static final TagKey<Biome> HAS_JUNGLE_DIG_SITE = create("has_structure/jungle_dig_site");
    public static final TagKey<Biome> HAS_COLD_DIG_SITE = create("has_structure/cold_dig_site");
    public static final TagKey<Biome> HAS_SWAMP_DIG_SITE = create("has_structure/swamp_dig_site");
    public static final TagKey<Biome> HAS_HIGHLAND_DIG_SITE = create("has_structure/highland_dig_site");
    public static final TagKey<Biome> HAS_COASTAL_DIG_SITE = create("has_structure/coastal_dig_site");
    public static final TagKey<Biome> SPAWNS_TRICERATOPS = create("spawns_triceratops");

    private static TagKey<Biome> create(String name){
        return TagKey.create(Registries.BIOME, Constants.id(name));
    }
}
