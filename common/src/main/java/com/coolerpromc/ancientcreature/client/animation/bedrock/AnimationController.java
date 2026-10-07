package com.coolerpromc.ancientcreature.client.animation.bedrock;

import com.coolerpromc.ancientcreature.client.model.bedrock.BedrockFormatException;
import com.coolerpromc.ancientcreature.client.model.bedrock.BedrockJson;
import com.coolerpromc.ancientcreature.molang.Molang;
import com.coolerpromc.ancientcreature.molang.MolangExpression;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.*;

/**
 * A Bedrock animation controller: a state machine whose states play animations.
 *
 * <p>Reads the real Bedrock format —
 * {@code {"format_version": "1.10.0", "animation_controllers": {"controller.animation.x": {...}}}} —
 * with {@code initial_state}, and per state {@code animations} (names or {@code {name: blend}}),
 * {@code transitions}, {@code blend_transition} (seconds or a weight curve),
 * {@code blend_via_shortest_path}, {@code on_entry}, {@code on_exit}, {@code sound_effects} and
 * {@code particle_effects}.
 *
 * <p>Also reads Ancient Creature's earlier format ({@code "format_version": 1} with top-level
 * {@code states}), so existing species packs keep working; such a file holds one controller.
 */
public record AnimationController(String name, String initialState, Map<String, State> states) {
    public static final float DEFAULT_BLEND_SECONDS = 0.0F;
    /** Blend used for legacy files, which always blended. */
    private static final float LEGACY_BLEND_SECONDS = 0.2F;

    public record State(List<StateAnimation> animations, List<Transition> transitions, BlendCurve blend, boolean blendViaShortestPath, List<MolangExpression> onEntry, List<MolangExpression> onExit, List<String> soundEffects, List<StateParticle> particleEffects) {
        public static final State EMPTY = new State(List.of(), List.of(), BlendCurve.NONE, false, List.of(), List.of(), List.of(), List.of());
    }

    public record StateAnimation(String name, MolangExpression weight) {
    }

    public record Transition(String target, MolangExpression condition) {
    }

    public record StateParticle(String effect, Optional<String> locator, Optional<MolangExpression> preEffectScript) {
    }

    /**
     * How long, and along which curve, the previous state fades out after a transition. A plain number
     * is a linear fade over that many seconds; a curve maps seconds to the outgoing state's weight.
     */
    public record BlendCurve(float[] times, float[] weights) {
        public static final BlendCurve NONE = linear(0.0F);

        public static BlendCurve linear(float seconds) {
            return seconds <= 0.0F ? new BlendCurve(new float[]{0.0F}, new float[]{0.0F}) : new BlendCurve(new float[]{0.0F, seconds}, new float[]{1.0F, 0.0F});
        }

        public float duration() {
            return this.times[this.times.length - 1];
        }

        /** Weight of the outgoing state {@code seconds} after the transition. */
        public float outgoingWeight(float seconds) {
            if (seconds >= this.duration()) {
                return 0.0F;
            }
            if (seconds <= this.times[0]) {
                return this.weights[0];
            }
            for (int i = 1; i < this.times.length; i++) {
                if (seconds <= this.times[i]) {
                    float span = this.times[i] - this.times[i - 1];
                    float t = span <= 0 ? 1 : (seconds - this.times[i - 1]) / span;
                    return this.weights[i - 1] + (this.weights[i] - this.weights[i - 1]) * t;
                }
            }
            return 0.0F;
        }
    }

    public State state(String name) {
        return this.states.getOrDefault(name, State.EMPTY);
    }

    public List<String> validate() {
        List<String> problems = new ArrayList<>();
        if (!this.states.containsKey(this.initialState)) {
            problems.add("initial_state '" + this.initialState + "' is not one of the declared states");
        }
        this.states.forEach((name, state) -> {
            for (Transition transition : state.transitions()) {
                if (!this.states.containsKey(transition.target())) {
                    problems.add("state '" + name + "' transitions to unknown state '" + transition.target() + "'");
                }
            }
        });
        return problems;
    }

    // ------------------------------------------------------------------ parsing

    /** Whether {@code json} is the earlier single-controller Ancient Creature format. */
    public static boolean isLegacy(JsonObject json) {
        JsonElement version = json.get("format_version");
        return json.has("states") && !json.has("animation_controllers") && (version == null || (version.isJsonPrimitive() && version.getAsJsonPrimitive().isNumber()));
    }

