package com.coolerpromc.ancientcreature.trade;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.item.FossilPart;
import com.coolerpromc.ancientcreature.item.ModItems;
import com.coolerpromc.ancientcreature.loot.custom.SetFossilDataFunction;
import com.coolerpromc.ancientcreature.registry.ModRegistries;
import com.coolerpromc.ancientcreature.tag.ModStructureTags;
import net.minecraft.advancements.predicates.DataComponentMatchers;
import net.minecraft.advancements.predicates.ItemPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.TradeCost;
import net.minecraft.world.item.trading.VillagerTrade;
import net.minecraft.world.level.saveddata.maps.MapDecorationTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.storage.loot.functions.DiscardItem;
import net.minecraft.world.level.storage.loot.functions.ExplorationMapFunction;
import net.minecraft.world.level.storage.loot.functions.FilteredFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.SetNameFunction;

import java.util.List;
import java.util.Optional;

/** Trades that put fossils within reach of players who would rather pay than dig. */
public final class ModVillagerTrades {
    /** A travelling trader's find: an uncleaned fossil from wherever the trader happens to be. */
    public static final ResourceKey<VillagerTrade> WANDERING_TRADER_FOSSIL = key("wandering_trader/emerald_fossil_part");
    public static final ResourceKey<VillagerTrade> WANDERING_TRADER_AMBER = key("wandering_trader/emerald_amber");
    public static final ResourceKey<VillagerTrade> CARTOGRAPHER_DIG_SITE_MAP = key("cartographer/3/emerald_and_compass_dig_site_map");

    public static final String DIG_SITE_MAP_NAME = "item.ancientcreature.dig_site_map";

    private ModVillagerTrades() {
    }

    public static void bootstrap(BootstrapContext<VillagerTrade> context) {
        HolderGetter<FossilPart> parts = context.lookup(ModRegistries.FOSSIL_PART);
        HolderGetter<Item> items = context.lookup(Registries.ITEM);

        List<Holder<FossilPart>> bones = FossilPart.BONE_PARTS.stream()
            .filter(key -> key != FossilPart.EGG)
            .<Holder<FossilPart>>map(parts::getOrThrow)
            .toList();

        context.register(WANDERING_TRADER_FOSSIL, new VillagerTrade(new TradeCost(Items.EMERALD, 12), new ItemStackTemplate(ModItems.FOSSIL_PART.get()), 2, 1, 0.05F, Optional.empty(),
            List.of(SetFossilDataFunction.setData(bones).build())));
        context.register(WANDERING_TRADER_AMBER, new VillagerTrade(new TradeCost(Items.EMERALD, 20), new ItemStackTemplate(ModItems.FOSSIL_PART.get()), 1, 1, 0.05F, Optional.empty(),
            List.of(SetFossilDataFunction.setData(List.of(parts.getOrThrow(FossilPart.AMBER))).build())));
        context.register(CARTOGRAPHER_DIG_SITE_MAP, new VillagerTrade(new TradeCost(Items.EMERALD, 10), Optional.of(new TradeCost(Items.COMPASS, 1)), new ItemStackTemplate(Items.MAP), 12, 10, 0.2F, Optional.empty(),
            List.of(
                ExplorationMapFunction.makeExplorationMap()
                    .setDestination(ModStructureTags.DIG_SITES)
                    .setMapDecoration(MapDecorationTypes.RED_X)
                    .setSearchRadius(100)
                    .setSkipKnownStructures(true)
                    .build(),
                SetNameFunction.setName(Component.translatable(DIG_SITE_MAP_NAME), SetNameFunction.Target.ITEM_NAME).build(),
                filledMapOnly(items)
            )));
    }

    /** Vanilla's guard for map trades: if no site was found the map stays blank, so drop the trade's result. */
    public static LootItemFunction filledMapOnly(HolderGetter<Item> items) {
        return FilteredFunction.filtered(
                new ItemPredicate.Builder()
                    .of(items, Items.FILLED_MAP)
                    .withComponents(DataComponentMatchers.Builder.components().any(DataComponents.MAP_ID).build())
                    .build()
            )
            .onFail(Optional.of(DiscardItem.discardItem().build()))
            .build();
    }

    private static ResourceKey<VillagerTrade> key(String path) {
        return ResourceKey.create(Registries.VILLAGER_TRADE, Constants.id(path));
    }
}
