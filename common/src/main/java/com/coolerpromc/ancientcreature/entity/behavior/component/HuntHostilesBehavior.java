package com.coolerpromc.ancientcreature.entity.behavior.component;

import com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorConfig;
import com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorRegistry;
import com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorType;
import com.coolerpromc.ancientcreature.entity.custom.AncientCreatureEntity;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Enemy;

/** Attacks nearby hostile mobs. */
public record HuntHostilesBehavior(int randomInterval, boolean adultsOnly) implements CreatureBehaviorConfig {
    public static final MapCodec<HuntHostilesBehavior> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
        BehaviorCodecs.CHANCE.optionalFieldOf("random_interval", 5).forGetter(HuntHostilesBehavior::randomInterval),
        Codec.BOOL.optionalFieldOf("adults_only", false).forGetter(HuntHostilesBehavior::adultsOnly)
    ).apply(i, HuntHostilesBehavior::new));

    @Override
    public CreatureBehaviorType<?> type() {
        return CreatureBehaviorRegistry.HUNT_HOSTILES;
    }

    @Override
    public CreatureBehaviorConfig.GoalSlot slot() {
        return CreatureBehaviorConfig.GoalSlot.TARGET;
    }

    @Override
    public Goal createGoal(AncientCreatureEntity entity) {
        return new NearestAttackableTargetGoal<>(entity, Mob.class, this.randomInterval, true, false,
            (target, level) -> (!this.adultsOnly || !entity.isBaby()) && target instanceof Enemy);
    }
}
