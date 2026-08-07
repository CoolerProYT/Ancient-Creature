package com.coolerpromc.ancientcreature.entity.behavior.component;

import com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorConfig;
import com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorRegistry;
import com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorType;
import com.coolerpromc.ancientcreature.entity.custom.AncientCreatureEntity;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;

/** Fights back against whatever hurt it. */
public record DefensiveRetaliationBehavior(boolean alertOthers) implements CreatureBehaviorConfig {
    public static final MapCodec<DefensiveRetaliationBehavior> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
        Codec.BOOL.optionalFieldOf("alert_others", false).forGetter(DefensiveRetaliationBehavior::alertOthers)
    ).apply(i, DefensiveRetaliationBehavior::new));

    @Override
    public CreatureBehaviorType<?> type() {
        return CreatureBehaviorRegistry.DEFENSIVE_RETALIATION;
    }

    @Override
    public CreatureBehaviorConfig.GoalSlot slot() {
        return CreatureBehaviorConfig.GoalSlot.TARGET;
    }

    @Override
    public Goal createGoal(AncientCreatureEntity entity) {
        HurtByTargetGoal goal = new HurtByTargetGoal(entity);
        return this.alertOthers ? goal.setAlertOthers(AncientCreatureEntity.class) : goal;
    }
}
