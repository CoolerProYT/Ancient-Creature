package com.coolerpromc.ancientcreature.client.model.bedrock;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

/**
 * A Bedrock cube. {@code inflate} and {@code mirror} are optional because a missing value inherits the
 * bone's.
 */
public record BedrockCube(
    Vector3fc origin,
    Vector3fc size,
    Uv uv,
    Optional<Float> inflate,
    Optional<Boolean> mirror,
    Optional<Vector3fc> pivot,
    Vector3fc rotation
) {
    public boolean isRotated() {
        return this.rotation.x() != 0.0F || this.rotation.y() != 0.0F || this.rotation.z() != 0.0F;
    }

    public boolean isFinite() {
        return finite(this.origin) && finite(this.size) && this.pivot.map(BedrockCube::finite).orElse(true) && finite(this.rotation);
    }

    private static boolean finite(Vector3fc v) {
        return Float.isFinite(v.x()) && Float.isFinite(v.y()) && Float.isFinite(v.z());
    }

    /** Either a box-UV offset or a set of per-face rectangles. */
    public sealed interface Uv permits BoxUv, FaceUvs {
    }

    public record BoxUv(Vector2fc offset) implements Uv {
    }

    public record FaceUvs(Map<BedrockFace, FaceUv> faces) implements Uv {
    }

    /**
     * A per-face rectangle as written in the file: the face spans {@code uv} to {@code uv + size}, and a
     * negative size flips it. {@code rotation} is {@code uv_rotation} in degrees (0, 90, 180 or 270).
     */
    public record FaceUv(Vector2fc uv, Vector2fc size, int rotation, Optional<String> materialInstance) {
    }

    static BedrockCube parse(JsonElement element, String path) throws BedrockFormatException {
        JsonObject json = BedrockJson.object(element, path);
        Vector3f origin = BedrockJson.vec3(json, "origin", path);
        Vector3f size = BedrockJson.vec3(json, "size", path);
        if (origin == null) {
            origin = new Vector3f();
        }
        if (size == null) {
            size = new Vector3f();
        }

        Uv uv;
        JsonElement uvJson = BedrockJson.get(json, "uv");
        if (uvJson == null) {
            uv = new BoxUv(new Vector2f());
        } else if (uvJson.isJsonArray()) {
            uv = new BoxUv(BedrockJson.vec2(uvJson, path + ".uv"));
        } else if (uvJson.isJsonObject()) {
            Map<BedrockFace, FaceUv> faces = new EnumMap<>(BedrockFace.class);
            for (Map.Entry<String, JsonElement> entry : uvJson.getAsJsonObject().entrySet()) {
                BedrockFace face = BedrockFace.byName(entry.getKey());
                String facePath = path + ".uv." + entry.getKey();
                if (face == null) {
                    throw new BedrockFormatException(facePath + " is not a cube face (north, south, east, west, up, down)");
                }
                JsonObject faceJson = BedrockJson.object(entry.getValue(), facePath);
                JsonElement faceUv = BedrockJson.get(faceJson, "uv");
                if (faceUv == null) {
                    throw new BedrockFormatException(facePath + ".uv is required");
                }
                JsonElement faceSize = BedrockJson.get(faceJson, "uv_size");
                Vector2f sizeUv = faceSize != null ? BedrockJson.vec2(faceSize, facePath + ".uv_size") : defaultFaceSize(face, size);
                int rotation = Math.floorMod(Math.round(BedrockJson.number(faceJson, "uv_rotation", 0, facePath) / 90.0F) * 90, 360);
                String material = BedrockJson.string(faceJson, "material_instance", "");
                faces.put(face, new FaceUv(BedrockJson.vec2(faceUv, facePath + ".uv"), sizeUv, rotation, material.isEmpty() ? Optional.empty() : Optional.of(material)));
            }
            uv = new FaceUvs(Map.copyOf(faces));
        } else {
            throw new BedrockFormatException(path + ".uv must be [u, v] or an object of faces");
        }

        JsonElement inflate = BedrockJson.get(json, "inflate");
        JsonElement mirror = BedrockJson.get(json, "mirror");
        Vector3f pivot = BedrockJson.vec3(json, "pivot", path);
        Vector3f rotation = BedrockJson.vec3(json, "rotation", path);

        return new BedrockCube(origin, size, uv,
            inflate == null ? Optional.empty() : Optional.of(BedrockJson.number(inflate, path + ".inflate")),
            mirror == null ? Optional.empty() : Optional.of(BedrockJson.bool(json, "mirror", false)),
            Optional.ofNullable(pivot),
            rotation == null ? new Vector3f() : rotation);
    }

    /**
     * Bedrock lets {@code uv_size} be omitted; the face's own dimensions are used.
     */
    private static Vector2f defaultFaceSize(BedrockFace face, Vector3fc size) {
        return switch (face) {
            case NORTH, SOUTH -> new Vector2f(size.x(), size.y());
            case EAST, WEST -> new Vector2f(size.z(), size.y());
            case UP, DOWN -> new Vector2f(size.x(), size.z());
        };
    }
}
