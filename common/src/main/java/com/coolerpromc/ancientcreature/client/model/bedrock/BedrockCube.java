package com.coolerpromc.ancientcreature.client.model.bedrock;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Direction;
import net.minecraft.util.ExtraCodecs;
import org.joml.Vector2fc;
import org.joml.Vector3fc;

import java.util.Map;
import java.util.Optional;

public record BedrockCube(Vector3fc origin, Vector3fc size, CubeUv uv, float inflate, boolean mirror, Optional<Vector3fc> pivot, Optional<Vector3fc> rotation) {
    public static final Codec<BedrockCube> CODEC = RecordCodecBuilder.create(i -> i.group(
        ExtraCodecs.VECTOR3F.fieldOf("origin").forGetter(BedrockCube::origin),
        ExtraCodecs.VECTOR3F.fieldOf("size").forGetter(BedrockCube::size),
        CubeUv.CODEC.optionalFieldOf("uv", CubeUv.ZERO).forGetter(BedrockCube::uv),
        Codec.FLOAT.optionalFieldOf("inflate", 0.0F).forGetter(BedrockCube::inflate),
        Codec.BOOL.optionalFieldOf("mirror", false).forGetter(BedrockCube::mirror),
        ExtraCodecs.VECTOR3F.optionalFieldOf("pivot").forGetter(BedrockCube::pivot),
        ExtraCodecs.VECTOR3F.optionalFieldOf("rotation").forGetter(BedrockCube::rotation)
    ).apply(i, BedrockCube::new));

    public boolean isRotated() {
        return this.rotation.isPresent() && (this.rotation.get().x() != 0.0F || this.rotation.get().y() != 0.0F || this.rotation.get().z() != 0.0F);
    }

    public boolean hasValidSize() {
        return this.size.x() >= 0.0F && this.size.y() >= 0.0F && this.size.z() >= 0.0F && Float.isFinite(this.size.x()) && Float.isFinite(this.size.y()) && Float.isFinite(this.size.z());
    }

    public record CubeUv(Optional<Vector2fc> box, Map<Direction, FaceUv> faces) {
        public static final CubeUv ZERO = new CubeUv(Optional.empty(), Map.of());

        private static final Codec<Map<Direction, FaceUv>> FACES_CODEC = Codec.unboundedMap(Direction.CODEC, FaceUv.CODEC);

        public static final Codec<CubeUv> CODEC = Codec.either(ExtraCodecs.VECTOR2F, FACES_CODEC).xmap(
            either -> either.map(box -> new CubeUv(Optional.of(box), Map.of()), faces -> new CubeUv(Optional.empty(), faces)),
            uv -> uv.box().<Either<Vector2fc, Map<Direction, FaceUv>>>map(Either::left).orElseGet(() -> Either.right(uv.faces()))
        );

        public boolean isPerFace() {
            return this.box.isEmpty() && !this.faces.isEmpty();
        }


        public float u() {
            return this.box.map(Vector2fc::x).orElseGet(() -> (float) this.faces.values().stream().mapToDouble(f -> f.uv().x()).min().orElse(0.0));
        }

        public float v() {
            return this.box.map(Vector2fc::y).orElseGet(() -> (float) this.faces.values().stream().mapToDouble(f -> f.uv().y()).min().orElse(0.0));
        }
    }

    public record FaceUv(Vector2fc uv, Optional<Vector2fc> uvSize) {
        public static final Codec<FaceUv> CODEC = RecordCodecBuilder.create(i -> i.group(
            ExtraCodecs.VECTOR2F.fieldOf("uv").forGetter(FaceUv::uv),
            ExtraCodecs.VECTOR2F.optionalFieldOf("uv_size").forGetter(FaceUv::uvSize)
        ).apply(i, FaceUv::new));
    }
}
