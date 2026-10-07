package com.coolerpromc.ancientcreature.client.animation.bedrock;

import com.coolerpromc.ancientcreature.client.entity.state.AncientCreatureRenderState;
import com.coolerpromc.ancientcreature.entity.custom.AncientCreatureAction;
import com.coolerpromc.ancientcreature.entity.custom.AncientCreatureEntity;
import com.coolerpromc.ancientcreature.molang.MolangArgs;
import com.coolerpromc.ancientcreature.molang.MolangQueryHost;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

/**
 * Answers {@code query.*} for a creature while it is being animated.
 *
 * <p>Bedrock's own query names are supported with Bedrock's meaning — {@code modified_distance_moved}
 * and {@code modified_move_speed} drive walk cycles exactly like vanilla Bedrock animations expect,
 * {@code ground_speed} is blocks per second, {@code target_x_rotation} is the head's pitch, and so on —
 * so animations written for Bedrock entities work unchanged. Ancient Creature's own queries
 * ({@code action}, {@code hunger}, {@code is_hungry}, ...) and any registered through
 * {@link CreatureQueryRegistry} are available alongside.
 *
 * <p>Every value is read from the client-side entity at render time; nothing here can change gameplay.
 */
public final class CreatureQueryHost implements MolangQueryHost, AnimationPlayer.AnimClock {
    private AncientCreatureEntity entity;
    private AncientCreatureRenderState state;
    private float partialTick;
    private float animTime;
    private float deltaTime;
    private CreatureAnimator animator;

    public void bind(AncientCreatureEntity entity, AncientCreatureRenderState state, float partialTick, CreatureAnimator animator) {
        this.entity = entity;
        this.state = state;
        this.partialTick = partialTick;
        this.animator = animator;
    }

    /** Drops the entity reference so a cached animator does not keep a removed entity alive. */
    public void unbind() {
        this.entity = null;
        this.state = null;
        this.animator = null;
    }

    void animTime(float animTime) {
        this.animTime = animTime;
    }

    void deltaTime(float deltaTime) {
        this.deltaTime = deltaTime;
    }

    @Override
    public void set(float animTime, float deltaTime) {
        this.animTime = animTime;
        this.deltaTime = deltaTime;
    }

    private static double flag(boolean value) {
        return value ? 1.0 : 0.0;
    }

