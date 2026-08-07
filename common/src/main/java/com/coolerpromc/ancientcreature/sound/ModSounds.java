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
    public static final RegistryHandler<SoundEvent, SoundEvent> TRICERATOPS_AMBIENT = register("entity.triceratops.ambient");
    public static final RegistryHandler<SoundEvent, SoundEvent> TRICERATOPS_BELLOW = register("entity.triceratops.bellow");
    public static final RegistryHandler<SoundEvent, SoundEvent> TRICERATOPS_HURT = register("entity.triceratops.hurt");
    public static final RegistryHandler<SoundEvent, SoundEvent> TRICERATOPS_DEATH = register("entity.triceratops.death");
    public static final RegistryHandler<SoundEvent, SoundEvent> TRICERATOPS_ATTACK = register("entity.triceratops.attack");
    public static final RegistryHandler<SoundEvent, SoundEvent> TRICERATOPS_STEP = register("entity.triceratops.step");
    public static final RegistryHandler<SoundEvent, SoundEvent> TYRANNOSAURUS_REX_AMBIENT = register("entity.tyrannosaurus_rex.ambient");
    public static final RegistryHandler<SoundEvent, SoundEvent> TYRANNOSAURUS_REX_ROAR = register("entity.tyrannosaurus_rex.roar");
    public static final RegistryHandler<SoundEvent, SoundEvent> TYRANNOSAURUS_REX_HURT = register("entity.tyrannosaurus_rex.hurt");
    public static final RegistryHandler<SoundEvent, SoundEvent> TYRANNOSAURUS_REX_DEATH = register("entity.tyrannosaurus_rex.death");
    public static final RegistryHandler<SoundEvent, SoundEvent> TYRANNOSAURUS_REX_BITE = register("entity.tyrannosaurus_rex.bite");
    public static final RegistryHandler<SoundEvent, SoundEvent> TYRANNOSAURUS_REX_STEP = register("entity.tyrannosaurus_rex.step");
    public static final RegistryHandler<SoundEvent, SoundEvent> MEGALODON_AMBIENT = register("entity.megalodon.ambient");
    public static final RegistryHandler<SoundEvent, SoundEvent> MEGALODON_HURT = register("entity.megalodon.hurt");
    public static final RegistryHandler<SoundEvent, SoundEvent> MEGALODON_DEATH = register("entity.megalodon.death");
    public static final RegistryHandler<SoundEvent, SoundEvent> MEGALODON_BITE = register("entity.megalodon.bite");
    public static final RegistryHandler<SoundEvent, SoundEvent> MEGALODON_STEP = register("entity.megalodon.step");

    public static RegistryHandler<SoundEvent, SoundEvent> register(String name){
        return Services.REGISTRY.registerSoundEvent(name);
    }

    public static void init(){
        Constants.LOG.info("Registering sounds.");
    }
}
