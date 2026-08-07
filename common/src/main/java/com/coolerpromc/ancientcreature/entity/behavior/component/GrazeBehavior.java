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
 *
 * <p>Finishing a graze restores {@code hunger_value}. This is how a herbivore feeds itself, and it is why
 * a plant eater never needs the hunger gate that a predator does.
 */
public record GrazeBehavior(int duration, int chance, float hungerValue) implements CreatureBehaviorConfig {
    public static final MapCodec<GrazeBehavior> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
        BehaviorCodecs.TICKS.optionalFieldOf("duration", 80).forGetter(GrazeBehavior::duration),
        BehaviorCodecs.CHANCE.optionalFieldOf("chance", 400).forGetter(GrazeBehavior::chance),
        BehaviorCodecs.HUNGER.optionalFieldOf("hunger_value", 4.0F).forGetter(GrazeBehavior::hungerValue)
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
            if (--this.ticksLeft == 0) {
                // Fed at the end of the mouthful, so a graze cut short by a threat is not a free meal.
                this.creature.feed(this.config.hungerValue());
            }
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
