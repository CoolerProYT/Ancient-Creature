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

public final class AnimationControllerManager extends SimplePreparableReloadListener<Map<Identifier, AnimationController>> {
    public static final Identifier ID = Constants.id("animation_controllers");
    public static final String DIRECTORY = Constants.MODID + "/animation_controllers";

    public static final AnimationControllerManager INSTANCE = new AnimationControllerManager();

    private static final FileToIdConverter CONVERTER = new FileToIdConverter(DIRECTORY, ".controller.json");
    private static final Gson GSON = new Gson();

    private volatile Map<Identifier, AnimationController> controllers = Map.of();

    private AnimationControllerManager() {
    }

    public @Nullable AnimationController get(Identifier id) {
        return this.controllers.get(id);
    }

    public boolean contains(Identifier id) {
        return this.controllers.containsKey(id);
    }

    public java.util.Set<Identifier> ids() {
        return this.controllers.keySet();
    }

    @Override
    protected Map<Identifier, AnimationController> prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
        Map<Identifier, AnimationController> loaded = new LinkedHashMap<>();

        for (Map.Entry<Identifier, Resource> entry : CONVERTER.listMatchingResources(resourceManager).entrySet()) {
            Identifier file = entry.getKey();
            Identifier controllerId = CONVERTER.fileToId(file);

            try (BufferedReader reader = entry.getValue().openAsReader()) {
                JsonElement json = GsonHelper.fromJson(GSON, reader, JsonElement.class);
                DataResult<AnimationController> parsed = AnimationController.CODEC.parse(JsonOps.INSTANCE, json)
                    .flatMap(AnimationController::validate);

                parsed.ifSuccess(controller -> loaded.put(controllerId, controller))
                    .ifError(error -> Constants.LOG.error("Invalid Ancient Creature animation controller {} ({}): {}",
                        controllerId, file, error.message()));
            } catch (IOException | RuntimeException e) {
                Constants.LOG.error("Could not read Ancient Creature animation controller {} ({})", controllerId, file, e);
            }
        }

        return loaded;
    }

    @Override
    protected void apply(Map<Identifier, AnimationController> prepared, ResourceManager resourceManager, ProfilerFiller profiler) {
        this.controllers = Map.copyOf(prepared);
        Constants.LOG.info("Loaded {} Ancient Creature animation controllers", this.controllers.size());
    }

    @Override
    public String getName() {
        return "AncientCreatureAnimationControllers";
    }
}
