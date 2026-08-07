package com.coolerpromc.ancientcreature.client.species;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.client.animation.bedrock.AnimationControllerManager;
import com.coolerpromc.ancientcreature.client.animation.bedrock.BedrockAnimationManager;
import com.coolerpromc.ancientcreature.client.model.bedrock.BedrockGeometryManager;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jspecify.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public final class ClientSpeciesManager extends SimplePreparableReloadListener<Map<Identifier, ClientSpeciesDefinition>> {
    public static final Identifier ID = Constants.id("client_species");
    public static final String DIRECTORY = Constants.MODID + "/species";

    public static final ClientSpeciesManager INSTANCE = new ClientSpeciesManager();

    private static final FileToIdConverter CONVERTER = FileToIdConverter.json(DIRECTORY);
    private static final Gson GSON = new Gson();

    private volatile Map<Identifier, ClientSpeciesDefinition> definitions = Map.of();
    private volatile int generation;

    private final Set<Identifier> warnedMissing = java.util.Collections.synchronizedSet(new HashSet<>());

    private ClientSpeciesManager() {
    }

    public @Nullable ClientSpeciesDefinition get(Identifier speciesId) {
        return this.definitions.get(speciesId);
    }

    public @Nullable ClientSpeciesDefinition getOrWarn(Identifier speciesId) {
        ClientSpeciesDefinition definition = this.definitions.get(speciesId);
        if (definition == null && this.warnedMissing.add(speciesId)) {
            Constants.LOG.warn("No client species definition for '{}'. Expected a file at assets/{}/{}/{}.json", speciesId, speciesId.getNamespace(), DIRECTORY, speciesId.getPath());
        }
        return definition;
    }

    public boolean contains(Identifier speciesId) {
        return this.definitions.containsKey(speciesId);
    }

    public Set<Identifier> ids() {
        return this.definitions.keySet();
    }

    public int generation() {
        return this.generation;
    }

    @Override
    protected Map<Identifier, ClientSpeciesDefinition> prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
        Map<Identifier, ClientSpeciesDefinition> loaded = new LinkedHashMap<>();

        for (Map.Entry<Identifier, Resource> entry : CONVERTER.listMatchingResources(resourceManager).entrySet()) {
            Identifier file = entry.getKey();
            Identifier speciesId = CONVERTER.fileToId(file);

            try (BufferedReader reader = entry.getValue().openAsReader()) {
                JsonElement json = GsonHelper.fromJson(GSON, reader, JsonElement.class);
                DataResult<ClientSpeciesDefinition> parsed = ClientSpeciesDefinition.CODEC.parse(JsonOps.INSTANCE, json);

                parsed.ifSuccess(definition -> loaded.put(speciesId, definition)).ifError(error -> Constants.LOG.error("Invalid Ancient Creature client species definition {} ({}): {}", speciesId, file, error.message()));
            } catch (IOException | RuntimeException e) {
                Constants.LOG.error("Could not read Ancient Creature client species definition {} ({})", speciesId, file, e);
            }
        }

        return loaded;
    }

    @Override
    protected void apply(Map<Identifier, ClientSpeciesDefinition> prepared, ResourceManager resourceManager, ProfilerFiller profiler) {
        this.definitions = Map.copyOf(prepared);
        this.generation++;
        this.warnedMissing.clear();

        prepared.forEach(ClientSpeciesManager::reportDanglingReferences);

        Constants.LOG.info("Loaded {} Ancient Creature client species definitions", this.definitions.size());
    }

    private static void reportDanglingReferences(Identifier speciesId, ClientSpeciesDefinition definition) {
        if (!BedrockGeometryManager.INSTANCE.contains(definition.geometry())) {
            Constants.LOG.error("Client species '{}' references missing geometry '{}'", speciesId, definition.geometry());
        }
        definition.animations().ifPresent(animations -> {
            if (!BedrockAnimationManager.INSTANCE.contains(animations)) {
                Constants.LOG.error("Client species '{}' references missing animation file '{}'", speciesId, animations);
            }
        });
        definition.controller().ifPresent(controller -> {
            if (!AnimationControllerManager.INSTANCE.contains(controller)) {
                Constants.LOG.error("Client species '{}' references missing animation controller '{}'", speciesId, controller);
            }
        });
    }

    @Override
    public String getName() {
        return "AncientCreatureClientSpecies";
    }
}
