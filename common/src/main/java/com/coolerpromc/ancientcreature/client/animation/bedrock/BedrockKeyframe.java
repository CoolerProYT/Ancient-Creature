package com.coolerpromc.ancientcreature.client.animation.bedrock;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.List;
import java.util.Optional;

public record BedrockKeyframe(float time, Vector3fc pre, Vector3fc post, Interpolation interpolation) {
    public enum Interpolation {
        LINEAR,
        CATMULL_ROM;

        public static Interpolation parse(String name) {
            return switch (name.toLowerCase(java.util.Locale.ROOT)) {
                case "catmullrom", "catmull_rom", "smooth", "bezier" -> CATMULL_ROM;
                default -> LINEAR;
            };
        }
    }

    private static final Codec<Vector3fc> SCALAR_OR_VECTOR = Codec.either(Codec.FLOAT, ExtraCodecs.VECTOR3F)
        .xmap(
            either -> either.map(scalar -> new Vector3f(scalar, scalar, scalar), vector -> vector),
            vector -> Either.right(vector)
        );

    private record Complex(Optional<Vector3fc> pre, Optional<Vector3fc> post, String lerpMode) {
        private static final Codec<Complex> CODEC = RecordCodecBuilder.create(i -> i.group(
            listOrValue().optionalFieldOf("pre").forGetter(Complex::pre),
            listOrValue().optionalFieldOf("post").forGetter(Complex::post),
            Codec.STRING.optionalFieldOf("lerp_mode", "linear").forGetter(Complex::lerpMode)
        ).apply(i, Complex::new));

        private static Codec<Vector3fc> listOrValue() {
            return Codec.either(SCALAR_OR_VECTOR, SCALAR_OR_VECTOR.listOf()).xmap(
                either -> either.map(v -> v, list -> list.isEmpty() ? new Vector3f() : list.getFirst()),
                Either::left
            );
        }
    }

    public static final Codec<ValueAtTime> VALUE_CODEC = Codec.either(SCALAR_OR_VECTOR, Complex.CODEC).xmap(
        either -> either.map(
            vector -> new ValueAtTime(vector, vector, Interpolation.LINEAR),
            complex -> {
                Vector3fc post = complex.post().orElseGet(() -> complex.pre().orElseGet(Vector3f::new));
                Vector3fc pre = complex.pre().orElse(post);
                return new ValueAtTime(pre, post, Interpolation.parse(complex.lerpMode()));
            }),
        value -> Either.left(value.post())
    );

    public record ValueAtTime(Vector3fc pre, Vector3fc post, Interpolation interpolation) {
    }

    public static BedrockKeyframe of(float time, ValueAtTime value) {
        return new BedrockKeyframe(time, value.pre(), value.post(), value.interpolation());
    }

    public static List<BedrockKeyframe> sorted(List<BedrockKeyframe> frames) {
        return frames.stream().sorted(java.util.Comparator.comparingDouble(BedrockKeyframe::time)).toList();
    }
}
