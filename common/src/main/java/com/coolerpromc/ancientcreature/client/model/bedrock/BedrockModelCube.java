package com.coolerpromc.ancientcreature.client.model.bedrock;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.core.Direction;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.Set;

final class BedrockModelCube extends ModelPart.Cube {
    BedrockModelCube(float x, float y, float z, float width, float height, float depth, Set<Direction> faces) {
        super(0, 0, x, y, z, width, height, depth, 0, 0, 0, false, 1, 1, faces);
    }

    @Override
    public void compile(PoseStack.Pose pose, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        Matrix4f matrix = pose.pose();
        Vector3f scratch = new Vector3f();
        for (ModelPart.Polygon polygon : this.polygons) {
            pose.transformNormal(polygon.normal(), scratch);
            float nx = scratch.x();
            float ny = scratch.y();
            float nz = scratch.z();
            for (ModelPart.Vertex vertex : polygon.vertices()) {
                matrix.transformPosition(vertex.worldX(), vertex.worldY(), vertex.worldZ(), scratch);
                buffer.addVertex(scratch.x(), scratch.y(), scratch.z(), color, vertex.u(), vertex.v(), packedOverlay, packedLight, nx, ny, nz);
            }
        }
    }
}
