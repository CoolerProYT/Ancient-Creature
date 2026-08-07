package com.coolerpromc.ancientcreature.entity.behavior.component;

import com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorConfig;
import com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorRegistry;
import com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorType;
import com.coolerpromc.ancientcreature.entity.custom.AncientCreatureEntity;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Attacks players who come close carrying a weapon.
 *
 * <p>The owner is always excluded, so a tamed creature never turns on the player who hatched it. This
 * reproduces the {@code isArmedPlayerThreat} check that used to be hard-coded in {@code Triceratops},
 * but without any species check inside the AI.
 */
public record TerritorialBehavior(double range, int randomInterval, boolean requireWeapon, boolean adultsOnly)
    implements CreatureBehaviorConfig {

    public static final MapCodec<TerritorialBehavior> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
        BehaviorCodecs.DISTANCE.optionalFieldOf("range", 12.0).forGetter(TerritorialBehavior::range),
        BehaviorCodecs.CHANCE.optionalFieldOf("random_interval", 5).forGetter(TerritorialBehavior::randomInterval),
        Codec.BOOL.optionalFieldOf("require_weapon", true).forGetter(TerritorialBehavior::requireWeapon),
        Codec.BOOL.optionalFieldOf("adults_only", false).forGetter(TerritorialBehavior::adultsOnly)
    ).apply(i, TerritorialBehavior::new));

    @Override
    public CreatureBehaviorType<?> type() {
        return CreatureBehaviorRegistry.TERRITORIAL;
    }

    @Override
    public CreatureBehaviorConfig.GoalSlot slot() {
        return CreatureBehaviorConfig.GoalSlot.TARGET;
    }

    @Override
    public Goal createGoal(AncientCreatureEntity entity) {
        double rangeSqr = this.range * this.range;
        return new NearestAttackableTargetGoal<>(entity, Player.class, this.randomInterval, true, false,
            (target, level) -> this.isThreat(entity, target, rangeSqr));
    }

    private boolean isThreat(AncientCreatureEntity creature, LivingEntity target, double rangeSqr) {
        if (this.adultsOnly && creature.isBaby()) {
            return false;
        }
        if (!(target instanceof Player player) || player.isCreative() || player.isSpectator() || creature.isOwnedBy(player)) {
            return false;
        }
        if (creature.distanceToSqr(player) > rangeSqr) {
            return false;
        }
        return !this.requireWeapon || isWeapon(player.getMainHandItem()) || isWeapon(player.getOffhandItem());
    }

    private static boolean isWeapon(ItemStack stack) {
        return stack.is(ItemTags.WEAPON_ENCHANTABLE)
            || stack.is(ItemTags.BOW_ENCHANTABLE)
            || stack.is(ItemTags.CROSSBOW_ENCHANTABLE)
            || stack.is(ItemTags.TRIDENT_ENCHANTABLE);
    }
}
