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

    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
        output.accept(PLAINS_DIG_SITE, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1)).add(LootItem.lootTableItem(ModItems.RIB_FOSSIL_FRAGMENT))));
    }

    private static ResourceKey<LootTable> register(String location) {
        return ResourceKey.create(Registries.LOOT_TABLE, Constants.id(location));
    }
}
