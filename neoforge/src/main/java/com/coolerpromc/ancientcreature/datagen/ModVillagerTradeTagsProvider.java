package com.coolerpromc.ancientcreature.datagen;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.trade.ModVillagerTrades;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.KeyTagProvider;
import net.minecraft.tags.VillagerTradeTags;
import net.minecraft.world.item.trading.VillagerTrade;

import java.util.concurrent.CompletableFuture;

public class ModVillagerTradeTagsProvider extends KeyTagProvider<VillagerTrade> {
    public ModVillagerTradeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, Registries.VILLAGER_TRADE, lookupProvider, Constants.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(VillagerTradeTags.WANDERING_TRADER_UNCOMMON).add(ModVillagerTrades.WANDERING_TRADER_FOSSIL, ModVillagerTrades.WANDERING_TRADER_AMBER);
        tag(VillagerTradeTags.CARTOGRAPHER_LEVEL_3).add(ModVillagerTrades.CARTOGRAPHER_DIG_SITE_MAP);
    }
}