    /** Parses a Bedrock animation controller file. Legacy files yield one controller named {@code legacyName}. */
    public static Map<String, AnimationController> parseFile(JsonElement element, String legacyName) throws BedrockFormatException {
        JsonObject json = BedrockJson.object(element, "$");
        Map<String, AnimationController> out = new LinkedHashMap<>();
        if (isLegacy(json)) {
            if (json.get("format_version").getAsInt() != 1) {
                throw new BedrockFormatException("$.format_version " + json.get("format_version") + " is not supported (expected 1 or a Bedrock version string)");
            }
            out.put(legacyName, parseController(legacyName, json, "$", true));
            return out;
        }
        JsonElement controllers = BedrockJson.get(json, "animation_controllers");
        if (controllers == null) {
            throw new BedrockFormatException("$.animation_controllers is required");
        }
        for (Map.Entry<String, JsonElement> entry : BedrockJson.object(controllers, "$.animation_controllers").entrySet()) {
            out.put(entry.getKey(), parseController(entry.getKey(), BedrockJson.object(entry.getValue(), "$.animation_controllers." + entry.getKey()), "$.animation_controllers." + entry.getKey(), false));
        }
        return out;
    }

    private static AnimationController parseController(String name, JsonObject json, String path, boolean legacy) throws BedrockFormatException {
        JsonElement statesJson = BedrockJson.get(json, "states");
        if (statesJson == null) {
            throw new BedrockFormatException(path + ".states is required");
        }
        Map<String, State> states = new LinkedHashMap<>();
        for (Map.Entry<String, JsonElement> entry : BedrockJson.object(statesJson, path + ".states").entrySet()) {
            states.put(entry.getKey(), parseState(BedrockJson.object(entry.getValue(), path + ".states." + entry.getKey()), path + ".states." + entry.getKey(), legacy));
        }
        String initial = BedrockJson.string(json, "initial_state", legacy ? "idle" : "default");
        if (!states.containsKey(initial) && !legacy && states.size() == 1) {
            initial = states.keySet().iterator().next();
        }
        return new AnimationController(name, initial, Collections.unmodifiableMap(states));
    }

