package com.coolerpromc.ancientcreature.entity.behavior.component;

import com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorConfig;
import com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorRegistry;
import com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorType;
import com.coolerpromc.ancientcreature.entity.custom.AncientCreatureEntity;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.player.Player;

/** Turns the head toward a nearby player. */
public record LookAtPlayerBehavior(float lookDistance, float probability) implements CreatureBehaviorConfig {
    public static final MapCodec<LookAtPlayerBehavior> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
        BehaviorCodecs.DISTANCE_F.optionalFieldOf("look_distance", 8.0F).forGetter(LookAtPlayerBehavior::lookDistance),
        com.mojang.serialization.Codec.floatRange(0.0F, 1.0F).optionalFieldOf("probability", 0.02F).forGetter(LookAtPlayerBehavior::probability)
    ).apply(i, LookAtPlayerBehavior::new));

    @Override
    public CreatureBehaviorType<?> type() {
        return CreatureBehaviorRegistry.LOOK_AT_PLAYER;
    }

    @Override
    public Goal createGoal(AncientCreatureEntity entity) {
        return new LookAtPlayerGoal(entity, Player.class, this.lookDistance, this.probability);
    }
}
