package com.coolerpromc.ancientcreature.client.model.bedrock;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import org.joml.Matrix3f;
import org.joml.Vector2fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.*;

/**
 * Turns a {@link BedrockGeometry} into a vanilla {@link ModelPart} tree that renders exactly as the
 * model does in Blockbench and in Bedrock.
 *
 * <h2>Coordinate conversion</h2>
 * Derived from Blockbench's own Bedrock importer and Java exporter, which together define how a
 * Bedrock file maps onto a Java {@code ModelPart}:
 * <ul>
 *   <li>bone offset: {@code (px - parent.px, -(py - parent.py), pz - parent.pz)}, plus 24 on Y for
 *       root bones (Java model space is Y-down with the feet at Y=24);</li>
 *   <li>bone and cube rotation: the file's {@code [x, y, z]} degrees, used as-is;</li>
 *   <li>cube box: {@code (ox - px, py - oy - height, oz - pz)} relative to the bone pivot.</li>
 * </ul>
 *
 * <h2>Faces</h2>
 * Every cube is built from per-face rectangles. Box UV is first expanded to those rectangles the way
 * Bedrock does it — with cube sizes floored to whole texels and {@code mirror} flipping each face and
 * swapping east and west — so box UV, per-face UV and {@code uv_rotation} all go through one path and
 * match Blockbench texel for texel. Bedrock's {@code east} face is the cube's negative-X side.
 *
 * <p>Rotated cubes are baked straight into their bone's vertices, so the part tree has exactly one
 * part per bone and animations address bones by their real names.
 */
public final class BedrockModelBaker {
    private static final float JAVA_Y_ORIGIN = 24.0F;
    private static final Direction[] DIRECTIONS = Direction.values();

    public static BakedBedrockModel bake(BedrockGeometry geometry) {
        Map<String, List<BedrockBone>> children = geometry.childrenByParent();
        Map<String, ModelPart> parts = new LinkedHashMap<>();
        Map<String, String> parents = new HashMap<>();
        Map<String, BakedBedrockModel.Locator> locators = new HashMap<>();

        Map<String, ModelPart> rootChildren = new LinkedHashMap<>();
        for (BedrockBone bone : children.getOrDefault("", List.of())) {
            rootChildren.put(bone.name(), bakeBone(geometry, bone, null, children, parts, parents, locators, new HashSet<>()));
        }
        ModelPart root = new ModelPart(List.of(), rootChildren);
        root.setInitialPose(PartPose.ZERO);
        return new BakedBedrockModel(geometry, root, parts, parents, locators);
    }

    private static ModelPart bakeBone(BedrockGeometry geometry, BedrockBone bone, BedrockBone parent, Map<String, List<BedrockBone>> children, Map<String, ModelPart> parts, Map<String, String> parents, Map<String, BakedBedrockModel.Locator> locators, Set<String> visiting) {
        if (!visiting.add(bone.key())) {
            // A cycle; validation reports it. Stop here rather than recursing forever.
            return new ModelPart(List.of(), Map.of());
        }

        Vector3fc pivot = bone.pivot();
        float x = pivot.x() - (parent == null ? 0.0F : parent.pivot().x());
        float y = -(pivot.y() - (parent == null ? 0.0F : parent.pivot().y()));
        float z = pivot.z() - (parent == null ? 0.0F : parent.pivot().z());
        if (parent == null) {
            y += JAVA_Y_ORIGIN;
        }

        List<ModelPart.Cube> cubes = new ArrayList<>();
        for (BedrockCube cube : bone.cubes()) {
            List<ModelPart.Polygon> polygons = cubePolygons(geometry, bone, cube);
            addContainers(cubes, polygons);
        }
        bone.polyMesh().ifPresent(mesh -> addContainers(cubes, meshPolygons(geometry, bone, mesh)));

        Map<String, ModelPart> childParts = new LinkedHashMap<>();
        for (BedrockBone child : children.getOrDefault(bone.key(), List.of())) {
            childParts.put(child.name(), bakeBone(geometry, child, bone, children, parts, parents, locators, visiting));
        }

        ModelPart part = new ModelPart(cubes, childParts);
        Vector3fc rotation = bone.rotation();
        part.setInitialPose(PartPose.offsetAndRotation(x, y, z,
            rotation.x() * Mth.DEG_TO_RAD, rotation.y() * Mth.DEG_TO_RAD, rotation.z() * Mth.DEG_TO_RAD));
        part.resetPose();
        part.skipDraw = bone.neverRender();

        parts.putIfAbsent(bone.key(), part);
        if (parent != null) {
            parents.put(bone.key(), parent.key());
        }
        bone.locators().forEach((name, locator) -> locators.putIfAbsent(name, new BakedBedrockModel.Locator(bone.key(),
            new Vector3f(locator.offset().x() - pivot.x(), -(locator.offset().y() - pivot.y()), locator.offset().z() - pivot.z()))));
        return part;
    }

