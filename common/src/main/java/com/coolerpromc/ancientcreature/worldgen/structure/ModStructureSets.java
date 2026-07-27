package com.coolerpromc.ancientcreature.worldgen.structure;

import com.coolerpromc.ancientcreature.Constants;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;

public class ModStructureSets {
    public static final ResourceKey<StructureSet> PLAINS_DIG_SITE = register("plains_dig_site");
    public static final ResourceKey<StructureSet> ARID_DIG_SITE = register("arid_dig_site");
    public static final ResourceKey<StructureSet> SAVANNA_DIG_SITE = register("savanna_dig_site");
    public static final ResourceKey<StructureSet> BADLANDS_DIG_SITE = register("badlands_dig_site");
    public static final ResourceKey<StructureSet> JUNGLE_DIG_SITE = register("jungle_dig_site");
    public static final ResourceKey<StructureSet> COLD_DIG_SITE = register("cold_dig_site");
    public static final ResourceKey<StructureSet> SWAMP_DIG_SITE = register("swamp_dig_site");
    public static final ResourceKey<StructureSet> HIGHLAND_DIG_SITE = register("highland_dig_site");
    public static final ResourceKey<StructureSet> COASTAL_DIG_SITE = register("coastal_dig_site");

    public static void boostrap(BootstrapContext<StructureSet> context){
        context.register(PLAINS_DIG_SITE, digSite(context, ModStructures.PLAINS_DIG_SITE, 42170427));
        context.register(ARID_DIG_SITE, digSite(context, ModStructures.ARID_DIG_SITE, 42170428));
        context.register(SAVANNA_DIG_SITE, digSite(context, ModStructures.SAVANNA_DIG_SITE, 42170429));
        context.register(BADLANDS_DIG_SITE, digSite(context, ModStructures.BADLANDS_DIG_SITE, 42170430));
        context.register(JUNGLE_DIG_SITE, digSite(context, ModStructures.JUNGLE_DIG_SITE, 42170431));
        context.register(COLD_DIG_SITE, digSite(context, ModStructures.COLD_DIG_SITE, 42170432));
        context.register(SWAMP_DIG_SITE, digSite(context, ModStructures.SWAMP_DIG_SITE, 42170433));
        context.register(HIGHLAND_DIG_SITE, digSite(context, ModStructures.HIGHLAND_DIG_SITE, 42170434));
        context.register(COASTAL_DIG_SITE, digSite(context, ModStructures.COASTAL_DIG_SITE, 42170435));
    }

    private static StructureSet digSite(BootstrapContext<StructureSet> context, ResourceKey<Structure> structureKey, int salt){
        HolderGetter<Structure> structures = context.lookup(Registries.STRUCTURE);

        Holder<Structure> structure = structures.getOrThrow(structureKey);

        return new StructureSet(structure, new RandomSpreadStructurePlacement(40, 20, RandomSpreadType.TRIANGULAR, salt));
    }

    private static ResourceKey<StructureSet> register(String location){
        return ResourceKey.create(Registries.STRUCTURE_SET, Constants.id(location));
    }
}
