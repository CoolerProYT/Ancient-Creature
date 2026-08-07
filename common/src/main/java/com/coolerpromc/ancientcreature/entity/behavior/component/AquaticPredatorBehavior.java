package com.coolerpromc.ancientcreature.entity.behavior.component;

import com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorConfig;
import com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorRegistry;
import com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorType;
import com.coolerpromc.ancientcreature.entity.custom.AncientCreatureEntity;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.AgeableWaterCreature;
import net.minecraft.world.entity.animal.fish.WaterAnimal;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.player.Player;

/**
 * Hunts whatever is swimming nearby: players in the water, and other aquatic life.
 *
 * <p>Reproduces the {@code isValidPrey} check that used to live inside {@code Megalodon}, without any
 * species test in the AI. The owner and the hunter's own species are excluded.
 */
public record AquaticPredatorBehavior(
    int randomInterval,
    boolean targetPlayers,
    boolean targetAquaticLife,
    boolean requireInWater,
    boolean adultsOnly,
    boolean requiresHunger
) implements CreatureBehaviorConfig {

    public static final MapCodec<AquaticPredatorBehavior> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
        BehaviorCodecs.CHANCE.optionalFieldOf("random_interval", 10).forGetter(AquaticPredatorBehavior::randomInterval),
        Codec.BOOL.optionalFieldOf("target_players", true).forGetter(AquaticPredatorBehavior::targetPlayers),
        Codec.BOOL.optionalFieldOf("target_aquatic_life", true).forGetter(AquaticPredatorBehavior::targetAquaticLife),
        Codec.BOOL.optionalFieldOf("require_in_water", true).forGetter(AquaticPredatorBehavior::requireInWater),
        Codec.BOOL.optionalFieldOf("adults_only", true).forGetter(AquaticPredatorBehavior::adultsOnly),
        Codec.BOOL.optionalFieldOf("requires_hunger", true).forGetter(AquaticPredatorBehavior::requiresHunger)
    ).apply(i, AquaticPredatorBehavior::new));


    @Override
    public CreatureBehaviorType<?> type() {
        return CreatureBehaviorRegistry.AQUATIC_PREDATOR;
    }

    @Override
    public CreatureBehaviorConfig.GoalSlot slot() {
        return CreatureBehaviorConfig.GoalSlot.TARGET;
    }

    @Override
    public Goal createGoal(AncientCreatureEntity entity) {
        return new NearestAttackableTargetGoal<>(entity, LivingEntity.class, this.randomInterval, true, false,
            (target, level) -> this.isPrey(entity, target));
    }

    private boolean isPrey(AncientCreatureEntity creature, LivingEntity target) {
        if (this.adultsOnly && creature.isBaby()) {
            return false;
        }
        if (this.requiresHunger && !creature.wantsToHunt()) {
            return false;
        }
        if (creature.isOwnedBy(target)) {
            return false;
        }
        if (this.requireInWater && !target.isInWater()) {
            return false;
        }
        if (target instanceof Player player) {
            return this.targetPlayers && !player.isCreative() && !player.isSpectator();
        }
        return this.targetAquaticLife
            && (target instanceof AgeableWaterCreature || target instanceof WaterAnimal || target instanceof Guardian);
    }
}
