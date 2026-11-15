package com.coolerpromc.ancientcreature.species;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

import java.util.Locale;

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
