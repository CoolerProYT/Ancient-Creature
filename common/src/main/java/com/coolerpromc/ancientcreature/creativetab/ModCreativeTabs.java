package com.coolerpromc.ancientcreature.creativetab;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.item.ModItems;
import com.coolerpromc.ancientcreature.platform.Services;
import com.coolerpromc.ancientcreature.platform.util.RegistryHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;

public class ModCreativeTabs {
    public static final RegistryHandler<CreativeModeTab, CreativeModeTab> TAB = Services.REGISTRY.registerCreativeTab("ancient_creature", ModItems.STONE_CHISEL::toStack, Component.translatable("tab.ancientcreature.ancient_creature"), (output, parameters) -> {});

    public static void init(){
        Constants.LOG.info("Registering creative tabs.");
    }
}
