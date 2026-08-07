package com.coolerpromc.ancientcreature.client.entity.model;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.client.animation.bedrock.AnimationController;
import com.coolerpromc.ancientcreature.client.animation.bedrock.AnimationQueryContext;
import com.coolerpromc.ancientcreature.client.animation.bedrock.BedrockAnimation;
import com.coolerpromc.ancientcreature.client.animation.bedrock.BedrockAnimationManager;
import com.coolerpromc.ancientcreature.client.entity.state.AncientCreatureRenderState;
import com.coolerpromc.ancientcreature.client.model.bedrock.BedrockGeometryManager;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.jspecify.annotations.Nullable;

import java.util.*;

public class AncientCreatureModel extends EntityModel<AncientCreatureRenderState> {
    private static final Set<String> WARNINGS = Collections.synchronizedSet(new HashSet<>());

    private static Map<Identifier, AncientCreatureModel> cache = new LinkedHashMap<>();
    private static int cachedGeneration = -1;

    public AncientCreatureModel(ModelPart root) {
        super(root);
    }

    public static ModelPart emptyRoot() {
        return new ModelPart(List.of(), Map.of());
    }

    public static @Nullable AncientCreatureModel forGeometry(Identifier geometry) {
        int generation = BedrockGeometryManager.INSTANCE.generation();
        if (generation != cachedGeneration) {
            cache = new LinkedHashMap<>();
            cachedGeneration = generation;
            WARNINGS.clear();
        }

        AncientCreatureModel cached = cache.get(geometry);
        if (cached != null) {
            return cached;
        }

        ModelPart baked = BedrockGeometryManager.INSTANCE.getBakedModel(geometry);
        if (baked == null) {
            if (WARNINGS.add("geo:" + geometry)) {
                Constants.LOG.warn("Geometry '{}' is not loaded; creatures using it will not render. Expected a file at assets/{}/{}/{}.geo.json", geometry, geometry.getNamespace(), BedrockGeometryManager.DIRECTORY, geometry.getPath());
            }
            return null;
        }

        AncientCreatureModel model = new AncientCreatureModel(baked);
        cache.put(geometry, model);
        return model;
    }

    @Override
    public void setupAnim(AncientCreatureRenderState state) {
        super.setupAnim(state);

        AnimationController controller = state.controller;
        if (controller == null) {
            return;
        }

        if (state.previousControllerState != null && state.blendWeight < 1.0F) {
            this.applyState(state, controller, state.previousControllerState, state.previousStateSeconds, 1.0F - state.blendWeight);
        }
        this.applyState(state, controller, state.controllerState, state.controllerStateSeconds, state.blendWeight);
    }

    private void applyState(AncientCreatureRenderState state, AnimationController controller, String stateName, float seconds, float weight) {
        if (weight <= 0.0F) {
            return;
        }

        List<String> animations = controller.state(stateName).animations();
        for (String animationName : animations) {
            BedrockAnimation animation = BedrockAnimationManager.INSTANCE.get(state.animationFile, animationName);
            if (animation == null) {
                if (WARNINGS.add("anim:" + state.animationFile + "#" + animationName)) {
                    Constants.LOG.warn("Animation controller state '{}' plays '{}', which is not in animation file '{}'", stateName, animationName, state.animationFile);
                }
                continue;
            }
            animation.apply(this.root, seconds, Mth.clamp(weight, 0.0F, 1.0F), WARNINGS);
        }
    }

    public static AnimationQueryContext queryContext(AncientCreatureRenderState state, float stateSeconds) {
        return new AnimationQueryContext.OfRenderState(state, stateSeconds);
    }
}
