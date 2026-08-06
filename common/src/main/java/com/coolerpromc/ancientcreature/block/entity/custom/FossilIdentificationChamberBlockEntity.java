package com.coolerpromc.ancientcreature.block.entity.custom;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.block.ModBlocks;
import com.coolerpromc.ancientcreature.block.entity.ModBlockEntities;
import com.coolerpromc.ancientcreature.config.ModCommonConfig;
import com.coolerpromc.ancientcreature.data.component.ModDataComponents;
import com.coolerpromc.ancientcreature.data.component.custom.FossilData;
import com.coolerpromc.ancientcreature.entity.Species;
import com.coolerpromc.ancientcreature.item.FossilPart;
import com.coolerpromc.ancientcreature.menu.custom.FossilIdentificationChamberMenu;
import com.coolerpromc.ancientcreature.saveddata.IdentifiedSpeciesData;
import com.coolerpromc.ancientcreature.sound.ModSounds;
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

public class FossilIdentificationChamberBlockEntity extends BlockEntity implements MenuProvider, ICapabilityProvider {
    public static final int DATA_PROGRESS = 0;
    public static final int DATA_MAX_PROGRESS = 1;

    private final AnimationState identifyingAnimationState = new AnimationState();
    private final ContainerData data;
    private boolean isIdentifying = false;
    private int progress = 0;
    private int maxProgress = ModCommonConfig.CONFIG.unknownIdentifyingTick.get();

    private final SimpleContainer inputContainer = new SimpleContainer(1){
        @Override
        public boolean canPlaceItem(int slot, ItemStack itemStack) {
            return isValidInput(itemStack);
        }

        @Override
        public void setChanged() {
            if (!isEmpty() && FossilIdentificationChamberBlockEntity.this.level instanceof ServerLevel level){
                setMaxProgress(level);
            }
        }
    };
    private final SimpleContainer outputContainer = new SimpleContainer(1){
        @Override
        public boolean canPlaceItem(int slot, ItemStack itemStack) {
            return false;
        }
    };

