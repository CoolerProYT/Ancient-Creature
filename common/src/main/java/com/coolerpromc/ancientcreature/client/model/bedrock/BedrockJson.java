package com.coolerpromc.ancientcreature.client.model.bedrock;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Lenient readers for the Bedrock resource-pack JSON formats.
 *
 * <p>Bedrock's own loader is forgiving — numbers may be strings, vectors may be scalars, keys are
 * case-insensitive in places — and files exported by Blockbench or written by hand rely on that.
 * These helpers accept the same variations and throw {@link BedrockFormatException} with a JSON path
 * when something is genuinely unusable.
 */
public final class BedrockJson {
    public static JsonObject object(JsonElement element, String path) throws BedrockFormatException {
        if (element == null || !element.isJsonObject()) {
            throw new BedrockFormatException(path + " must be an object");
        }
        return element.getAsJsonObject();
    }

    public static @Nullable JsonElement get(JsonObject object, String key) {
        JsonElement exact = object.get(key);
        if (exact != null) {
            return exact;
        }
        for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(key)) {
                return entry.getValue();
            }
        }
        return null;
    }

    public static String string(JsonObject object, String key, String fallback) {
        JsonElement element = get(object, key);
        return element != null && element.isJsonPrimitive() ? element.getAsString() : fallback;
    }

    public static boolean bool(JsonObject object, String key, boolean fallback) {
        JsonElement element = get(object, key);
        if (element == null || !element.isJsonPrimitive()) {
            return fallback;
        }
        JsonPrimitive primitive = element.getAsJsonPrimitive();
        if (primitive.isBoolean()) {
            return primitive.getAsBoolean();
        }
        if (primitive.isNumber()) {
            return primitive.getAsDouble() != 0.0;
        }
        return Boolean.parseBoolean(primitive.getAsString().trim());
    }

    public static float number(JsonObject object, String key, float fallback, String path) throws BedrockFormatException {
        JsonElement element = get(object, key);
        return element == null ? fallback : number(element, path + "." + key);
    }

    public static float number(JsonElement element, String path) throws BedrockFormatException {
        if (element != null && element.isJsonPrimitive()) {
            JsonPrimitive primitive = element.getAsJsonPrimitive();
            if (primitive.isNumber()) {
                return primitive.getAsFloat();
            }
            if (primitive.isString()) {
                try {
                    return Float.parseFloat(primitive.getAsString().trim());
                } catch (NumberFormatException ignored) {
                    // fall through
                }
            }
            if (primitive.isBoolean()) {
                return primitive.getAsBoolean() ? 1.0F : 0.0F;
            }
        }
        throw new BedrockFormatException(path + " must be a number");
    }

    public static @Nullable Vector3f vec3(JsonObject object, String key, String path) throws BedrockFormatException {
        JsonElement element = get(object, key);
        return element == null ? null : vec3(element, path + "." + key);
    }

    public static Vector3f vec3(JsonElement element, String path) throws BedrockFormatException {
        if (element.isJsonArray()) {
            JsonArray array = element.getAsJsonArray();
            if (array.size() == 3) {
                return new Vector3f(number(array.get(0), path + "[0]"), number(array.get(1), path + "[1]"), number(array.get(2), path + "[2]"));
            }
            if (array.size() == 1) {
                float v = number(array.get(0), path + "[0]");
                return new Vector3f(v, v, v);
            }
            throw new BedrockFormatException(path + " must have 3 components, got " + array.size());
        }
        float v = number(element, path);
        return new Vector3f(v, v, v);
    }

    public static Vector2f vec2(JsonElement element, String path) throws BedrockFormatException {
        if (element.isJsonArray() && element.getAsJsonArray().size() == 2) {
            JsonArray array = element.getAsJsonArray();
            return new Vector2f(number(array.get(0), path + "[0]"), number(array.get(1), path + "[1]"));
        }
        throw new BedrockFormatException(path + " must be a [u, v] pair");
    }

    /** A Molang-or-number value as source text, for {@link com.coolerpromc.ancientcreature.molang.Molang#of}. */
    public static Object molangSource(JsonElement element) {
        if (element.isJsonPrimitive()) {
            JsonPrimitive primitive = element.getAsJsonPrimitive();
            if (primitive.isNumber()) {
                return primitive.getAsDouble();
            }
            if (primitive.isBoolean()) {
                return primitive.getAsBoolean();
            }
            return primitive.getAsString();
        }
        return element.toString();
    }

    /** A string or an array of strings, flattened. Bedrock script blocks accept both. */
    public static List<String> strings(@Nullable JsonElement element) {
        List<String> out = new ArrayList<>();
        if (element == null || element.isJsonNull()) {
            return out;
        }
        if (element.isJsonArray()) {
            for (JsonElement entry : element.getAsJsonArray()) {
                if (entry.isJsonPrimitive()) {
                    out.add(entry.getAsString());
                }
            }
        } else if (element.isJsonPrimitive()) {
            out.add(element.getAsString());
        }
        return out;
    }

    public static String lower(String text) {
        return text.toLowerCase(Locale.ROOT);
    }

    private BedrockJson() {
    }
}
