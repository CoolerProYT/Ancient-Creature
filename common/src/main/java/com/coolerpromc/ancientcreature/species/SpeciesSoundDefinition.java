package com.coolerpromc.ancientcreature.species;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.ExtraCodecs;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public record SpeciesSoundDefinition(
    Optional<Identifier> ambient,
    Optional<Identifier> hurt,
    Optional<Identifier> death,
    Optional<Identifier> step,
    Optional<Identifier> attack,
    Optional<Identifier> alert,
    int ambientInterval
) {
    public static final int DEFAULT_AMBIENT_INTERVAL = 240;

    public static final SpeciesSoundDefinition EMPTY = new SpeciesSoundDefinition(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), DEFAULT_AMBIENT_INTERVAL);

    public static final Codec<SpeciesSoundDefinition> CODEC = RecordCodecBuilder.create(i -> i.group(
        SpeciesCodecs.VANILLA_ID.optionalFieldOf("ambient").forGetter(SpeciesSoundDefinition::ambient),
        SpeciesCodecs.VANILLA_ID.optionalFieldOf("hurt").forGetter(SpeciesSoundDefinition::hurt),
        SpeciesCodecs.VANILLA_ID.optionalFieldOf("death").forGetter(SpeciesSoundDefinition::death),
        SpeciesCodecs.VANILLA_ID.optionalFieldOf("step").forGetter(SpeciesSoundDefinition::step),
        SpeciesCodecs.VANILLA_ID.optionalFieldOf("attack").forGetter(SpeciesSoundDefinition::attack),
        SpeciesCodecs.VANILLA_ID.optionalFieldOf("alert").forGetter(SpeciesSoundDefinition::alert),
        ExtraCodecs.POSITIVE_INT.optionalFieldOf("ambient_interval", DEFAULT_AMBIENT_INTERVAL).forGetter(SpeciesSoundDefinition::ambientInterval)
    ).apply(i, SpeciesSoundDefinition::new));

    private static final StreamCodec<RegistryFriendlyByteBuf, Optional<Identifier>> OPTIONAL_ID =
        ByteBufCodecs.optional(Identifier.STREAM_CODEC);

    public static final StreamCodec<RegistryFriendlyByteBuf, SpeciesSoundDefinition> STREAM_CODEC = StreamCodec.composite(
        OPTIONAL_ID, SpeciesSoundDefinition::ambient,
        OPTIONAL_ID, SpeciesSoundDefinition::hurt,
        OPTIONAL_ID, SpeciesSoundDefinition::death,
        OPTIONAL_ID, SpeciesSoundDefinition::step,
        OPTIONAL_ID, SpeciesSoundDefinition::attack,
        OPTIONAL_ID, SpeciesSoundDefinition::alert,
        ByteBufCodecs.VAR_INT, SpeciesSoundDefinition::ambientInterval,
        SpeciesSoundDefinition::new
    );

    public @Nullable SoundEvent ambientSound() {
        return resolve(this.ambient);
    }

    public @Nullable SoundEvent hurtSound() {
        return resolve(this.hurt);
    }

    public @Nullable SoundEvent deathSound() {
        return resolve(this.death);
    }

    public @Nullable SoundEvent stepSound() {
        return resolve(this.step);
    }

    public @Nullable SoundEvent attackSound() {
        return resolve(this.attack);
    }

    public @Nullable SoundEvent alertSound() {
        return resolve(this.alert);
    }

    private static @Nullable SoundEvent resolve(Optional<Identifier> id) {
        return id.map(BuiltInRegistries.SOUND_EVENT::getValue).orElse(null);
    }
}
