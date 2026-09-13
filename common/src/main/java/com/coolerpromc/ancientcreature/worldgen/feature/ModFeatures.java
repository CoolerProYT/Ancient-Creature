package com.coolerpromc.ancientcreature.worldgen.feature;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.platform.Services;
import com.coolerpromc.ancientcreature.platform.util.RegistryHandler;
import com.coolerpromc.ancientcreature.worldgen.feature.cusom.SimpleWaterloggableBlockFeature;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.levelgen.feature.Feature;

public class ModFeatures {
    public static final RegistryHandler<MapCodec<? extends Feature>, MapCodec<SimpleWaterloggableBlockFeature>> WATERLOGGABLE_BLOCK = Services.REGISTRY.registerFeature("waterloggable_block", SimpleWaterloggableBlockFeature.CODEC);

    public static void init(){
        Constants.LOG.info("Registering features.");
    }
}
