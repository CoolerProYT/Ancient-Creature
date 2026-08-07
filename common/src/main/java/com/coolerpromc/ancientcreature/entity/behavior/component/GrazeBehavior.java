package com.coolerpromc.ancientcreature.entity.behavior.component;

import com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorConfig;
import com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorRegistry;
import com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorType;
import com.coolerpromc.ancientcreature.entity.custom.AncientCreatureAction;
import com.coolerpromc.ancientcreature.entity.custom.AncientCreatureEntity;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

/**
 * Periodically stops to graze.
 *
 * <p>The goal only sets the synchronised {@link AncientCreatureAction}; the client's animation
 * controller decides what to draw. Nothing here touches rendering, and the client cannot start or stop
 * it on its own.
 */
public record GrazeBehavior(int duration, int chance) implements CreatureBehaviorConfig {
    public static final MapCodec<GrazeBehavior> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
        BehaviorCodecs.TICKS.optionalFieldOf("duration", 80).forGetter(GrazeBehavior::duration),
        BehaviorCodecs.CHANCE.optionalFieldOf("chance", 400).forGetter(GrazeBehavior::chance)
    ).apply(i, GrazeBehavior::new));

    @Override
    public CreatureBehaviorType<?> type() {
        return CreatureBehaviorRegistry.GRAZE;
    }

    @Override
    public Goal createGoal(AncientCreatureEntity entity) {
        return new GrazeGoal(entity, this);
    }

    private static final class GrazeGoal extends Goal {
        private final AncientCreatureEntity creature;
        private final GrazeBehavior config;
        private int ticksLeft;

        private GrazeGoal(AncientCreatureEntity creature, GrazeBehavior config) {
            this.creature = creature;
            this.config = config;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (this.creature.isBaby() || this.creature.getTarget() != null || this.creature.isInWater()) {
                return false;
            }
            return this.creature.getRandom().nextInt(this.config.chance()) == 0
                && this.creature.onGround()
                && this.creature.level().getBlockState(this.creature.blockPosition().below()).isSolid();
        }

        @Override
        public boolean canContinueToUse() {
            return this.ticksLeft > 0 && this.creature.getTarget() == null;
        }

        @Override
        public void start() {
            this.ticksLeft = this.config.duration();
            this.creature.getNavigation().stop();
            this.creature.setAction(AncientCreatureAction.GRAZE);
        }

        @Override
        public void tick() {
            this.ticksLeft--;
        }

        @Override
        public void stop() {
            this.ticksLeft = 0;
            this.creature.clearAction(AncientCreatureAction.GRAZE);
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }
}
