package com.coolerpromc.ancientcreature.entity.behavior;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;

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
            type -> (MapCodec<? extends CreatureBehaviorConfig>) type.codec());

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
