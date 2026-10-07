package com.coolerpromc.ancientcreature.species;

import com.coolerpromc.ancientcreature.client.animation.bedrock.AnimationController;
import com.coolerpromc.ancientcreature.client.animation.bedrock.BedrockAnimation;
import com.coolerpromc.ancientcreature.client.model.bedrock.BakedBedrockModel;
import com.coolerpromc.ancientcreature.client.model.bedrock.BedrockBone;
import com.coolerpromc.ancientcreature.client.model.bedrock.BedrockCube;
import com.coolerpromc.ancientcreature.client.model.bedrock.BedrockGeometry;
import com.coolerpromc.ancientcreature.client.model.bedrock.BedrockModelBaker;
import com.coolerpromc.ancientcreature.client.species.BedrockClientEntity;
import com.coolerpromc.ancientcreature.client.species.BedrockRenderController;
import com.coolerpromc.ancientcreature.client.species.ClientSpeciesDefinition;
import com.coolerpromc.ancientcreature.molang.MolangContext;
import com.coolerpromc.ancientcreature.molang.MolangResources;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import net.minecraft.SharedConstants;
import net.minecraft.resources.Identifier;
import net.minecraft.server.Bootstrap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Every shipped species must have a complete, self-consistent Bedrock resource set: a client entity,
 * geometry that bakes, render controllers whose texture arrays line up with the server's variants,
 * textures that exist at the declared resolution, and animations and controllers that only refer to
 * things that exist.
 */
class ShippedAppearanceTest {
    private static final Path RESOURCES = Path.of("src/main/resources");
    private static final Path ASSETS = RESOURCES.resolve("assets/ancientcreature/ancientcreature");

    private static final Map<String, BedrockGeometry> GEOMETRY = new HashMap<>();
    private static final Map<String, BedrockAnimation> ANIMATIONS = new HashMap<>();
    private static final Map<String, AnimationController> CONTROLLERS = new HashMap<>();
    private static final Map<String, BedrockRenderController> RENDER_CONTROLLERS = new HashMap<>();
    private static final Map<Identifier, BedrockClientEntity> ENTITIES = new HashMap<>();

    @BeforeAll
    static void load() throws Exception {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        for (Path file : files("geo")) {
            for (BedrockGeometry geometry : BedrockGeometry.parseFile(json(file))) {
                assertTrue(geometry.validate().isEmpty(), () -> file + ": " + geometry.validate());
                GEOMETRY.put(geometry.identifier().toLowerCase(Locale.ROOT), geometry);
            }
        }
        for (Path file : files("animations")) {
            BedrockAnimation.parseFile(json(file)).forEach((name, animation) -> ANIMATIONS.put(name.toLowerCase(Locale.ROOT), animation));
        }
        for (Path file : files("animation_controllers")) {
            AnimationController.parseFile(json(file), "legacy:" + file.getFileName()).forEach((name, controller) -> {
                assertTrue(controller.validate().isEmpty(), () -> file + ": " + controller.validate());
                CONTROLLERS.put(name.toLowerCase(Locale.ROOT), controller);
            });
        }
        for (Path file : files("render_controllers")) {
            RENDER_CONTROLLERS.putAll(BedrockRenderController.parseFile(json(file)));
        }
        for (Path file : files("entity")) {
            for (BedrockClientEntity entity : BedrockClientEntity.parseFile(json(file), "ancientcreature")) {
                ENTITIES.put(entity.identifier(), entity);
            }
        }
    }

    static Stream<String> species() throws IOException {
        try (Stream<Path> files = Files.list(RESOURCES.resolve("data/ancientcreature/ancientcreature/species"))) {
            return files.map(p -> p.getFileName().toString().replace(".json", "")).sorted().toList().stream();
        }
    }

    private static List<Path> files(String directory) throws IOException {
        try (Stream<Path> files = Files.list(ASSETS.resolve(directory))) {
            return files.filter(p -> p.toString().endsWith(".json")).sorted().toList();
        }
    }

    private static JsonElement json(Path path) throws IOException {
        try (Reader reader = Files.newBufferedReader(path)) {
            return JsonParser.parseReader(reader);
        }
    }

