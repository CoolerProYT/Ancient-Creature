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

public final class AncientCreatureApi {

    public static <C extends CreatureBehaviorConfig> CreatureBehaviorType<C> registerBehavior(
        Identifier id, MapCodec<C> codec, int defaultPriority) {
        return CreatureBehaviorRegistry.register(id, codec, defaultPriority);
    }

    public static Identifier registerBehaviorProfile(Identifier id, List<CreatureBehaviorComponent> components) {
        return BehaviorProfiles.register(id, components);
    }

    public static CreatureBehaviorComponent component(CreatureBehaviorConfig config) {
        return new CreatureBehaviorComponent(config);
    }

    public static CreatureBehaviorComponent component(CreatureBehaviorConfig config, int priority) {
        return new CreatureBehaviorComponent(Optional.of(priority), config);
    }

    public static Collection<CreatureBehaviorType<?>> behaviorTypes() {
        return CreatureBehaviorRegistry.types();
    }

    public static Collection<Identifier> behaviorProfiles() {
        return BehaviorProfiles.ids();
    }

    public static Optional<SpeciesDefinition> getSpecies(Identifier speciesId) {
        return new Species(speciesId).definition();
    }

    public static boolean isSpeciesLoaded(Identifier speciesId) {
        return getSpecies(speciesId).isPresent();
    }

    public static List<Identifier> loadedSpecies() {
        return Species.values().stream().map(Species::id).toList();
    }

    public static void onSpeciesReloaded(Runnable listener) {
        com.coolerpromc.ancientcreature.species.SpeciesSyncHandler.addReloadListener(listener);
    }

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

    public static Optional<Identifier> speciesOf(net.minecraft.world.entity.Entity entity) {
        return entity instanceof AncientCreatureEntity creature
            ? Optional.of(creature.getSpecies().id())
            : Optional.empty();
    }

    public static net.minecraft.world.entity.EntityType<AncientCreatureEntity> creatureType() {
        return ModEntities.ANCIENT_CREATURE.get();
    }

    public static int speciesFormatVersion() {
        return SpeciesDefinition.CURRENT_FORMAT_VERSION;
    }

    public static SpeciesManager serverSpecies() {
        return SpeciesManager.SERVER;
    }

    public static SpeciesManager clientSpecies() {
        return SpeciesManager.CLIENT;
    }

    private AncientCreatureApi() {
    }
}
