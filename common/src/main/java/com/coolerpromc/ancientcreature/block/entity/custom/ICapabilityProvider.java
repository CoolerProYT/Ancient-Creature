package com.coolerpromc.ancientcreature.block.entity.custom;

import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public interface ICapabilityProvider {
    @Nullable Container getContainerBySide(@Nullable Direction direction);
    Container[] getAllContainers();

    /** Whether automation may take this stack out of the given container; machines allow only finished products. */
    boolean canExtract(Container container, ItemStack stack);

    /** The container a hopper or pipe on this side sees, with extraction limited by {@link #canExtract}. */
    default @Nullable Container getAutomationContainer(@Nullable Direction direction) {
        Container container = this.getContainerBySide(direction);
        return container == null ? null : MachineSideContainer.of(container, stack -> this.canExtract(container, stack));
    }
}
