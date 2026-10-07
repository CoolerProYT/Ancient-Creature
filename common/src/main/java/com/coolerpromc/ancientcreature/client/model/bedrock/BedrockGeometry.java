package com.coolerpromc.ancientcreature.client.model.bedrock;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.*;

/**
 * One {@code geometry.*} definition from a Bedrock {@code .geo.json} file.
 *
 * <p>Both file layouts Bedrock accepts are read:
 * <ul>
 *   <li>1.12.0 and later: {@code "minecraft:geometry": [ { "description": {...}, "bones": [...] } ]}</li>
 *   <li>1.8.0 / 1.10.0 legacy: {@code "geometry.name": {...}} or
 *       {@code "geometry.name:geometry.parent": {...}}, including parent inheritance within the file</li>
 * </ul>
 * Coordinates are kept exactly as written in the file; {@link BedrockModelBaker} converts them.
 */
public record BedrockGeometry(String identifier, int textureWidth, int textureHeight, float visibleBoundsWidth, float visibleBoundsHeight, Vector3fc visibleBoundsOffset, List<BedrockBone> bones) {

    public static List<BedrockGeometry> parseFile(JsonElement json) throws BedrockFormatException {
        JsonObject root = BedrockJson.object(json, "$");
        JsonElement modern = BedrockJson.get(root, "minecraft:geometry");
        List<BedrockGeometry> out = new ArrayList<>();

        if (modern != null) {
            if (!modern.isJsonArray()) {
                throw new BedrockFormatException("$.minecraft:geometry must be an array");
            }
            JsonArray array = modern.getAsJsonArray();
            for (int i = 0; i < array.size(); i++) {
                String path = "$.minecraft:geometry[" + i + "]";
                JsonObject entry = BedrockJson.object(array.get(i), path);
                JsonObject description = BedrockJson.object(BedrockJson.get(entry, "description"), path + ".description");
                String identifier = BedrockJson.string(description, "identifier", "");
                if (identifier.isEmpty()) {
                    throw new BedrockFormatException(path + ".description.identifier is required");
                }
                out.add(new BedrockGeometry(identifier,
                    (int) BedrockJson.number(description, "texture_width", 64, path),
                    (int) BedrockJson.number(description, "texture_height", 64, path),
                    BedrockJson.number(description, "visible_bounds_width", 1, path),
                    BedrockJson.number(description, "visible_bounds_height", 1, path),
                    orZero(BedrockJson.vec3(description, "visible_bounds_offset", path)),
                    parseBones(BedrockJson.get(entry, "bones"), path + ".bones")));
            }
            return out;
        }

        // Legacy layout: every "geometry.*" key is a model, optionally inheriting from another.
        Map<String, JsonObject> bodies = new LinkedHashMap<>();
        Map<String, String> parents = new HashMap<>();
        for (Map.Entry<String, JsonElement> entry : root.entrySet()) {
            String key = entry.getKey();
            if (!key.toLowerCase(Locale.ROOT).startsWith("geometry.") || !entry.getValue().isJsonObject()) {
                continue;
            }
            int colon = key.indexOf(':');
            String name = colon >= 0 ? key.substring(0, colon) : key;
            if (colon >= 0) {
                parents.put(name, key.substring(colon + 1));
            }
            bodies.put(name, entry.getValue().getAsJsonObject());
        }
        if (bodies.isEmpty()) {
            throw new BedrockFormatException("$ has neither 'minecraft:geometry' nor any legacy 'geometry.*' entries");
        }

        Map<String, BedrockGeometry> resolved = new LinkedHashMap<>();
        for (String name : bodies.keySet()) {
            resolveLegacy(name, bodies, parents, resolved, new HashSet<>());
        }
        out.addAll(resolved.values());
        return out;
    }

