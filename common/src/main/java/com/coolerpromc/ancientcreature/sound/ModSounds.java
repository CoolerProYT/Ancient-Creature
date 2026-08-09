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
    public static final RegistryHandler<SoundEvent, SoundEvent> PTERANODON_AMBIENT = register("entity.pteranodon.ambient");
    public static final RegistryHandler<SoundEvent, SoundEvent> PTERANODON_ALERT = register("entity.pteranodon.alert");
    public static final RegistryHandler<SoundEvent, SoundEvent> PTERANODON_HURT = register("entity.pteranodon.hurt");
    public static final RegistryHandler<SoundEvent, SoundEvent> PTERANODON_DEATH = register("entity.pteranodon.death");
    public static final RegistryHandler<SoundEvent, SoundEvent> PTERANODON_ATTACK = register("entity.pteranodon.attack");
    public static final RegistryHandler<SoundEvent, SoundEvent> PTERANODON_STEP = register("entity.pteranodon.step");
    public static final RegistryHandler<SoundEvent, SoundEvent> ANKYLOSAURUS_AMBIENT = register("entity.ankylosaurus.ambient");
    public static final RegistryHandler<SoundEvent, SoundEvent> ANKYLOSAURUS_ALERT = register("entity.ankylosaurus.alert");
    public static final RegistryHandler<SoundEvent, SoundEvent> ANKYLOSAURUS_HURT = register("entity.ankylosaurus.hurt");
    public static final RegistryHandler<SoundEvent, SoundEvent> ANKYLOSAURUS_DEATH = register("entity.ankylosaurus.death");
    public static final RegistryHandler<SoundEvent, SoundEvent> ANKYLOSAURUS_ATTACK = register("entity.ankylosaurus.attack");
    public static final RegistryHandler<SoundEvent, SoundEvent> ANKYLOSAURUS_STEP = register("entity.ankylosaurus.step");
    public static final RegistryHandler<SoundEvent, SoundEvent> DEINONYCHUS_AMBIENT = register("entity.deinonychus.ambient");
    public static final RegistryHandler<SoundEvent, SoundEvent> DEINONYCHUS_CALL = register("entity.deinonychus.call");
    public static final RegistryHandler<SoundEvent, SoundEvent> DEINONYCHUS_HURT = register("entity.deinonychus.hurt");
    public static final RegistryHandler<SoundEvent, SoundEvent> DEINONYCHUS_DEATH = register("entity.deinonychus.death");
    public static final RegistryHandler<SoundEvent, SoundEvent> DEINONYCHUS_ATTACK = register("entity.deinonychus.attack");
    public static final RegistryHandler<SoundEvent, SoundEvent> DEINONYCHUS_STEP = register("entity.deinonychus.step");
    public static final RegistryHandler<SoundEvent, SoundEvent> BRACHIOSAURUS_AMBIENT = register("entity.brachiosaurus.ambient");
    public static final RegistryHandler<SoundEvent, SoundEvent> BRACHIOSAURUS_CALL = register("entity.brachiosaurus.call");
    public static final RegistryHandler<SoundEvent, SoundEvent> BRACHIOSAURUS_HURT = register("entity.brachiosaurus.hurt");
    public static final RegistryHandler<SoundEvent, SoundEvent> BRACHIOSAURUS_DEATH = register("entity.brachiosaurus.death");
    public static final RegistryHandler<SoundEvent, SoundEvent> BRACHIOSAURUS_ATTACK = register("entity.brachiosaurus.attack");
    public static final RegistryHandler<SoundEvent, SoundEvent> BRACHIOSAURUS_STEP = register("entity.brachiosaurus.step");

    public static RegistryHandler<SoundEvent, SoundEvent> register(String name){
        return Services.REGISTRY.registerSoundEvent(name);
    }

    public static void init(){
        Constants.LOG.info("Registering sounds.");
    }
}
