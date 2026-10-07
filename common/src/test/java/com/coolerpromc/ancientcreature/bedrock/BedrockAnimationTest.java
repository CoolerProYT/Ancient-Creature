package com.coolerpromc.ancientcreature.bedrock;

import com.coolerpromc.ancientcreature.client.animation.bedrock.AnimationController;
import com.coolerpromc.ancientcreature.client.animation.bedrock.AnimationPlayer;
import com.coolerpromc.ancientcreature.client.animation.bedrock.BedrockAnimation;
import com.coolerpromc.ancientcreature.client.model.bedrock.BedrockFormatException;
import com.coolerpromc.ancientcreature.molang.MolangArgs;
import com.coolerpromc.ancientcreature.molang.MolangContext;
import com.coolerpromc.ancientcreature.molang.MolangQueryHost;
import com.coolerpromc.ancientcreature.molang.MolangVariables;
import com.google.gson.JsonParser;
import org.joml.Vector3f;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/** Bedrock animation and controller semantics, matched to Blockbench's preview. */
class BedrockAnimationTest {
    private static final float EPS = 1.0E-4F;

    private static BedrockAnimation anim(String body) throws BedrockFormatException {
        Map<String, BedrockAnimation> file = BedrockAnimation.parseFile(JsonParser.parseString(
            "{\"format_version\": \"1.8.0\", \"animations\": {\"animation.t.a\": " + body + "}}"));
        return file.get("animation.t.a");
    }

    private static float rotX(BedrockAnimation animation, float time, MolangContext ctx) {
        return animation.bones().get("b").rotation().sample(time, ctx, new Vector3f()).x;
    }

    @Test
    void linearInterpolatesAndClampsOutsideTheKeys() throws Exception {
        BedrockAnimation a = anim("{\"bones\": {\"b\": {\"rotation\": {\"0.0\": [0, 0, 0], \"1.0\": [10, 0, 0]}}}}");
        MolangContext ctx = new MolangContext();
        assertEquals(0.0F, rotX(a, -1.0F, ctx), EPS);
        assertEquals(2.5F, rotX(a, 0.25F, ctx), EPS);
        assertEquals(10.0F, rotX(a, 5.0F, ctx), EPS);
        assertEquals(1.0F, a.length(), EPS, "length defaults to the last keyframe");
    }

    @Test
    void preAndPostSplitAKeyframe() throws Exception {
        BedrockAnimation a = anim("{\"bones\": {\"b\": {\"rotation\": {\"0.0\": [0, 0, 0], \"1.0\": {\"pre\": [10, 0, 0], \"post\": [-10, 0, 0]}, \"2.0\": [0, 0, 0]}}}}");
        MolangContext ctx = new MolangContext();
        assertEquals(5.0F, rotX(a, 0.5F, ctx), EPS, "arrives at pre");
        assertEquals(10.0F, rotX(a, 1.0F, ctx), EPS, "exactly on the key shows pre");
        assertEquals(-5.0F, rotX(a, 1.5F, ctx), EPS, "leaves from post");
    }

    @Test
    void stepHoldsUntilTheNextKey() throws Exception {
        BedrockAnimation a = anim("{\"bones\": {\"b\": {\"rotation\": {\"0.0\": {\"post\": [4, 0, 0], \"lerp_mode\": \"step\"}, \"1.0\": [8, 0, 0]}}}}");
        assertEquals(4.0F, rotX(a, 0.99F, new MolangContext()), EPS);
    }

    @Test
    void catmullRomPassesThroughKeysAndSmoothsBetween() throws Exception {
        BedrockAnimation a = anim("{\"bones\": {\"b\": {\"rotation\": {"
            + "\"0.0\": {\"post\": [0, 0, 0], \"lerp_mode\": \"catmullrom\"},"
            + "\"1.0\": {\"post\": [10, 0, 0], \"lerp_mode\": \"catmullrom\"},"
            + "\"2.0\": {\"post\": [0, 0, 0], \"lerp_mode\": \"catmullrom\"}}}}}");
        MolangContext ctx = new MolangContext();
        assertEquals(10.0F, rotX(a, 1.0F, ctx), EPS);
        float mid = rotX(a, 0.5F, ctx);
        assertTrue(mid > 5.0F, "a spline through 0, 10, 0 bulges above the straight line at the midpoint, got " + mid);
    }

