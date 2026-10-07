package com.coolerpromc.ancientcreature.entity.riding;

import com.coolerpromc.ancientcreature.entity.custom.AncientCreatureAction;
import com.coolerpromc.ancientcreature.entity.custom.AncientCreatureEntity;
import com.coolerpromc.ancientcreature.entity.util.CreatureBlockBreaker;
import com.coolerpromc.ancientcreature.species.SpeciesRidingProperties;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * Carries out a ridden creature's mount ability. Every ability spares the rider, the rider's other
 * creatures and anything the rider could not attack anyway (for example on a no-PvP server).
 */
public final class MountAbilities {
    private MountAbilities() {
    }

    public static void perform(ServerLevel level, AncientCreatureEntity creature, Player rider) {
        SpeciesRidingProperties riding = creature.speciesDefinition().riding();
        float power = riding.power();
        float damage = (float) Math.max(2.0, creature.getAttributeValue(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE)) * power;
        float size = Math.max(1.0F, creature.getBbWidth());
        Vec3 look = Vec3.directionFromRotation(0.0F, creature.getYRot());

        switch (riding.ability()) {
            case NONE -> {
                return;
            }
            case ROAR -> {
                creature.setAction(AncientCreatureAction.ROAR, 40);
                play(level, creature, creature.speciesSounds().alertSound(), SoundEvents.RAVAGER_ROAR, 2.0F);
                for (LivingEntity target : around(level, creature, rider, 10.0 * power, 6.0)) {
                    if (target instanceof Enemy) {
                        target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 1), creature);
                        target.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 100, 1), creature);
                        if (target instanceof PathfinderMob mob) {
                            Vec3 away = DefaultRandomPos.getPosAway(mob, 16, 7, creature.position());
                            if (away != null) {
                                mob.getNavigation().moveTo(away.x, away.y, away.z, 1.4);
                            }
                            mob.setTarget(null);
                        }
                    }
                }
                level.sendParticles(ParticleTypes.SONIC_BOOM, creature.getX() + look.x * size, creature.getEyeY(), creature.getZ() + look.z * size, 1, 0, 0, 0, 0);
            }
            case CHARGE -> {
                creature.setAction(AncientCreatureAction.ATTACK, 16);
                creature.setDeltaMovement(creature.getDeltaMovement().add(look.scale(1.4 * power)).add(0, 0.1, 0));
                creature.needsSync = true;
                hitAhead(level, creature, rider, look, size + 2.5, size, damage, 1.6F * power);
                CreatureBlockBreaker.destroyChargeObstacles(level, creature, 1.5);
                play(level, creature, creature.speciesSounds().attackSound(), SoundEvents.RAVAGER_ATTACK, 1.5F);
            }
            case BITE -> {
                creature.setAction(AncientCreatureAction.ATTACK, 10);
                LivingEntity bitten = null;
                double best = Double.MAX_VALUE;
                for (LivingEntity target : inFront(level, creature, rider, look, size + 2.5, size * 0.75)) {
                    double d = target.distanceToSqr(creature);
                    if (d < best) {
                        best = d;
                        bitten = target;
                    }
                }
                if (bitten != null) {
                    bitten.hurtServer(level, level.damageSources().mobAttack(creature), damage * 1.6F);
                    bitten.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 60, 1), creature);
                }
                play(level, creature, creature.speciesSounds().attackSound(), SoundEvents.EVOKER_FANGS_ATTACK, 1.4F);
            }
            case TAIL_SWEEP -> {
                creature.setAction(AncientCreatureAction.ATTACK, 14);
                for (LivingEntity target : around(level, creature, rider, size + 3.0, 2.5)) {
                    Vec3 to = target.position().subtract(creature.position()).multiply(1, 0, 1);
                    if (to.normalize().dot(look) < 0.3) {
                        hit(level, creature, target, damage * 1.2F, 1.8F * power);
                    }
                }
                level.sendParticles(ParticleTypes.SWEEP_ATTACK, creature.getX() - look.x * size, creature.getY() + 1.0, creature.getZ() - look.z * size, 3, size * 0.5, 0.3, size * 0.5, 0);
                play(level, creature, null, SoundEvents.PLAYER_ATTACK_SWEEP, 1.3F);
            }
            case STOMP -> {
                creature.setAction(AncientCreatureAction.ATTACK, 14);
                for (LivingEntity target : around(level, creature, rider, size + 3.5, 2.0)) {
                    if (target.onGround()) {
                        hit(level, creature, target, damage, 0.8F * power);
                        target.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 80, 2), creature);
                    }
                }
                level.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, level.getBlockState(creature.blockPosition().below())),
                    creature.getX(), creature.getY() + 0.1, creature.getZ(), 60, size, 0.1, size, 0.15);
                play(level, creature, creature.speciesSounds().stepSound(), SoundEvents.GENERIC_EXPLODE.value(), 0.8F);
            }
            case POUNCE -> {
                creature.setAction(AncientCreatureAction.ATTACK, 14);
                creature.setDeltaMovement(look.scale(1.1 * power).add(0, 0.55, 0));
                creature.needsSync = true;
                creature.setPounceStrike(damage * 1.3F);
                play(level, creature, creature.speciesSounds().attackSound(), SoundEvents.FOX_AGGRO, 1.2F);
            }
            case DIVE -> {
                creature.setAction(AncientCreatureAction.ATTACK, 14);
                if (creature.speciesCategory().isFlying()) {
                    creature.setDeltaMovement(look.scale(1.3 * power).add(0, -0.9, 0));
                    creature.setPounceStrike(damage * 1.4F);
                } else {
                    creature.setDeltaMovement(creature.getDeltaMovement().add(Vec3.directionFromRotation(rider.getXRot(), creature.getYRot()).scale(1.6 * power)));
                    hitAhead(level, creature, rider, look, size + 2.0, size, damage, 1.0F);
                }
                creature.needsSync = true;
                play(level, creature, creature.speciesSounds().alertSound(), SoundEvents.PHANTOM_SWOOP, 1.3F);
            }
        }
    }

    /** Lands a pounce or dive: hits everything the creature touches on the way down. */
    public static void strikeOnContact(ServerLevel level, AncientCreatureEntity creature, float damage) {
        Player rider = creature.getFirstPassenger() instanceof Player player ? player : null;
        for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, creature.getBoundingBox().inflate(0.75),
            target -> valid(creature, rider, target))) {
            hit(level, creature, target, damage, 0.9F);
        }
    }

    private static void hitAhead(ServerLevel level, AncientCreatureEntity creature, Player rider, Vec3 look, double reach, double halfWidth, float damage, float knockback) {
        for (LivingEntity target : inFront(level, creature, rider, look, reach, halfWidth)) {
            hit(level, creature, target, damage, knockback);
        }
    }

    private static void hit(ServerLevel level, AncientCreatureEntity creature, LivingEntity target, float damage, float knockback) {
        if (target.hurtServer(level, level.damageSources().mobAttack(creature), damage)) {
            Vec3 away = target.position().subtract(creature.position()).multiply(1, 0, 1);
            if (away.lengthSqr() > 1.0E-4) {
                away = away.normalize();
                target.push(away.x * knockback * 0.5, 0.2 * knockback, away.z * knockback * 0.5);
                target.needsSync = true;
            }
        }
    }

    private static List<LivingEntity> inFront(ServerLevel level, AncientCreatureEntity creature, Player rider, Vec3 look, double reach, double halfWidth) {
        Vec3 centre = creature.position().add(look.scale(reach * 0.5));
        AABB box = new AABB(centre, centre).inflate(Math.max(halfWidth, reach * 0.5), creature.getBbHeight(), Math.max(halfWidth, reach * 0.5)).move(0, creature.getBbHeight() * 0.5, 0);
        return level.getEntitiesOfClass(LivingEntity.class, box, target -> valid(creature, rider, target)
            && target.position().subtract(creature.position()).multiply(1, 0, 1).normalize().dot(look) > 0.35);
    }

    private static List<LivingEntity> around(ServerLevel level, AncientCreatureEntity creature, Player rider, double radius, double height) {
        return level.getEntitiesOfClass(LivingEntity.class, creature.getBoundingBox().inflate(radius, height, radius), target -> valid(creature, rider, target));
    }

    private static boolean valid(AncientCreatureEntity creature, Player rider, LivingEntity target) {
        if (target == creature || target == rider || !target.isAlive() || target.isSpectator() || target.isPassengerOfSameVehicle(creature)) {
            return false;
        }
        if (target instanceof AncientCreatureEntity other && rider != null && other.isOwnedBy(rider)) {
            return false;
        }
        if (target instanceof Player player && rider != null && !rider.canHarmPlayer(player)) {
            return false;
        }
        return !(target instanceof net.minecraft.world.entity.OwnableEntity ownable && rider != null && ownable.getOwnerReference() != null
            && ownable.getOwnerReference().matches(rider));
    }

    private static void play(ServerLevel level, AncientCreatureEntity creature, SoundEvent own, SoundEvent fallback, float volume) {
        level.playSound(null, creature.getX(), creature.getY(), creature.getZ(), own != null ? own : fallback, creature.getSoundSource(), volume, 0.9F + creature.getRandom().nextFloat() * 0.2F);
    }
}
