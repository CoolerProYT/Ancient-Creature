package com.coolerpromc.ancientcreature.menu;

import com.coolerpromc.ancientcreature.block.entity.upgrade.MachineUpgrades;
import com.coolerpromc.ancientcreature.item.custom.MachineUpgradeItem;
import com.coolerpromc.ancientcreature.menu.slot.ContainerSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

/**
 * Shared layout and shift-click handling for the lab machines.
 *
 * <p>Slot order is always: the 36 player slots, the machine's input slots, its output slots, then the
 * two upgrade slots shown on the panel to the right of the screen. Shift-clicking an upgrade module
 * from the inventory goes to the panel; anything else goes to the first input that accepts it; nothing
 * is ever shift-clicked into an output.
 */
public abstract class MachineMenu extends AbstractContainerMenu {
    /** Top-left of the upgrade panel, relative to the screen origin. */
    public static final int PANEL_X = 178;
    public static final int PANEL_Y = 20;
    public static final int PANEL_WIDTH = 26;
    public static final int PANEL_HEIGHT = 48;

    private static final int PLAYER_SLOTS = 36;
    private int outputStart = -1;
    private int upgradeStart = -1;

    protected MachineMenu(MenuType<?> type, int containerId) {
        super(type, containerId);
    }

    protected void addPlayerInventory(Inventory inventory) {
        this.addInventoryHotbarSlots(inventory, 8, 142);
        this.addInventoryExtendedSlots(inventory, 8, 84);
    }

    /** Call after adding the inputs and before adding the outputs. */
    protected void beginOutputs() {
        this.outputStart = this.slots.size();
    }

    /** Call last. */
    protected void addUpgradeSlots(MachineUpgrades upgrades) {
        if (this.outputStart < 0) {
            this.outputStart = this.slots.size();
        }
        this.upgradeStart = this.slots.size();
        this.addSlot(new ContainerSlot(upgrades.container(), 0, PANEL_X + 5, PANEL_Y + 5));
        this.addSlot(new ContainerSlot(upgrades.container(), 1, PANEL_X + 5, PANEL_Y + 25));
    }

    @Override
    public @NonNull ItemStack quickMoveStack(@NonNull Player player, int index) {
        Slot slot = this.slots.get(index);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();
        int inputEnd = this.outputStart < 0 ? this.slots.size() : this.outputStart;

        if (index < PLAYER_SLOTS) {
            boolean moved = false;
            if (stack.getItem() instanceof MachineUpgradeItem && this.upgradeStart >= 0) {
                moved = this.moveItemStackTo(stack, this.upgradeStart, this.upgradeStart + MachineUpgrades.SLOTS, false);
            }
            if (!moved) {
                moved = this.moveItemStackTo(stack, PLAYER_SLOTS, inputEnd, false);
            }
            if (!moved) {
                // shuffle between hotbar (0-8) and the main inventory (9-35)
                moved = index < 9 ? this.moveItemStackTo(stack, 9, PLAYER_SLOTS, false) : this.moveItemStackTo(stack, 0, 9, false);
            }
            if (!moved) {
                return ItemStack.EMPTY;
            }
        } else if (!this.moveItemStackTo(stack, 0, PLAYER_SLOTS, true)) {
            return ItemStack.EMPTY;
        }

        if (stack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        if (stack.getCount() == original.getCount()) {
            return ItemStack.EMPTY;
        }
        slot.onTake(player, stack);
        return original;
    }

    /** Progress bar width for a {@code width}-pixel arrow, safe when nothing is processing. */
    protected static int progressWidth(int progress, int max, int width) {
        if (max <= 0 || progress <= 0) {
            return 0;
        }
        return Math.min(width, (int) ((float) progress / max * width));
    }
}
