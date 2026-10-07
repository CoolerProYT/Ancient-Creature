package com.coolerpromc.ancientcreature.client.species;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.client.animation.bedrock.AnimationControllerManager;
import com.coolerpromc.ancientcreature.client.animation.bedrock.BedrockAnimationManager;
import com.coolerpromc.ancientcreature.client.model.bedrock.BedrockFormatException;
import com.coolerpromc.ancientcreature.client.model.bedrock.BedrockGeometryManager;
import com.coolerpromc.ancientcreature.molang.MolangExpression;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
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
 * Loads the appearance side of species: {@code ancientcreature/species/*.json},
 * {@code ancientcreature/entity/*.json} (Bedrock client entities) and
 * {@code ancientcreature/render_controllers/*.json}, then resolves each species into a
 * {@link SpeciesAppearance} on first use.
 */
public final class ClientSpeciesManager extends SimplePreparableReloadListener<ClientSpeciesManager.Loaded> {
    public static final Identifier ID = Constants.id("client_species");
    public static final String DIRECTORY = Constants.MODID + "/species";
    public static final String ENTITY_DIRECTORY = Constants.MODID + "/entity";
    public static final String RENDER_CONTROLLER_DIRECTORY = Constants.MODID + "/render_controllers";

    public static final ClientSpeciesManager INSTANCE = new ClientSpeciesManager();

    private static final FileToIdConverter SPECIES = FileToIdConverter.json(DIRECTORY);
    private static final FileToIdConverter ENTITIES = FileToIdConverter.json(ENTITY_DIRECTORY);
    private static final FileToIdConverter RENDER_CONTROLLERS = FileToIdConverter.json(RENDER_CONTROLLER_DIRECTORY);

    record Loaded(Map<Identifier, ClientSpeciesDefinition> definitions, Map<Identifier, BedrockClientEntity> entities, Map<String, BedrockRenderController> renderControllers) {
    }

    private volatile Loaded loaded = new Loaded(Map.of(), Map.of(), Map.of());
    private volatile int generation;

    private final Map<Identifier, Optional<SpeciesAppearance>> appearances = new ConcurrentHashMap<>();
    private volatile long appearanceStamp = Long.MIN_VALUE;
    private final Set<Identifier> warnedMissing = Collections.synchronizedSet(new HashSet<>());

    private ClientSpeciesManager() {
    }

    public @Nullable ClientSpeciesDefinition get(Identifier speciesId) {
        return this.loaded.definitions().get(speciesId);
    }

    /** The species' settings, or defaults when it only has a client entity. */
    public ClientSpeciesDefinition settings(Identifier speciesId) {
        return this.loaded.definitions().getOrDefault(speciesId, ClientSpeciesDefinition.DEFAULT);
    }

    public boolean contains(Identifier speciesId) {
        return this.loaded.definitions().containsKey(speciesId) || this.loaded.entities().containsKey(speciesId);
    }

    public Set<Identifier> ids() {
        Set<Identifier> ids = new LinkedHashSet<>(this.loaded.definitions().keySet());
        ids.addAll(this.loaded.entities().keySet());
        return ids;
    }

    public @Nullable BedrockClientEntity clientEntity(Identifier identifier) {
        return this.loaded.entities().get(identifier);
    }

    public @Nullable BedrockRenderController renderController(String name) {
        return this.loaded.renderControllers().get(name.toLowerCase(Locale.ROOT));
    }

    public int generation() {
        return this.generation;
    }

    /** The resolved appearance, or {@code null} (logged once) when the resource pack does not dress this species. */
    public @Nullable SpeciesAppearance appearance(Identifier speciesId) {
        long stamp = ((long) this.generation << 48) ^ ((long) BedrockAnimationManager.INSTANCE.generation() << 32) ^ ((long) AnimationControllerManager.INSTANCE.generation() << 16) ^ BedrockGeometryManager.INSTANCE.generation();
        if (stamp != this.appearanceStamp) {
            this.appearances.clear();
            this.appearanceStamp = stamp;
        }
        return this.appearances.computeIfAbsent(speciesId, this::resolve).orElse(null);
    }

    private Optional<SpeciesAppearance> resolve(Identifier speciesId) {
        ClientSpeciesDefinition settings = this.loaded.definitions().get(speciesId);
        BedrockClientEntity entity;
        if (settings != null && settings.isLegacy()) {
            entity = LegacyAppearance.toClientEntity(speciesId, settings);
        } else {
            Identifier entityId = settings != null ? settings.clientEntity().orElse(speciesId) : speciesId;
            entity = this.loaded.entities().get(entityId);
            if (entity == null) {
                if (this.warnedMissing.add(speciesId)) {
                    Constants.LOG.warn("No appearance for species '{}': expected a client entity with identifier '{}' under assets/*/{}/ or a species file at assets/{}/{}/{}.json",
                        speciesId, entityId, ENTITY_DIRECTORY, speciesId.getNamespace(), DIRECTORY, speciesId.getPath());
                }
                return Optional.empty();
            }
        }

        List<SpeciesAppearance.ActiveRenderController> controllers = new ArrayList<>();
        for (BedrockClientEntity.ControllerRef ref : entity.renderControllers()) {
            BedrockRenderController controller = this.renderController(ref.name());
            if (controller == null) {
                Constants.LOG.error("Client entity '{}' uses render controller '{}', which is not loaded", entity.identifier(), ref.name());
                continue;
            }
            controllers.add(new SpeciesAppearance.ActiveRenderController(controller, ref.condition()));
        }

        reportDanglingReferences(entity);
        return Optional.of(new SpeciesAppearance(speciesId, entity, List.copyOf(controllers), settings != null ? settings : ClientSpeciesDefinition.DEFAULT));
    }

    private static void reportDanglingReferences(BedrockClientEntity entity) {
        entity.geometry().forEach((key, geometry) -> {
            if (geometry.startsWith(BedrockGeometryManager.LEGACY_PREFIX)) {
                geometry = geometry.substring(BedrockGeometryManager.LEGACY_PREFIX.length());
            }
            String reference = geometry;
            if (!BedrockGeometryManager.INSTANCE.contains(reference)) {
                Constants.LOG.error("Client entity '{}' geometry '{}' refers to '{}', which is not loaded", entity.identifier(), key, reference);
            }
        });
        entity.animations().forEach((key, name) -> {
            if (BedrockAnimationManager.INSTANCE.get(name) == null && !AnimationControllerManager.INSTANCE.contains(name)) {
                Constants.LOG.error("Client entity '{}' animation '{}' refers to '{}', which is neither a loaded animation nor a controller", entity.identifier(), key, name);
            }
        });
    }

    @Override
    protected Loaded prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
        Map<Identifier, ClientSpeciesDefinition> definitions = new LinkedHashMap<>();
        for (Map.Entry<Identifier, Resource> entry : SPECIES.listMatchingResources(resourceManager).entrySet()) {
            Identifier file = entry.getKey();
            Identifier speciesId = SPECIES.fileToId(file);
            try (BufferedReader reader = entry.getValue().openAsReader()) {
                DataResult<ClientSpeciesDefinition> parsed = ClientSpeciesDefinition.CODEC.parse(JsonOps.INSTANCE, JsonParser.parseReader(reader));
                parsed.ifSuccess(definition -> definitions.put(speciesId, definition))
                    .ifError(error -> Constants.LOG.error("Invalid species appearance {} ({}): {}", speciesId, file, error.message()));
            } catch (IOException | RuntimeException e) {
                Constants.LOG.error("Could not read species appearance {}", file, e);
            }
        }

        Map<Identifier, BedrockClientEntity> entities = new LinkedHashMap<>();
        for (Map.Entry<Identifier, Resource> entry : ENTITIES.listMatchingResources(resourceManager).entrySet()) {
            Identifier file = entry.getKey();
            try (BufferedReader reader = entry.getValue().openAsReader()) {
                JsonElement json = JsonParser.parseReader(reader);
                for (BedrockClientEntity entity : BedrockClientEntity.parseFile(json, file.getNamespace())) {
                    if (entities.put(entity.identifier(), entity) != null) {
                        Constants.LOG.warn("Client entity '{}' is defined more than once; {} wins", entity.identifier(), file);
                    }
                }
            } catch (BedrockFormatException e) {
                Constants.LOG.error("Could not parse client entity {}: {}", file, e.getMessage());
            } catch (IOException | RuntimeException e) {
                Constants.LOG.error("Could not read client entity {}", file, e);
            }
        }

        Map<String, BedrockRenderController> renderControllers = new LinkedHashMap<>();
        for (Map.Entry<Identifier, Resource> entry : RENDER_CONTROLLERS.listMatchingResources(resourceManager).entrySet()) {
            Identifier file = entry.getKey();
            try (BufferedReader reader = entry.getValue().openAsReader()) {
                renderControllers.putAll(BedrockRenderController.parseFile(JsonParser.parseReader(reader)));
            } catch (BedrockFormatException e) {
                Constants.LOG.error("Could not parse render controllers {}: {}", file, e.getMessage());
            } catch (IOException | RuntimeException e) {
                Constants.LOG.error("Could not read render controllers {}", file, e);
            }
        }
        return new Loaded(Map.copyOf(definitions), Map.copyOf(entities), Map.copyOf(renderControllers));
    }

    @Override
    protected void apply(Loaded prepared, ResourceManager resourceManager, ProfilerFiller profiler) {
        this.loaded = prepared;
        this.generation++;
        this.warnedMissing.clear();
        Constants.LOG.info("Loaded {} species appearances, {} client entities and {} render controllers",
            prepared.definitions().size(), prepared.entities().size(), prepared.renderControllers().size());
    }

    @Override
    public String getName() {
        return "AncientCreatureClientSpecies";
    }

    /** Converts a format-1 species file into the client entity it describes. */
    static final class LegacyAppearance {
        static BedrockClientEntity toClientEntity(Identifier speciesId, ClientSpeciesDefinition legacy) {
            Map<String, Identifier> textures = new LinkedHashMap<>();
            textures.put("default", legacy.texture().orElseThrow());
            for (ClientSpeciesDefinition.TextureVariant variant : legacy.variants()) {
                textures.put(variant.id().toLowerCase(Locale.ROOT), variant.texture());
            }

            Map<String, String> animations = new LinkedHashMap<>();
            // Geometry and animations from a format-1 pack were authored for the old axis handling and are
            // converted on load so the species looks exactly as it did.
            legacy.animations().ifPresent(file -> BedrockAnimationManager.INSTANCE.getFile(file).keySet().forEach(full -> {
                int dot = full.lastIndexOf('.');
                String converted = BedrockAnimationManager.LEGACY_PREFIX + full;
                animations.putIfAbsent(dot >= 0 ? full.substring(dot + 1) : full, converted);
                animations.putIfAbsent(full, converted);
            }));
            List<BedrockClientEntity.AnimateEntry> animate = new ArrayList<>();
            legacy.controller().ifPresent(controller -> {
                animations.put("ancientcreature_legacy_controller", controller.toString());
                animate.add(new BedrockClientEntity.AnimateEntry("ancientcreature_legacy_controller", MolangExpression.ONE));
            });

            return new BedrockClientEntity(speciesId, Map.of("default", "entity_alphatest"), Collections.unmodifiableMap(textures),
                Map.of("default", BedrockGeometryManager.LEGACY_PREFIX + legacy.geometry().orElseThrow()), Collections.unmodifiableMap(animations),
                new BedrockClientEntity.Scripts(List.of(), List.of(), List.copyOf(animate), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()),
                List.of(), Map.of(), Map.of());
        }
    }
}
