package com.coolerpromc.ancientcreature.config;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.coolerconfig.config.ConfigBuilder;
import com.coolerpromc.coolerconfig.config.ConfigFormat;
import com.coolerpromc.coolerconfig.config.ConfigSpec;
import com.coolerpromc.coolerconfig.config.ConfigValue;

import java.util.List;

public final class ModCommonConfig {
    public static final ModCommonConfig CONFIG;
    public static final ConfigSpec CONFIG_SPEC;

    public final ConfigValue<Integer> cleaningTick;
    public final ConfigValue<Integer> unknownIdentifyingTick;
    public final ConfigValue<Integer> knownIdentifyingTick;
    public final ConfigValue<Integer> extractingTick;
    public final ConfigValue<Integer> sequencingTick;
    public final ConfigValue<Integer> embryogenesisTick;
    public final ConfigValue<Float> incubationTimeMultiplier;
    public final ConfigValue<Boolean> giveFieldGuideOnFirstJoin;

    public final ConfigValue<Boolean> creaturesBreakBlocks;
    public final ConfigValue<List<String>> passiveSpecies;
    public final ConfigValue<Integer> populationCap;
    public final ConfigValue<Integer> populationRadius;
    public final ConfigValue<Double> electricFenceDamage;
    public final ConfigValue<Boolean> comfortEnabled;
    public final ConfigValue<Boolean> ridingRequiresSaddle;

    private ModCommonConfig(ConfigBuilder builder){
        cleaningTick = builder.defineInt("FossilCleaningTable.cleaningTick", 100, 1, Integer.MAX_VALUE, "Total processing time for cleaning a fossil fragment or fossil egg");
        unknownIdentifyingTick = builder.defineInt("FossilIdentificationTable.unknownIdentifyingTick", 400, 1, Integer.MAX_VALUE, "Total processing time for identifying an unknown species");
        knownIdentifyingTick = builder.defineInt("FossilIdentificationTable.knownIdentifyingTick", 100, 1, Integer.MAX_VALUE, "Total processing time for identifying a known species");
        extractingTick = builder.defineInt("DNAExtractor.extractingTick", 100, 1, Integer.MAX_VALUE, "Total processing time for extracting dna from cleaned fossil");
        sequencingTick = builder.defineInt("GenomeSequencing.sequencingTick", 100, 1, Integer.MAX_VALUE, "Total processing time for genome sequencing");
        embryogenesisTick = builder.defineInt("EmbryogenesisChamber.embryogenesisTick", 100, 1, Integer.MAX_VALUE, "Total processing time for embryogenesis chamber");
        incubationTimeMultiplier = builder.defineFloat("Incubator.incubationTimeMultiplier", 1f, 0.01f, Float.MAX_VALUE, "Incubation time are defined per species, but modifying this config value can make the machine work slower or faster []");
        giveFieldGuideOnFirstJoin = builder.defineBoolean("FieldGuide.giveOnFirstJoin", true, "Give players a Paleontologist's Field Guide the first time they join a world");

        creaturesBreakBlocks = builder.defineBoolean("Creatures.breakBlocks", true, "Whether charging creatures may smash blocks in #ancientcreature:creature_destroyable. Also requires the mobGriefing game rule.");
        passiveSpecies = builder.defineList("Creatures.passiveSpecies", List.of(), "Species ids (e.g. \"ancientcreature:tyrannosaurus_rex\") that never start a fight. They still defend themselves when attacked.");
        populationCap = builder.defineInt("Creatures.populationCap", 64, 0, 4096, "Maximum revived creatures within populationRadius of a new one. Breeding, hatching and capsule releases wait or fail beyond it. 0 disables the cap.");
        ridingRequiresSaddle = builder.defineBoolean("Riding.requireSaddle", true, "Whether a creature needs a Creature Saddle before its owner can ride it.");
        comfortEnabled = builder.defineBoolean("Creatures.comfort", true, "Track creature comfort (space, company, climate, hunger, pain). Comfortable creatures heal and breed; distressed large ones smash weak enclosures.");
        electricFenceDamage = builder.defineDouble("Containment.electricFenceDamage", 4.0, 0.0, 100.0, "Damage a fully charged electric fence deals per shock. Weaker charge deals down to half of this.");
        populationRadius = builder.defineInt("Creatures.populationRadius", 64, 8, 256, "Radius in blocks used to count creatures for populationCap.");
    }

    static {
        ConfigBuilder builder = ConfigSpec.builder(Constants.MODID, ConfigFormat.TOML).watchForChanges();

        CONFIG = new ModCommonConfig(builder);
        CONFIG_SPEC = builder.build();
    }

    /** Whether the species is listed in {@code Creatures.passiveSpecies}. */
    public static boolean isForcedPassive(net.minecraft.resources.Identifier species) {
        List<String> listed = CONFIG.passiveSpecies.get();
        if (listed.isEmpty()) {
            return false;
        }
        String id = species.toString();
        for (String entry : listed) {
            if (id.equals(entry) || (entry.indexOf(':') < 0 && species.getPath().equals(entry))) {
                return true;
            }
        }
        return false;
    }

    public static void init(){
        Constants.LOG.info("Registering common config.");
    }
}
