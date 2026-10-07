package com.coolerpromc.ancientcreature.species;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;

import java.util.List;

/**
 * Marks a species as a hybrid and names the two species whose genomes make it. Read from {@code hybrid}:
 * <pre>{@code "hybrid": { "parents": ["ancientcreature:stegosaurus", "ancientcreature:triceratops"] }}</pre>
 *
 * <p>A hybrid has no fossils. It is made in the Embryogenesis Chamber from a completed genome of one
 * parent with a completed genome of the other in the donor slot; the order does not matter. Splicing
 * loses some fidelity, and hybrids are always sterile.
 */
public record SpeciesHybridProperties(Identifier first, Identifier second) {
    /** The share of the parents' average fidelity a spliced genome keeps. */
    public static final float SPLICE_FIDELITY = 0.85F;

    public static final Codec<SpeciesHybridProperties> CODEC = RecordCodecBuilder.create(i -> i.group(
        SpeciesCodecs.SPECIES_ID.listOf().comapFlatMap(
            list -> list.size() == 2 && !list.get(0).equals(list.get(1))
                ? DataResult.success(list)
                : DataResult.error(() -> "hybrid.parents must name two different species, got " + list),
            list -> list
        ).fieldOf("parents").forGetter(SpeciesHybridProperties::parents)
    ).apply(i, list -> new SpeciesHybridProperties(list.get(0), list.get(1))));

    public List<Identifier> parents() {
        return List.of(this.first, this.second);
    }

    public boolean madeFrom(Identifier a, Identifier b) {
        return (this.first.equals(a) && this.second.equals(b)) || (this.first.equals(b) && this.second.equals(a));
    }
}
