package com.coolerpromc.ancientcreature.molang;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * The {@code math.*} library. Angles are in degrees, as in Bedrock.
 *
 * <p>Includes the standard Bedrock functions plus the {@code math.ease_*} easing family Blockbench
 * understands, all with the {@code (start, end, t)} signature.
 */
final class MolangMath {
    interface Function {
        double apply(MolangContext ctx, MolangNode[] args);

        /** Pure functions of constant arguments are folded at compile time. */
        default boolean pure() {
            return true;
        }

        default int minArgs() {
            return 0;
        }
    }

    private record Fixed(int arity, Impl impl) implements Function {
        @Override
        public double apply(MolangContext ctx, MolangNode[] args) {
            double a = args.length > 0 ? args[0].number(ctx) : 0.0;
            double b = args.length > 1 ? args[1].number(ctx) : 0.0;
            double c = args.length > 2 ? args[2].number(ctx) : 0.0;
            return this.impl.apply(a, b, c);
        }

        @Override
        public int minArgs() {
            return this.arity;
        }
    }

    private record Random(int arity, RandomImpl impl) implements Function {
        @Override
        public double apply(MolangContext ctx, MolangNode[] args) {
            double a = args.length > 0 ? args[0].number(ctx) : 0.0;
            double b = args.length > 1 ? args[1].number(ctx) : 0.0;
            double c = args.length > 2 ? args[2].number(ctx) : 0.0;
            return this.impl.apply(ctx, a, b, c);
        }

        @Override
        public boolean pure() {
            return false;
        }

        @Override
        public int minArgs() {
            return this.arity;
        }
    }

    @FunctionalInterface
    private interface Impl {
        double apply(double a, double b, double c);
    }

    @FunctionalInterface
    private interface RandomImpl {
        double apply(MolangContext ctx, double a, double b, double c);
    }

    private static final Map<String, Function> FUNCTIONS = new HashMap<>();
    /** Names that may be written without parentheses. */
    static final Set<String> CONSTANTS = Set.of("pi");

    private static void fn(String name, int arity, Impl impl) {
        FUNCTIONS.put(name, new Fixed(arity, impl));
    }

    private static void rnd(String name, int arity, RandomImpl impl) {
        FUNCTIONS.put(name, new Random(arity, impl));
    }

