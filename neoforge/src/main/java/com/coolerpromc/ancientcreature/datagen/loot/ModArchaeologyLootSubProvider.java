package com.coolerpromc.ancientcreature.datagen.loot;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.item.FossilPart;
import com.coolerpromc.ancientcreature.item.ModItems;
import com.coolerpromc.ancientcreature.loot.custom.SetFossilDataFunction;
import com.coolerpromc.ancientcreature.registry.ModRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders;

import java.util.List;

import static com.coolerpromc.ancientcreature.item.FossilPart.*;

public record ModArchaeologyLootSubProvider(LootTableSubProvider.Context context) implements LootTableSubProvider {
    public static final ResourceKey<LootTable> DIG_SITE_GRAVEL = register("archaeology/dig_site_gravel");
    public static final ResourceKey<LootTable> DIG_SITE_SAND = register("archaeology/dig_site_sand");

    @Override
    public void run() {
        addBrushingLoot(DIG_SITE_GRAVEL);
        addBrushingLoot(DIG_SITE_SAND);
    }

    private void addBrushingLoot(ResourceKey<LootTable> lootTable) {
        context.accept(lootTable, LootTable.lootTable().withPool(
            LootPool.lootPool()
                .setRolls(ContextIntProviders.exactly(1))
                .add(LootItem.lootTableItem(ModItems.FOSSIL_PART).setWeight(5).apply(SetFossilDataFunction.setData(List.of(byKey(CLAW), byKey(TOOTH), byKey(RIB), byKey(LIMB)))))
                .add(LootItem.lootTableItem(ModItems.EGG_SHELL_FRAGMENT).setWeight(10))
                .add(LootItem.lootTableItem(Items.COAL).setWeight(20))
                .add(LootItem.lootTableItem(Items.STICK).setWeight(20))
                .add(LootItem.lootTableItem(Items.BONE).setWeight(20))
                .add(EmptyLootItem.emptyItem().setWeight(1))
            )
        );
    }

    private Holder<FossilPart> byKey(ResourceKey<FossilPart> key){
        HolderGetter<FossilPart> fossilParts = context.lookup(ModRegistries.FOSSIL_PART);
        return fossilParts.getOrThrow(key);
    }

    private static ResourceKey<LootTable> register(String location) {
        return ResourceKey.create(Registries.LOOT_TABLE, Constants.id(location));
    }
}
