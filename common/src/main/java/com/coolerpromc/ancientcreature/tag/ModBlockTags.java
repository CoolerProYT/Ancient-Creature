package com.coolerpromc.ancientcreature.tag;

import com.coolerpromc.ancientcreature.Constants;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class ModBlockTags {
    public static final TagKey<Block> MINEABLE_WITH_CHISEL = create("mineable_with_chisel");
    public static final TagKey<Block> CREATURE_DESTROYABLE = create("creature_destroyable");

    public static TagKey<Block> create(String name){
        return TagKey.create(Registries.BLOCK, Constants.id(name));
    }
}
