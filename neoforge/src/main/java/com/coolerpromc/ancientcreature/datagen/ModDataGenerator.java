package com.coolerpromc.ancientcreature.datagen;

import com.coolerpromc.ancientcreature.Constants;
import net.minecraft.core.HolderLookup;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = Constants.MODID)
public class ModDataGenerator {
    @SubscribeEvent
    public static void onGatherData(GatherDataEvent.Client event) {
        event.createProvider(ModLanguageProvider::new);
        event.createProvider(ModModelProvider::new);

        ModDataPackProvider datapackProvider = event.createProvider(ModDataPackProvider::new);
        CompletableFuture<HolderLookup.Provider> modRegistries = datapackProvider.getRegistryProvider();

        event.createProvider(ModItemTagsProvider::new);
        event.createProvider(ModBlockTagsProvider::new);
        event.createProvider(ModBiomeTagsProvider::new);
        event.addProvider(new ModLootTableProvider(event.getGenerator().getPackOutput(), modRegistries));
        event.addProvider(new ModRecipeProvider.Runner(event.getGenerator().getPackOutput(), modRegistries));
        event.addProvider(new ModAdvancementProvider(event.getGenerator().getPackOutput(), modRegistries));
    }
}
