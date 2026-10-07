package com.coolerpromc.ancientcreature.entity.goal;

import com.coolerpromc.ancientcreature.entity.custom.AncientCreatureEntity;
import com.coolerpromc.ancientcreature.entity.custom.CreatureCommand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.EnumSet;

/**
 * The goals behind {@link CreatureCommand}. They are added to every species automatically, ahead of
 * the species' own behaviour, and do nothing for creatures without an owner.
 */
public final class CommandGoals {
    private CommandGoals() {
    }

    /** {@link CreatureCommand#STAY}: holds position. Swimming and floating still run. */
    public static final class Stay extends Goal {
        private final AncientCreatureEntity creature;

        public Stay(AncientCreatureEntity creature) {
            this.creature = creature;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP));
        }

        @Override
        public boolean canUse() {
            return this.creature.getCommand() == CreatureCommand.STAY && !this.creature.isVehicle()
                && (this.creature.onGround() || this.creature.isInWater() || this.creature.speciesCategory().isFlying());
        }

        @Override
        public boolean canContinueToUse() {
            return this.creature.getCommand() == CreatureCommand.STAY && !this.creature.isVehicle();
        }

        @Override
        public void start() {
            this.creature.getNavigation().stop();
            this.creature.setTarget(null);
        }

        @Override
        public void tick() {
            this.creature.getNavigation().stop();
            if (this.creature.speciesCategory().isFlying() && !this.creature.onGround()) {
                // a flyer told to stay settles down rather than hovering in place forever
                Vec3 v = this.creature.getDeltaMovement();
                this.creature.setDeltaMovement(v.x * 0.6, Math.min(v.y, -0.08), v.z * 0.6);
            }
        }
    }

    /** {@link CreatureCommand#FOLLOW}: walks, swims or flies after the owner; teleports to catch up. */
    public static final class FollowOwner extends Goal {
        private static final float START = 10.0F;
        private static final float STOP = 4.0F;
        private static final double TELEPORT_SQR = 24.0 * 24.0;

        private final AncientCreatureEntity creature;
        private final double speed;
        private @Nullable LivingEntity owner;
        private int recalc;
        private float oldWaterCost;

        public FollowOwner(AncientCreatureEntity creature, double speed) {
            this.creature = creature;
            this.speed = speed;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        private float startDistance() {
            return START + this.creature.getBbWidth();
        }

        private float stopDistance() {
            return STOP + this.creature.getBbWidth();
        }

        @Override
        public boolean canUse() {
            if (this.creature.getCommand() != CreatureCommand.FOLLOW || this.creature.isVehicle() || this.creature.isLeashed()) {
                return false;
            }
            LivingEntity owner = this.creature.getOwner();
            if (owner == null || owner.isSpectator() || owner.level() != this.creature.level()) {
                return false;
            }
            if (this.creature.distanceToSqr(owner) < this.startDistance() * this.startDistance()) {
                return false;
            }
            this.owner = owner;
            return true;
        }

        @Override
        public boolean canContinueToUse() {
            return this.owner != null && this.creature.getCommand() == CreatureCommand.FOLLOW && !this.creature.isVehicle()
                && this.owner.isAlive() && this.owner.level() == this.creature.level()
                && this.creature.distanceToSqr(this.owner) > this.stopDistance() * this.stopDistance();
        }

        @Override
        public void start() {
            this.recalc = 0;
            this.oldWaterCost = this.creature.getPathfindingMalus(PathType.WATER);
            if (!this.creature.speciesCategory().isAquatic()) {
                this.creature.setPathfindingMalus(PathType.WATER, 0.0F);
            }
        }

        @Override
        public void stop() {
            this.owner = null;
            this.creature.getNavigation().stop();
            this.creature.setPathfindingMalus(PathType.WATER, this.oldWaterCost);
        }

        @Override
        public void tick() {
            if (this.owner == null) {
                return;
            }
            this.creature.getLookControl().setLookAt(this.owner, 10.0F, this.creature.getMaxHeadXRot());
            if (--this.recalc > 0) {
                return;
            }
            this.recalc = this.adjustedTickDelay(10);
            if (this.creature.distanceToSqr(this.owner) >= TELEPORT_SQR && !this.creature.speciesCategory().isAquatic()) {
                if (teleportNear(this.creature, this.owner.blockPosition())) {
                    return;
                }
            }
            this.creature.getNavigation().moveTo(this.owner, this.speed);
        }
    }

    /** Tries a few spots 2-4 blocks around {@code target} where the creature's whole hitbox fits. */
    public static boolean teleportNear(AncientCreatureEntity creature, BlockPos target) {
        int reach = 2 + (int) Math.ceil(creature.getBbWidth());
        for (int attempt = 0; attempt < 12; attempt++) {
            int dx = creature.getRandom().nextIntBetweenInclusive(-reach - 2, reach + 2);
            int dz = creature.getRandom().nextIntBetweenInclusive(-reach - 2, reach + 2);
            if (Math.abs(dx) < reach && Math.abs(dz) < reach) {
                continue;
            }
            for (int dy = 1; dy >= -1; dy--) {
                BlockPos pos = target.offset(dx, dy, dz);
                if (canStandAt(creature, pos)) {
                    creature.snapTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, creature.getYRot(), creature.getXRot());
                    creature.getNavigation().stop();
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean canStandAt(AncientCreatureEntity creature, BlockPos pos) {
        if (!creature.speciesCategory().isFlying() && WalkNodeEvaluator.getPathTypeStatic(creature, pos) != PathType.WALKABLE) {
            return false;
        }
        BlockPos delta = pos.subtract(creature.blockPosition());
        return creature.level().noCollision(creature, creature.getBoundingBox().move(delta));
    }

    /** {@link CreatureCommand#ROAM}: heads back when it has wandered out of its home range. */
    public static final class ReturnHome extends Goal {
        private final AncientCreatureEntity creature;
        private final double speed;

        public ReturnHome(AncientCreatureEntity creature, double speed) {
            this.creature = creature;
            this.speed = speed;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return this.creature.getCommand() == CreatureCommand.ROAM && this.creature.hasHome() && !this.creature.isWithinHome() && !this.creature.isVehicle();
        }

        @Override
        public boolean canContinueToUse() {
            return this.canUse() && !this.creature.getNavigation().isDone();
        }

        @Override
        public void start() {
            BlockPos home = this.creature.getHomePosition();
            this.creature.getNavigation().moveTo(home.getX() + 0.5, home.getY(), home.getZ() + 0.5, this.speed);
        }
    }

    /**
     * Following creatures that can fight take on whatever hurts their owner, and whatever their owner
     * attacks — but never another creature of the same owner.
     */
    public static final class DefendOwner extends TargetGoal {
        private final AncientCreatureEntity creature;
        private @Nullable LivingEntity candidate;
        private int lastHurtBy;
        private int lastHurt;

        public DefendOwner(AncientCreatureEntity creature) {
            super(creature, false);
            this.creature = creature;
            this.setFlags(EnumSet.of(Flag.TARGET));
        }

        @Override
        public boolean canUse() {
            if (this.creature.getCommand() != CreatureCommand.FOLLOW || !this.creature.canFight() || this.creature.isForcedPassive() || this.creature.isBaby()) {
                return false;
            }
            LivingEntity owner = this.creature.getOwner();
            if (owner == null) {
                return false;
            }
            LivingEntity attacker = owner.getLastHurtByMob();
            if (attacker != null && owner.getLastHurtByMobTimestamp() != this.lastHurtBy && this.valid(attacker, owner)) {
                this.candidate = attacker;
                this.lastHurtBy = owner.getLastHurtByMobTimestamp();
                return true;
            }
            LivingEntity victim = owner.getLastHurtMob();
            if (victim != null && owner.getLastHurtMobTimestamp() != this.lastHurt && this.valid(victim, owner)) {
                this.candidate = victim;
                this.lastHurt = owner.getLastHurtMobTimestamp();
                return true;
            }
            return false;
        }

        private boolean valid(LivingEntity target, LivingEntity owner) {
            if (target == this.creature || target == owner) {
                return false;
            }
            if (target instanceof AncientCreatureEntity other && other.isOwnedBy(owner)) {
                return false;
            }
            return this.canAttack(target, TargetingConditions.DEFAULT);
        }

        @Override
        public void start() {
            this.creature.setTarget(this.candidate);
            super.start();
        }
    }
}
