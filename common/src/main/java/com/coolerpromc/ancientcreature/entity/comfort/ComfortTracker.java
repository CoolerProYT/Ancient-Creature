package com.coolerpromc.ancientcreature.entity.comfort;

import com.coolerpromc.ancientcreature.entity.custom.AncientCreatureEntity;
import com.coolerpromc.ancientcreature.species.SpeciesCareProperties;
import com.coolerpromc.ancientcreature.species.SpeciesHungerProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.AABB;

import java.util.ArrayDeque;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Works out how comfortable a creature is, from 0 to 100.
 *
 * <p>Every few seconds the tracker scores the creature's situation — hunger, room to move, company,
 * climate, recent pain and recent care — into a target, and comfort drifts towards it, so a single bad
 * moment does not swing it and a lasting problem always shows. The single biggest problem is reported as
 * the {@link Factor} players see.
 *
 * <p>Space is the only expensive part: a bounded flood fill over the floor the creature can stand on (or
 * the water it can swim in), refreshed every half minute and stopping as soon as it has found enough.
 */
public final class ComfortTracker {
    public static final float START = 70.0F;
    public static final int UPDATE_INTERVAL = 100;
    private static final int SPACE_INTERVAL = 600;
    private static final float DRIFT = 3.0F;

    /** The reasons comfort can fall, in the order they are synchronised. {@code NONE} means content. */
    public enum Factor {
        NONE, HUNGER, SPACE, LONELY, CROWDED, CLIMATE, PAIN, STRANDED;

        public String key() {
            return "comfort.ancientcreature.reason." + this.name().toLowerCase(java.util.Locale.ROOT);
        }
    }

    private final AncientCreatureEntity creature;
    private int nextSpaceCheck;
    private float spaceRatio = 1.0F;
    private int lastFedTick = -100000;

    public ComfortTracker(AncientCreatureEntity creature) {
        this.creature = creature;
    }

    public void onHandFed() {
        this.lastFedTick = this.creature.tickCount;
    }

    /** Recomputes and returns the target comfort and the main problem. Server side only. */
    public Result evaluate(ServerLevel level) {
        Map<Factor, Float> penalties = new EnumMap<>(Factor.class);
        float bonus = 0.0F;
        AncientCreatureEntity c = this.creature;
        SpeciesCareProperties care = c.speciesDefinition().care();

        // hunger
        SpeciesHungerProperties hunger = c.hungerProperties();
        if (c.getHunger() <= 0.0F) {
            penalties.put(Factor.HUNGER, 30.0F);
        } else if (hunger.isHungryAt(c.getHunger())) {
            penalties.put(Factor.HUNGER, 15.0F);
        } else if (hunger.isSatedAt(c.getHunger())) {
            bonus += 5.0F;
        }

        // stranded aquatic animal
        if (c.speciesCategory().isAquatic() && !c.isInWater()) {
            penalties.put(Factor.STRANDED, 40.0F);
        }

        // space
        if (c.tickCount >= this.nextSpaceCheck) {
            this.nextSpaceCheck = c.tickCount + SPACE_INTERVAL;
            this.spaceRatio = this.measureSpace(level, care.spaceFor(c.getBbWidth()));
        }
        if (this.spaceRatio < 1.0F) {
            penalties.put(Factor.SPACE, 40.0F * (1.0F - this.spaceRatio));
        }

        // company
        int kin = level.getEntitiesOfClass(AncientCreatureEntity.class, new AABB(c.blockPosition()).inflate(24.0, 8.0, 24.0),
            other -> other != c && other.isAlive() && other.getSpecies().equals(c.getSpecies())).size();
        int group = kin + 1;
        switch (care.social()) {
            case HERD -> {
                if (group < care.groupMin()) {
                    penalties.put(Factor.LONELY, 15.0F * (care.groupMin() - group) / Math.max(1, care.groupMin() - 1));
                } else if (group > care.groupMax()) {
                    penalties.put(Factor.CROWDED, Math.min(20.0F, 4.0F * (group - care.groupMax())));
                } else {
                    bonus += 10.0F;
                }
            }
            case PAIR -> {
                if (kin == 1) {
                    bonus += 8.0F;
                } else if (kin == 0) {
                    penalties.put(Factor.LONELY, 5.0F);
                } else {
                    penalties.put(Factor.CROWDED, Math.min(20.0F, 6.0F * (kin - 1)));
                }
            }
            case SOLITARY -> {
                if (kin > 0 && !c.isBaby()) {
                    penalties.put(Factor.CROWDED, Math.min(20.0F, 8.0F * kin));
                }
            }
        }

        // climate
        float temperature = level.getBiome(c.blockPosition()).value().getBaseTemperature();
        float climate = switch (care.climate()) {
            case ANY -> 0.0F;
            case COLD -> temperature > 1.0F ? 25.0F : temperature > 0.5F ? 10.0F : -5.0F;
            case WARM -> temperature < 0.3F ? 25.0F : temperature < 0.7F ? 10.0F : -5.0F;
            case TEMPERATE -> temperature < 0.0F || temperature > 1.5F ? 15.0F : 0.0F;
        };
        if (climate > 0.0F) {
            penalties.put(Factor.CLIMATE, climate);
        } else {
            bonus -= climate;
        }

        // pain: fresh fence shocks and wounds
        float pain = 0.0F;
        if (c.wasRecentlyShocked(2400)) {
            pain += 25.0F;
        }
        if (c.getLastHurtByMob() != null && c.tickCount - c.getLastHurtByMobTimestamp() < 600) {
            pain += 10.0F;
        }
        if (pain > 0.0F) {
            penalties.put(Factor.PAIN, pain);
        }

        // care
        if (c.tickCount - this.lastFedTick < 6000) {
            bonus += 10.0F;
        }
        if (c.isBaby()) {
            bonus += 10.0F;
        }

        float total = 0.0F;
        Factor worst = Factor.NONE;
        float worstValue = 4.0F;
        for (Map.Entry<Factor, Float> entry : penalties.entrySet()) {
            total += entry.getValue();
            if (entry.getValue() > worstValue) {
                worst = entry.getKey();
                worstValue = entry.getValue();
            }
        }
        float target = Math.max(0.0F, Math.min(100.0F, START + bonus - total));
        return new Result(target, worst);
    }

