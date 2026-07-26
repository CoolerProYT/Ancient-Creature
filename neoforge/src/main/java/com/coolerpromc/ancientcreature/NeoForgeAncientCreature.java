package com.coolerpromc.ancientcreature;

import com.coolerpromc.ancientcreature.platform.NeoForgeRegistryHelper;
import com.coolerpromc.ancientcreature.platform.Services;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

@Mod(Constants.MODID)
@EventBusSubscriber(modid = Constants.MODID)
public class NeoForgeAncientCreature {
    public NeoForgeAncientCreature(IEventBus eventBus) {
        AncientCreature.init();
        NeoForgeRegistryHelper.register(eventBus);
    }

    @SubscribeEvent
    public static void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
        Services.REGISTRY.applyEntityAttributeRegistrations(event::put);
    }
}