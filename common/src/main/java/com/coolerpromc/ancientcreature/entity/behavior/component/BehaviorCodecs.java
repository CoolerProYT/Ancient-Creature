package com.coolerpromc.ancientcreature.entity.behavior.component;

import com.mojang.serialization.Codec;

public final class BehaviorCodecs {

    public static final Codec<Double> SPEED = Codec.doubleRange(0.01, 16.0);

    public static final Codec<Double> DISTANCE = Codec.doubleRange(0.0, 128.0);

    public static final Codec<Float> DISTANCE_F = Codec.floatRange(0.0F, 128.0F);

    public static final Codec<Integer> TICKS = Codec.intRange(0, 72000);

    public static final Codec<Integer> CHANCE = Codec.intRange(1, 10000);

    public static final Codec<Integer> BLOCK_RANGE = Codec.intRange(1, 128);

    public static final Codec<Integer> ALTITUDE = Codec.intRange(0, 320);

    public static final Codec<Float> HUNGER = Codec.floatRange(0.0F, 1024.0F);

    private BehaviorCodecs() {
    }
}
