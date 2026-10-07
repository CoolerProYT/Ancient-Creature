package com.coolerpromc.ancientcreature.entity.custom;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.config.ModCommonConfig;
import com.coolerpromc.ancientcreature.data.component.custom.CreatureGenome;
import com.coolerpromc.ancientcreature.entity.ModEntities;
import com.coolerpromc.ancientcreature.entity.Species;
import com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorComponent;
import com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorConfig;
import com.coolerpromc.ancientcreature.entity.comfort.ComfortTracker;
import com.coolerpromc.ancientcreature.entity.goal.CommandGoals;
import com.coolerpromc.ancientcreature.entity.goal.RampageGoal;
import com.coolerpromc.ancientcreature.entity.riding.MountAbilities;
import com.coolerpromc.ancientcreature.item.custom.CreatureArmorItem;
import com.coolerpromc.ancientcreature.item.custom.CreatureSaddleItem;
import net.minecraft.sounds.SoundEvents;
import com.coolerpromc.ancientcreature.species.*;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
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
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AncientCreatureEntity extends OwnedAncientCreature {
    private static final EntityDataAccessor<String> DATA_SPECIES = SynchedEntityData.defineId(AncientCreatureEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> DATA_VARIANT = SynchedEntityData.defineId(AncientCreatureEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Byte> DATA_ACTION = SynchedEntityData.defineId(AncientCreatureEntity.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Float> DATA_HUNGER = SynchedEntityData.defineId(AncientCreatureEntity.class, EntityDataSerializers.FLOAT);
    /** Client-visible state bits: see the FLAG_ constants. */
    private static final EntityDataAccessor<Float> DATA_COMFORT = SynchedEntityData.defineId(AncientCreatureEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Byte> DATA_COMFORT_REASON = SynchedEntityData.defineId(AncientCreatureEntity.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Byte> DATA_ARMOR_TIER = SynchedEntityData.defineId(AncientCreatureEntity.class, EntityDataSerializers.BYTE);
    /** The genome's size factor, synced because it decides the bounding box and the rendered size. */
    private static final EntityDataAccessor<Float> DATA_GENOME_SIZE = SynchedEntityData.defineId(AncientCreatureEntity.class, EntityDataSerializers.FLOAT);
    /** Fidelity percent (bits 0-7), temperament (8-9), fertile (10), frail (11): what Jade shows. */
    private static final EntityDataAccessor<Integer> DATA_GENOME_INFO = SynchedEntityData.defineId(AncientCreatureEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Byte> DATA_FLAGS = SynchedEntityData.defineId(AncientCreatureEntity.class, EntityDataSerializers.BYTE);

    protected static final int FLAG_OWNED = 1;
    protected static final int FLAG_SADDLED = 1 << 1;
    protected static final int FLAG_SITTING = 1 << 2;

    private static final String SPECIES_TAG = "Species";
    private static final String VARIANT_TAG = "Variant";
    private static final String HUNGER_TAG = "Hunger";
    private static final String COMMAND_TAG = "Command";
    private static final String COMFORT_TAG = "Comfort";
    private static final String SADDLE_TAG = "Saddle";
    private static final String ARMOR_TAG = "CreatureArmor";
    private static final String GENOME_TAG = "Genome";
    private static final Identifier GENOME_VITALITY = Constants.id("genome_vitality");
    private static final Identifier GENOME_VIGOR = Constants.id("genome_vigor");
    private static final Identifier GENOME_STRENGTH = Constants.id("genome_strength");
    /** Null for creatures made before genetics, and spawned by command: they read as {@link CreatureGenome#neutral}. */
    private @Nullable CreatureGenome genome;

    private static final Identifier ARMOR_MODIFIER = Constants.id("creature_armor");

    /** Saddle in slot 0, barding in slot 1. */
    private ItemStack saddle = ItemStack.EMPTY;
    private ItemStack armor = ItemStack.EMPTY;
    private int abilityCooldown;
    /** Damage a pounce or dive deals on contact, while it is in the air. */
    private float pounceStrike;

    /** Comfort at or above which a creature breeds. */
    public static final float BREEDING_COMFORT = 50.0F;
    /** Comfort at or above which a creature slowly heals. */
    public static final float CONTENT_COMFORT = 75.0F;
    /** Comfort below which a creature is distressed. */
    public static final float DISTRESS_COMFORT = 30.0F;

    private final ComfortTracker comfortTracker = new ComfortTracker(this);

    /** How far a creature told to roam may wander from where it was told. */
    public static final int ROAM_RADIUS = 24;

    private CreatureCommand command = CreatureCommand.ROAM;

    public static final String DEFAULT_VARIANT = "default";

    private static final Set<Identifier> WARNED_MISSING = java.util.Collections.synchronizedSet(new HashSet<>());

    private Species species = Species.TRICERATOPS;
    private @Nullable SpeciesDefinition definition;
    private int appliedGeneration = -1;
    private float appliedScale = Float.NaN;
    private boolean goalsBuilt;
    private int actionTicks;
    private @Nullable SpeciesEntityCategory appliedCategory;

    /** Counts down to the next single point of hunger lost, and to the next starvation tick. */
    private int hungerTicks;
    private int starveTicks;

    /**
     * Whether hunger came from saved data. A creature restored from NBT keeps the meter it had; one that
     * has never been saved starts full, which needs the species max and so cannot be done in
     * {@code defineSynchedData}.
     */
    private boolean hungerRestored;

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
            .add(Attributes.FLYING_SPEED, 0.4)
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
        builder.define(DATA_HUNGER, SpeciesHungerProperties.DEFAULT.max());
        builder.define(DATA_FLAGS, (byte) 0);
        builder.define(DATA_ARMOR_TIER, (byte) 0);
        builder.define(DATA_COMFORT, ComfortTracker.START);
        builder.define(DATA_COMFORT_REASON, (byte) 0);
        builder.define(DATA_GENOME_SIZE, 1.0F);
        builder.define(DATA_GENOME_INFO, packGenomeInfo(CreatureGenome.neutral(Species.TRICERATOPS)));
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
        // a stored genome of another species no longer applies; re-derive the traits
        this.setGenome(this.genome);
    }

    public String getVariant() {
        return this.entityData.get(DATA_VARIANT);
    }

    public void setVariant(String variant) {
        this.entityData.set(DATA_VARIANT, variant == null || variant.isBlank() ? DEFAULT_VARIANT : variant);
    }

    // ------------------------------------------------------------------ gear

    public ItemStack getSaddle() {
        return this.saddle;
    }

    public ItemStack getCreatureArmor() {
        return this.armor;
    }

    /** {@code query.armor_tier}: 0 without barding, otherwise the tier's index. */
    public int getArmorTier() {
        return this.entityData.get(DATA_ARMOR_TIER);
    }

    public void setSaddle(ItemStack stack) {
        this.saddle = stack.copyWithCount(stack.isEmpty() ? 0 : 1);
        if (!this.level().isClientSide()) {
            this.setFlag(FLAG_SADDLED, !this.saddle.isEmpty());
        }
    }

    public void setCreatureArmor(ItemStack stack) {
        this.armor = stack.isEmpty() ? ItemStack.EMPTY : stack.copyWithCount(1);
        if (!this.level().isClientSide()) {
            CreatureArmorItem.Tier tier = this.armor.getItem() instanceof CreatureArmorItem item ? item.tier() : null;
            this.entityData.set(DATA_ARMOR_TIER, (byte) (tier == null ? 0 : tier.index));
            this.applyArmorModifiers(tier);
        }
    }

    private void applyArmorModifiers(CreatureArmorItem.@Nullable Tier tier) {
        setModifier(Attributes.ARMOR, tier == null ? 0 : tier.armor);
        setModifier(Attributes.ARMOR_TOUGHNESS, tier == null ? 0 : tier.toughness);
        setModifier(Attributes.KNOCKBACK_RESISTANCE, tier == null ? 0 : tier.knockbackResistance);
    }

    private void setModifier(net.minecraft.core.Holder<net.minecraft.world.entity.ai.attributes.Attribute> attribute, double amount) {
        AttributeInstance instance = this.getAttribute(attribute);
        if (instance == null) {
            return;
        }
        instance.removeModifier(ARMOR_MODIFIER);
        if (amount != 0) {
            instance.addPermanentModifier(new net.minecraft.world.entity.ai.attributes.AttributeModifier(ARMOR_MODIFIER, amount,
                net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_VALUE));
        }
    }

    /** Whether the owner may mount: adult, and saddled unless the config says otherwise. */
    public boolean canBeRiddenBy(Player player) {
        return this.isOwnedBy(player) && !this.isBaby() && !this.isVehicle()
            && (this.isSaddled() || !ModCommonConfig.CONFIG.ridingRequiresSaddle.get());
    }

    @Override
    protected void dropEquipment(ServerLevel level) {
        super.dropEquipment(level);
        if (!this.saddle.isEmpty()) {
            this.spawnAtLocation(level, this.saddle);
            this.setSaddle(ItemStack.EMPTY);
        }
        if (!this.armor.isEmpty()) {
            this.spawnAtLocation(level, this.armor);
            this.setCreatureArmor(ItemStack.EMPTY);
        }
    }

    // ------------------------------------------------------------------ mount ability

    public int getAbilityCooldown() {
        return this.abilityCooldown;
    }

    /** Called from the rider's key press, already validated as coming from this creature's rider. */
    public void tryMountAbility(Player rider) {
        if (!(this.level() instanceof ServerLevel level) || this.getControllingPassenger() != rider || !this.isOwnedBy(rider)) {
            return;
        }
        SpeciesRidingProperties riding = this.speciesDefinition().riding();
        if (riding.ability() == SpeciesRidingProperties.Ability.NONE) {
            rider.sendOverlayMessage(Component.translatable("message.ancientcreature.ability.none", this.getName()));
            return;
        }
        if (this.abilityCooldown > 0) {
            rider.sendOverlayMessage(Component.translatable("message.ancientcreature.ability.cooldown", (this.abilityCooldown + 19) / 20));
            return;
        }
        this.abilityCooldown = riding.cooldown();
        MountAbilities.perform(level, this, rider);
    }

    public void setPounceStrike(float damage) {
        this.pounceStrike = damage;
    }

    private void tickMountAbility(ServerLevel level) {
        if (this.abilityCooldown > 0) {
            this.abilityCooldown--;
        }
        if (this.pounceStrike > 0.0F) {
            MountAbilities.strikeOnContact(level, this, this.pounceStrike);
            if (this.onGround() || this.isInWater()) {
                this.pounceStrike = 0.0F;
            }
        }
    }

    public float getComfort() {
        return this.entityData.get(DATA_COMFORT);
    }

    public void setComfort(float comfort) {
        this.entityData.set(DATA_COMFORT, Math.max(0.0F, Math.min(100.0F, comfort)));
    }

    public ComfortTracker.Factor getComfortReason() {
        int index = this.entityData.get(DATA_COMFORT_REASON);
        ComfortTracker.Factor[] values = ComfortTracker.Factor.values();
        return index >= 0 && index < values.length ? values[index] : ComfortTracker.Factor.NONE;
    }

    public static boolean comfortEnabled() {
        return ModCommonConfig.CONFIG.comfortEnabled.get();
    }

    /** Distressed adults may rampage through weak enclosures. */
    public boolean isDistressed() {
        return comfortEnabled() && !this.isBaby() && this.getComfort() < DISTRESS_COMFORT;
    }

    private void tickComfort(ServerLevel level) {
        if (!comfortEnabled() || (this.tickCount + this.getId()) % ComfortTracker.UPDATE_INTERVAL != 0) {
            return;
        }
        ComfortTracker.Result result = this.comfortTracker.evaluate(level);
        // calm animals settle more easily than fierce ones
        float temperament = switch (this.getTemperament()) {
            case CALM -> 5.0F;
            case STEADY -> 0.0F;
            case FIERCE -> -5.0F;
        };
        float target = Math.max(0.0F, Math.min(100.0F, result.target() + temperament));
        this.setComfort(ComfortTracker.drift(this.getComfort(), target));
        this.entityData.set(DATA_COMFORT_REASON, (byte) result.reason().ordinal());
        if (this.getComfort() >= CONTENT_COMFORT && this.getHealth() < this.getMaxHealth() && !this.hungerProperties().isHungryAt(this.getHunger())) {
            this.heal(1.0F);
        }
    }

    protected boolean getFlag(int flag) {
        return (this.entityData.get(DATA_FLAGS) & flag) != 0;
    }

    protected void setFlag(int flag, boolean value) {
        byte flags = this.entityData.get(DATA_FLAGS);
        this.entityData.set(DATA_FLAGS, (byte) (value ? flags | flag : flags & ~flag));
    }

    /** Whether the creature has an owner, as far as the client knows. The owner's identity stays server-side. */
    public boolean hasOwnerClientSide() {
        return this.getFlag(FLAG_OWNED);
    }

    public boolean isSaddled() {
        return this.getFlag(FLAG_SADDLED);
    }

    public boolean isSitting() {
        return this.getFlag(FLAG_SITTING);
    }

    @Override
    protected void onOwnerChanged() {
        if (!this.level().isClientSide()) {
            this.setFlag(FLAG_OWNED, this.getOwnerReference() != null);
        }
    }

    public CreatureCommand getCommand() {
        return this.hasOwnerClientSide() || this.getOwnerReference() != null ? this.command : CreatureCommand.ROAM;
    }

    /**
     * Applies an owner's command. Roaming anchors the home range at the creature's current position;
     * the other commands lift it.
     */
    public void setCommand(CreatureCommand command) {
        this.command = command;
        if (command == CreatureCommand.ROAM) {
            this.setHomeTo(this.blockPosition(), ROAM_RADIUS);
        } else {
            this.clearHome();
        }
        if (command == CreatureCommand.STAY) {
            this.getNavigation().stop();
            this.setTarget(null);
        }
        if (!this.level().isClientSide()) {
            this.setFlag(FLAG_SITTING, command == CreatureCommand.STAY);
        }
    }

    /** Whether the species has any way to attack, so it can be asked to defend its owner. */
    public boolean canFight() {
        for (CreatureBehaviorComponent component : this.speciesDefinition().resolvedBehaviors()) {
            var type = component.config().type();
            if (type == com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorRegistry.MELEE_ATTACK
                || type == com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorRegistry.CHARGE_ATTACK
                || type == com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorRegistry.ROAR_ATTACK
                || type == com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorRegistry.AQUATIC_PREDATOR) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gives the creature a skin from its species' weighted variant list, unless it already has one that
     * the species knows. Called whenever a creature comes into being: hatching, capsule release,
     * breeding and commands.
     */
    public void rollVariantIfUnset() {
        if (this.level().isClientSide() || !this.hasResolvedSpecies()) {
            return;
        }
        SpeciesDefinition definition = this.speciesDefinition();
        if (!definition.variants().isEmpty() && definition.variantIndex(this.getVariant()) < 0) {
            this.setVariant(definition.rollVariant(this.random));
        }
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

    public SpeciesHungerProperties hungerProperties() {
        return this.speciesDefinition().hunger();
    }

    /** How fed this creature is. High is full; zero is starving. */
    public float getHunger() {
        return this.entityData.get(DATA_HUNGER);
    }

    public void setHunger(float hunger) {
        if (!this.level().isClientSide()) {
            this.hungerRestored = true;
            this.entityData.set(DATA_HUNGER, this.hungerProperties().clamp(hunger));
        }
    }

    /**
     * Whether this creature is hungry enough to go looking for prey.
     *
     * <p>Only prey <em>selection</em> consults this. Retaliation, territorial defence and the attack goals
     * themselves never do, so a well-fed creature still defends itself and still drives off armed players.
     *
     * <p>Once a hunt starts the creature stays interested until eating brings it up to
     * {@code full_threshold}, rather than losing its target the instant it crosses {@code hunt_threshold}
     * again. Without that gap a predator mid-chase would drop and reacquire its target every tick.
     */
    public boolean wantsToHunt() {
        if (this.isForcedPassive()) {
            return false;
        }
        SpeciesHungerProperties hunger = this.hungerProperties();
        if (hunger.isSatedAt(this.getHunger())) {
            return false;
        }
        return hunger.isHungryAt(this.getHunger()) || this.getTarget() != null;
    }

    /** Restores hunger and plays the eating action. Returns whether anything was actually eaten. */
    public boolean feed(float amount) {
        if (this.level().isClientSide() || amount <= 0.0F) {
            return false;
        }
        float before = this.getHunger();
        if (before >= this.hungerProperties().max()) {
            return false;
        }
        this.setHunger(before + amount);
        this.setAction(AncientCreatureAction.EAT, 20);
        return true;
    }

    private void tickHunger() {
        SpeciesHungerProperties hunger = this.hungerProperties();

        if (++this.hungerTicks >= hunger.decayInterval()) {
            this.hungerTicks = 0;
            float next = hunger.clamp(this.getHunger() - 1.0F);
            if (next != this.getHunger()) {
                this.entityData.set(DATA_HUNGER, next);
            }
        }

        if (!hunger.starves() || this.getHunger() > 0.0F) {
            this.starveTicks = 0;
            return;
        }
        if (++this.starveTicks >= hunger.starveInterval()) {
            this.starveTicks = 0;
            this.hurtServer((ServerLevel) this.level(), this.damageSources().starve(), hunger.starveDamage());
        }
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
            this.applyHunger(resolved);
            this.rebuildGoals(resolved);
        }
        this.refreshDimensionsForNewDefinition();
    }

    /**
     * A creature that was never saved starts fed; one restored from disk keeps what it had, clamped in
     * case the datapack lowered {@code hunger.max} since.
     */
    private void applyHunger(SpeciesDefinition definition) {
        SpeciesHungerProperties hunger = definition.hunger();
        if (!this.hungerRestored) {
            this.hungerRestored = true;
            this.entityData.set(DATA_HUNGER, hunger.max());
            return;
        }
        float clamped = hunger.clamp(this.getHunger());
        if (clamped != this.getHunger()) {
            this.entityData.set(DATA_HUNGER, clamped);
        }
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
        return this.speciesDefinition().respiration() != SpeciesRespiration.AIR;
    }

    /** Ticks a gill-breather survives on land; vanilla fish use the same 300-tick air supply. */
    @Override
    public void baseTick() {
        int air = this.getAirSupply();
        super.baseTick();
        if (!this.level().isClientSide() && this.isAlive() && this.hasResolvedSpecies()
            && this.speciesDefinition().respiration() == SpeciesRespiration.WATER) {
            if (this.isInWater()) {
                this.setAirSupply(this.getMaxAirSupply());
            } else {
                this.setAirSupply(air - 1);
                if (this.getAirSupply() <= -20) {
                    this.setAirSupply(0);
                    this.hurtServer((ServerLevel) this.level(), this.damageSources().dryOut(), 2.0F);
                }
            }
        }
    }

    /** A stranded gill-breather thrashes towards water, the way vanilla fish flop. */
    private void tickStranded() {
        if (this.speciesDefinition().respiration() == SpeciesRespiration.WATER && !this.isInWater() && this.onGround() && !this.isVehicle()
            && this.random.nextInt(Math.max(4, (int) (8 * this.getBbWidth()))) == 0) {
            this.setDeltaMovement(this.getDeltaMovement().add((this.random.nextFloat() * 2.0F - 1.0F) * 0.05F, 0.3F, (this.random.nextFloat() * 2.0F - 1.0F) * 0.05F));
            this.setOnGround(false);
            this.needsSync = true;
        }
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

        // Owner commands sit above the species' own behaviour; they idle for unowned creatures.
        double travel = definition.category().isAquatic() ? 1.0 : 1.15;
        this.goalSelector.addGoal(1, new CommandGoals.Stay(this));
        this.goalSelector.addGoal(4, new CommandGoals.FollowOwner(this, travel));
        this.goalSelector.addGoal(5, new CommandGoals.ReturnHome(this, 1.0));
        this.targetSelector.addGoal(1, new CommandGoals.DefendOwner(this));
        if (this.speciesCategory() == SpeciesEntityCategory.LAND) {
            this.goalSelector.addGoal(3, new RampageGoal(this));
        }

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
        return this.speciesDefinition().scaleFor(this.isBaby()) * this.entityData.get(DATA_GENOME_SIZE);
    }

    // ------------------------------------------------------------------ genome

    public CreatureGenome getGenome() {
        CreatureGenome stored = this.genome;
        return stored != null && stored.species().equals(this.getSpecies()) ? stored : CreatureGenome.neutral(this.getSpecies());
    }

    /** Sets the genome and applies its traits. Null gives the neutral genome of the creature's species. */
    public void setGenome(@Nullable CreatureGenome genome) {
        this.genome = genome;
        CreatureGenome applied = this.getGenome();
        this.entityData.set(DATA_GENOME_SIZE, applied.size());
        this.entityData.set(DATA_GENOME_INFO, packGenomeInfo(applied));
        if (!this.level().isClientSide()) {
            this.applyGenomeModifiers(applied);
        }
    }

    private void applyGenomeModifiers(CreatureGenome genome) {
        setMultiplier(Attributes.MAX_HEALTH, GENOME_VITALITY, genome.vitality() - 1.0F);
        setMultiplier(Attributes.MOVEMENT_SPEED, GENOME_VIGOR, genome.vigor() - 1.0F);
        // bigger animals hit harder
        setMultiplier(Attributes.ATTACK_DAMAGE, GENOME_STRENGTH, (genome.size() - 1.0F) * 0.75F);
        if (this.getHealth() > this.getMaxHealth()) {
            this.setHealth(this.getMaxHealth());
        }
    }

    private void setMultiplier(net.minecraft.core.Holder<net.minecraft.world.entity.ai.attributes.Attribute> attribute, Identifier id, float amount) {
        AttributeInstance instance = this.getAttribute(attribute);
        if (instance == null) {
            return;
        }
        instance.removeModifier(id);
        if (Math.abs(amount) > 1.0E-4F) {
            instance.addTransientModifier(new net.minecraft.world.entity.ai.attributes.AttributeModifier(id, amount,
                net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        }
    }

    private static int packGenomeInfo(CreatureGenome genome) {
        return Math.round(genome.fidelity() * 100.0F)
            | genome.temperament().ordinal() << 8
            | (genome.fertile() ? 1 << 10 : 0)
            | (genome.frail() ? 1 << 11 : 0);
    }

    /** Client-safe genome readouts, from synced data. */
    public int getGenomeFidelityPercent() {
        return this.entityData.get(DATA_GENOME_INFO) & 0xFF;
    }

    public CreatureGenome.Temperament getTemperament() {
        int index = this.entityData.get(DATA_GENOME_INFO) >> 8 & 3;
        CreatureGenome.Temperament[] values = CreatureGenome.Temperament.values();
        return values[Math.min(index, values.length - 1)];
    }

    public boolean isFertile() {
        return (this.entityData.get(DATA_GENOME_INFO) & 1 << 10) != 0;
    }

    public boolean isFrail() {
        return (this.entityData.get(DATA_GENOME_INFO) & 1 << 11) != 0;
    }

    private void updateDimensionsIfNeeded() {
        float scale = this.growthScale();
        if (this.appliedScale != scale) {
            this.appliedScale = scale;
            this.refreshDimensions();
        }
    }

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
            if (this.hasResolvedSpecies()) {
                this.tickHunger();
                this.tickComfort((ServerLevel) this.level());
                this.tickMountAbility((ServerLevel) this.level());
            }
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide() && this.hasResolvedSpecies() && !this.goalsBuilt) {
            this.rebuildGoals(this.speciesDefinition());
        }
        if (!this.level().isClientSide() && this.hasResolvedSpecies()) {
            this.tickStranded();
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

    /**
     * All species share one entity type, so its registry loot table cannot identify the creature that
     * died. Route the normal vanilla death-loot pipeline through the table selected by species data.
     * The four-argument overload still builds the complete entity loot context, including the damage
     * source, attacker and looting level.
     */
    @Override
    protected void dropFromLootTable(ServerLevel level, DamageSource source, boolean causedByPlayer) {
        this.speciesDefinition().lootTable().ifPresent(id -> {
            ResourceKey<LootTable> lootTable = ResourceKey.create(Registries.LOOT_TABLE, id);
            super.dropFromLootTable(level, source, causedByPlayer, lootTable);
        });
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

    /**
     * Eating a kill is what refills a predator, and is the other half of gating hunting on hunger — without
     * it a hungry creature would hunt forever and never be satisfied.
     */
    @Override
    public boolean killedEntity(ServerLevel level, LivingEntity killed, DamageSource source) {
        boolean result = super.killedEntity(level, killed, source);
        this.feed(this.hungerProperties().killValue());
        return result;
    }

    /**
     * Hand-feeding tops the creature up as well as breeding it.
     *
     * <p>Breeding is left to {@code Animal}, which consumes the item and sets the creature in love. Only
     * when it declines — an adult on breeding cooldown, say — does this fall through to plain feeding, so
     * a player can still keep a fed creature out of a hunting mood.
     */
    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);
        boolean wasFood = this.isFood(held);

        InteractionResult result = super.mobInteract(player, hand);
        if (result.consumesAction()) {
            if (wasFood) {
                this.feed(this.hungerProperties().foodValue());
                this.comfortTracker.onHandFed();
            }
            return result;
        }

        if (!wasFood && this.isOwnedBy(player) && !this.isBaby()) {
            InteractionResult gear = this.interactGear(player, held);
            if (gear != null) {
                return gear;
            }
        }

        if (!wasFood && !player.isSecondaryUseActive() && this.isOwnedBy(player) && !this.isBaby() && !this.isVehicle()) {
            if (this.canBeRiddenBy(player)) {
                if (!this.level().isClientSide()) {
                    player.startRiding(this);
                }
                return InteractionResult.SUCCESS;
            }
            if (held.isEmpty() && !this.level().isClientSide()) {
                player.sendOverlayMessage(Component.translatable("message.ancientcreature.needs_saddle", this.getName()));
            }
        }

        if (!wasFood || this.getHunger() >= this.hungerProperties().max()) {
            return result;
        }

        if (!this.level().isClientSide()) {
            this.feed(this.hungerProperties().foodValue());
            this.comfortTracker.onHandFed();
            held.consume(1, player);
        }
        return InteractionResult.SUCCESS;
    }

    /**
     * Ownership is persisted on the server and is not part of the client's synced entity data. The
     * client therefore trusts the server-approved first passenger so it can simulate and send rider
     * input, while the server still verifies that passenger is the owner.
     */
    @Override
    public @Nullable LivingEntity getControllingPassenger() {
        return this.getFirstPassenger() instanceof Player player && (this.level().isClientSide() || this.isOwnedBy(player))
            ? player
            : super.getControllingPassenger();
    }

    @Override
    protected void tickRidden(Player controller, Vec3 riddenInput) {
        super.tickRidden(controller, riddenInput);
        this.setRot(controller.getYRot(), controller.getXRot() * 0.5F);
        this.yRotO = this.yBodyRot = this.yHeadRot = this.getYRot();
    }

    @Override
    protected Vec3 getRiddenInput(Player controller, Vec3 selfInput) {
        if (this.speciesCategory() == SpeciesEntityCategory.LAND) {
            float forward = controller.zza;
            if (forward < 0.0F) {
                forward *= 0.25F;
            }
            return new Vec3(controller.xxa * 0.5F, 0.0, forward);
        }

        float forward = controller.zza;
        float pitchRadians = controller.getXRot() * (float) (Math.PI / 180.0);
        float horizontal = (float) Math.cos(pitchRadians);
        float vertical = (float) -Math.sin(pitchRadians);
        if (forward < 0.0F) {
            horizontal *= -0.25F;
            vertical *= -0.25F;
        } else if (forward == 0.0F) {
            horizontal = 0.0F;
            vertical = 0.0F;
        }

        if (this.speciesCategory().isFlying() && controller.isJumping()) {
            vertical += 0.5F;
        }
        return new Vec3(controller.xxa * 0.5F, vertical, horizontal);
    }

    @Override
    protected float getRiddenSpeed(Player controller) {
        if (this.speciesCategory().isAquatic()) {
            return 0.0325F * (float) this.getAttributeValue(Attributes.MOVEMENT_SPEED);
        }
        if (this.speciesCategory().isFlying()) {
            return (float) this.getAttributeValue(Attributes.FLYING_SPEED);
        }
        return (float) this.getAttributeValue(Attributes.MOVEMENT_SPEED);
    }

    /**
     * Saddle and barding: using one on your adult creature puts it on; sneaking with an empty hand takes
     * the barding off first, then the saddle. Returns null when the interaction is not about gear.
     */
    private @Nullable InteractionResult interactGear(Player player, ItemStack held) {
        if (held.getItem() instanceof CreatureSaddleItem && this.saddle.isEmpty()) {
            if (!this.level().isClientSide()) {
                this.setSaddle(held);
                held.consume(1, player);
                this.level().playSound(null, this, SoundEvents.HORSE_SADDLE.value(), this.getSoundSource(), 1.0F, 0.8F);
            }
            return InteractionResult.SUCCESS;
        }
        if (held.getItem() instanceof CreatureArmorItem && this.armor.isEmpty()) {
            if (!this.level().isClientSide()) {
                this.setCreatureArmor(held);
                held.consume(1, player);
                this.level().playSound(null, this, SoundEvents.HORSE_ARMOR.value(), this.getSoundSource(), 1.0F, 0.8F);
            }
            return InteractionResult.SUCCESS;
        }
        if (held.isEmpty() && player.isSecondaryUseActive() && (!this.armor.isEmpty() || !this.saddle.isEmpty())) {
            if (!this.level().isClientSide()) {
                ItemStack removed;
                if (!this.armor.isEmpty()) {
                    removed = this.armor;
                    this.setCreatureArmor(ItemStack.EMPTY);
                } else {
                    removed = this.saddle;
                    this.setSaddle(ItemStack.EMPTY);
                }
                player.getInventory().placeItemBackInInventory(removed);
                this.level().playSound(null, this, SoundEvents.ARMOR_EQUIP_LEATHER.value(), this.getSoundSource(), 1.0F, 0.8F);
            }
            return InteractionResult.SUCCESS;
        }
        return null;
    }

    @Override
    public boolean canMate(Animal partner) {
        return partner instanceof AncientCreatureEntity other && other.getSpecies().equals(this.getSpecies())
            && this.isFertile() && other.isFertile() && super.canMate(partner);
    }

    @Override
    public @Nullable AncientCreatureEntity getBreedOffspring(ServerLevel level, AgeableMob partner) {
        AncientCreatureEntity offspring = ModEntities.ANCIENT_CREATURE.get().create(level, EntitySpawnReason.BREEDING);
        if (offspring != null) {
            offspring.setSpecies(this.getSpecies());
            // Offspring take a parent's skin, with a small chance of a fresh roll from the species list.
            AncientCreatureEntity other = partner instanceof AncientCreatureEntity creature ? creature : this;
            if (this.random.nextFloat() < 0.1F) {
                offspring.setVariant(this.speciesDefinition().rollVariant(this.random));
            } else {
                offspring.setVariant(this.random.nextBoolean() ? this.getVariant() : other.getVariant());
            }
            offspring.setGenome(CreatureGenome.inherit(this.getGenome(), other.getGenome(), this.random));
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
        output.putFloat(HUNGER_TAG, this.getHunger());
        output.store(COMMAND_TAG, CreatureCommand.CODEC, this.command);
        output.putFloat(COMFORT_TAG, this.getComfort());
        if (!this.saddle.isEmpty()) {
            output.store(SADDLE_TAG, ItemStack.CODEC, this.saddle);
        }
        if (!this.armor.isEmpty()) {
            output.store(ARMOR_TAG, ItemStack.CODEC, this.armor);
        }
        output.storeNullable(GENOME_TAG, CreatureGenome.CODEC, this.genome);
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(net.minecraft.world.level.ServerLevelAccessor level, net.minecraft.world.DifficultyInstance difficulty, EntitySpawnReason reason, @Nullable SpawnGroupData groupData) {
        SpawnGroupData data = super.finalizeSpawn(level, difficulty, reason, groupData);
        this.rollVariantIfUnset();
        return data;
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        Species stored = input.read(SPECIES_TAG, Species.CODEC).orElse(this.species);
        this.setVariant(input.getStringOr(VARIANT_TAG, DEFAULT_VARIANT));

        // Read before setSpecies, because applying the species is what decides whether an unsaved
        // creature starts full — and a creature saved before hunger existed has no tag to restore.
        input.read(HUNGER_TAG, Codec.FLOAT).ifPresent(hunger -> {
            this.hungerRestored = true;
            this.entityData.set(DATA_HUNGER, hunger);
        });

        this.setSpecies(stored);
        // Before super: the vitality modifier has to be in place when the saved health is restored,
        // or a hardy creature's health would be clamped to the species' base maximum.
        this.setGenome(input.read(GENOME_TAG, CreatureGenome.CODEC).orElse(null));

        super.readAdditionalSaveData(input);
        this.setFlag(FLAG_OWNED, this.getOwnerReference() != null);
        this.command = input.read(COMMAND_TAG, CreatureCommand.CODEC).orElse(CreatureCommand.ROAM);
        this.setComfort(input.getFloatOr(COMFORT_TAG, ComfortTracker.START));
        this.setSaddle(input.read(SADDLE_TAG, ItemStack.CODEC).orElse(ItemStack.EMPTY));
        this.setCreatureArmor(input.read(ARMOR_TAG, ItemStack.CODEC).orElse(ItemStack.EMPTY));
        this.setFlag(FLAG_SITTING, this.command == CreatureCommand.STAY && this.getOwnerReference() != null);
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

    /** Ticks since this creature was last shocked by an electric fence; see {@link #onShocked()}. */
    private int lastShockTick = -100000;

    /** Called by the electric fence. Feeds the comfort system, which makes fences a deterrent. */
    public void onShocked() {
        this.lastShockTick = this.tickCount;
        this.getNavigation().stop();
    }

    public boolean wasRecentlyShocked(int withinTicks) {
        return this.tickCount - this.lastShockTick < withinTicks;
    }

    /** Listed in the {@code Creatures.passiveSpecies} config: may only fight back. */
    public boolean isForcedPassive() {
        return ModCommonConfig.isForcedPassive(this.getSpecies().id());
    }

    @Override
    public void setTarget(@Nullable LivingEntity target) {
        if (target != null && !this.level().isClientSide() && this.isForcedPassive() && target != this.getLastHurtByMob()) {
            target = null;
        }
        super.setTarget(target);
    }

    /**
     * Whether another revived creature may come into being near {@code pos} under the
     * {@code Creatures.populationCap} config.
     */
    public static boolean hasRoomFor(ServerLevel level, net.minecraft.world.phys.Vec3 pos) {
        int cap = ModCommonConfig.CONFIG.populationCap.get();
        if (cap <= 0) {
            return true;
        }
        double r = ModCommonConfig.CONFIG.populationRadius.get();
        return level.getEntitiesOfClass(AncientCreatureEntity.class, new net.minecraft.world.phys.AABB(pos.subtract(r, r, r), pos.add(r, r, r))).size() < cap;
    }

    @Override
    public boolean canFallInLove() {
        return super.canFallInLove() && this.isFertile() && (!comfortEnabled() || this.getComfort() >= BREEDING_COMFORT)
            && (!(this.level() instanceof ServerLevel server) || hasRoomFor(server, this.position()));
    }

    @Override
    public Component getName() {
        return this.species.displayName();
    }
}
