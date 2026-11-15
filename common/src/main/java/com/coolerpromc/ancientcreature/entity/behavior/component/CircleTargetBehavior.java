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
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

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

        private static final int MAX_ORBIT_ALTITUDE = 48;

        private static final int DIVE_WINDOW = 80;

        private final AncientCreatureEntity creature;
        private final CircleTargetBehavior config;
        private float angle;
        private int ticks;
        private int diveUntil;

        private CircleGoal(AncientCreatureEntity creature, CircleTargetBehavior config) {
            this.creature = creature;
            this.config = config;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = this.creature.getTarget();
            return target != null && target.isAlive() && !this.creature.isBaby()
                && this.creature.tickCount >= this.diveUntil;
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
            if (this.ticks >= this.config.diveAfter()) {

                this.diveUntil = this.creature.tickCount + DIVE_WINDOW;
            }
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

            double height = target.onGround() || target.isInWater() ? this.config.height() : 0.0;
            Vec3 orbit = target.position().add(
                Mth.cos(this.angle) * this.config.radius(),
                height,
                Mth.sin(this.angle) * this.config.radius());
            int ground = this.creature.level().getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mth.floor(orbit.x), Mth.floor(orbit.z));
            double ceiling = Math.min(ground + MAX_ORBIT_ALTITUDE, this.creature.level().getMaxY() - 2);
            if (orbit.y > ceiling) {
                orbit = new Vec3(orbit.x, ceiling, orbit.z);
            }

            this.creature.getLookControl().setLookAt(target, 30.0F, 30.0F);
            this.creature.getMoveControl().setWantedPosition(orbit.x, orbit.y, orbit.z, this.config.speedModifier());
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }
}
