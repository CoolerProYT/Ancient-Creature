package com.coolerpromc.ancientcreature.client.animation.bedrock;

import com.coolerpromc.ancientcreature.client.entity.state.AncientCreatureRenderState;

import java.util.Set;

/**
 * The values an animation controller expression may read.
 *
 * <p>This is the whole vocabulary — deliberately a closed, documented set rather than general Molang.
 * Everything here is derived from the render state, which the client extracts from synchronised entity
 * data, so a controller can never read or change server state.
 *
 * <table>
 *   <caption>Supported queries</caption>
 *   <tr><td>{@code query.anim_time}</td><td>seconds since the current state was entered</td></tr>
 *   <tr><td>{@code query.life_time}</td><td>seconds since the entity spawned</td></tr>
 *   <tr><td>{@code query.head_yaw} / {@code query.head_pitch}</td><td>degrees</td></tr>
 *   <tr><td>{@code query.limb_swing} / {@code query.limb_swing_amount}</td><td>walk animation position and speed</td></tr>
 *   <tr><td>{@code query.ground_speed}</td><td>alias of {@code limb_swing_amount}</td></tr>
 *   <tr><td>{@code query.health} / {@code query.max_health}</td><td>hit points</td></tr>
 *   <tr><td>{@code query.is_moving} / {@code query.is_running}</td><td>0 or 1</td></tr>
 *   <tr><td>{@code query.is_attacking} / {@code query.is_baby} / {@code query.is_in_water}</td><td>0 or 1</td></tr>
 *   <tr><td>{@code query.is_hurt} / {@code query.is_dead}</td><td>0 or 1</td></tr>
 *   <tr><td>{@code query.action}</td><td>current action name: {@code none}, {@code attack}, {@code roar}, {@code graze}, {@code eat}</td></tr>
 * </table>
 */
public interface AnimationQueryContext {
    double number(String name);
    String string(String name);

    Set<String> BUILT_IN = Set.of(
        "anim_time", "life_time", "head_yaw", "head_pitch", "limb_swing", "limb_swing_amount",
        "ground_speed", "health", "max_health", "is_moving", "is_running", "is_attacking",
        "is_baby", "is_in_water", "is_hurt", "is_dead", "action");

    static boolean isBuiltIn(String name) {
        return BUILT_IN.contains(name);
    }

    record OfRenderState(AncientCreatureRenderState state, float stateSeconds) implements AnimationQueryContext {
        @Override
        public double number(String name) {
            return switch (name) {
                case "anim_time" -> this.stateSeconds;
                case "life_time" -> this.state.ageInTicks / 20.0F;
                case "head_yaw" -> this.state.yRot;
                case "head_pitch" -> this.state.xRot;
                case "limb_swing" -> this.state.walkAnimationPos;
                case "limb_swing_amount", "ground_speed" -> this.state.walkAnimationSpeed;
                case "health" -> this.state.creatureHealth;
                case "max_health" -> this.state.creatureMaxHealth;
                case "is_moving" -> this.state.walkAnimationSpeed > 0.01F ? 1.0 : 0.0;
                case "is_running" -> this.state.isSprinting ? 1.0 : 0.0;
                case "is_attacking" -> this.state.isAggressive ? 1.0 : 0.0;
                case "is_baby" -> this.state.isBaby ? 1.0 : 0.0;
                case "is_in_water" -> this.state.isInWater ? 1.0 : 0.0;
                case "is_hurt" -> this.state.hurtTimeRemaining > 0 ? 1.0 : 0.0;
                case "is_dead" -> this.state.deathTime > 0.0F ? 1.0 : 0.0;
                default -> CreatureQueryRegistry.number(name, this.state, this.stateSeconds);
            };
        }

        @Override
        public String string(String name) {
            if ("action".equals(name)) {
                return this.state.action.queryName();
            }
            return CreatureQueryRegistry.string(name, this.state, this.stateSeconds);
        }
    }
}
