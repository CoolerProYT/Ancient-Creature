package com.coolerpromc.ancientcreature.entity.behavior.component;

import com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorConfig;
import com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorRegistry;
import com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorType;
import com.coolerpromc.ancientcreature.entity.custom.AncientCreatureEntity;
import com.coolerpromc.ancientcreature.entity.util.CreatureBlockBreaker;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

/**
 * A sprinting charge at the current target.
 *
 * <p>This is the data-driven equivalent of the goal that used to be hard-coded inside
 * {@code Triceratops}: it sprints, plays the species' {@code alert} sound on start, and (optionally)
 * ploughs through blocks tagged {@code ancientcreature:creature_destroyable} while charging.
 */
public record ChargeAttackBehavior(
    double speedModifier,
    boolean followWithoutSight,
    boolean breakBlocks,
    double breakReach
) implements CreatureBehaviorConfig {
    public static final MapCodec<ChargeAttackBehavior> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
        BehaviorCodecs.SPEED.optionalFieldOf("speed_multiplier", 1.45).forGetter(ChargeAttackBehavior::speedModifier),
        Codec.BOOL.optionalFieldOf("follow_without_sight", true).forGetter(ChargeAttackBehavior::followWithoutSight),
        Codec.BOOL.optionalFieldOf("break_blocks", true).forGetter(ChargeAttackBehavior::breakBlocks),
        BehaviorCodecs.DISTANCE.optionalFieldOf("break_reach", 0.6).forGetter(ChargeAttackBehavior::breakReach)
    ).apply(i, ChargeAttackBehavior::new));

    @Override
    public CreatureBehaviorType<?> type() {
        return CreatureBehaviorRegistry.CHARGE_ATTACK;
    }

    @Override
    public Goal createGoal(AncientCreatureEntity entity) {
        return new ChargeGoal(entity, this);
    }

    private static final class ChargeGoal extends MeleeAttackGoal {
        private final AncientCreatureEntity creature;
        private final ChargeAttackBehavior config;

        private ChargeGoal(AncientCreatureEntity creature, ChargeAttackBehavior config) {
            super(creature, config.speedModifier(), config.followWithoutSight());
            this.creature = creature;
            this.config = config;
        }

        @Override
        public void start() {
            super.start();
            this.creature.setSprinting(true);
            SoundEvent alert = this.creature.speciesSounds().alertSound();
            if (alert != null) {
                this.creature.playSound(alert, 1.4F, 0.9F);
            }
        }

        @Override
        public void tick() {
            super.tick();
            if (this.config.breakBlocks()
                && this.creature.level() instanceof ServerLevel serverLevel
                && this.creature.tickCount % 2 == 0) {
                CreatureBlockBreaker.destroyChargeObstacles(serverLevel, this.creature, this.config.breakReach());
            }
        }

        @Override
        public void stop() {
            super.stop();
            this.creature.setSprinting(false);
        }
    }
}
