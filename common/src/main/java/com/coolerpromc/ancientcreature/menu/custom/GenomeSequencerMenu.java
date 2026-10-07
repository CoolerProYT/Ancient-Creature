package com.coolerpromc.ancientcreature.menu.custom;

import com.coolerpromc.ancientcreature.block.ModBlocks;
import com.coolerpromc.ancientcreature.block.entity.custom.DNAExtractorBlockEntity;
import com.coolerpromc.ancientcreature.block.entity.custom.GenomeSequencerBlockEntity;
import com.coolerpromc.ancientcreature.menu.MachineMenu;
import com.coolerpromc.ancientcreature.menu.ModMenus;
import com.coolerpromc.ancientcreature.menu.slot.ContainerSlot;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;

public class GenomeSequencerMenu extends MachineMenu {
    private final Level level;
    private final BlockPos pos;
    private final ContainerData data;

    public GenomeSequencerMenu(int containerId, Inventory inventory, BlockPos pos) {
        this(containerId, inventory, inventory.player, (GenomeSequencerBlockEntity) inventory.player.level().getBlockEntity(pos), new SimpleContainerData(2));
    }

    public GenomeSequencerMenu(int containerId, Inventory inventory, Player player, GenomeSequencerBlockEntity blockEntity, ContainerData data) {
        super(ModMenus.GENOME_SEQUENCER.get(), containerId);
        this.level = player.level();
        this.pos = blockEntity.getBlockPos();
        this.data = data;

        addDataSlots(data);
        addPlayerInventory(inventory);

        addSlot(new ContainerSlot(blockEntity.getDnaSampleContainer(), 0, 34, 34));
        addSlot(new ContainerSlot(blockEntity.getCartridgeContainer(), 0, 124, 34));
        beginOutputs();
        addUpgradeSlots(blockEntity.getUpgrades());
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(this.level, this.pos), player, ModBlocks.GENOME_SEQUENCER.getBlock());
    }

    public int getProgress(){
        return this.data.get(DNAExtractorBlockEntity.DATA_PROGRESS);
    }

    public int getMaxProgress(){
        return this.data.get(DNAExtractorBlockEntity.DATA_MAX_PROGRESS);
    }

    public int getProgressWidth(){
        return progressWidth(getProgress(), getMaxProgress(), 24);
    }
}
