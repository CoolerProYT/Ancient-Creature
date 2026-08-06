package com.coolerpromc.ancientcreature.block.entity.custom;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.block.entity.ModBlockEntities;
import com.coolerpromc.ancientcreature.config.ModCommonConfig;
import com.coolerpromc.ancientcreature.data.component.ModDataComponents;
import com.coolerpromc.ancientcreature.data.component.custom.FossilData;
import com.coolerpromc.ancientcreature.item.ModItems;
import com.coolerpromc.ancientcreature.menu.custom.FossilCleaningTableMenu;
import com.coolerpromc.ancientcreature.platform.Services;
import com.coolerpromc.ancientcreature.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.*;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import static com.coolerpromc.ancientcreature.sound.SoundUtils.stopSound;

public class FossilCleaningTableBlockEntity extends BlockEntity implements MenuProvider, ICapabilityProvider {
    public static final int DATA_PROGRESS = 0;
    public static final int DATA_MAX_PROGRESS = 1;

    private final ContainerData data;
    private final SimpleContainer brushContainer = new SimpleContainer(1){
        @Override
        public boolean canPlaceItem(int slot, ItemStack itemStack) {
            return itemStack.is(Services.PLATFORM.brushToolTag());
        }
    };
    private final SimpleContainer fossilContainer = new SimpleContainer(1){
        @Override
        public boolean canPlaceItem(int slot, @NonNull ItemStack itemStack) {
            return isValidFossil(itemStack);
        }
    };
    private final SimpleContainer outputContainer = new SimpleContainer(2){
        @Override
        public boolean canPlaceItem(int slot, @NonNull ItemStack itemStack) {
            return false;
        }
    };
    private int progress = 0;
    private int maxProgress = ModCommonConfig.CONFIG.cleaningTick.get();
    private boolean isCleaning = false;
    private final AnimationState cleaningAnimationState = new AnimationState();

