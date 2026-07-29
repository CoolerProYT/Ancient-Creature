package com.coolerpromc.ancientcreature.loot;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.loot.custom.SetFossilCompletenessFunction;
import com.coolerpromc.ancientcreature.loot.custom.SetFossilPartFunction;
import com.coolerpromc.ancientcreature.loot.custom.SetFossilSpeciesFunction;
import com.coolerpromc.ancientcreature.platform.Services;
import com.coolerpromc.ancientcreature.platform.util.RegistryHandler;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;

public class ModLootFunctions {
    public static final RegistryHandler<MapCodec<? extends LootItemFunction>, MapCodec<SetFossilCompletenessFunction>> SET_FOSSIL_COMPLETENESS = Services.REGISTRY.registerLootItemFunction("set_fossil_completeness", SetFossilCompletenessFunction.CODEC);
    public static final RegistryHandler<MapCodec<? extends LootItemFunction>, MapCodec<SetFossilPartFunction>> SET_FOSSIL_PART = Services.REGISTRY.registerLootItemFunction("set_fossil_part", SetFossilPartFunction.CODEC);
    public static final RegistryHandler<MapCodec<? extends LootItemFunction>, MapCodec<SetFossilSpeciesFunction>> SET_FOSSIL_SPECIES = Services.REGISTRY.registerLootItemFunction("set_fossil_species", SetFossilSpeciesFunction.CODEC);

    public static void init(){
        Constants.LOG.info("Registering loot functions.");
    }
}
