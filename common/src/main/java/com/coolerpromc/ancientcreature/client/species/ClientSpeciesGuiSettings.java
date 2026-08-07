package com.coolerpromc.ancientcreature.client.species;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public record ClientSpeciesGuiSettings(float scale, Vector3fc translation, Vector3fc rotation) {
    public static final ClientSpeciesGuiSettings DEFAULT = new ClientSpeciesGuiSettings(12.0F, new Vector3f(0.0F, 0.0F, 0.0F), new Vector3f(0.0F, 215.0F, 0.0F));

    public static final Codec<ClientSpeciesGuiSettings> CODEC = RecordCodecBuilder.create(i -> i.group(
        ExtraCodecs.floatRange(Float.MIN_NORMAL, 512.0F).optionalFieldOf("scale", DEFAULT.scale()).forGetter(ClientSpeciesGuiSettings::scale),
        ExtraCodecs.VECTOR3F.optionalFieldOf("translation", DEFAULT.translation()).forGetter(ClientSpeciesGuiSettings::translation),
        ExtraCodecs.VECTOR3F.optionalFieldOf("rotation", DEFAULT.rotation()).forGetter(ClientSpeciesGuiSettings::rotation)
    ).apply(i, ClientSpeciesGuiSettings::new));
}
