package com.coolerpromc.ancientcreature.client.animation.bedrock;

import com.coolerpromc.ancientcreature.client.entity.state.AncientCreatureRenderState;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Extra {@code query.*} values that animation controllers can read.
 *
 * <p>Built-in queries live in {@link AnimationQueryContext}; this holds ones added by other mods. A
 * query only ever <em>reads</em> the render state, so a controller still cannot change gameplay.
 *
 * <p>Client only. Register during client initialization, before any resource pack loads.
 */
public final class CreatureQueryRegistry {
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
        if (AnimationQueryContext.isBuiltIn(name)) {
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
