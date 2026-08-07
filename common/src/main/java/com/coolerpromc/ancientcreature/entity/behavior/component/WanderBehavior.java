package com.coolerpromc.ancientcreature.entity.behavior.component;

import com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorConfig;
import com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorRegistry;
import com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorType;
import com.coolerpromc.ancientcreature.entity.custom.AncientCreatureEntity;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;

/** Idle strolling. Automatically picks a swimming variant for aquatic species. */
public record WanderBehavior(double speedModifier, boolean avoidWater, int interval) implements CreatureBehaviorConfig {
    public static final MapCodec<WanderBehavior> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
        BehaviorCodecs.SPEED.optionalFieldOf("speed_modifier", 0.9).forGetter(WanderBehavior::speedModifier),
        com.mojang.serialization.Codec.BOOL.optionalFieldOf("avoid_water", true).forGetter(WanderBehavior::avoidWater),
        ExtraCodecs.POSITIVE_INT.optionalFieldOf("interval", 120).forGetter(WanderBehavior::interval)
    ).apply(i, WanderBehavior::new));

    @Override
    public CreatureBehaviorType<?> type() {
        return CreatureBehaviorRegistry.WANDER;
    }

    @Override
    public Goal createGoal(AncientCreatureEntity entity) {
        if (entity.speciesCategory().isAquatic()) {
            return new RandomSwimmingGoal(entity, this.speedModifier, this.interval);
        }
        return this.avoidWater
            ? new WaterAvoidingRandomStrollGoal(entity, this.speedModifier)
            : new RandomStrollGoal(entity, this.speedModifier, this.interval);
    }
}
