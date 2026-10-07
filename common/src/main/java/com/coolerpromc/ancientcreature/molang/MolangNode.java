package com.coolerpromc.ancientcreature.molang;

import com.coolerpromc.ancientcreature.molang.MolangResources.ResourceRef;

import java.util.function.DoubleBinaryOperator;

/**
 * A compiled Molang syntax tree node.
 *
 * <p>Evaluation is numeric by default; {@link #object} is only used where a string may legitimately
 * appear (equality, assignment, query arguments, render-controller resource lookups), so the common
 * keyframe path never boxes a value.
 */
abstract class MolangNode {
    abstract double number(MolangContext ctx);

    Object object(MolangContext ctx) {
        return this.number(ctx);
    }

    boolean isConstant() {
        return false;
    }

    /** Statically known to possibly produce a non-number. */
    boolean mayBeText() {
        return false;
    }

    // ------------------------------------------------------------------ literals

    static final class Constant extends MolangNode {
        final double value;

        Constant(double value) {
            this.value = value;
        }

        @Override
        double number(MolangContext ctx) {
            return this.value;
        }

        @Override
        boolean isConstant() {
            return true;
        }
    }

    static final class Text extends MolangNode {
        final String value;

        Text(String value) {
            this.value = value;
        }

        @Override
        double number(MolangContext ctx) {
            return 0.0;
        }

        @Override
        Object object(MolangContext ctx) {
            return this.value;
        }

        @Override
        boolean isConstant() {
            return true;
        }

        @Override
        boolean mayBeText() {
            return true;
        }
    }

    static final class Resource extends MolangNode {
        final ResourceRef ref;

        Resource(String category, String name) {
            this.ref = new ResourceRef(category, name);
        }

        @Override
        double number(MolangContext ctx) {
            return 0.0;
        }

        @Override
        Object object(MolangContext ctx) {
            return this.ref;
        }

        @Override
        boolean mayBeText() {
            return true;
        }
    }

    static final class This extends MolangNode {
        @Override
        double number(MolangContext ctx) {
            return ctx.thisValue;
        }
    }

    // ------------------------------------------------------------------ data access

    static final class Query extends MolangNode {
        final String name;
        final MolangNode[] args;

        Query(String name, MolangNode[] args) {
            this.name = name;
            this.args = args;
        }

        private MolangArgs args(MolangContext ctx) {
            return this.args.length == 0 ? MolangArgs.NONE : new MolangArgs(this.args, ctx);
        }

        @Override
        double number(MolangContext ctx) {
            return ctx.queryNumber(this.name, this.args(ctx));
        }

        @Override
        Object object(MolangContext ctx) {
            return ctx.queryObject(this.name, this.args(ctx));
        }

        @Override
        boolean mayBeText() {
            return true;
        }
    }

    enum Store {
        VARIABLE, TEMP, CONTEXT;

        MolangVariables of(MolangContext ctx) {
            return switch (this) {
                case VARIABLE -> ctx.variables;
                case TEMP -> ctx.temp;
                case CONTEXT -> ctx.context;
            };
        }
    }

    static final class Variable extends MolangNode {
        final Store store;
        final String name;

        Variable(Store store, String name) {
            this.store = store;
            this.name = name;
        }

        @Override
        double number(MolangContext ctx) {
            return this.store.of(ctx).number(this.name);
        }

        @Override
        Object object(MolangContext ctx) {
            return this.store.of(ctx).value(this.name);
        }

        @Override
        boolean mayBeText() {
            return true;
        }

        boolean isSet(MolangContext ctx) {
            return this.store.of(ctx).has(this.name);
        }
    }

    static final class Assign extends MolangNode {
        final Store store;
        final String name;
        final MolangNode value;

        Assign(Store store, String name, MolangNode value) {
            this.store = store;
            this.name = name;
            this.value = value;
        }

        @Override
        double number(MolangContext ctx) {
            if (this.value.mayBeText()) {
                Object v = this.value.object(ctx);
                this.store.of(ctx).set(this.name, v);
                return MolangValues.toNumber(v);
            }
            double v = this.value.number(ctx);
            this.store.of(ctx).set(this.name, v);
            return v;
        }
    }

    static final class ArrayAccess extends MolangNode {
        final String array;
        final MolangNode index;

        ArrayAccess(String array, MolangNode index) {
            this.array = array;
            this.index = index;
        }

        @Override
        double number(MolangContext ctx) {
            return MolangValues.toNumber(this.object(ctx));
        }

