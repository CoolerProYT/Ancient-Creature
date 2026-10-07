package com.coolerpromc.ancientcreature.client.model.bedrock;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.*;

/**
 * A Bedrock bone.
 *
 * <p>{@code mirror} and {@code inflate} on a bone are the defaults for its cubes, exactly as Bedrock and
 * Blockbench treat them.
 *
 * <p>{@code bind_pose_rotation} (legacy 1.8 files) turns only this bone's own geometry about its pivot;
 * unlike {@code rotation} it is not inherited by child bones. Vanilla's cow relies on this: its body has
 * a 90-degree bind pose while its legs and head, which are children of the body, stay upright.
 */
public record BedrockBone(
    String name,
    Optional<String> parent,
    Vector3fc pivot,
    Vector3fc rotation,
    Vector3fc bindPoseRotation,
    boolean mirror,
    float inflate,
    List<BedrockCube> cubes,
    Optional<BedrockPolyMesh> polyMesh,
    Map<String, BedrockLocator> locators,
    boolean neverRender,
    boolean reset
) {
    /** Lower-case name, the lookup key Bedrock uses. */
    public String key() {
        return this.name.toLowerCase(Locale.ROOT);
    }

    public boolean hasBindPose() {
        return this.bindPoseRotation.x() != 0.0F || this.bindPoseRotation.y() != 0.0F || this.bindPoseRotation.z() != 0.0F;
    }

    public boolean hasRotation() {
        return this.rotation.x() != 0.0F || this.rotation.y() != 0.0F || this.rotation.z() != 0.0F;
    }

    static BedrockBone parse(JsonElement element, String path) throws BedrockFormatException {
        JsonObject json = BedrockJson.object(element, path);
        String name = BedrockJson.string(json, "name", "");
        if (name.isEmpty()) {
            throw new BedrockFormatException(path + ".name is required");
        }
        String parent = BedrockJson.string(json, "parent", "");

        Vector3f pivot = BedrockJson.vec3(json, "pivot", path);
        Vector3f rotation = BedrockJson.vec3(json, "rotation", path);
        Vector3f bindPose = BedrockJson.vec3(json, "bind_pose_rotation", path);

        boolean mirror = BedrockJson.bool(json, "mirror", false);
        float inflate = BedrockJson.number(json, "inflate", 0.0F, path);

        List<BedrockCube> cubes = new ArrayList<>();
        JsonElement cubeArray = BedrockJson.get(json, "cubes");
        if (cubeArray != null) {
            if (!cubeArray.isJsonArray()) {
                throw new BedrockFormatException(path + ".cubes must be an array");
            }
            JsonArray array = cubeArray.getAsJsonArray();
            for (int i = 0; i < array.size(); i++) {
                cubes.add(BedrockCube.parse(array.get(i), path + ".cubes[" + i + "]"));
            }
        }

        JsonElement mesh = BedrockJson.get(json, "poly_mesh");
        Optional<BedrockPolyMesh> polyMesh = mesh == null ? Optional.empty() : Optional.of(BedrockPolyMesh.parse(mesh, path + ".poly_mesh"));

        Map<String, BedrockLocator> locators = new LinkedHashMap<>();
        JsonElement locatorJson = BedrockJson.get(json, "locators");
        if (locatorJson != null && locatorJson.isJsonObject()) {
            for (Map.Entry<String, JsonElement> entry : locatorJson.getAsJsonObject().entrySet()) {
                locators.put(entry.getKey().toLowerCase(Locale.ROOT), BedrockLocator.parse(entry.getValue(), path + ".locators." + entry.getKey()));
            }
        }

        return new BedrockBone(name,
            parent.isEmpty() ? Optional.empty() : Optional.of(parent),
            pivot == null ? new Vector3f() : pivot,
            rotation == null ? new Vector3f() : rotation,
            bindPose == null ? new Vector3f() : bindPose,
            mirror,
            inflate,
            List.copyOf(cubes),
            polyMesh,
            Map.copyOf(locators),
            BedrockJson.bool(json, "neverRender", false) || BedrockJson.bool(json, "never_render", false),
            BedrockJson.bool(json, "reset", false));
    }
}
