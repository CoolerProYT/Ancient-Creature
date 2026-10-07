package com.coolerpromc.ancientcreature.bedrock;

import com.coolerpromc.ancientcreature.client.model.bedrock.*;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.SharedConstants;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.server.Bootstrap;
import org.joml.Vector3f;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pins the geometry loader to real Bedrock behaviour.
 *
 * <p>The anchor is ground truth from the game itself: Mojang's legacy Bedrock cow geometry and Java
 * Edition's cow model are the same model, so baking the former must give exactly the latter, vertex
 * for vertex and UV for UV. That single comparison fixes the axis conventions, the legacy file format,
 * {@code bind_pose_rotation}, bone hierarchy offsets, box UV and {@code mirror}. The remaining tests
 * check the parts the cow does not use against equivalences Blockbench guarantees.
 */
class BedrockGeometryTest {
    private static final float EPS = 1.0E-4F;

    @BeforeAll
    static void boot() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    /** The cow as Bedrock ships it (legacy 1.8 format, values from Mojang's bedrock-samples). */
    private static final String BEDROCK_COW = """
        { "format_version": "1.8.0", "geometry.cow.v1.8": {
          "texturewidth": 64, "textureheight": 32,
          "bones": [
            { "name": "body", "pivot": [0, 19, 2], "bind_pose_rotation": [90, 0, 0], "cubes": [
              { "origin": [-6, 11, -5], "size": [12, 18, 10], "uv": [18, 4] },
              { "origin": [-2, 11, -6], "size": [4, 6, 1], "uv": [52, 0] } ] },
            { "name": "head", "parent": "body", "pivot": [0, 20, -8], "locators": { "lead": [0, 20, -8] }, "cubes": [
              { "origin": [-4, 16, -14], "size": [8, 8, 6], "uv": [0, 0] },
              { "origin": [-5, 22, -12], "size": [1, 3, 1], "uv": [22, 0] },
              { "origin": [4, 22, -12], "size": [1, 3, 1], "uv": [22, 0] } ] },
            { "name": "leg0", "parent": "body", "pivot": [-4, 12, 7], "cubes": [ { "origin": [-6, 0, 5], "size": [4, 12, 4], "uv": [0, 16] } ] },
            { "name": "leg1", "parent": "body", "mirror": true, "pivot": [4, 12, 7], "cubes": [ { "origin": [2, 0, 5], "size": [4, 12, 4], "uv": [0, 16] } ] },
            { "name": "leg2", "parent": "body", "pivot": [-4, 12, -6], "cubes": [ { "origin": [-6, 0, -7], "size": [4, 12, 4], "uv": [0, 16] } ] },
            { "name": "leg3", "parent": "body", "mirror": true, "pivot": [4, 12, -6], "cubes": [ { "origin": [2, 0, -7], "size": [4, 12, 4], "uv": [0, 16] } ] }
          ] } }
        """;

