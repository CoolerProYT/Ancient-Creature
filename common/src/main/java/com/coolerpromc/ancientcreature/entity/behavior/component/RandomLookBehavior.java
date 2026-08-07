package com.coolerpromc.ancientcreature.entity.behavior.component;

import com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorConfig;
import com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorRegistry;
import com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorType;
import com.coolerpromc.ancientcreature.entity.custom.AncientCreatureEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;

/** Idle head movement. */
public record RandomLookBehavior() implements CreatureBehaviorConfig {
    public static final RandomLookBehavior INSTANCE = new RandomLookBehavior();
    public static final MapCodec<RandomLookBehavior> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public CreatureBehaviorType<?> type() {
        return CreatureBehaviorRegistry.RANDOM_LOOK;
    }

    @Override
    public Goal createGoal(AncientCreatureEntity entity) {
        return new RandomLookAroundGoal(entity);
    }
}
