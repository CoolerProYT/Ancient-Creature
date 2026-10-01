package com.coolerpromc.ancientcreature.client.block.renderer;

import com.coolerpromc.ancientcreature.Constants;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;

/**
 * Texture layers shared by the machine block-entity and item renderers: a translucent base (glass, fluids),
 * an always-lit glow layer (screens, LED strips) and an extra layer that only lights up while the machine runs.
 */
public final class MachineLayers {
    private static final int FULL_BRIGHT = 0xF000F0;

    private final RenderType base;
    private final RenderType glow;
    private final RenderType active;

    private MachineLayers(String name, boolean hasGlow) {
        this.base = RenderTypes.entityTranslucent(texture(name, ""));
        this.glow = hasGlow ? RenderTypes.eyes(texture(name, "_glow")) : null;
        this.active = RenderTypes.eyes(texture(name, "_active"));
    }

    public static MachineLayers of(String name) {
        return new MachineLayers(name, true);
    }

    public static MachineLayers withoutGlow(String name) {
        return new MachineLayers(name, false);
    }

    private static Identifier texture(String name, String suffix) {
        return Constants.id("textures/entity/block/" + name + suffix + ".png");
    }

    public RenderType base() {
        return this.base;
    }

    public <S> void submitLights(Model<? super S> model, S state, PoseStack poseStack, SubmitNodeCollector collector, boolean running) {
        if (this.glow != null) {
            collector.submitModel(model, state, poseStack, this.glow, FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0);
        }
        if (running) {
            collector.submitModel(model, state, poseStack, this.active, FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0);
        }
    }
}
