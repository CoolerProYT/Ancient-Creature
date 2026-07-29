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
        add("tooltip.ancientcreature.fossil_damage_rate", "-%s%% Completeness");
        add("tooltip.ancientcreature.species", "§7Species: %s");

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

        add(ModBlocks.FOSSIL_ORE.getBlock(), "Fossil Ore");
        add(ModBlocks.ROCK_PILE.getBlock(), "Rock Pile");
        add(ModBlocks.FOSSIL_CLEANING_TABLE.getBlock(), "Fossil Cleaning Table");
    }
}
