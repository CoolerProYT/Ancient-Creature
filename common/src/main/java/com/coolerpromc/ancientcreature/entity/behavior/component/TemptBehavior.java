package com.coolerpromc.ancientcreature.entity.behavior.component;

import com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorConfig;
import com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorRegistry;
import com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorType;
import com.coolerpromc.ancientcreature.entity.custom.AncientCreatureEntity;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.TemptGoal;

/** Follows a player holding food. The item set comes from the species {@code diet} block. */
public record TemptBehavior(double speedModifier, boolean canScare) implements CreatureBehaviorConfig {
    public static final MapCodec<TemptBehavior> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
        BehaviorCodecs.SPEED.optionalFieldOf("speed_modifier", 1.15).forGetter(TemptBehavior::speedModifier),
        Codec.BOOL.optionalFieldOf("can_scare", false).forGetter(TemptBehavior::canScare)
    ).apply(i, TemptBehavior::new));

    @Override
    public CreatureBehaviorType<?> type() {
        return CreatureBehaviorRegistry.TEMPT;
    }

    @Override
    public Goal createGoal(AncientCreatureEntity entity) {
        if (entity.diet().isEmpty()) {
            return null;
        }
        return new TemptGoal(entity, this.speedModifier, entity.diet(), this.canScare);
    }
}
