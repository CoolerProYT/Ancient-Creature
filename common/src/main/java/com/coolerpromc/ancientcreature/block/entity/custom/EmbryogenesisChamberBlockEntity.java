package com.coolerpromc.ancientcreature.block.entity.custom;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.block.ModBlocks;
import com.coolerpromc.ancientcreature.block.entity.ModBlockEntities;
import com.coolerpromc.ancientcreature.block.entity.upgrade.MachineUpgrades;
import com.coolerpromc.ancientcreature.config.ModCommonConfig;
import com.coolerpromc.ancientcreature.data.component.ModDataComponents;
import com.coolerpromc.ancientcreature.data.component.custom.CreatureGenome;
import com.coolerpromc.ancientcreature.data.component.custom.GenomeData;
import com.coolerpromc.ancientcreature.species.SpeciesHybridProperties;
import com.coolerpromc.ancientcreature.entity.Species;
import com.coolerpromc.ancientcreature.item.ModItems;
import com.coolerpromc.ancientcreature.menu.custom.EmbryogenesisChamberMenu;
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

public class EmbryogenesisChamberBlockEntity extends BlockEntity implements MenuProvider, ICapabilityProvider {
    public static final int DATA_PROGRESS = 0;
    public static final int DATA_MAX_PROGRESS = 1;

    private final AnimationState processingAnimationState = new AnimationState();
    private final ContainerData data;
    private final MachineUpgrades upgrades = new MachineUpgrades(this::setChanged);
    private boolean isProcessing = false;
    private int progress = 0;
    private int maxProgress = ModCommonConfig.CONFIG.embryogenesisTick.get();

    private final SimpleContainer genomeContainer = new SimpleContainer(1){
        @Override
        public boolean canPlaceItem(int slot, ItemStack itemStack) {
            return itemStack.is(ModItems.GENOME_CARTRIDGE_COMPLETED.get());
        }
    };
    /** A second completed genome; with one here the chamber splices the two into their hybrid. */
    private final SimpleContainer donorContainer = new SimpleContainer(1){
        @Override
        public boolean canPlaceItem(int slot, ItemStack itemStack) {
            return itemStack.is(ModItems.GENOME_CARTRIDGE_COMPLETED.get());
        }
    };
    private final SimpleContainer eggContainer = new SimpleContainer(1){
        @Override
        public boolean canPlaceItem(int slot, ItemStack itemStack) {
            return itemStack.is(ModItems.ARTIFICIAL_EGG.get());
        }
    };
    private final SimpleContainer nutrientContainer = new SimpleContainer(1){
        @Override
        public boolean canPlaceItem(int slot, ItemStack itemStack) {
            return itemStack.is(ModItems.NUTRIENT_SOLUTION.get());
        }
    };
    private final SimpleContainer outputContainer = new SimpleContainer(1) {
        @Override
        public boolean canPlaceItem(int slot, ItemStack stack) {
            return false;
        }
    };