    public FossilCleaningTableBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(ModBlockEntities.FOSSIL_CLEANING_TABLE.get(), worldPosition, blockState);
        this.data = new ContainerData() {
            @Override
            public int get(int dataId) {
                return switch (dataId){
                    case DATA_PROGRESS -> progress;
                    case DATA_MAX_PROGRESS -> maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int dataId, int value) {
                switch (dataId){
                    case DATA_PROGRESS -> progress = value;
                    case DATA_MAX_PROGRESS -> maxProgress = value;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
        ModCommonConfig.CONFIG_SPEC.addReloadListener(() -> {
            this.maxProgress = ModCommonConfig.CONFIG.cleaningTick.get();
        });
    }

    @Override
    public @NonNull Component getDisplayName() {
        return Component.translatable("block.ancientcreature.fossil_cleaning_table");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, @NonNull Inventory inventory, @NonNull Player player) {
        return new FossilCleaningTableMenu(containerId, inventory, player, this, this.data);
    }

    @Override
    protected void saveAdditional(@NonNull ValueOutput output) {
        super.saveAdditional(output);
        ContainerHelper.saveAllItems(output.child("brush"), brushContainer.getItems());
        ContainerHelper.saveAllItems(output.child("fossil"), fossilContainer.getItems());
        ContainerHelper.saveAllItems(output.child("output"), outputContainer.getItems());
        output.putInt("progress", progress);
        output.putInt("maxProgress", maxProgress);
        output.putBoolean("isCleaning", isCleaning);
    }

    @Override
    protected void loadAdditional(@NonNull ValueInput input) {
        super.loadAdditional(input);
        ContainerHelper.loadAllItems(input.childOrEmpty("brush"), brushContainer.getItems());
        ContainerHelper.loadAllItems(input.childOrEmpty("fossil"), fossilContainer.getItems());
        ContainerHelper.loadAllItems(input.childOrEmpty("output"), outputContainer.getItems());
        progress = input.getIntOr("progress", 0);
        maxProgress = input.getIntOr("maxProgress", ModCommonConfig.CONFIG.cleaningTick.get());
        isCleaning = input.getBooleanOr("isCleaning", false);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        try(ProblemReporter.ScopedCollector reporter = new ProblemReporter.ScopedCollector(this.problemPath(), Constants.LOG)){
            TagValueOutput output = TagValueOutput.createWithContext(reporter, registries);
            saveAdditional(output);
            return output.buildResult();
        }
    }
    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    private boolean isValidFossil(ItemStack stack){
        return stack.getOrDefault(ModDataComponents.FOSSIL_DATA.get(), FossilData.EMPTY).isDirty();
    }

    public void tick(Level level, BlockPos blockPos, BlockState state) {
        if (!(level instanceof ServerLevel serverLevel)) return;

        if (canClean()){
            if (progress == 0){
                this.isCleaning = true;
                level.playSound(null, blockPos, ModSounds.CLEANING_TABLE_BRUSH.get(), SoundSource.BLOCKS, 1f, 1f);
                level.sendBlockUpdated(blockPos, state, state, 3);
            }
            else if (progress % 100 == 0 && progress < maxProgress){
                level.playSound(null, blockPos, ModSounds.CLEANING_TABLE_BRUSH.get(), SoundSource.BLOCKS, 1f, 1f);
            }
            progress++;
            setChanged();

            if (progress >= maxProgress){
                cleanAndOutput(serverLevel);
                progress = 0;
                setChanged();
                this.isCleaning = false;
                stopSound(serverLevel, blockPos, ModSounds.CLEANING_TABLE_BRUSH);
                level.sendBlockUpdated(blockPos, state, state, 3);
            }
        }
        else {
            boolean wasWorking = progress != 0;
            progress = 0;
            setChanged();
            this.isCleaning = false;
            if (wasWorking){
                stopSound(serverLevel, blockPos, ModSounds.CLEANING_TABLE_BRUSH);
                level.sendBlockUpdated(blockPos, state, state, 3);
            }
        }
    }

    private void cleanAndOutput(ServerLevel serverLevel) {
        ItemStack fossil = fossilContainer.removeItem(0, 1);
        FossilData fossilData = fossil.get(ModDataComponents.FOSSIL_DATA.get());
        fossil.set(ModDataComponents.FOSSIL_DATA.get(), fossilData.clean());
        brushContainer.getItem(0).hurtAndBreak(1, serverLevel, null, _ -> {});
        outputContainer.addItem(fossil);
        ItemStack dirtFragment = new ItemStack(ModItems.DIRT_FRAGMENT, serverLevel.getRandom().nextIntBetweenInclusive(0, 2));
        outputContainer.addItem(dirtFragment);
    }

    private boolean canClean() {
        return hasBrush() && hasFossil() && hasEnoughOutputSlot();
    }

    private boolean hasEnoughOutputSlot() {
        ItemStack cleanFossil = fossilContainer.getItem(0).copy();
        FossilData fossilData = cleanFossil.get(ModDataComponents.FOSSIL_DATA.get());
        cleanFossil.set(ModDataComponents.FOSSIL_DATA.get(), fossilData.clean());
        ItemStack maxDirtFragment = new ItemStack(ModItems.DIRT_FRAGMENT, 2);

        ItemStack[] simulatedSlots = new ItemStack[outputContainer.getContainerSize()];
        for (int i = 0; i < simulatedSlots.length; i++) {
            simulatedSlots[i] = outputContainer.getItem(i).copy();
        }

        return simulateInsert(simulatedSlots, cleanFossil) && simulateInsert(simulatedSlots, maxDirtFragment);
    }

    private boolean simulateInsert(ItemStack[] slots, ItemStack toInsert) {
        ItemStack remaining = toInsert.copy();

        for (int i = 0; i < slots.length && !remaining.isEmpty(); i++) {
            ItemStack slot = slots[i];
            if (!slot.isEmpty() && ItemStack.isSameItemSameComponents(slot, remaining)) {
                int space = slot.getMaxStackSize() - slot.getCount();
                if (space > 0) {
                    int move = Math.min(space, remaining.getCount());
                    slot.grow(move);
                    remaining.shrink(move);
                }
            }
        }

        for (int i = 0; i < slots.length && !remaining.isEmpty(); i++) {
            if (slots[i].isEmpty()) {
                slots[i] = remaining.copy();
                remaining.setCount(0);
            }
        }

        return remaining.isEmpty();
    }

    private boolean hasFossil() {
        return !fossilContainer.getItem(0).isEmpty();
    }

    private boolean hasBrush() {
        return !brushContainer.getItem(0).isEmpty();
    }

    public SimpleContainer getBrushContainer() {
        return brushContainer;
    }

    public SimpleContainer getFossilContainer() {
        return fossilContainer;
    }

    public SimpleContainer getOutputContainer() {
        return outputContainer;
    }

    @Override
    public void preRemoveSideEffects(@NonNull BlockPos pos, @NonNull BlockState state) {
        assert this.level != null;
        Containers.dropContents(this.level, pos, brushContainer);
        Containers.dropContents(this.level, pos, fossilContainer);
        Containers.dropContents(this.level, pos, outputContainer);
        if (progress > 0 && level instanceof ServerLevel serverLevel){
            stopSound(serverLevel, pos, ModSounds.CLEANING_TABLE_BRUSH);
        }
    }

    public boolean isCleaning() {
        return isCleaning;
    }

    public AnimationState getCleaningAnimationState() {
        return cleaningAnimationState;
    }

    public Container getContainerBySide(@Nullable Direction direction) {
        if (direction == Direction.DOWN){
            return getOutputContainer();
        }
        if (direction == Direction.UP){
            return getBrushContainer();
        }
        return getFossilContainer();
    }

    @Override
    public Container[] getAllContainers() {
        return new SimpleContainer[]{getBrushContainer(), getFossilContainer(), getOutputContainer()};
    }
}
