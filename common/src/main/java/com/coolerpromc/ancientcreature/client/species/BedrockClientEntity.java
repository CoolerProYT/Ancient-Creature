package com.coolerpromc.ancientcreature.client.species;

import com.coolerpromc.ancientcreature.client.model.bedrock.BedrockFormatException;
import com.coolerpromc.ancientcreature.client.model.bedrock.BedrockJson;
import com.coolerpromc.ancientcreature.molang.Molang;
import com.coolerpromc.ancientcreature.molang.MolangExpression;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.Identifier;

import java.util.*;

/**
 * A Bedrock client entity definition ({@code "minecraft:client_entity"}): which geometry, textures,
 * materials, animations, scripts and render controllers an entity uses.
 *
 * <p>Read from {@code assets/<ns>/ancientcreature/entity/*.json}. The {@code description.identifier} is
 * the species id it dresses, e.g. {@code ancientcreature:triceratops}. Texture paths are written the
 * Bedrock way — {@code "textures/entity/triceratops"}, relative to the pack root and without the
 * extension — and resolve inside the file's own namespace unless they name one explicitly.
 */
public record BedrockClientEntity(
    Identifier identifier,
    Map<String, String> materials,
    Map<String, Identifier> textures,
    Map<String, String> geometry,
    Map<String, String> animations,
    Scripts scripts,
    List<ControllerRef> renderControllers,
    Map<String, String> soundEffects,
    Map<String, String> particleEffects
) {
    public record Scripts(List<MolangExpression> initialize, List<MolangExpression> preAnimation, List<AnimateEntry> animate, Optional<MolangExpression> scale, Optional<MolangExpression> scaleX, Optional<MolangExpression> scaleY, Optional<MolangExpression> scaleZ) {
        public static final Scripts EMPTY = new Scripts(List.of(), List.of(), List.of(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
    }

    /** One entry of {@code scripts.animate}: a short animation or controller name and its blend weight. */
    public record AnimateEntry(String name, MolangExpression weight) {
    }

    public record ControllerRef(String name, MolangExpression condition) {
    }

    public static List<BedrockClientEntity> parseFile(JsonElement element, String namespace) throws BedrockFormatException {
        JsonObject root = BedrockJson.object(element, "$");
        JsonElement body = BedrockJson.get(root, "minecraft:client_entity");
        if (body == null) {
            throw new BedrockFormatException("$ has no 'minecraft:client_entity'");
        }
        JsonObject description = BedrockJson.object(BedrockJson.get(BedrockJson.object(body, "$.minecraft:client_entity"), "description"), "$.minecraft:client_entity.description");
        String path = "$.minecraft:client_entity.description";

        String id = BedrockJson.string(description, "identifier", "");
        Identifier identifier = Identifier.tryParse(id);
        if (id.isEmpty() || identifier == null) {
            throw new BedrockFormatException(path + ".identifier must be a namespaced id such as 'ancientcreature:triceratops'");
        }

        Map<String, Identifier> textures = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : stringMap(description, "textures", path).entrySet()) {
            textures.put(entry.getKey(), texturePath(entry.getValue(), namespace, path + ".textures." + entry.getKey()));
        }

        Scripts scripts = Scripts.EMPTY;
        JsonElement scriptsJson = BedrockJson.get(description, "scripts");
        if (scriptsJson != null) {
            JsonObject s = BedrockJson.object(scriptsJson, path + ".scripts");
            List<AnimateEntry> animate = new ArrayList<>();
            JsonElement animateJson = BedrockJson.get(s, "animate");
            if (animateJson != null) {
                if (!animateJson.isJsonArray()) {
                    throw new BedrockFormatException(path + ".scripts.animate must be an array");
                }
                for (JsonElement entry : animateJson.getAsJsonArray()) {
                    if (entry.isJsonPrimitive()) {
                        animate.add(new AnimateEntry(entry.getAsString(), MolangExpression.ONE));
                    } else if (entry.isJsonObject()) {
                        for (Map.Entry<String, JsonElement> e : entry.getAsJsonObject().entrySet()) {
                            animate.add(new AnimateEntry(e.getKey(), Molang.of(BedrockJson.molangSource(e.getValue()), path + ".scripts.animate." + e.getKey())));
                        }
                    }
                }
            }
            scripts = new Scripts(
                compileAll(BedrockJson.get(s, "initialize"), path + ".scripts.initialize"),
                compileAll(BedrockJson.get(s, "pre_animation"), path + ".scripts.pre_animation"),
                List.copyOf(animate),
                optional(s, "scale", path), optional(s, "scalex", path), optional(s, "scaley", path), optional(s, "scalez", path));
        }

        List<ControllerRef> renderControllers = new ArrayList<>();
        JsonElement rcJson = BedrockJson.get(description, "render_controllers");
        if (rcJson != null && rcJson.isJsonArray()) {
            for (JsonElement entry : rcJson.getAsJsonArray()) {
                if (entry.isJsonPrimitive()) {
                    renderControllers.add(new ControllerRef(entry.getAsString(), MolangExpression.ONE));
                } else if (entry.isJsonObject()) {
                    for (Map.Entry<String, JsonElement> e : entry.getAsJsonObject().entrySet()) {
                        renderControllers.add(new ControllerRef(e.getKey(), Molang.of(BedrockJson.molangSource(e.getValue()), path + ".render_controllers." + e.getKey())));
                    }
                }
            }
        }

        Map<String, String> sounds = new LinkedHashMap<>();
        JsonElement soundJson = BedrockJson.get(description, "sound_effects");
        if (soundJson != null && soundJson.isJsonObject()) {
            for (Map.Entry<String, JsonElement> entry : soundJson.getAsJsonObject().entrySet()) {
                // Bedrock allows "name" or {"sound": "name"}.
                String sound = entry.getValue().isJsonObject() ? BedrockJson.string(entry.getValue().getAsJsonObject(), "sound", "") : entry.getValue().getAsString();
                if (!sound.isEmpty()) {
                    sounds.put(entry.getKey().toLowerCase(Locale.ROOT), sound);
                }
            }
        }

        Map<String, String> animations = new LinkedHashMap<>();
        stringMap(description, "animations", path).forEach((k, v) -> animations.put(k.toLowerCase(Locale.ROOT), v));
        Map<String, String> particles = new LinkedHashMap<>();
        stringMap(description, "particle_effects", path).forEach((k, v) -> particles.put(k.toLowerCase(Locale.ROOT), v));

        List<BedrockClientEntity> out = new ArrayList<>();
        out.add(new BedrockClientEntity(identifier,
            Map.copyOf(lowerKeys(stringMap(description, "materials", path))),
            Collections.unmodifiableMap(lowerKeys(textures)),
            Collections.unmodifiableMap(lowerKeys(stringMap(description, "geometry", path))),
            Collections.unmodifiableMap(animations),
            scripts,
            List.copyOf(renderControllers),
            Collections.unmodifiableMap(sounds),
            Collections.unmodifiableMap(particles)));
        return out;
    }

    /** {@code textures/entity/foo} → {@code ns:textures/entity/foo.png}; explicit namespaces are respected. */
    public static Identifier texturePath(String raw, String namespace, String path) throws BedrockFormatException {
        String value = raw.trim();
        String ns = namespace;
        int colon = value.indexOf(':');
        if (colon >= 0) {
            ns = value.substring(0, colon);
            value = value.substring(colon + 1);
        }
        if (!value.endsWith(".png")) {
            value = value + ".png";
        }
        Identifier id = Identifier.tryBuild(ns, value);
        if (id == null) {
            throw new BedrockFormatException(path + " '" + raw + "' is not a valid texture path");
        }
        return id;
    }

    private static <V> Map<String, V> lowerKeys(Map<String, V> in) {
        Map<String, V> out = new LinkedHashMap<>();
        in.forEach((k, v) -> out.put(k.toLowerCase(Locale.ROOT), v));
        return out;
    }

    private static Map<String, String> stringMap(JsonObject json, String key, String path) throws BedrockFormatException {
        Map<String, String> out = new LinkedHashMap<>();
        JsonElement element = BedrockJson.get(json, key);
        if (element == null) {
            return out;
        }
        for (Map.Entry<String, JsonElement> entry : BedrockJson.object(element, path + "." + key).entrySet()) {
            if (entry.getValue().isJsonPrimitive()) {
                out.put(entry.getKey(), entry.getValue().getAsString());
            }
        }
        return out;
    }

    private static List<MolangExpression> compileAll(JsonElement element, String path) {
        List<MolangExpression> out = new ArrayList<>();
        for (String source : BedrockJson.strings(element)) {
            out.add(Molang.compileOr(source, MolangExpression.ZERO, path));
        }
        return List.copyOf(out);
    }

    private static Optional<MolangExpression> optional(JsonObject json, String key, String path) {
        JsonElement element = BedrockJson.get(json, key);
        return element == null ? Optional.empty() : Optional.of(Molang.of(BedrockJson.molangSource(element), path + ".scripts." + key));
    }
}
