package com.coolerpromc.ancientcreature.species;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

import java.util.Locale;

/**
 * How a species breathes.
 *
 * <ul>
 *   <li>{@code air}: drowns underwater like any land animal.</li>
 *   <li>{@code water}: breathes through gills; stranded on land it thrashes, dries out and suffocates,
 *       like vanilla fish and squid.</li>
 *   <li>{@code amphibious}: never runs out of air in either; marine reptiles that surface to breathe are
 *       modelled this way because creature AI does not surface on its own.</li>
 * </ul>
 * When a species file leaves it out, aquatic species are {@code water} and everything else {@code air}.
 */
public enum SpeciesRespiration implements StringRepresentable {
    AIR, WATER, AMPHIBIOUS;

    public static final Codec<SpeciesRespiration> CODEC = StringRepresentable.fromEnum(SpeciesRespiration::values);

    @Override
    public String getSerializedName() {
        return this.name().toLowerCase(Locale.ROOT);
    }

    public static SpeciesRespiration defaultFor(SpeciesEntityCategory category) {
        return category.isAquatic() ? WATER : AIR;
    }
}
