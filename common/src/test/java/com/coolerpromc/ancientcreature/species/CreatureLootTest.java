package com.coolerpromc.ancientcreature.species;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import net.minecraft.SharedConstants;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.level.storage.loot.LootTable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CreatureLootTest {
    @BeforeAll
    static void bootstrapMinecraft() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void creatureDropsRetainFunctionsAndConditions() throws IOException {
        var ops = VanillaRegistries.createLookup().createSerializationContext(JsonOps.INSTANCE);
        try (var paths = Files.list(Path.of("src/main/resources/data/ancientcreature/loot_table/entities"))) {
            for (Path path : paths.filter(file -> file.toString().endsWith(".json")).sorted().toList()) {
                String source = Files.readString(path);
                assertFalse(source.contains("\"modifier\""), path.toString());
                JsonElement json = JsonParser.parseString(source.replaceAll(
                    "\"name\": \"ancientcreature:[^\"]+\"", "\"name\": \"minecraft:stone\""));
                LootTable table = LootTable.DIRECT_CODEC.parse(ops, json)
                    .getOrThrow(message -> new AssertionError(path + ": " + message));
                JsonElement encoded = LootTable.DIRECT_CODEC.encodeStart(ops, table)
                    .getOrThrow(message -> new AssertionError(path + ": " + message));
                List<JsonElement> functions = values(json, "function");
                assertTrue(functions.size() > 0, path.toString());
                assertEquals(functions, values(encoded, "function"), path.toString());
                for (String field : List.of("condition", "count", "is_on_fire", "enchantments")) {
                    assertEquals(values(json, field).stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting())),
                        values(encoded, field).stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting())),
                        path + ": " + field);
                }
            }
        }
    }

    private static List<JsonElement> values(JsonElement json, String field) {
        List<JsonElement> values = new ArrayList<>();
        if (json.isJsonArray()) {
            json.getAsJsonArray().forEach(element -> values.addAll(values(element, field)));
        } else if (json.isJsonObject()) {
            json.getAsJsonObject().entrySet().forEach(entry -> {
                if (entry.getKey().equals(field)) {
                    values.add(entry.getValue());
                }
                values.addAll(values(entry.getValue(), field));
            });
        }
        return values;
    }
}
