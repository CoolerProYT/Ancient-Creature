package com.coolerpromc.ancientcreature.entity.behavior.component;

import com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorConfig;
import com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorRegistry;
import com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorType;
import com.coolerpromc.ancientcreature.entity.custom.AncientCreatureEntity;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

/** Plain chase-and-bite. */
public record MeleeAttackBehavior(double speedModifier, boolean followWithoutSight, boolean adultsOnly) implements CreatureBehaviorConfig {
    public static final MapCodec<MeleeAttackBehavior> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
        BehaviorCodecs.SPEED.optionalFieldOf("speed_modifier", 1.2).forGetter(MeleeAttackBehavior::speedModifier),
        Codec.BOOL.optionalFieldOf("follow_without_sight", true).forGetter(MeleeAttackBehavior::followWithoutSight),
        Codec.BOOL.optionalFieldOf("adults_only", false).forGetter(MeleeAttackBehavior::adultsOnly)
    ).apply(i, MeleeAttackBehavior::new));

    @Override
    public CreatureBehaviorType<?> type() {
        return CreatureBehaviorRegistry.MELEE_ATTACK;
    }

    @Override
    public Goal createGoal(AncientCreatureEntity entity) {
        if (!this.adultsOnly) {
            return new MeleeAttackGoal(entity, this.speedModifier, this.followWithoutSight);
        }
        return new MeleeAttackGoal(entity, this.speedModifier, this.followWithoutSight) {
            @Override
            public boolean canUse() {
                return !entity.isBaby() && super.canUse();
            }
        };
    }
}
