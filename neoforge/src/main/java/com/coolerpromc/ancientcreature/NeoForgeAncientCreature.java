package com.coolerpromc.ancientcreature;

import com.coolerpromc.ancientcreature.creativetab.ModCreativeTabs;
import com.coolerpromc.ancientcreature.event.CreativeTabEvents;
import com.coolerpromc.ancientcreature.event.ItemEvents;
import com.coolerpromc.ancientcreature.platform.NeoForgeRegistryHelper;
import com.coolerpromc.ancientcreature.platform.Services;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@Mod(Constants.MODID)
@EventBusSubscriber(modid = Constants.MODID)
public class NeoForgeAncientCreature {
    public NeoForgeAncientCreature(IEventBus eventBus) {
        AncientCreature.init();
        NeoForgeRegistryHelper.register(eventBus);
    }

    @SubscribeEvent
    public static void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
        AncientCreature.initEntityAttribute();
        Services.REGISTRY.applyEntityAttributeRegistrations(event::put);
    }

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemEvents.onItemTooltip(event.getItemStack(), event.getContext(), event.getFlags(), event.getToolTip());
    }

    @SubscribeEvent
    public static void onBuildCreativeModeTabContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTab() == ModCreativeTabs.TAB.get()){
            CreativeTabEvents.onModifyOutput(event.getParameters());
        }
    }

    @SubscribeEvent
    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        AncientCreature.initCapability();
        Services.CAPABILITIES.applyRegistrations(event);
    }

    @SubscribeEvent
    public static void onRegisterBrewingRecipes(RegisterBrewingRecipesEvent event) {
        AncientCreature.initBrewingRecipe();
        Services.REGISTRY.applyBrewingRecipeRegistrations(event.getBuilder()::addContainerRecipe);
    }
}