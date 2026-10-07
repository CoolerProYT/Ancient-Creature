package com.coolerpromc.ancientcreature.bedrock;

import com.coolerpromc.ancientcreature.client.animation.bedrock.BedrockAnimation;
import com.coolerpromc.ancientcreature.client.model.bedrock.BedrockGeometry;
import com.coolerpromc.ancientcreature.client.model.bedrock.BedrockModelBaker;
import com.coolerpromc.ancientcreature.molang.MolangContext;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.SharedConstants;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.server.Bootstrap;
import net.minecraft.util.Mth;
import org.joml.Vector3f;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Species packs written for releases before Ancient Creature followed Bedrock's axes (species
 * {@code format_version} 1) are converted on load. The conversion must reproduce exactly what the old
 * renderer drew. The old renderer is reproduced here verbatim and compared on random models that
 * exercise everything it handled: nested and rotated bones, rotated cubes with and without their own
 * pivot, fractional sizes, mirroring and inflation.
 */
class LegacyAxesTest {
    @BeforeAll
    static void boot() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void convertedGeometryRendersLikeTheOldBaker() throws Exception {
        Random random = new Random(7);
        for (int model = 0; model < 40; model++) {
            JsonObject geometry = randomGeometry(random);
            String file = "{\"format_version\":\"1.12.0\",\"minecraft:geometry\":[" + geometry + "]}";
            BedrockGeometry parsed = BedrockGeometry.parseFile(JsonParser.parseString(file)).getFirst();
            BedrockGeometryTest.assertSamePolygons(
                BedrockGeometryTest.polygons(legacyBake(geometry)),
                BedrockGeometryTest.polygons(BedrockModelBaker.bake(parsed.fromLegacyAxes()).root()),
                "random model " + model);
        }
    }

    @Test
    void convertedAnimationsPoseLikeTheOldApplier() throws Exception {
        String json = """
            { "format_version": "1.8.0", "animations": { "animation.t.a": { "loop": true, "bones": { "b": {
              "rotation": { "0.0": [10, 20, 30], "1.0": [-5, "q.anim_time * 4", 12] },
              "position": [3, 4, 5], "scale": 1.5 } } } } }""";
        BedrockAnimation original = BedrockAnimation.parseFile(JsonParser.parseString(json)).get("animation.t.a");
        BedrockAnimation converted = original.fromLegacyAxes();
        MolangContext ctx = new MolangContext();
        for (float t = 0; t <= 1.0F; t += 0.1F) {
            Vector3f o = original.bones().get("b").rotation().sample(t, ctx, new Vector3f());
            Vector3f n = converted.bones().get("b").rotation().sample(t, ctx, new Vector3f());
            // old applier added (x, -y, z); the new one adds (x, y, z)
            assertEquals(o.x, n.x, 1e-4);
            assertEquals(-o.y, n.y, 1e-4);
            assertEquals(o.z, n.z, 1e-4);
        }
        Vector3f o = original.bones().get("b").position().sample(0, ctx, new Vector3f());
        Vector3f n = converted.bones().get("b").position().sample(0, ctx, new Vector3f());
        // old applier added (-x, -y, z); the new one adds (x, -y, z)
        assertEquals(-o.x, n.x, 1e-4);
        assertEquals(o.y, n.y, 1e-4);
        assertEquals(o.z, n.z, 1e-4);
    }

    // ------------------------------------------------------------------ random models

    private static float r(Random random, float lo, float hi, boolean whole) {
        float v = lo + random.nextFloat() * (hi - lo);
        return whole ? Math.round(v) : Math.round(v * 100) / 100.0F;
    }

    private static JsonArray vec(float... v) {
        JsonArray a = new JsonArray();
        for (float f : v) {
            a.add(f);
        }
        return a;
    }

    private static JsonObject randomGeometry(Random random) {
        JsonObject description = new JsonObject();
        description.addProperty("identifier", "geometry.random");
        description.addProperty("texture_width", 128);
        description.addProperty("texture_height", 128);
        JsonArray bones = new JsonArray();
        int count = 2 + random.nextInt(4);
        for (int b = 0; b < count; b++) {
            JsonObject bone = new JsonObject();
            bone.addProperty("name", "bone" + b);
            if (b > 0) {
                bone.addProperty("parent", "bone" + random.nextInt(b));
            }
            bone.add("pivot", vec(r(random, -8, 8, false), r(random, 0, 20, false), r(random, -8, 8, false)));
            if (random.nextBoolean()) {
                bone.add("rotation", vec(r(random, -60, 60, false), r(random, -60, 60, false), r(random, -60, 60, false)));
            }
            JsonArray cubes = new JsonArray();
            for (int c = 0; c < 1 + random.nextInt(3); c++) {
                boolean whole = random.nextBoolean();
                JsonObject cube = new JsonObject();
                cube.add("origin", vec(r(random, -8, 8, whole), r(random, 0, 20, whole), r(random, -8, 8, whole)));
                cube.add("size", vec(r(random, 1, 8, whole), r(random, 1, 8, whole), r(random, 1, 8, whole)));
                cube.add("uv", vec(random.nextInt(60), random.nextInt(60)));
                if (random.nextInt(3) == 0) {
                    cube.addProperty("mirror", true);
                }
                if (random.nextInt(4) == 0) {
                    cube.addProperty("inflate", r(random, -0.4F, 0.6F, false));
                }
                if (random.nextBoolean()) {
                    cube.add("rotation", vec(r(random, -45, 45, false), r(random, -45, 45, false), r(random, -45, 45, false)));
                    if (random.nextBoolean()) {
                        cube.add("pivot", vec(r(random, -8, 8, false), r(random, 0, 20, false), r(random, -8, 8, false)));
                    }
                }
                cubes.add(cube);
            }
            bone.add("cubes", cubes);
            bones.add(bone);
        }
        JsonObject geometry = new JsonObject();
        geometry.add("description", description);
        geometry.add("bones", bones);
        return geometry;
    }

