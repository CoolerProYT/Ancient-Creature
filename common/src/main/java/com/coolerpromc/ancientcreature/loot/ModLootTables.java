package com.coolerpromc.ancientcreature.loot;

import com.coolerpromc.ancientcreature.Constants;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;

public final class ModLootTables {
    public static final ResourceKey<LootTable> ABANDONED_DIG_SITE_CHEST = create("chests/abandoned_dig_site");

    public static final ResourceKey<LootTable> ARCHAEOLOGIST_EQUIPMENT = create("equipment/archaeologist");

    private ModLootTables() {
    }

    public static ResourceKey<LootTable> create(String path) {
        return ResourceKey.create(Registries.LOOT_TABLE, Constants.id(path));
    }
}
