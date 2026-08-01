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
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public class Triceratops extends OwnedAncientCreature {
    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState chargeAnimationState = new AnimationState();
    public final AnimationState grazeAnimationState = new AnimationState();
    public final AnimationState bellowAnimationState = new AnimationState();

    public Triceratops(EntityType<? extends Triceratops> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createAnimalAttributes()
                .add(Attributes.MAX_HEALTH, 45.0)
                .add(Attributes.MOVEMENT_SPEED, 0.22)
                .add(Attributes.ATTACK_DAMAGE, 8.0)
                .add(Attributes.ATTACK_KNOCKBACK, 1.5)
                .add(Attributes.FOLLOW_RANGE, 20.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.65);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new ChargeAttackGoal(this, 1.45, true));
        this.goalSelector.addGoal(3, new BreedGoal(this, 1.0));
        this.goalSelector.addGoal(4, new TemptGoal(this, 1.15, stack -> stack.is(Items.WHEAT), false));
        this.goalSelector.addGoal(5, new FollowParentGoal(this, 1.1));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.9));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, 5, true, false, this::isArmedPlayerThreat));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, net.minecraft.world.entity.Mob.class, 5, true, false, (target, level) -> target instanceof Enemy));
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide()) {
            boolean moving = this.walkAnimation.isMoving();
            this.chargeAnimationState.animateWhen(this.isAggressive() && moving, this.tickCount);
            this.idleAnimationState.animateWhen(!moving && !this.chargeAnimationState.isStarted() && !this.grazeAnimationState.isStarted() && !this.bellowAnimationState.isStarted(), this.tickCount);
        } else if (this.level() instanceof ServerLevel serverLevel && this.isAggressive() && this.isSprinting() && this.getTarget() != null && this.tickCount % 2 == 0) {
            CreatureBlockBreaker.destroyChargeObstacles(serverLevel, this, 0.6);
        }
    }

    private boolean isArmedPlayerThreat(net.minecraft.world.entity.LivingEntity target, ServerLevel level) {
        if (!(target instanceof Player player) || player.isCreative() || player.isSpectator() || this.isOwnedBy(player)) {
            return false;
        }

        return this.distanceToSqr(player) <= 144.0 && (isWeapon(player.getMainHandItem()) || isWeapon(player.getOffhandItem()));
    }

    private static boolean isWeapon(ItemStack stack) {
        return stack.is(ItemTags.WEAPON_ENCHANTABLE) || stack.is(ItemTags.BOW_ENCHANTABLE) || stack.is(ItemTags.CROSSBOW_ENCHANTABLE) || stack.is(ItemTags.TRIDENT_ENCHANTABLE);
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(Items.WHEAT);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.TRICERATOPS_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.TRICERATOPS_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.TRICERATOPS_DEATH.get();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState blockState) {
        this.playSound(ModSounds.TRICERATOPS_STEP.get(), 0.35F, 0.9F + this.random.nextFloat() * 0.15F);
    }

    @Override
    protected void playAttackSound() {
        this.playSound(ModSounds.TRICERATOPS_ATTACK.get(), 1.0F, 0.9F + this.random.nextFloat() * 0.1F);
    }

    @Override
    public int getAmbientSoundInterval() {
        return 240;
    }

    @Override
    public @Nullable Triceratops getBreedOffspring(ServerLevel level, AgeableMob partner) {
        return ModEntities.TRICERATOPS.get().create(level, EntitySpawnReason.BREEDING);
    }

    @Override
    public float getAgeScale() {
        return this.isBaby() ? 0.5F : 1.0F;
    }

    private static class ChargeAttackGoal extends MeleeAttackGoal {
        private final Triceratops triceratops;

        private ChargeAttackGoal(Triceratops triceratops, double speedModifier, boolean followTargetWithoutSight) {
            super(triceratops, speedModifier, followTargetWithoutSight);
            this.triceratops = triceratops;
        }

        @Override
        public void start() {
            super.start();
            this.triceratops.setSprinting(true);
            this.triceratops.playSound(ModSounds.TRICERATOPS_BELLOW.get(), 1.4F, 0.9F);
        }

        @Override
        public void stop() {
            super.stop();
            this.triceratops.setSprinting(false);
        }
    }
}
