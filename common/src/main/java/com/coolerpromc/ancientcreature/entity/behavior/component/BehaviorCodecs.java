package com.coolerpromc.ancientcreature.entity.behavior.component;

import com.mojang.serialization.Codec;

/** Shared, range-checked primitives for behavior component configuration. */
public final class BehaviorCodecs {
    /** Movement speed multipliers. Zero or negative would freeze the goal; absurd values fling the mob. */
    public static final Codec<Double> SPEED = Codec.doubleRange(0.01, 16.0);

    /** Distances in blocks. */
    public static final Codec<Double> DISTANCE = Codec.doubleRange(0.0, 128.0);

    /** Distances in blocks, as a float (goal APIs are inconsistent about this). */
    public static final Codec<Float> DISTANCE_F = Codec.floatRange(0.0F, 128.0F);

    /** Cooldowns and durations in ticks. */
    public static final Codec<Integer> TICKS = Codec.intRange(0, 72000);

    /** Reciprocal chance for goals that use {@code randomInterval}. */
    public static final Codec<Integer> CHANCE = Codec.intRange(1, 10000);

    private BehaviorCodecs() {
    }
}
