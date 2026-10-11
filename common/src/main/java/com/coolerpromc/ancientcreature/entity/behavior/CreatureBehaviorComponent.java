package com.coolerpromc.ancientcreature.entity.behavior;

import com.coolerpromc.ancientcreature.Constants;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * One entry of a species' {@code behavior.components} list: a configured component plus the goal
 * priority it should be installed at.
 *
 * <p>JSON shape — {@code "priority"} sits alongside the component's own fields:
 * <pre>{@code
 * { "type": "ancientcreature:charge_attack", "priority": 1, "speed_multiplier": 1.45 }
 * }</pre>
 */
public record CreatureBehaviorComponent(Optional<Integer> explicitPriority, CreatureBehaviorConfig config) {
    @SuppressWarnings("unchecked")
    private static final MapCodec<CreatureBehaviorConfig> CONFIG_CODEC =
        CreatureBehaviorRegistry.TYPE_CODEC.dispatchMap(
            "type",
            CreatureBehaviorConfig::type,
            type -> warnOnUnknownKeys((MapCodec<? extends CreatureBehaviorConfig>) type.codec(), type));

    public static final Codec<CreatureBehaviorComponent> CODEC = RecordCodecBuilder.create(i -> i.group(
        Codec.intRange(0, 100).optionalFieldOf("priority").forGetter(CreatureBehaviorComponent::explicitPriority),
        CONFIG_CODEC.forGetter(CreatureBehaviorComponent::config)
    ).apply(i, CreatureBehaviorComponent::new));

    /**
     * Components are re-encoded to JSON for the network hop rather than given a bespoke stream codec —
     * they are a handful of small objects sent once per player, and this keeps a single source of truth
     * for the format.
     */
    public static final StreamCodec<RegistryFriendlyByteBuf, CreatureBehaviorComponent> STREAM_CODEC =
        ByteBufCodecs.fromCodecWithRegistries(CODEC);

    /**
     * Unknown keys are otherwise dropped without a word, so a misspelt option silently falls back to its
     * default. Warn instead of failing, so a datapack written for a newer version still loads.
     */
    private static <C> MapCodec<C> warnOnUnknownKeys(MapCodec<C> codec, CreatureBehaviorType<?> type) {
        return new MapCodec<>() {
            @Override
            public <T> Stream<T> keys(DynamicOps<T> ops) {
                return codec.keys(ops);
            }

            @Override
            public <T> DataResult<C> decode(DynamicOps<T> ops, MapLike<T> input) {
                Set<String> known = codec.keys(ops).map(key -> ops.getStringValue(key).result().orElse("")).collect(Collectors.toSet());
                input.entries().forEach(entry -> ops.getStringValue(entry.getFirst()).result().ifPresent(key -> {
                    if (!known.contains(key) && !NON_CONFIG_KEYS.contains(key)) {
                        Constants.LOG.warn("Behavior component '{}' has no option '{}'; it is ignored. Known options: {}", type.id(), key, known);
                    }
                }));
                return codec.decode(ops, input);
            }

            @Override
            public <T> RecordBuilder<T> encode(C input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
                return codec.encode(input, ops, prefix);
            }
        };
    }

    private static final Set<String> NON_CONFIG_KEYS = Set.of("type", "priority");

    public CreatureBehaviorComponent(CreatureBehaviorConfig config) {
        this(Optional.empty(), config);
    }

    public int priority() {
        return this.explicitPriority.orElseGet(() -> this.config.type().defaultPriority());
    }

    public CreatureBehaviorConfig.GoalSlot slot() {
        return this.config.slot();
    }
}
