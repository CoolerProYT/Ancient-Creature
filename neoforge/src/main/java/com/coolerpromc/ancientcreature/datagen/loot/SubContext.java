package com.coolerpromc.ancientcreature.datagen.loot;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.function.BiConsumer;

/** The registries and output a loot sub provider writes to, bundled the way newer versions hand them over. */
record SubContext(HolderLookup.Provider registries, BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
    <T> HolderGetter<T> lookup(ResourceKey<? extends Registry<? extends T>> registry) {
        return this.registries.lookupOrThrow(registry);
    }

    void accept(ResourceKey<LootTable> key, LootTable.Builder table) {
        this.output.accept(key, table);
    }
}
