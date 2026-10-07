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
 * Loads every Bedrock animation under {@code assets/<ns>/ancientcreature/animations/}.
 *
 * <p>Animations are global and found by full name ({@code animation.triceratops.idle}), as in
 * Bedrock. The per-file grouping is kept as well, because earlier species files name an animation file
 * and refer to its clips by short name.
 */
public final class BedrockAnimationManager extends SimplePreparableReloadListener<BedrockAnimationManager.Loaded> {
    public static final Identifier ID = Constants.id("bedrock_animations");
    public static final String DIRECTORY = Constants.MODID + "/animations";

    public static final BedrockAnimationManager INSTANCE = new BedrockAnimationManager();

    private volatile int generation;

    public int generation() {
        return this.generation;
    }

    private static final FileToIdConverter CONVERTER = new FileToIdConverter(DIRECTORY, ".json");

    record Loaded(Map<String, BedrockAnimation> byName, Map<Identifier, Map<String, BedrockAnimation>> byFile) {
    }

    private volatile Loaded loaded = new Loaded(Map.of(), Map.of());

    private BedrockAnimationManager() {
    }

    /** See {@link com.coolerpromc.ancientcreature.client.model.bedrock.BedrockGeometryManager#LEGACY_PREFIX}. */
    public static final String LEGACY_PREFIX = com.coolerpromc.ancientcreature.client.model.bedrock.BedrockGeometryManager.LEGACY_PREFIX;

    private final Map<String, BedrockAnimation> legacy = new java.util.concurrent.ConcurrentHashMap<>();

    /** By full name, case-insensitive. A {@link #LEGACY_PREFIX}ed name gives the axis-converted animation. */
    public @Nullable BedrockAnimation get(String name) {
        if (name.startsWith(LEGACY_PREFIX)) {
            String key = name.substring(LEGACY_PREFIX.length()).toLowerCase(Locale.ROOT);
            BedrockAnimation source = this.loaded.byName().get(key);
            return source == null ? null : this.legacy.computeIfAbsent(key, k -> source.fromLegacyAxes());
        }
        return this.loaded.byName().get(name.toLowerCase(Locale.ROOT));
    }

    /** The animations of one file, keyed by full name. */
    public Map<String, BedrockAnimation> getFile(Identifier file) {
        return this.loaded.byFile().getOrDefault(file, Map.of());
    }

    public boolean contains(Identifier file) {
        return this.loaded.byFile().containsKey(file);
    }

    public Set<Identifier> ids() {
        return this.loaded.byFile().keySet();
    }

    public Set<String> names() {
        return this.loaded.byName().keySet();
    }

    @Override
    protected Loaded prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
        Map<String, BedrockAnimation> byName = new LinkedHashMap<>();
        Map<Identifier, Map<String, BedrockAnimation>> byFile = new LinkedHashMap<>();
        for (Map.Entry<Identifier, Resource> entry : CONVERTER.listMatchingResources(resourceManager).entrySet()) {
            Identifier file = entry.getKey();
            Identifier fileId = strip(CONVERTER.fileToId(file));
            try (BufferedReader reader = entry.getValue().openAsReader()) {
                JsonElement json = JsonParser.parseReader(reader);
                Map<String, BedrockAnimation> animations = BedrockAnimation.parseFile(json);
                Map<String, BedrockAnimation> lower = new LinkedHashMap<>();
                animations.forEach((name, animation) -> {
                    String key = name.toLowerCase(Locale.ROOT);
                    lower.put(key, animation);
                    if (byName.put(key, animation) != null) {
                        Constants.LOG.warn("Bedrock animation '{}' is defined more than once; {} wins", name, file);
                    }
                });
                byFile.put(fileId, Collections.unmodifiableMap(lower));
            } catch (BedrockFormatException e) {
                Constants.LOG.error("Could not parse Bedrock animations {}: {}", file, e.getMessage());
            } catch (IOException | RuntimeException e) {
                Constants.LOG.error("Could not read Bedrock animations {}", file, e);
            }
        }
        return new Loaded(Map.copyOf(byName), Map.copyOf(byFile));
    }

    private static Identifier strip(Identifier id) {
        String path = id.getPath();
        return path.endsWith(".animation") ? Identifier.fromNamespaceAndPath(id.getNamespace(), path.substring(0, path.length() - ".animation".length())) : id;
    }

    @Override
    protected void apply(Loaded prepared, ResourceManager resourceManager, ProfilerFiller profiler) {
        this.loaded = prepared;
        this.legacy.clear();
        this.generation++;
        Constants.LOG.info("Loaded {} Bedrock animations from {} files", prepared.byName().size(), prepared.byFile().size());
    }

    @Override
    public String getName() {
        return "AncientCreatureBedrockAnimations";
    }
}