        @Override
        Object object(MolangContext ctx) {
            Object[] members = ctx.resources().array(this.array);
            if (members == null || members.length == 0) {
                return 0.0;
            }
            // Bedrock floors the index and wraps it into range, so q.variant beyond the list cycles.
            int i = (int) Math.floor(this.index.number(ctx));
            i = Math.floorMod(i, members.length);
            return members[i];
        }

        @Override
        boolean mayBeText() {
            return true;
        }
    }

    // ------------------------------------------------------------------ operators

    static final class Negate extends MolangNode {
        final MolangNode operand;

        Negate(MolangNode operand) {
            this.operand = operand;
        }

        @Override
        double number(MolangContext ctx) {
            return -this.operand.number(ctx);
        }

        @Override
        boolean isConstant() {
            return this.operand.isConstant();
        }
    }

    static final class Not extends MolangNode {
        final MolangNode operand;

        Not(MolangNode operand) {
            this.operand = operand;
        }

        @Override
        double number(MolangContext ctx) {
            return truthy(this.operand, ctx) ? 0.0 : 1.0;
        }

        @Override
        boolean isConstant() {
            return this.operand.isConstant();
        }
    }

    static final class Arithmetic extends MolangNode {
        final MolangNode left;
        final MolangNode right;
        final DoubleBinaryOperator op;

        Arithmetic(MolangNode left, MolangNode right, DoubleBinaryOperator op) {
            this.left = left;
            this.right = right;
            this.op = op;
        }

        @Override
        double number(MolangContext ctx) {
            return this.op.applyAsDouble(this.left.number(ctx), this.right.number(ctx));
        }

        @Override
        boolean isConstant() {
            return this.left.isConstant() && this.right.isConstant();
        }
    }

    static final class Equality extends MolangNode {
        final MolangNode left;
        final MolangNode right;
        final boolean negate;

        Equality(MolangNode left, MolangNode right, boolean negate) {
            this.left = left;
            this.right = right;
            this.negate = negate;
        }

        @Override
        double number(MolangContext ctx) {
            boolean equal;
            if (this.left.mayBeText() || this.right.mayBeText()) {
                equal = MolangValues.equal(this.left.object(ctx), this.right.object(ctx));
            } else {
                equal = this.left.number(ctx) == this.right.number(ctx);
            }
            return equal != this.negate ? 1.0 : 0.0;
        }

        @Override
        boolean isConstant() {
            return this.left.isConstant() && this.right.isConstant();
        }
    }

    static final class And extends MolangNode {
        final MolangNode left;
        final MolangNode right;

        And(MolangNode left, MolangNode right) {
            this.left = left;
            this.right = right;
        }

        @Override
        double number(MolangContext ctx) {
            return truthy(this.left, ctx) && truthy(this.right, ctx) ? 1.0 : 0.0;
        }

        @Override
        boolean isConstant() {
            return this.left.isConstant() && this.right.isConstant();
        }
    }

    static final class Or extends MolangNode {
        final MolangNode left;
        final MolangNode right;

        Or(MolangNode left, MolangNode right) {
            this.left = left;
            this.right = right;
        }

        @Override
        double number(MolangContext ctx) {
            return truthy(this.left, ctx) || truthy(this.right, ctx) ? 1.0 : 0.0;
        }

        @Override
        boolean isConstant() {
            return this.left.isConstant() && this.right.isConstant();
        }
    }

    /** {@code a ? b : c}, or the binary form {@code a ? b}, which yields 0 when a is false. */
    static final class Ternary extends MolangNode {
        final MolangNode condition;
        final MolangNode whenTrue;
        final MolangNode whenFalse;

        Ternary(MolangNode condition, MolangNode whenTrue, MolangNode whenFalse) {
            this.condition = condition;
            this.whenTrue = whenTrue;
            this.whenFalse = whenFalse;
        }

        @Override
        double number(MolangContext ctx) {
            return truthy(this.condition, ctx) ? this.whenTrue.number(ctx) : this.whenFalse.number(ctx);
        }

        @Override
        Object object(MolangContext ctx) {
            return truthy(this.condition, ctx) ? this.whenTrue.object(ctx) : this.whenFalse.object(ctx);
        }

        @Override
        boolean isConstant() {
            return this.condition.isConstant() && this.whenTrue.isConstant() && this.whenFalse.isConstant();
        }

        @Override
        boolean mayBeText() {
            return this.whenTrue.mayBeText() || this.whenFalse.mayBeText();
        }
    }

    /** {@code a ?? b}: b when a is an unset variable, otherwise a. */
    static final class Coalesce extends MolangNode {
        final MolangNode left;
        final MolangNode right;

        Coalesce(MolangNode left, MolangNode right) {
            this.left = left;
            this.right = right;
        }

