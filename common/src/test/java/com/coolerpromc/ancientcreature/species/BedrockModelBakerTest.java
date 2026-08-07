package com.coolerpromc.ancientcreature.species;

import com.coolerpromc.ancientcreature.reference.ReferenceModels;
import com.coolerpromc.ancientcreature.client.model.bedrock.BedrockGeometry;
import com.coolerpromc.ancientcreature.client.model.bedrock.BedrockModelBaker;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.JsonOps;
import net.minecraft.SharedConstants;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.server.Bootstrap;
import org.joml.Vector4f;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Proves each migrated creature is the same model, not a lookalike.
 *
 * <p>Bakes each shipped {@code .geo.json} through {@link BedrockModelBaker} and walks the result with
 * {@link ModelPart#visit}, which reports every cube together with its fully composed rest-pose
 * transform. The same walk is done over the original hand-written {@code TriceratopsModel}, and the
 * two are compared corner by corner and UV by UV. Tyrannosaurus Rex matters most here: unlike the
 * others it has bones that are both rotated and parents, which is where a naive conversion breaks.
 *
 * <p>That makes "the current model appearance is preserved" a checked fact rather than a claim: if the
 * coordinate conversion in the baker or in the converter that produced the JSON were wrong by so much
 * as a rotation sign, this fails.
 */
class BedrockModelBakerTest {
    /** Model units; well below anything visible, but tight enough to catch a sign or axis error. */
    private static final float EPSILON = 1.0E-3F;

    @BeforeAll
    static void bootstrapMinecraft() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @ParameterizedTest(name = "{0}")
    @CsvSource({
        "triceratops, 66",
        "tyrannosaurus_rex, 73",
        "megalodon, 60",
    })
    void bakedGeometryMatchesTheOriginalJavaModel(String species, int cubeCount) throws IOException {
        Map<String, CubeSnapshot> expected = snapshot(referenceModel(species).bakeRoot());
        Map<String, CubeSnapshot> actual = snapshot(BedrockModelBaker.bake(loadGeometry(species)));

        assertFalse(expected.isEmpty(), "the reference model produced no cubes");
        assertEquals(cubeCount, expected.size(), "reference model cube count");

        List<String> problems = new ArrayList<>();

        for (Map.Entry<String, CubeSnapshot> entry : expected.entrySet()) {
            CubeSnapshot want = entry.getValue();
            CubeSnapshot got = actual.get(entry.getKey());
            if (got == null) {
                problems.add(entry.getKey() + ": missing from the baked model");
                continue;
            }
            want.compareTo(entry.getKey(), got, problems);
        }
        for (String key : actual.keySet()) {
            if (!expected.containsKey(key)) {
                problems.add(key + ": present in the baked model but not the reference");
            }
        }

        assertTrue(problems.isEmpty(),
            () -> problems.size() + " difference(s) between the Bedrock-baked and Java " + species + " models:\n"
                + String.join("\n", problems.subList(0, Math.min(problems.size(), 40))));
    }

    private static LayerDefinition referenceModel(String species) {
        return switch (species) {
            case "triceratops" -> ReferenceModels.createTriceratopsLayer();
            case "tyrannosaurus_rex" -> ReferenceModels.createTyrannosaurusRexLayer();
            case "megalodon" -> ReferenceModels.createMegalodonLayer();
            default -> throw new IllegalArgumentException(species);
        };
    }

    /** Every cube in the tree, keyed by its part path, with world-space corners and UVs. */
    private static Map<String, CubeSnapshot> snapshot(ModelPart root) {
        Map<String, CubeSnapshot> cubes = new LinkedHashMap<>();
        PoseStack poseStack = new PoseStack();

        root.visit(poseStack, (pose, path, index, cube) -> {
            List<float[]> corners = new ArrayList<>();
            List<float[]> uvs = new ArrayList<>();

            for (ModelPart.Polygon polygon : cube.polygons) {
                for (ModelPart.Vertex vertex : polygon.vertices()) {
                    Vector4f position = new Vector4f(vertex.x(), vertex.y(), vertex.z(), 1.0F)
                        .mul(pose.pose());
                    corners.add(new float[]{position.x(), position.y(), position.z()});
                    uvs.add(new float[]{vertex.u(), vertex.v()});
                }
            }
            cubes.put(path + "#" + index, new CubeSnapshot(corners, uvs));
        });

        return cubes;
    }

    private record CubeSnapshot(List<float[]> corners, List<float[]> uvs) {
        void compareTo(String key, CubeSnapshot other, List<String> problems) {
            if (this.corners.size() != other.corners.size()) {
                problems.add(key + ": vertex count " + this.corners.size() + " vs " + other.corners.size());
                return;
            }
            for (int i = 0; i < this.corners.size(); i++) {
                float[] a = this.corners.get(i);
                float[] b = other.corners.get(i);
                for (int axis = 0; axis < 3; axis++) {
                    if (Math.abs(a[axis] - b[axis]) > EPSILON) {
                        problems.add(key + " vertex " + i + " axis " + "xyz".charAt(axis)
                            + ": expected " + a[axis] + " but baked " + b[axis]);
                    }
                }
                float[] uvA = this.uvs.get(i);
                float[] uvB = other.uvs.get(i);
                if (Math.abs(uvA[0] - uvB[0]) > EPSILON || Math.abs(uvA[1] - uvB[1]) > EPSILON) {
                    problems.add(key + " vertex " + i + " uv: expected (" + uvA[0] + ", " + uvA[1]
                        + ") but baked (" + uvB[0] + ", " + uvB[1] + ")");
                }
            }
        }
    }

    private static BedrockGeometry loadGeometry(String species) throws IOException {
        Path path = Path.of("src/main/resources/assets/ancientcreature/ancientcreature/geo/" + species + ".geo.json");
        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            JsonElement json = new Gson().fromJson(reader, JsonElement.class);
            List<BedrockGeometry> geometries = BedrockGeometry.FILE_CODEC.parse(JsonOps.INSTANCE, json)
                .getOrThrow(message -> new AssertionError(path + ": " + message));
            assertEquals(1, geometries.size());
            return geometries.getFirst();
        }
    }
}
