package com.coolerpromc.ancientcreature.datagen;

import com.coolerpromc.ancientcreature.Constants;
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

        add(ModItems.FOSSIL_FRAGMENT.get(), "Fossil Fragment");
    }
}