    // ------------------------------------------------------------------ cubes

    /** A face's texture rectangle in the form Bedrock writes per-face UV: corner plus signed size. */
    record FaceRect(float u, float v, float w, float h, int rotation) {
    }

    static Map<BedrockFace, FaceRect> faceRects(BedrockCube cube, boolean mirror) {
        Map<BedrockFace, FaceRect> rects = new EnumMap<>(BedrockFace.class);
        if (cube.uv() instanceof BedrockCube.FaceUvs faces) {
            faces.faces().forEach((face, uv) -> rects.put(face, new FaceRect(uv.uv().x(), uv.uv().y(), uv.size().x(), uv.size().y(), uv.rotation())));
            return rects;
        }

        Vector2fc offset = ((BedrockCube.BoxUv) cube.uv()).offset();
        float u = offset.x();
        float v = offset.y();
        // Bedrock lays box UV out with whole-texel sizes.
        float w = (float) Math.floor(cube.size().x() + 1.0E-7);
        float h = (float) Math.floor(cube.size().y() + 1.0E-7);
        float d = (float) Math.floor(cube.size().z() + 1.0E-7);

        // Blockbench's box layout, as {fromU, fromV, sizeU, sizeV} relative to the offset.
        Map<BedrockFace, float[]> layout = new EnumMap<>(BedrockFace.class);
        layout.put(BedrockFace.EAST, new float[]{0, d, d, h});
        layout.put(BedrockFace.WEST, new float[]{d + w, d, d, h});
        layout.put(BedrockFace.UP, new float[]{d + w, d, -w, -d});
        layout.put(BedrockFace.DOWN, new float[]{d + w * 2, 0, -w, d});
        layout.put(BedrockFace.SOUTH, new float[]{d * 2 + w, d, w, h});
        layout.put(BedrockFace.NORTH, new float[]{d, d, w, h});

        if (mirror) {
            for (float[] f : layout.values()) {
                f[0] += f[2];
                f[2] = -f[2];
            }
            float[] east = layout.get(BedrockFace.EAST);
            layout.put(BedrockFace.EAST, layout.get(BedrockFace.WEST));
            layout.put(BedrockFace.WEST, east);
        }

        for (Map.Entry<BedrockFace, float[]> entry : layout.entrySet()) {
            float[] f = entry.getValue();
            float fu = u + f[0];
            float fv = v + f[1];
            float fw = f[2];
            float fh = f[3];
            if (entry.getKey() == BedrockFace.UP || entry.getKey() == BedrockFace.DOWN) {
                // How Blockbench writes up/down: from the opposite corner with the size negated.
                fu += fw;
                fv += fh;
                fw = -fw;
                fh = -fh;
            }
            rects.put(entry.getKey(), new FaceRect(fu, fv, fw, fh, 0));
        }
        return rects;
    }