    private static BedrockGeometry resolveLegacy(String name, Map<String, JsonObject> bodies, Map<String, String> parents, Map<String, BedrockGeometry> resolved, Set<String> visiting) throws BedrockFormatException {
        BedrockGeometry done = resolved.get(name);
        if (done != null) {
            return done;
        }
        if (!visiting.add(name)) {
            throw new BedrockFormatException("$." + name + " inherits from itself");
        }
        JsonObject body = bodies.get(name);
        String path = "$." + name;
        BedrockGeometry parent = null;
        String parentName = parents.get(name);
        if (parentName != null) {
            if (!bodies.containsKey(parentName)) {
                throw new BedrockFormatException(path + " inherits from '" + parentName + "', which is not in this file");
            }
            parent = resolveLegacy(parentName, bodies, parents, resolved, visiting);
        }

        List<BedrockBone> bones = parseBones(BedrockJson.get(body, "bones"), path + ".bones");
        if (parent != null) {
            // Child bones replace same-named parent bones; the rest of the parent is inherited.
            Map<String, BedrockBone> merged = new LinkedHashMap<>();
            for (BedrockBone bone : parent.bones()) {
                merged.put(bone.name().toLowerCase(Locale.ROOT), bone);
            }
            for (BedrockBone bone : bones) {
                merged.put(bone.name().toLowerCase(Locale.ROOT), bone);
            }
            bones = List.copyOf(merged.values());
        }

        BedrockGeometry geometry = new BedrockGeometry(name,
            (int) BedrockJson.number(body, "texturewidth", parent != null ? parent.textureWidth() : 64, path),
            (int) BedrockJson.number(body, "textureheight", parent != null ? parent.textureHeight() : 64, path),
            BedrockJson.number(body, "visible_bounds_width", 1, path),
            BedrockJson.number(body, "visible_bounds_height", 1, path),
            orZero(BedrockJson.vec3(body, "visible_bounds_offset", path)),
            bones);
        resolved.put(name, geometry);
        return geometry;
    }

    private static List<BedrockBone> parseBones(JsonElement element, String path) throws BedrockFormatException {
        if (element == null) {
            return List.of();
        }
        if (!element.isJsonArray()) {
            throw new BedrockFormatException(path + " must be an array");
        }
        List<BedrockBone> bones = new ArrayList<>();
        JsonArray array = element.getAsJsonArray();
        for (int i = 0; i < array.size(); i++) {
            bones.add(BedrockBone.parse(array.get(i), path + "[" + i + "]"));
        }
        return List.copyOf(bones);
    }

    private static Vector3fc orZero(Vector3f v) {
        return v == null ? new Vector3f() : v;
    }

    /**
     * This geometry as a file written for Ancient Creature before it followed Bedrock's axes would have
     * to be written today, so that a species pack from that era renders exactly as it used to.
     *
     * <p>Earlier releases read X un-mirrored, with X and Y rotations inverted, and laid box UV out with
     * fractional sizes. The conversion mirrors X, inverts those rotations and writes fractional box-UV
     * cubes as the equivalent per-face UV.
     */
    public BedrockGeometry fromLegacyAxes() {
        List<BedrockBone> converted = new ArrayList<>();
        for (BedrockBone bone : this.bones) {
            List<BedrockCube> cubes = new ArrayList<>();
            for (BedrockCube cube : bone.cubes()) {
                cubes.add(legacyCube(cube));
            }
            converted.add(new BedrockBone(bone.name(), bone.parent(), mirrorX(bone.pivot()), legacyRotation(bone.rotation()),
                legacyRotation(bone.bindPoseRotation()), bone.mirror(), bone.inflate(), List.copyOf(cubes), bone.polyMesh(), bone.locators(),
                bone.neverRender(), bone.reset()));
        }
        return new BedrockGeometry(this.identifier, this.textureWidth, this.textureHeight, this.visibleBoundsWidth, this.visibleBoundsHeight, this.visibleBoundsOffset, List.copyOf(converted));
    }

    private static Vector3fc mirrorX(Vector3fc v) {
        return new Vector3f(-v.x(), v.y(), v.z());
    }

    private static Vector3fc legacyRotation(Vector3fc r) {
        return new Vector3f(-r.x(), -r.y(), r.z());
    }

    private static BedrockCube legacyCube(BedrockCube cube) {
        Vector3fc o = cube.origin();
        Vector3fc size = cube.size();
        BedrockCube.Uv uv = cube.uv();
        if (uv instanceof BedrockCube.BoxUv box) {
            float u = (float) Math.floor(box.offset().x());
            float v = (float) Math.floor(box.offset().y());
            boolean whole = isWhole(size.x()) && isWhole(size.y()) && isWhole(size.z());
            if (whole) {
                uv = new BedrockCube.BoxUv(new org.joml.Vector2f(u, v));
            } else {
                // The old layout used the fractional sizes; keep exactly that texture mapping.
                float w = size.x();
                float h = size.y();
                float d = size.z();
                boolean mirror = cube.mirror().orElse(false);
                Map<BedrockFace, BedrockCube.FaceUv> faces = new EnumMap<>(BedrockFace.class);
                faces.put(BedrockFace.EAST, face(u, v + d, d, h));
                faces.put(BedrockFace.NORTH, face(u + d, v + d, w, h));
                faces.put(BedrockFace.WEST, face(u + d + w, v + d, d, h));
                faces.put(BedrockFace.SOUTH, face(u + 2 * d + w, v + d, w, h));
                faces.put(BedrockFace.UP, face(u + d, v, w, d));
                faces.put(BedrockFace.DOWN, face(u + d + w, v + d, w, -d));
                if (mirror) {
                    // Old mirrored cubes: each face flipped horizontally, east and west swapped.
                    Map<BedrockFace, BedrockCube.FaceUv> flipped = new EnumMap<>(BedrockFace.class);
                    faces.forEach((key, f) -> flipped.put(key, face(f.uv().x() + f.size().x(), f.uv().y(), -f.size().x(), f.size().y())));
                    BedrockCube.FaceUv east = flipped.get(BedrockFace.EAST);
                    flipped.put(BedrockFace.EAST, flipped.get(BedrockFace.WEST));
                    flipped.put(BedrockFace.WEST, east);
                    faces = flipped;
                }
                uv = new BedrockCube.FaceUvs(Map.copyOf(faces));
            }
        }
        return new BedrockCube(new Vector3f(-(o.x() + size.x()), o.y(), o.z()), size, uv, cube.inflate(),
            uv instanceof BedrockCube.FaceUvs ? Optional.of(false) : cube.mirror(),
            cube.pivot().map(BedrockGeometry::mirrorX), legacyRotation(cube.rotation()));
    }

