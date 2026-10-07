package com.coolerpromc.ancientcreature.tag;

import com.coolerpromc.ancientcreature.Constants;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.structure.Structure;

public class ModStructureTags {
    /** Every dig site; exploration maps found in camps and sold by cartographers lead to these. */
    public static final TagKey<Structure> DIG_SITES = TagKey.create(Registries.STRUCTURE, Constants.id("dig_sites"));
}
