package com.coolerpromc.ancientcreature.client.model.bedrock;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public record BedrockLocator(Vector3fc offset, Vector3fc rotation) {
    static BedrockLocator parse(JsonElement element, String path) throws BedrockFormatException {
        if (element.isJsonArray()) {
            return new BedrockLocator(BedrockJson.vec3(element, path), new Vector3f());
        }
        JsonObject json = BedrockJson.object(element, path);
        Vector3f offset = BedrockJson.vec3(json, "offset", path);
        Vector3f rotation = BedrockJson.vec3(json, "rotation", path);
        return new BedrockLocator(offset == null ? new Vector3f() : offset, rotation == null ? new Vector3f() : rotation);
    }
}
