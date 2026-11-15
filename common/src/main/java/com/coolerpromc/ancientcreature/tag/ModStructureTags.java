package com.coolerpromc.ancientcreature.tag;

import com.coolerpromc.ancientcreature.Constants;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.structure.Structure;

public class ModStructureTags {

    public static final TagKey<Structure> DIG_SITES = TagKey.create(Registries.STRUCTURE, Constants.id("dig_sites"));
}
