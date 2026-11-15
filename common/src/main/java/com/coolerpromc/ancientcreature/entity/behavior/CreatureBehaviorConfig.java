package com.coolerpromc.ancientcreature.entity.behavior;

import com.coolerpromc.ancientcreature.entity.custom.AncientCreatureEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import org.jspecify.annotations.Nullable;

public interface CreatureBehaviorConfig {
    CreatureBehaviorType<?> type();

    @Nullable Goal createGoal(AncientCreatureEntity entity);

    default GoalSlot slot() {
        return GoalSlot.GOAL;
    }

    enum GoalSlot {

        GOAL,

        TARGET
    }
}
