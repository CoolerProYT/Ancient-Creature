package com.coolerpromc.ancientcreature.client.animation.bedrock;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.client.model.bedrock.BedrockFormatException;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jspecify.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

/**
 * Loads every animation controller under {@code assets/<ns>/ancientcreature/animation_controllers/}.
 *
 * <p>Bedrock-format controllers are found by name ({@code controller.animation.x.move}); an earlier
 * single-controller file is found by its file id ({@code ancientcreature:triceratops}).
 */
public final class AnimationControllerManager extends SimplePreparableReloadListener<Map<String, AnimationController>> {
    public static final Identifier ID = Constants.id("animation_controllers");
    public static final String DIRECTORY = Constants.MODID + "/animation_controllers";

    public static final AnimationControllerManager INSTANCE = new AnimationControllerManager();

    private volatile int generation;

    public int generation() {
        return this.generation;
    }

    private static final FileToIdConverter CONVERTER = new FileToIdConverter(DIRECTORY, ".json");

    private volatile Map<String, AnimationController> controllers = Map.of();

    private AnimationControllerManager() {
    }

    public @Nullable AnimationController get(String name) {
        return this.controllers.get(name.toLowerCase(Locale.ROOT));
    }

    public @Nullable AnimationController get(Identifier legacyFile) {
        return this.get(legacyFile.toString());
    }

    public boolean contains(String name) {
        return this.controllers.containsKey(name.toLowerCase(Locale.ROOT));
    }

    public boolean contains(Identifier legacyFile) {
        return this.contains(legacyFile.toString());
    }

    public Set<String> names() {
        return this.controllers.keySet();
    }

    @Override
    protected Map<String, AnimationController> prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
        Map<String, AnimationController> loaded = new LinkedHashMap<>();
        for (Map.Entry<Identifier, Resource> entry : CONVERTER.listMatchingResources(resourceManager).entrySet()) {
            Identifier file = entry.getKey();
            Identifier fileId = strip(CONVERTER.fileToId(file));
            try (BufferedReader reader = entry.getValue().openAsReader()) {
                JsonElement json = JsonParser.parseReader(reader);
                for (Map.Entry<String, AnimationController> controller : AnimationController.parseFile(json, fileId.toString()).entrySet()) {
                    List<String> problems = controller.getValue().validate();
                    if (!problems.isEmpty()) {
                        Constants.LOG.error("Invalid animation controller '{}' in {}: {}", controller.getKey(), file, String.join("; ", problems));
                        continue;
                    }
                    if (loaded.put(controller.getKey().toLowerCase(Locale.ROOT), controller.getValue()) != null) {
                        Constants.LOG.warn("Animation controller '{}' is defined more than once; {} wins", controller.getKey(), file);
                    }
                }
            } catch (BedrockFormatException e) {
                Constants.LOG.error("Could not parse animation controllers {}: {}", file, e.getMessage());
            } catch (IOException | RuntimeException e) {
                Constants.LOG.error("Could not read animation controllers {}", file, e);
            }
        }
        return Map.copyOf(loaded);
    }

    private static Identifier strip(Identifier id) {
        String path = id.getPath();
        for (String suffix : new String[]{".controller", ".animation_controllers", ".animation_controller", ".ac"}) {
            if (path.endsWith(suffix)) {
                return Identifier.fromNamespaceAndPath(id.getNamespace(), path.substring(0, path.length() - suffix.length()));
            }
        }
        return id;
    }

    @Override
    protected void apply(Map<String, AnimationController> prepared, ResourceManager resourceManager, ProfilerFiller profiler) {
        this.controllers = prepared;
        this.generation++;
        Constants.LOG.info("Loaded {} animation controllers", prepared.size());
    }

    @Override
    public String getName() {
        return "AncientCreatureAnimationControllers";
    }
}
