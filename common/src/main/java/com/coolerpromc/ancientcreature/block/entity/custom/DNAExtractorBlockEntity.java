package com.coolerpromc.ancientcreature.block.entity.custom;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.block.ModBlocks;
import com.coolerpromc.ancientcreature.block.entity.ModBlockEntities;
import com.coolerpromc.ancientcreature.config.ModCommonConfig;
import com.coolerpromc.ancientcreature.data.component.ModDataComponents;
import com.coolerpromc.ancientcreature.data.component.custom.DNAData;
import com.coolerpromc.ancientcreature.data.component.custom.FossilData;
import com.coolerpromc.ancientcreature.entity.Species;
import com.coolerpromc.ancientcreature.item.DNAIntegrityLevel;
import com.coolerpromc.ancientcreature.item.FossilPart;
import com.coolerpromc.ancientcreature.item.ModItems;
import com.coolerpromc.ancientcreature.menu.custom.DNAExtractorMenu;
import com.coolerpromc.ancientcreature.sound.ModSounds;
import com.coolerpromc.ancientcreature.tag.ModItemTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
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

public class DNAExtractorBlockEntity extends BlockEntity implements MenuProvider, ICapabilityProvider {
    public static final int DATA_PROGRESS = 0;
    public static final int DATA_MAX_PROGRESS = 1;

    private final AnimationState extractingAnimationState = new AnimationState();
    private final ContainerData data;
    private boolean isExtracting = false;
    private int progress = 0;
    private int maxProgress = ModCommonConfig.CONFIG.extractingTick.get();

    private final SimpleContainer extractionFluidContainer = new SimpleContainer(1){
        @Override
        public boolean canPlaceItem(int slot, ItemStack itemStack) {
            return itemStack.is(ModItemTags.EXTRACTION_FLUIDS);
        }
    };

    private final SimpleContainer fossilContainer = new SimpleContainer(1){
        @Override
        public boolean canPlaceItem(int slot, ItemStack itemStack) {
            return isValidFossil(itemStack);
        }

        private boolean isValidFossil(ItemStack itemStack) {
            FossilData fossilData = itemStack.get(ModDataComponents.FOSSIL_DATA.get());
            boolean isIdentified = fossilData.identified();
            boolean isDirty = fossilData.isDirty();
            return isIdentified && !isDirty && fossilData.getSpecies() != null;
        }
    };

    private final SimpleContainer sampleVialContainer = new SimpleContainer(1){
        @Override
        public boolean canPlaceItem(int slot, ItemStack itemStack) {
            return itemStack.is(ModItems.SAMPLE_VIAL.get());
        }
    };

    private final SimpleContainer outputContainer = new SimpleContainer(1){
        @Override
        public boolean canPlaceItem(int slot, ItemStack itemStack) {
            return false;
        }
    };

