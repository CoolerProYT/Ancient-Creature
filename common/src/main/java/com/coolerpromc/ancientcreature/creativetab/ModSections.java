package com.coolerpromc.ancientcreature.creativetab;

import com.coolerpromc.ancientcreature.block.ModBlocks;
import com.coolerpromc.ancientcreature.creativetab.section.Section;
import com.coolerpromc.ancientcreature.creativetab.section.SectionColored;
import com.coolerpromc.ancientcreature.creativetab.section.SectionTextured;
import com.coolerpromc.ancientcreature.data.component.ModDataComponents;
import com.coolerpromc.ancientcreature.data.component.custom.FossilCompleteness;
import com.coolerpromc.ancientcreature.data.component.custom.FossilPart;
import com.coolerpromc.ancientcreature.item.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ModSections {
    public static List<Section> ALL = List.of();

    public static List<Section> build() {
        List<ItemStack> fragments = new ArrayList<>();
        List<ItemStack> chisels = List.of(
            ModItems.STONE_CHISEL.toStack(),
            ModItems.COPPER_CHISEL.toStack(),
            ModItems.IRON_CHISEL.toStack(),
            ModItems.GOLDEN_CHISEL.toStack(),
            ModItems.DIAMOND_CHISEL.toStack(),
            ModItems.NETHERITE_CHISEL.toStack()
        );
        List<ItemStack> misc = List.of(
            ModItems.EGG_FOSSIL.toStack(),
            ModBlocks.FOSSIL_ORE.toStack(),
            ModBlocks.ROCK_PILE.toStack(),
            ModItems.ROCK_FRAGMENT.toStack(),
            ModItems.DIRT_FRAGMENT.toStack(),
            ModBlocks.FOSSIL_CLEANING_TABLE.toStack(),
            ModBlocks.FOSSIL_IDENTIFICATION_CHAMBER.toStack(),
            ModBlocks.DNA_EXTRACTOR.toStack(),
            ModBlocks.GENOME_SEQUENCER.toStack(),
            ModBlocks.EMBRYOGENESIS_CHAMBER.toStack(),
            ModBlocks.INCUBATOR.toStack(),
            ModItems.DNA_SAMPLE.toStack(),
            ModItems.SAMPLE_VIAL.toStack(),
            ModItems.EXTRACTION_FLUID.toStack(),
            ModItems.GENOME_CARTRIDGE_BLANK.toStack(),
            ModItems.GENOME_CARTRIDGE_FILLED.toStack(),
            ModItems.GENOME_CARTRIDGE_COMPLETED.toStack(),
            ModItems.ARTIFICIAL_EGG.toStack(),
            ModItems.NUTRIENT_SOLUTION.toStack(),
            ModItems.FERTILIZED_ANCIENT_EGG.toStack()
        );

        for (FossilPart value : FossilPart.values()) {
            ItemStack fossilFragment = ModItems.FOSSIL_FRAGMENT.toStack();
            fossilFragment.set(ModDataComponents.FOSSIL_PART.get(), value);
            fossilFragment.set(ModDataComponents.FOSSIL_COMPLETENESS.get(), new FossilCompleteness(1f));
            fragments.add(fossilFragment);

            ItemStack clean = fossilFragment.copy();
            clean.set(ModDataComponents.IS_DIRTY.get(), false);
            fragments.add(clean);
        }

        ALL = List.of(
            SectionTextured.of(
                "fossil_fragments",
                Component.translatable("tab.ancientcreature.fossil_fragments"),
                0xFFFFFFFF,
                fragments
            ),
            SectionTextured.of(
                "chisels",
                Component.translatable("tab.ancientcreature.chisels"),
                0xFFFFFFFF,
                chisels
            ),
            new SectionColored(
                "misc",
                Component.translatable("tab.ancientcreature.misc"),
                0xFF7f5417,
                -1,
                misc
            )
        );
        return ALL;
    }
}
