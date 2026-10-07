package com.coolerpromc.ancientcreature.client.animation.bedrock;

import com.coolerpromc.ancientcreature.client.entity.state.AncientCreatureRenderState;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Extra {@code query.*} values that animation controllers can read.
 *
 * <p>Built-in queries live in {@link CreatureQueryHost}; this holds ones added by other mods. A
 * query only ever <em>reads</em> the render state, so a controller still cannot change gameplay.
 *
 * <p>Client only. Register during client initialization, before any resource pack loads.
 */
public final class CreatureQueryRegistry {
    /** Names {@link CreatureQueryHost} answers itself; registered queries may not shadow them. */
    static final java.util.Set<String> BUILT_IN = java.util.Set.of(
        "anim_time", "delta_time", "life_time", "frame_alpha", "time_of_day", "moon_phase", "time_stamp",
        "modified_distance_moved", "limb_swing", "modified_move_speed", "limb_swing_amount", "ground_speed",
        "vertical_speed", "walk_distance", "yaw_speed", "is_moving", "is_on_ground", "is_in_water",
        "is_in_water_or_rain", "is_in_lava", "is_swimming", "is_sprinting", "is_running", "is_jumping",
        "is_gliding", "is_flying", "body_y_rotation", "head_y_rotation", "target_y_rotation", "head_yaw",
        "head_x_rotation", "target_x_rotation", "head_pitch", "scale", "position", "position_delta",
        "distance_from_camera", "health", "max_health", "is_alive", "is_dead", "hurt_time", "is_hurt",
        "is_on_fire", "is_onfire", "is_baby", "is_invisible", "is_leashed", "has_target", "is_attacking",
        "is_angry", "is_eating", "is_grazing", "is_roaring", "is_riding", "has_rider", "is_tamed",
        "is_saddled", "armor_tier", "is_sitting", "hunger", "comfort", "is_distressed", "is_hungry", "variant", "mark_variant", "skin_id",
        "all_animations_finished", "any_animation_finished", "is_first_person", "action", "variant_name", "species");

    private static final Map<String, NumberQuery> NUMBERS = new LinkedHashMap<>();
    private static final Map<String, StringQuery> STRINGS = new LinkedHashMap<>();

    /** A numeric {@code query.<name>}. Non-zero counts as true in a condition. */
    @FunctionalInterface
    public interface NumberQuery {
        double evaluate(AncientCreatureRenderState state, float secondsInState);
    }

    /** A string {@code query.<name>}, comparable with {@code ==} and {@code !=}. */
    @FunctionalInterface
    public interface StringQuery {
        String evaluate(AncientCreatureRenderState state, float secondsInState);
    }

    /**
     * Adds a numeric query.
     *
     * @param name bare name without the {@code query.} prefix, e.g. {@code "wing_beat"}
     * @throws IllegalArgumentException if the name is taken or shadows a built-in query
     */
    public static void registerNumber(String name, NumberQuery query) {
        checkAvailable(name);
        NUMBERS.put(name, query);
    }

    /**
     * Adds a string query.
     *
     * @throws IllegalArgumentException if the name is taken or shadows a built-in query
     */
    public static void registerString(String name, StringQuery query) {
        checkAvailable(name);
        STRINGS.put(name, query);
    }

    private static void checkAvailable(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Query name must not be blank");
        }
        if (NUMBERS.containsKey(name) || STRINGS.containsKey(name)) {
            throw new IllegalArgumentException("Duplicate animation query '" + name + "'");
        }
        if (BUILT_IN.contains(name)) {
            throw new IllegalArgumentException("Animation query '" + name + "' shadows a built-in query; pick another name");
        }
    }

    static double number(String name, AncientCreatureRenderState state, float secondsInState) {
        NumberQuery query = NUMBERS.get(name);
        return query == null ? Double.NaN : query.evaluate(state, secondsInState);
    }

    static String string(String name, AncientCreatureRenderState state, float secondsInState) {
        StringQuery query = STRINGS.get(name);
        return query == null ? null : query.evaluate(state, secondsInState);
    }

    public static Collection<String> numberQueryNames() {
        return java.util.Collections.unmodifiableCollection(NUMBERS.keySet());
    }

    public static Collection<String> stringQueryNames() {
        return java.util.Collections.unmodifiableCollection(STRINGS.keySet());
    }

    private CreatureQueryRegistry() {
    }
}
