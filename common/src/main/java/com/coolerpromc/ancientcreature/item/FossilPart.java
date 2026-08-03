package com.coolerpromc.ancientcreature.item;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;

public enum FossilPart implements StringRepresentable {
    RIB("rib", 0.04f),
    TOOTH("tooth", 0.16f),
    SKULL("skull", 0.12f),
    VERTEBRA("vertebra", 0.08f),
    LIMB("limb", 0.0f),
    CLAW("claw", -0.04f);

    public static final Codec<FossilPart> CODEC = StringRepresentable.fromEnum(FossilPart::values);
    public static final StreamCodec<RegistryFriendlyByteBuf, FossilPart> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(CODEC);

    final String name;
    final float dnaExtractingBonus;

    FossilPart(String name, float dnaExtractingBonus){
        this.name = name;
        this.dnaExtractingBonus = dnaExtractingBonus;
    }

    @Override
    public String getSerializedName() {
        return name;
    }

    public float getDnaExtractingBonus() {
        return dnaExtractingBonus;
    }
}