    public record Result(float target, Factor reason) {
    }

    /** Moves {@code current} one step towards {@code target}. */
    public static float drift(float current, float target) {
        if (Math.abs(target - current) <= DRIFT) {
            return target;
        }
        return current + Math.signum(target - current) * DRIFT;
    }

    /** Fraction of the wanted space the creature actually has, 0..1. Flyers always have enough. */
    float measureSpace(ServerLevel level, int wanted) {
        AncientCreatureEntity c = this.creature;
        if (c.speciesCategory().isFlying()) {
            return 1.0F;
        }
        boolean aquatic = c.speciesCategory().isAquatic();
        int radius = Math.max(12, (int) Math.ceil(Math.sqrt(wanted) * 1.6));
        BlockPos origin = c.blockPosition();
        Set<BlockPos> seen = new HashSet<>();
        ArrayDeque<BlockPos> queue = new ArrayDeque<>();
        BlockPos start = aquatic ? origin : this.groundAt(level, origin);
        if (start == null) {
            return 1.0F;
        }
        queue.add(start);
        seen.add(start);
        int found = 0;
        while (!queue.isEmpty() && found < wanted) {
            BlockPos pos = queue.poll();
            found++;
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if ((dx == 0) == (dz == 0)) {
                        continue;
                    }
                    if (aquatic) {
                        for (int dy = -1; dy <= 1; dy++) {
                            this.visit(level, pos.offset(dx, dy, dz), origin, radius, seen, queue, true);
                        }
                        this.visit(level, pos.above(), origin, radius, seen, queue, true);
                        this.visit(level, pos.below(), origin, radius, seen, queue, true);
                    } else {
                        BlockPos next = this.groundNear(level, pos.offset(dx, 0, dz));
                        if (next != null) {
                            this.visit(level, next, origin, radius, seen, queue, false);
                        }
                    }
                }
            }
        }
        return Math.min(1.0F, found / (float) wanted);
    }

    private void visit(ServerLevel level, BlockPos pos, BlockPos origin, int radius, Set<BlockPos> seen, ArrayDeque<BlockPos> queue, boolean aquatic) {
        if (Math.abs(pos.getX() - origin.getX()) > radius || Math.abs(pos.getZ() - origin.getZ()) > radius || Math.abs(pos.getY() - origin.getY()) > 12) {
            return;
        }
        if (!level.isLoaded(pos) || !seen.add(pos)) {
            return;
        }
        if (aquatic && !level.getFluidState(pos).is(FluidTags.WATER)) {
            return;
        }
        queue.add(pos);
    }

    /** The standable spot at {@code pos}, stepping up or down one block like a walking mob. */
    private BlockPos groundNear(ServerLevel level, BlockPos pos) {
        for (int dy : new int[]{0, 1, -1}) {
            BlockPos p = pos.offset(0, dy, 0);
            if (this.standable(level, p)) {
                return p;
            }
        }
        return null;
    }

    private BlockPos groundAt(ServerLevel level, BlockPos pos) {
        for (int dy = 0; dy >= -3; dy--) {
            BlockPos p = pos.offset(0, dy, 0);
            if (this.standable(level, p)) {
                return p;
            }
        }
        return null;
    }

    private boolean standable(ServerLevel level, BlockPos feet) {
        BlockState at = level.getBlockState(feet);
        BlockState head = level.getBlockState(feet.above());
        BlockState below = level.getBlockState(feet.below());
        return at.isPathfindable(PathComputationType.LAND) && head.isPathfindable(PathComputationType.LAND)
            && !below.isPathfindable(PathComputationType.LAND) && below.isFaceSturdy(level, feet.below(), net.minecraft.core.Direction.UP);
    }
}
