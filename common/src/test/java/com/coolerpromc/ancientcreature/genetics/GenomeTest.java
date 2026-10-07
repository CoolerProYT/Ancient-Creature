package com.coolerpromc.ancientcreature.genetics;

import com.coolerpromc.ancientcreature.data.component.custom.CreatureGenome;
import com.coolerpromc.ancientcreature.data.component.custom.GenomeData;
import com.coolerpromc.ancientcreature.entity.Species;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.util.RandomSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GenomeTest {
    @BeforeAll
    static void bootstrap() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void fidelityIsCompletenessWeighted() {
        GenomeData genome = new GenomeData(0.0F, Species.TRICERATOPS, 0.0F)
            .withSample(0.25F, 0.8F)
            .withSample(0.25F, 0.4F);
        assertEquals(0.5F, genome.completeness(), 1e-5);
        assertEquals(0.6F, genome.fidelity(), 1e-5);
    }

    @Test
    void overflowingSampleOnlyCountsWhatFits() {
        GenomeData genome = new GenomeData(0.9F, Species.TRICERATOPS, 1.0F).withSample(0.5F, 0.0F);
        assertEquals(1.0F, genome.completeness(), 1e-5);
        // only 0.1 of the poor sample went in
        assertEquals(0.9F, genome.fidelity(), 1e-5);
    }

    @Test
    void legacyGenomeDataReadsWithDefaultFidelity() {
        JsonElement json = com.google.gson.JsonParser.parseString("{\"completeness\":0.4,\"species\":\"ancientcreature:triceratops\"}");
        GenomeData read = GenomeData.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow();
        assertEquals(GenomeData.LEGACY_FIDELITY, read.fidelity(), 1e-6);
    }

    @Test
    void rolledTraitsStayInCodecRange() {
        RandomSource random = RandomSource.create(42);
        for (int i = 0; i < 2000; i++) {
            float fidelity = random.nextFloat();
            CreatureGenome genome = CreatureGenome.roll(Species.TRICERATOPS, fidelity, random.nextInt(3), random);
            JsonElement json = CreatureGenome.CODEC.encodeStart(JsonOps.INSTANCE, genome).getOrThrow();
            assertEquals(genome, CreatureGenome.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow());
            CreatureGenome child = CreatureGenome.inherit(genome, genome, random);
            CreatureGenome.CODEC.encodeStart(JsonOps.INSTANCE, child).getOrThrow();
        }
    }

    @Test
    void perfectGenomeIsAlwaysHealthy() {
        RandomSource random = RandomSource.create(7);
        for (int i = 0; i < 500; i++) {
            CreatureGenome genome = CreatureGenome.roll(Species.TRICERATOPS, 1.0F, 0, random);
            assertTrue(genome.fertile());
            assertFalse(genome.frail());
        }
    }

    @Test
    void poorGenomesAreSterileMoreOften() {
        RandomSource random = RandomSource.create(3);
        int poor = 0, good = 0;
        for (int i = 0; i < 4000; i++) {
            if (!CreatureGenome.roll(Species.TRICERATOPS, 0.1F, 0, random).fertile()) poor++;
            if (!CreatureGenome.roll(Species.TRICERATOPS, 0.8F, 0, random).fertile()) good++;
        }
        assertTrue(poor > good * 3, poor + " vs " + good);
    }

    @Test
    void steadyingRestoresVitality() {
        CreatureGenome frail = new CreatureGenome(Species.TRICERATOPS, 0.2F, 1.0F, 0.7F, 1.0F, CreatureGenome.Temperament.STEADY, true, true);
        CreatureGenome steadied = frail.withoutFrailty();
        assertFalse(steadied.frail());
        assertEquals(1.0F, steadied.vitality(), 1e-5);
    }
}
