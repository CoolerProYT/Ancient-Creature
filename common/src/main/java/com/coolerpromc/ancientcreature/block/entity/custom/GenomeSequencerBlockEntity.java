package com.coolerpromc.ancientcreature.block.entity.custom;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.block.ModBlocks;
import com.coolerpromc.ancientcreature.block.entity.ModBlockEntities;
import com.coolerpromc.ancientcreature.config.ModCommonConfig;
import com.coolerpromc.ancientcreature.data.component.ModDataComponents;
import com.coolerpromc.ancientcreature.data.component.custom.DNAData;
import com.coolerpromc.ancientcreature.data.component.custom.GenomeData;
import com.coolerpromc.ancientcreature.entity.Species;
import com.coolerpromc.ancientcreature.item.DNAIntegrityLevel;
import com.coolerpromc.ancientcreature.item.ModItems;
import com.coolerpromc.ancientcreature.menu.custom.GenomeSequencerMenu;
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

public class GenomeSequencerBlockEntity extends BlockEntity implements MenuProvider, ICapabilityExposure {
    public static final int DATA_PROGRESS = 0;
    public static final int DATA_MAX_PROGRESS = 1;

    private final AnimationState processingAnimationState = new AnimationState();
    private final ContainerData data;
    private boolean isProcessing = false;
    private int progress = 0;
    private int maxProgress = ModCommonConfig.CONFIG.sequencingTick.get();

    private final SimpleContainer dnaSampleContainer = new SimpleContainer(1){
        @Override
        public boolean canPlaceItem(int slot, ItemStack itemStack) {
            return itemStack.is(ModItems.DNA_SAMPLE.get());
        }
    };

    private final SimpleContainer cartridgeContainer = new SimpleContainer(1){
        @Override
        public boolean canPlaceItem(int slot, ItemStack itemStack) {
            return itemStack.is(ModItems.GENOME_CARTRIDGE_BLANK.get()) || itemStack.is(ModItems.GENOME_CARTRIDGE_FILLED.get());
        }
    };

