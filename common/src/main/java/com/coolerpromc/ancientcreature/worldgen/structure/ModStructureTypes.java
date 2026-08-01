package com.coolerpromc.ancientcreature.worldgen.structure;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.platform.Services;
import com.coolerpromc.ancientcreature.platform.util.RegistryHandler;
import com.coolerpromc.ancientcreature.worldgen.structure.custom.FlatJigsawStructure;
import net.minecraft.world.level.levelgen.structure.StructureType;

public class ModStructureTypes {
    public static final RegistryHandler<StructureType<?>, StructureType<FlatJigsawStructure>> FLAT_JIGSAW = Services.REGISTRY.registerStructureType("flat_jigsaw", FlatJigsawStructure.CODEC);

    public static void init(){
        Constants.LOG.info("Registering structure type.");
    }
}
