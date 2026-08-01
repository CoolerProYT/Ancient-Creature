package com.coolerpromc.ancientcreature.tag;

import com.coolerpromc.ancientcreature.Constants;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ModItemTags {
    public static final TagKey<Item> CHISELS = create("chisels");
    public static final TagKey<Item> EXTRACTION_FLUIDS = create("extraction_fluids");

    public static TagKey<Item> create(String name){
        return TagKey.create(Registries.ITEM, Constants.id(name));
    }
}
