package com.coolerpromc.ancientcreature.entity.behavior.component;

import com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorConfig;
import com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorRegistry;
import com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorType;
import com.coolerpromc.ancientcreature.entity.custom.AncientCreatureEntity;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

/**
 * Circles above the current target before diving at it.
 *
 * <p>Gives an airborne predator something to do that a ground melee goal cannot express: it holds
 * altitude and orbits until {@code dive_after} ticks have passed, then releases so a melee component
 * can take over.
 */
public record CircleTargetBehavior(
    double speedModifier,
    double radius,
    double height,
    int diveAfter
) implements CreatureBehaviorConfig {

    public static final MapCodec<CircleTargetBehavior> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
        BehaviorCodecs.SPEED.optionalFieldOf("speed_modifier", 1.1).forGetter(CircleTargetBehavior::speedModifier),
        BehaviorCodecs.DISTANCE.optionalFieldOf("radius", 8.0).forGetter(CircleTargetBehavior::radius),
        BehaviorCodecs.DISTANCE.optionalFieldOf("height", 6.0).forGetter(CircleTargetBehavior::height),
        BehaviorCodecs.TICKS.optionalFieldOf("dive_after", 100).forGetter(CircleTargetBehavior::diveAfter)
    ).apply(i, CircleTargetBehavior::new));

    @Override
    public CreatureBehaviorType<?> type() {
        return CreatureBehaviorRegistry.CIRCLE_TARGET;
    }

    @Override
    public Goal createGoal(AncientCreatureEntity entity) {
        if (!entity.speciesCategory().isFlying()) {
            return null;
        }
        return new CircleGoal(entity, this);
    }

    private static final class CircleGoal extends Goal {
        private final AncientCreatureEntity creature;
        private final CircleTargetBehavior config;
        private float angle;
        private int ticks;

        private CircleGoal(AncientCreatureEntity creature, CircleTargetBehavior config) {
            this.creature = creature;
            this.config = config;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = this.creature.getTarget();
            return target != null && target.isAlive() && !this.creature.isBaby();
        }

        @Override
        public boolean canContinueToUse() {
            return this.ticks < this.config.diveAfter() && this.canUse();
        }

        @Override
        public void start() {
            this.ticks = 0;
            this.angle = this.creature.getRandom().nextFloat() * Mth.TWO_PI;
        }

        @Override
        public void stop() {
            this.ticks = 0;
            this.creature.getNavigation().stop();
        }

        @Override
        public void tick() {
            LivingEntity target = this.creature.getTarget();
            if (target == null) {
                return;
            }

            this.ticks++;
            this.angle += 0.05F;
            if (this.angle > Mth.TWO_PI) {
                this.angle -= Mth.TWO_PI;
            }

            Vec3 orbit = target.position().add(
                Mth.cos(this.angle) * this.config.radius(),
                this.config.height(),
                Mth.sin(this.angle) * this.config.radius());

            this.creature.getLookControl().setLookAt(target, 30.0F, 30.0F);
            this.creature.getMoveControl().setWantedPosition(orbit.x, orbit.y, orbit.z, this.config.speedModifier());
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }
}
