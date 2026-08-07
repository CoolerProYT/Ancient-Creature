package com.coolerpromc.ancientcreature.entity.behavior.component;

import com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorConfig;
import com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorRegistry;
import com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorType;
import com.coolerpromc.ancientcreature.entity.custom.AncientCreatureEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.TryFindWaterGoal;

/** Sends a beached aquatic creature back towards water. */
public record FindWaterBehavior() implements CreatureBehaviorConfig {
    public static final FindWaterBehavior INSTANCE = new FindWaterBehavior();
    public static final MapCodec<FindWaterBehavior> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public CreatureBehaviorType<?> type() {
        return CreatureBehaviorRegistry.FIND_WATER;
    }

    @Override
    public Goal createGoal(AncientCreatureEntity entity) {
        return new TryFindWaterGoal(entity);
    }
}
