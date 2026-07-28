package com.coolerpromc.ancientcreature.data.component.custom;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;

public enum FossilPart implements StringRepresentable {
    RIB("rib"),
    TOOTH("tooth"),
    SKULL("skull"),
    VERTEBRA("vertebra"),
    LIMB("limb"),
    CLAW("claw");

    public static final Codec<FossilPart> CODEC = StringRepresentable.fromEnum(FossilPart::values);
    public static final StreamCodec<RegistryFriendlyByteBuf, FossilPart> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(CODEC);

    final String name;

    FossilPart(String name){
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}
