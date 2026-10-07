package com.coolerpromc.ancientcreature.datagen.loot;

import com.coolerpromc.ancientcreature.item.FossilPart;
import com.coolerpromc.ancientcreature.item.ModItems;
import com.coolerpromc.ancientcreature.loot.ModLootTables;
import com.coolerpromc.ancientcreature.loot.custom.FieldNotesFunction;
import com.coolerpromc.ancientcreature.registry.ModRegistries;
import com.coolerpromc.ancientcreature.tag.ModStructureTags;
import com.coolerpromc.ancientcreature.worldgen.structure.DigSiteType;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.saveddata.maps.MapDecorationTypes;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import com.coolerpromc.ancientcreature.trade.ModVillagerTrades;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.trading.VillagerTrades;
import net.minecraft.world.level.storage.loot.functions.DiscardItem;
import net.minecraft.world.level.storage.loot.functions.ExplorationMapFunction;
import net.minecraft.world.level.storage.loot.functions.FilteredFunction;
import net.minecraft.world.level.storage.loot.functions.SetNameFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders;

import java.util.List;

import static com.coolerpromc.ancientcreature.item.FossilPart.*;

public record ModChestLootSubProvider(LootTableSubProvider.Context context) implements LootTableSubProvider {
    @Override
    public void run() {
        HolderGetter<FossilPart> parts = this.context.lookup(ModRegistries.FOSSIL_PART);
        HolderGetter<Structure> structures = this.context.lookup(Registries.STRUCTURE);

        for (DigSiteType type : DigSiteType.values()) {
            LootPool.Builder finds = LootPool.lootPool().setRolls(ContextIntProviders.between(1, 2));
            FossilLoot.addWeightedFossils(finds, parts, FossilLoot.digSiteParts(type), FossilLoot.digSiteSpecies(type), FossilLoot.digSiteBonus(type));

            this.context.accept(type.chestLoot(), LootTable.lootTable()
                .withPool(finds)
                .withPool(fieldNotes(3, 2))
                .withPool(digSiteMap(structures, 0.25f))
                .withPool(LootPool.lootPool().setRolls(ContextIntProviders.between(2, 4))
                    .add(LootItem.lootTableItem(Items.BRUSH).setWeight(2))
                    .add(LootItem.lootTableItem(ModItems.STONE_CHISEL).setWeight(2))
                    .add(LootItem.lootTableItem(ModItems.COPPER_CHISEL).setWeight(1))
                    .add(LootItem.lootTableItem(ModItems.IRON_CHISEL).setWeight(1))
                    .add(LootItem.lootTableItem(ModItems.EGG_SHELL_FRAGMENT).setWeight(4).apply(SetItemCountFunction.setCount(ContextIntProviders.between(1, 3))))
                    .add(LootItem.lootTableItem(ModItems.SAMPLE_VIAL).setWeight(2).apply(SetItemCountFunction.setCount(ContextIntProviders.between(1, 2))))
                    .add(LootItem.lootTableItem(Items.PAPER).setWeight(4).apply(SetItemCountFunction.setCount(ContextIntProviders.between(2, 6))))
                    .add(LootItem.lootTableItem(Items.BREAD).setWeight(4).apply(SetItemCountFunction.setCount(ContextIntProviders.between(1, 3))))
                    .add(LootItem.lootTableItem(Items.TORCH).setWeight(3).apply(SetItemCountFunction.setCount(ContextIntProviders.between(2, 6))))
                    .add(LootItem.lootTableItem(Items.COAL).setWeight(3).apply(SetItemCountFunction.setCount(ContextIntProviders.between(1, 4))))
                    .add(LootItem.lootTableItem(Items.EMERALD).setWeight(2).apply(SetItemCountFunction.setCount(ContextIntProviders.between(1, 3))))
                    .add(LootItem.lootTableItem(Items.LANTERN).setWeight(1)))
            );
        }

        // The overrun camps kept their best finds locked up: rarer parts, better preserved.
        LootPool.Builder finds = LootPool.lootPool().setRolls(ContextIntProviders.between(2, 3));
        FossilLoot.addWeightedFossils(finds, parts, FossilLoot.weights(SKULL, 4, VERTEBRA, 3, TOOTH, 3, EGG, 2, AMBER, 1), List.of(), 0.1f);
        this.context.accept(ModLootTables.ABANDONED_DIG_SITE_CHEST, LootTable.lootTable()
            .withPool(finds)
            .withPool(fieldNotes(1, 0))
            .withPool(digSiteMap(structures, 0.5f))
            .withPool(LootPool.lootPool().setRolls(ContextIntProviders.between(3, 5))
                .add(LootItem.lootTableItem(ModItems.IRON_CHISEL).setWeight(3))
                .add(LootItem.lootTableItem(ModItems.GOLDEN_CHISEL).setWeight(2))
                .add(LootItem.lootTableItem(ModItems.DIAMOND_CHISEL).setWeight(1))
                .add(LootItem.lootTableItem(ModItems.EXTRACTION_FLUID).setWeight(2))
                .add(LootItem.lootTableItem(ModItems.SAMPLE_VIAL).setWeight(3).apply(SetItemCountFunction.setCount(ContextIntProviders.between(1, 3))))
                .add(LootItem.lootTableItem(ModItems.EGG_SHELL_FRAGMENT).setWeight(4).apply(SetItemCountFunction.setCount(ContextIntProviders.between(2, 4))))
                .add(LootItem.lootTableItem(Items.EMERALD).setWeight(3).apply(SetItemCountFunction.setCount(ContextIntProviders.between(2, 5))))
                .add(LootItem.lootTableItem(Items.GOLDEN_APPLE).setWeight(1))
                .add(LootItem.lootTableItem(Items.ROTTEN_FLESH).setWeight(5).apply(SetItemCountFunction.setCount(ContextIntProviders.between(2, 6))))
                .add(LootItem.lootTableItem(Items.COBWEB).setWeight(2).apply(SetItemCountFunction.setCount(ContextIntProviders.between(1, 3)))))
        );
    }

    /** The expedition's notebook: which species were recorded in this region, and some excavation tips. */
    private static LootPool.Builder fieldNotes(int weight, int emptyWeight) {
        LootPool.Builder pool = LootPool.lootPool().setRolls(ContextIntProviders.exactly(1))
            .add(LootItem.lootTableItem(Items.WRITTEN_BOOK).setWeight(weight).apply(FieldNotesFunction.fieldNotes()));
        if (emptyWeight > 0) {
            pool.add(EmptyLootItem.emptyItem().setWeight(emptyWeight));
        }
        return pool;
    }

    /** A map to another, still unexplored dig site. */
    private static LootPool.Builder digSiteMap(HolderGetter<Structure> structures, float chance) {
        return LootPool.lootPool().setRolls(ContextIntProviders.exactly(1))
            .add(LootItem.lootTableItem(Items.FILLED_MAP).setWeight(Math.round(chance * 100))
                .apply(ExplorationMapFunction.makeExplorationMap(structures.getOrThrow(ModStructureTags.DIG_SITES))
                    .setMapDecoration(MapDecorationTypes.RED_X)
                    .setSearchRadius(50)
                    .setSkipKnownStructures(true))
                .apply(SetNameFunction.setName(Component.translatable(ModVillagerTrades.DIG_SITE_MAP_NAME), SetNameFunction.Target.ITEM_NAME))
                .apply(FilteredFunction.filtered(VillagerTrades.anyValidMap().build()).onFail(DiscardItem.discardItem().build())))
            .add(EmptyLootItem.emptyItem().setWeight(100 - Math.round(chance * 100)));
    }
}
