package com.coolerpromc.ancientcreature.platform.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

@FunctionalInterface
public interface MenuFactory<T extends AbstractContainerMenu> {
    T create(int syncId, Inventory inventory, BlockPos blockPos);
}