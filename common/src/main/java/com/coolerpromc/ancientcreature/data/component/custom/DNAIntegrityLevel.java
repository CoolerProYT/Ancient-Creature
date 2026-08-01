package com.coolerpromc.ancientcreature.data.component.custom;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import org.jspecify.annotations.Nullable;

import java.util.Locale;

public enum DNAIntegrityLevel implements StringRepresentable {
    DEGRADED("Degraded"),
    PARTIAL("Partial"),
    STABLE("Stable"),
    PRESERVED_EMBRYO("Preserved Embryo");

    public static final Codec<DNAIntegrityLevel> CODEC = StringRepresentable.fromEnum(DNAIntegrityLevel::values);
    public static final StreamCodec<RegistryFriendlyByteBuf, DNAIntegrityLevel> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(CODEC);

    final String name;

    DNAIntegrityLevel(String name){
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return this.name.toLowerCase(Locale.ROOT).replace(" ", "_");
    }

    public static @Nullable DNAIntegrityLevel byScore(float score){
        if (score < 0.05f){
            return null;
        }
        if (score <= 0.15f){
            return DEGRADED;
        }
        if (score <= 0.30f){
            return PARTIAL;
        }
        if (score <= 0.60f){
            return STABLE;
        }
        return PRESERVED_EMBRYO;
    }
}
