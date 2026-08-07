package com.coolerpromc.ancientcreature.entity.behavior.component;

import com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorConfig;
import com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorRegistry;
import com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorType;
import com.coolerpromc.ancientcreature.entity.custom.AncientCreatureAction;
import com.coolerpromc.ancientcreature.entity.custom.AncientCreatureEntity;
import com.coolerpromc.ancientcreature.entity.util.CreatureBlockBreaker;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

/**
 * A melee attack that opens with a roar.
 *
 * <p>On engaging a target the creature stops, roars for {@code roar_duration} ticks while tracking the
 * target, then charges. The roar goes on cooldown so it does not fire on every re-engage. This is the
 * data-driven equivalent of the goal that used to be hard-coded inside {@code TyrannosaurusRex}.
 *
 * <p>The roar is published as {@link AncientCreatureAction#ROAR} so the client's animation controller
 * can pick the matching clip; the client never decides when it happens.
 */
public record RoarAttackBehavior(
    double speedModifier,
    boolean followWithoutSight,
    int roarDuration,
    int roarCooldown,
    boolean breakBlocks,
    double breakReach,
    boolean adultsOnly
) implements CreatureBehaviorConfig {

    public static final MapCodec<RoarAttackBehavior> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
        BehaviorCodecs.SPEED.optionalFieldOf("speed_multiplier", 1.35).forGetter(RoarAttackBehavior::speedModifier),
        Codec.BOOL.optionalFieldOf("follow_without_sight", true).forGetter(RoarAttackBehavior::followWithoutSight),
        BehaviorCodecs.TICKS.optionalFieldOf("roar_duration", 48).forGetter(RoarAttackBehavior::roarDuration),
        BehaviorCodecs.TICKS.optionalFieldOf("roar_cooldown", 240).forGetter(RoarAttackBehavior::roarCooldown),
        Codec.BOOL.optionalFieldOf("break_blocks", true).forGetter(RoarAttackBehavior::breakBlocks),
        BehaviorCodecs.DISTANCE.optionalFieldOf("break_reach", 1.0).forGetter(RoarAttackBehavior::breakReach),
        Codec.BOOL.optionalFieldOf("adults_only", true).forGetter(RoarAttackBehavior::adultsOnly)
    ).apply(i, RoarAttackBehavior::new));

    @Override
    public CreatureBehaviorType<?> type() {
        return CreatureBehaviorRegistry.ROAR_ATTACK;
    }

    @Override
    public Goal createGoal(AncientCreatureEntity entity) {
        return new RoaringAttackGoal(entity, this);
    }

    private static final class RoaringAttackGoal extends MeleeAttackGoal {
        private final AncientCreatureEntity creature;
        private final RoarAttackBehavior config;
        private int roarTicks;
        private int cooldown;

        private RoaringAttackGoal(AncientCreatureEntity creature, RoarAttackBehavior config) {
            super(creature, config.speedModifier(), config.followWithoutSight());
            this.creature = creature;
            this.config = config;
        }

        @Override
        public boolean canUse() {
            if (this.config.adultsOnly() && this.creature.isBaby()) {
                return false;
            }
            return super.canUse();
        }

        @Override
        public void start() {
            super.start();
            if (this.cooldown <= 0) {
                this.roarTicks = this.config.roarDuration();
                this.cooldown = this.config.roarCooldown();
                this.creature.getNavigation().stop();
                this.creature.setAction(AncientCreatureAction.ROAR, this.config.roarDuration());

                SoundEvent roar = this.creature.speciesSounds().alertSound();
                if (roar != null) {
                    this.creature.playSound(roar, 2.2F, 0.9F + this.creature.getRandom().nextFloat() * 0.08F);
                }
            } else {
                this.creature.setSprinting(true);
            }
        }

        @Override
        public void tick() {
            if (this.cooldown > 0) {
                this.cooldown--;
            }

            if (this.roarTicks > 0) {
                // Rooted in place for the length of the roar, still tracking the target.
                this.creature.getNavigation().stop();
                LivingEntity target = this.creature.getTarget();
                if (target != null) {
                    this.creature.getLookControl().setLookAt(target, 30.0F, 30.0F);
                }
                if (--this.roarTicks == 0) {
                    this.creature.setSprinting(true);
                }
                return;
            }

            super.tick();

            if (this.config.breakBlocks()
                && this.creature.level() instanceof ServerLevel serverLevel
                && this.creature.isSprinting()
                && this.creature.tickCount % 2 == 0) {
                CreatureBlockBreaker.destroyChargeObstacles(serverLevel, this.creature, this.config.breakReach());
            }
        }

        @Override
        public void stop() {
            super.stop();
            this.roarTicks = 0;
            this.creature.setSprinting(false);
            this.creature.clearAction(AncientCreatureAction.ROAR);
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }
}
