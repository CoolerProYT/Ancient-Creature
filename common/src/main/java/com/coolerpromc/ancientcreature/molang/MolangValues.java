package com.coolerpromc.ancientcreature.molang;

import java.util.Objects;

/** Conversions between the two Molang value kinds, numbers and strings. */
final class MolangValues {
    static double toNumber(Object value) {
        if (value instanceof Number n) {
            return n.doubleValue();
        }
        if (value instanceof Boolean b) {
            return b ? 1.0 : 0.0;
        }
        return 0.0;
    }

    static boolean truthy(Object value) {
        if (value instanceof Number n) {
            return n.doubleValue() != 0.0;
        }
        if (value instanceof String s) {
            return !s.isEmpty();
        }
        return value != null;
    }

    static String toText(Object value) {
        if (value instanceof Double d) {
            double v = d;
            if (v == Math.rint(v) && !Double.isInfinite(v)) {
                return Long.toString((long) v);
            }
            return Double.toString(v);
        }
        return String.valueOf(value);
    }

    /**
     * Molang equality. Two numbers compare numerically; anything involving a string compares as text,
     * case-insensitively, because Molang identifiers and enum-like strings are case-insensitive.
     */
    static boolean equal(Object a, Object b) {
        if (a instanceof Number x && b instanceof Number y) {
            return x.doubleValue() == y.doubleValue();
        }
        if (a instanceof String || b instanceof String) {
            return toText(a).equalsIgnoreCase(toText(b));
        }
        return Objects.equals(a, b);
    }

    private MolangValues() {
    }
}
