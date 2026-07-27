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

    public static void boostrap(BootstrapContext<StructureSet> context){
        context.register(PLAINS_DIG_SITE, plainsDigSite(context));
    }

    private static StructureSet plainsDigSite(BootstrapContext<StructureSet> context){
        HolderGetter<Structure> structures = context.lookup(Registries.STRUCTURE);

        Holder<Structure> structure = structures.getOrThrow(ModStructures.PLAINS_DIG_SITE);

        return new StructureSet(structure, new RandomSpreadStructurePlacement(40, 20, RandomSpreadType.TRIANGULAR, 42170427));
    }

    private static ResourceKey<StructureSet> register(String location){
        return ResourceKey.create(Registries.STRUCTURE_SET, Constants.id(location));
    }
}