    // ------------------------------------------------------------------ the old baker, as it shipped

    private static ModelPart legacyBake(JsonObject geometry) {
        JsonObject description = geometry.getAsJsonObject("description");
        Map<String, List<JsonObject>> children = new HashMap<>();
        for (JsonElement element : geometry.getAsJsonArray("bones")) {
            JsonObject bone = element.getAsJsonObject();
            children.computeIfAbsent(bone.has("parent") ? bone.get("parent").getAsString() : "", k -> new ArrayList<>()).add(bone);
        }
        MeshDefinition mesh = new MeshDefinition();
        for (JsonObject bone : children.getOrDefault("", List.of())) {
            add(bone, null, mesh.getRoot(), children);
        }
        return LayerDefinition.create(mesh, description.get("texture_width").getAsInt(), description.get("texture_height").getAsInt()).bakeRoot();
    }

    private static float[] get(JsonObject o, String key, float... fallback) {
        if (!o.has(key)) {
            return fallback;
        }
        JsonArray a = o.getAsJsonArray(key);
        float[] out = new float[a.size()];
        for (int i = 0; i < a.size(); i++) {
            out[i] = a.get(i).getAsFloat();
        }
        return out;
    }

    private static void add(JsonObject bone, JsonObject parent, PartDefinition parentPart, Map<String, List<JsonObject>> children) {
        float[] pivot = get(bone, "pivot", 0, 0, 0);
        float[] pp = parent == null ? new float[]{0, 0, 0} : get(parent, "pivot", 0, 0, 0);
        float[] rot = get(bone, "rotation", 0, 0, 0);
        PartPose pose = PartPose.offsetAndRotation(-(pivot[0] - pp[0]), -(pivot[1] - pp[1]) + (parent == null ? 24 : 0), pivot[2] - pp[2],
            -rot[0] * Mth.DEG_TO_RAD, -rot[1] * Mth.DEG_TO_RAD, rot[2] * Mth.DEG_TO_RAD);
        CubeListBuilder cubes = CubeListBuilder.create();
        List<JsonObject> rotated = new ArrayList<>();
        for (JsonElement e : bone.getAsJsonArray("cubes")) {
            JsonObject cube = e.getAsJsonObject();
            float[] r = get(cube, "rotation", 0, 0, 0);
            if (r[0] != 0 || r[1] != 0 || r[2] != 0) {
                rotated.add(cube);
            } else {
                append(cube, pivot, cubes);
            }
        }
        String name = bone.get("name").getAsString();
        PartDefinition part = parentPart.addOrReplaceChild(name, cubes, pose);
        for (int i = 0; i < rotated.size(); i++) {
            JsonObject cube = rotated.get(i);
            float[] cp = get(cube, "pivot", pivot);
            float[] r = get(cube, "rotation");
            CubeListBuilder b = CubeListBuilder.create();
            append(cube, cp, b);
            part.addOrReplaceChild(name + "_r" + (i + 1), b, PartPose.offsetAndRotation(-(cp[0] - pivot[0]), -(cp[1] - pivot[1]), cp[2] - pivot[2],
                -r[0] * Mth.DEG_TO_RAD, -r[1] * Mth.DEG_TO_RAD, r[2] * Mth.DEG_TO_RAD));
        }
        for (JsonObject child : children.getOrDefault(name, List.of())) {
            add(child, bone, part, children);
        }
    }

    private static void append(JsonObject cube, float[] pivot, CubeListBuilder builder) {
        float[] origin = get(cube, "origin");
        float[] size = get(cube, "size");
        float[] uv = get(cube, "uv", 0, 0);
        float inflate = cube.has("inflate") ? cube.get("inflate").getAsFloat() : 0;
        boolean mirror = cube.has("mirror") && cube.get("mirror").getAsBoolean();
        builder.texOffs(Mth.floor(uv[0]), Mth.floor(uv[1])).mirror(mirror)
            .addBox(pivot[0] - origin[0] - size[0], pivot[1] - origin[1] - size[1], origin[2] - pivot[2], size[0], size[1], size[2], new CubeDeformation(inflate))
            .mirror(false);
    }
}