    public EmbryogenesisChamberBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.EMBRYOGENESIS_CHAMBER.get(), pos, state);
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
        return Component.translatable("block.ancientcreature.embryogenesis_chamber");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new EmbryogenesisChamberMenu(containerId, inventory, player, this, this.data);
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        if (level == null) return;
        BlockPos placeholderPos = pos.above();
        if (level.getBlockState(placeholderPos).is(ModBlocks.PLACEHOLDER.blockHolder())) {
            level.removeBlock(placeholderPos, false);
        }
        upgrades.drop(level, pos);
        Containers.dropContents(level, pos, genomeContainer);
        Containers.dropContents(level, pos, donorContainer);
        Containers.dropContents(level, pos, eggContainer);
        Containers.dropContents(level, pos, nutrientContainer);
        Containers.dropContents(level, pos, outputContainer);
        if (isProcessing && level instanceof ServerLevel serverLevel) {
            stopSound(serverLevel, pos, ModSounds.EMBRYOGENESIS_CHAMBER_PROCESSING);
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        upgrades.save(output);
        ContainerHelper.saveAllItems(output.child("genome"), genomeContainer.getItems());
        ContainerHelper.saveAllItems(output.child("donor"), donorContainer.getItems());
        ContainerHelper.saveAllItems(output.child("egg"), eggContainer.getItems());
        ContainerHelper.saveAllItems(output.child("nutrient"), nutrientContainer.getItems());
        ContainerHelper.saveAllItems(output.child("output"), outputContainer.getItems());
        output.putInt("progress", progress);
        output.putInt("maxProgress", maxProgress);
        output.putBoolean("isProcessing", isProcessing);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        upgrades.load(input);
        ContainerHelper.loadAllItems(input.childOrEmpty("genome"), genomeContainer.getItems());
        ContainerHelper.loadAllItems(input.childOrEmpty("donor"), donorContainer.getItems());
        ContainerHelper.loadAllItems(input.childOrEmpty("egg"), eggContainer.getItems());
        ContainerHelper.loadAllItems(input.childOrEmpty("nutrient"), nutrientContainer.getItems());
        ContainerHelper.loadAllItems(input.childOrEmpty("output"), outputContainer.getItems());
        progress = input.getIntOr("progress", 0);
        maxProgress = input.getIntOr("maxProgress", ModCommonConfig.CONFIG.embryogenesisTick.get());
        isProcessing = input.getBooleanOr("isProcessing", false);
    }

    private int baseTicks(ServerLevel level) {
        return ModCommonConfig.CONFIG.embryogenesisTick.get();
    }

    public MachineUpgrades getUpgrades() {
        return upgrades;
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (!(level instanceof ServerLevel serverLevel)) return;

        int baseTicks = baseTicks(serverLevel);
        maxProgress = baseTicks <= 0 ? 0 : upgrades.scaleTime(baseTicks);

        if (canProcess()){
            if (progress == 0){
                this.isProcessing = true;
                level.playSound(null, pos, ModSounds.EMBRYOGENESIS_CHAMBER_PROCESSING.get(), SoundSource.BLOCKS, 0.5f, 1f);
                level.sendBlockUpdated(pos, state, state, 3);
            }
            else if (progress % 100 == 0 && progress < maxProgress){
                level.playSound(null, pos, ModSounds.EMBRYOGENESIS_CHAMBER_PROCESSING.get(), SoundSource.BLOCKS, 0.5f, 1f);
            }
            progress++;
            setChanged();

            if (progress >= maxProgress){
                finishProcessing(serverLevel, pos);
                progress = 0;
                this.isProcessing = false;
                setChanged();
                stopSound(serverLevel, pos, ModSounds.EMBRYOGENESIS_CHAMBER_PROCESSING);
                level.sendBlockUpdated(pos, state, state, 3);
            }
        }
        else {
            boolean wasWorking = progress != 0;
            progress = 0;
            this.isProcessing = false;
            setChanged();
            if (wasWorking){
                stopSound(serverLevel, pos, ModSounds.EMBRYOGENESIS_CHAMBER_PROCESSING);
                level.sendBlockUpdated(pos, state, state, 3);
            }
        }
    }

    private void finishProcessing(ServerLevel serverLevel, BlockPos pos) {
        Species species = resultSpecies();
        if (species == null) return;
        ItemStack stack = genomeContainer.removeItem(0, 1);
        ItemStack donor = donorContainer.removeItem(0, 1);
        eggContainer.removeItem(0, 1);
        if (!upgrades.saves(serverLevel.getRandom())) {
            nutrientContainer.removeItem(0, 1);
        }
        // The embryo's traits are decided here, from how faithful the genome is.
        float fidelity = fidelityOf(stack);
        if (!donor.isEmpty()) {
            fidelity = (fidelity + fidelityOf(donor)) * 0.5f * SpeciesHybridProperties.SPLICE_FIDELITY;
        }
        CreatureGenome genome = CreatureGenome.roll(species, fidelity, upgrades.precision(), serverLevel.getRandom());
        if (!donor.isEmpty()) {
            genome = genome.sterile();
            // a hybrid has no fossil to identify, so it enters the journal when it is first made
            var identified = com.coolerpromc.ancientcreature.saveddata.IdentifiedSpeciesData.getIdentifiedSpeciesData(serverLevel.getServer());
            if (!identified.getIdentifiedSpecies().contains(species)) {
                identified.addIdentifiedSpecies(species, serverLevel);
            }
        }
        ItemStack egg = ModItems.FERTILIZED_ANCIENT_EGG.toStack();
        egg.set(ModDataComponents.SPECIES.get(), species);
        egg.set(ModDataComponents.GENOME.get(), genome);
        outputContainer.addItem(egg);
    }

    private static float fidelityOf(ItemStack cartridge) {
        GenomeData genomeData = cartridge.get(ModDataComponents.GENOME_DATA.get());
        return genomeData != null ? genomeData.fidelity() : GenomeData.LEGACY_FIDELITY;
    }

    /** The species the chamber would make now: the cartridge's, or the hybrid of it and the donor. */
    private @Nullable Species resultSpecies() {
        Species species = genomeContainer.getItem(0).get(ModDataComponents.SPECIES.get());
        if (species == null) return null;
        ItemStack donor = donorContainer.getItem(0);
        if (donor.isEmpty()) return species;
        Species other = donor.get(ModDataComponents.SPECIES.get());
        return other == null ? null : Species.hybridOf(species, other).orElse(null);
    }

    private boolean canProcess() {
        return hasNutrient() && hasEgg() && hasValidCartridge() && hasOutputSlot();
    }

    private boolean hasOutputSlot() {
        return outputContainer.isEmpty();
    }

    private boolean hasValidCartridge() {
        return resultSpecies() != null;
    }

    private boolean hasEgg() {
        return !eggContainer.isEmpty();
    }

    private boolean hasNutrient() {
        return !nutrientContainer.isEmpty();
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

    public AnimationState getProcessingAnimationState() {
        return processingAnimationState;
    }

    public SimpleContainer getGenomeContainer() {
        return genomeContainer;
    }

    public SimpleContainer getDonorContainer() {
        return donorContainer;
    }

    public SimpleContainer getEggContainer() {
        return eggContainer;
    }

    public SimpleContainer getNutrientContainer() {
        return nutrientContainer;
    }

    public SimpleContainer getOutputContainer() {
        return outputContainer;
    }

    @Override
    public Container getContainerBySide(@Nullable Direction direction) {
        if (direction == Direction.DOWN) return outputContainer;
        if (direction == Direction.UP) return eggContainer;
        if (direction == Direction.EAST || direction == Direction.WEST) return genomeContainer;
        return nutrientContainer;
    }

    @Override
    public Container[] getAllContainers() {
        return new SimpleContainer[]{getGenomeContainer(), getEggContainer(), getNutrientContainer(), getOutputContainer()};
    }
}
