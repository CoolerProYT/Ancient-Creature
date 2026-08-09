package com.coolerpromc.ancientcreature.entity.behavior.component;

import com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorConfig;
import com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorRegistry;
import com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorType;
import com.coolerpromc.ancientcreature.entity.custom.AncientCreatureAction;
import com.coolerpromc.ancientcreature.entity.custom.AncientCreatureEntity;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

/**
 * Lets tall herbivores seek out and browse leaf canopies without destroying the tree.
 *
 * <p>The goal searches infrequently, walks beneath a reachable leaf block, raises the species' browse
 * animation through the existing grazing action, and restores hunger only after the mouthful finishes.
 */
public record BrowseLeavesBehavior(
    int duration,
    int chance,
    int horizontalRange,
    int minHeight,
    int maxHeight,
    double speedModifier,
    double reachDistance,
    float hungerValue
) implements CreatureBehaviorConfig {
    public static final MapCodec<BrowseLeavesBehavior> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
        BehaviorCodecs.TICKS.optionalFieldOf("duration", 100).forGetter(BrowseLeavesBehavior::duration),
        BehaviorCodecs.CHANCE.optionalFieldOf("chance", 300).forGetter(BrowseLeavesBehavior::chance),
        BehaviorCodecs.BLOCK_RANGE.optionalFieldOf("horizontal_range", 7).forGetter(BrowseLeavesBehavior::horizontalRange),
        BehaviorCodecs.ALTITUDE.optionalFieldOf("min_height", 3).forGetter(BrowseLeavesBehavior::minHeight),
        BehaviorCodecs.ALTITUDE.optionalFieldOf("max_height", 8).forGetter(BrowseLeavesBehavior::maxHeight),
        BehaviorCodecs.SPEED.optionalFieldOf("speed_modifier", 0.7).forGetter(BrowseLeavesBehavior::speedModifier),
        BehaviorCodecs.DISTANCE.optionalFieldOf("reach_distance", 2.75).forGetter(BrowseLeavesBehavior::reachDistance),
        BehaviorCodecs.HUNGER.optionalFieldOf("hunger_value", 8.0F).forGetter(BrowseLeavesBehavior::hungerValue)
    ).apply(i, BrowseLeavesBehavior::new));

    @Override
    public CreatureBehaviorType<?> type() {
        return CreatureBehaviorRegistry.BROWSE_LEAVES;
    }

    @Override
    public Goal createGoal(AncientCreatureEntity entity) {
        return new BrowseGoal(entity, this);
    }

    private static final class BrowseGoal extends Goal {
        private final AncientCreatureEntity creature;
        private final BrowseLeavesBehavior config;
        private BlockPos leaves;
        private int eatingTicks;
        private int travelTicks;
        private boolean finished;

        private BrowseGoal(AncientCreatureEntity creature, BrowseLeavesBehavior config) {
            this.creature = creature;
            this.config = config;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (this.config.maxHeight() < this.config.minHeight()
                || this.creature.getTarget() != null
                || this.creature.isInWater()
                || !this.creature.onGround()
                || this.creature.getRandom().nextInt(this.config.chance()) != 0) {
                return false;
            }
            this.leaves = this.findNearestLeaves();
            return this.leaves != null;
        }

        @Override
        public boolean canContinueToUse() {
            return !this.finished
                && this.leaves != null
                && this.creature.getTarget() == null
                && this.creature.level().getBlockState(this.leaves).is(BlockTags.LEAVES)
                && this.travelTicks < 240;
        }

        @Override
        public void start() {
            this.eatingTicks = 0;
            this.travelTicks = 0;
            this.finished = false;
            this.moveTowardLeaves();
        }

        @Override
        public void tick() {
            if (this.leaves == null) {
                this.finished = true;
                return;
            }

            this.travelTicks++;
            this.creature.getLookControl().setLookAt(
                this.leaves.getX() + 0.5,
                this.leaves.getY() + 0.5,
                this.leaves.getZ() + 0.5,
                12.0F,
                this.creature.getMaxHeadXRot()
            );

            double dx = this.creature.getX() - (this.leaves.getX() + 0.5);
            double dz = this.creature.getZ() - (this.leaves.getZ() + 0.5);
            if (dx * dx + dz * dz > this.config.reachDistance() * this.config.reachDistance()) {
                if (this.travelTicks % 20 == 0 || this.creature.getNavigation().isDone()) {
                    this.moveTowardLeaves();
                }
                return;
            }

            this.creature.getNavigation().stop();
            this.creature.setAction(AncientCreatureAction.GRAZE);
            if (++this.eatingTicks >= this.config.duration()) {
                this.creature.feed(this.config.hungerValue());
                this.finished = true;
            }
        }

        @Override
        public void stop() {
            this.creature.getNavigation().stop();
            this.creature.clearAction(AncientCreatureAction.GRAZE);
            this.leaves = null;
            this.eatingTicks = 0;
            this.travelTicks = 0;
            this.finished = false;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        private void moveTowardLeaves() {
            this.creature.getNavigation().moveTo(
                this.leaves.getX() + 0.5,
                this.creature.getY(),
                this.leaves.getZ() + 0.5,
                this.config.speedModifier()
            );
        }

        private BlockPos findNearestLeaves() {
            BlockPos origin = this.creature.blockPosition();
            BlockPos best = null;
            double bestDistance = Double.MAX_VALUE;
            for (int y = this.config.minHeight(); y <= this.config.maxHeight(); y++) {
                for (int x = -this.config.horizontalRange(); x <= this.config.horizontalRange(); x++) {
                    for (int z = -this.config.horizontalRange(); z <= this.config.horizontalRange(); z++) {
                        BlockPos candidate = origin.offset(x, y, z);
                        if (!this.creature.level().getBlockState(candidate).is(BlockTags.LEAVES)) {
                            continue;
                        }
                        double distance = x * x + z * z;
                        if (distance < bestDistance) {
                            bestDistance = distance;
                            best = candidate.immutable();
                        }
                    }
                }
            }
            return best;
        }
    }
}
