package com.coolerpromc.ancientcreature.sound;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.platform.Services;
import com.coolerpromc.ancientcreature.platform.util.RegistryHandler;
import net.minecraft.sounds.SoundEvent;

public class ModSounds {
    public static final RegistryHandler<SoundEvent, SoundEvent> IDENTIFICATION_CHAMBER_SCAN = register("block.fossil_identification_chamber.scan");
    public static final RegistryHandler<SoundEvent, SoundEvent> IDENTIFICATION_CHAMBER_FAILED = register("block.fossil_identification_chamber.failed");
    public static final RegistryHandler<SoundEvent, SoundEvent> CLEANING_TABLE_BRUSH = register("block.fossil_cleaning_table.brush");
    public static final RegistryHandler<SoundEvent, SoundEvent> DNA_EXTRACTOR_PROCESSING = register("block.dna_extractor.processing");
    public static final RegistryHandler<SoundEvent, SoundEvent> GENOME_SEQUENCER_PROCESSING = register("block.genome_sequencer.processing");
    public static final RegistryHandler<SoundEvent, SoundEvent> EMBRYOGENESIS_CHAMBER_PROCESSING = register("block.embryogenesis_chamber.processing");
    public static final RegistryHandler<SoundEvent, SoundEvent> INCUBATOR_PROCESSING = register("block.incubator.processing");

    public static RegistryHandler<SoundEvent, SoundEvent> register(String name){
        return Services.REGISTRY.registerSoundEvent(name);
    }

    public static void init(){
        Constants.LOG.info("Registering sounds.");
    }
}
