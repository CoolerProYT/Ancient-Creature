package com.coolerpromc.ancientcreature.block.entity.custom;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.block.ModBlocks;
import com.coolerpromc.ancientcreature.block.entity.ModBlockEntities;
import com.coolerpromc.ancientcreature.data.component.ModDataComponents;
import com.coolerpromc.ancientcreature.data.component.custom.FossilCompleteness;
import com.coolerpromc.ancientcreature.entity.Species;
import com.coolerpromc.ancientcreature.menu.custom.FossilIdentificationChamberMenu;
import com.coolerpromc.ancientcreature.saveddata.IdentifiedSpeciesData;
import com.coolerpromc.ancientcreature.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
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

import static com.coolerpromc.ancientcreature.sound.SoundUtils.stopIdentificationSound;

public class FossilIdentificationChamberBlockEntity extends BlockEntity implements MenuProvider {
    public static final int DATA_PROGRESS = 0;
    public static final int DATA_MAX_PROGRESS = 1;

    private final AnimationState identifyingAnimationState = new AnimationState();
    private final ContainerData data;
    private boolean isIdentifying = false;
    private final int knownSpeciesProcessingTime = 100;
    private final int unknownSpeciesProcessingTime = 400;
    private int progress = 0;
    private int maxProgress = unknownSpeciesProcessingTime;

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
        if (level != null && level.getBlockState(placeholderPos).is(ModBlocks.PLACEHOLDER.blockHolder())) {
            level.removeBlock(placeholderPos, false);
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        inputContainer.storeAsItemList(output.list("input", ItemStack.CODEC));
        outputContainer.storeAsItemList(output.list("output", ItemStack.CODEC));
        output.putInt("progress", progress);
        output.putInt("maxProgress", maxProgress);
        output.putBoolean("isIdentifying", isIdentifying);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        inputContainer.fromItemList(input.listOrEmpty("input", ItemStack.CODEC));
        outputContainer.fromItemList(input.listOrEmpty("output", ItemStack.CODEC));
        progress = input.getIntOr("progress", 0);
        maxProgress = input.getIntOr("maxProgress", 100);
        isIdentifying = input.getBooleanOr("isIdentifying", false);
    }

    public boolean isValidInput(ItemStack stack){
        return !stack.getOrDefault(ModDataComponents.IS_DIRTY.get(), true) && !stack.getOrDefault(ModDataComponents.IDENTIFIED.get(), true) && stack.has(ModDataComponents.SPECIES.get());
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
                level.sendBlockUpdated(pos, state, state, 3);
            }
        }
        else {
            boolean wasWorking = progress != 0;
            progress = 0;
            this.isIdentifying = false;
            setChanged();
            if (wasWorking){
                stopIdentificationSound(serverLevel, pos, ModSounds.IDENTIFICATION_CHAMBER_SCAN);
                level.sendBlockUpdated(pos, state, state, 3);
            }
        }
    }

    private void finishIdentifying(ServerLevel level, BlockPos pos) {
        ItemStack fossil = inputContainer.removeItem(0, 1);
        Species species = fossil.get(ModDataComponents.SPECIES.get());
        IdentifiedSpeciesData data = IdentifiedSpeciesData.getIdentifiedSpeciesData(level.getServer());

        if (species != null){
            if (level.getRandom().nextFloat() <= species.getIdentifyFailChance() && !data.getIdentifiedSpecies().contains(species)){
                level.playSound(null, pos, ModSounds.IDENTIFICATION_CHAMBER_FAILED.get(), SoundSource.BLOCKS, 0.5f, 0.5f);
                fossil.set(ModDataComponents.IDENTIFICATION_FAILED.get(), true);
                float completeness = fossil.getOrDefault(ModDataComponents.FOSSIL_COMPLETENESS.get(), new FossilCompleteness(0.01f)).value() * (1f - species.getFossilDamageRate());
                fossil.set(ModDataComponents.FOSSIL_COMPLETENESS.get(), new FossilCompleteness(completeness));
                outputContainer.addItem(fossil);
            }
            else{
                fossil.set(ModDataComponents.IDENTIFIED.get(), true);
                fossil.set(ModDataComponents.IDENTIFICATION_FAILED.get(), false);
                outputContainer.addItem(fossil);

                if (!data.getIdentifiedSpecies().contains(species)){
                    data.addIdentifiedSpecies(species);
                }
            }
        }
    }

    private void setMaxProgress(ServerLevel level) {
        IdentifiedSpeciesData data = IdentifiedSpeciesData.getIdentifiedSpeciesData(level.getServer());
        Species species = inputContainer.getItem(0).get(ModDataComponents.SPECIES.get());
        this.maxProgress = data.getIdentifiedSpecies().contains(species) ? knownSpeciesProcessingTime : unknownSpeciesProcessingTime;
    }

    private boolean canIdentify() {
        return hasFossil() && hasOutputSlot();
    }

    private boolean hasOutputSlot(){
        ItemStack stack = inputContainer.getItem(0).copy();
        stack.set(ModDataComponents.IDENTIFIED.get(), true);
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
}
