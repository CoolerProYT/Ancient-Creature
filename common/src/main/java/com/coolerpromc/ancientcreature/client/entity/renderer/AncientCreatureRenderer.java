package com.coolerpromc.ancientcreature.client.entity.renderer;

import com.coolerpromc.ancientcreature.client.animation.bedrock.AnimationController;
import com.coolerpromc.ancientcreature.client.animation.bedrock.AnimationControllerManager;
import com.coolerpromc.ancientcreature.client.entity.model.AncientCreatureModel;
import com.coolerpromc.ancientcreature.client.entity.state.AncientCreatureRenderState;
import com.coolerpromc.ancientcreature.client.species.ClientSpeciesDefinition;
import com.coolerpromc.ancientcreature.client.species.ClientSpeciesManager;
import com.coolerpromc.ancientcreature.entity.custom.AncientCreatureEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AncientCreatureRenderer extends MobRenderer<AncientCreatureEntity, AncientCreatureRenderState, AncientCreatureModel> {
    private static final Identifier MISSING_TEXTURE = Identifier.withDefaultNamespace("textures/misc/unknown_pack.png");
    private final Map<UUID, ControllerPlayback> playback = new HashMap<>();

    public AncientCreatureRenderer(EntityRendererProvider.Context context) {
        super(context, new AncientCreatureModel(AncientCreatureModel.emptyRoot()), 0.5F);
    }

    @Override
    public AncientCreatureRenderState createRenderState() {
        return new AncientCreatureRenderState();
    }

    @Override
    public void extractRenderState(AncientCreatureEntity entity, AncientCreatureRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);

        state.species = entity.getSpecies();
        state.variant = entity.getVariant();
        state.action = entity.getAction();
        state.isSprinting = entity.isSprinting();
        state.isAggressive = entity.isAggressive();
        state.isOnGround = entity.onGround();
        state.hunger = entity.getHunger();
        state.isHungry = entity.hungerProperties().isHungryAt(entity.getHunger());
        state.creatureHealth = entity.getHealth();
        state.creatureMaxHealth = entity.getMaxHealth();
        state.hurtTimeRemaining = entity.hurtTime;

        ClientSpeciesDefinition definition = ClientSpeciesManager.INSTANCE.getOrWarn(state.species.id());
        state.clientDefinition = definition;

        if (definition == null) {
            state.bakedModel = null;
            state.controller = null;
            state.renderScale = 1.0F;
            state.shadowRadius = 0.5F;
            return;
        }

        state.renderScale = definition.renderScale();
        state.shadowRadius = definition.shadowRadius();
        state.animationFile = definition.animations().orElse(Identifier.withDefaultNamespace("empty"));

        AncientCreatureModel model = AncientCreatureModel.forGeometry(definition.geometry());
        state.bakedModel = model == null ? null : model.root();

        AnimationController controller = definition.controller().map(AnimationControllerManager.INSTANCE::get).orElse(null);
        state.controller = controller;

        if (controller != null) {
            this.advancePlayback(entity, state, controller, partialTicks);
        }
    }


    private void advancePlayback(AncientCreatureEntity entity, AncientCreatureRenderState state, AnimationController controller, float partialTicks) {
        ControllerPlayback current = this.playback.computeIfAbsent(entity.getUUID(), uuid -> new ControllerPlayback(controller.initialState()));

        float now = (entity.tickCount + partialTicks) / 20.0F;
        current.advanceTo(now);

        state.controllerState = current.state;
        state.controllerStateSeconds = current.secondsInState;
        String next = controller.resolveState(current.state, AncientCreatureModel.queryContext(state, current.secondsInState));

        if (!next.equals(current.state)) {
            current.beginTransition(next, controller.state(next).blendSeconds(), now);
        }

        state.controllerState = current.state;
        state.controllerStateSeconds = current.secondsInState;
        state.previousControllerState = current.previousState;
        state.previousStateSeconds = current.previousSeconds;
        state.blendWeight = current.blendWeight();

        if (this.playback.size() > 512) {
            this.playback.clear();
        }
    }

    @Override
    public void submit(AncientCreatureRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        if (state.bakedModel == null) {
            return;
        }

        AncientCreatureModel model = AncientCreatureModel.forGeometry(state.clientDefinition != null ? state.clientDefinition.geometry() : Identifier.withDefaultNamespace("empty"));
        if (model == null) {
            return;
        }

        this.model = model;
        super.submit(state, poseStack, collector, camera);
    }

    @Override
    protected float getShadowRadius(AncientCreatureRenderState state) {
        return state.shadowRadius * state.ageScale;
    }

    @Override
    protected void scale(AncientCreatureRenderState state, PoseStack poseStack) {
        float scale = state.ageScale * state.renderScale;
        poseStack.scale(scale, scale, scale);
    }

    @Override
    public Identifier getTextureLocation(AncientCreatureRenderState state) {
        ClientSpeciesDefinition definition = state.clientDefinition;
        if (definition == null) {
            return MISSING_TEXTURE;
        }
        return definition.textureFor(state.variant);
    }

    private static final class ControllerPlayback {
        private String state;
        private float secondsInState;
        private float lastUpdate = Float.NaN;

        private String previousState;
        private float previousSeconds;
        private float blendSeconds;
        private float blendElapsed;

        private ControllerPlayback(String initialState) {
            this.state = initialState;
        }

        private void advanceTo(float now) {
            if (Float.isNaN(this.lastUpdate)) {
                this.lastUpdate = now;
                return;
            }
            float delta = Mth.clamp(now - this.lastUpdate, 0.0F, 0.5F);
            this.lastUpdate = now;
            this.secondsInState += delta;
            if (this.previousState != null) {
                this.previousSeconds += delta;
                this.blendElapsed += delta;
                if (this.blendElapsed >= this.blendSeconds) {
                    this.previousState = null;
                }
            }
        }

        private void beginTransition(String next, float blendSeconds, float now) {
            this.previousState = this.state;
            this.previousSeconds = this.secondsInState;
            this.blendSeconds = Math.max(blendSeconds, 1.0E-4F);
            this.blendElapsed = 0.0F;
            this.state = next;
            this.secondsInState = 0.0F;
            this.lastUpdate = now;
        }

        private float blendWeight() {
            if (this.previousState == null || this.blendSeconds <= 0.0F) {
                return 1.0F;
            }
            return Mth.clamp(this.blendElapsed / this.blendSeconds, 0.0F, 1.0F);
        }
    }
}
