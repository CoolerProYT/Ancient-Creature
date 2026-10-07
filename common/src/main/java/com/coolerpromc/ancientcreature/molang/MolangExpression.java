package com.coolerpromc.ancientcreature.molang;

/**
 * A compiled Molang expression.
 *
 * <p>Compile once with {@link Molang#compile}, evaluate as often as needed. Expressions whose value
 * cannot change — plain numbers, arithmetic on literals, pure math on literals — are folded to a
 * constant at compile time, so a numeric keyframe costs a field read.
 */
public final class MolangExpression {
    public static final MolangExpression ZERO = constant(0.0);
    public static final MolangExpression ONE = constant(1.0);

    private final MolangNode root;
    private final boolean complex;
    private final String source;
    private final boolean constant;
    private final double constantValue;

    MolangExpression(MolangNode root, boolean complex, String source) {
        this.root = root;
        this.complex = complex;
        this.source = source;
        this.constant = !complex && root.isConstant() && !root.mayBeText();
        this.constantValue = this.constant ? sanitize(root.number(new MolangContext())) : 0.0;
    }

    public static MolangExpression constant(double value) {
        return new MolangExpression(new MolangNode.Constant(value), false, trim(value));
    }

    private static String trim(double value) {
        return value == Math.rint(value) && !Double.isInfinite(value) ? Long.toString((long) value) : Double.toString(value);
    }

    public boolean isConstant() {
        return this.constant;
    }

    /** The folded value; only meaningful when {@link #isConstant()}. */
    public double constantValue() {
        return this.constantValue;
    }

    public String source() {
        return this.source;
    }

    /** Evaluates as a number. Non-finite results become 0 so a bad expression cannot corrupt a pose. */
    public double evaluate(MolangContext ctx) {
        if (this.constant) {
            return this.constantValue;
        }
        ctx.beginEvaluation();
        if (this.complex) {
            this.root.number(ctx);
            return ctx.returned ? sanitize(MolangValues.toNumber(ctx.returnValue)) : 0.0;
        }
        return sanitize(this.root.number(ctx));
    }

    /** Evaluates keeping string and resource-reference results, for render controllers and comparisons. */
    public Object evaluateObject(MolangContext ctx) {
        if (this.constant) {
            return this.constantValue;
        }
        ctx.beginEvaluation();
        if (this.complex) {
            this.root.number(ctx);
            return ctx.returned ? ctx.returnValue : 0.0;
        }
        Object value = this.root.object(ctx);
        return value instanceof Double d ? sanitize(d) : value;
    }

    public boolean test(MolangContext ctx) {
        if (this.constant) {
            return this.constantValue != 0.0;
        }
        return MolangValues.truthy(this.evaluateObject(ctx));
    }

    /** Runs the expression for its side effects (assignments), as in pre_animation or on_entry scripts. */
    public void execute(MolangContext ctx) {
        if (!this.constant) {
            ctx.beginEvaluation();
            this.root.number(ctx);
        }
    }

    private static double sanitize(double value) {
        return Double.isFinite(value) ? value : 0.0;
    }

    @Override
    public String toString() {
        return this.source;
    }
}
