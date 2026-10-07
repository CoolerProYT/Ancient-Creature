package com.coolerpromc.ancientcreature.molang;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A bag of Molang variables ({@code variable.*}, {@code temp.*} or {@code context.*}).
 *
 * <p>Values are numbers or strings. Reading an unset name yields zero, as in Bedrock; {@link #has}
 * distinguishes unset from zero for the {@code ??} operator.
 */
public final class MolangVariables {
    private final Map<String, Object> values = new HashMap<>();

    public boolean has(String name) {
        return this.values.containsKey(name);
    }

    public double number(String name) {
        Object value = this.values.get(name);
        return value instanceof Number n ? n.doubleValue() : 0.0;
    }

    public Object value(String name) {
        Object value = this.values.get(name);
        return value == null ? 0.0 : value;
    }

    public void set(String name, double value) {
        this.values.put(name, value);
    }

    public void set(String name, Object value) {
        this.values.put(name, value instanceof Number n ? n.doubleValue() : value);
    }

    public void remove(String name) {
        this.values.remove(name);
    }

    public void clear() {
        if (!this.values.isEmpty()) {
            this.values.clear();
        }
    }

    public Set<String> names() {
        return this.values.keySet();
    }

    public boolean isEmpty() {
        return this.values.isEmpty();
    }
}
