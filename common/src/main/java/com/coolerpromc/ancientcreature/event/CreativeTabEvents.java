package com.coolerpromc.ancientcreature.event;

import com.coolerpromc.ancientcreature.creativetab.ModSections;
import com.coolerpromc.ancientcreature.creativetab.TabLayout;
import com.coolerpromc.ancientcreature.creativetab.section.Section;
import net.minecraft.world.item.CreativeModeTab;

import java.util.List;

public class CreativeTabEvents {
    public static void onModifyOutput(CreativeModeTab.ItemDisplayParameters context) {
        List<Section> sections = ModSections.build();
        TabLayout.build(sections);
    }
}
