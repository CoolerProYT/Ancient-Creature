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

        add("jei.ancientcreature.category.fossil_hunting", "Fossil Part Hunting");
        add("jei.ancientcreature.method.chiseling", "Method: Chisel fossil ore");
        add("jei.ancientcreature.method.rock_pile", "Method: Break a rock pile that has egg");
        add("jei.ancientcreature.method.brushing", "Method: Brush suspicious blocks at dig sites");
        add("jei.ancientcreature.biomes", "Biomes: %s");
        add("jei.ancientcreature.biome.overworld", "All Overworld biomes");
        add("jei.ancientcreature.biome.ocean", "Ocean biomes only");
        add("jei.ancientcreature.species_result", "Possible species: %s");
        add("jei.ancientcreature.parts", "Possible parts: %s");
        add("jei.ancientcreature.parts_egg_only", "Result: Egg fossil");
        add("jei.ancientcreature.completeness_range", "Completeness: %s%%-%s%%");
        add("jei.ancientcreature.fortune_bonus", "Fortune adds 10% completeness before chisel damage");
        add("jei.ancientcreature.rock_pile_fossil_condition", "Only rock piles visibly containing a fossil yield this");
        add("jei.ancientcreature.rock_pile_egg_condition", "Only rock piles visibly containing an egg yield this");
        add("jei.ancientcreature.archaeology_chance", "Fossil chance: 5/76 (6.58%%); skulls/vertebrae unavailable");
        add("jei.ancientcreature.cleaning_preserves", "Preserves species, part, and completeness");
        add("jei.ancientcreature.dirt_output", "Also yields 0-2 dirt fragments; costs 1 brush durability");
        add("jei.ancientcreature.identification_chance", "First identification failure chance: %s%%");
        add("jei.ancientcreature.identification_damage", "On failure, completeness is reduced by %s%%");
        add("jei.ancientcreature.identification_time", "400 ticks for a new species; 100 known species");
        add("jei.ancientcreature.dna_formula", "Integrity score = completeness + fossil-part bonus");
        add("jei.ancientcreature.egg_limb_bonus", "Egg fossils use the limb bonus (0 points)");
        add("jei.ancientcreature.integrity_result", "Resulting integrity: %s");
        add("jei.ancientcreature.extraction_consumption", "Consumes fossil + vial; costs 1 fluid durability");
        add("jei.ancientcreature.genome_added", "Genome completeness added: %s%%-%s%%");
        add("jei.ancientcreature.genome_caps", "At 100%%, the cartridge becomes completed");
        add("jei.ancientcreature.same_species", "DNA Sample must have same species as filled cartridge");
        add("jei.ancientcreature.incubation_time", "Incubation time: %ss");

        add("tab.ancientcreature.ancient_creature", "Ancient Creature");
        add("tab.ancientcreature.fossils", "Fossils");
        add("tab.ancientcreature.chisels", "Chisels");
        add("tab.ancientcreature.machines", "Machines");
        add("tab.ancientcreature.dna_samples", "DNA Samples");
        add("tab.ancientcreature.genomes", "Genomes");
        add("tab.ancientcreature.processed_items", "Processed Items");
        add("tab.ancientcreature.fertilized_ancient_eggs", "Fertilized Eggs");
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
        add("species.ancientcreature.tyrannosaurus_rex", "Tyrannosaurus Rex");
        add("species.ancientcreature.megalodon", "Megalodon");
        add("entity.ancientcreature.tyrannosaurus_rex", "Tyrannosaurus Rex");
        add("entity.ancientcreature.megalodon", "Megalodon");
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
        add("subtitles.ancientcreature.entity.triceratops.ambient", "Triceratops rumbles");
        add("subtitles.ancientcreature.entity.triceratops.bellow", "Triceratops bellows");
        add("subtitles.ancientcreature.entity.triceratops.hurt", "Triceratops hurts");
        add("subtitles.ancientcreature.entity.triceratops.death", "Triceratops dies");
        add("subtitles.ancientcreature.entity.triceratops.attack", "Triceratops attacks");
        add("subtitles.ancientcreature.entity.triceratops.step", "Triceratops steps");
        add("subtitles.ancientcreature.entity.tyrannosaurus_rex.ambient", "Tyrannosaurus rex rumbles");
        add("subtitles.ancientcreature.entity.tyrannosaurus_rex.roar", "Tyrannosaurus rex roars");
        add("subtitles.ancientcreature.entity.tyrannosaurus_rex.hurt", "Tyrannosaurus rex hurts");
        add("subtitles.ancientcreature.entity.tyrannosaurus_rex.death", "Tyrannosaurus rex dies");
        add("subtitles.ancientcreature.entity.tyrannosaurus_rex.bite", "Tyrannosaurus rex bites");
        add("subtitles.ancientcreature.entity.tyrannosaurus_rex.step", "Tyrannosaurus rex steps");

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
