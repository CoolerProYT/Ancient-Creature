package com.coolerpromc.ancientcreature.loot;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.loot.custom.SetFossilDataFunction;
import com.coolerpromc.ancientcreature.platform.Services;
import com.coolerpromc.ancientcreature.platform.util.RegistryHandler;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;

public class ModLootFunctions {
    public static final RegistryHandler<MapCodec<? extends LootItemFunction>, MapCodec<SetFossilDataFunction>> SET_FOSSIL_DATA = Services.REGISTRY.registerLootItemFunction("set_fossil_data", SetFossilDataFunction.CODEC);

    public static void init(){
        Constants.LOG.info("Registering loot functions.");
    }
}
