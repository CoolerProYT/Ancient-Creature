package com.coolerpromc.ancientcreature.entity.behavior;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.entity.behavior.component.*;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import net.minecraft.resources.Identifier;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * The closed set of behavior components a species definition may use.
 *
 * <p>This is deliberately <em>not</em> a Minecraft registry: it never needs to be synced or frozen, it
 * must be readable while parsing datapack JSON, and keeping it local makes it obvious that datapacks can
 * only select from these entries. Nothing in JSON can reach Java code that is not listed here.
 */
public final class CreatureBehaviorRegistry {
    private static final Map<Identifier, CreatureBehaviorType<?>> TYPES = new LinkedHashMap<>();

    /**
     * Set the first time a species definition is parsed. Registering after that would mean species
     * files loaded earlier had already been rejected for using an "unknown" type, so it is refused
     * with an explanation rather than half-working.
     */
    private static volatile boolean frozen;

    public static final CreatureBehaviorType<FloatBehavior> FLOAT =
        register("float", FloatBehavior.CODEC, 0);
    public static final CreatureBehaviorType<PanicBehavior> PANIC =
        register("panic", PanicBehavior.CODEC, 1);
    public static final CreatureBehaviorType<ChargeAttackBehavior> CHARGE_ATTACK =
        register("charge_attack", ChargeAttackBehavior.CODEC, 1);
    public static final CreatureBehaviorType<RoarAttackBehavior> ROAR_ATTACK =
        register("roar_attack", RoarAttackBehavior.CODEC, 1);
    public static final CreatureBehaviorType<MeleeAttackBehavior> MELEE_ATTACK =
        register("melee_attack", MeleeAttackBehavior.CODEC, 2);
    public static final CreatureBehaviorType<FindWaterBehavior> FIND_WATER =
        register("find_water", FindWaterBehavior.CODEC, 0);
    public static final CreatureBehaviorType<BreedBehavior> BREED =
        register("breed", BreedBehavior.CODEC, 3);
    public static final CreatureBehaviorType<TemptBehavior> TEMPT =
        register("tempt", TemptBehavior.CODEC, 4);
    public static final CreatureBehaviorType<FollowParentBehavior> FOLLOW_PARENT =
        register("follow_parent", FollowParentBehavior.CODEC, 5);
    public static final CreatureBehaviorType<GrazeBehavior> GRAZE =
        register("graze", GrazeBehavior.CODEC, 5);
    public static final CreatureBehaviorType<BrowseLeavesBehavior> BROWSE_LEAVES =
        register("browse_leaves", BrowseLeavesBehavior.CODEC, 5);
    public static final CreatureBehaviorType<HerdingBehavior> HERDING =
        register("herding", HerdingBehavior.CODEC, 6);
    public static final CreatureBehaviorType<SwimmingBehavior> SWIMMING =
        register("swimming", SwimmingBehavior.CODEC, 6);
    public static final CreatureBehaviorType<WanderBehavior> WANDER =
        register("wander", WanderBehavior.CODEC, 6);
    public static final CreatureBehaviorType<FlyBehavior> FLY =
        register("fly", FlyBehavior.CODEC, 6);
    public static final CreatureBehaviorType<CircleTargetBehavior> CIRCLE_TARGET =
        register("circle_target", CircleTargetBehavior.CODEC, 1);
    public static final CreatureBehaviorType<LookAtPlayerBehavior> LOOK_AT_PLAYER =
        register("look_at_player", LookAtPlayerBehavior.CODEC, 7);
    public static final CreatureBehaviorType<RandomLookBehavior> RANDOM_LOOK =
        register("random_look", RandomLookBehavior.CODEC, 8);

    public static final CreatureBehaviorType<DefensiveRetaliationBehavior> DEFENSIVE_RETALIATION =
        register("defensive_retaliation", DefensiveRetaliationBehavior.CODEC, 1);
    public static final CreatureBehaviorType<TerritorialBehavior> TERRITORIAL =
        register("territorial", TerritorialBehavior.CODEC, 2);
    public static final CreatureBehaviorType<HuntHostilesBehavior> HUNT_HOSTILES =
        register("hunt_hostiles", HuntHostilesBehavior.CODEC, 4);
    public static final CreatureBehaviorType<HuntAnimalsBehavior> HUNT_ANIMALS =
        register("hunt_animals", HuntAnimalsBehavior.CODEC, 3);
    public static final CreatureBehaviorType<AquaticPredatorBehavior> AQUATIC_PREDATOR =
        register("aquatic_predator", AquaticPredatorBehavior.CODEC, 2);

    /** Resolves the {@code "type"} field, reporting every valid alternative when it does not match. */
    public static final Codec<CreatureBehaviorType<?>> TYPE_CODEC = Identifier.CODEC.comapFlatMap(
        id -> {
            frozen = true;
            CreatureBehaviorType<?> type = TYPES.get(id);
            return type != null
                ? DataResult.success(type)
                : DataResult.error(() -> "unknown behavior component type '" + id + "'; known types: " + knownTypeNames());
        },
        CreatureBehaviorType::id
    );

    /**
     * Adds a behavior component type that species JSON can then use.
     *
     * <p>Call this during mod initialization, before any datapack loads. See
     * {@link com.coolerpromc.ancientcreature.api.AncientCreatureApi#registerBehavior}.
     *
     * @throws IllegalStateException if the id is taken, or if species data has already been parsed
     */
    public static <C extends CreatureBehaviorConfig> CreatureBehaviorType<C> register(
        Identifier id, MapCodec<C> codec, int defaultPriority) {
        if (frozen) {
            throw new IllegalStateException("Cannot register behavior type " + id
                + " after species data has loaded. Register it during mod initialization instead.");
        }
        CreatureBehaviorType<C> type = new CreatureBehaviorType<>(id, codec, defaultPriority);
        if (TYPES.putIfAbsent(id, type) != null) {
            throw new IllegalStateException("Duplicate creature behavior type " + id);
        }
        return type;
    }

    private static <C extends CreatureBehaviorConfig> CreatureBehaviorType<C> register(
        String name, MapCodec<C> codec, int defaultPriority) {
        return register(Constants.id(name), codec, defaultPriority);
    }

    public static Optional<CreatureBehaviorType<?>> get(Identifier id) {
        return Optional.ofNullable(TYPES.get(id));
    }

    public static Collection<CreatureBehaviorType<?>> types() {
        return java.util.Collections.unmodifiableCollection(TYPES.values());
    }

    private static String knownTypeNames() {
        return String.join(", ", TYPES.keySet().stream().map(Identifier::toString).sorted().toList());
    }

    private CreatureBehaviorRegistry() {
    }
}
