package com.coolerpromc.ancientcreature;

import com.coolerpromc.ancientcreature.creativetab.ModCreativeTabs;
import com.coolerpromc.ancientcreature.event.CreativeTabEvents;
import com.coolerpromc.ancientcreature.event.ItemEvents;
import com.coolerpromc.ancientcreature.event.PlayerEvents;
import com.coolerpromc.ancientcreature.platform.Services;
import com.coolerpromc.ancientcreature.worldgen.feature.ModPlacedFeatures;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.registry.FabricPotionBrewingBuilder;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.levelgen.GenerationStep;

public class FabricAncientCreature implements ModInitializer {
    @Override
    public void onInitialize() {
        AncientCreature.init();
        AncientCreature.initCapability();
        AncientCreature.initEntityAttribute();
        AncientCreature.initBiomeModifier();
        AncientCreature.initBrewingRecipe();
        AncientCreature.initDatapackRegistry();
        AncientCreature.initPayloadType();

        Services.REGISTRY.applyEntityAttributeRegistrations(FabricDefaultAttributeRegistry::register);
        Services.REGISTRY.applyBiomeModifierRegistrations((biomeTagKey, step, placedFeatureKey) -> BiomeModifications.addFeature(BiomeSelectors.tag(biomeTagKey), step, placedFeatureKey));

        ItemTooltipCallback.EVENT.register(ItemEvents::onItemTooltip);
        CreativeModeTabEvents.modifyOutputEvent(ModCreativeTabs.TAB.key()).register(output -> CreativeTabEvents.onModifyOutput(output.getContext()));
        ServerPlayerEvents.JOIN.register(PlayerEvents::onPlayerJoin);
        Services.CAPABILITIES.applyRegistrations(null);

        FabricPotionBrewingBuilder.BUILD.register(builder -> Services.REGISTRY.applyBrewingRecipeRegistrations(builder::addContainerRecipe));
        Services.REGISTRY.applyDatapackRegistryRegistrations(DynamicRegistries::registerSynced);

        Services.REGISTRY.applyClientboundPayloadRegistrations(PayloadTypeRegistry.clientboundPlay()::register);
    }
}
