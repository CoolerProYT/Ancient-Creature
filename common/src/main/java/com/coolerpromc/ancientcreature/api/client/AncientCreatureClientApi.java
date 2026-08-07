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

/**
 * Public entry point for client-side extensions.
 *
 * <p>Kept separate from {@link com.coolerpromc.ancientcreature.api.AncientCreatureApi} so a dedicated
 * server never classloads any of it. Only call this from a client entrypoint.
 *
 * <h2>Adding an animation query</h2>
 * <pre>{@code
 * // during client initialization:
 * AncientCreatureClientApi.registerNumberQuery("wing_beat",
 *     (state, secondsInState) -> Math.sin(state.ageInTicks * 0.3) * 0.5 + 0.5);
 * }</pre>
 *
 * A controller can then use it:
 * <pre>{@code
 * { "glide": "query.wing_beat > 0.8" }
 * }</pre>
 */
public final class AncientCreatureClientApi {

    /**
     * Adds a numeric {@code query.<name>} for animation controllers.
     *
     * @param name bare name, without the {@code query.} prefix
     * @throws IllegalArgumentException if the name is taken or shadows a built-in query
     */
    public static void registerNumberQuery(String name, CreatureQueryRegistry.NumberQuery query) {
        CreatureQueryRegistry.registerNumber(name, query);
    }

    /**
     * Adds a string {@code query.<name>}, comparable with {@code ==} and {@code !=}.
     *
     * @throws IllegalArgumentException if the name is taken or shadows a built-in query
     */
    public static void registerStringQuery(String name, CreatureQueryRegistry.StringQuery query) {
        CreatureQueryRegistry.registerString(name, query);
    }

    /** The visual definition currently loaded for a species, if the resource pack provides one. */
    public static Optional<ClientSpeciesDefinition> getClientSpecies(Identifier speciesId) {
        return Optional.ofNullable(
            com.coolerpromc.ancientcreature.client.species.ClientSpeciesManager.INSTANCE.get(speciesId));
    }

    /**
     * The baked part tree for a geometry file id, baking it on first use.
     *
     * <p>Cached until the next resource reload. Returns {@code null} if the geometry is not loaded.
     */
    public static @Nullable ModelPart getBakedGeometry(Identifier geometryId) {
        return BedrockGeometryManager.INSTANCE.getBakedModel(geometryId);
    }

    /** Whether a geometry file is loaded. */
    public static boolean hasGeometry(Identifier geometryId) {
        return BedrockGeometryManager.INSTANCE.contains(geometryId);
    }

    /** Whether an animation file is loaded. */
    public static boolean hasAnimations(Identifier animationFileId) {
        return BedrockAnimationManager.INSTANCE.contains(animationFileId);
    }

    /** Whether an animation controller is loaded. */
    public static boolean hasController(Identifier controllerId) {
        return AnimationControllerManager.INSTANCE.contains(controllerId);
    }

    /**
     * Bumped on every resource reload.
     *
     * <p>Compare against a stored value to know when to drop anything cached from geometry.
     */
    public static int geometryGeneration() {
        return BedrockGeometryManager.INSTANCE.generation();
    }

    private AncientCreatureClientApi() {
    }
}
