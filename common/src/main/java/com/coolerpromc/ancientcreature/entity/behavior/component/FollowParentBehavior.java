package com.coolerpromc.ancientcreature.entity.behavior.component;

import com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorConfig;
import com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorRegistry;
import com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorType;
import com.coolerpromc.ancientcreature.entity.custom.AncientCreatureEntity;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.Goal;

/** Babies trail after an adult. */
public record FollowParentBehavior(double speedModifier) implements CreatureBehaviorConfig {
    public static final MapCodec<FollowParentBehavior> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
        BehaviorCodecs.SPEED.optionalFieldOf("speed_modifier", 1.1).forGetter(FollowParentBehavior::speedModifier)
    ).apply(i, FollowParentBehavior::new));

    @Override
    public CreatureBehaviorType<?> type() {
        return CreatureBehaviorRegistry.FOLLOW_PARENT;
    }

    @Override
    public Goal createGoal(AncientCreatureEntity entity) {
        return new FollowParentGoal(entity, this.speedModifier);
    }
}
