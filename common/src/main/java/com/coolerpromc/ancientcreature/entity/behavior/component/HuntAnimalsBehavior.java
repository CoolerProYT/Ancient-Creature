package com.coolerpromc.ancientcreature.entity.behavior.component;

import com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorConfig;
import com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorRegistry;
import com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorType;
import com.coolerpromc.ancientcreature.entity.custom.AncientCreatureEntity;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;

/**
 * Preys on ordinary animals.
 *
 * <p>Members of the hunter's own species are excluded by {@code AncientCreatureEntity#canAttack}, so a
 * pack of the same creature never turns on itself.
 */
public record HuntAnimalsBehavior(int randomInterval, boolean adultsOnly) implements CreatureBehaviorConfig {
    public static final MapCodec<HuntAnimalsBehavior> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
        BehaviorCodecs.CHANCE.optionalFieldOf("random_interval", 10).forGetter(HuntAnimalsBehavior::randomInterval),
        Codec.BOOL.optionalFieldOf("adults_only", true).forGetter(HuntAnimalsBehavior::adultsOnly)
    ).apply(i, HuntAnimalsBehavior::new));

    @Override
    public CreatureBehaviorType<?> type() {
        return CreatureBehaviorRegistry.HUNT_ANIMALS;
    }

    @Override
    public CreatureBehaviorConfig.GoalSlot slot() {
        return CreatureBehaviorConfig.GoalSlot.TARGET;
    }

    @Override
    public Goal createGoal(AncientCreatureEntity entity) {
        return new NearestAttackableTargetGoal<>(entity, Animal.class, this.randomInterval, true, false,
            (target, level) -> (!this.adultsOnly || !entity.isBaby()) && target instanceof Animal);
    }
}
