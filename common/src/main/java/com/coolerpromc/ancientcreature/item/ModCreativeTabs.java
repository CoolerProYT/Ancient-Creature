package com.coolerpromc.ancientcreature.item;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.block.ModBlocks;
import com.coolerpromc.ancientcreature.data.component.ModDataComponents;
import com.coolerpromc.ancientcreature.data.component.custom.FossilCompleteness;
import com.coolerpromc.ancientcreature.data.component.custom.FossilPart;
import com.coolerpromc.ancientcreature.platform.Services;
import com.coolerpromc.ancientcreature.platform.util.RegistryHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class ModCreativeTabs {
    public static final RegistryHandler<CreativeModeTab, CreativeModeTab> TAB = Services.REGISTRY.registerCreativeTab("ancient_creature", ModItems.STONE_CHISEL::toStack, Component.translatable("tab.ancientcreature.ancient_creature"), (output, parameters) -> {
        output.accept(ModItems.STONE_CHISEL);
        output.accept(ModItems.COPPER_CHISEL);
        output.accept(ModItems.IRON_CHISEL);
        output.accept(ModItems.GOLDEN_CHISEL);
        output.accept(ModItems.DIAMOND_CHISEL);
        output.accept(ModItems.NETHERITE_CHISEL);
        output.accept(ModItems.EGG_FOSSIL);
        output.accept(ModBlocks.FOSSIL_ORE);
        output.accept(ModBlocks.ROCK_PILE);

        for (FossilPart value : FossilPart.values()) {
            ItemStack fossilFragment = ModItems.FOSSIL_FRAGMENT.toStack();
            fossilFragment.set(ModDataComponents.FOSSIL_PART.get(), value);
            fossilFragment.set(ModDataComponents.FOSSIL_COMPLETENESS.get(), new FossilCompleteness(1f));
            output.accept(fossilFragment);

            ItemStack clean = fossilFragment.copy();
            clean.set(ModDataComponents.IS_DIRTY.get(), false);
            output.accept(clean);
        }
    });

    public static void init(){
        Constants.LOG.info("Registering creative tabs.");
    }
}
