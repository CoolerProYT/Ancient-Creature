package com.coolerpromc.ancientcreature.block.entity.custom;

import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

import java.util.function.Predicate;
import java.util.stream.IntStream;

/**
 * What hoppers and pipes see of one machine container: they may insert whatever the slot accepts, but
 * only take out what the machine allows (finished products), so automation cannot drain input slots.
 */
public final class MachineSideContainer implements WorldlyContainer {
    private final Container delegate;
    private final Predicate<ItemStack> extractable;
    private final int[] slots;

    private MachineSideContainer(Container delegate, Predicate<ItemStack> extractable) {
        this.delegate = delegate;
        this.extractable = extractable;
        this.slots = IntStream.range(0, delegate.getContainerSize()).toArray();
    }

    public static Container of(Container container, Predicate<ItemStack> extractable) {
        return new MachineSideContainer(container, extractable);
    }

    @Override
    public int[] getSlotsForFace(Direction direction) {
        return this.slots;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction direction) {
        return this.delegate.canPlaceItem(slot, stack);
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction direction) {
        return this.extractable.test(stack);
    }

    @Override
    public int getContainerSize() {
        return this.delegate.getContainerSize();
    }

    @Override
    public boolean isEmpty() {
        return this.delegate.isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        return this.delegate.getItem(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int count) {
        return this.delegate.removeItem(slot, count);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return this.delegate.removeItemNoUpdate(slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        this.delegate.setItem(slot, stack);
    }

    @Override
    public int getMaxStackSize() {
        return this.delegate.getMaxStackSize();
    }

    @Override
    public void setChanged() {
        this.delegate.setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return this.delegate.stillValid(player);
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return this.delegate.canPlaceItem(slot, stack);
    }

    @Override
    public void clearContent() {
        this.delegate.clearContent();
    }
}