    public FossilIdentificationChamberBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(ModBlockEntities.FOSSIL_IDENTIFICATION_CHAMBER.get(), worldPosition, blockState);
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
            if (level instanceof ServerLevel serverLevel && !inputContainer.isEmpty()){
                setMaxProgress(serverLevel);
            }
        });
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.ancientcreature.fossil_identification_chamber");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new FossilIdentificationChamberMenu(containerId, inventory, player, this, this.data);
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        BlockPos placeholderPos = pos.above();
        if (level != null){
            if (level.getBlockState(placeholderPos).is(ModBlocks.PLACEHOLDER.blockHolder())) {
                level.removeBlock(placeholderPos, false);
            }
            Containers.dropContents(this.level, pos, inputContainer);
            Containers.dropContents(this.level, pos, outputContainer);
            if (progress > 0 && level instanceof ServerLevel serverLevel){
                stopSound(serverLevel, pos, ModSounds.IDENTIFICATION_CHAMBER_SCAN);
            }
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        ContainerHelper.saveAllItems(output.child("input"), inputContainer.getItems());
        ContainerHelper.saveAllItems(output.child("output"), outputContainer.getItems());
        output.putInt("progress", progress);
        output.putInt("maxProgress", maxProgress);
        output.putBoolean("isIdentifying", isIdentifying);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        ContainerHelper.loadAllItems(input.childOrEmpty("input"), inputContainer.getItems());
        ContainerHelper.loadAllItems(input.childOrEmpty("output"), outputContainer.getItems());
        progress = input.getIntOr("progress", 0);
        maxProgress = input.getIntOr("maxProgress", 100);
        isIdentifying = input.getBooleanOr("isIdentifying", false);
    }

    public boolean isValidInput(ItemStack stack){
        FossilData fossilData = stack.getOrDefault(ModDataComponents.FOSSIL_DATA.get(), FossilData.EMPTY);
        return !fossilData.isDirty() && !fossilData.identified() && fossilData.getSpecies() != null;
    }

    public boolean isIdentifying(){
        return isIdentifying;
    }

    public AnimationState getIdentifyingAnimationState() {
        return identifyingAnimationState;
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (!(level instanceof ServerLevel serverLevel)) return;

        if (canIdentify()){
            if (progress == 0){
                this.isIdentifying = true;
                level.playSound(null, pos, ModSounds.IDENTIFICATION_CHAMBER_SCAN.get(), SoundSource.BLOCKS, 1f, 1f);
                level.sendBlockUpdated(pos, state, state, 3);
            }
            else if (progress % 100 == 0 && progress < maxProgress){
                level.playSound(null, pos, ModSounds.IDENTIFICATION_CHAMBER_SCAN.get(), SoundSource.BLOCKS, 1f, 1f);
            }
            progress++;
            setChanged();

            if (progress >= maxProgress){
                finishIdentifying(serverLevel, pos);
                progress = 0;
                this.isIdentifying = false;
                setChanged();
                stopSound(serverLevel, pos, ModSounds.IDENTIFICATION_CHAMBER_SCAN);
                level.sendBlockUpdated(pos, state, state, 3);
            }
        }
        else {
            boolean wasWorking = progress != 0;
            progress = 0;
            this.isIdentifying = false;
            setChanged();
            if (wasWorking){
                stopSound(serverLevel, pos, ModSounds.IDENTIFICATION_CHAMBER_SCAN);
                level.sendBlockUpdated(pos, state, state, 3);
            }
        }
    }

    private void finishIdentifying(ServerLevel level, BlockPos pos) {
        ItemStack fossil = inputContainer.removeItem(0, 1);
        FossilData fossilData = fossil.getOrDefault(ModDataComponents.FOSSIL_DATA.get(), FossilData.EMPTY);
        Holder<FossilPart> fossilPart = fossilData.fossilPart();
        Species species = fossilData.getSpecies();
        IdentifiedSpeciesData data = IdentifiedSpeciesData.getIdentifiedSpeciesData(level.getServer());
        float failChance = fossilPart.value().identifyFailChance();
        float damageRate = fossilPart.value().fossilDamageRate();

        if (species != null){
            if (level.getRandom().nextFloat() <= failChance && !data.getIdentifiedSpecies().contains(species)){
                level.playSound(null, pos, ModSounds.IDENTIFICATION_CHAMBER_FAILED.get(), SoundSource.BLOCKS, 0.5f, 0.5f);
                FossilData newData = fossilData.identifyFailed();
                newData = newData.setCompleteness(newData.completeness() * (1f - damageRate));
                fossil.set(ModDataComponents.FOSSIL_DATA.get(), newData);
                outputContainer.addItem(fossil);
            }
            else{
                fossil.set(ModDataComponents.FOSSIL_DATA.get(), fossilData.identify());
                outputContainer.addItem(fossil);

                if (!data.getIdentifiedSpecies().contains(species)){
                    data.addIdentifiedSpecies(species, level);
                }
            }
        }
    }

    private void setMaxProgress(ServerLevel level) {
        IdentifiedSpeciesData data = IdentifiedSpeciesData.getIdentifiedSpeciesData(level.getServer());
        FossilData fossilData = inputContainer.getItem(0).get(ModDataComponents.FOSSIL_DATA.get());
        Species species = fossilData.getSpecies();
        this.maxProgress = data.getIdentifiedSpecies().contains(species) ? ModCommonConfig.CONFIG.knownIdentifyingTick.get() : ModCommonConfig.CONFIG.unknownIdentifyingTick.get();
    }

    private boolean canIdentify() {
        return hasFossil() && hasOutputSlot();
    }

    private boolean hasOutputSlot(){
        ItemStack stack = inputContainer.getItem(0).copy();
        FossilData fossilData = stack.get(ModDataComponents.FOSSIL_DATA.get());
        stack.set(ModDataComponents.FOSSIL_DATA.get(), fossilData.identify());
        return outputContainer.canAddItem(stack);
    }

    private boolean hasFossil(){
        return !inputContainer.getItem(0).isEmpty();
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

    public SimpleContainer getInputContainer() {
        return inputContainer;
    }

    public SimpleContainer getOutputContainer() {
        return outputContainer;
    }

    public Container getContainerBySide(@Nullable Direction direction) {
        if (direction == Direction.DOWN){
            return getOutputContainer();
        }
        return getInputContainer();
    }

    @Override
    public Container[] getAllContainers() {
        return new SimpleContainer[]{getInputContainer(), getOutputContainer()};
    }
}
