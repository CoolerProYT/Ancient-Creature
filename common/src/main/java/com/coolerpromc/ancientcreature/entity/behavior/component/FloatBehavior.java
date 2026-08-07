package com.coolerpromc.ancientcreature.entity.behavior.component;

import com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorConfig;
import com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorRegistry;
import com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorType;
import com.coolerpromc.ancientcreature.entity.custom.AncientCreatureEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;

/** Keeps a land creature's head above water. Skipped for aquatic species. */
public record FloatBehavior() implements CreatureBehaviorConfig {
    public static final FloatBehavior INSTANCE = new FloatBehavior();
    public static final MapCodec<FloatBehavior> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public CreatureBehaviorType<?> type() {
        return CreatureBehaviorRegistry.FLOAT;
    }

    @Override
    public Goal createGoal(AncientCreatureEntity entity) {
        return entity.speciesCategory().isAquatic() ? null : new FloatGoal(entity);
    }
}
