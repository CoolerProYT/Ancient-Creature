package com.coolerpromc.ancientcreature.entity.behavior.component;

import com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorConfig;
import com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorRegistry;
import com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorType;
import com.coolerpromc.ancientcreature.entity.custom.AncientCreatureEntity;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.PanicGoal;

/** Runs away after being hurt. Suits prey species. */
public record PanicBehavior(double speedModifier) implements CreatureBehaviorConfig {
    public static final MapCodec<PanicBehavior> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
        BehaviorCodecs.SPEED.optionalFieldOf("speed_modifier", 1.25).forGetter(PanicBehavior::speedModifier)
    ).apply(i, PanicBehavior::new));

    @Override
    public CreatureBehaviorType<?> type() {
        return CreatureBehaviorRegistry.PANIC;
    }

    @Override
    public Goal createGoal(AncientCreatureEntity entity) {
        return new PanicGoal(entity, this.speedModifier);
    }
}