    public GenomeSequencerBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(ModBlockEntities.GENOME_SEQUENCER.get(), worldPosition, blockState);
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
            maxProgress = ModCommonConfig.CONFIG.sequencingTick.get();
        });
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.ancientcreature.genome_sequencer");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new GenomeSequencerMenu(containerId, inventory, player, this, this.data);
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        BlockPos placeholderPos = pos.above();
        if (level != null){
            if (level.getBlockState(placeholderPos).is(ModBlocks.PLACEHOLDER.blockHolder())) {
                level.removeBlock(placeholderPos, false);
            }
            Containers.dropContents(this.level, pos, dnaSampleContainer);
            Containers.dropContents(this.level, pos, cartridgeContainer);
            if (progress > 0 && level instanceof ServerLevel serverLevel){
                stopSound(serverLevel, pos, ModSounds.GENOME_SEQUENCER_PROCESSING);
            }
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        ContainerHelper.saveAllItems(output.child("dnaSample"), dnaSampleContainer.getItems());
        ContainerHelper.saveAllItems(output.child("cartridge"), cartridgeContainer.getItems());
        output.putInt("progress", progress);
        output.putInt("maxProgress", maxProgress);
        output.putBoolean("isExtracting", isProcessing);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        ContainerHelper.loadAllItems(input.childOrEmpty("dnaSample"), dnaSampleContainer.getItems());
        ContainerHelper.loadAllItems(input.childOrEmpty("cartridge"), cartridgeContainer.getItems());
        progress = input.getIntOr("progress", 0);
        maxProgress = input.getIntOr("maxProgress", ModCommonConfig.CONFIG.sequencingTick.get());
        isProcessing = input.getBooleanOr("isExtracting", false);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (!(level instanceof ServerLevel serverLevel)) return;

        if (canProcess()){
            if (progress == 0){
                this.isProcessing = true;
                level.playSound(null, pos, ModSounds.GENOME_SEQUENCER_PROCESSING.get(), SoundSource.BLOCKS, 2f, 1f);
                level.sendBlockUpdated(pos, state, state, 3);
            }
            else if (progress % 100 == 0 && progress < maxProgress){
                level.playSound(null, pos, ModSounds.GENOME_SEQUENCER_PROCESSING.get(), SoundSource.BLOCKS, 2f, 1f);
            }
            progress++;
            setChanged();

            if (progress >= maxProgress){
                finishProcessing(serverLevel, pos);
                progress = 0;
                this.isProcessing = false;
                setChanged();
                stopSound(serverLevel, pos, ModSounds.GENOME_SEQUENCER_PROCESSING);
                level.sendBlockUpdated(pos, state, state, 3);
            }
        }
        else {
            boolean wasWorking = progress != 0;
            progress = 0;
            this.isProcessing = false;
            setChanged();
            if (wasWorking){
                stopSound(serverLevel, pos, ModSounds.GENOME_SEQUENCER_PROCESSING);
                level.sendBlockUpdated(pos, state, state, 3);
            }
        }
    }

    private void finishProcessing(ServerLevel serverLevel, BlockPos pos) {
        ItemStack dnaSample = dnaSampleContainer.removeItem(0, 1);
        DNAData dnaData = dnaSample.get(ModDataComponents.DNA_DATA.get());
        Species species = dnaData.species();
        DNAIntegrityLevel integrityLevel = dnaData.integrityLevel();
        if (species == null || integrityLevel == null) return;
        float completeness = integrityLevel.getGenomeCompleteness(serverLevel.getRandom());
        ItemStack cartridge = cartridgeContainer.removeItem(0, 1);
        if (cartridge.is(ModItems.GENOME_CARTRIDGE_BLANK.get())){
            ItemStack newCartridge = ModItems.GENOME_CARTRIDGE_FILLED.toStack();
            newCartridge.set(ModDataComponents.GENOME_DATA.get(), new GenomeData(completeness, species));
            cartridgeContainer.setItem(0, newCartridge);
        }
        else {
            GenomeData genomeData = cartridge.get(ModDataComponents.GENOME_DATA.get());
            if (genomeData == null) return;
            float currentCompleteness = genomeData.completeness();
            float newCompleteness = Math.min(currentCompleteness + completeness, 1f);
            if (newCompleteness < 1f){
                cartridge.set(ModDataComponents.GENOME_DATA.get(), genomeData.setCompleteness(newCompleteness));
                cartridgeContainer.setItem(0, cartridge);
            }else {
                ItemStack completed = ModItems.GENOME_CARTRIDGE_COMPLETED.toStack();
                completed.set(ModDataComponents.SPECIES.get(), species);
                cartridgeContainer.setItem(0, completed);
            }
        }
    }

    private boolean canProcess() {
        return hasValidDnaSample() && hasEnoughOutputSlot();
    }

    private boolean hasValidDnaSample() {
        return !dnaSampleContainer.getItem(0).isEmpty();
    }

    private boolean hasEnoughOutputSlot() {
        ItemStack dna = dnaSampleContainer.getItem(0).copy();
        DNAData dnaData = dna.get(ModDataComponents.DNA_DATA.get());
        Species species = dnaData.species();
        if (species == null) return false;
        ItemStack cartridge = cartridgeContainer.getItem(0);
        if (cartridge.isEmpty()) return false;
        if (cartridge.is(ModItems.GENOME_CARTRIDGE_BLANK.get())) return true;
        if (cartridge.is(ModItems.GENOME_CARTRIDGE_COMPLETED.get())) return false;
        GenomeData genomeData = cartridge.get(ModDataComponents.GENOME_DATA.get());
        if (genomeData == null) return true;
        float completeness = genomeData.completeness();
        if (completeness >= 1f) return false;
        return species.equals(genomeData.species());
    }

    public boolean isProcessing() {
        return isProcessing;
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        try(ProblemReporter.ScopedCollector reporter = new ProblemReporter.ScopedCollector(this.problemPath(), Constants.LOG)){
            TagValueOutput output = TagValueOutput.createWithContext(reporter, registries);
            saveAdditional(output);
            return output.buildResult();
        }
    }

    public SimpleContainer getDnaSampleContainer() {
        return dnaSampleContainer;
    }

    public SimpleContainer getCartridgeContainer() {
        return cartridgeContainer;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public AnimationState getProcessingAnimationState() {
        return processingAnimationState;
    }

    public Container getContainerBySide(@Nullable Direction direction) {
        if (direction == Direction.DOWN || direction == Direction.UP){
            return getCartridgeContainer();
        }
        return getDnaSampleContainer();
    }

    @Override
    public Container[] getAllContainers() {
        return new SimpleContainer[]{getDnaSampleContainer(), getCartridgeContainer()};
    }
}
