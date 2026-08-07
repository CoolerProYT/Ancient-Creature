package com.coolerpromc.ancientcreature.client.model.bedrock;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.List;
import java.util.Optional;

public record BedrockBone(String name, Optional<String> parent, Vector3fc pivot, Optional<Vector3fc> rotation, List<BedrockCube> cubes, boolean neverRender) {
    private static final Vector3fc ORIGIN = new Vector3f(0.0F, 0.0F, 0.0F);

    public static final Codec<BedrockBone> CODEC = RecordCodecBuilder.create(i -> i.group(
        Codec.STRING.fieldOf("name").forGetter(BedrockBone::name),
        Codec.STRING.optionalFieldOf("parent").forGetter(BedrockBone::parent),
        ExtraCodecs.VECTOR3F.optionalFieldOf("pivot", ORIGIN).forGetter(BedrockBone::pivot),
        ExtraCodecs.VECTOR3F.optionalFieldOf("rotation").forGetter(BedrockBone::rotation),
        BedrockCube.CODEC.listOf().optionalFieldOf("cubes", List.of()).forGetter(BedrockBone::cubes),
        Codec.BOOL.optionalFieldOf("neverRender", false).forGetter(BedrockBone::neverRender)
    ).apply(i, BedrockBone::new));

    public float rotationX() {
        return this.rotation.map(Vector3fc::x).orElse(0.0F);
    }

    public float rotationY() {
        return this.rotation.map(Vector3fc::y).orElse(0.0F);
    }

    public float rotationZ() {
        return this.rotation.map(Vector3fc::z).orElse(0.0F);
    }
}
