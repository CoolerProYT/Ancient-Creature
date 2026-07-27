package com.coolerpromc.ancientcreature.platform.util;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

@FunctionalInterface
public interface CreativeTabOutput {
    void accept(ItemStack itemStack);

    default void accept(ItemLike itemLike){
        accept(new ItemStack(itemLike));
    }
}