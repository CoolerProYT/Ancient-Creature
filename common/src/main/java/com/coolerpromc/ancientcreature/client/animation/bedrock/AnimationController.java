package com.coolerpromc.ancientcreature.client.animation.bedrock;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public record AnimationController(int formatVersion, String initialState, Map<String, State> states) {
    public static final int CURRENT_FORMAT_VERSION = 1;
    public static final float DEFAULT_BLEND_SECONDS = 0.2F;

    private static final Codec<Integer> FORMAT_VERSION_CODEC = Codec.INT.validate(v -> v >= 1 && v <= CURRENT_FORMAT_VERSION ? DataResult.success(v) : DataResult.error(() -> "unsupported animation controller format_version " + v));

    public static final Codec<AnimationController> CODEC = RecordCodecBuilder.create(i -> i.group(
        FORMAT_VERSION_CODEC.optionalFieldOf("format_version", CURRENT_FORMAT_VERSION).forGetter(AnimationController::formatVersion),
        Codec.STRING.optionalFieldOf("initial_state", "idle").forGetter(AnimationController::initialState),
        Codec.unboundedMap(Codec.STRING, State.CODEC).fieldOf("states").forGetter(AnimationController::states)
    ).apply(i, AnimationController::new));

    public DataResult<AnimationController> validate() {
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

        if (!problems.isEmpty()) {
            return DataResult.error(() -> String.join("; ", problems));
        }
        return DataResult.success(this);
    }

    public State state(String name) {
        return this.states.getOrDefault(name, State.EMPTY);
    }

    public String resolveState(String current, AnimationQueryContext context) {
        String name = this.states.containsKey(current) ? current : this.initialState;

        for (int hops = 0; hops < this.states.size(); hops++) {
            State state = this.state(name);
            String next = null;
            for (Transition transition : state.transitions()) {
                if (transition.condition().test(context)) {
                    next = transition.target();
                    break;
                }
            }
            if (next == null || next.equals(name) || !this.states.containsKey(next)) {
                return name;
            }
            name = next;
        }
        return name;
    }

    public record State(List<String> animations, float blendSeconds, List<Transition> transitions) {
        public static final State EMPTY = new State(List.of(), DEFAULT_BLEND_SECONDS, List.of());

        public static final Codec<State> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.STRING.listOf().optionalFieldOf("animations", List.of()).forGetter(State::animations),
            ExtraCodecs.floatRange(0.0F, 10.0F).optionalFieldOf("blend_transition", DEFAULT_BLEND_SECONDS).forGetter(State::blendSeconds),
            Transition.CODEC.listOf().optionalFieldOf("transitions", List.of()).forGetter(State::transitions)
        ).apply(i, State::new));
    }

    public record Transition(String target, AnimationExpression condition) {
        public static final Codec<Transition> CODEC =
            Codec.unboundedMap(Codec.STRING, AnimationExpression.CODEC).comapFlatMap(
                map -> {
                    if (map.size() != 1) {
                        return DataResult.error(() -> "a transition must be a single { \"state\": \"condition\" } pair, got " + map.size() + " entries");
                    }
                    Map.Entry<String, AnimationExpression> entry = map.entrySet().iterator().next();
                    return DataResult.success(new Transition(entry.getKey(), entry.getValue()));
                },
                transition -> Map.of(transition.target(), transition.condition()));
    }
}
