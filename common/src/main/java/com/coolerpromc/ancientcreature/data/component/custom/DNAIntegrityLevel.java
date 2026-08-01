package com.coolerpromc.ancientcreature.data.component.custom;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.storage.loot.IntRange;
import org.jspecify.annotations.Nullable;

import java.util.Locale;

public enum DNAIntegrityLevel implements StringRepresentable {
    DEGRADED("Degraded", UniformInt.of(1, 4)),
    PARTIAL("Partial", UniformInt.of(4, 8)),
    STABLE("Stable", UniformInt.of(8, 15)),
    PRESERVED_EMBRYO("Preserved Embryo", UniformInt.of(15, 30));

    public static final Codec<DNAIntegrityLevel> CODEC = StringRepresentable.fromEnum(DNAIntegrityLevel::values);
    public static final StreamCodec<RegistryFriendlyByteBuf, DNAIntegrityLevel> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(CODEC);

    final String name;
    final UniformInt genomeCompleteness;

    DNAIntegrityLevel(String name, UniformInt genomeCompleteness){
        this.name = name;
        this.genomeCompleteness = genomeCompleteness;
    }

    @Override
    public String getSerializedName() {
        return this.name.toLowerCase(Locale.ROOT).replace(" ", "_");
    }

    public float getGenomeCompleteness(RandomSource random) {
        return genomeCompleteness.sample(random) / 100f;
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