    /** Java Edition's cow, as vanilla builds it, with the same cube data as the Bedrock file. */
    private static ModelPart javaCow() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-4.0F, -4.0F, -6.0F, 8.0F, 8.0F, 6.0F)
                .texOffs(22, 0).addBox(-5.0F, -5.0F, -4.0F, 1.0F, 3.0F, 1.0F)
                .texOffs(22, 0).addBox(4.0F, -5.0F, -4.0F, 1.0F, 3.0F, 1.0F),
            PartPose.offset(0.0F, 4.0F, -8.0F));
        root.addOrReplaceChild("body", CubeListBuilder.create()
                .texOffs(18, 4).addBox(-6.0F, -10.0F, -7.0F, 12.0F, 18.0F, 10.0F)
                .texOffs(52, 0).addBox(-2.0F, 2.0F, -8.0F, 4.0F, 6.0F, 1.0F),
            PartPose.offsetAndRotation(0.0F, 5.0F, 2.0F, (float) (Math.PI / 2), 0.0F, 0.0F));
        CubeListBuilder leftLeg = CubeListBuilder.create().mirror().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F);
        CubeListBuilder rightLeg = CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F);
        root.addOrReplaceChild("right_hind_leg", rightLeg, PartPose.offset(-4.0F, 12.0F, 7.0F));
        root.addOrReplaceChild("left_hind_leg", leftLeg, PartPose.offset(4.0F, 12.0F, 7.0F));
        root.addOrReplaceChild("right_front_leg", rightLeg, PartPose.offset(-4.0F, 12.0F, -5.0F));
        root.addOrReplaceChild("left_front_leg", leftLeg, PartPose.offset(4.0F, 12.0F, -5.0F));
        return LayerDefinition.create(mesh, 64, 32).bakeRoot();
    }

    private static BedrockGeometry parse(String json) throws BedrockFormatException {
        List<BedrockGeometry> geometries = BedrockGeometry.parseFile(JsonParser.parseString(json));
        assertEquals(1, geometries.size());
        assertTrue(geometries.getFirst().validate().isEmpty(), () -> String.join("; ", geometries.getFirst().validate()));
        return geometries.getFirst();
    }

    @Test
    void bedrockCowBakesExactlyIntoJavaCow() throws Exception {
        BakedBedrockModel baked = BedrockModelBaker.bake(parse(BEDROCK_COW));
        assertSamePolygons(polygons(javaCow()), polygons(baked.root()), "cow");
        assertNotNull(baked.locator("lead"), "locators are kept");
        assertNotNull(baked.bone("LEG0"), "bone lookup is case-insensitive");
    }

    @ParameterizedTest(name = "mirror={0}")
    @ValueSource(booleans = {false, true})
    void boxUvMatchesVanillaCubesForWholeTexelSizes(boolean mirror) throws Exception {
        Random random = new Random(42);
        for (int n = 0; n < 50; n++) {
            int w = 1 + random.nextInt(9), h = 1 + random.nextInt(9), d = 1 + random.nextInt(9);
            int u = random.nextInt(20), v = random.nextInt(20);
            float ox = random.nextInt(9) - 4, oy = random.nextInt(9), oz = random.nextInt(9) - 4;
            String json = cube(ox, oy, oz, w, h, d, "[" + u + "," + v + "]", mirror);
            MeshDefinition mesh = new MeshDefinition();
            mesh.getRoot().addOrReplaceChild("bone", CubeListBuilder.create().texOffs(u, v).mirror(mirror)
                .addBox(ox - 0, 0 - oy - h, oz - 0, w, h, d), PartPose.offset(0, 24, 0));
            assertSamePolygons(polygons(LayerDefinition.create(mesh, 64, 64).bakeRoot()),
                polygons(BedrockModelBaker.bake(parse(json)).root()), json);
        }
    }

    /**
     * A cube written with per-face UV the way Blockbench exports a box-UV cube must look identical to
     * the box-UV cube, for fractional sizes too (where Bedrock floors the box layout).
     */
    @Test
    void perFaceUvEquivalentToBoxUv() throws Exception {
        float w = 4, h = 3, d = 2;
        int u = 8, v = 5;
        String box = cube(-2, 0, -1, w, h, d, "[" + u + "," + v + "]", false);
        String faces = cube(-2, 0, -1, w, h, d, """
            { "east": {"uv": [8, 7], "uv_size": [2, 3]}, "north": {"uv": [10, 7], "uv_size": [4, 3]},
              "west": {"uv": [14, 7], "uv_size": [2, 3]}, "south": {"uv": [16, 7], "uv_size": [4, 3]},
              "up": {"uv": [10, 5], "uv_size": [4, 2]}, "down": {"uv": [14, 7], "uv_size": [4, -2]} }""", false);
        assertSamePolygons(polygons(BedrockModelBaker.bake(parse(box)).root()), polygons(BedrockModelBaker.bake(parse(faces)).root()), "per-face");
    }

    @Test
    void boxUvLayoutFloorsFractionalSizes() throws Exception {
        String fractional = cube(0, 0, 0, 4.6F, 3.9F, 2.2F, "[0,0]", false);
        String whole = cube(0, 0, 0, 4.6F, 3.9F, 2.2F, """
            { "east": {"uv": [0, 2], "uv_size": [2, 3]}, "north": {"uv": [2, 2], "uv_size": [4, 3]},
              "west": {"uv": [6, 2], "uv_size": [2, 3]}, "south": {"uv": [8, 2], "uv_size": [4, 3]},
              "up": {"uv": [2, 0], "uv_size": [4, 2]}, "down": {"uv": [6, 2], "uv_size": [4, -2]} }""", false);
        assertSamePolygons(polygons(BedrockModelBaker.bake(parse(whole)).root()), polygons(BedrockModelBaker.bake(parse(fractional)).root()), "floored");
    }

    @Test
    void uvRotation180FlipsBothAxes() throws Exception {
        String rotated = cube(0, 0, 0, 4, 4, 4, "{ \"north\": {\"uv\": [0, 0], \"uv_size\": [4, 4], \"uv_rotation\": 180} }", false);
        String flipped = cube(0, 0, 0, 4, 4, 4, "{ \"north\": {\"uv\": [4, 4], \"uv_size\": [-4, -4]} }", false);
        assertSamePolygons(polygons(BedrockModelBaker.bake(parse(flipped)).root()), polygons(BedrockModelBaker.bake(parse(rotated)).root()), "rot180");

        String quarter = cube(0, 0, 0, 4, 4, 4, "{ \"north\": {\"uv\": [0, 0], \"uv_size\": [4, 4], \"uv_rotation\": 90} }", false);
        String twice = cube(0, 0, 0, 4, 4, 4, "{ \"north\": {\"uv\": [0, 0], \"uv_size\": [4, 4], \"uv_rotation\": 450} }", false);
        assertSamePolygons(polygons(BedrockModelBaker.bake(parse(quarter)).root()), polygons(BedrockModelBaker.bake(parse(twice)).root()), "rot90 == rot450");
    }

    @Test
    void rotatedCubesRotateAboutTheirPivotInsideTheBone() throws Exception {
        // A 2x2x2 cube centred on its pivot, turned 90° about Y, occupies the same box.
        String turned = """
            { "format_version": "1.12.0", "minecraft:geometry": [ { "description": { "identifier": "geometry.t", "texture_width": 16, "texture_height": 16 },
              "bones": [ { "name": "b", "pivot": [0, 0, 0], "cubes": [ { "origin": [-1, 0, -1], "size": [2, 2, 2], "pivot": [0, 1, 0], "rotation": [0, 90, 0], "uv": [0, 0] } ] } ] } ] }""";
        BakedBedrockModel model = BedrockModelBaker.bake(parse(turned));
        assertEquals(1, model.root().getAllParts().size() - 1, "rotated cubes are baked into their bone, not extra parts");
        float[] bounds = bounds(polygons(model.root()));
        assertArrayEquals(new float[]{-1 / 16F, 22 / 16F, -1 / 16F, 1 / 16F, 24 / 16F, 1 / 16F}, bounds, EPS);
    }

    @Test
    void legacyInheritanceMergesParentBones() throws Exception {
        String json = """
            { "format_version": "1.8.0",
              "geometry.base": { "texturewidth": 32, "textureheight": 32, "bones": [
                { "name": "body", "pivot": [0, 0, 0], "cubes": [ { "origin": [0, 0, 0], "size": [1, 1, 1], "uv": [0, 0] } ] },
                { "name": "tail", "pivot": [0, 0, 0] } ] },
              "geometry.child:geometry.base": { "bones": [
                { "name": "tail", "pivot": [0, 0, 0], "cubes": [ { "origin": [0, 0, 2], "size": [1, 1, 3], "uv": [0, 8] } ] } ] } }""";
        Map<String, BedrockGeometry> byName = new HashMap<>();
        for (BedrockGeometry g : BedrockGeometry.parseFile(JsonParser.parseString(json))) {
            byName.put(g.identifier(), g);
        }
        BedrockGeometry child = byName.get("geometry.child");
        assertEquals(32, child.textureWidth(), "texture size is inherited");
        assertEquals(2, child.bones().size());
        assertEquals(1, child.boneMap().get("tail").cubes().size(), "the child's bone replaces the parent's");
        assertEquals(1, child.boneMap().get("body").cubes().size(), "parent bones are inherited");
    }

    @Test
    void polyMeshesBake() throws Exception {
        String json = """
            { "format_version": "1.12.0", "minecraft:geometry": [ { "description": { "identifier": "geometry.m", "texture_width": 16, "texture_height": 16 },
              "bones": [ { "name": "b", "pivot": [0, 0, 0], "poly_mesh": { "normalized_uvs": true,
                "positions": [[0,0,0],[1,0,0],[1,1,0],[0,1,0]], "normals": [[0,0,-1]], "uvs": [[0,0],[1,0],[1,1],[0,1]],
                "polys": [ [[0,0,0],[1,0,1],[2,0,2],[3,0,3]], [[0,0,0],[1,0,1],[2,0,2]] ] } } ] } ] }""";
        List<float[][]> polygons = polygons(BedrockModelBaker.bake(parse(json)).root());
        assertEquals(2, polygons.size(), "a quad and a triangle");
        assertEquals(4, polygons.get(1).length, "triangles draw as degenerate quads");
    }

    @Test
    void structuralProblemsAreReported() throws Exception {
        String cyclic = """
            { "format_version": "1.12.0", "minecraft:geometry": [ { "description": { "identifier": "geometry.c" },
              "bones": [ { "name": "a", "parent": "b" }, { "name": "b", "parent": "a" }, { "name": "c" }, { "name": "C" } ] } ] }""";
        BedrockGeometry geometry = BedrockGeometry.parseFile(JsonParser.parseString(cyclic)).getFirst();
        List<String> problems = geometry.validate();
        assertTrue(problems.stream().anyMatch(p -> p.contains("duplicate")), problems::toString);
        assertTrue(problems.stream().anyMatch(p -> p.contains("cyclic")), problems::toString);
        assertThrows(BedrockFormatException.class, () -> BedrockGeometry.parseFile(JsonParser.parseString("{\"minecraft:geometry\": [ {\"description\": {}} ]}")));
        assertThrows(BedrockFormatException.class, () -> BedrockGeometry.parseFile(JsonParser.parseString("{\"nothing\": 1}")));
    }

    @Test
    void numbersMayBeStringsAsBedrockAllows() throws Exception {
        String json = cube(0, 0, 0, 2, 2, 2, "[0,0]", false).replace("\"size\": [2.0, 2.0, 2.0]", "\"size\": [\"2\", \"2\", \"2\"]");
        assertEquals(2.0F, parse(json).bones().getFirst().cubes().getFirst().size().x());
    }

    // ------------------------------------------------------------------ helpers

    private static String cube(float ox, float oy, float oz, float w, float h, float d, String uv, boolean mirror) {
        return String.format(Locale.ROOT, """
            { "format_version": "1.12.0", "minecraft:geometry": [ { "description": { "identifier": "geometry.test", "texture_width": 64, "texture_height": 64 },
              "bones": [ { "name": "bone", "pivot": [0.0, 0.0, 0.0], "cubes": [ { "origin": [%s, %s, %s], "size": [%s, %s, %s], "uv": %s, "mirror": %s } ] } ] } ] }""",
            ox, oy, oz, w, h, d, uv, mirror);
    }

    static List<float[][]> polygons(ModelPart root) {
        List<float[][]> out = new ArrayList<>();
        root.visit(new PoseStack(), (pose, path, index, cube) -> {
            for (ModelPart.Polygon polygon : cube.polygons) {
                float[][] corners = new float[polygon.vertices().length][];
                for (int i = 0; i < corners.length; i++) {
                    ModelPart.Vertex vertex = polygon.vertices()[i];
                    Vector3f p = pose.pose().transformPosition(vertex.worldX(), vertex.worldY(), vertex.worldZ(), new Vector3f());
                    corners[i] = new float[]{p.x, p.y, p.z, vertex.u(), vertex.v()};
                }
                out.add(corners);
            }
        });
        return out;
    }

    private static float[] bounds(List<float[][]> polygons) {
        float[] b = {Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE};
        for (float[][] polygon : polygons) {
            for (float[] c : polygon) {
                for (int k = 0; k < 3; k++) {
                    b[k] = Math.min(b[k], c[k]);
                    b[k + 3] = Math.max(b[k + 3], c[k]);
                }
            }
        }
        return b;
    }

    /** Same corners in the same cyclic order, ignoring which corner each polygon lists first. */
    static boolean samePolygon(float[][] a, float[][] b) {
        if (a.length != b.length) {
            return false;
        }
        for (int shift = 0; shift < a.length; shift++) {
            boolean all = true;
            for (int i = 0; i < a.length && all; i++) {
                float[] p = a[i];
                float[] q = b[(i + shift) % b.length];
                for (int k = 0; k < 5; k++) {
                    all &= Math.abs(p[k] - q[k]) < EPS;
                }
            }
            if (all) {
                return true;
            }
        }
        return false;
    }

    static void assertSamePolygons(List<float[][]> expected, List<float[][]> actual, String what) {
        assertEquals(expected.size(), actual.size(), what + ": polygon count");
        List<float[][]> unmatched = new ArrayList<>(actual);
        for (float[][] polygon : expected) {
            float[][] match = unmatched.stream().filter(candidate -> samePolygon(polygon, candidate)).findFirst().orElse(null);
            assertNotNull(match, () -> what + ": nothing matches " + Arrays.deepToString(polygon) + "\nactual: " + actual.stream().map(Arrays::deepToString).toList());
            unmatched.remove(match);
        }
    }
}