    private static SpeciesDefinition server(String species) throws IOException {
        return SpeciesDefinition.CODEC.parse(JsonOps.INSTANCE, json(RESOURCES.resolve("data/ancientcreature/ancientcreature/species/" + species + ".json")))
            .getOrThrow(message -> new AssertionError(species + ": " + message));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("species")
    void everyShippedSpeciesIsComplete(String species) throws Exception {
        SpeciesDefinition server = server(species);
        assertTrue(server.validate().result().isPresent(), () -> species + ": " + server.validate().error().orElseThrow().message());
        Identifier loot = server.lootTable().orElseThrow(() -> new AssertionError(species + " declares no loot table"));
        assertTrue(Files.exists(RESOURCES.resolve("data/" + loot.getNamespace() + "/loot_table/" + loot.getPath() + ".json")), species + " loot table " + loot + " is missing");
        assertFalse(server.variants().isEmpty(), species + " has no skin variants");
        assertTrue(server.variants().stream().mapToInt(SpeciesVariant::weight).sum() > 0, species + " variants all have zero weight");

        ClientSpeciesDefinition settings = ClientSpeciesDefinition.CODEC.parse(JsonOps.INSTANCE, json(ASSETS.resolve("species/" + species + ".json")))
            .getOrThrow(message -> new AssertionError(species + " appearance: " + message));
        assertEquals(2, settings.formatVersion(), species + " appearance should be format_version 2");

        Identifier id = Identifier.fromNamespaceAndPath("ancientcreature", species);
        BedrockClientEntity entity = ENTITIES.get(settings.clientEntity().orElse(id));
        assertNotNull(entity, species + " has no client entity");

        // geometry
        String geometryId = entity.geometry().get("default");
        assertNotNull(geometryId, species + " has no default geometry");
        BedrockGeometry geometry = GEOMETRY.get(geometryId.toLowerCase(Locale.ROOT));
        assertNotNull(geometry, species + " geometry " + geometryId + " is not loaded");
        BakedBedrockModel baked = BedrockModelBaker.bake(geometry);

        // textures exist and match the geometry's UV space
        for (Map.Entry<String, Identifier> texture : entity.textures().entrySet()) {
            Path png = RESOURCES.resolve("assets/" + texture.getValue().getNamespace() + "/" + texture.getValue().getPath());
            assertTrue(Files.exists(png), species + " texture " + texture.getKey() + " missing at " + png);
            BufferedImage image = ImageIO.read(png.toFile());
            assertEquals((float) geometry.textureWidth() / geometry.textureHeight(), (float) image.getWidth() / image.getHeight(), 1.0E-4F,
                species + " texture " + texture.getKey() + " has the wrong aspect for its geometry");
            assertEquals(0, image.getWidth() % geometry.textureWidth(), species + " texture " + texture.getKey() + " is not a whole multiple of the UV size");
        }

        // render controllers line up with the server's variant order
        assertFalse(entity.renderControllers().isEmpty(), species + " has no render controller");
        for (BedrockClientEntity.ControllerRef ref : entity.renderControllers()) {
            BedrockRenderController rc = RENDER_CONTROLLERS.get(ref.name().toLowerCase(Locale.ROOT));
            assertNotNull(rc, species + " render controller " + ref.name() + " is missing");
            Object[] skins = rc.array("skins");
            assertNotNull(skins, species + " render controller has no Array.skins");
            assertEquals(server.variants().size(), skins.length, species + ": one skin per server variant");
            for (int i = 0; i < skins.length; i++) {
                MolangResources.ResourceRef ref2 = (MolangResources.ResourceRef) skins[i];
                assertEquals(server.variants().get(i).id(), ref2.name(), species + " skin " + i + " is out of order with the server variants");
                assertTrue(entity.textures().containsKey(ref2.name()), species + " has no texture '" + ref2.name() + "'");
            }
            // gear layers follow query.armor_tier and query.is_saddled
            assertEquals(List.of("default"), layers(rc, 0, 0, 0), species + " bare");
            assertEquals(List.of("default", "armor_diamond", "saddle"), layers(rc, 0, 3, 1), species + " geared");
            assertEquals(List.of("default", "armor_iron"), layers(rc, 0, 1, 0), species + " barded");
            for (String layer : layers(rc, 0, 4, 1)) {
                assertTrue(entity.textures().containsKey(layer), species + " has no texture '" + layer + "'");
            }
            Object geometryRef = rc.geometry().evaluateObject(new MolangContext().resources(rc));
            assertInstanceOf(MolangResources.ResourceRef.class, geometryRef);
            assertTrue(entity.geometry().containsKey(((MolangResources.ResourceRef) geometryRef).name()));
        }

        // animations and controllers resolve, and only target bones that exist
        for (BedrockClientEntity.AnimateEntry entry : entity.scripts().animate()) {
            String target = entity.animations().getOrDefault(entry.name().toLowerCase(Locale.ROOT), entry.name()).toLowerCase(Locale.ROOT);
            assertTrue(CONTROLLERS.containsKey(target) || ANIMATIONS.containsKey(target), species + " animates unknown '" + entry.name() + "'");
            AnimationController controller = CONTROLLERS.get(target);
            if (controller != null) {
                controller.states().forEach((stateName, state) -> {
                    for (AnimationController.StateAnimation animation : state.animations()) {
                        String full = entity.animations().get(animation.name().toLowerCase(Locale.ROOT));
                        assertNotNull(full, species + " controller state " + stateName + " plays '" + animation.name() + "', which the client entity does not name");
                        assertTrue(ANIMATIONS.containsKey(full.toLowerCase(Locale.ROOT)), species + " animation " + full + " is not loaded");
                    }
                });
            }
        }
        for (String full : entity.animations().values()) {
            BedrockAnimation animation = ANIMATIONS.get(full.toLowerCase(Locale.ROOT));
            if (animation == null) {
                assertTrue(CONTROLLERS.containsKey(full.toLowerCase(Locale.ROOT)), species + " names unknown animation " + full);
                continue;
            }
            for (String bone : animation.bones().keySet()) {
                assertNotNull(baked.bone(bone), species + " animation " + full + " targets missing bone " + bone);
            }
        }
    }

    private static List<String> layers(BedrockRenderController rc, int variant, int armorTier, int saddled) {
        MolangContext ctx = new MolangContext(new com.coolerpromc.ancientcreature.molang.MolangVariables(), (name, args) -> switch (name) {
            case "variant" -> variant;
            case "armor_tier" -> armorTier;
            case "is_saddled" -> saddled;
            default -> Double.NaN;
        }).resources(rc);
        List<String> out = new ArrayList<>();
        for (var texture : rc.textures()) {
            if (texture.evaluateObject(ctx) instanceof MolangResources.ResourceRef ref) {
                out.add(ref.name());
            }
        }
        return out;
    }

    /**
     * The eye must be somewhere on the creature: line-of-sight and {@code isEyeInFluid} start there,
     * so an eye above the model sees over blocks the creature is standing behind.
     */
    @ParameterizedTest(name = "{0}")
    @MethodSource("species")
    void hitboxesFitTheirModel(String species) throws Exception {
        SpeciesDefinition definition = server(species);
        BedrockGeometry geometry = GEOMETRY.get("geometry." + species);
        assertNotNull(geometry, species);
        float lowest = Float.MAX_VALUE;
        float highest = -Float.MAX_VALUE;
        for (BedrockBone bone : geometry.bones()) {
            for (BedrockCube cube : bone.cubes()) {
                lowest = Math.min(lowest, cube.origin().y() / 16.0F);
                highest = Math.max(highest, (cube.origin().y() + cube.size().y()) / 16.0F);
            }
        }
        float eye = definition.physical().resolvedEyeHeight();
        float top = highest;
        float bottom = lowest;
        assertTrue(eye <= top, () -> species + " eye " + eye + " is above its model top " + top);
        assertTrue(eye >= bottom, () -> species + " eye " + eye + " is below its model bottom " + bottom);
        assertTrue(eye <= definition.physical().height(), () -> species + " eye " + eye + " is outside its hitbox");
    }

    @Test
    void everyGeometryAndAnimationFileIsUsed() {
        Set<String> usedGeometry = new HashSet<>();
        Set<String> usedAnimations = new HashSet<>();
        for (BedrockClientEntity entity : ENTITIES.values()) {
            entity.geometry().values().forEach(g -> usedGeometry.add(g.toLowerCase(Locale.ROOT)));
            entity.animations().values().forEach(a -> usedAnimations.add(a.toLowerCase(Locale.ROOT)));
        }
        Set<String> unusedGeometry = new TreeSet<>(GEOMETRY.keySet());
        unusedGeometry.removeAll(usedGeometry);
        assertTrue(unusedGeometry.isEmpty(), () -> "geometry no client entity uses: " + unusedGeometry);
        Set<String> unusedAnimations = new TreeSet<>(ANIMATIONS.keySet());
        unusedAnimations.removeAll(usedAnimations);
        assertTrue(unusedAnimations.isEmpty(), () -> "animations no client entity names: " + unusedAnimations);
    }
}
