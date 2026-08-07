package com.coolerpromc.ancientcreature.api;

import com.coolerpromc.ancientcreature.entity.ModEntities;
import com.coolerpromc.ancientcreature.entity.Species;
import com.coolerpromc.ancientcreature.entity.behavior.BehaviorProfiles;
import com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorComponent;
import com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorConfig;
import com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorRegistry;
import com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorType;
import com.coolerpromc.ancientcreature.entity.custom.AncientCreatureEntity;
import com.coolerpromc.ancientcreature.species.SpeciesDefinition;
import com.coolerpromc.ancientcreature.species.SpeciesManager;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.LivingEntity;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Public entry point for other mods extending the Ancient Creature species system.
 *
 * <p>Everything reachable from here is intended to stay source-compatible across releases. Classes
 * outside {@code com.coolerpromc.ancientcreature.api} are internal and may change without notice.
 *
 * <h2>Adding a behavior component</h2>
 * <pre>{@code
 * public record DigBehavior(int duration) implements CreatureBehaviorConfig {
 *     public static final MapCodec<DigBehavior> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
 *         Codec.INT.optionalFieldOf("duration", 40).forGetter(DigBehavior::duration)
 *     ).apply(i, DigBehavior::new));
 *
 *     public static CreatureBehaviorType<DigBehavior> TYPE;
 *
 *     @Override public CreatureBehaviorType<?> type() { return TYPE; }
 *
 *     @Override public Goal createGoal(AncientCreatureEntity entity) {
 *         return new MyDigGoal(entity, this.duration);
 *     }
 * }
 *
 * // during mod initialization:
 * DigBehavior.TYPE = AncientCreatureApi.registerBehavior(
 *     Identifier.fromNamespaceAndPath("mymod", "dig"), DigBehavior.CODEC, 5);
 * }</pre>
 *
 * Species JSON can then use it:
 * <pre>{@code
 * { "type": "mymod:dig", "priority": 5, "duration": 60 }
 * }</pre>
 *
 * <h2>Timing</h2>
 * Registration must happen during mod initialization, before any datapack loads. The registries freeze
 * the first time species data is parsed; registering after that throws, because species files loaded
 * earlier would already have been rejected for referring to an unknown type.
 */
public final class AncientCreatureApi {

    // ------------------------------------------------------------------ behavior

    /**
     * Registers a behavior component type that species JSON can select by id.
     *
     * @param id             the id used in {@code "type"}, e.g. {@code mymod:dig}
     * @param codec          parses this component's own configuration fields
     * @param defaultPriority goal priority used when the JSON omits {@code "priority"}
     * @throws IllegalStateException if the id is taken or species data has already loaded
     */
    public static <C extends CreatureBehaviorConfig> CreatureBehaviorType<C> registerBehavior(
        Identifier id, MapCodec<C> codec, int defaultPriority) {
        return CreatureBehaviorRegistry.register(id, codec, defaultPriority);
    }

    /**
     * Registers a named bundle of components, selectable with {@code "behavior": {"profile": ...}}.
     *
     * <p>A species using the profile can still override any single component by listing it.
     *
     * @throws IllegalStateException if the id is taken or species data has already loaded
     */
    public static Identifier registerBehaviorProfile(Identifier id, List<CreatureBehaviorComponent> components) {
        return BehaviorProfiles.register(id, components);
    }

    /** Wraps a config in a component that uses its type's default priority. */
    public static CreatureBehaviorComponent component(CreatureBehaviorConfig config) {
        return new CreatureBehaviorComponent(config);
    }

    /** Wraps a config in a component at an explicit priority. */
    public static CreatureBehaviorComponent component(CreatureBehaviorConfig config, int priority) {
        return new CreatureBehaviorComponent(Optional.of(priority), config);
    }

    /** Every registered component type, built-in and added. */
    public static Collection<CreatureBehaviorType<?>> behaviorTypes() {
        return CreatureBehaviorRegistry.types();
    }

    /** Every registered profile id, built-in and added. */
    public static Collection<Identifier> behaviorProfiles() {
        return BehaviorProfiles.ids();
    }

    // ------------------------------------------------------------------ species

    /**
     * The definition currently loaded for {@code speciesId}, if any.
     *
     * <p>Reads server data where available and falls back to the client's synchronised copy, so this
     * works from either side.
     */
    public static Optional<SpeciesDefinition> getSpecies(Identifier speciesId) {
        return new Species(speciesId).definition();
    }

    /** Whether a definition is currently loaded for {@code speciesId}. */
    public static boolean isSpeciesLoaded(Identifier speciesId) {
        return getSpecies(speciesId).isPresent();
    }

    /** Every currently loaded species id, sorted. Empty before a world loads. */
    public static List<Identifier> loadedSpecies() {
        return Species.values().stream().map(Species::id).toList();
    }

    /**
     * Runs {@code listener} after every successful species reload, on the server thread.
     *
     * <p>Use this to invalidate anything derived from species data. The listener is called for the
     * initial load too.
     */
    public static void onSpeciesReloaded(Runnable listener) {
        com.coolerpromc.ancientcreature.species.SpeciesSyncHandler.addReloadListener(listener);
    }

    // ------------------------------------------------------------------ entities

    /**
     * Spawns a creature of {@code speciesId}.
     *
     * <p>The species is applied before the entity enters the world, so it is placed using its own
     * bounding box rather than the generic default.
     *
     * @param owner optional owner; an owned creature will never target them
     * @return the spawned creature, or {@code null} if it could not be placed
     */
    public static @Nullable AncientCreatureEntity spawn(
        ServerLevel level, Identifier speciesId, BlockPos pos, boolean baby, @Nullable LivingEntity owner) {
        Species species = new Species(speciesId);
        AncientCreatureEntity creature = ModEntities.ANCIENT_CREATURE.get().create(level, entity -> {
            entity.setSpecies(species);
            entity.setBaby(baby);
        }, pos, EntitySpawnReason.SPAWN_ITEM_USE, true, false);

        if (creature == null) {
            return null;
        }
        if (owner != null) {
            creature.setOwner(owner);
        }
        level.addFreshEntity(creature);
        return creature;
    }

    /** The species of any entity, or empty if it is not an Ancient Creature. */
    public static Optional<Identifier> speciesOf(net.minecraft.world.entity.Entity entity) {
        return entity instanceof AncientCreatureEntity creature
            ? Optional.of(creature.getSpecies().id())
            : Optional.empty();
    }

    /** The single generic creature entity type every species spawns as. */
    public static net.minecraft.world.entity.EntityType<AncientCreatureEntity> creatureType() {
        return ModEntities.ANCIENT_CREATURE.get();
    }

    // ------------------------------------------------------------------ misc

    /** Sanity check for addons that want to fail fast on a mismatched Ancient Creature version. */
    public static int speciesFormatVersion() {
        return SpeciesDefinition.CURRENT_FORMAT_VERSION;
    }

    /** Direct access to the server-side manager, for callers that need more than the helpers above. */
    public static SpeciesManager serverSpecies() {
        return SpeciesManager.SERVER;
    }

    /** Direct access to the client's synchronised copy. */
    public static SpeciesManager clientSpecies() {
        return SpeciesManager.CLIENT;
    }

    private AncientCreatureApi() {
    }
}
