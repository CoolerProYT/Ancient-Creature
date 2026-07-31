package com.coolerpromc.ancientcreature.menu.custom;

import com.coolerpromc.ancientcreature.block.ModBlocks;
import com.coolerpromc.ancientcreature.block.entity.custom.FossilCleaningTableBlockEntity;
import com.coolerpromc.ancientcreature.block.entity.custom.FossilIdentificationChamberBlockEntity;
import com.coolerpromc.ancientcreature.menu.ModMenus;
import com.coolerpromc.ancientcreature.menu.slot.ContainerSlot;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;

public class FossilIdentificationChamberMenu extends AbstractContainerMenu {
    private final Level level;
    private final BlockPos pos;
    private final ContainerData data;

    public FossilIdentificationChamberMenu(int containerId, Inventory inventory, BlockPos pos) {
        this(containerId, inventory, inventory.player, (FossilIdentificationChamberBlockEntity) inventory.player.level().getBlockEntity(pos), new SimpleContainerData(2));
    }

    public FossilIdentificationChamberMenu(int containerId, Inventory inventory, Player player, FossilIdentificationChamberBlockEntity blockEntity, ContainerData data) {
        super(ModMenus.FOSSIL_IDENTIFICATION_CHAMBER.get(), containerId);
        this.level = player.level();
        this.pos = blockEntity.getBlockPos();
        this.data = data;

        addDataSlots(data);
        addInventoryHotbarSlots(inventory, 8, 84 + 58);
        addInventoryExtendedSlots(inventory, 8, 84);

        addSlot(new ContainerSlot(blockEntity.getInputContainer(), 0, 44, 34));
        addSlot(new ContainerSlot(blockEntity.getOutputContainer(), 0, 116, 34));
    }

    @Override
    public @NonNull ItemStack quickMoveStack(@NonNull Player player, int slotIndex) {
        ItemStack clicked = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);
        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();
            clicked = stack.copy();
            if (slotIndex < 9){
                if (!this.moveItemStackTo(stack, 36, this.slots.size() - 1, false)){
                    if (!this.moveItemStackTo(stack, 9, 36, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            }
            else if (slotIndex < 36) {
                if (!this.moveItemStackTo(stack, 36, this.slots.size() - 1, false)) {
                    if (!this.moveItemStackTo(stack, 0, 9, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            } else if (!this.moveItemStackTo(stack, 0, 36, false)) {
                return ItemStack.EMPTY;
            }

            if (stack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (stack.getCount() == clicked.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, stack);
        }

        return clicked;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(this.level, this.pos), player, ModBlocks.FOSSIL_IDENTIFICATION_CHAMBER.getBlock());
    }

    public int getProgress(){
        return this.data.get(FossilIdentificationChamberBlockEntity.DATA_PROGRESS);
    }

    public int getMaxProgress(){
        return this.data.get(FossilIdentificationChamberBlockEntity.DATA_MAX_PROGRESS);
    }

    public int getProgressWidth(){
        return (int) (((float)getProgress() / (float) getMaxProgress()) * 24);
    }
}
