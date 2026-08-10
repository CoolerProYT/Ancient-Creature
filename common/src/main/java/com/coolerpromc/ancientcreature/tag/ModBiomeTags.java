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
    public static final TagKey<Biome> SPAWNS_ANKYLOSAURUS = create("spawns_ankylosaurus");
    public static final TagKey<Biome> SPAWNS_ARGENTINOSAURUS = create("spawns_argentinosaurus");
    public static final TagKey<Biome> SPAWNS_ARTHROPLEURA = create("spawns_arthropleura");
    public static final TagKey<Biome> SPAWNS_BRACHIOSAURUS = create("spawns_brachiosaurus");
    public static final TagKey<Biome> SPAWNS_CARNOTAURUS = create("spawns_carnotaurus");
    public static final TagKey<Biome> SPAWNS_DEINONYCHUS = create("spawns_deinonychus");
    public static final TagKey<Biome> SPAWNS_DILOPHOSAURUS = create("spawns_dilophosaurus");
    public static final TagKey<Biome> SPAWNS_DIRE_WOLF = create("spawns_dire_wolf");
    public static final TagKey<Biome> SPAWNS_DUNKLEOSTEUS = create("spawns_dunkleosteus");
    public static final TagKey<Biome> SPAWNS_MEGALODON = create("spawns_megalodon");
    public static final TagKey<Biome> SPAWNS_MOSASAURUS = create("spawns_mosasaurus");
    public static final TagKey<Biome> SPAWNS_PARASAUROLOPHUS = create("spawns_parasaurolophus");
    public static final TagKey<Biome> SPAWNS_PLESIOSAURUS = create("spawns_plesiosaurus");
    public static final TagKey<Biome> SPAWNS_PTERANODON = create("spawns_pteranodon");
    public static final TagKey<Biome> SPAWNS_QUETZALCOATLUS = create("spawns_quetzalcoatlus");
    public static final TagKey<Biome> SPAWNS_SMILODON = create("spawns_smilodon");
    public static final TagKey<Biome> SPAWNS_SPINOSAURUS = create("spawns_spinosaurus");
    public static final TagKey<Biome> SPAWNS_STEGOSAURUS = create("spawns_stegosaurus");
    public static final TagKey<Biome> SPAWNS_TRICERATOPS = create("spawns_triceratops");
    public static final TagKey<Biome> SPAWNS_TYRANNOSAURUS_REX = create("spawns_tyrannosaurus_rex");
    public static final TagKey<Biome> SPAWNS_VELOCIRAPTOR = create("spawns_velociraptor");
    public static final TagKey<Biome> SPAWNS_WOOLLY_MAMMOTH = create("spawns_woolly_mammoth");
    public static final TagKey<Biome> SPAWNS_WOOLLY_RHINOCEROS = create("spawns_woolly_rhinoceros");

    private static TagKey<Biome> create(String name){
        return TagKey.create(Registries.BIOME, Constants.id(name));
    }
}
