package com.coolerpromc.ancientcreature.species;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.StringRepresentable;

import java.util.Locale;

/**
 * What a ridden creature can do when its rider presses the mount ability key. Read from {@code riding}:
 * <pre>{@code "riding": { "ability": "tail_sweep", "cooldown": 80, "power": 1.0 }}</pre>
 * {@code power} scales damage and reach; {@code cooldown} is in ticks.
 */
public record SpeciesRidingProperties(Ability ability, int cooldown, float power) {
    public static final SpeciesRidingProperties DEFAULT = new SpeciesRidingProperties(Ability.NONE, 100, 1.0F);

    public enum Ability implements StringRepresentable {
        /** Nothing. */
        NONE,
        /** A terrifying call: nearby hostile mobs flee and are weakened. */
        ROAR,
        /** A short sprint that rams and throws everything ahead. */
        CHARGE,
        /** A crushing bite on the creature directly ahead. */
        BITE,
        /** A sweep of the tail that hits everything behind and beside. */
        TAIL_SWEEP,
        /** A ground-shaking stomp that hurts and slows everything close by. */
        STOMP,
        /** A leap forward that lands on whatever is ahead. */
        POUNCE,
        /** A steep swoop that strikes what is below and ahead (flyers), or a burst of speed (swimmers). */
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
        Codec.floatRange(0.1F, 10.0F).optionalFieldOf("power", DEFAULT.power()).forGetter(SpeciesRidingProperties::power)
    ).apply(i, SpeciesRidingProperties::new));
}
