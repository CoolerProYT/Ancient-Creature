package com.coolerpromc.ancientcreature.sound;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.platform.Services;
import com.coolerpromc.ancientcreature.platform.util.RegistryHandler;
import net.minecraft.sounds.SoundEvent;

public class ModSounds {
    public static final RegistryHandler<SoundEvent, SoundEvent> IDENTIFICATION_CHAMBER_SCAN = register("block.fossil_identification_chamber.scan");
    public static final RegistryHandler<SoundEvent, SoundEvent> IDENTIFICATION_CHAMBER_FAILED = register("block.fossil_identification_chamber.failed");
    public static final RegistryHandler<SoundEvent, SoundEvent> CLEANING_TABLE_BRUSH = register("block.fossil_cleaning_table.brush");

    public static RegistryHandler<SoundEvent, SoundEvent> register(String name){
        return Services.REGISTRY.registerSoundEvent(name);
    }

    public static void init(){
        Constants.LOG.info("Registering sounds.");
    }
}
