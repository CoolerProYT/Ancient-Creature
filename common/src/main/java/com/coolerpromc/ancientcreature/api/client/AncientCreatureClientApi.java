package com.coolerpromc.ancientcreature.api.client;

import com.coolerpromc.ancientcreature.client.animation.bedrock.AnimationControllerManager;
import com.coolerpromc.ancientcreature.client.animation.bedrock.BedrockAnimationManager;
import com.coolerpromc.ancientcreature.client.animation.bedrock.CreatureQueryRegistry;
import com.coolerpromc.ancientcreature.client.model.bedrock.BedrockGeometryManager;
import com.coolerpromc.ancientcreature.client.species.ClientSpeciesDefinition;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public final class AncientCreatureClientApi {

    public static void registerNumberQuery(String name, CreatureQueryRegistry.NumberQuery query) {
        CreatureQueryRegistry.registerNumber(name, query);
    }

    public static void registerStringQuery(String name, CreatureQueryRegistry.StringQuery query) {
        CreatureQueryRegistry.registerString(name, query);
    }

    public static Optional<ClientSpeciesDefinition> getClientSpecies(Identifier speciesId) {
        return Optional.ofNullable(
            com.coolerpromc.ancientcreature.client.species.ClientSpeciesManager.INSTANCE.get(speciesId));
    }

    public static @Nullable ModelPart getBakedGeometry(Identifier geometryId) {
        var baked = BedrockGeometryManager.INSTANCE.getBaked(geometryId);
        return baked == null ? null : baked.root();
    }

    public static boolean hasGeometry(Identifier geometryId) {
        return BedrockGeometryManager.INSTANCE.contains(geometryId);
    }

    public static boolean hasAnimations(Identifier animationFileId) {
        return BedrockAnimationManager.INSTANCE.contains(animationFileId);
    }

    public static boolean hasController(Identifier controllerId) {
        return AnimationControllerManager.INSTANCE.contains(controllerId);
    }

    public static int geometryGeneration() {
        return BedrockGeometryManager.INSTANCE.generation();
    }

    private AncientCreatureClientApi() {
    }
}
