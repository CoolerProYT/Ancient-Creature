package com.coolerpromc.ancientcreature.block.entity.custom;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.block.entity.ModBlockEntities;
import com.coolerpromc.ancientcreature.entity.Species;
import com.coolerpromc.ancientcreature.entity.custom.AncientCreatureEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

public class EggBlockEntity extends BlockEntity {
    private @Nullable Species species = null;
    private int hatchTime = 1000;
    private int progress = 0;
    /** The parents' owner; the hatchling belongs to them. Null for eggs of unowned parents. */
    private java.util.@Nullable UUID owner;
    private @Nullable String variant;
    private com.coolerpromc.ancientcreature.data.component.custom.@Nullable CreatureGenome genome;

    public EggBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(ModBlockEntities.EGG.get(), worldPosition, blockState);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.storeNullable("species", Species.CODEC, species);
        output.putInt("hatchTime", hatchTime);
        output.putInt("progress", progress);
        output.storeNullable("owner", net.minecraft.core.UUIDUtil.CODEC, owner);
        output.storeNullable("variant", com.mojang.serialization.Codec.STRING, variant);
        output.storeNullable("genome", com.coolerpromc.ancientcreature.data.component.custom.CreatureGenome.CODEC, genome);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        species = input.read("species", Species.CODEC).orElse(null);
        hatchTime = input.getIntOr("hatchTime", 1000);
        progress = input.getIntOr("progress", 0);
        owner = input.read("owner", net.minecraft.core.UUIDUtil.CODEC).orElse(null);
        variant = input.read("variant", com.mojang.serialization.Codec.STRING).orElse(null);
        genome = input.read("genome", com.coolerpromc.ancientcreature.data.component.custom.CreatureGenome.CODEC).orElse(null);
    }

    public void setParents(java.util.@Nullable UUID owner, @Nullable String variant) {
        this.owner = owner;
        this.variant = variant;
        setChanged();
    }

    /** The genome the hatchling inherited from its parents. */
    public void setGenome(com.coolerpromc.ancientcreature.data.component.custom.@Nullable CreatureGenome genome) {
        this.genome = genome;
        setChanged();
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

    public int getHatchTime() {
        return hatchTime;
    }

    public void setHatchTime(int hatchTime) {
        this.hatchTime = hatchTime;
        setChanged();
    }

    public void setSpecies(@Nullable Species species) {
        this.species = species;
    }

    public @Nullable Species getSpecies() {
        return species;
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (!(level instanceof ServerLevel serverLevel)) return;

        if (species == null){
            level.removeBlockEntity(pos);
            level.removeBlock(pos, false);
            return;
        }

        if (progress >= hatchTime){
            if (!AncientCreatureEntity.hasRoomFor(serverLevel, net.minecraft.world.phys.Vec3.atCenterOf(pos))) {
                // Wait for room; the egg stays ready and hatches as soon as the area thins out.
                return;
            }
            final Species hatching = species;
            final String inherited = variant;
            final var inheritedGenome = genome;
            Entity spawned = species.getEntityType().create(serverLevel, entity -> {
                // Applied before the entity is added so it is placed with its own bounding box.
                if (entity instanceof AncientCreatureEntity creature) {
                    creature.setSpecies(hatching);
                    if (inherited != null) {
                        creature.setVariant(inherited);
                    }
                    creature.setGenome(inheritedGenome);
                }
                if (entity instanceof AgeableMob ageable) {
                    ageable.setBaby(true);
                }
            }, pos, EntitySpawnReason.SPAWN_ITEM_USE, true, false);
            if (spawned != null) {
                if (owner != null && spawned instanceof AncientCreatureEntity creature) {
                    creature.setOwnerUuid(owner);
                    creature.setCommand(com.coolerpromc.ancientcreature.entity.custom.CreatureCommand.ROAM);
                }
                level.addFreshEntity(spawned);
                level.removeBlockEntity(pos);
                level.removeBlock(pos, false);
            }
        }else{
            progress++;
            setChanged();
        }
    }

    public int getRemainingTime(){
        return this.hatchTime - this.progress;
    }
}
