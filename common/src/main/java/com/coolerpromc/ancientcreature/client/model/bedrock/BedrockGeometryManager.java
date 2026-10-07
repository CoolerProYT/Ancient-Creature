package com.coolerpromc.ancientcreature.client.model.bedrock;

import com.coolerpromc.ancientcreature.Constants;
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
import java.util.concurrent.ConcurrentHashMap;

/**
 * Loads every Bedrock geometry under {@code assets/<ns>/ancientcreature/geo/}.
 *
 * <p>Any {@code .json} file is accepted, so Blockbench's default {@code name.geo.json} works as-is.
 * Geometries are found by their Bedrock identifier ({@code geometry.triceratops}), as a client entity
 * refers to them, and also by file id ({@code ancientcreature:triceratops}) for the first geometry in a
 * file, which is how earlier species files refer to them.
 */
public final class BedrockGeometryManager extends SimplePreparableReloadListener<BedrockGeometryManager.Loaded> {
    public static final Identifier ID = Constants.id("bedrock_geometry");
    public static final String DIRECTORY = Constants.MODID + "/geo";

    public static final BedrockGeometryManager INSTANCE = new BedrockGeometryManager();

    private static final FileToIdConverter CONVERTER = new FileToIdConverter(DIRECTORY, ".json");

    record Loaded(Map<String, BedrockGeometry> byIdentifier, Map<Identifier, String> byFile) {
    }

    /**
     * Prefix marking a reference to a geometry written for the axes Ancient Creature used before it
     * followed Bedrock's; such a geometry is converted on bake. Used for format-1 species files.
     */
    public static final String LEGACY_PREFIX = "ancientcreature_legacy_axes|";

    private volatile Loaded loaded = new Loaded(Map.of(), Map.of());
    private final Map<String, Optional<BakedBedrockModel>> baked = new ConcurrentHashMap<>();
    private volatile int generation;

    private BedrockGeometryManager() {
    }

    public int generation() {
        return this.generation;
    }

    /** By Bedrock identifier ({@code geometry.x}, case-insensitive) or by file id ({@code ns:path}). */
    public @Nullable BedrockGeometry getGeometry(String reference) {
        String key = this.resolveKey(reference);
        return key == null ? null : this.loaded.byIdentifier().get(key);
    }

    public @Nullable BedrockGeometry getGeometry(Identifier fileId) {
        return this.getGeometry(fileId.toString());
    }

    public boolean contains(String reference) {
        return this.resolveKey(reference) != null;
    }

    public boolean contains(Identifier fileId) {
        return this.contains(fileId.toString());
    }

    public Set<String> identifiers() {
        return this.loaded.byIdentifier().keySet();
    }

    public Set<Identifier> fileIds() {
        return this.loaded.byFile().keySet();
    }

    private @Nullable String resolveKey(String reference) {
        if (reference.startsWith(LEGACY_PREFIX)) {
            reference = reference.substring(LEGACY_PREFIX.length());
        }
        String lower = reference.toLowerCase(Locale.ROOT);
        if (this.loaded.byIdentifier().containsKey(lower)) {
            return lower;
        }
        Identifier file = Identifier.tryParse(reference);
        return file == null ? null : this.loaded.byFile().get(file);
    }

    /** The baked model, baked on first use and cached until the next reload. */
    public @Nullable BakedBedrockModel getBaked(String reference) {
        String key = this.resolveKey(reference);
        if (key == null) {
            return null;
        }
        boolean legacy = reference.startsWith(LEGACY_PREFIX);
        return this.baked.computeIfAbsent(legacy ? LEGACY_PREFIX + key : key, k -> {
            BedrockGeometry geometry = this.loaded.byIdentifier().get(key);
            try {
                return Optional.of(BedrockModelBaker.bake(legacy ? geometry.fromLegacyAxes() : geometry));
            } catch (RuntimeException e) {
                Constants.LOG.error("Failed to bake Bedrock geometry '{}'", k, e);
                return Optional.empty();
            }
        }).orElse(null);
    }

    public @Nullable BakedBedrockModel getBaked(Identifier fileId) {
        return this.getBaked(fileId.toString());
    }

    @Override
    protected Loaded prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
        Map<String, BedrockGeometry> byIdentifier = new LinkedHashMap<>();
        Map<Identifier, String> byFile = new LinkedHashMap<>();

        for (Map.Entry<Identifier, Resource> entry : CONVERTER.listMatchingResources(resourceManager).entrySet()) {
            Identifier file = entry.getKey();
            Identifier fileId = stripGeoSuffix(CONVERTER.fileToId(file));
            try (BufferedReader reader = entry.getValue().openAsReader()) {
                JsonElement json = JsonParser.parseReader(reader);
                List<BedrockGeometry> geometries = BedrockGeometry.parseFile(json);
                boolean first = true;
                for (BedrockGeometry geometry : geometries) {
                    List<String> problems = geometry.validate();
                    if (!problems.isEmpty()) {
                        Constants.LOG.error("Invalid Bedrock geometry '{}' in {}: {}", geometry.identifier(), file, String.join("; ", problems));
                        continue;
                    }
                    String key = geometry.identifier().toLowerCase(Locale.ROOT);
                    if (byIdentifier.put(key, geometry) != null) {
                        Constants.LOG.warn("Bedrock geometry '{}' is defined more than once; {} wins", geometry.identifier(), file);
                    }
                    if (first) {
                        byFile.put(fileId, key);
                        first = false;
                    }
                }
            } catch (BedrockFormatException e) {
                Constants.LOG.error("Could not parse Bedrock geometry {}: {}", file, e.getMessage());
            } catch (IOException | RuntimeException e) {
                Constants.LOG.error("Could not read Bedrock geometry {}", file, e);
            }
        }
        return new Loaded(Map.copyOf(byIdentifier), Map.copyOf(byFile));
    }

    /** {@code triceratops.geo} → {@code triceratops}, so both naming styles give the same file id. */
    static Identifier stripGeoSuffix(Identifier id) {
        String path = id.getPath();
        return path.endsWith(".geo") ? Identifier.fromNamespaceAndPath(id.getNamespace(), path.substring(0, path.length() - 4)) : id;
    }

    @Override
    protected void apply(Loaded prepared, ResourceManager resourceManager, ProfilerFiller profiler) {
        this.loaded = prepared;
        this.baked.clear();
        this.generation++;
        Constants.LOG.info("Loaded {} Bedrock geometries", prepared.byIdentifier().size());
    }

    @Override
    public String getName() {
        return "AncientCreatureBedrockGeometry";
    }
}
