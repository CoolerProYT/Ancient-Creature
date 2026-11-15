package com.coolerpromc.ancientcreature.entity.behavior.component;

import com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorConfig;
import com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorRegistry;
import com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorType;
import com.coolerpromc.ancientcreature.entity.custom.AncientCreatureEntity;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.Goal;

public record BreedBehavior(double speedModifier) implements CreatureBehaviorConfig {
    public static final MapCodec<BreedBehavior> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
        BehaviorCodecs.SPEED.optionalFieldOf("speed_modifier", 1.0).forGetter(BreedBehavior::speedModifier)
    ).apply(i, BreedBehavior::new));

    @Override
    public CreatureBehaviorType<?> type() {
        return CreatureBehaviorRegistry.BREED;
    }

    @Override
    public Goal createGoal(AncientCreatureEntity entity) {
        return new BreedGoal(entity, this.speedModifier, AncientCreatureEntity.class);
    }
}
