package com.coolerpromc.ancientcreature.entity.behavior.component;

import com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorConfig;
import com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorRegistry;
import com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorType;
import com.coolerpromc.ancientcreature.entity.custom.AncientCreatureEntity;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal;

/**
 * Free flight for a {@code flying} species.
 *
 * <p>Only applies to flying species — the goal needs the {@code FlyingPathNavigation} that
 * {@link AncientCreatureEntity} installs for that category, so it is skipped for anything else rather
 * than pathing into the ground.
 */
public record FlyBehavior(double speedModifier) implements CreatureBehaviorConfig {
    public static final MapCodec<FlyBehavior> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
        BehaviorCodecs.SPEED.optionalFieldOf("speed_modifier", 1.0).forGetter(FlyBehavior::speedModifier)
    ).apply(i, FlyBehavior::new));

    @Override
    public CreatureBehaviorType<?> type() {
        return CreatureBehaviorRegistry.FLY;
    }

    @Override
    public Goal createGoal(AncientCreatureEntity entity) {
        if (!entity.speciesCategory().isFlying()) {
            return null;
        }
        return new WaterAvoidingRandomFlyingGoal(entity, this.speedModifier);
    }
}
