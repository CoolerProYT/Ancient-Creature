package com.coolerpromc.ancientcreature.creativetab;

import com.coolerpromc.ancientcreature.block.ModBlocks;
import com.coolerpromc.ancientcreature.creativetab.section.Section;
import com.coolerpromc.ancientcreature.creativetab.section.SectionTextured;
import com.coolerpromc.ancientcreature.data.component.ModDataComponents;
import com.coolerpromc.ancientcreature.data.component.custom.*;
import com.coolerpromc.ancientcreature.item.DNAIntegrityLevel;
import com.coolerpromc.ancientcreature.item.FossilPart;
import com.coolerpromc.ancientcreature.entity.Species;
import com.coolerpromc.ancientcreature.item.ModItems;
import com.coolerpromc.ancientcreature.registry.ModRegistries;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ModSections {
    public static List<Section> ALL = List.of();

    public static List<Section> build(CreativeModeTab.ItemDisplayParameters params) {
        List<ItemStack> fossils = new ArrayList<>();
        List<ItemStack> fertilizedAncientEggs = new ArrayList<>();
        List<ItemStack> dnaSamples = new ArrayList<>();
        List<ItemStack> genomes = new ArrayList<>();
        genomes.add(ModItems.GENOME_CARTRIDGE_BLANK.toStack());
        List<ItemStack> chisels = List.of(
            ModItems.STONE_CHISEL.toStack(),
            ModItems.COPPER_CHISEL.toStack(),
            ModItems.IRON_CHISEL.toStack(),
            ModItems.GOLDEN_CHISEL.toStack(),
            ModItems.DIAMOND_CHISEL.toStack(),
            ModItems.NETHERITE_CHISEL.toStack()
        );
        List<ItemStack> machines = List.of(
            ModBlocks.FOSSIL_CLEANING_TABLE.toStack(),
            ModBlocks.FOSSIL_IDENTIFICATION_CHAMBER.toStack(),
            ModBlocks.DNA_EXTRACTOR.toStack(),
            ModBlocks.GENOME_SEQUENCER.toStack(),
            ModBlocks.EMBRYOGENESIS_CHAMBER.toStack(),
            ModBlocks.INCUBATOR.toStack()
        );
        List<ItemStack> misc = List.of(
            ModBlocks.FOSSIL_ORE.toStack(),
            ModBlocks.ROCK_PILE.toStack(),
            ModItems.ROCK_FRAGMENT.toStack(),
            ModItems.DIRT_FRAGMENT.toStack(),
            ModItems.SAMPLE_VIAL.toStack(),
            ModItems.EXTRACTION_FLUID.toStack(),
            ModItems.ARTIFICIAL_EGG.toStack(),
            ModItems.NUTRIENT_SOLUTION.toStack()
        );

        for (Species species : Species.values()) {
            for (DNAIntegrityLevel value : DNAIntegrityLevel.values()) {
                ItemStack dnaSample = ModItems.DNA_SAMPLE.toStack();
                dnaSample.set(ModDataComponents.DNA_DATA.get(), new DNAData(value, species));
                dnaSamples.add(dnaSample);
            }

            ItemStack filledGenome = ModItems.GENOME_CARTRIDGE_FILLED.toStack();
            filledGenome.set(ModDataComponents.GENOME_DATA.get(), new GenomeData(0.5f, species));
            genomes.add(filledGenome);

            ItemStack completedGenome = ModItems.GENOME_CARTRIDGE_COMPLETED.toStack();
            completedGenome.set(ModDataComponents.SPECIES.get(), species);
            genomes.add(completedGenome);

            ItemStack fertilizedAncientEgg = ModItems.FERTILIZED_ANCIENT_EGG.toStack();
            fertilizedAncientEgg.set(ModDataComponents.SPECIES.get(), species);
            fertilizedAncientEggs.add(fertilizedAncientEgg);

            ItemStack capsule = ModItems.BABY_CREATURE_CAPSULE.toStack();
            capsule.set(ModDataComponents.SPECIES.get(), species);
            fertilizedAncientEggs.add(capsule);

            for (Holder<FossilPart> value : params.holders().lookupOrThrow(ModRegistries.FOSSIL_PART).listElements().toList()) {
                ItemStack fossilFragment = ModItems.FOSSIL_PART.toStack();
                fossilFragment.set(ModDataComponents.FOSSIL_DATA.get(), new FossilData(value, species, 1f, true, false));
                fossils.add(fossilFragment);

                ItemStack clean = fossilFragment.copy();
                fossilFragment.set(ModDataComponents.FOSSIL_DATA.get(), new FossilData(value, species, 1f, false, true));
                fossils.add(clean);
            }
        }

        ALL = List.of(
            SectionTextured.of(
                "fossils",
                Component.translatable("tab.ancientcreature.fossils"),
                0xFFFFFFFF,
                fossils
            ),
            SectionTextured.of(
                "chisels",
                Component.translatable("tab.ancientcreature.chisels"),
                0xFFFFFFFF,
                chisels
            ),
            SectionTextured.of(
                "machines",
                Component.translatable("tab.ancientcreature.machines"),
                0xFFFFFFFF,
                machines
            ),
            SectionTextured.of(
                "dna_samples",
                Component.translatable("tab.ancientcreature.dna_samples"),
                0xFFFFFFFF,
                dnaSamples
            ),
            SectionTextured.of(
                "genomes",
                Component.translatable("tab.ancientcreature.genomes"),
                0xFFFFFFFF,
                genomes
            ),
            SectionTextured.of(
                "processed_items",
                Component.translatable("tab.ancientcreature.processed_items"),
                0xFFFFFFFF,
                fertilizedAncientEggs
            ),
            SectionTextured.of(
                "misc",
                Component.translatable("tab.ancientcreature.misc"),
                0xFFFFFFFF,
                misc
            )
        );
        return ALL;
    }
}
