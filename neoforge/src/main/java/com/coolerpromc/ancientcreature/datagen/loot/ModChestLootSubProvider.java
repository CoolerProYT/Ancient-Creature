package com.coolerpromc.ancientcreature.datagen.loot;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.item.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.function.BiConsumer;

public record ModChestLootSubProvider(HolderLookup.Provider registries) implements LootTableSubProvider {
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
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
        addDigSiteLoot(output, PLAINS_DIG_SITE);
        addDigSiteLoot(output, ARID_DIG_SITE);
        addDigSiteLoot(output, SAVANNA_DIG_SITE);
        addDigSiteLoot(output, BADLANDS_DIG_SITE);
        addDigSiteLoot(output, JUNGLE_DIG_SITE);
        addDigSiteLoot(output, COLD_DIG_SITE);
        addDigSiteLoot(output, SWAMP_DIG_SITE);
        addDigSiteLoot(output, HIGHLAND_DIG_SITE);
        addDigSiteLoot(output, COASTAL_DIG_SITE);
    }

    private static void addDigSiteLoot(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output, ResourceKey<LootTable> lootTable) {
        output.accept(lootTable, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1)).add(LootItem.lootTableItem(ModItems.FOSSIL_FRAGMENT))));
    }

    private static ResourceKey<LootTable> register(String location) {
        return ResourceKey.create(Registries.LOOT_TABLE, Constants.id(location));
    }
}
