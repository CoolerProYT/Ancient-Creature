package com.coolerpromc.ancientcreature;

import com.coolerpromc.ancientcreature.platform.ServicesClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.*;

@Mod(dist = Dist.CLIENT, value = Constants.MODID)
@EventBusSubscriber(modid = Constants.MODID, value = Dist.CLIENT)
public class NeoForgeAncientCreatureClient {
    public NeoForgeAncientCreatureClient(IEventBus eventBus){
        AncientCreatureClient.init();
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        AncientCreatureClient.initRenderer();
        ServicesClient.REGISTRY.applyEntityRendererRegistrations(event::registerEntityRenderer);
    }

    @SubscribeEvent
    public static void onRegisterGuiLayer(RegisterGuiLayersEvent event) {
        AncientCreatureClient.initGuiLayer();
        ServicesClient.REGISTRY.applyGuiLayerRegistrations((id, layer) -> event.registerAboveAll(id, layer::render));
    }

    @SubscribeEvent
    public static void onRegisterColorHandlersItemTintSources(RegisterColorHandlersEvent.ItemTintSources event) {
        AncientCreatureClient.initItemTintSource();
        ServicesClient.REGISTRY.applyItemTintSourceRegistrations(event::register);
    }

    @SubscribeEvent
    public static void onRegisterConditionalItemModelProperty(RegisterConditionalItemModelPropertyEvent event) {
        AncientCreatureClient.initItemCondition();
        ServicesClient.REGISTRY.applyItemConditionRegistrations(event::register);
    }

    @SubscribeEvent
    public static void onRegisterSelectItemModelProperty(RegisterSelectItemModelPropertyEvent event) {
        AncientCreatureClient.initItemSelect();
        ServicesClient.REGISTRY.applyItemSelectRegistrations(event::register);
    }
}
