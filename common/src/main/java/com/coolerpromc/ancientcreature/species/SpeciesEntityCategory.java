package com.coolerpromc.ancientcreature.species;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;

public enum SpeciesEntityCategory implements StringRepresentable {
    LAND("land"),
    AQUATIC("aquatic"),
    FLYING("flying");

    public static final Codec<SpeciesEntityCategory> CODEC = StringRepresentable.fromEnum(SpeciesEntityCategory::values);
    public static final StreamCodec<RegistryFriendlyByteBuf, SpeciesEntityCategory> STREAM_CODEC =
        ByteBufCodecs.fromCodecWithRegistries(CODEC);

    private final String name;

    SpeciesEntityCategory(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    public boolean isAquatic() {
        return this == AQUATIC;
    }

    public boolean isFlying() {
        return this == FLYING;
    }
}