    @Test
    void keyframesMayBeMolang() throws Exception {
        BedrockAnimation a = anim("{\"loop\": true, \"bones\": {\"b\": {\"rotation\": [\"math.sin(q.anim_time * 90) * 30\", 0, \"v.tilt\"]}}}");
        MolangVariables vars = new MolangVariables();
        float[] time = {1.0F};
        MolangContext ctx = new MolangContext(vars, new MolangQueryHost() {
            @Override
            public double queryNumber(String name, MolangArgs args) {
                return name.equals("anim_time") ? time[0] : Double.NaN;
            }
        });
        vars.set("tilt", 7.0);
        Vector3f v = a.bones().get("b").rotation().sample(time[0], ctx, new Vector3f());
        assertEquals(30.0F, v.x, EPS);
        assertEquals(7.0F, v.z, EPS);
    }

    @Test
    void loopModesAndDelays() throws Exception {
        MolangContext ctx = new MolangContext();
        AnimationPlayer.AnimClock clock = (t, d) -> {
        };
        List<String> fired = new ArrayList<>();
        AnimationPlayer.EventSink sink = new AnimationPlayer.EventSink() {
            @Override
            public void sound(BedrockAnimation.TimedEffect effect) {
                fired.add("sound:" + effect.effect());
            }

            @Override
            public void particle(BedrockAnimation.TimedEffect effect) {
                fired.add("particle:" + effect.effect());
            }
        };

        AnimationPlayer once = new AnimationPlayer(anim("{\"animation_length\": 1.0, \"bones\": {}, \"sound_effects\": {\"0.5\": {\"effect\": \"roar\"}}}"));
        once.restart(ctx);
        once.advance(0.4F, ctx, clock, sink);
        assertTrue(fired.isEmpty());
        once.advance(0.2F, ctx, clock, sink);
        assertEquals(List.of("sound:roar"), fired);
        once.advance(1.0F, ctx, clock, sink);
        assertTrue(once.isFinished());
        assertFalse(once.isPosing(), "a finished play-once animation stops posing");

        AnimationPlayer hold = new AnimationPlayer(anim("{\"loop\": \"hold_on_last_frame\", \"animation_length\": 1.0, \"bones\": {}}"));
        hold.restart(ctx);
        hold.advance(3.0F, ctx, clock, sink);
        assertTrue(hold.isPosing(), "hold_on_last_frame keeps posing");
        assertEquals(1.0F, hold.animTime(), EPS);

        fired.clear();
        AnimationPlayer loop = new AnimationPlayer(anim("{\"loop\": true, \"animation_length\": 1.0, \"start_delay\": 0.5, \"bones\": {}, \"particle_effects\": {\"0.0\": {\"effect\": \"dust\"}}}"));
        loop.restart(ctx);
        loop.advance(0.25F, ctx, clock, sink);
        assertFalse(loop.isPosing(), "nothing plays during start_delay");
        loop.advance(0.5F, ctx, clock, sink);
        assertEquals(0.25F, loop.animTime(), EPS, "time starts counting once the delay has passed");
        loop.advance(1.0F, ctx, clock, sink);
        assertEquals(0.25F, loop.animTime(), EPS, "loops wrap");
        assertEquals(2, fired.size(), "the 0.0 event fires on the first frame and again after wrapping");
    }

