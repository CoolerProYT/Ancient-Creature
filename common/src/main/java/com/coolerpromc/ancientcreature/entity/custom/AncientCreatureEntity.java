package com.coolerpromc.ancientcreature.entity.custom;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.entity.ModEntities;
import com.coolerpromc.ancientcreature.entity.Species;
import com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorComponent;
import com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorConfig;
import com.coolerpromc.ancientcreature.species.*;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.*;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AncientCreatureEntity extends OwnedAncientCreature {
    private static final EntityDataAccessor<String> DATA_SPECIES = SynchedEntityData.defineId(AncientCreatureEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> DATA_VARIANT = SynchedEntityData.defineId(AncientCreatureEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Byte> DATA_ACTION = SynchedEntityData.defineId(AncientCreatureEntity.class, EntityDataSerializers.BYTE);

    private static final String SPECIES_TAG = "Species";
    private static final String VARIANT_TAG = "Variant";

    public static final String DEFAULT_VARIANT = "default";

    private static final Set<Identifier> WARNED_MISSING = java.util.Collections.synchronizedSet(new HashSet<>());

    private Species species = Species.TRICERATOPS;
    private @Nullable SpeciesDefinition definition;
    private int appliedGeneration = -1;
    private float appliedScale = Float.NaN;
    private boolean goalsBuilt;
    private int actionTicks;
    private @Nullable SpeciesEntityCategory appliedCategory;

    /**
     * Debug-only: an action to re-trigger forever, set by the {@code /ancientcreature action ... loop}
     * command. Deliberately transient — it is not saved and does not survive a reload, because it is a
     * tool for looking at a clip, not creature state.
     */
    private @Nullable AncientCreatureAction loopAction;
    private int loopPeriod = 60;
    private int loopGap;

    /** Ticks spent back on {@code NONE} between loop replays, so the controller re-enters the state. */
    private static final int LOOP_GAP_TICKS = 3;

    public AncientCreatureEntity(EntityType<? extends AncientCreatureEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createAnimalAttributes()
            .add(Attributes.MAX_HEALTH, 20.0)
            .add(Attributes.MOVEMENT_SPEED, 0.25)
            .add(Attributes.ATTACK_DAMAGE, 1.0)
            .add(Attributes.ATTACK_KNOCKBACK, 0.0)
            .add(Attributes.ATTACK_SPEED, Attributes.DEFAULT_ATTACK_SPEED)
            .add(Attributes.FOLLOW_RANGE, 16.0)
            .add(Attributes.KNOCKBACK_RESISTANCE, 0.0)
            .add(Attributes.ARMOR, 0.0)
            .add(Attributes.ARMOR_TOUGHNESS, 0.0)
            .add(Attributes.STEP_HEIGHT, 0.6)
            .add(Attributes.SAFE_FALL_DISTANCE, 3.0)
            .add(Attributes.FALL_DAMAGE_MULTIPLIER, 1.0)
            .add(Attributes.JUMP_STRENGTH, 0.42)
            .add(Attributes.GRAVITY, 0.08)
            .add(Attributes.MOVEMENT_EFFICIENCY, 0.0)
            .add(Attributes.WATER_MOVEMENT_EFFICIENCY, 0.0)
            .add(Attributes.OXYGEN_BONUS, 0.0)
            .add(Attributes.BURNING_TIME, 1.0)
            .add(Attributes.EXPLOSION_KNOCKBACK_RESISTANCE, 0.0)
            .add(Attributes.MAX_ABSORPTION, 0.0)
            .add(Attributes.TEMPT_RANGE, 10.0)
            .add(Attributes.CAMERA_DISTANCE, 4.0)
            .add(Attributes.SCALE, 1.0);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_SPECIES, Species.TRICERATOPS.id().toString());
        builder.define(DATA_VARIANT, DEFAULT_VARIANT);
        builder.define(DATA_ACTION, AncientCreatureAction.NONE.id());
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> accessor) {
        super.onSyncedDataUpdated(accessor);
        if (DATA_SPECIES.equals(accessor)) {
            Identifier id = Identifier.tryParse(this.entityData.get(DATA_SPECIES));
            this.species = id != null ? new Species(id) : Species.TRICERATOPS;
            this.definition = null;
            this.appliedGeneration = -1;
            this.appliedScale = Float.NaN;
            this.appliedCategory = null;
            this.refreshSpecies();
        }
    }

    public Species getSpecies() {
        return this.species;
    }

    public void setSpecies(Species species) {
        this.species = species;
        this.entityData.set(DATA_SPECIES, species.id().toString());
        this.definition = null;
        this.appliedGeneration = -1;
        this.appliedScale = Float.NaN;
        this.appliedCategory = null;
        this.goalsBuilt = false;
        this.refreshSpecies();
    }

    public String getVariant() {
        return this.entityData.get(DATA_VARIANT);
    }

    public void setVariant(String variant) {
        this.entityData.set(DATA_VARIANT, variant == null || variant.isBlank() ? DEFAULT_VARIANT : variant);
    }

    public void onSpeciesDataReloaded() {
        this.appliedGeneration = -1;
        this.goalsBuilt = false;
        this.refreshSpecies();
    }

    public SpeciesDefinition speciesDefinition() {
        SpeciesDefinition cached = this.definition;
        return cached != null ? cached : SpeciesDefinition.FALLBACK;
    }

    public boolean hasResolvedSpecies() {
        return this.definition != null;
    }

    public SpeciesEntityCategory speciesCategory() {
        return this.speciesDefinition().category();
    }

    public SpeciesSoundDefinition speciesSounds() {
        return this.speciesDefinition().sounds();
    }

    public SpeciesDietProperties diet() {
        return this.speciesDefinition().diet();
    }

    private void refreshSpecies() {
        SpeciesManager manager = this.level().isClientSide() ? SpeciesManager.CLIENT : SpeciesManager.SERVER;
        int generation = manager.generation();
        if (generation == this.appliedGeneration) {
            return;
        }
        this.appliedGeneration = generation;

        SpeciesDefinition resolved = manager.getOptional(this.species.id()).orElse(null);
        this.definition = resolved;

        if (resolved == null) {
            if (WARNED_MISSING.add(this.species.id())) {
                Constants.LOG.warn("No species definition for '{}'. Creatures of this species will stay inert with default size and attributes until the datapack providing it is loaded.", this.species.id());
            }
            this.clearGoals();
            this.goalsBuilt = false;
            this.refreshDimensionsForNewDefinition();
            return;
        }

        this.applyMovementDomain(resolved);
        if (!this.level().isClientSide()) {
            this.applyAttributes(resolved);
            this.rebuildGoals(resolved);
        }
        this.refreshDimensionsForNewDefinition();
    }

    private void applyAttributes(SpeciesDefinition definition) {
        float healthFraction = this.getMaxHealth() > 0 ? this.getHealth() / this.getMaxHealth() : 1.0F;
        boolean changedMaxHealth = false;

        List<SpeciesAttributeProperties.Resolved> resolved = definition.attributes().resolve().result().orElse(List.of());
        for (SpeciesAttributeProperties.Resolved entry : resolved) {
            AttributeInstance instance = this.getAttribute(entry.attribute());
            if (instance == null) {
                Constants.LOG.warn("Species '{}' sets attribute '{}', which the generic creature does not have; ignoring.", this.species.id(), entry.attribute().getRegisteredName());
                continue;
            }
            if (entry.clamped()) {
                Constants.LOG.warn("Species '{}' has an out-of-range value for attribute '{}'; clamped to {}.", this.species.id(), entry.attribute().getRegisteredName(), entry.value());
            }
            if (instance.getBaseValue() != entry.value()) {
                instance.setBaseValue(entry.value());
                changedMaxHealth |= entry.attribute().equals(Attributes.MAX_HEALTH);
            }
        }

        if (changedMaxHealth) {
            this.setHealth(Math.max(1.0F, healthFraction * this.getMaxHealth()));
        }
    }

    private void applyMovementDomain(SpeciesDefinition definition) {
        SpeciesEntityCategory category = definition.category();
        if (category == this.appliedCategory) {
            return;
        }
        this.appliedCategory = category;

        switch (category) {
            case AQUATIC -> {
                this.navigation = new WaterBoundPathNavigation(this, this.level());
                this.moveControl = new SmoothSwimmingMoveControl(this, 85, 10, 0.04F, 0.1F, true);
                this.lookControl = new SmoothSwimmingLookControl(this, 10);
                this.setPathfindingMalus(PathType.WATER, 0.0F);
            }
            case FLYING -> {
                FlyingPathNavigation flying = new FlyingPathNavigation(this, this.level());
                flying.setCanOpenDoors(false);
                flying.setCanFloat(true);
                this.navigation = flying;
                this.moveControl = new FlyingMoveControl(this, 20, true);
                this.lookControl = new LookControl(this);
                this.setPathfindingMalus(PathType.WATER, -1.0F);
            }
            case LAND -> {
                this.navigation = new GroundPathNavigation(this, this.level());
                this.moveControl = new MoveControl(this);
                this.lookControl = new LookControl(this);
                this.setPathfindingMalus(PathType.WATER, PathType.WATER.getMalus());
            }
        }
    }

    @Override
    public boolean canBreatheUnderwater() {
        return this.speciesCategory().isAquatic();
    }

    @Override
    public boolean isPushedByFluid() {
        return !this.speciesCategory().isAquatic();
    }

    @Override
    public boolean causeFallDamage(double distance, float multiplier, DamageSource source) {
        if (this.speciesCategory().isFlying()) {
            return false;
        }
        return super.causeFallDamage(distance, multiplier, source);
    }

    @Override
    protected void registerGoals() {
    }

    private void clearGoals() {
        this.goalSelector.removeAllGoals(goal -> true);
        this.targetSelector.removeAllGoals(goal -> true);
    }

    private void rebuildGoals(SpeciesDefinition definition) {
        this.clearGoals();

        for (CreatureBehaviorComponent component : definition.resolvedBehaviors()) {
            Goal goal;
            try {
                goal = component.config().createGoal(this);
            } catch (RuntimeException e) {
                Constants.LOG.error("Behavior component '{}' of species '{}' failed to build a goal; skipping it.", component.config().type(), this.species.id(), e);
                continue;
            }
            if (goal == null) {
                continue;
            }
            if (component.slot() == CreatureBehaviorConfig.GoalSlot.TARGET) {
                this.targetSelector.addGoal(component.priority(), goal);
            } else {
                this.goalSelector.addGoal(component.priority(), goal);
            }
        }
        this.goalsBuilt = true;
    }

    @Override
    protected EntityDimensions getDefaultDimensions(Pose pose) {
        SpeciesDefinition definition = this.definition;
        if (definition == null) {
            return super.getDefaultDimensions(pose);
        }
        return definition.dimensionsFor(this.growthScale());
    }

    @Override
    public float getAgeScale() {
        return this.growthScale();
    }

    private float growthScale() {
        return this.speciesDefinition().scaleFor(this.isBaby());
    }

    /**
     * Resizes after a growth-scale change (a baby ageing up, {@code /data} editing Age).
     *
     * <p>Only tracks the scale, so it cannot see a datapack edit to {@code physical}: the scale is
     * still 1.0 either side of a reload. Definition changes go through
     * {@link #refreshDimensionsForNewDefinition()} instead.
     */
    private void updateDimensionsIfNeeded() {
        float scale = this.growthScale();
        if (this.appliedScale != scale) {
            this.appliedScale = scale;
            this.refreshDimensions();
        }
    }

    /**
     * Resizes unconditionally because the definition itself changed — a datapack reload or a species
     * swap. Width, height and eye height all come from {@code physical}, and editing those is the
     * normal iteration loop for a pack author, so this must not be gated on the growth scale.
     */
    private void refreshDimensionsForNewDefinition() {
        this.appliedScale = this.growthScale();
        this.refreshDimensions();
    }

    public AncientCreatureAction getAction() {
        return AncientCreatureAction.byId(this.entityData.get(DATA_ACTION));
    }

    public void setAction(AncientCreatureAction action) {
        if (!this.level().isClientSide()) {
            this.entityData.set(DATA_ACTION, action.id());
            this.actionTicks = 0;
        }
    }

    public void setAction(AncientCreatureAction action, int ticks) {
        if (!this.level().isClientSide()) {
            this.entityData.set(DATA_ACTION, action.id());
            this.actionTicks = Math.max(1, ticks);
        }
    }

    public void clearAction(AncientCreatureAction expected) {
        if (!this.level().isClientSide() && this.getAction() == expected) {
            this.entityData.set(DATA_ACTION, AncientCreatureAction.NONE.id());
        }
    }

    /**
     * Debug-only: repeatedly replay {@code action}, or stop repeating when given {@code null}.
     *
     * <p>The client's animation controller only restarts a clip when the state it is in changes, so
     * simply holding the action would play a non-looping clip once and then sit in the rest pose. This
     * drops back to {@link AncientCreatureAction#NONE} for {@link #LOOP_GAP_TICKS} between plays so the
     * controller genuinely leaves and re-enters the state.
     */
    public void setLoopAction(@Nullable AncientCreatureAction action, int period) {
        if (this.level().isClientSide()) {
            return;
        }
        this.loopAction = action == null || action == AncientCreatureAction.NONE ? null : action;
        this.loopPeriod = Math.max(1, period);
        this.loopGap = 0;
        if (this.loopAction == null) {
            this.entityData.set(DATA_ACTION, AncientCreatureAction.NONE.id());
            this.actionTicks = 0;
        } else {
            this.setAction(this.loopAction, this.loopPeriod);
        }
    }

    public @Nullable AncientCreatureAction getLoopAction() {
        return this.loopAction;
    }

    private void tickActionLoop() {
        if (this.loopAction == null || this.getAction() != AncientCreatureAction.NONE) {
            return;
        }
        if (this.loopGap < LOOP_GAP_TICKS) {
            this.loopGap++;
            return;
        }
        this.loopGap = 0;
        this.setAction(this.loopAction, this.loopPeriod);
    }

    @Override
    public void tick() {
        super.tick();
        this.refreshSpecies();
        this.updateDimensionsIfNeeded();

        if (!this.level().isClientSide() && this.actionTicks > 0 && --this.actionTicks == 0) {
            this.entityData.set(DATA_ACTION, AncientCreatureAction.NONE.id());
        }

        if (!this.level().isClientSide()) {
            this.tickActionLoop();
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide() && this.hasResolvedSpecies() && !this.goalsBuilt) {
            this.rebuildGoals(this.speciesDefinition());
        }
    }

    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return this.speciesSounds().ambientSound();
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource source) {
        return this.speciesSounds().hurtSound();
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return this.speciesSounds().deathSound();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState blockState) {
        SoundEvent step = this.speciesSounds().stepSound();
        if (step != null) {
            this.playSound(step, 0.35F, 0.9F + this.random.nextFloat() * 0.15F);
        } else {
            super.playStepSound(pos, blockState);
        }
    }

    @Override
    protected void playAttackSound() {
        this.setAction(AncientCreatureAction.ATTACK, 8);

        SoundEvent attack = this.speciesSounds().attackSound();
        if (attack != null) {
            this.playSound(attack, 1.0F, 0.9F + this.random.nextFloat() * 0.1F);
        } else {
            super.playAttackSound();
        }
    }

    @Override
    public int getAmbientSoundInterval() {
        return this.speciesSounds().ambientInterval();
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return this.diet().test(stack);
    }

    @Override
    public boolean canMate(Animal partner) {
        return partner instanceof AncientCreatureEntity other && other.getSpecies().equals(this.getSpecies()) && super.canMate(partner);
    }

    @Override
    public @Nullable AncientCreatureEntity getBreedOffspring(ServerLevel level, AgeableMob partner) {
        AncientCreatureEntity offspring = ModEntities.ANCIENT_CREATURE.get().create(level, EntitySpawnReason.BREEDING);
        if (offspring != null) {
            offspring.setSpecies(this.getSpecies());
            offspring.setVariant(this.getVariant());
        }
        return offspring;
    }

    @Override
    public @Nullable Species speciesForBreedingEgg() {
        return this.hasResolvedSpecies() ? this.getSpecies() : null;
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.store(SPECIES_TAG, Species.CODEC, this.species);
        output.putString(VARIANT_TAG, this.getVariant());
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        Species stored = input.read(SPECIES_TAG, Species.CODEC).orElse(this.species);
        this.setVariant(input.getStringOr(VARIANT_TAG, DEFAULT_VARIANT));
        this.setSpecies(stored);

        super.readAdditionalSaveData(input);
    }

    @Override
    public void setBaby(boolean baby) {
        super.setBaby(baby);
        this.updateDimensionsIfNeeded();
    }

    @Override
    public void setAge(int age) {
        super.setAge(age);
        this.updateDimensionsIfNeeded();
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        if (target instanceof AncientCreatureEntity other && other.getSpecies().equals(this.getSpecies())) {
            return false;
        }
        return super.canAttack(target);
    }

    @Override
    public Component getName() {
        return this.species.displayName();
    }
}
