package com.coolerpromc.ancientcreature.client.animation.bedrock;

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
import org.jspecify.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public final class BedrockAnimationManager extends SimplePreparableReloadListener<Map<Identifier, Map<String, BedrockAnimation>>> {
    public static final Identifier ID = Constants.id("bedrock_animations");
    public static final String DIRECTORY = Constants.MODID + "/animations";

    public static final BedrockAnimationManager INSTANCE = new BedrockAnimationManager();

    private static final FileToIdConverter CONVERTER = new FileToIdConverter(DIRECTORY, ".animation.json");
    private static final Gson GSON = new Gson();

    private volatile Map<Identifier, Map<String, BedrockAnimation>> files = Map.of();

    private BedrockAnimationManager() {
    }

    public Map<String, BedrockAnimation> getFile(Identifier id) {
        return this.files.getOrDefault(id, Map.of());
    }

    public @Nullable BedrockAnimation get(Identifier file, String name) {
        Map<String, BedrockAnimation> animations = this.files.get(file);
        if (animations == null) {
            return null;
        }
        BedrockAnimation exact = animations.get(name);
        if (exact != null) {
            return exact;
        }
        for (Map.Entry<String, BedrockAnimation> entry : animations.entrySet()) {
            if (shortName(entry.getKey()).equals(name)) {
                return entry.getValue();
            }
        }
        return null;
    }

    public boolean contains(Identifier file) {
        return this.files.containsKey(file);
    }

    public Set<Identifier> ids() {
        return this.files.keySet();
    }

    private static String shortName(String bedrockName) {
        int dot = bedrockName.lastIndexOf('.');
        return dot >= 0 ? bedrockName.substring(dot + 1) : bedrockName;
    }

    @Override
    protected Map<Identifier, Map<String, BedrockAnimation>> prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
        Map<Identifier, Map<String, BedrockAnimation>> loaded = new LinkedHashMap<>();

        for (Map.Entry<Identifier, Resource> entry : CONVERTER.listMatchingResources(resourceManager).entrySet()) {
            Identifier file = entry.getKey();
            Identifier animationId = CONVERTER.fileToId(file);

            try (BufferedReader reader = entry.getValue().openAsReader()) {
                JsonElement json = GsonHelper.fromJson(GSON, reader, JsonElement.class);
                DataResult<Map<String, BedrockAnimation>> parsed = BedrockAnimation.FILE_CODEC.parse(JsonOps.INSTANCE, json);

                parsed.ifSuccess(animations -> {
                    Map<String, BedrockAnimation> named = new LinkedHashMap<>();
                    animations.forEach((name, animation) -> named.put(name, animation.withName(name)));
                    loaded.put(animationId, Map.copyOf(named));
                }).ifError(error -> Constants.LOG.error("Could not parse Ancient Creature animations {} ({}): {}",
                    animationId, file, error.message()));
            } catch (IOException | RuntimeException e) {
                Constants.LOG.error("Could not read Ancient Creature animations {} ({})", animationId, file, e);
            }
        }

        return loaded;
    }

    @Override
    protected void apply(Map<Identifier, Map<String, BedrockAnimation>> prepared, ResourceManager resourceManager, ProfilerFiller profiler) {
        this.files = Map.copyOf(prepared);
        int total = this.files.values().stream().mapToInt(Map::size).sum();
        Constants.LOG.info("Loaded {} Bedrock animations from {} files", total, this.files.size());
    }

    @Override
    public String getName() {
        return "AncientCreatureBedrockAnimations";
    }
}
