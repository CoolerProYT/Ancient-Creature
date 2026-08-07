package com.coolerpromc.ancientcreature.entity.behavior.component;

import com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorConfig;
import com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorRegistry;
import com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorType;
import com.coolerpromc.ancientcreature.entity.custom.AncientCreatureEntity;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.AABB;
import org.jspecify.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;

/** Keeps members of the same species loosely together. */
public record HerdingBehavior(double speedModifier, double searchRange, double joinDistance, double stopDistance)
    implements CreatureBehaviorConfig {

    public static final MapCodec<HerdingBehavior> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
        BehaviorCodecs.SPEED.optionalFieldOf("speed_modifier", 1.0).forGetter(HerdingBehavior::speedModifier),
        BehaviorCodecs.DISTANCE.optionalFieldOf("search_range", 24.0).forGetter(HerdingBehavior::searchRange),
        BehaviorCodecs.DISTANCE.optionalFieldOf("join_distance", 12.0).forGetter(HerdingBehavior::joinDistance),
        BehaviorCodecs.DISTANCE.optionalFieldOf("stop_distance", 5.0).forGetter(HerdingBehavior::stopDistance)
    ).apply(i, HerdingBehavior::new));

    @Override
    public CreatureBehaviorType<?> type() {
        return CreatureBehaviorRegistry.HERDING;
    }

    @Override
    public Goal createGoal(AncientCreatureEntity entity) {
        return new HerdingGoal(entity, this);
    }

    private static final class HerdingGoal extends Goal {
        private final AncientCreatureEntity creature;
        private final HerdingBehavior config;
        private @Nullable AncientCreatureEntity leader;
        private int recalculateCooldown;

        private HerdingGoal(AncientCreatureEntity creature, HerdingBehavior config) {
            this.creature = creature;
            this.config = config;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (this.creature.getTarget() != null) {
                return false;
            }
            this.leader = this.findHerdMate();
            return this.leader != null;
        }

        @Override
        public boolean canContinueToUse() {
            return this.leader != null
                && this.leader.isAlive()
                && this.creature.getTarget() == null
                && this.creature.distanceToSqr(this.leader) > this.config.stopDistance() * this.config.stopDistance();
        }

        @Override
        public void start() {
            this.recalculateCooldown = 0;
        }

        @Override
        public void stop() {
            this.leader = null;
            this.creature.getNavigation().stop();
        }

        @Override
        public void tick() {
            if (this.leader == null || --this.recalculateCooldown > 0) {
                return;
            }
            this.recalculateCooldown = this.adjustedTickDelay(10);
            this.creature.getNavigation().moveTo(this.leader, this.config.speedModifier());
        }

        private @Nullable AncientCreatureEntity findHerdMate() {
            double joinSqr = this.config.joinDistance() * this.config.joinDistance();
            AABB box = this.creature.getBoundingBox().inflate(this.config.searchRange());
            List<AncientCreatureEntity> nearby = this.creature.level()
                .getEntitiesOfClass(AncientCreatureEntity.class, box,
                    other -> other != this.creature
                        && other.isAlive()
                        && other.getSpecies().equals(this.creature.getSpecies()));

            AncientCreatureEntity best = null;
            double bestDistance = Double.MAX_VALUE;
            for (AncientCreatureEntity other : nearby) {
                double distance = this.creature.distanceToSqr(other);
                if (distance > joinSqr && distance < bestDistance) {
                    best = other;
                    bestDistance = distance;
                }
            }
            return best;
        }
    }
}