    static {
        fn("abs", 1, (a, b, c) -> Math.abs(a));
        fn("acos", 1, (a, b, c) -> Math.toDegrees(Math.acos(a)));
        fn("asin", 1, (a, b, c) -> Math.toDegrees(Math.asin(a)));
        fn("atan", 1, (a, b, c) -> Math.toDegrees(Math.atan(a)));
        fn("atan2", 2, (a, b, c) -> Math.toDegrees(Math.atan2(a, b)));
        fn("ceil", 1, (a, b, c) -> Math.ceil(a));
        fn("clamp", 3, (a, b, c) -> Math.max(b, Math.min(c, a)));
        fn("cos", 1, (a, b, c) -> Math.cos(Math.toRadians(a)));
        fn("sin", 1, (a, b, c) -> Math.sin(Math.toRadians(a)));
        fn("exp", 1, (a, b, c) -> Math.exp(a));
        fn("floor", 1, (a, b, c) -> Math.floor(a));
        fn("hermite_blend", 1, (a, b, c) -> 3 * a * a - 2 * a * a * a);
        fn("lerp", 3, (a, b, c) -> a + (b - a) * c);
        fn("lerprotate", 3, (a, b, c) -> a + wrapDegrees(b - a) * c);
        fn("inverse_lerp", 3, (a, b, c) -> a == b ? 0.0 : (c - a) / (b - a));
        fn("ln", 1, (a, b, c) -> a <= 0 ? 0.0 : Math.log(a));
        fn("max", 2, (a, b, c) -> Math.max(a, b));
        fn("min", 2, (a, b, c) -> Math.min(a, b));
        fn("min_angle", 1, (a, b, c) -> wrapDegrees(a));
        fn("mod", 2, (a, b, c) -> b == 0 ? 0.0 : a % b);
        fn("pi", 0, (a, b, c) -> Math.PI);
        fn("pow", 2, (a, b, c) -> Math.pow(a, b));
        fn("round", 1, (a, b, c) -> Math.round(a));
        fn("sqrt", 1, (a, b, c) -> a < 0 ? 0.0 : Math.sqrt(a));
        fn("trunc", 1, (a, b, c) -> a < 0 ? Math.ceil(a) : Math.floor(a));
        fn("sign", 1, (a, b, c) -> a >= 0 ? 1.0 : -1.0);
        fn("copy_sign", 2, (a, b, c) -> Math.copySign(a, b));

        rnd("random", 2, (ctx, a, b, c) -> a + ctx.random() * (b - a));
        rnd("random_integer", 2, (ctx, a, b, c) -> {
            long lo = Math.round(Math.min(a, b));
            long hi = Math.round(Math.max(a, b));
            return lo + (long) Math.floor(ctx.random() * (hi - lo + 1));
        });
        rnd("die_roll", 3, (ctx, a, b, c) -> {
            int n = (int) Math.min(1024, Math.max(0, a));
            double sum = 0;
            for (int i = 0; i < n; i++) {
                sum += b + ctx.random() * (c - b);
            }
            return sum;
        });
        rnd("die_roll_integer", 3, (ctx, a, b, c) -> {
            int n = (int) Math.min(1024, Math.max(0, a));
            long lo = Math.round(Math.min(b, c));
            long hi = Math.round(Math.max(b, c));
            double sum = 0;
            for (int i = 0; i < n; i++) {
                sum += lo + (long) Math.floor(ctx.random() * (hi - lo + 1));
            }
            return sum;
        });

        easing("quad", t -> t * t);
        easing("cubic", t -> t * t * t);
        easing("quart", t -> t * t * t * t);
        easing("quint", t -> t * t * t * t * t);
        easing("sine", t -> 1 - Math.cos(t * Math.PI / 2));
        easing("expo", t -> t == 0 ? 0 : Math.pow(2, 10 * t - 10));
        easing("circ", t -> 1 - Math.sqrt(1 - t * t));
        easing("back", t -> 2.70158 * t * t * t - 1.70158 * t * t);
        easing("elastic", t -> t == 0 || t == 1 ? t : -Math.pow(2, 10 * t - 10) * Math.sin((t * 10 - 10.75) * (2 * Math.PI / 3)));
        easing("bounce", t -> 1 - bounceOut(1 - t));
    }

    private interface Curve {
        double in(double t);
    }

    private static void easing(String name, Curve in) {
        fn("ease_in_" + name, 3, (a, b, c) -> a + (b - a) * in.in(clamp01(c)));
        fn("ease_out_" + name, 3, (a, b, c) -> a + (b - a) * (1 - in.in(1 - clamp01(c))));
        fn("ease_in_out_" + name, 3, (a, b, c) -> {
            double t = clamp01(c);
            double eased = t < 0.5 ? in.in(t * 2) / 2 : 1 - in.in((1 - t) * 2) / 2;
            return a + (b - a) * eased;
        });
    }

    private static double bounceOut(double t) {
        double n = 7.5625;
        double d = 2.75;
        if (t < 1 / d) {
            return n * t * t;
        } else if (t < 2 / d) {
            t -= 1.5 / d;
            return n * t * t + 0.75;
        } else if (t < 2.5 / d) {
            t -= 2.25 / d;
            return n * t * t + 0.9375;
        }
        t -= 2.625 / d;
        return n * t * t + 0.984375;
    }

    private static double clamp01(double t) {
        return t < 0 ? 0 : (t > 1 ? 1 : t);
    }

    static double wrapDegrees(double degrees) {
        double wrapped = degrees % 360.0;
        if (wrapped >= 180.0) {
            wrapped -= 360.0;
        }
        if (wrapped < -180.0) {
            wrapped += 360.0;
        }
        return wrapped;
    }

    static Function get(String name) {
        return FUNCTIONS.get(name);
    }

    static Set<String> names() {
        return FUNCTIONS.keySet();
    }

    private MolangMath() {
    }
}
