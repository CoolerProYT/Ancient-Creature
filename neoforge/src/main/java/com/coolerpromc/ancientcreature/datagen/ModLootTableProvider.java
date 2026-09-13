package com.coolerpromc.ancientcreature.datagen;

import com.coolerpromc.ancientcreature.datagen.loot.ModArchaeologyLootSubProvider;
import com.coolerpromc.ancientcreature.datagen.loot.ModBlockLootSubProvider;
import com.coolerpromc.ancientcreature.datagen.loot.ModChestLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.List;
import java.util.Set;

public class ModLootTableProvider extends LootTableProvider {
    public static final List<SubProviderEntry> providers = List.of(
        new SubProviderEntry(ModBlockLootSubProvider::new, LootContextParamSets.BLOCK),
        new SubProviderEntry(ModChestLootSubProvider::new, LootContextParamSets.CHEST),
        new SubProviderEntry(ModArchaeologyLootSubProvider::new, LootContextParamSets.ARCHAEOLOGY)
    );

    public ModLootTableProvider() {
        super(Set.of(), providers);
    }
}
