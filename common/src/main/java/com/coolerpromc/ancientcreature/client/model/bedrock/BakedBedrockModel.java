package com.coolerpromc.ancientcreature.client.model.bedrock;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelPart;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class BakedBedrockModel {
    private final BedrockGeometry geometry;
    private final ModelPart root;
    private final Map<String, ModelPart> bones;
    private final Map<String, String> parents;
    private final Map<String, Locator> locators;
    private final List<ModelPart> allParts;

    public record Locator(String bone, Vector3fc offset) {
    }

    BakedBedrockModel(BedrockGeometry geometry, ModelPart root, Map<String, ModelPart> bones, Map<String, String> parents, Map<String, Locator> locators) {
        this.geometry = geometry;
        this.root = root;
        this.bones = Map.copyOf(bones);
        this.parents = Map.copyOf(parents);
        this.locators = Map.copyOf(locators);
        this.allParts = root.getAllParts();
    }

    public BedrockGeometry geometry() {
        return this.geometry;
    }

    public ModelPart root() {
        return this.root;
    }

    public @Nullable ModelPart bone(String name) {
        return this.bones.get(name.toLowerCase(Locale.ROOT));
    }

    public Map<String, ModelPart> bones() {
        return this.bones;
    }

    public @Nullable String parentOf(String bone) {
        return this.parents.get(bone.toLowerCase(Locale.ROOT));
    }

    public @Nullable Locator locator(String name) {
        return this.locators.get(name.toLowerCase(Locale.ROOT));
    }

    public Map<String, Locator> locators() {
        return this.locators;
    }

    public void resetPose() {
        for (ModelPart part : this.allParts) {
            part.resetPose();
            part.visible = true;
        }
    }

    public List<ModelPart> chain(String bone) {
        List<ModelPart> chain = new ArrayList<>();
        String current = bone.toLowerCase(Locale.ROOT);
        while (current != null) {
            ModelPart part = this.bones.get(current);
            if (part == null) {
                break;
            }
            chain.add(part);
            current = this.parents.get(current);
        }
        Collections.reverse(chain);
        return chain;
    }

    public boolean transformToLocator(String locator, PoseStack pose) {
        Locator found = this.locator(locator);
        if (found == null) {
            return false;
        }
        this.root.translateAndRotate(pose);
        for (ModelPart part : this.chain(found.bone())) {
            part.translateAndRotate(pose);
        }
        Vector3f offset = new Vector3f(found.offset());
        pose.translate(offset.x / 16.0F, offset.y / 16.0F, offset.z / 16.0F);
        return true;
    }
}
