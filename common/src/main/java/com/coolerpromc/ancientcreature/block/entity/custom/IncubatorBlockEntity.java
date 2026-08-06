package com.coolerpromc.ancientcreature.block.entity.custom;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.block.ModBlocks;
import com.coolerpromc.ancientcreature.block.entity.ModBlockEntities;
import com.coolerpromc.ancientcreature.config.ModCommonConfig;
import com.coolerpromc.ancientcreature.data.component.ModDataComponents;
import com.coolerpromc.ancientcreature.entity.Species;
import com.coolerpromc.ancientcreature.item.ModItems;
import com.coolerpromc.ancientcreature.menu.custom.IncubatorMenu;
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
import org.jspecify.annotations.Nullable;

import static com.coolerpromc.ancientcreature.sound.SoundUtils.stopSound;

public class IncubatorBlockEntity extends BlockEntity implements MenuProvider, ICapabilityProvider {
    public static final int DATA_PROGRESS = 0;
    public static final int DATA_MAX_PROGRESS = 1;

    private final AnimationState processingAnimationState = new AnimationState();
    private final ContainerData data;
    private int progress;
    private int maxProgress = 100;
    private boolean isProcessing;

    private final SimpleContainer inputContainer = new SimpleContainer(1) {
        @Override
        public boolean canPlaceItem(int slot, ItemStack stack) {
            return stack.is(ModItems.FERTILIZED_ANCIENT_EGG.get());
        }

        @Override
        public void setChanged() {
            setMaxProgress();
        }
    };

    private final SimpleContainer outputContainer = new SimpleContainer(1) {
        @Override
        public boolean canPlaceItem(int slot, ItemStack stack) {
            return false;
        }
    };

    public IncubatorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.INCUBATOR.get(), pos, state);
        data = new ContainerData() {
            @Override
            public int get(int id) {
                return switch (id) {
                    case DATA_PROGRESS -> progress;
                    case DATA_MAX_PROGRESS -> maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int id, int value) {
                switch (id) {
                    case DATA_PROGRESS -> progress = value;
                    case DATA_MAX_PROGRESS -> maxProgress = value;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
        ModCommonConfig.CONFIG_SPEC.addReloadListener(this::setMaxProgress);
    }

    private void setMaxProgress(){
        Species species = inputContainer.getItem(0).get(ModDataComponents.SPECIES.get());
        if (species == null) {
            maxProgress = 0;
            return;
        }
        maxProgress = (int) (species.getIncubationTime() * ModCommonConfig.CONFIG.incubationTimeMultiplier.get());
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.ancientcreature.incubator");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new IncubatorMenu(containerId, inventory, player, this, data);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (!(level instanceof ServerLevel serverLevel)) return;

        if (canProcess()){
            if (progress == 0){
                this.isProcessing = true;
                level.playSound(null, pos, ModSounds.INCUBATOR_PROCESSING.get(), SoundSource.BLOCKS, 2f, 1f);
                level.sendBlockUpdated(pos, state, state, 3);
            }
            else if (progress % 100 == 0 && progress < maxProgress){
                level.playSound(null, pos, ModSounds.INCUBATOR_PROCESSING.get(), SoundSource.BLOCKS, 2f, 1f);
            }
            progress++;
            setChanged();

            if (progress >= maxProgress){
                finishExtracting(serverLevel, pos);
                progress = 0;
                this.isProcessing = false;
                setChanged();
                stopSound(serverLevel, pos, ModSounds.INCUBATOR_PROCESSING);
                level.sendBlockUpdated(pos, state, state, 3);
            }
        }
        else {
            boolean wasWorking = progress != 0;
            progress = 0;
            this.isProcessing = false;
            setChanged();
            if (wasWorking){
                stopSound(serverLevel, pos, ModSounds.INCUBATOR_PROCESSING);
                level.sendBlockUpdated(pos, state, state, 3);
            }
        }
    }

    private void finishExtracting(ServerLevel serverLevel, BlockPos pos) {
        ItemStack stack = inputContainer.removeItem(0, 1);
        Species species = stack.get(ModDataComponents.SPECIES.get());
        if (species == null) return;
        ItemStack output = ModItems.BABY_CREATURE_CAPSULE.toStack();
        output.set(ModDataComponents.SPECIES.get(), species);
        outputContainer.addItem(output);
    }

    private boolean canProcess() {
        return !inputContainer.isEmpty() && maxProgress > 0 && outputContainer.isEmpty();
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        if (level == null) return;
        BlockPos placeholderPos = pos.above();
        if (level.getBlockState(placeholderPos).is(ModBlocks.PLACEHOLDER.blockHolder()))
            level.removeBlock(placeholderPos, false);
        Containers.dropContents(level, pos, inputContainer);
        Containers.dropContents(level, pos, outputContainer);
        if (isProcessing && level instanceof ServerLevel serverLevel)
            stopSound(serverLevel, pos, ModSounds.INCUBATOR_PROCESSING);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        ContainerHelper.saveAllItems(output.child("input"), inputContainer.getItems());
        ContainerHelper.saveAllItems(output.child("output"), outputContainer.getItems());
        output.putInt("progress", progress);
        output.putInt("maxProgress", maxProgress);
        output.putBoolean("processing", isProcessing);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        ContainerHelper.loadAllItems(input.childOrEmpty("input"), inputContainer.getItems());
        ContainerHelper.loadAllItems(input.childOrEmpty("output"), outputContainer.getItems());
        progress = input.getIntOr("progress", 0);
        maxProgress = Math.max(1, input.getIntOr("maxProgress", 100));
        isProcessing = input.getBooleanOr("processing", false);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        try (ProblemReporter.ScopedCollector reporter = new ProblemReporter.ScopedCollector(problemPath(), Constants.LOG)) {
            TagValueOutput output = TagValueOutput.createWithContext(reporter, registries);
            saveAdditional(output);
            return output.buildResult();
        }
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public boolean isProcessing() {
        return isProcessing;
    }

    public int getProgress() {
        return progress;
    }

    public int getMaxProgress() {
        return maxProgress;
    }

    public AnimationState getProcessingAnimationState() {
        return processingAnimationState;
    }

    public SimpleContainer getInputContainer() {
        return inputContainer;
    }

    public SimpleContainer getOutputContainer() {
        return outputContainer;
    }

    @Override
    public Container getContainerBySide(@Nullable Direction direction) {
        return direction == Direction.DOWN ? outputContainer : inputContainer;
    }

    @Override
    public Container[] getAllContainers() {
        return new SimpleContainer[]{getInputContainer(), getOutputContainer()};
    }
}