    private static List<ModelPart.Polygon> cubePolygons(BedrockGeometry geometry, BedrockBone bone, BedrockCube cube) {
        Vector3fc pivot = bone.pivot();
        float inflate = cube.inflate().orElse(bone.inflate());
        boolean mirror = cube.mirror().orElse(bone.mirror());

        float minX = cube.origin().x() - pivot.x() - inflate;
        float maxX = cube.origin().x() + cube.size().x() - pivot.x() + inflate;
        float minY = pivot.y() - cube.origin().y() - cube.size().y() - inflate;
        float maxY = pivot.y() - cube.origin().y() + inflate;
        float minZ = cube.origin().z() - pivot.z() - inflate;
        float maxZ = cube.origin().z() + cube.size().z() - pivot.z() + inflate;

        Vector3f t0 = new Vector3f(minX, minY, minZ);
        Vector3f t1 = new Vector3f(maxX, minY, minZ);
        Vector3f t2 = new Vector3f(maxX, maxY, minZ);
        Vector3f t3 = new Vector3f(minX, maxY, minZ);
        Vector3f l0 = new Vector3f(minX, minY, maxZ);
        Vector3f l1 = new Vector3f(maxX, minY, maxZ);
        Vector3f l2 = new Vector3f(maxX, maxY, maxZ);
        Vector3f l3 = new Vector3f(minX, maxY, maxZ);

        Matrix3f rotation = null;
        Vector3f rotationPivot = null;
        if (cube.isRotated()) {
            Vector3fc cp = cube.pivot().orElse(pivot);
            rotationPivot = new Vector3f(cp.x() - pivot.x(), -(cp.y() - pivot.y()), cp.z() - pivot.z());
            rotation = new Matrix3f().rotationZYX(cube.rotation().z() * Mth.DEG_TO_RAD, cube.rotation().y() * Mth.DEG_TO_RAD, cube.rotation().x() * Mth.DEG_TO_RAD);
        }

        Matrix3f bindPose = bone.hasBindPose()
            ? new Matrix3f().rotationZYX(bone.bindPoseRotation().z() * Mth.DEG_TO_RAD, bone.bindPoseRotation().y() * Mth.DEG_TO_RAD, bone.bindPoseRotation().x() * Mth.DEG_TO_RAD)
            : null;

        Map<BedrockFace, FaceRect> rects = faceRects(cube, mirror);
        List<ModelPart.Polygon> polygons = new ArrayList<>(6);
        float texW = geometry.textureWidth();
        float texH = geometry.textureHeight();

        // Corner order and normals follow vanilla ModelPart.Cube so lighting and culling behave the same.
        addFace(polygons, rects.get(BedrockFace.UP), new Vector3f[]{l1, l0, t0, t1}, new Vector3f(0, -1, 0), texW, texH, rotation, rotationPivot, bindPose);
        addFace(polygons, rects.get(BedrockFace.DOWN), new Vector3f[]{t2, t3, l3, l2}, new Vector3f(0, 1, 0), texW, texH, rotation, rotationPivot, bindPose);
        addFace(polygons, rects.get(BedrockFace.EAST), new Vector3f[]{t0, l0, l3, t3}, new Vector3f(-1, 0, 0), texW, texH, rotation, rotationPivot, bindPose);
        addFace(polygons, rects.get(BedrockFace.NORTH), new Vector3f[]{t1, t0, t3, t2}, new Vector3f(0, 0, -1), texW, texH, rotation, rotationPivot, bindPose);
        addFace(polygons, rects.get(BedrockFace.WEST), new Vector3f[]{l1, t1, t2, l2}, new Vector3f(1, 0, 0), texW, texH, rotation, rotationPivot, bindPose);
        addFace(polygons, rects.get(BedrockFace.SOUTH), new Vector3f[]{l0, l1, l2, l3}, new Vector3f(0, 0, 1), texW, texH, rotation, rotationPivot, bindPose);
        return polygons;
    }

    /**
     * One face. Vanilla's polygon puts (U1,V0) on corner 0, (U0,V0) on 1, (U0,V1) on 2 and (U1,V1) on 3,
     * with (U0,V0,U1,V1) = (u, v, u + w, v + h) of the Bedrock rectangle for every face.
     *
     * <p>{@code uv_rotation} is applied the way Blockbench does: by cycling the four UV corners in its
     * own slot order (top-left, top-right, bottom-left, bottom-right of its stored rectangle). Up and down
     * faces store that rectangle corner-swapped, so their slots land on different vanilla corners.
     */
    private static void addFace(List<ModelPart.Polygon> out, FaceRect rect, Vector3f[] corners, Vector3f normal, float texW, float texH, Matrix3f rotation, Vector3f rotationPivot, Matrix3f bindPose) {
        if (rect == null) {
            return;
        }
        float u0 = rect.u();
        float v0 = rect.v();
        float u1 = rect.u() + rect.w();
        float v1 = rect.v() + rect.h();

        // vanilla corner index -> (u, v)
        float[][] uv = {{u1, v0}, {u0, v0}, {u0, v1}, {u1, v1}};

        if (rect.rotation() != 0) {
            boolean upOrDown = normal.y() != 0.0F;
            // Blockbench slot k sits on vanilla corner SLOT_TO_CORNER[k].
            int[] slotToCorner = upOrDown ? new int[]{3, 2, 0, 1} : new int[]{1, 0, 2, 3};
            float[][] slots = new float[4][];
            for (int k = 0; k < 4; k++) {
                slots[k] = uv[slotToCorner[k]];
            }
            for (int r = rect.rotation(); r > 0; r -= 90) {
                float[] a = slots[0];
                slots[0] = slots[2];
                slots[2] = slots[3];
                slots[3] = slots[1];
                slots[1] = a;
            }
            float[][] rotated = new float[4][];
            for (int k = 0; k < 4; k++) {
                rotated[slotToCorner[k]] = slots[k];
            }
            uv = rotated;
        }

        ModelPart.Vertex[] vertices = new ModelPart.Vertex[4];
        for (int i = 0; i < 4; i++) {
            Vector3f p = transform(corners[i], rotation, rotationPivot);
            if (bindPose != null) {
                bindPose.transform(p);
            }
            vertices[i] = new ModelPart.Vertex(p.x, p.y, p.z, uv[i][0] / texW, uv[i][1] / texH);
        }
        Vector3f n = new Vector3f(normal);
        if (rotation != null) {
            rotation.transform(n);
        }
        if (bindPose != null) {
            bindPose.transform(n);
        }
        out.add(new ModelPart.Polygon(vertices, n));
    }

