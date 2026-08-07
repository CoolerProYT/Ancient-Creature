package com.coolerpromc.ancientcreature.block.entity.custom;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.block.entity.ModBlockEntities;
import com.coolerpromc.ancientcreature.entity.Species;
import com.coolerpromc.ancientcreature.entity.custom.AncientCreatureEntity;
import com.coolerpromc.ancientcreature.entity.custom.OwnableAncientCreature;
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
import net.minecraft.world.entity.player.Player;
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

    public EggBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(ModBlockEntities.EGG.get(), worldPosition, blockState);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.storeNullable("species", Species.CODEC, species);
        output.putInt("hatchTime", hatchTime);
        output.putInt("progress", progress);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        species = input.read("species", Species.CODEC).orElse(null);
        hatchTime = input.getIntOr("hatchTime", 1000);
        progress = input.getIntOr("progress", 0);
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
            final Species hatching = species;
            Entity spawned = species.getEntityType().create(serverLevel, entity -> {
                // Applied before the entity is added so it is placed with its own bounding box.
                if (entity instanceof AncientCreatureEntity creature) {
                    creature.setSpecies(hatching);
                }
                if (entity instanceof AgeableMob ageable) {
                    ageable.setBaby(true);
                }
            }, pos, EntitySpawnReason.SPAWN_ITEM_USE, true, false);
            if (spawned != null) {
                Player player = serverLevel.getNearestPlayer(pos.getX(), pos.getY(), pos.getZ(), 10, false);
                if (spawned instanceof OwnableAncientCreature creature && player != null) {
                    creature.setOwner(player);
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