    @Override
    public double queryNumber(String name, MolangArgs args) {
        AncientCreatureEntity e = this.entity;
        AncientCreatureRenderState s = this.state;
        if (e == null || s == null) {
            return Double.NaN;
        }
        return switch (name) {
            // time
            case "anim_time" -> this.animTime;
            case "delta_time" -> this.deltaTime;
            case "life_time" -> s.ageInTicks / 20.0;
            case "frame_alpha" -> this.partialTick;
            case "time_of_day" -> Math.floorMod(e.level().getOverworldClockTime() - 18000L, 24000L) / 24000.0;
            case "moon_phase" -> Math.floorMod(e.level().getOverworldClockTime() / 24000L, 8L);
            case "time_stamp" -> e.level().getGameTime();

            // movement
            case "modified_distance_moved", "limb_swing" -> s.walkAnimationPos;
            case "modified_move_speed", "limb_swing_amount" -> s.walkAnimationSpeed;
            case "ground_speed" -> {
                Vec3 v = e.getDeltaMovement();
                yield Math.sqrt(v.x * v.x + v.z * v.z) * 20.0;
            }
            case "vertical_speed" -> e.getDeltaMovement().y * 20.0;
            case "walk_distance" -> e.moveDist;
            case "yaw_speed" -> (e.getYRot() - e.yRotO) * 20.0;
            case "is_moving" -> flag(s.walkAnimationSpeed > 0.01F || e.getDeltaMovement().horizontalDistanceSqr() > 1.0E-6);
            case "is_on_ground" -> flag(e.onGround());
            case "is_in_water" -> flag(e.isInWater());
            case "is_in_water_or_rain" -> flag(e.isInWaterOrRain());
            case "is_in_lava" -> flag(e.isInLava());
            case "is_swimming" -> flag(e.isInWater() && !e.onGround());
            case "is_sprinting", "is_running" -> flag(e.isSprinting());
            case "is_jumping" -> flag(!e.onGround() && !e.isInWater() && e.getDeltaMovement().y > 0.05);
            case "is_gliding", "is_flying" -> flag(e.speciesCategory().isFlying() && !e.onGround());

            // body and head
            case "body_y_rotation" -> s.bodyRot;
            case "head_y_rotation", "target_y_rotation", "head_yaw" -> s.yRot;
            case "head_x_rotation", "target_x_rotation", "head_pitch" -> s.xRot;
            case "scale" -> s.ageScale;
            case "position" -> switch ((int) args.number(0)) {
                case 0 -> Mth.lerp(this.partialTick, e.xo, e.getX());
                case 1 -> Mth.lerp(this.partialTick, e.yo, e.getY());
                default -> Mth.lerp(this.partialTick, e.zo, e.getZ());
            };
            case "position_delta" -> switch ((int) args.number(0)) {
                case 0 -> e.getX() - e.xo;
                case 1 -> e.getY() - e.yo;
                default -> e.getZ() - e.zo;
            };
            case "distance_from_camera" -> {
                var camera = Minecraft.getInstance().gameRenderer.getMainCamera();
                yield camera == null ? 0.0 : camera.position().distanceTo(e.getPosition(this.partialTick));
            }

            // vitals
            case "health" -> e.getHealth();
            case "max_health" -> e.getMaxHealth();
            case "is_alive" -> flag(e.isAlive());
            case "is_dead" -> flag(s.deathTime > 0.0F || !e.isAlive());
            case "hurt_time" -> e.hurtTime;
            case "is_hurt" -> flag(s.hurtTimeRemaining > 0);
            case "is_on_fire", "is_onfire" -> flag(e.isOnFire());
            case "is_baby" -> flag(e.isBaby());
            case "is_invisible" -> flag(e.isInvisible());
            case "is_leashed" -> flag(e.isLeashed());

            // behaviour
            case "has_target", "is_attacking", "is_angry" -> flag(e.isAggressive());
            case "is_eating", "is_grazing" -> flag(s.action == AncientCreatureAction.EAT || s.action == AncientCreatureAction.GRAZE);
            case "is_roaring" -> flag(s.action == AncientCreatureAction.ROAR);
            case "is_riding" -> flag(e.isPassenger());
            case "has_rider" -> flag(e.isVehicle());
            case "is_tamed" -> flag(s.hasOwner);
            case "is_saddled" -> flag(s.isSaddled);
            case "armor_tier" -> s.armorTier;
            case "is_sitting" -> flag(s.isSitting);
            case "hunger" -> s.hunger;
            case "comfort" -> e.getComfort();
            case "is_distressed" -> flag(e.isDistressed());
            case "is_hungry" -> flag(s.isHungry);
            case "variant" -> s.variantIndex;
            case "mark_variant", "skin_id" -> s.variantIndex;

            // animation state
            case "all_animations_finished" -> flag(this.animator != null && this.animator.allAnimationsFinished());
            case "any_animation_finished" -> flag(this.animator != null && this.animator.anyAnimationFinished());
            case "is_first_person" -> 0.0;
            default -> CreatureQueryRegistry.number(name, s, this.animTime);
        };
    }

    @Override
    public Object queryObject(String name, MolangArgs args) {
        if (this.state == null) {
            return Double.NaN;
        }
        return switch (name) {
            case "action" -> this.state.action.queryName();
            case "variant_name" -> this.state.variant;
            case "species" -> this.state.species.id().toString();
            default -> {
                String text = CreatureQueryRegistry.string(name, this.state, this.animTime);
                yield text != null ? text : (Object) this.queryNumber(name, args);
            }
        };
    }
}
