package com.coolerpromc.ancientcreature.worldgen.feature;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.platform.Services;
import com.coolerpromc.ancientcreature.platform.util.RegistryHandler;
import com.coolerpromc.ancientcreature.worldgen.feature.cusom.SimpleWaterloggableBlockFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;

public class ModFeatures {
    public static final RegistryHandler<Feature<?>, Feature<SimpleBlockConfiguration>> WATERLOGGABLE_BLOCK = Services.REGISTRY.registerFeature("waterloggable_block", new SimpleWaterloggableBlockFeature(SimpleBlockConfiguration.CODEC));

    public static void init(){
        Constants.LOG.info("Registering features.");
    }
}