        private boolean useRight(MolangContext ctx) {
            return this.left instanceof Variable variable && !variable.isSet(ctx);
        }

        @Override
        double number(MolangContext ctx) {
            return this.useRight(ctx) ? this.right.number(ctx) : this.left.number(ctx);
        }

        @Override
        Object object(MolangContext ctx) {
            return this.useRight(ctx) ? this.right.object(ctx) : this.left.object(ctx);
        }

        @Override
        boolean mayBeText() {
            return this.left.mayBeText() || this.right.mayBeText();
        }
    }

    static final class MathCall extends MolangNode {
        final MolangMath.Function function;
        final MolangNode[] args;

        MathCall(MolangMath.Function function, MolangNode[] args) {
            this.function = function;
            this.args = args;
        }

        @Override
        double number(MolangContext ctx) {
            return this.function.apply(ctx, this.args);
        }

        @Override
        boolean isConstant() {
            if (!this.function.pure()) {
                return false;
            }
            for (MolangNode arg : this.args) {
                if (!arg.isConstant()) {
                    return false;
                }
            }
            return true;
        }
    }

    /** {@code a->b}: entity references are not available outside Bedrock, so this is always 0. */
    static final class Unsupported extends MolangNode {
        @Override
        double number(MolangContext ctx) {
            return 0.0;
        }
    }

    // ------------------------------------------------------------------ statements

    static final class Block extends MolangNode {
        final MolangNode[] statements;

        Block(MolangNode[] statements) {
            this.statements = statements;
        }

        @Override
        double number(MolangContext ctx) {
            for (MolangNode statement : this.statements) {
                statement.number(ctx);
                if (ctx.returned || ctx.breaking || ctx.continuing) {
                    break;
                }
            }
            return 0.0;
        }
    }

    static final class Return extends MolangNode {
        final MolangNode value;

        Return(MolangNode value) {
            this.value = value;
        }

        @Override
        double number(MolangContext ctx) {
            Object v = this.value.mayBeText() ? this.value.object(ctx) : (Object) this.value.number(ctx);
            ctx.returnValue = v;
            ctx.returned = true;
            return MolangValues.toNumber(v);
        }
    }

    static final class Break extends MolangNode {
        @Override
        double number(MolangContext ctx) {
            ctx.breaking = true;
            return 0.0;
        }
    }

    static final class Continue extends MolangNode {
        @Override
        double number(MolangContext ctx) {
            ctx.continuing = true;
            return 0.0;
        }
    }

    /** {@code loop(count, {...})}, capped at 1024 iterations like Bedrock. */
    static final class Loop extends MolangNode {
        static final int MAX_ITERATIONS = 1024;
        final MolangNode count;
        final MolangNode body;

        Loop(MolangNode count, MolangNode body) {
            this.count = count;
            this.body = body;
        }

        @Override
        double number(MolangContext ctx) {
            int n = (int) Math.min(MAX_ITERATIONS, Math.max(0, Math.round(this.count.number(ctx))));
            for (int i = 0; i < n; i++) {
                this.body.number(ctx);
                ctx.continuing = false;
                if (ctx.breaking) {
                    ctx.breaking = false;
                    break;
                }
                if (ctx.returned) {
                    break;
                }
            }
            return 0.0;
        }
    }

    /** {@code for_each(t.x, array.name, {...})} over a render-controller array. */
    static final class ForEach extends MolangNode {
        final Store store;
        final String name;
        final String array;
        final MolangNode body;

        ForEach(Store store, String name, String array, MolangNode body) {
            this.store = store;
            this.name = name;
            this.array = array;
            this.body = body;
        }

        @Override
        double number(MolangContext ctx) {
            Object[] members = ctx.resources().array(this.array);
            if (members == null) {
                return 0.0;
            }
            int limit = Math.min(members.length, Loop.MAX_ITERATIONS);
            for (int i = 0; i < limit; i++) {
                this.store.of(ctx).set(this.name, members[i]);
                this.body.number(ctx);
                ctx.continuing = false;
                if (ctx.breaking) {
                    ctx.breaking = false;
                    break;
                }
                if (ctx.returned) {
                    break;
                }
            }
            return 0.0;
        }
    }

    // ------------------------------------------------------------------ helpers

    static boolean truthy(MolangNode node, MolangContext ctx) {
        if (node.mayBeText()) {
            return MolangValues.truthy(node.object(ctx));
        }
        return node.number(ctx) != 0.0;
    }

    static double divide(double a, double b) {
        return b == 0.0 ? 0.0 : a / b;
    }
}
