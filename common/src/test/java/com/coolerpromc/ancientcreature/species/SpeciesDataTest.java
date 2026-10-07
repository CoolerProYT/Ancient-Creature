package com.coolerpromc.ancientcreature.species;

import com.coolerpromc.ancientcreature.api.AncientCreatureApi;
import com.coolerpromc.ancientcreature.entity.Species;
import com.coolerpromc.ancientcreature.entity.behavior.BehaviorProfiles;
import com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorComponent;
import com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorConfig;
import com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorRegistry;
import com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorType;
import com.coolerpromc.ancientcreature.entity.behavior.component.AquaticPredatorBehavior;
import com.coolerpromc.ancientcreature.entity.behavior.component.BrowseLeavesBehavior;
import com.coolerpromc.ancientcreature.entity.behavior.component.FlyBehavior;
import com.coolerpromc.ancientcreature.entity.behavior.component.GrazeBehavior;
import com.coolerpromc.ancientcreature.entity.behavior.component.HuntAnimalsBehavior;
import com.coolerpromc.ancientcreature.entity.behavior.component.HuntHostilesBehavior;
import com.coolerpromc.ancientcreature.entity.behavior.component.TerritorialBehavior;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.SharedConstants;
import net.minecraft.resources.Identifier;
import net.minecraft.server.Bootstrap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Parses the server-side species data this mod ships through the real codecs.
 *
 * <p>This is the check that the shipped species files are actually loadable, that the
 * enum-to-identifier migration still reads old saves, and that a broken definition fails with a useful
 * message instead of being silently accepted. Client resources are covered by {@code ShippedAppearanceTest}.
 */
class SpeciesDataTest {
    private static final Gson GSON = new Gson();
    private static final Path RESOURCES = Path.of("src/main/resources");

    private static CreatureBehaviorType<TestBehavior> testBehaviorType;
    private static Identifier testProfile;

    @BeforeAll
    static void bootstrapMinecraft() {
        // Needed before anything touches BuiltInRegistries (attribute validation does).
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();

        // Registered here because the registries freeze the first time species data is parsed - which
        // is exactly where a real addon has to do it: during mod initialization, before any datapack.
        testBehaviorType = AncientCreatureApi.registerBehavior(
            Identifier.fromNamespaceAndPath("testmod", "sunbathe"), TestBehavior.CODEC, 7);
        testProfile = AncientCreatureApi.registerBehaviorProfile(
            Identifier.fromNamespaceAndPath("testmod", "sunbather"),
            List.of(AncientCreatureApi.component(new TestBehavior(99))));
    }

