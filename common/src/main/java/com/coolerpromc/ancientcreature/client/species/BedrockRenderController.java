package com.coolerpromc.ancientcreature.client.species;

import com.coolerpromc.ancientcreature.client.model.bedrock.BedrockFormatException;
import com.coolerpromc.ancientcreature.client.model.bedrock.BedrockJson;
import com.coolerpromc.ancientcreature.molang.Molang;
import com.coolerpromc.ancientcreature.molang.MolangContext;
import com.coolerpromc.ancientcreature.molang.MolangExpression;
import com.coolerpromc.ancientcreature.molang.MolangResources;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.*;
import java.util.regex.Pattern;

/**
 * A Bedrock render controller: which geometry, texture layers and material to render, which bones are
 * visible, and colour tinting — each chosen by Molang at render time.
 *
 * <p>Reads {@code arrays} ({@code textures}, {@code geometries}, {@code materials}), {@code geometry},
 * {@code textures} (several entries render as stacked layers), {@code materials},
 * {@code part_visibility}, {@code color}, {@code overlay_color}, {@code is_hurt_color},
 * {@code on_fire_color} and {@code ignore_lighting}. Bone patterns accept {@code *} wildcards and are
 * applied in order, later rules winning, as in Bedrock.
 */
public record BedrockRenderController(
    String name,
    Map<String, Object[]> arrays,
    MolangExpression geometry,
    List<MolangExpression> textures,
    List<PatternRule> materials,
    List<PatternRule> partVisibility,
    Optional<Color> color,
    Optional<Color> overlayColor,
    Optional<Color> hurtColor,
    Optional<Color> onFireColor,
    boolean ignoreLighting
) implements MolangResources {
    public record PatternRule(String pattern, Pattern regex, MolangExpression value) {
        public boolean matches(String bone) {
            return this.regex.matcher(bone).matches();
        }
    }

    public record Color(MolangExpression r, MolangExpression g, MolangExpression b, MolangExpression a) {
        /** ARGB, each channel clamped to [0, 1]. */
        public int argb(MolangContext ctx) {
            return channel(this.a, ctx) << 24 | channel(this.r, ctx) << 16 | channel(this.g, ctx) << 8 | channel(this.b, ctx);
        }

        public float alpha(MolangContext ctx) {
            return (float) Math.max(0.0, Math.min(1.0, this.a.evaluate(ctx)));
        }

        private static int channel(MolangExpression expression, MolangContext ctx) {
            return (int) Math.round(Math.max(0.0, Math.min(1.0, expression.evaluate(ctx))) * 255.0);
        }
    }

    @Override
    public Object[] array(String name) {
        return this.arrays.get(name.toLowerCase(Locale.ROOT));
    }

    public static Map<String, BedrockRenderController> parseFile(JsonElement element) throws BedrockFormatException {
        JsonObject root = BedrockJson.object(element, "$");
        JsonElement controllers = BedrockJson.get(root, "render_controllers");
        if (controllers == null) {
            throw new BedrockFormatException("$.render_controllers is required");
        }
        Map<String, BedrockRenderController> out = new LinkedHashMap<>();
        for (Map.Entry<String, JsonElement> entry : BedrockJson.object(controllers, "$.render_controllers").entrySet()) {
            String path = "$.render_controllers." + entry.getKey();
            out.put(entry.getKey().toLowerCase(Locale.ROOT), parse(entry.getKey(), BedrockJson.object(entry.getValue(), path), path));
        }
        return out;
    }

    private static BedrockRenderController parse(String name, JsonObject json, String path) throws BedrockFormatException {
        Map<String, Object[]> arrays = new HashMap<>();
        JsonElement arraysJson = BedrockJson.get(json, "arrays");
        if (arraysJson != null) {
            for (Map.Entry<String, JsonElement> category : BedrockJson.object(arraysJson, path + ".arrays").entrySet()) {
                for (Map.Entry<String, JsonElement> array : BedrockJson.object(category.getValue(), path + ".arrays." + category.getKey()).entrySet()) {
                    String arrayName = array.getKey().toLowerCase(Locale.ROOT);
                    if (arrayName.startsWith("array.")) {
                        arrayName = arrayName.substring("array.".length());
                    }
                    List<Object> members = new ArrayList<>();
                    for (String member : BedrockJson.strings(array.getValue())) {
                        members.add(Molang.compileOr(member, MolangExpression.ZERO, path + ".arrays." + array.getKey()).evaluateObject(new MolangContext()));
                    }
                    arrays.put(arrayName, members.toArray());
                }
            }
        }

        List<MolangExpression> textures = new ArrayList<>();
        for (String texture : BedrockJson.strings(BedrockJson.get(json, "textures"))) {
            textures.add(Molang.compileOr(texture, MolangExpression.ZERO, path + ".textures"));
        }

        JsonElement geometry = BedrockJson.get(json, "geometry");
        return new BedrockRenderController(name, Map.copyOf(arrays),
            geometry == null ? Molang.compileOr("Geometry.default", MolangExpression.ZERO, path) : Molang.of(BedrockJson.molangSource(geometry), path + ".geometry"),
            List.copyOf(textures),
            rules(BedrockJson.get(json, "materials"), path + ".materials"),
            rules(BedrockJson.get(json, "part_visibility"), path + ".part_visibility"),
            color(BedrockJson.get(json, "color"), path + ".color"),
            color(BedrockJson.get(json, "overlay_color"), path + ".overlay_color"),
            color(BedrockJson.get(json, "is_hurt_color"), path + ".is_hurt_color"),
            color(BedrockJson.get(json, "on_fire_color"), path + ".on_fire_color"),
            BedrockJson.bool(json, "ignore_lighting", false));
    }

    private static List<PatternRule> rules(JsonElement element, String path) throws BedrockFormatException {
        List<PatternRule> out = new ArrayList<>();
        if (element == null) {
            return out;
        }
        if (!element.isJsonArray()) {
            throw new BedrockFormatException(path + " must be an array of {\"bone pattern\": value}");
        }
        JsonArray array = element.getAsJsonArray();
        for (int i = 0; i < array.size(); i++) {
            for (Map.Entry<String, JsonElement> entry : BedrockJson.object(array.get(i), path + "[" + i + "]").entrySet()) {
                out.add(new PatternRule(entry.getKey(), glob(entry.getKey()), Molang.of(BedrockJson.molangSource(entry.getValue()), path + "[" + i + "]." + entry.getKey())));
            }
        }
        return List.copyOf(out);
    }

    private static Pattern glob(String pattern) {
        StringBuilder regex = new StringBuilder();
        for (char c : pattern.toLowerCase(Locale.ROOT).toCharArray()) {
            if (c == '*') {
                regex.append(".*");
            } else {
                regex.append(Pattern.quote(String.valueOf(c)));
            }
        }
        return Pattern.compile(regex.toString());
    }

    private static Optional<Color> color(JsonElement element, String path) throws BedrockFormatException {
        if (element == null) {
            return Optional.empty();
        }
        JsonObject json = BedrockJson.object(element, path);
        return Optional.of(new Color(channel(json, "r", path), channel(json, "g", path), channel(json, "b", path), channel(json, "a", path)));
    }

    private static MolangExpression channel(JsonObject json, String key, String path) {
        JsonElement element = BedrockJson.get(json, key);
        return element == null ? (key.equals("a") ? MolangExpression.ONE : MolangExpression.ZERO) : Molang.of(BedrockJson.molangSource(element), path + "." + key);
    }
}
