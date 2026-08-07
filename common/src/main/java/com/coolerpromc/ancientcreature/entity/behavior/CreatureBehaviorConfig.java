package com.coolerpromc.ancientcreature.entity.behavior;

import com.coolerpromc.ancientcreature.entity.custom.AncientCreatureEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import org.jspecify.annotations.Nullable;

/**
 * The parsed configuration of one behavior component from a species definition.
 *
 * <p>Implementations are immutable records. They can only be produced by a {@link CreatureBehaviorType}
 * registered in {@link CreatureBehaviorRegistry}, so a datapack can never reach arbitrary Java — it can
 * only select from, and configure, this closed set.
 */
public interface CreatureBehaviorConfig {
    CreatureBehaviorType<?> type();

    /**
     * Builds the vanilla goal for this component.
     *
     * @return the goal, or {@code null} if this component is not applicable to the given entity
     *         (for example a swim goal on a land species).
     */
    @Nullable Goal createGoal(AncientCreatureEntity entity);

    /** Which of the mob's two goal selectors this component belongs to. */
    default GoalSlot slot() {
        return GoalSlot.GOAL;
    }

    enum GoalSlot {
        /** {@code Mob#goalSelector} — what the creature does. */
        GOAL,
        /** {@code Mob#targetSelector} — what the creature decides to attack. */
        TARGET
    }
}