    public DNAExtractorBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(ModBlockEntities.DNA_EXTRACTOR.get(), worldPosition, blockState);
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
            maxProgress = ModCommonConfig.CONFIG.extractingTick.get();
        });
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.ancientcreature.dna_extractor");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new DNAExtractorMenu(containerId, inventory, player, this, this.data);
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        BlockPos placeholderPos = pos.above();
        if (level != null){
            if (level.getBlockState(placeholderPos).is(ModBlocks.PLACEHOLDER.blockHolder())) {
                level.removeBlock(placeholderPos, false);
            }
            Containers.dropContents(this.level, pos, extractionFluidContainer);
            Containers.dropContents(this.level, pos, sampleVialContainer);
            Containers.dropContents(this.level, pos, fossilContainer);
            Containers.dropContents(this.level, pos, outputContainer);
            if (progress > 0 && level instanceof ServerLevel serverLevel){
                stopSound(serverLevel, pos, ModSounds.DNA_EXTRACTOR_PROCESSING);
            }
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        ContainerHelper.saveAllItems(output.child("extractionFluid"), extractionFluidContainer.getItems());
        ContainerHelper.saveAllItems(output.child("fossil"), fossilContainer.getItems());
        ContainerHelper.saveAllItems(output.child("sampleVial"), sampleVialContainer.getItems());
        ContainerHelper.saveAllItems(output.child("output"), outputContainer.getItems());
        output.putInt("progress", progress);
        output.putInt("maxProgress", maxProgress);
        output.putBoolean("isExtracting", isExtracting);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        ContainerHelper.loadAllItems(input.childOrEmpty("extractionFluid"), extractionFluidContainer.getItems());
        ContainerHelper.loadAllItems(input.childOrEmpty("fossil"), fossilContainer.getItems());
        ContainerHelper.loadAllItems(input.childOrEmpty("sampleVial"), sampleVialContainer.getItems());
        ContainerHelper.loadAllItems(input.childOrEmpty("output"), outputContainer.getItems());
        progress = input.getIntOr("progress", 0);
        maxProgress = input.getIntOr("maxProgress", ModCommonConfig.CONFIG.extractingTick.get());
        isExtracting = input.getBooleanOr("isExtracting", false);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (!(level instanceof ServerLevel serverLevel)) return;

        if (canExtract()){
            if (progress == 0){
                this.isExtracting = true;
                level.playSound(null, pos, ModSounds.DNA_EXTRACTOR_PROCESSING.get(), SoundSource.BLOCKS, 2f, 1f);
                level.sendBlockUpdated(pos, state, state, 3);
            }
            else if (progress % 100 == 0 && progress < maxProgress){
                level.playSound(null, pos, ModSounds.DNA_EXTRACTOR_PROCESSING.get(), SoundSource.BLOCKS, 2f, 1f);
            }
            progress++;
            setChanged();

            if (progress >= maxProgress){
                finishExtracting(serverLevel, pos);
                progress = 0;
                this.isExtracting = false;
                setChanged();
                stopSound(serverLevel, pos, ModSounds.DNA_EXTRACTOR_PROCESSING);
                level.sendBlockUpdated(pos, state, state, 3);
            }
        }
        else {
            boolean wasWorking = progress != 0;
            progress = 0;
            this.isExtracting = false;
            setChanged();
            if (wasWorking){
                stopSound(serverLevel, pos, ModSounds.DNA_EXTRACTOR_PROCESSING);
                level.sendBlockUpdated(pos, state, state, 3);
            }
        }
    }

    private void finishExtracting(ServerLevel serverLevel, BlockPos pos) {
        ItemStack output = calculateOutputStack();
        extractionFluidContainer.getItem(0).hurtAndBreak(1, serverLevel, null, _ -> {});
        if (!output.isEmpty()){
            sampleVialContainer.getItem(0).shrink(1);
        }
        fossilContainer.getItem(0).shrink(1);
        outputContainer.addItem(output);
    }

    private boolean canExtract() {
        return hasExtractionFluid() && hasSampleVial() && hasFossil() && hasEnoughOutputSlot();
    }

    private boolean hasEnoughOutputSlot() {
        ItemStack outputStack = calculateOutputStack();
        return outputContainer.canAddItem(outputStack);
    }

    private ItemStack calculateOutputStack(){
        ItemStack input = fossilContainer.getItem(0);
        FossilData fossilData = input.getOrDefault(ModDataComponents.FOSSIL_DATA.get(), FossilData.EMPTY);
        Holder<FossilPart> part = fossilData.fossilPart();
        Species species = fossilData.getSpecies();
        float completeness = fossilData.completeness();
        ItemStack output = ModItems.DNA_SAMPLE.toStack();
        float integrityScore = Math.max(0.01f, completeness + part.value().dnaExtractingBonus());
        DNAIntegrityLevel integrityLevel = DNAIntegrityLevel.byScore(integrityScore);
        if (integrityLevel == null){
            return ItemStack.EMPTY;
        }
        output.set(ModDataComponents.DNA_DATA.get(), new DNAData(integrityLevel, species));
        return output;
    }

    private boolean hasFossil() {
        return !fossilContainer.isEmpty();
    }

    private boolean hasSampleVial() {
        return !sampleVialContainer.isEmpty();
    }

    private boolean hasExtractionFluid() {
        return !extractionFluidContainer.isEmpty();
    }

    public boolean isExtracting() {
        return isExtracting;
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        try(ProblemReporter.ScopedCollector reporter = new ProblemReporter.ScopedCollector(this.problemPath(), Constants.LOG)){
            TagValueOutput output = TagValueOutput.createWithContext(reporter, registries);
            saveAdditional(output);
            return output.buildResult();
        }
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public AnimationState getExtractingAnimationState() {
        return extractingAnimationState;
    }

    public SimpleContainer getExtractionFluidContainer() {
        return extractionFluidContainer;
    }

    public SimpleContainer getFossilContainer() {
        return fossilContainer;
    }

    public SimpleContainer getSampleVialContainer() {
        return sampleVialContainer;
    }

    public SimpleContainer getOutputContainer() {
        return outputContainer;
    }

    public Container getContainerBySide(@Nullable Direction direction) {
        if (direction == Direction.DOWN){
            return getOutputContainer();
        }
        if (direction == Direction.UP){
            return getSampleVialContainer();
        }
        if (direction == Direction.EAST || direction ==Direction.WEST){
            return getFossilContainer();
        }
        return getExtractionFluidContainer();
    }

    @Override
    public Container[] getAllContainers() {
        return new SimpleContainer[]{getExtractionFluidContainer(), getSampleVialContainer(), getFossilContainer(), getOutputContainer()};
    }
}
