package com.coolerpromc.ancientcreature.block.entity.upgrade;

import com.coolerpromc.ancientcreature.item.custom.MachineUpgradeItem;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * The two upgrade slots every lab machine has, and what the modules in them do.
 *
 * <p>Effects stack per module: two speed modules are stronger than one, a speed and a precision module
 * give one of each.
 */
public final class MachineUpgrades {
    public static final int SLOTS = 2;
    private static final String TAG = "upgrades";

    private final SimpleContainer container;

    public MachineUpgrades(Runnable onChanged) {
        this.container = new SimpleContainer(SLOTS) {
            @Override
            public boolean canPlaceItem(int slot, ItemStack stack) {
                return stack.getItem() instanceof MachineUpgradeItem;
            }

            @Override
            public int getMaxStackSize() {
                return 1;
            }

            @Override
            public void setChanged() {
                super.setChanged();
                onChanged.run();
            }
        };
    }

    public SimpleContainer container() {
        return this.container;
    }

    public int count(MachineUpgradeItem.Kind kind) {
        int count = 0;
        for (ItemStack stack : this.container.getItems()) {
            if (stack.getItem() instanceof MachineUpgradeItem item && item.kind() == kind) {
                count++;
            }
        }
        return count;
    }

    /** Processing time with speed modules: each removes a third of what is left. */
    public int scaleTime(int baseTicks) {
        double factor = Math.pow(2.0 / 3.0, this.count(MachineUpgradeItem.Kind.SPEED));
        return Math.max(1, (int) Math.round(baseTicks * factor));
    }

    public int precision() {
        return this.count(MachineUpgradeItem.Kind.PRECISION);
    }

    /** Whether efficiency modules save this use of a consumable (25% per module). */
    public boolean saves(RandomSource random) {
        int modules = this.count(MachineUpgradeItem.Kind.EFFICIENCY);
        return modules > 0 && random.nextFloat() < 0.25F * modules;
    }

    public void save(ValueOutput output) {
        ContainerHelper.saveAllItems(output.child(TAG), this.container.getItems());
    }

    public void load(ValueInput input) {
        ContainerHelper.loadAllItems(input.childOrEmpty(TAG), this.container.getItems());
    }

    public void drop(Level level, BlockPos pos) {
        Containers.dropContents(level, pos, this.container);
    }
}
