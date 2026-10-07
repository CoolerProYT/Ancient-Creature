package com.coolerpromc.ancientcreature.client.model.bedrock;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.ArrayList;
import java.util.List;

/**
 * A Bedrock {@code poly_mesh}: free-form triangles and quads in model space.
 *
 * <p>{@code polys} is either an explicit list of polygons, each a list of
 * {@code [position, normal, uv]} index triples, or the string {@code "tri_list"}/{@code "quad_list"},
 * meaning the three arrays are parallel and consumed three or four vertices at a time.
 */
public record BedrockPolyMesh(boolean normalizedUvs, List<Vector3fc> positions, List<Vector3fc> normals, List<Vector2fc> uvs, List<int[][]> polygons) {

    static BedrockPolyMesh parse(JsonElement element, String path) throws BedrockFormatException {
        JsonObject json = BedrockJson.object(element, path);
        boolean normalized = BedrockJson.bool(json, "normalized_uvs", false);
        List<Vector3fc> positions = vectors3(BedrockJson.get(json, "positions"), path + ".positions");
        List<Vector3fc> normals = vectors3(BedrockJson.get(json, "normals"), path + ".normals");
        List<Vector2fc> uvs = new ArrayList<>();
        JsonElement uvJson = BedrockJson.get(json, "uvs");
        if (uvJson != null && uvJson.isJsonArray()) {
            JsonArray array = uvJson.getAsJsonArray();
            for (int i = 0; i < array.size(); i++) {
                uvs.add(BedrockJson.vec2(array.get(i), path + ".uvs[" + i + "]"));
            }
        }

        List<int[][]> polygons = new ArrayList<>();
        JsonElement polys = BedrockJson.get(json, "polys");
        if (polys == null) {
            throw new BedrockFormatException(path + ".polys is required");
        }
        if (polys.isJsonPrimitive()) {
            String mode = polys.getAsString();
            int corners = switch (mode) {
                case "tri_list" -> 3;
                case "quad_list" -> 4;
                default -> throw new BedrockFormatException(path + ".polys must be a list, \"tri_list\" or \"quad_list\"");
            };
            for (int start = 0; start + corners <= positions.size(); start += corners) {
                int[][] polygon = new int[corners][];
                for (int c = 0; c < corners; c++) {
                    polygon[c] = new int[]{start + c, start + c, start + c};
                }
                polygons.add(polygon);
            }
        } else if (polys.isJsonArray()) {
            JsonArray array = polys.getAsJsonArray();
            for (int p = 0; p < array.size(); p++) {
                String polyPath = path + ".polys[" + p + "]";
                if (!array.get(p).isJsonArray()) {
                    throw new BedrockFormatException(polyPath + " must be a list of [position, normal, uv]");
                }
                JsonArray corners = array.get(p).getAsJsonArray();
                if (corners.size() < 3 || corners.size() > 4) {
                    throw new BedrockFormatException(polyPath + " must have 3 or 4 corners");
                }
                int[][] polygon = new int[corners.size()][];
                for (int c = 0; c < corners.size(); c++) {
                    JsonElement corner = corners.get(c);
                    if (!corner.isJsonArray() || corner.getAsJsonArray().size() != 3) {
                        throw new BedrockFormatException(polyPath + "[" + c + "] must be [position, normal, uv]");
                    }
                    JsonArray indices = corner.getAsJsonArray();
                    polygon[c] = new int[]{
                        index(indices.get(0), positions.size(), polyPath),
                        index(indices.get(1), normals.size(), polyPath),
                        index(indices.get(2), uvs.size(), polyPath)
                    };
                }
                polygons.add(polygon);
            }
        } else {
            throw new BedrockFormatException(path + ".polys must be a list or a string");
        }
        return new BedrockPolyMesh(normalized, List.copyOf(positions), List.copyOf(normals), List.copyOf(uvs), List.copyOf(polygons));
    }

    private static int index(JsonElement element, int bound, String path) throws BedrockFormatException {
        int index = (int) BedrockJson.number(element, path);
        if (index < 0 || (bound > 0 && index >= bound)) {
            throw new BedrockFormatException(path + " index " + index + " is out of range (" + bound + " entries)");
        }
        return index;
    }

    private static List<Vector3fc> vectors3(JsonElement element, String path) throws BedrockFormatException {
        List<Vector3fc> out = new ArrayList<>();
        if (element != null && element.isJsonArray()) {
            JsonArray array = element.getAsJsonArray();
            for (int i = 0; i < array.size(); i++) {
                Vector3f v = BedrockJson.vec3(array.get(i), path + "[" + i + "]");
                out.add(v);
            }
        }
        return out;
    }
}
