package com.coolerpromc.ancientcreature.entity.behavior;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.entity.behavior.component.*;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.resources.Identifier;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Named bundles of behavior components, so the common cases are one line of JSON.
 *
 * <p>A profile is only a starting list: components a species declares explicitly replace the profile's
 * entry of the same type, so any single piece stays overridable without re-listing the rest.
 */
public final class BehaviorProfiles {
    private static final Map<Identifier, List<CreatureBehaviorComponent>> PROFILES = new LinkedHashMap<>();

    /** See {@link CreatureBehaviorRegistry} — profiles freeze on first use for the same reason. */
    private static volatile boolean frozen;

    /** Nothing but idle wandering and looking around. */
    public static final Identifier PASSIVE = register("passive", List.of(
        component(FloatBehavior.INSTANCE),
        component(new PanicBehavior(1.25)),
        component(new BreedBehavior(1.0)),
        component(new TemptBehavior(1.15, false)),
        component(new FollowParentBehavior(1.1)),
        component(new WanderBehavior(0.9, true, 120)),
        component(new LookAtPlayerBehavior(8.0F, 0.02F)),
        component(RandomLookBehavior.INSTANCE)
    ));

    /**
     * Peaceful grazer that charges anything which threatens it.
     *
     * <p>Nothing here is gated on hunger: a herbivore that fights back, guards its space and drives off
     * zombies is defending itself, not feeding. Grazing is what keeps it fed.
     */
    public static final Identifier DEFENSIVE_HERBIVORE = register("defensive_herbivore", List.of(
        component(FloatBehavior.INSTANCE),
        component(new ChargeAttackBehavior(1.45, true, true, 0.6)),
        component(new BreedBehavior(1.0)),
        component(new TemptBehavior(1.15, false)),
        component(new FollowParentBehavior(1.1)),
        component(new WanderBehavior(0.9, true, 120)),
        component(new LookAtPlayerBehavior(8.0F, 0.02F)),
        component(RandomLookBehavior.INSTANCE),
        component(new DefensiveRetaliationBehavior(false)),
        component(new TerritorialBehavior(12.0, 5, true, false, false)),
        component(new HuntHostilesBehavior(5, false, false))
    ));

    /**
     * Apex land predator: roars before it charges, hunts players, animals and hostiles, and only does
     * any of it once grown.
     *
     * <p>The two <em>predatory</em> components are hunger-gated. Hunting animals is feeding, and so is
     * stalking players -- this profile sets {@code require_weapon: false}, so without the gate the creature
     * charges anyone who walks within 32 blocks forever, which is what made it feel like it attacked at
     * random. Fed, it now ignores them.
     *
     * <p>Retaliation stays ungated, and so does {@code hunt_hostiles}: driving off a zombie is defence,
     * and a predator that only did it while starving would ignore threats it should answer.
     */
    public static final Identifier APEX_PREDATOR = register("apex_predator", List.of(
        component(FloatBehavior.INSTANCE),
        component(new RoarAttackBehavior(1.35, true, 48, 240, true, 1.0, true)),
        component(new BreedBehavior(1.0)),
        component(new TemptBehavior(1.05, false)),
        component(new FollowParentBehavior(1.1)),
        component(new WanderBehavior(0.8, true, 120)),
        component(new LookAtPlayerBehavior(12.0F, 0.02F)),
        component(RandomLookBehavior.INSTANCE),
        component(new DefensiveRetaliationBehavior(false)),
        component(new TerritorialBehavior(32.0, 10, false, true, true)),
        component(new HuntAnimalsBehavior(10, true, true)),
        component(new HuntHostilesBehavior(10, true, false))
    ));

    /**
     * Open-water hunter.
     *
     * <p>No breeding or tempting, a swim wander, and prey selection restricted to things actually in the
     * water -- and, now, to when it is hungry. A fed Megalodon lets swimmers past.
     */
    public static final Identifier AQUATIC_PREDATOR = register("aquatic_predator", List.of(
        component(FindWaterBehavior.INSTANCE),
        component(new MeleeAttackBehavior(1.35, true, true)),
        component(new SwimmingBehavior(1.0, 30)),
        component(RandomLookBehavior.INSTANCE),
        component(new DefensiveRetaliationBehavior(false)),
        component(new AquaticPredatorBehavior(10, true, true, true, true, true))
    ));

    /**
     * Airborne browser: flies, is tempted down by food, breeds, and does not fight back.
     *
     * <p>Requires {@code "entity_category": "flying"} — the flight components skip themselves on any
     * other category rather than pathing into the ground.
     */
    public static final Identifier FLYING_PASSIVE = register("flying_passive", List.of(
        component(new PanicBehavior(1.4)),
        component(new BreedBehavior(1.0)),
        component(new TemptBehavior(1.1, false)),
        component(new FollowParentBehavior(1.1)),
        component(FlyBehavior.wandering(1.0)),
        component(new LookAtPlayerBehavior(10.0F, 0.02F)),
        component(RandomLookBehavior.INSTANCE)
    ));

    /**
     * Airborne hunter: circles its target, then dives.
     *
     * <p>Requires {@code "entity_category": "flying"}. Prey selection is hunger-gated, so a fed flyer
     * soars past animals and players instead of stooping on everything it passes.
     */
    public static final Identifier FLYING_PREDATOR = register("flying_predator", List.of(
        component(new CircleTargetBehavior(1.2, 8.0, 6.0, 100)),
        component(new MeleeAttackBehavior(1.3, true, true)),
        component(FlyBehavior.wandering(1.0)),
        component(new LookAtPlayerBehavior(16.0F, 0.02F)),
        component(RandomLookBehavior.INSTANCE),
        component(new DefensiveRetaliationBehavior(false)),
        component(new TerritorialBehavior(24.0, 10, false, true, true)),
        component(new HuntAnimalsBehavior(10, true, true))
    ));

    public static final Codec<Identifier> PROFILE_ID_CODEC = Identifier.CODEC.validate(
        id -> {
            frozen = true;
            return PROFILES.containsKey(id)
                ? DataResult.success(id)
                : DataResult.error(() -> "unknown behavior profile '" + id + "'; known profiles: " + knownProfileNames());
        });

    /**
     * Adds a named component bundle that species JSON can then select with {@code behavior.profile}.
     *
     * <p>Call during mod initialization. See
     * {@link com.coolerpromc.ancientcreature.api.AncientCreatureApi#registerBehaviorProfile}.
     *
     * @throws IllegalStateException if the id is taken, or if species data has already been parsed
     */
    public static Identifier register(Identifier id, List<CreatureBehaviorComponent> components) {
        if (frozen) {
            throw new IllegalStateException("Cannot register behavior profile " + id
                + " after species data has loaded. Register it during mod initialization instead.");
        }
        if (PROFILES.putIfAbsent(id, List.copyOf(components)) != null) {
            throw new IllegalStateException("Duplicate behavior profile " + id);
        }
        return id;
    }

    private static Identifier register(String name, List<CreatureBehaviorComponent> components) {
        return register(Constants.id(name), components);
    }

    private static CreatureBehaviorComponent component(CreatureBehaviorConfig config) {
        return new CreatureBehaviorComponent(config);
    }

    public static Optional<List<CreatureBehaviorComponent>> get(Identifier id) {
        return Optional.ofNullable(PROFILES.get(id));
    }

    public static Collection<Identifier> ids() {
        return java.util.Collections.unmodifiableCollection(PROFILES.keySet());
    }

    private static String knownProfileNames() {
        return String.join(", ", PROFILES.keySet().stream().map(Identifier::toString).sorted().toList());
    }

    private BehaviorProfiles() {
    }
}