    private static Vector3f transform(Vector3f corner, Matrix3f rotation, Vector3f pivot) {
        Vector3f p = new Vector3f(corner);
        if (rotation != null) {
            p.sub(pivot);
            rotation.transform(p);
            p.add(pivot);
        }
        return p;
    }

    // ------------------------------------------------------------------ poly mesh

    private static List<ModelPart.Polygon> meshPolygons(BedrockGeometry geometry, BedrockBone bone, BedrockPolyMesh mesh) {
        List<ModelPart.Polygon> out = new ArrayList<>();
        Vector3fc pivot = bone.pivot();
        float texW = geometry.textureWidth();
        float texH = geometry.textureHeight();
        for (int[][] polygon : mesh.polygons()) {
            int corners = polygon.length;
            ModelPart.Vertex[] vertices = new ModelPart.Vertex[4];
            Vector3f normal = new Vector3f();
            for (int c = 0; c < 4; c++) {
                // Triangles repeat their last corner so they draw as a degenerate quad.
                int[] index = polygon[Math.min(c, corners - 1)];
                Vector3fc pos = mesh.positions().get(index[0]);
                float u = 0;
                float v = 0;
                if (index[2] < mesh.uvs().size()) {
                    Vector2fc uv = mesh.uvs().get(index[2]);
                    u = mesh.normalizedUvs() ? uv.x() : uv.x() / texW;
                    v = mesh.normalizedUvs() ? uv.y() : uv.y() / texH;
                }
                vertices[c] = new ModelPart.Vertex(pos.x() - pivot.x(), -(pos.y() - pivot.y()), pos.z() - pivot.z(), u, v);
                if (c < corners && index[1] < mesh.normals().size()) {
                    Vector3fc n = mesh.normals().get(index[1]);
                    normal.add(n.x(), -n.y(), n.z());
                }
            }
            if (normal.lengthSquared() < 1.0E-8F) {
                Vector3f a = new Vector3f(vertices[1].x() - vertices[0].x(), vertices[1].y() - vertices[0].y(), vertices[1].z() - vertices[0].z());
                Vector3f b = new Vector3f(vertices[2].x() - vertices[0].x(), vertices[2].y() - vertices[0].y(), vertices[2].z() - vertices[0].z());
                normal = a.cross(b);
            }
            if (normal.lengthSquared() > 1.0E-8F) {
                normal.normalize();
            } else {
                normal.set(0, -1, 0);
            }
            out.add(new ModelPart.Polygon(vertices, normal));
        }
        return out;
    }

    // ------------------------------------------------------------------ containers

    /**
     * Packs polygons into vanilla {@link ModelPart.Cube}s. A cube's polygon array has one slot per face
     * it was constructed with, so build containers of exactly the right length and fill them.
     */
    private static void addContainers(List<ModelPart.Cube> out, List<ModelPart.Polygon> polygons) {
        for (int start = 0; start < polygons.size(); start += DIRECTIONS.length) {
            int count = Math.min(DIRECTIONS.length, polygons.size() - start);
            Set<Direction> faces = EnumSet.noneOf(Direction.class);
            faces.addAll(Arrays.asList(DIRECTIONS).subList(0, count));

            float minX = Float.MAX_VALUE, minY = Float.MAX_VALUE, minZ = Float.MAX_VALUE;
            float maxX = -Float.MAX_VALUE, maxY = -Float.MAX_VALUE, maxZ = -Float.MAX_VALUE;
            for (int i = start; i < start + count; i++) {
                for (ModelPart.Vertex vertex : polygons.get(i).vertices()) {
                    minX = Math.min(minX, vertex.x());
                    minY = Math.min(minY, vertex.y());
                    minZ = Math.min(minZ, vertex.z());
                    maxX = Math.max(maxX, vertex.x());
                    maxY = Math.max(maxY, vertex.y());
                    maxZ = Math.max(maxZ, vertex.z());
                }
            }
            ModelPart.Cube container = new ModelPart.Cube(0, 0, minX, minY, minZ, maxX - minX, maxY - minY, maxZ - minZ, 0, 0, 0, false, 1, 1, faces);
            for (int i = 0; i < count; i++) {
                container.polygons[i] = polygons.get(start + i);
            }
            out.add(container);
        }
    }

    private BedrockModelBaker() {
    }
}
