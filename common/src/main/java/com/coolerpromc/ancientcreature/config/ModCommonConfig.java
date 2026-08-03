package com.coolerpromc.ancientcreature.config;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.coolerconfig.config.ConfigBuilder;
import com.coolerpromc.coolerconfig.config.ConfigFormat;
import com.coolerpromc.coolerconfig.config.ConfigSpec;
import com.coolerpromc.coolerconfig.config.ConfigValue;

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

    private ModCommonConfig(ConfigBuilder builder){
        cleaningTick = builder.defineInt("FossilCleaningTable.cleaningTick", 100, 1, Integer.MAX_VALUE, "Total processing time for cleaning a fossil fragment or fossil egg");
        unknownIdentifyingTick = builder.defineInt("FossilIdentificationTable.unknownIdentifyingTick", 400, 1, Integer.MAX_VALUE, "Total processing time for identifying an unknown species");
        knownIdentifyingTick = builder.defineInt("FossilIdentificationTable.knownIdentifyingTick", 100, 1, Integer.MAX_VALUE, "Total processing time for identifying a known species");
        extractingTick = builder.defineInt("DNAExtractor.extractingTick", 100, 1, Integer.MAX_VALUE, "Total processing time for extracting dna from cleaned fossil");
        sequencingTick = builder.defineInt("GenomeSequencing.sequencingTick", 100, 1, Integer.MAX_VALUE, "Total processing time for genome sequencing");
        embryogenesisTick = builder.defineInt("EmbryogenesisChamber.embryogenesisTick", 100, 1, Integer.MAX_VALUE, "Total processing time for embryogenesis chamber");
        incubationTimeMultiplier = builder.defineFloat("Incubator.incubationTimeMultiplier", 1f, 0.01f, Float.MAX_VALUE, "Incubation time are defined per species, but modifying this config value can make the machine work slower or faster []");
    }

    static {
        ConfigBuilder builder = ConfigSpec.builder(Constants.MODID, ConfigFormat.TOML).watchForChanges();

        CONFIG = new ModCommonConfig(builder);
        CONFIG_SPEC = builder.build();
    }

    public static void init(){
        Constants.LOG.info("Registering common config.");
    }
}
