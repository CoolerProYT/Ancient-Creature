package com.coolerpromc.ancientcreature.platform.util;

import net.minecraft.world.item.ItemStack;

@FunctionalInterface
public interface CreativeTabOutput {
    void accept(ItemStack itemLike);
}