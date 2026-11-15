package com.coolerpromc.ancientcreature.species;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public record SpeciesRidingProperties(Ability ability, int cooldown, float power, boolean rideable) {
    public static final SpeciesRidingProperties DEFAULT = new SpeciesRidingProperties(Ability.NONE, 100, 1.0F, true);

    public enum Ability implements StringRepresentable {

        NONE,

        ROAR,

        CHARGE,

        BITE,

        TAIL_SWEEP,

        STOMP,

        POUNCE,

        DIVE;

        public static final Codec<Ability> CODEC = StringRepresentable.fromEnum(Ability::values);

        @Override
        public String getSerializedName() {
            return this.name().toLowerCase(Locale.ROOT);
        }
    }

    public static final Codec<SpeciesRidingProperties> CODEC = RecordCodecBuilder.create(i -> i.group(
        Ability.CODEC.optionalFieldOf("ability", DEFAULT.ability()).forGetter(SpeciesRidingProperties::ability),
        Codec.intRange(10, 12000).optionalFieldOf("cooldown", DEFAULT.cooldown()).forGetter(SpeciesRidingProperties::cooldown),
        Codec.floatRange(0.1F, 10.0F).optionalFieldOf("power", DEFAULT.power()).forGetter(SpeciesRidingProperties::power),
        Codec.BOOL.optionalFieldOf("rideable", true).forGetter(SpeciesRidingProperties::rideable)
    ).apply(i, SpeciesRidingProperties::new));
}
