package com.coolerpromc.ancientcreature.entity.behavior.component;

import com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorConfig;
import com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorRegistry;
import com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorType;
import com.coolerpromc.ancientcreature.entity.custom.AncientCreatureEntity;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.EnumSet;

/**
 * Free flight for a {@code flying} species, held inside an altitude band above the terrain.
 *
 * <p>Only applies to flying species — the goal needs the {@code FlyingPathNavigation} that
 * {@link AncientCreatureEntity} installs for that category, so it is skipped for anything else rather
 * than pathing into the ground.
 *
 * <p>This replaces vanilla's {@code WaterAvoidingRandomFlyingGoal}, which hard-codes its wander box to
 * 8 blocks horizontally and 7 vertically biased 3 up and 1 down. That box is why an untuned flyer looks
 * pinned to one height: it can never pick a waypoint far enough above where it already is to climb. The
 * band here is measured from the ground column under each candidate rather than from the creature, so a
 * species keeps its altitude over hills instead of drifting into them.
 */
public record FlyBehavior(
    double speedModifier,
    int wanderRange,
    int verticalRange,
    int minAltitude,
    int maxAltitude,
    int interval
) implements CreatureBehaviorConfig {

    // Kept as its own field: chaining validate() straight onto mapCodec() leaves the record type
    // unconstrained and inference fails on the whole group.
    private static final MapCodec<FlyBehavior> FIELDS = RecordCodecBuilder.mapCodec(i -> i.group(
        BehaviorCodecs.SPEED.optionalFieldOf("speed_modifier", 1.0).forGetter(FlyBehavior::speedModifier),
        BehaviorCodecs.BLOCK_RANGE.optionalFieldOf("wander_range", 16).forGetter(FlyBehavior::wanderRange),
        BehaviorCodecs.BLOCK_RANGE.optionalFieldOf("vertical_range", 12).forGetter(FlyBehavior::verticalRange),
        BehaviorCodecs.ALTITUDE.optionalFieldOf("min_altitude", 5).forGetter(FlyBehavior::minAltitude),
        BehaviorCodecs.ALTITUDE.optionalFieldOf("max_altitude", 32).forGetter(FlyBehavior::maxAltitude),
        BehaviorCodecs.CHANCE.optionalFieldOf("interval", 10).forGetter(FlyBehavior::interval)
    ).apply(i, FlyBehavior::new));

    public static final MapCodec<FlyBehavior> CODEC = FIELDS.validate(FlyBehavior::validate);

    /** An inverted band would leave nowhere legal to fly, so say so at load rather than at spawn. */
    private static DataResult<FlyBehavior> validate(FlyBehavior fly) {
        if (fly.maxAltitude < fly.minAltitude) {
            return DataResult.error(() -> "max_altitude (" + fly.maxAltitude
                + ") is below min_altitude (" + fly.minAltitude + "); the flight band would be empty");
        }
        return DataResult.success(fly);
    }

    /**
     * The profile default: wander at the given speed inside the standard band.
     *
     * <p>A static factory rather than an overloaded constructor, because a second constructor would make
     * {@code FlyBehavior::new} ambiguous in the codec above.
     */
    public static FlyBehavior wandering(double speedModifier) {
        return new FlyBehavior(speedModifier, 16, 12, 5, 32, 10);
    }

    @Override
    public CreatureBehaviorType<?> type() {
        return CreatureBehaviorRegistry.FLY;
    }

    @Override
    public Goal createGoal(AncientCreatureEntity entity) {
        if (!entity.speciesCategory().isFlying()) {
            return null;
        }
        return new FlyWanderGoal(entity, this);
    }

    private static final class FlyWanderGoal extends Goal {
        private static final int ATTEMPTS = 12;

        private final AncientCreatureEntity creature;
        private final FlyBehavior config;

        private FlyWanderGoal(AncientCreatureEntity creature, FlyBehavior config) {
            this.creature = creature;
            this.config = config;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (this.creature.getTarget() != null || !this.creature.getNavigation().isDone()) {
                return false;
            }
            return this.creature.getRandom().nextInt(this.config.interval()) == 0;
        }

        @Override
        public boolean canContinueToUse() {
            return !this.creature.getNavigation().isDone() && this.creature.getTarget() == null;
        }

        @Override
        public void start() {
            Vec3 destination = this.findDestination();
            if (destination != null) {
                this.creature.getNavigation().moveTo(destination.x, destination.y, destination.z, this.config.speedModifier());
            }
        }

        @Override
        public void stop() {
            this.creature.getNavigation().stop();
        }

        private @Nullable Vec3 findDestination() {
            Level level = this.creature.level();
            RandomSource random = this.creature.getRandom();
            BlockPos origin = this.creature.blockPosition();

            int horizontal = this.config.wanderRange();
            int vertical = this.config.verticalRange();

            for (int attempt = 0; attempt < ATTEMPTS; attempt++) {
                int x = origin.getX() + random.nextInt(2 * horizontal + 1) - horizontal;
                int z = origin.getZ() + random.nextInt(2 * horizontal + 1) - horizontal;
                int y = origin.getY() + random.nextInt(2 * vertical + 1) - vertical;

                // Never force-load a chunk just to pick a waypoint.
                if (!level.hasChunkAt(new BlockPos(x, y, z))) {
                    continue;
                }

                int ground = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
                int lowest = ground + this.config.minAltitude();
                int highest = Math.max(lowest, ground + this.config.maxAltitude());
                y = Mth.clamp(y, lowest, Math.min(highest, level.getMaxY()));

                BlockPos candidate = new BlockPos(x, y, z);
                if (level.isEmptyBlock(candidate) && level.isEmptyBlock(candidate.above())) {
                    return Vec3.atCenterOf(candidate);
                }
            }
            return null;
        }
    }
}
