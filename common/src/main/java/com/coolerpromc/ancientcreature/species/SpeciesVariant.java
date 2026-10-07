package com.coolerpromc.ancientcreature.species;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;

import java.util.List;

/**
 * A skin variant a species can hatch with. The server rolls it by weight; the client entity maps the
 * id to a texture (by texture name, or through a render controller reading {@code query.variant},
 * which is the variant's position in this list).
 */
public record SpeciesVariant(String id, int weight) {
    public static final String DEFAULT_ID = "default";

    public static final Codec<SpeciesVariant> CODEC = RecordCodecBuilder.create(i -> i.group(
        Codec.STRING.fieldOf("id").forGetter(SpeciesVariant::id),
        ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("weight", 1).forGetter(SpeciesVariant::weight)
    ).apply(i, SpeciesVariant::new));

    /** Rolls a variant id by weight; {@code "default"} when the list is empty or all weights are zero. */
    public static String roll(List<SpeciesVariant> variants, RandomSource random) {
        int total = 0;
        for (SpeciesVariant variant : variants) {
            total += variant.weight();
        }
        if (total <= 0) {
            return variants.isEmpty() ? DEFAULT_ID : variants.getFirst().id();
        }
        int roll = random.nextInt(total);
        for (SpeciesVariant variant : variants) {
            roll -= variant.weight();
            if (roll < 0) {
                return variant.id();
            }
        }
        return variants.getLast().id();
    }
}
