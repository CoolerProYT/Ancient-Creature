package com.coolerpromc.ancientcreature.datagen.loot;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.item.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders;

public record ModChestLootSubProvider(LootTableSubProvider.Context context) implements LootTableSubProvider {
    public static final ResourceKey<LootTable> PLAINS_DIG_SITE = register("chests/plains_dig_site");
    public static final ResourceKey<LootTable> ARID_DIG_SITE = register("chests/arid_dig_site");
    public static final ResourceKey<LootTable> SAVANNA_DIG_SITE = register("chests/savanna_dig_site");
    public static final ResourceKey<LootTable> BADLANDS_DIG_SITE = register("chests/badlands_dig_site");
    public static final ResourceKey<LootTable> JUNGLE_DIG_SITE = register("chests/jungle_dig_site");
    public static final ResourceKey<LootTable> COLD_DIG_SITE = register("chests/cold_dig_site");
    public static final ResourceKey<LootTable> SWAMP_DIG_SITE = register("chests/swamp_dig_site");
    public static final ResourceKey<LootTable> HIGHLAND_DIG_SITE = register("chests/highland_dig_site");
    public static final ResourceKey<LootTable> COASTAL_DIG_SITE = register("chests/coastal_dig_site");

    @Override
    public void run() {
        addDigSiteLoot(PLAINS_DIG_SITE);
        addDigSiteLoot(ARID_DIG_SITE);
        addDigSiteLoot(SAVANNA_DIG_SITE);
        addDigSiteLoot(BADLANDS_DIG_SITE);
        addDigSiteLoot(JUNGLE_DIG_SITE);
        addDigSiteLoot(COLD_DIG_SITE);
        addDigSiteLoot(SWAMP_DIG_SITE);
        addDigSiteLoot(HIGHLAND_DIG_SITE);
        addDigSiteLoot(COASTAL_DIG_SITE);
    }

    private void addDigSiteLoot(ResourceKey<LootTable> lootTable) {
        context.accept(lootTable, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ContextIntProviders.exactly(1)).add(LootItem.lootTableItem(ModItems.FOSSIL_PART))));
    }

    private static ResourceKey<LootTable> register(String location) {
        return ResourceKey.create(Registries.LOOT_TABLE, Constants.id(location));
    }
}