    private static State parseState(JsonObject json, String path, boolean legacy) throws BedrockFormatException {
        List<StateAnimation> animations = new ArrayList<>();
        JsonElement animationsJson = BedrockJson.get(json, "animations");
        if (animationsJson != null) {
            if (!animationsJson.isJsonArray()) {
                throw new BedrockFormatException(path + ".animations must be an array");
            }
            for (JsonElement entry : animationsJson.getAsJsonArray()) {
                animations.addAll(namedExpressions(entry, MolangExpression.ONE, path + ".animations").stream().map(p -> new StateAnimation(p.name, p.expression)).toList());
            }
        }

        List<Transition> transitions = new ArrayList<>();
        JsonElement transitionsJson = BedrockJson.get(json, "transitions");
        if (transitionsJson != null) {
            if (!transitionsJson.isJsonArray()) {
                throw new BedrockFormatException(path + ".transitions must be an array");
            }
            JsonArray array = transitionsJson.getAsJsonArray();
            for (int i = 0; i < array.size(); i++) {
                JsonObject transition = BedrockJson.object(array.get(i), path + ".transitions[" + i + "]");
                if (transition.size() != 1) {
                    throw new BedrockFormatException(path + ".transitions[" + i + "] must be a single {\"state\": \"condition\"} pair");
                }
                Map.Entry<String, JsonElement> only = transition.entrySet().iterator().next();
                transitions.add(new Transition(only.getKey(), compileStrict(BedrockJson.molangSource(only.getValue()), path + ".transitions[" + i + "]")));
            }
        }

        BlendCurve blend = legacy ? BlendCurve.linear(LEGACY_BLEND_SECONDS) : BlendCurve.NONE;
        JsonElement blendJson = BedrockJson.get(json, "blend_transition");
        if (blendJson != null) {
            if (blendJson.isJsonObject()) {
                TreeMap<Float, Float> points = new TreeMap<>();
                for (Map.Entry<String, JsonElement> entry : blendJson.getAsJsonObject().entrySet()) {
                    try {
                        points.put(Float.parseFloat(entry.getKey()), BedrockJson.number(entry.getValue(), path + ".blend_transition." + entry.getKey()));
                    } catch (NumberFormatException e) {
                        throw new BedrockFormatException(path + ".blend_transition has a non-numeric time '" + entry.getKey() + "'");
                    }
                }
                float[] times = new float[points.size()];
                float[] weights = new float[points.size()];
                int i = 0;
                for (Map.Entry<Float, Float> point : points.entrySet()) {
                    times[i] = point.getKey();
                    weights[i++] = point.getValue();
                }
                blend = times.length == 0 ? BlendCurve.NONE : new BlendCurve(times, weights);
            } else {
                blend = BlendCurve.linear(Math.max(0.0F, BedrockJson.number(blendJson, path + ".blend_transition")));
            }
        }

        List<MolangExpression> onEntry = scripts(BedrockJson.get(json, "on_entry"), path + ".on_entry");
        List<MolangExpression> onExit = scripts(BedrockJson.get(json, "on_exit"), path + ".on_exit");

        List<String> sounds = new ArrayList<>();
        JsonElement soundJson = BedrockJson.get(json, "sound_effects");
        if (soundJson != null && soundJson.isJsonArray()) {
            for (JsonElement entry : soundJson.getAsJsonArray()) {
                if (entry.isJsonObject()) {
                    String effect = BedrockJson.string(entry.getAsJsonObject(), "effect", "");
                    if (!effect.isEmpty()) {
                        sounds.add(effect.toLowerCase(Locale.ROOT));
                    }
                }
            }
        }

        List<StateParticle> particles = new ArrayList<>();
        JsonElement particleJson = BedrockJson.get(json, "particle_effects");
        if (particleJson != null && particleJson.isJsonArray()) {
            for (JsonElement entry : particleJson.getAsJsonArray()) {
                if (entry.isJsonObject()) {
                    JsonObject particle = entry.getAsJsonObject();
                    String effect = BedrockJson.string(particle, "effect", "");
                    if (effect.isEmpty()) {
                        continue;
                    }
                    String locator = BedrockJson.string(particle, "locator", "");
                    JsonElement script = BedrockJson.get(particle, "pre_effect_script");
                    particles.add(new StateParticle(effect.toLowerCase(Locale.ROOT),
                        locator.isEmpty() ? Optional.empty() : Optional.of(locator.toLowerCase(Locale.ROOT)),
                        script == null ? Optional.empty() : Optional.of(Molang.of(BedrockJson.molangSource(script), path + ".particle_effects"))));
                }
            }
        }

        return new State(List.copyOf(animations), List.copyOf(transitions), blend,
            BedrockJson.bool(json, "blend_via_shortest_path", false), onEntry, onExit, List.copyOf(sounds), List.copyOf(particles));
    }

    /**
     * Transition conditions must compile: a typo would otherwise leave a state that can never be left,
     * which is far harder to diagnose than a load error.
     */
    private static MolangExpression compileStrict(Object source, String path) throws BedrockFormatException {
        if (source instanceof Number || source instanceof Boolean) {
            return Molang.of(source, path);
        }
        try {
            return Molang.compile(String.valueOf(source));
        } catch (com.coolerpromc.ancientcreature.molang.MolangException e) {
            throw new BedrockFormatException(path + ": invalid Molang \"" + source + "\": " + e.getMessage());
        }
    }

    record Named(String name, MolangExpression expression) {
    }

    /** {@code "name"} or {@code {"name": "molang"}} as used by animation lists. */
    static List<Named> namedExpressions(JsonElement entry, MolangExpression fallback, String path) throws BedrockFormatException {
        List<Named> out = new ArrayList<>();
        if (entry.isJsonPrimitive()) {
            out.add(new Named(entry.getAsString(), fallback));
        } else if (entry.isJsonObject()) {
            for (Map.Entry<String, JsonElement> e : entry.getAsJsonObject().entrySet()) {
                JsonElement value = e.getValue();
                Object source = value.isJsonPrimitive() ? BedrockJson.molangSource(value) : (value instanceof JsonPrimitive p ? p.getAsString() : value.toString());
                out.add(new Named(e.getKey(), Molang.of(source, path + "." + e.getKey())));
            }
        } else {
            throw new BedrockFormatException(path + " entries must be a name or {\"name\": \"condition\"}");
        }
        return out;
    }

    static List<MolangExpression> scripts(JsonElement element, String path) {
        List<MolangExpression> out = new ArrayList<>();
        for (String source : BedrockJson.strings(element)) {
            out.add(Molang.compileOr(source, MolangExpression.ZERO, path));
        }
        return List.copyOf(out);
    }
}
