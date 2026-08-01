package com.coolerpromc.ancientcreature.entity.custom;

import com.coolerpromc.ancientcreature.entity.ModEntities;
import com.coolerpromc.ancientcreature.entity.util.CreatureBlockBreaker;
import com.coolerpromc.ancientcreature.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public class TyrannosaurusRex extends OwnedAncientCreature {
    private static final byte ROAR_EVENT = 60;
    private static final byte BITE_EVENT = 61;
    private static final int ROAR_DURATION = 48;

    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState roarAnimationState = new AnimationState();
    public final AnimationState biteAnimationState = new AnimationState();
    private int roarCooldown;
    private int roarAnimationTicks;
    private int biteAnimationTicks;

    public TyrannosaurusRex(EntityType<? extends TyrannosaurusRex> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createAnimalAttributes()
                .add(Attributes.MAX_HEALTH, 100.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.ATTACK_DAMAGE, 16.0)
                .add(Attributes.ATTACK_KNOCKBACK, 2.0)
                .add(Attributes.FOLLOW_RANGE, 32.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.75)
                .add(Attributes.ARMOR, 6.0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new RoaringAttackGoal(this, 1.35, true));
        this.goalSelector.addGoal(3, new BreedGoal(this, 1.0));
        this.goalSelector.addGoal(4, new TemptGoal(this, 1.05, stack -> stack.is(ItemTags.MEAT), false));
        this.goalSelector.addGoal(5, new FollowParentGoal(this, 1.1));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 12.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, this::isValidPlayerPrey));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Animal.class, 10, true, false, this::isValidAnimalPrey));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Mob.class, 10, true, false,
                (target, level) -> !this.isBaby() && target instanceof Enemy));
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide()) {
            if (this.roarAnimationTicks > 0 && --this.roarAnimationTicks == 0) {
                this.roarAnimationState.stop();
            }
            if (this.biteAnimationTicks > 0 && --this.biteAnimationTicks == 0) {
                this.biteAnimationState.stop();
            }
            boolean moving = this.walkAnimation.isMoving();
            this.idleAnimationState.animateWhen(!moving && !this.roarAnimationState.isStarted() && !this.biteAnimationState.isStarted(), this.tickCount);
        } else {
            if (this.roarCooldown > 0) {
                --this.roarCooldown;
            }
            if (this.level() instanceof ServerLevel serverLevel
                    && !this.isBaby() && this.isAggressive() && this.isSprinting()
                    && this.getTarget() != null && this.tickCount % 2 == 0) {
                CreatureBlockBreaker.destroyChargeObstacles(serverLevel, this, 1.0);
            }
        }
    }

    @Override
    public void handleEntityEvent(byte event) {
        if (event == ROAR_EVENT) {
            this.roarAnimationState.start(this.tickCount);
            this.roarAnimationTicks = ROAR_DURATION;
        } else if (event == BITE_EVENT) {
            this.biteAnimationState.start(this.tickCount);
            this.biteAnimationTicks = 16;
        } else {
            super.handleEntityEvent(event);
        }
    }

    private boolean isValidPlayerPrey(LivingEntity target, ServerLevel level) {
        return !this.isBaby() && target instanceof Player player && !player.isCreative() && !player.isSpectator() && !this.isOwnedBy(player);
    }

    private boolean isValidAnimalPrey(LivingEntity target, ServerLevel level) {
        return !this.isBaby() && target instanceof Animal && !(target instanceof TyrannosaurusRex);
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(ItemTags.MEAT);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.TYRANNOSAURUS_REX_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.TYRANNOSAURUS_REX_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.TYRANNOSAURUS_REX_DEATH.get();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState blockState) {
        this.playSound(ModSounds.TYRANNOSAURUS_REX_STEP.get(), 0.8F, 0.85F + this.random.nextFloat() * 0.1F);
    }

    @Override
    protected void playAttackSound() {
        if (!this.level().isClientSide()) {
            this.level().broadcastEntityEvent(this, BITE_EVENT);
        }
        this.playSound(ModSounds.TYRANNOSAURUS_REX_BITE.get(), 1.3F, 0.9F + this.random.nextFloat() * 0.1F);
    }

    @Override
    public int getAmbientSoundInterval() {
        return 300;
    }

    @Override
    public @Nullable TyrannosaurusRex getBreedOffspring(ServerLevel level, AgeableMob partner) {
        return ModEntities.TYRANNOSAURUS_REX.get().create(level, EntitySpawnReason.BREEDING);
    }

    @Override
    public float getAgeScale() {
        return this.isBaby() ? 0.42F : 1.0F;
    }

    private static class RoaringAttackGoal extends MeleeAttackGoal {
        private final TyrannosaurusRex tyrannosaurusRex;
        private int roarTicks;

        private RoaringAttackGoal(TyrannosaurusRex tyrannosaurusRex, double speedModifier, boolean followTargetWithoutSight) {
            super(tyrannosaurusRex, speedModifier, followTargetWithoutSight);
            this.tyrannosaurusRex = tyrannosaurusRex;
        }

        @Override
        public boolean canUse() {
            return !this.tyrannosaurusRex.isBaby() && super.canUse();
        }

        @Override
        public void start() {
            super.start();
            if (this.tyrannosaurusRex.roarCooldown <= 0) {
                this.roarTicks = ROAR_DURATION;
                this.tyrannosaurusRex.roarCooldown = 240;
                this.tyrannosaurusRex.getNavigation().stop();
                this.tyrannosaurusRex.level().broadcastEntityEvent(this.tyrannosaurusRex, ROAR_EVENT);
                this.tyrannosaurusRex.playSound(ModSounds.TYRANNOSAURUS_REX_ROAR.get(), 2.2F, 0.9F + this.tyrannosaurusRex.random.nextFloat() * 0.08F);
            } else {
                this.tyrannosaurusRex.setSprinting(true);
            }
        }

        @Override
        public void tick() {
            if (this.roarTicks > 0) {
                this.tyrannosaurusRex.getNavigation().stop();
                LivingEntity target = this.tyrannosaurusRex.getTarget();
                if (target != null) {
                    this.tyrannosaurusRex.getLookControl().setLookAt(target, 30.0F, 30.0F);
                }
                if (--this.roarTicks == 0) {
                    this.tyrannosaurusRex.setSprinting(true);
                }
                return;
            }
            super.tick();
        }

        @Override
        public void stop() {
            super.stop();
            this.roarTicks = 0;
            this.tyrannosaurusRex.setSprinting(false);
        }
    }
}
