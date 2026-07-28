package com.coolerpromc.ancientcreature.item;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.block.ModBlocks;
import com.coolerpromc.ancientcreature.platform.Services;
import com.coolerpromc.ancientcreature.platform.util.RegistryHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;

public class ModCreativeTabs {
    public static final RegistryHandler<CreativeModeTab, CreativeModeTab> TAB = Services.REGISTRY.registerCreativeTab("ancient_creature", ModItems.CHISEL::toStack, Component.translatable("tab.ancientcreature.ancient_creature"), (output, parameters) -> {
        output.accept(ModItems.CHISEL);
        output.accept(ModItems.EGG_FOSSIL);
        output.accept(ModBlocks.FOSSIL_ORE);
        output.accept(ModBlocks.ROCK_PILE);
    });

    public static void init(){
        Constants.LOG.info("Registering creative tabs.");
    }
}
