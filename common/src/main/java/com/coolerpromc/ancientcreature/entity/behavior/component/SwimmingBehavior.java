package com.coolerpromc.ancientcreature.entity.behavior.component;

import com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorConfig;
import com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorRegistry;
import com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorType;
import com.coolerpromc.ancientcreature.entity.custom.AncientCreatureEntity;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;

/** Open-water wandering for aquatic species. */
public record SwimmingBehavior(double speedModifier, int interval) implements CreatureBehaviorConfig {
    public static final MapCodec<SwimmingBehavior> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
        BehaviorCodecs.SPEED.optionalFieldOf("speed_modifier", 1.0).forGetter(SwimmingBehavior::speedModifier),
        BehaviorCodecs.TICKS.optionalFieldOf("interval", 10).forGetter(SwimmingBehavior::interval)
    ).apply(i, SwimmingBehavior::new));

    @Override
    public CreatureBehaviorType<?> type() {
        return CreatureBehaviorRegistry.SWIMMING;
    }

    @Override
    public Goal createGoal(AncientCreatureEntity entity) {
        return new RandomSwimmingGoal(entity, this.speedModifier, Math.max(1, this.interval));
    }
}
