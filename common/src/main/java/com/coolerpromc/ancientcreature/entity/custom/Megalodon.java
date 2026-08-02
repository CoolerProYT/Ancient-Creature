package com.coolerpromc.ancientcreature.entity.custom;

import com.coolerpromc.ancientcreature.entity.ModEntities;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.goal.TryFindWaterGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.animal.AgeableWaterCreature;
import net.minecraft.world.entity.animal.fish.WaterAnimal;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

public class Megalodon extends OwnedWaterAncientCreature {
    private static final byte BITE_EVENT = 62;

    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState swimAnimationState = new AnimationState();
    public final AnimationState biteAnimationState = new AnimationState();
    private int biteAnimationTicks;

    public Megalodon(EntityType<? extends Megalodon> type, Level level) {
        super(type, level);
        this.moveControl = new SmoothSwimmingMoveControl(this, 85, 10, 0.04F, 0.1F, true);
        this.lookControl = new SmoothSwimmingLookControl(this, 10);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 140.0)
                .add(Attributes.MOVEMENT_SPEED, 1.15)
                .add(Attributes.ATTACK_DAMAGE, 20.0)
                .add(Attributes.ATTACK_KNOCKBACK, 2.5)
                .add(Attributes.FOLLOW_RANGE, 40.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.85)
                .add(Attributes.ARMOR, 4.0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new TryFindWaterGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.35, true));
        this.goalSelector.addGoal(5, new RandomSwimmingGoal(this, 1.0, 30));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, true, false, this::isValidPrey));
    }

    private boolean isValidPrey(LivingEntity target, ServerLevel level) {
        if (this.isBaby() || this.isOwnedBy(target) || target instanceof Megalodon || !target.isInWater()) {
            return false;
        }
        if (target instanceof Player player) {
            return !player.isCreative() && !player.isSpectator();
        }
        return target instanceof AgeableWaterCreature || target instanceof WaterAnimal || target instanceof Guardian;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide()) {
            if (this.biteAnimationTicks > 0 && --this.biteAnimationTicks == 0) {
                this.biteAnimationState.stop();
            }
            boolean moving = this.walkAnimation.isMoving();
            this.swimAnimationState.animateWhen(moving && !this.biteAnimationState.isStarted(), this.tickCount);
            this.idleAnimationState.animateWhen(!moving && !this.biteAnimationState.isStarted(), this.tickCount);
        }
    }

    @Override
    public void handleEntityEvent(byte event) {
        if (event == BITE_EVENT) {
            this.biteAnimationState.start(this.tickCount);
            this.biteAnimationTicks = 16;
        } else {
            super.handleEntityEvent(event);
        }
    }

    @Override
    public void playAttackSound() {
        if (!this.level().isClientSide()) {
            this.level().broadcastEntityEvent(this, BITE_EVENT);
        }
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new WaterBoundPathNavigation(this, level);
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        return !this.isBaby() && super.canAttack(target);
    }

    @Override
    public @Nullable Megalodon getBreedOffspring(ServerLevel level, AgeableMob partner) {
        return ModEntities.MEGALODON.get().create(level, EntitySpawnReason.BREEDING);
    }

    @Override
    public float getAgeScale() {
        return this.isBaby() ? 0.35F : 1.0F;
    }
}
