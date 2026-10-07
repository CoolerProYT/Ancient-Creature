package com.coolerpromc.ancientcreature.molang;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MolangTest {
    private static final double EPS = 1.0E-9;

    private static double eval(String source) throws MolangException {
        return Molang.compile(source).evaluate(context(Map.of()));
    }

    private static MolangContext context(Map<String, Object> queries) {
        return new MolangContext(new MolangVariables(), new MolangQueryHost() {
            @Override
            public double queryNumber(String name, MolangArgs args) {
                if (name.equals("position")) {
                    return new double[]{10, 20, 30}[(int) args.number(0)];
                }
                Object value = queries.get(name);
                return value instanceof Number n ? n.doubleValue() : (value == null ? Double.NaN : 0.0);
            }

            @Override
            public Object queryObject(String name, MolangArgs args) {
                Object value = queries.get(name);
                return value != null ? value : this.queryNumber(name, args);
            }
        });
    }

    @ParameterizedTest(name = "{0} = {1}")
    @CsvSource(delimiterString = " => ", value = {
        "1 + 2 * 3 => 7",
        "(1 + 2) * 3 => 9",
        "-2 * -3 => 6",
        "10 / 4 => 2.5",
        "5 / 0 => 0",
        "!0 => 1",
        "!1 + 1 => 1",
        "1 < 2 && 2 <= 2 => 1",
        "1 > 2 || 3 >= 4 => 0",
        "2 == 2.0 => 1",
        "2 != 2 => 0",
        "1 ? 5 : 6 => 5",
        "0 ? 5 : 6 => 6",
        "0 ? 5 => 0",
        "1 ? 0 ? 1 : 2 : 3 => 2",
        "math.sin(90) => 1",
        "math.cos(180) => -1",
        "math.atan2(1, 1) => 45",
        "math.clamp(5, 0, 3) => 3",
        "math.lerp(0, 10, 0.25) => 2.5",
        "math.lerprotate(350, 10, 0.5) => 360",
        "math.min_angle(270) => -90",
        "math.mod(7, 3) => 1",
        "math.pow(2, 10) => 1024",
        "math.hermite_blend(0.5) => 0.5",
        "math.round(2.5) => 3",
        "math.trunc(-2.7) => -2",
        "math.abs(-3) => 3",
        "math.pi => 3.141592653589793",
        "1.5f + 0.5 => 2",
        "1e2 => 100",
        "TRUE + FALSE => 1",
        "Math.Sqrt(16) => 4",
        "math.ease_in_out_quad(0, 10, 0.5) => 5",
    })
    void arithmeticAndFunctions(String source, double expected) throws MolangException {
        assertEquals(expected, eval(source), 1.0E-6, source);
    }

    @Test
    void stringsCompareCaseInsensitively() throws MolangException {
        assertEquals(1.0, eval("'abc' == 'ABC'"), EPS);
        assertEquals(1.0, eval("'a' != 'b'"), EPS);
    }

    @Test
    void numbersAndPureMathFoldToConstants() throws MolangException {
        assertTrue(Molang.compile("12.5").isConstant());
        assertTrue(Molang.compile("math.sin(30) * 2 + 1").isConstant());
        assertFalse(Molang.compile("math.random(0, 1)").isConstant());
        assertFalse(Molang.compile("q.anim_time * 2").isConstant());
        assertEquals(2.0, Molang.compile("math.sin(30) * 2 + 1").constantValue(), EPS);
    }

    @Test
    void queriesReadTheHostAndAcceptArgumentsAndBothPrefixes() throws MolangException {
        MolangContext ctx = context(Map.of("anim_time", 1.5, "is_moving", 1.0, "action", "roar"));
        assertEquals(3.0, Molang.compile("query.anim_time * 2").evaluate(ctx), EPS);
        assertEquals(1.0, Molang.compile("q.is_moving").evaluate(ctx), EPS);
        assertEquals(20.0, Molang.compile("q.position(1)").evaluate(ctx), EPS);
        assertTrue(Molang.compile("q.action == 'roar'").test(ctx));
        assertFalse(Molang.compile("q.action == 'graze'").test(ctx));
        assertTrue(Molang.compile("Query.Action != 'none'").test(ctx));
    }

    @Test
    void unknownQueriesEvaluateToZero() throws MolangException {
        assertEquals(0.0, Molang.compile("q.not_a_real_query + 1").evaluate(context(Map.of())) - 1.0, EPS);
    }

    @Test
    void variablesPersistAcrossEvaluationsAndTempsDoNot() throws MolangException {
        MolangContext ctx = context(Map.of());
        Molang.compile("v.count = (v.count ?? 10) + 1; t.scratch = 5;").execute(ctx);
        Molang.compile("v.count = v.count + 1;").execute(ctx);
        assertEquals(12.0, Molang.compile("variable.count").evaluate(ctx), EPS);
        assertEquals(0.0, Molang.compile("t.scratch").evaluate(ctx), EPS, "temp.* must be cleared per evaluation");
        Molang.compile("v.name = 'rex';").execute(ctx);
        assertTrue(Molang.compile("v.name == 'rex'").test(ctx));
    }

    @Test
    void complexExpressionsReturnZeroUnlessTheyReturn() throws MolangException {
        assertEquals(0.0, eval("t.a = 3; t.a * 2;"), EPS);
        assertEquals(6.0, eval("t.a = 3; return t.a * 2;"), EPS);
        assertEquals(7.0, eval("t.a = 3; return t.a > 2 ? 7 : 8;"), EPS);
    }

    @Test
    void loopsBreakAndContinue() throws MolangException {
        assertEquals(10.0, eval("t.i = 0; loop(10, {t.i = t.i + 1;}); return t.i;"), EPS);
        assertEquals(4.0, eval("t.i = 0; loop(10, {t.i = t.i + 1; (t.i >= 4) ? break;}); return t.i;"), EPS);
        assertEquals(5.0, eval("t.i = 0; t.n = 0; loop(10, {t.i = t.i + 1; (math.mod(t.i, 2) == 0) ? continue; t.n = t.n + 1;}); return t.n;"), EPS);
        assertEquals(1024.0, eval("t.i = 0; loop(99999, {t.i = t.i + 1;}); return t.i;"), EPS, "loops are capped");
    }

    @Test
    void thisReadsTheContextValue() throws MolangException {
        MolangContext ctx = context(Map.of());
        ctx.thisValue = 4.0;
        assertEquals(8.0, Molang.compile("this * 2").evaluate(ctx), EPS);
    }

    @Test
    void arraysWrapAndResolveResources() throws MolangException {
        MolangContext ctx = context(Map.of("variant", 3.0)).resources(new MolangResources() {
            @Override
            public Object[] array(String name) {
                return name.equals("skins")
                    ? new Object[]{new ResourceRef("texture", "default"), new ResourceRef("texture", "albino")}
                    : null;
            }
        });
        Object picked = Molang.compile("Array.skins[q.variant]").evaluateObject(ctx);
        assertEquals(new MolangResources.ResourceRef("texture", "albino"), picked);
        assertEquals(new MolangResources.ResourceRef("geometry", "default"), Molang.compile("Geometry.default").evaluateObject(ctx));
    }

    @Test
    void nonFiniteResultsAreSanitised() throws MolangException {
        assertEquals(0.0, eval("math.pow(10, 400)"), EPS);
        assertEquals(0.0, eval("math.sqrt(-1)"), EPS);
    }

    @ParameterizedTest
    @ValueSource(strings = {"1 +", "(1", "q.", "foo", "math.nope(1)", "math.sin", "1 2", "'open", "3 = 4", "#"})
    void malformedSourceIsRejectedAtCompileTime(String source) {
        assertThrows(MolangException.class, () -> Molang.compile(source));
    }

    @Test
    void emptySourceIsZero() throws MolangException {
        assertEquals(0.0, eval("   "), EPS);
        assertEquals(0.0, eval(";"), EPS);
    }
}
