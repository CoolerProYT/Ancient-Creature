package com.coolerpromc.ancientcreature.datagen;

import com.coolerpromc.ancientcreature.datagen.loot.ModBlockLootSubProvider;
import com.coolerpromc.ancientcreature.datagen.loot.ModChestLootSubProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ModLootTableProvider extends LootTableProvider {
    public static final List<SubProviderEntry> providers = List.of(
        new SubProviderEntry(ModBlockLootSubProvider::new, LootContextParamSets.BLOCK),
        new SubProviderEntry(ModChestLootSubProvider::new, LootContextParamSets.CHEST)
    );

    public ModLootTableProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, Set.of(), providers, registries);
    }
}