    @Test
    void animTimeUpdateDrivesTime() throws Exception {
        float[] seen = new float[2];
        MolangContext ctx = new MolangContext(new MolangVariables(), new MolangQueryHost() {
            @Override
            public double queryNumber(String name, MolangArgs args) {
                return switch (name) {
                    case "anim_time" -> seen[0];
                    case "delta_time" -> seen[1];
                    default -> Double.NaN;
                };
            }
        });
        AnimationPlayer player = new AnimationPlayer(anim("{\"loop\": true, \"animation_length\": 10.0, \"anim_time_update\": \"q.anim_time + q.delta_time * 3\", \"bones\": {}}"));
        player.restart(ctx);
        player.advance(0.5F, ctx, (t, d) -> {
            seen[0] = t;
            seen[1] = d;
        }, new AnimationPlayer.EventSink() {
            @Override
            public void sound(BedrockAnimation.TimedEffect effect) {
            }

            @Override
            public void particle(BedrockAnimation.TimedEffect effect) {
            }
        });
        assertEquals(1.5F, player.animTime(), EPS);
    }

    @Test
    void bedrockControllersParse() throws Exception {
        Map<String, AnimationController> controllers = AnimationController.parseFile(JsonParser.parseString("""
            { "format_version": "1.10.0", "animation_controllers": { "controller.animation.t.move": {
              "initial_state": "default",
              "states": {
                "default": { "animations": ["idle", { "look": "!q.is_sleeping" }], "transitions": [ { "walk": "q.is_moving" } ],
                             "blend_transition": 0.3, "on_entry": ["v.walking = 0;"] },
                "walk": { "animations": [{ "walk": "q.modified_move_speed" }], "transitions": [ { "default": "!q.is_moving" } ],
                          "blend_transition": { "0.0": 1.0, "0.1": 0.5, "0.4": 0.0 }, "sound_effects": [ { "effect": "step" } ] }
              } } } }"""), "unused");
        AnimationController controller = controllers.get("controller.animation.t.move");
        assertNotNull(controller);
        assertTrue(controller.validate().isEmpty());
        assertEquals(2, controller.state("default").animations().size());
        assertEquals(0.3F, controller.state("default").blend().duration(), EPS);
        assertEquals(0.5F, controller.state("default").blend().outgoingWeight(0.15F), EPS);
        assertEquals(0.75F, controller.state("walk").blend().outgoingWeight(0.05F), EPS);
        assertEquals(0.0F, controller.state("walk").blend().outgoingWeight(0.5F), EPS);
        assertEquals(List.of("step"), controller.state("walk").soundEffects());
    }

    @Test
    void legacyControllersStillParse() throws Exception {
        Map<String, AnimationController> controllers = AnimationController.parseFile(JsonParser.parseString("""
            { "format_version": 1, "initial_state": "idle", "states": {
              "idle": { "animations": ["idle"], "transitions": [ { "walk": "query.is_moving" } ] },
              "walk": { "animations": ["walk"], "transitions": [ { "idle": "!query.is_moving" } ] } } }"""), "mypack:rex");
        AnimationController controller = controllers.get("mypack:rex");
        assertNotNull(controller);
        assertEquals(0.2F, controller.state("idle").blend().duration(), EPS, "legacy states keep their implicit blend");
    }

    @Test
    void badControllersFailLoudly() {
        assertThrows(BedrockFormatException.class, () -> AnimationController.parseFile(JsonParser.parseString("""
            { "format_version": "1.10.0", "animation_controllers": { "c": { "states": {
              "a": { "transitions": [ { "b": "q.is_moving &&" } ] }, "b": {} } } } }"""), "x"));
        AnimationController dangling = assertDoesNotThrow(() -> AnimationController.parseFile(JsonParser.parseString("""
            { "format_version": "1.10.0", "animation_controllers": { "c": { "initial_state": "a", "states": {
              "a": { "transitions": [ { "nowhere": "1" } ] } } } } }"""), "x")).get("c");
        assertFalse(dangling.validate().isEmpty(), "a transition to an unknown state is reported");
    }
}
