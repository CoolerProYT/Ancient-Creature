package com.coolerpromc.ancientcreature.datagen;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.block.ModBlocks;
import com.coolerpromc.ancientcreature.item.ModItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class ModLanguageProvider extends LanguageProvider {
    public ModLanguageProvider(PackOutput output) {
        super(output, Constants.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        add("tooltip.ancientcreature.fossil_completeness", "§7Completeness: %s");
        add("tooltip.ancientcreature.genome_completeness", "§7Completeness: %s");
        add("tooltip.ancientcreature.fossil_damage_rate", "-%s%% Completeness");
        add("tooltip.ancientcreature.species", "§7Species: %s");
        add("tooltip.ancientcreature.dna_integrity_level", "§7Integrity Level: %s");

        add("tab.ancientcreature.ancient_creature", "Ancient Creature");
        add("tab.ancientcreature.fossil_fragments", "Fossil Fragments");
        add("tab.ancientcreature.chisels", "Chisels");
        add("tab.ancientcreature.misc", "Misc");

        add("name.ancientcreature.dirty", "Dirty");
        add("name.ancientcreature.unidentified", "Unidentified");

        add("fossilPart.ancientcreature.rib", "Rib");
        add("fossilPart.ancientcreature.tooth", "Tooth");
        add("fossilPart.ancientcreature.skull", "Skull");
        add("fossilPart.ancientcreature.vertebra", "Vertebra");
        add("fossilPart.ancientcreature.limb", "Limb");
        add("fossilPart.ancientcreature.claw", "Claw");

        add("species.ancientcreature.triceratops", "Triceratops");
        add("species.ancientcreature.unidentified", "???");

        add("dna.ancientcreature.degraded", "Degraded");
        add("dna.ancientcreature.partial", "Partial");
        add("dna.ancientcreature.stable", "Stable");
        add("dna.ancientcreature.preserved_embryo", "Preserved Embryo");

        add("subtitles.ancientcreature.block.fossil_identification_chamber.scan", "Fossil chamber scans");
        add("subtitles.ancientcreature.block.fossil_identification_chamber.failed", "Fossil identification fails");
        add("subtitles.ancientcreature.block.fossil_cleaning_table.brush", "Brush scrapes fossil");
        add("subtitles.ancientcreature.block.dna_extractor.processing", "DNA extractor processes sample");
        add("subtitles.ancientcreature.block.genome_sequencer.processing", "Genome sequencer analyzes sample");
        add("subtitles.ancientcreature.block.embryogenesis_chamber.processing", "Embryogenesis chamber cultivates embryo");
        add("subtitles.ancientcreature.block.incubator.processing", "Incubator regulates temperature");

        add(ModItems.STONE_CHISEL.get(), "Stone Chisel");
        add(ModItems.COPPER_CHISEL.get(), "Copper Chisel");
        add(ModItems.IRON_CHISEL.get(), "Iron Chisel");
        add(ModItems.GOLDEN_CHISEL.get(), "Golden Chisel");
        add(ModItems.DIAMOND_CHISEL.get(), "Diamond Chisel");
        add(ModItems.NETHERITE_CHISEL.get(), "Netherite Chisel");

        add(ModItems.FOSSIL_FRAGMENT.get(), "Fossil Fragment");
        add(ModItems.EGG_SHELL_FRAGMENT.get(), "Egg Shell Fragment");
        add(ModItems.EGG_FOSSIL.get(), "Egg Fossil");
        add(ModItems.ROCK_FRAGMENT.get(), "Rock Fragment");
        add(ModItems.DIRT_FRAGMENT.get(), "Dirt Fragment");
        add(ModItems.DNA_SAMPLE.get(), "DNA Sample");
        add(ModItems.EXTRACTION_FLUID.get(), "Extraction Fluid");
        add(ModItems.SAMPLE_VIAL.get(), "Sample Vial");
        add(ModItems.GENOME_CARTRIDGE_BLANK.get(), "Genome Cartridge (Blank)");
        add(ModItems.GENOME_CARTRIDGE_FILLED.get(), "Genome Cartridge (Filled)");
        add(ModItems.GENOME_CARTRIDGE_COMPLETED.get(), "Genome Cartridge (Completed)");
        add(ModItems.ARTIFICIAL_EGG.get(), "Artificial Egg");
        add(ModItems.NUTRIENT_SOLUTION.get(), "Nutrient Solution");
        add(ModItems.FERTILIZED_ANCIENT_EGG.get(), "Fertilized Ancient Egg");
        add(ModItems.BABY_CREATURE_CAPSULE.get(), "Baby Creature Capsule");

        add(ModBlocks.FOSSIL_ORE.getBlock(), "Fossil Ore");
        add(ModBlocks.ROCK_PILE.getBlock(), "Rock Pile");
        add(ModBlocks.FOSSIL_CLEANING_TABLE.getBlock(), "Fossil Cleaning Table");
        add(ModBlocks.FOSSIL_IDENTIFICATION_CHAMBER.getBlock(), "Fossil Identification Chamber");
        add(ModBlocks.DNA_EXTRACTOR.getBlock(), "DNA Extractor");
        add(ModBlocks.GENOME_SEQUENCER.getBlock(), "Genome Sequencer");
        add(ModBlocks.EMBRYOGENESIS_CHAMBER.getBlock(), "Embryogenesis Chamber");
        add(ModBlocks.INCUBATOR.getBlock(), "Incubator");
    }
}
