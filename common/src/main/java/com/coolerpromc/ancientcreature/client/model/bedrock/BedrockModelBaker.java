package com.coolerpromc.ancientcreature.client.model.bedrock;

import com.coolerpromc.ancientcreature.Constants;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import org.joml.Vector3fc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class BedrockModelBaker {
    private static final float JAVA_Y_ORIGIN = 24.0F;

    public static ModelPart bake(BedrockGeometry geometry) {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        Map<String, List<BedrockBone>> childrenByParent = geometry.childrenByParent();
        Set<String> warnedPerFaceUv = new HashSet<>();

        for (BedrockBone bone : childrenByParent.getOrDefault("", List.of())) {
            addBone(geometry, bone, null, root, childrenByParent, warnedPerFaceUv);
        }

        return LayerDefinition.create(mesh, geometry.textureWidth(), geometry.textureHeight()).bakeRoot();
    }

    private static void addBone(BedrockGeometry geometry, BedrockBone bone, BedrockBone parent, PartDefinition parentPart, Map<String, List<BedrockBone>> childrenByParent, Set<String> warnedPerFaceUv) {
        Vector3fc pivot = bone.pivot();
        float offsetX = -(pivot.x() - (parent == null ? 0.0F : parent.pivot().x()));
        float offsetY = -(pivot.y() - (parent == null ? 0.0F : parent.pivot().y()));
        float offsetZ = pivot.z() - (parent == null ? 0.0F : parent.pivot().z());
        if (parent == null) {
            offsetY += JAVA_Y_ORIGIN;
        }

        PartPose pose = PartPose.offsetAndRotation(offsetX, offsetY, offsetZ, -bone.rotationX() * Mth.DEG_TO_RAD, -bone.rotationY() * Mth.DEG_TO_RAD, bone.rotationZ() * Mth.DEG_TO_RAD);

        CubeListBuilder cubes = CubeListBuilder.create();
        List<BedrockCube> rotatedCubes = new ArrayList<>();

        for (BedrockCube cube : bone.cubes()) {
            if (cube.isRotated()) {
                rotatedCubes.add(cube);
                continue;
            }
            appendCube(geometry, bone, cube, pivot, cubes, warnedPerFaceUv);
        }

        PartDefinition part = parentPart.addOrReplaceChild(bone.name(), cubes, pose);

        for (int index = 0; index < rotatedCubes.size(); index++) {
            BedrockCube cube = rotatedCubes.get(index);
            Vector3fc cubePivot = cube.pivot().orElse(pivot);
            Vector3fc rotation = cube.rotation().orElseThrow();

            CubeListBuilder rotatedBuilder = CubeListBuilder.create();
            appendCube(geometry, bone, cube, cubePivot, rotatedBuilder, warnedPerFaceUv);

            PartPose rotatedPose = PartPose.offsetAndRotation(-(cubePivot.x() - pivot.x()), -(cubePivot.y() - pivot.y()), cubePivot.z() - pivot.z(), -rotation.x() * Mth.DEG_TO_RAD, -rotation.y() * Mth.DEG_TO_RAD, rotation.z() * Mth.DEG_TO_RAD);

            part.addOrReplaceChild(bone.name() + "_r" + (index + 1), rotatedBuilder, rotatedPose);
        }

        for (BedrockBone child : childrenByParent.getOrDefault(bone.name(), List.of())) {
            addBone(geometry, child, bone, part, childrenByParent, warnedPerFaceUv);
        }
    }

    private static void appendCube(BedrockGeometry geometry, BedrockBone bone, BedrockCube cube, Vector3fc pivot, CubeListBuilder builder, Set<String> warnedPerFaceUv) {
        if (cube.uv().isPerFace() && warnedPerFaceUv.add(geometry.identifier() + "#" + bone.name())) {
            Constants.LOG.warn("Geometry '{}' bone '{}' uses per-face UV, which vanilla's cube builder cannot "
                + "reproduce. Falling back to box UV from the smallest face corner; the texture may be wrong. "
                + "Re-export the model with box UV for exact results.", geometry.identifier(), bone.name());
        }

        float width = cube.size().x();
        float height = cube.size().y();
        float depth = cube.size().z();

        float localX = pivot.x() - cube.origin().x() - width;
        float localY = pivot.y() - cube.origin().y() - height;
        float localZ = cube.origin().z() - pivot.z();

        builder.texOffs(Mth.floor(cube.uv().u()), Mth.floor(cube.uv().v()))
            .mirror(cube.mirror())
            .addBox(localX, localY, localZ, width, height, depth, new CubeDeformation(cube.inflate()))
            .mirror(false);
    }

    private BedrockModelBaker() {
    }
}
