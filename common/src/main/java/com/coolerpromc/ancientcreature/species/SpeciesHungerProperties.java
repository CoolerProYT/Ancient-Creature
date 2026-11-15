package com.coolerpromc.ancientcreature.species;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;

public record SpeciesHungerProperties(
    float max,
    int decayInterval,
    float huntThreshold,
    float fullThreshold,
    float foodValue,
    float killValue,
    float starveDamage,
    int starveInterval
) {

    public static final SpeciesHungerProperties DEFAULT =
        new SpeciesHungerProperties(20.0F, 1200, 10.0F, 18.0F, 6.0F, 8.0F, 0.0F, 200);

    public static final float MAX_HUNGER = 1024.0F;

    private static final Codec<SpeciesHungerProperties> FIELDS = RecordCodecBuilder.create(i -> i.group(
        ExtraCodecs.floatRange(Float.MIN_NORMAL, MAX_HUNGER).optionalFieldOf("max", DEFAULT.max()).forGetter(SpeciesHungerProperties::max),
        ExtraCodecs.POSITIVE_INT.optionalFieldOf("decay_interval", DEFAULT.decayInterval()).forGetter(SpeciesHungerProperties::decayInterval),
        ExtraCodecs.floatRange(0.0F, MAX_HUNGER).optionalFieldOf("hunt_threshold", DEFAULT.huntThreshold()).forGetter(SpeciesHungerProperties::huntThreshold),
        ExtraCodecs.floatRange(0.0F, MAX_HUNGER).optionalFieldOf("full_threshold", DEFAULT.fullThreshold()).forGetter(SpeciesHungerProperties::fullThreshold),
        ExtraCodecs.floatRange(0.0F, MAX_HUNGER).optionalFieldOf("food_value", DEFAULT.foodValue()).forGetter(SpeciesHungerProperties::foodValue),
        ExtraCodecs.floatRange(0.0F, MAX_HUNGER).optionalFieldOf("kill_value", DEFAULT.killValue()).forGetter(SpeciesHungerProperties::killValue),
        ExtraCodecs.floatRange(0.0F, 1024.0F).optionalFieldOf("starve_damage", DEFAULT.starveDamage()).forGetter(SpeciesHungerProperties::starveDamage),
        ExtraCodecs.POSITIVE_INT.optionalFieldOf("starve_interval", DEFAULT.starveInterval()).forGetter(SpeciesHungerProperties::starveInterval)
    ).apply(i, SpeciesHungerProperties::new));

    public static final Codec<SpeciesHungerProperties> CODEC = FIELDS.validate(SpeciesHungerProperties::validate);

    public static final StreamCodec<RegistryFriendlyByteBuf, SpeciesHungerProperties> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.FLOAT, SpeciesHungerProperties::max,
        ByteBufCodecs.VAR_INT, SpeciesHungerProperties::decayInterval,
        ByteBufCodecs.FLOAT, SpeciesHungerProperties::huntThreshold,
        ByteBufCodecs.FLOAT, SpeciesHungerProperties::fullThreshold,
        ByteBufCodecs.FLOAT, SpeciesHungerProperties::foodValue,
        ByteBufCodecs.FLOAT, SpeciesHungerProperties::killValue,
        ByteBufCodecs.FLOAT, SpeciesHungerProperties::starveDamage,
        ByteBufCodecs.VAR_INT, SpeciesHungerProperties::starveInterval,
        SpeciesHungerProperties::new
    );

    private static DataResult<SpeciesHungerProperties> validate(SpeciesHungerProperties hunger) {
        if (hunger.huntThreshold > hunger.max) {
            return DataResult.error(() -> "hunt_threshold (" + hunger.huntThreshold
                + ") is above max (" + hunger.max + "), so the creature would always count as hungry");
        }
        if (hunger.fullThreshold < hunger.huntThreshold) {
            return DataResult.error(() -> "full_threshold (" + hunger.fullThreshold
                + ") is below hunt_threshold (" + hunger.huntThreshold
                + "); a predator would start and stop hunting on the same tick");
        }
        return DataResult.success(hunger);
    }

    public boolean isHungryAt(float hunger) {
        return hunger <= this.huntThreshold;
    }

    public boolean isSatedAt(float hunger) {
        return hunger >= this.fullThreshold;
    }

    public boolean starves() {
        return this.starveDamage > 0.0F;
    }

    public float clamp(float hunger) {
        return Math.max(0.0F, Math.min(hunger, this.max));
    }
}
