package com.coolerpromc.ancientcreature.registry;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.item.FossilPart;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public class ModRegistries {
    public static final ResourceKey<Registry<FossilPart>> FOSSIL_PART = ResourceKey.createRegistryKey(Constants.id("fossil_part"));
}
