package com.coolerpromc.ancientcreature.entity.custom;

import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.AgeableWaterCreature;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

public abstract class OwnedAgeableWaterCreature extends AgeableWaterCreature implements OwnableAncientCreature {
    private static final String OWNER_TAG = "AncientCreatureOwner";
    private @Nullable EntityReference<LivingEntity> ownerReference;

    protected OwnedAgeableWaterCreature(EntityType<? extends AgeableWaterCreature> type, Level level) {
        super(type, level);
    }

    @Override
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

    @Override
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
}