    private static BedrockCube.FaceUv face(float u, float v, float w, float h) {
        return new BedrockCube.FaceUv(new org.joml.Vector2f(u, v), new org.joml.Vector2f(w, h), 0, Optional.empty());
    }

    private static boolean isWhole(float value) {
        return Math.abs(value - Math.round(value)) < 1.0E-6F;
    }

    /** Problems that make the model unrenderable or ambiguous. An empty list means it is fine to bake. */
    public List<String> validate() {
        List<String> problems = new ArrayList<>();
        if (!this.identifier.toLowerCase(Locale.ROOT).startsWith("geometry.")) {
            problems.add("identifier '" + this.identifier + "' must start with 'geometry.'");
        }
        if (this.textureWidth <= 0 || this.textureHeight <= 0) {
            problems.add("texture size must be positive, got " + this.textureWidth + "x" + this.textureHeight);
        }

        Map<String, BedrockBone> byName = new LinkedHashMap<>();
        for (BedrockBone bone : this.bones) {
            if (byName.put(bone.key(), bone) != null) {
                problems.add("duplicate bone '" + bone.name() + "'");
            }
        }
        for (BedrockBone bone : this.bones) {
            bone.parent().ifPresent(parent -> {
                if (!byName.containsKey(parent.toLowerCase(Locale.ROOT))) {
                    problems.add("bone '" + bone.name() + "' has unknown parent '" + parent + "'");
                }
            });
            for (int c = 0; c < bone.cubes().size(); c++) {
                BedrockCube cube = bone.cubes().get(c);
                if (!cube.isFinite()) {
                    problems.add("bone '" + bone.name() + "' cube " + c + " has a non-finite origin, size or pivot");
                }
            }
        }
        problems.addAll(findCycles(byName));
        return problems;
    }

    private static List<String> findCycles(Map<String, BedrockBone> byName) {
        List<String> problems = new ArrayList<>();
        Set<String> reported = new HashSet<>();
        for (BedrockBone bone : byName.values()) {
            Set<String> seen = new HashSet<>();
            Deque<String> chain = new ArrayDeque<>();
            String current = bone.key();
            while (current != null) {
                if (!seen.add(current)) {
                    String cycle = String.join(" -> ", chain) + " -> " + current;
                    if (reported.add(cycle)) {
                        problems.add("cyclic bone parenting: " + cycle);
                    }
                    break;
                }
                chain.addLast(current);
                BedrockBone node = byName.get(current);
                current = node == null ? null : node.parent().map(p -> p.toLowerCase(Locale.ROOT)).orElse(null);
            }
        }
        return problems;
    }

    /** Bones by lower-case name. Bedrock bone names are case-insensitive. */
    public Map<String, BedrockBone> boneMap() {
        Map<String, BedrockBone> map = new LinkedHashMap<>();
        for (BedrockBone bone : this.bones) {
            map.putIfAbsent(bone.key(), bone);
        }
        return map;
    }

    /** Children keyed by lower-case parent name; roots are under {@code ""}. Bones with a dangling parent are roots. */
    public Map<String, List<BedrockBone>> childrenByParent() {
        Map<String, BedrockBone> known = this.boneMap();
        Map<String, List<BedrockBone>> map = new LinkedHashMap<>();
        for (BedrockBone bone : this.bones) {
            String parent = bone.parent().map(p -> p.toLowerCase(Locale.ROOT)).filter(known::containsKey).orElse("");
            map.computeIfAbsent(parent, key -> new ArrayList<>()).add(bone);
        }
        return map;
    }
}