    private static <T> T parse(Codec<T> codec, Path path) throws IOException {
        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            JsonElement json = GSON.fromJson(reader, JsonElement.class);
            DataResult<T> result = codec.parse(JsonOps.INSTANCE, json);
            return result.getOrThrow(message -> new AssertionError(path + ": " + message));
        }
    }

    // ------------------------------------------------------------------ server species

    @Test
    void triceratopsServerDefinitionLoads() throws IOException {
        SpeciesDefinition definition = parse(SpeciesDefinition.CODEC,
            RESOURCES.resolve("data/ancientcreature/ancientcreature/species/triceratops.json"));

        assertTrue(definition.validate().result().isPresent(),
            () -> "validation failed: " + definition.validate().error().orElseThrow().message());

        // These are the values the hard-coded Triceratops used; the migration must not change them.
        // The hitbox is deliberately not among them — see hitboxesFitTheirModel().
        assertEquals(SpeciesEntityCategory.LAND, definition.category());
        assertEquals(0.5F, definition.growth().babyScale());
        assertEquals(1000, definition.spawn().incubationTime());
        assertEquals(240, definition.sounds().ambientInterval());
        assertEquals(Identifier.fromNamespaceAndPath("ancientcreature", "entities/triceratops"),
            definition.lootTable().orElseThrow());

        Map<Identifier, Double> attributes = definition.attributes().values();
        assertEquals(45.0, attributes.get(Identifier.withDefaultNamespace("max_health")));
        assertEquals(0.22, attributes.get(Identifier.withDefaultNamespace("movement_speed")));
        assertEquals(8.0, attributes.get(Identifier.withDefaultNamespace("attack_damage")));
        assertEquals(1.5, attributes.get(Identifier.withDefaultNamespace("attack_knockback")));
        assertEquals(20.0, attributes.get(Identifier.withDefaultNamespace("follow_range")));
        assertEquals(0.65, attributes.get(Identifier.withDefaultNamespace("knockback_resistance")));

        // Every attribute id must resolve against the real registry.
        assertTrue(definition.attributes().resolve().result().isPresent());

        // The profile must expand into the goal list the old entity hard-coded.
        assertEquals(11, definition.resolvedBehaviors().size());
    }

    @Test
    void migratedPredatorsKeepTheirOldStats() throws IOException {
        SpeciesDefinition rex = parse(SpeciesDefinition.CODEC,
            RESOURCES.resolve("data/ancientcreature/ancientcreature/species/tyrannosaurus_rex.json"));
        assertEquals(0.42F, rex.growth().babyScale());
        assertEquals(1600, rex.spawn().incubationTime());
        assertEquals(300, rex.sounds().ambientInterval());
        assertEquals(100.0, rex.attributes().values().get(Identifier.withDefaultNamespace("max_health")));
        assertEquals(16.0, rex.attributes().values().get(Identifier.withDefaultNamespace("attack_damage")));
        assertEquals(6.0, rex.attributes().values().get(Identifier.withDefaultNamespace("armor")));
        assertEquals(SpeciesEntityCategory.LAND, rex.category());

        SpeciesDefinition megalodon = parse(SpeciesDefinition.CODEC,
            RESOURCES.resolve("data/ancientcreature/ancientcreature/species/megalodon.json"));
        assertEquals(SpeciesEntityCategory.AQUATIC, megalodon.category(), "megalodon must swim");
        assertEquals(0.35F, megalodon.growth().babyScale());
        assertEquals(2000, megalodon.spawn().incubationTime());
        assertEquals(140.0, megalodon.attributes().values().get(Identifier.withDefaultNamespace("max_health")));
        assertEquals(1.15, megalodon.attributes().values().get(Identifier.withDefaultNamespace("movement_speed")));
        // Megalodon never bred or ate in the original, and must not start now.
        assertTrue(megalodon.diet().isEmpty(), "megalodon has no diet");
        assertTrue(megalodon.resolvedBehaviors().stream()
                .noneMatch(c -> c.config().type() == com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorRegistry.BREED),
            "megalodon must not breed");
    }

    /**
     * The eye must be somewhere on the creature.
     *
     * <p>Written after F3+B showed the Tyrannosaurus Rex's eye plane floating 0.34 blocks above the
     * top of its own model and the Megalodon's 0.5 above the top of its head — both inherited
     * unchanged from the hard-coded entity types, where nothing ever compared the two numbers.
     *
     * <p>Eye height is not cosmetic: line-of-sight ray casts and {@code isEyeInFluid} both start
     * there, so an eye above the model sees over blocks the creature is standing behind.

    /** Species are sent to clients with their datapack codec; every shipped species must survive the trip. */
    @Test
    void everyShippedSpeciesSurvivesTheNetworkSync() throws IOException {
        var registries = net.minecraft.core.RegistryAccess.fromRegistryOfRegistries(net.minecraft.core.registries.BuiltInRegistries.REGISTRY);
        try (var files = Files.list(RESOURCES.resolve("data/ancientcreature/ancientcreature/species"))) {
            for (Path file : files.toList()) {
                SpeciesDefinition sent = parse(SpeciesDefinition.CODEC, file);
                var buffer = new net.minecraft.network.RegistryFriendlyByteBuf(io.netty.buffer.Unpooled.buffer(), registries);
                SpeciesDefinition.STREAM_CODEC.encode(buffer, sent);
                SpeciesDefinition received = SpeciesDefinition.STREAM_CODEC.decode(buffer);
                assertEquals(SpeciesDefinition.CODEC.encodeStart(JsonOps.INSTANCE, sent).getOrThrow(),
                    SpeciesDefinition.CODEC.encodeStart(JsonOps.INSTANCE, received).getOrThrow(), file + " changed in transit");
                assertEquals(0, buffer.readableBytes(), file + " left bytes unread");
            }
        }
    }

    @Test
    void careNeedsAreReadAndValidated() throws IOException {
        SpeciesDefinition mammoth = parse(SpeciesDefinition.CODEC, RESOURCES.resolve("data/ancientcreature/ancientcreature/species/woolly_mammoth.json"));
        assertEquals(SpeciesCareProperties.Climate.COLD, mammoth.care().climate());
        assertEquals(SpeciesCareProperties.Social.HERD, mammoth.care().social());
        assertEquals(700, mammoth.care().spaceFor(2.35F));
        SpeciesDefinition plain = parseResult(SpeciesDefinition.CODEC,
            "{\"physical\":{\"width\":2.0,\"height\":1.0},\"behavior\":{\"profile\":\"ancientcreature:passive\"}}").getOrThrow();
        assertEquals(SpeciesCareProperties.DEFAULT, plain.care());
        assertEquals(144, plain.care().spaceFor(2.0F), "space defaults to about a 6x6 pen per square block of body");
        assertFalse(parseResult(SpeciesDefinition.CODEC,
            "{\"physical\":{\"width\":1.0,\"height\":1.0},\"care\":{\"social\":\"herd\",\"group_min\":5,\"group_max\":2}}")
            .result().isPresent(), "group_max below group_min must be rejected");
        SpeciesDefinition mosasaurus = parse(SpeciesDefinition.CODEC, RESOURCES.resolve("data/ancientcreature/ancientcreature/species/mosasaurus.json"));
        assertEquals(SpeciesRespiration.AMPHIBIOUS, mosasaurus.respiration());
        SpeciesDefinition megalodon = parse(SpeciesDefinition.CODEC, RESOURCES.resolve("data/ancientcreature/ancientcreature/species/megalodon.json"));
        assertEquals(SpeciesRespiration.WATER, megalodon.respiration(), "aquatic species breathe water by default");
    }

    @Test
    void speciesDefinitionRejectsBadValues() {
        assertFalse(parseResult(SpeciesDefinition.CODEC,
            "{\"physical\":{\"width\":0.0,\"height\":1.0},\"behavior\":{\"profile\":\"ancientcreature:passive\"}}")
            .result().isPresent(), "a zero width must be rejected");

        assertFalse(parseResult(SpeciesDefinition.CODEC,
            "{\"physical\":{\"width\":1.0,\"height\":-2.0},\"behavior\":{\"profile\":\"ancientcreature:passive\"}}")
            .result().isPresent(), "a negative height must be rejected");

        assertFalse(parseResult(SpeciesDefinition.CODEC,
            "{\"format_version\":99,\"physical\":{\"width\":1.0,\"height\":1.0}}")
            .result().isPresent(), "an unknown format_version must be rejected");

        assertFalse(parseResult(SpeciesDefinition.CODEC,
            "{\"physical\":{\"width\":1.0,\"height\":1.0},\"behavior\":{\"profile\":\"ancientcreature:nope\"}}")
            .result().isPresent(), "an unknown behavior profile must be rejected");

        assertFalse(parseResult(SpeciesDefinition.CODEC,
            "{\"physical\":{\"width\":1.0,\"height\":1.0},\"behavior\":{\"components\":[{\"type\":\"ancientcreature:not_a_thing\"}]}}")
            .result().isPresent(), "an unknown behavior component type must be rejected");

        // Missing behaviour is caught by validate(), not the codec.
        SpeciesDefinition noBehaviour = parseResult(SpeciesDefinition.CODEC,
            "{\"physical\":{\"width\":1.0,\"height\":1.0}}").getOrThrow();
        assertFalse(noBehaviour.validate().result().isPresent(), "a species with no behaviour must fail validation");

        // An attribute that does not exist is caught by validate() too.
        SpeciesDefinition badAttribute = parseResult(SpeciesDefinition.CODEC,
            "{\"physical\":{\"width\":1.0,\"height\":1.0},\"attributes\":{\"totally_made_up\":1.0},"
                + "\"behavior\":{\"profile\":\"ancientcreature:passive\"}}").getOrThrow();
        assertFalse(badAttribute.validate().result().isPresent(), "an unknown attribute must fail validation");
    }

    // ------------------------------------------------------------------ save compatibility

    @Test
    void legacyEnumNamesStillDeserialize() {
        // The old Species enum serialized as its bare lowercase name. Existing worlds are full of these.
        assertEquals(Species.TRICERATOPS, parseResult(Species.CODEC, "\"triceratops\"").getOrThrow());
        assertEquals(Species.TYRANNOSAURUS_REX, parseResult(Species.CODEC, "\"tyrannosaurus_rex\"").getOrThrow());
        assertEquals(Species.MEGALODON, parseResult(Species.CODEC, "\"megalodon\"").getOrThrow());

        // Fully qualified ids read back too, and are what gets written.
        assertEquals(Species.TRICERATOPS, parseResult(Species.CODEC, "\"ancientcreature:triceratops\"").getOrThrow());
        assertEquals("ancientcreature:triceratops",
            Species.CODEC.encodeStart(JsonOps.INSTANCE, Species.TRICERATOPS).getOrThrow().getAsString());

        // A datapack in another namespace is a first-class species.
        assertEquals(Identifier.fromNamespaceAndPath("examplepack", "stegosaurus"),
            parseResult(Species.CODEC, "\"examplepack:stegosaurus\"").getOrThrow().id());
    }

    // ------------------------------------------------------------------ client species


    // ------------------------------------------------------------------ flying + api

    @Test
    void flyingSpeciesHaveRealFlightBehaviour() {
        // "flying" used to be a label with nothing behind it. These profiles must actually exist and
        // must include the flight components.
        SpeciesDefinition flyer = parseResult(SpeciesDefinition.CODEC,
            "{\"entity_category\":\"flying\",\"physical\":{\"width\":1.0,\"height\":1.0},"
                + "\"behavior\":{\"profile\":\"ancientcreature:flying_passive\"}}").getOrThrow();

        assertEquals(SpeciesEntityCategory.FLYING, flyer.category());
        assertTrue(flyer.validate().result().isPresent());
        assertTrue(flyer.resolvedBehaviors().stream()
                .anyMatch(c -> c.config().type() == CreatureBehaviorRegistry.FLY),
            "flying_passive must include the fly component");

        SpeciesDefinition hunter = parseResult(SpeciesDefinition.CODEC,
            "{\"entity_category\":\"flying\",\"physical\":{\"width\":1.0,\"height\":1.0},"
                + "\"behavior\":{\"profile\":\"ancientcreature:flying_predator\"}}").getOrThrow();
        assertTrue(hunter.resolvedBehaviors().stream()
                .anyMatch(c -> c.config().type() == CreatureBehaviorRegistry.CIRCLE_TARGET),
            "flying_predator must include the circle_target component");
        assertTrue(hunter.resolvedBehaviors().stream()
                .anyMatch(c -> c.config().type() == CreatureBehaviorRegistry.FLY),
            "flying_predator must include the fly component");
    }

    /**
     * Flight altitude has to be tunable from JSON.
     *
     * <p>Free flight used to delegate to vanilla's {@code WaterAvoidingRandomFlyingGoal}, whose wander box
     * is hard-coded to 8 blocks out and 7 up, so every flyer sat at whatever height it spawned at with no
     * way to raise it. These are the knobs that replaced it, and the band has to survive a round trip
     * through the codec or a datapack cannot move a species off the default.
     */
    @Test
    void flightAltitudeIsConfigurable() {
        SpeciesDefinition soarer = parseResult(SpeciesDefinition.CODEC,
            "{\"entity_category\":\"flying\",\"physical\":{\"width\":1.0,\"height\":1.0},"
                + "\"behavior\":{\"profile\":\"ancientcreature:flying_passive\",\"components\":["
                + "{\"type\":\"ancientcreature:fly\",\"wander_range\":24,\"vertical_range\":16,"
                + "\"min_altitude\":8,\"max_altitude\":48}]}}").getOrThrow();

        FlyBehavior fly = soarer.resolvedBehaviors().stream()
            .map(c -> c.config())
            .filter(FlyBehavior.class::isInstance)
            .map(FlyBehavior.class::cast)
            .findFirst()
            .orElseThrow(() -> new AssertionError("the fly component went missing"));

        assertEquals(24, fly.wanderRange());
        assertEquals(16, fly.verticalRange());
        assertEquals(8, fly.minAltitude());
        assertEquals(48, fly.maxAltitude());

        // The declared component has to replace the profile's, not sit alongside it.
        assertEquals(1, soarer.resolvedBehaviors().stream()
            .filter(c -> c.config().type() == CreatureBehaviorRegistry.FLY).count());

        // An inverted band has nowhere legal to fly and must be rejected at load, not at spawn.
        assertFalse(parseResult(SpeciesDefinition.CODEC,
            "{\"entity_category\":\"flying\",\"physical\":{\"width\":1.0,\"height\":1.0},"
                + "\"behavior\":{\"components\":[{\"type\":\"ancientcreature:fly\","
                + "\"min_altitude\":40,\"max_altitude\":10}]}}").result().isPresent(),
            "max_altitude below min_altitude must be rejected");
    }

    /** The shipped Pteranodon has to actually use the wide band, or it soars at the default height. */
    @Test
    void thePteranodonSoarsWellAboveTheTerrain() throws IOException {
        SpeciesDefinition pteranodon = parse(SpeciesDefinition.CODEC,
            RESOURCES.resolve("data/ancientcreature/ancientcreature/species/pteranodon.json"));

        FlyBehavior fly = pteranodon.resolvedBehaviors().stream()
            .map(c -> c.config())
            .filter(FlyBehavior.class::isInstance)
            .map(FlyBehavior.class::cast)
            .findFirst()
            .orElseThrow(() -> new AssertionError("pteranodon has no fly component"));

        assertTrue(fly.maxAltitude() >= 40,
            () -> "pteranodon caps out at " + fly.maxAltitude() + " blocks; it should soar higher than that");
        assertTrue(fly.minAltitude() >= 6,
            () -> "pteranodon can drop to " + fly.minAltitude() + " blocks, which puts it in the trees");
    }

    // ------------------------------------------------------------------ hunger

    /** Every species gets a hunger meter, whether or not its JSON mentions one. */
    @Test
    void hungerDefaultsAreAppliedToEverySpecies() throws IOException {
        for (String name : new String[]{"triceratops", "tyrannosaurus_rex", "megalodon", "pteranodon", "ankylosaurus", "deinonychus", "brachiosaurus",
            "stegosaurus", "parasaurolophus", "spinosaurus", "carnotaurus", "velociraptor", "dilophosaurus", "argentinosaurus", "quetzalcoatlus",
            "mosasaurus", "plesiosaurus", "dunkleosteus", "smilodon", "woolly_mammoth", "woolly_rhinoceros", "dire_wolf", "arthropleura"}) {
            SpeciesDefinition definition = parse(SpeciesDefinition.CODEC,
                RESOURCES.resolve("data/ancientcreature/ancientcreature/species/" + name + ".json"));
            SpeciesHungerProperties hunger = definition.hunger();

            assertTrue(hunger.max() > 0.0F, name + " has no hunger capacity");
            assertTrue(hunger.huntThreshold() <= hunger.max(), name + " can never stop being hungry");
            assertTrue(hunger.fullThreshold() >= hunger.huntThreshold(),
                name + " would start and stop hunting on the same tick");
        }
    }

    @Test
    void hungerBlockIsConfigurableAndValidated() {
        SpeciesDefinition tuned = parseResult(SpeciesDefinition.CODEC,
            "{\"physical\":{\"width\":1.0,\"height\":1.0},\"behavior\":{\"profile\":\"ancientcreature:passive\"},"
                + "\"hunger\":{\"max\":40.0,\"decay_interval\":300,\"hunt_threshold\":15.0,"
                + "\"full_threshold\":35.0,\"food_value\":9.0,\"kill_value\":12.0,"
                + "\"starve_damage\":2.0,\"starve_interval\":100}}").getOrThrow();

        SpeciesHungerProperties hunger = tuned.hunger();
        assertEquals(40.0F, hunger.max());
        assertEquals(300, hunger.decayInterval());
        assertEquals(15.0F, hunger.huntThreshold());
        assertEquals(35.0F, hunger.fullThreshold());
        assertEquals(9.0F, hunger.foodValue());
        assertEquals(12.0F, hunger.killValue());
        assertTrue(hunger.starves(), "a positive starve_damage means the species starves");

        // Starvation is opt-in: a species that says nothing about it must not take damage.
        SpeciesDefinition plain = parseResult(SpeciesDefinition.CODEC,
            "{\"physical\":{\"width\":1.0,\"height\":1.0},\"behavior\":{\"profile\":\"ancientcreature:passive\"}}")
            .getOrThrow();
        assertFalse(plain.hunger().starves(), "starvation must default to off");

        // A threshold above the meter's own capacity would leave the creature permanently hungry.
        assertFalse(parseResult(SpeciesDefinition.CODEC,
            "{\"physical\":{\"width\":1.0,\"height\":1.0},\"hunger\":{\"max\":10.0,\"hunt_threshold\":25.0}}")
            .result().isPresent(), "hunt_threshold above max must be rejected");

        // Inverted thresholds would make a predator flicker in and out of hunting every tick.
        assertFalse(parseResult(SpeciesDefinition.CODEC,
            "{\"physical\":{\"width\":1.0,\"height\":1.0},"
                + "\"hunger\":{\"hunt_threshold\":18.0,\"full_threshold\":4.0}}")
            .result().isPresent(), "full_threshold below hunt_threshold must be rejected");
    }

    /**
     * The point of the whole feature: feeding decides whether a predator picks prey, and nothing else.
     *
     * <p>The split matters more than the gate. Gating retaliation or the attack goals themselves would
     * leave a fed creature unable to defend itself, which is a worse bug than attacking at random.
     */
    @Test
    void onlyPredatoryTargetingIsGatedOnHunger() {
        assertTrue(isHungerGated(CreatureBehaviorRegistry.HUNT_ANIMALS, BehaviorProfiles.APEX_PREDATOR),
            "hunting animals is feeding, so it must wait for hunger");
        assertTrue(isHungerGated(CreatureBehaviorRegistry.AQUATIC_PREDATOR, BehaviorProfiles.AQUATIC_PREDATOR),
            "an aquatic predator's prey selection must wait for hunger");
        assertTrue(isHungerGated(CreatureBehaviorRegistry.TERRITORIAL, BehaviorProfiles.APEX_PREDATOR),
            "an apex predator stalks players with require_weapon off, which is feeding, not defence");

        // Defence is never gated.
        assertFalse(isHungerGated(CreatureBehaviorRegistry.HUNT_HOSTILES, BehaviorProfiles.APEX_PREDATOR),
            "driving off hostiles is defence and must work when fed");
        assertFalse(isHungerGated(CreatureBehaviorRegistry.TERRITORIAL, BehaviorProfiles.DEFENSIVE_HERBIVORE),
            "a herbivore guarding its space is defending itself, not eating");
        assertFalse(isHungerGated(CreatureBehaviorRegistry.HUNT_HOSTILES, BehaviorProfiles.DEFENSIVE_HERBIVORE),
            "a herbivore does not eat zombies");
    }

    /** A datapack has to be able to turn the gate off and get the old always-hunting behaviour back. */
    @Test
    void theHungerGateCanBeTurnedOff() {
        SpeciesDefinition always = parseResult(SpeciesDefinition.CODEC,
            "{\"physical\":{\"width\":1.0,\"height\":1.0},\"behavior\":{\"components\":["
                + "{\"type\":\"ancientcreature:hunt_animals\",\"requires_hunger\":false}]}}").getOrThrow();

        CreatureBehaviorConfig config = always.resolvedBehaviors().getFirst().config();
        assertFalse(((HuntAnimalsBehavior) config).requiresHunger(),
            "requires_hunger:false must disable the gate");
    }

    /** Grazing is how a herbivore refills, so the profile that grazes has to award hunger. */
    @Test
    void grazingFeedsTheCreature() {
        SpeciesDefinition grazer = parseResult(SpeciesDefinition.CODEC,
            "{\"physical\":{\"width\":1.0,\"height\":1.0},\"behavior\":{\"components\":["
                + "{\"type\":\"ancientcreature:graze\"}]}}").getOrThrow();

        GrazeBehavior graze = (GrazeBehavior) grazer.resolvedBehaviors().getFirst().config();
        assertTrue(graze.hungerValue() > 0.0F,
            "a graze that restores nothing leaves a herbivore permanently hungry");
    }

    private static boolean isHungerGated(CreatureBehaviorType<?> type, Identifier profile) {
        CreatureBehaviorConfig config = BehaviorProfiles.get(profile).orElseThrow().stream()
            .map(CreatureBehaviorComponent::config)
            .filter(c -> c.type() == type)
            .findFirst()
            .orElseThrow(() -> new AssertionError(profile + " has no " + type + " component"));

        return switch (config) {
            case HuntAnimalsBehavior hunt -> hunt.requiresHunger();
            case HuntHostilesBehavior hunt -> hunt.requiresHunger();
            case AquaticPredatorBehavior hunt -> hunt.requiresHunger();
            case TerritorialBehavior territorial -> territorial.requiresHunger();
            default -> throw new AssertionError(type + " has no hunger gate to report");
        };
    }

    @Test
    void addonsCanRegisterBehaviorsAndProfiles() {
        assertEquals(Identifier.fromNamespaceAndPath("testmod", "sunbathe"), testBehaviorType.id());
        assertEquals(7, testBehaviorType.defaultPriority());
        assertTrue(AncientCreatureApi.behaviorTypes().contains(testBehaviorType));
        assertTrue(AncientCreatureApi.behaviorProfiles().contains(testProfile));

        // A species may now use both, and the registered fields decode.
        SpeciesDefinition definition = parseResult(SpeciesDefinition.CODEC,
            "{\"physical\":{\"width\":1.0,\"height\":1.0},\"behavior\":{\"profile\":\"testmod:sunbather\","
                + "\"components\":[{\"type\":\"testmod:sunbathe\",\"priority\":2,\"duration\":123}]}}").getOrThrow();

        assertTrue(definition.validate().result().isPresent());
        assertTrue(definition.lootTable().isEmpty(), "loot is optional for third-party species");
        List<com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorComponent> resolved =
            definition.resolvedBehaviors();
        // The explicit component replaces the profile's entry of the same type.
        assertEquals(1, resolved.size());
        assertEquals(2, resolved.getFirst().priority());
        assertEquals(123, ((TestBehavior) resolved.getFirst().config()).duration());

        // Registering after species data has been parsed is refused rather than half-working.
        assertThrows(IllegalStateException.class, () -> AncientCreatureApi.registerBehavior(
            Identifier.fromNamespaceAndPath("testmod", "too_late"), TestBehavior.CODEC, 1));
    }

    /** A minimal third-party behavior component, exactly as an addon would write one. */
    record TestBehavior(int duration) implements CreatureBehaviorConfig {
        static final com.mojang.serialization.MapCodec<TestBehavior> CODEC =
            com.mojang.serialization.codecs.RecordCodecBuilder.mapCodec(i -> i.group(
                Codec.INT.optionalFieldOf("duration", 40).forGetter(TestBehavior::duration)
            ).apply(i, TestBehavior::new));

        static CreatureBehaviorType<TestBehavior> registered;

        @Override
        public CreatureBehaviorType<?> type() {
            if (registered == null) {
                registered = (CreatureBehaviorType<TestBehavior>) CreatureBehaviorRegistry
                    .get(Identifier.fromNamespaceAndPath("testmod", "sunbathe")).orElseThrow();
            }
            return registered;
        }

        @Override
        public net.minecraft.world.entity.ai.goal.Goal createGoal(
            com.coolerpromc.ancientcreature.entity.custom.AncientCreatureEntity entity) {
            return null;
        }
    }

    private static <T> DataResult<T> parseResult(Codec<T> codec, String json) {
        return codec.parse(JsonOps.INSTANCE, GSON.fromJson(json, JsonElement.class));
    }
}
