package com.coolerpromc.ancientcreature.menu.slot;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ContainerSlot extends Slot {
    public ContainerSlot(Container container, int slot, int x, int y) {
        super(container, slot, x, y);
    }

    @Override
    public boolean mayPlace(ItemStack itemStack) {
        return container.canPlaceItem(getContainerSlot(), itemStack);
    }
}
