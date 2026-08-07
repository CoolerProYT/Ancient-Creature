package com.coolerpromc.ancientcreature.species;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;

public record SpeciesGrowthProperties(int adultAge, float babyScale, float adultScale) {
    public static final SpeciesGrowthProperties DEFAULT = new SpeciesGrowthProperties(24000, 0.5F, 1.0F);

    /** Guards against a datapack scaling a creature into an AABB that would stall collision lookups. */
    public static final float MAX_SCALE = 16.0F;

    public static final Codec<SpeciesGrowthProperties> CODEC = RecordCodecBuilder.create(i -> i.group(
        ExtraCodecs.POSITIVE_INT.optionalFieldOf("adult_age", DEFAULT.adultAge()).forGetter(SpeciesGrowthProperties::adultAge),
        ExtraCodecs.floatRange(Float.MIN_NORMAL, MAX_SCALE).optionalFieldOf("baby_scale", DEFAULT.babyScale()).forGetter(SpeciesGrowthProperties::babyScale),
        ExtraCodecs.floatRange(Float.MIN_NORMAL, MAX_SCALE).optionalFieldOf("adult_scale", DEFAULT.adultScale()).forGetter(SpeciesGrowthProperties::adultScale)
    ).apply(i, SpeciesGrowthProperties::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, SpeciesGrowthProperties> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_INT, SpeciesGrowthProperties::adultAge,
        ByteBufCodecs.FLOAT, SpeciesGrowthProperties::babyScale,
        ByteBufCodecs.FLOAT, SpeciesGrowthProperties::adultScale,
        SpeciesGrowthProperties::new
    );

    public float scaleFor(boolean baby) {
        return baby ? this.babyScale : this.adultScale;
    }
}
