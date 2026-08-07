package com.coolerpromc.ancientcreature.species;

import com.coolerpromc.ancientcreature.Constants;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class SpeciesReloadListener extends SimplePreparableReloadListener<Map<Identifier, SpeciesDefinition>> {
    public static final Identifier ID = Constants.id("species");
    public static final String DIRECTORY = Constants.MODID + "/species";

    private static final FileToIdConverter CONVERTER = FileToIdConverter.json(DIRECTORY);
    private static final Gson GSON = new Gson();

    private final SpeciesManager manager;
    private final Runnable afterReload;

    public SpeciesReloadListener(SpeciesManager manager, Runnable afterReload) {
        this.manager = manager;
        this.afterReload = afterReload;
    }

    @Override
    protected Map<Identifier, SpeciesDefinition> prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
        Map<Identifier, SpeciesDefinition> loaded = new LinkedHashMap<>();

        for (Map.Entry<Identifier, Resource> entry : CONVERTER.listMatchingResources(resourceManager).entrySet()) {
            Identifier file = entry.getKey();
            Identifier speciesId = CONVERTER.fileToId(file);

            try (BufferedReader reader = entry.getValue().openAsReader()) {
                JsonElement json = GsonHelper.fromJson(GSON, reader, JsonElement.class);
                DataResult<SpeciesDefinition> parsed = SpeciesDefinition.CODEC.parse(JsonOps.INSTANCE, json)
                    .flatMap(SpeciesDefinition::validate);

                parsed.ifSuccess(definition -> {
                        SpeciesDefinition previous = loaded.put(speciesId, definition);
                        if (previous != null) {
                            Constants.LOG.error("Duplicate Ancient Creature species definition for {}; keeping the last one loaded", speciesId);
                        }
                    })
                    .ifError(error -> Constants.LOG.error("Invalid Ancient Creature species definition {} ({}): {}", speciesId, file, error.message()));
            } catch (IOException | RuntimeException e) {
                Constants.LOG.error("Could not read Ancient Creature species definition {} ({})", speciesId, file, e);
            }
        }

        return loaded;
    }

    @Override
    protected void apply(Map<Identifier, SpeciesDefinition> prepared, ResourceManager resourceManager, ProfilerFiller profiler) {
        this.manager.replaceAll(prepared);
        this.afterReload.run();
    }

    @Override
    public String getName() {
        return "AncientCreatureSpecies";
    }
}
