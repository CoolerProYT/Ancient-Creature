package com.coolerpromc.ancientcreature.datagen;

import com.coolerpromc.ancientcreature.Constants;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = Constants.MODID)
public class ModDataGenerator {
    @SubscribeEvent
    public static void onGatherData(GatherDataEvent.Client event) {
        ModDataPackProvider datapackProvider = event.createProvider(ModDataPackProvider::new);
        CompletableFuture<HolderLookup.Provider> modRegistries = datapackProvider.getRegistryProvider();
        PackOutput output = event.getGenerator().getPackOutput();

        event.createProvider(ModLanguageProvider::new);
        event.addProvider(new ModModelProvider(output, modRegistries));
        event.createProvider(ModItemTagsProvider::new);
        event.createProvider(ModBlockTagsProvider::new);
        event.createProvider(ModBiomeTagsProvider::new);
        event.addProvider(new ModLootTableProvider(output, modRegistries));
        event.addProvider(new ModRecipeProvider.Runner(output, modRegistries));
        event.addProvider(new ModAdvancementProvider(output, modRegistries));
    }
}
