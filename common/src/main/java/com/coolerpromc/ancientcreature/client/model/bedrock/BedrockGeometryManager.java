package com.coolerpromc.ancientcreature.client.model.bedrock;

import com.coolerpromc.ancientcreature.Constants;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.client.model.geom.ModelPart;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class BedrockGeometryManager extends SimplePreparableReloadListener<Map<Identifier, BedrockGeometry>> {
    public static final Identifier ID = Constants.id("bedrock_geometry");
    public static final String DIRECTORY = Constants.MODID + "/geo";

    public static final BedrockGeometryManager INSTANCE = new BedrockGeometryManager();

    private static final FileToIdConverter CONVERTER = new FileToIdConverter(DIRECTORY, ".geo.json");
    private static final Gson GSON = new Gson();

    private volatile Map<Identifier, BedrockGeometry> geometries = Map.of();
    private volatile Map<Identifier, ModelPart> baked = new LinkedHashMap<>();
    private volatile int generation;

    private BedrockGeometryManager() {
    }

    public int generation() {
        return this.generation;
    }

    public @Nullable BedrockGeometry getGeometry(Identifier id) {
        return this.geometries.get(id);
    }

    public boolean contains(Identifier id) {
        return this.geometries.containsKey(id);
    }

    public java.util.Set<Identifier> ids() {
        return this.geometries.keySet();
    }

    public @Nullable ModelPart getBakedModel(Identifier id) {
        ModelPart cached = this.baked.get(id);
        if (cached != null) {
            return cached;
        }

        BedrockGeometry geometry = this.geometries.get(id);
        if (geometry == null) {
            return null;
        }

        try {
            ModelPart part = BedrockModelBaker.bake(geometry);
            Map<Identifier, ModelPart> updated = new LinkedHashMap<>(this.baked);
            updated.put(id, part);
            this.baked = updated;
            return part;
        } catch (RuntimeException e) {
            Constants.LOG.error("Failed to bake Ancient Creature geometry '{}'", id, e);
            return null;
        }
    }

    @Override
    protected Map<Identifier, BedrockGeometry> prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
        Map<Identifier, BedrockGeometry> loaded = new LinkedHashMap<>();

        for (Map.Entry<Identifier, Resource> entry : CONVERTER.listMatchingResources(resourceManager).entrySet()) {
            Identifier file = entry.getKey();
            Identifier geometryId = CONVERTER.fileToId(file);

            try (BufferedReader reader = entry.getValue().openAsReader()) {
                JsonElement json = GsonHelper.fromJson(GSON, reader, JsonElement.class);
                DataResult<List<BedrockGeometry>> parsed = BedrockGeometry.FILE_CODEC.parse(JsonOps.INSTANCE, json);

                parsed.ifSuccess(list -> {
                    if (list.isEmpty()) {
                        Constants.LOG.error("Ancient Creature geometry {} ({}) contains no 'minecraft:geometry' entries", geometryId, file);
                        return;
                    }

                    for (int index = 0; index < list.size(); index++) {
                        BedrockGeometry geometry = list.get(index);
                        DataResult<BedrockGeometry> validated = geometry.validate();
                        if (validated.error().isPresent()) {
                            Constants.LOG.error("Invalid Ancient Creature geometry {} ({}): {}", geometryId, file, validated.error().get().message());
                            continue;
                        }
                        Identifier id = index == 0 ? geometryId : idOf(geometryId, geometry.identifier());
                        loaded.put(id, geometry);
                    }
                }).ifError(error -> Constants.LOG.error("Could not parse Ancient Creature geometry {} ({}): {}", geometryId, file, error.message()));
            } catch (IOException | RuntimeException e) {
                Constants.LOG.error("Could not read Ancient Creature geometry {} ({})", geometryId, file, e);
            }
        }

        return loaded;
    }

    private static Identifier idOf(Identifier fileId, String bedrockIdentifier) {
        String name = bedrockIdentifier.startsWith("geometry.") ? bedrockIdentifier.substring("geometry.".length()) : bedrockIdentifier;
        return Identifier.fromNamespaceAndPath(fileId.getNamespace(), name.replace('.', '/'));
    }

    @Override
    protected void apply(Map<Identifier, BedrockGeometry> prepared, ResourceManager resourceManager, ProfilerFiller profiler) {
        this.geometries = Map.copyOf(prepared);
        this.baked = new LinkedHashMap<>();
        this.generation++;
        Constants.LOG.info("Loaded {} Bedrock geometries", this.geometries.size());
    }

    @Override
    public String getName() {
        return "AncientCreatureBedrockGeometry";
    }
}
