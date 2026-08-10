package com.coolerpromc.ancientcreature.species;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SpeciesSpawnBiomeTagsTest {
    private static final Path SPECIES = Path.of("src/main/resources/data/ancientcreature/ancientcreature/species");
    private static final Path BIOME_TAGS = Path.of("src/generated/resources/data/ancientcreature/tags/worldgen/biome");

    @Test
    void everySpeciesUsesItsOwnNonEmptySpawnBiomeTag() throws IOException {
        List<Path> definitions;
        try (var paths = Files.list(SPECIES)) {
            definitions = paths.filter(path -> path.toString().endsWith(".json")).sorted().toList();
        }

        assertFalse(definitions.isEmpty(), "no species definitions found");
        for (Path definition : definitions) {
            String speciesName = definition.getFileName().toString().replaceFirst("\\.json$", "");
            JsonObject species = readObject(definition);
            String biomeTag = species.getAsJsonObject("spawn").get("biomes").getAsString();
            String expectedTag = "#ancientcreature:spawns_" + speciesName;

            assertEquals(expectedTag, biomeTag, speciesName + " should use its species-specific biome tag");

            Path tagPath = BIOME_TAGS.resolve("spawns_" + speciesName + ".json");
            assertTrue(Files.isRegularFile(tagPath), () -> "missing biome tag " + tagPath);
            JsonArray values = readObject(tagPath).getAsJsonArray("values");
            assertTrue(values != null && !values.isEmpty(), () -> speciesName + " has no suitable spawn biomes");
        }
    }

    private static JsonObject readObject(Path path) throws IOException {
        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            return JsonParser.parseReader(reader).getAsJsonObject();
        }
    }
}
