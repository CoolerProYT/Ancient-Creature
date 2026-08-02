package com.coolerpromc.ancientcreature.entity.custom;

import com.coolerpromc.ancientcreature.block.ModBlocks;
import com.coolerpromc.ancientcreature.block.entity.custom.EggBlockEntity;
import com.coolerpromc.ancientcreature.entity.Species;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

public abstract class OwnedAncientCreature extends Animal implements OwnableAncientCreature {
    private static final String OWNER_TAG = "AncientCreatureOwner";
    private @Nullable EntityReference<LivingEntity> ownerReference;

    protected OwnedAncientCreature(EntityType<? extends Animal> type, Level level) {
        super(type, level);
    }

    public void setOwner(@Nullable LivingEntity owner) {
        this.ownerReference = EntityReference.of(owner);
        if (this.isOwnedBy(this.getTarget())) {
            this.setTarget(null);
        }
    }

    @Override
    public @Nullable EntityReference<LivingEntity> getOwnerReference() {
        return this.ownerReference;
    }

    public boolean isOwnedBy(@Nullable LivingEntity entity) {
        return entity != null && this.ownerReference != null && this.ownerReference.matches(entity);
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        return !this.isOwnedBy(target) && super.canAttack(target);
    }

    @Override
    public void setTarget(@Nullable LivingEntity target) {
        super.setTarget(this.isOwnedBy(target) ? null : target);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        EntityReference.store(this.ownerReference, output, OWNER_TAG);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.ownerReference = EntityReference.readWithOldOwnerConversion(input, OWNER_TAG, this.level());
    }

    public void spawnChildFromBreeding(ServerLevel level, Animal partner) {
        AgeableMob offspring = this.getBreedOffspring(level, partner);
        if (offspring != null) {
            offspring.setBaby(true);
            offspring.snapTo(this.getX(), this.getY(), this.getZ(), 0.0F, 0.0F);
            this.finalizeSpawnChildFromBreeding(level, partner, offspring);
            Species species = Species.byEntityType(offspring.getType());

            if (species == null) return;

            BlockState state = ModBlocks.EGG.defaultBlockState();
            BlockPos pos = this.getOnPos().above();
            level.setBlockAndUpdate(pos, state);
            if (level.getBlockEntity(pos) instanceof EggBlockEntity blockEntity){
                blockEntity.setSpecies(species);
                blockEntity.setHatchTime(species.getIncubationTime());
            }
        }
    }
}
