package com.coolerpromc.ancientcreature;

import com.coolerpromc.ancientcreature.client.SpeciesJournalKeyHandler;
import com.coolerpromc.ancientcreature.network.HandledCustomPacketPayload;
import com.coolerpromc.ancientcreature.platform.ServicesClient;
import com.coolerpromc.ancientcreature.platform.util.NeoForgePayloadContext;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;

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
        ServicesClient.REGISTRY.applyBlockEntityRendererRegistrations(event::registerBlockEntityRenderer);
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        AncientCreatureClient.initModelLayer();
        ServicesClient.REGISTRY.applyEntityModelLayerRegistrations(event::registerLayerDefinition);
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

    @SubscribeEvent
    public static void onRegisterMenuScreens(RegisterMenuScreensEvent event) {
        AncientCreatureClient.initMenuScreen();
        ServicesClient.REGISTRY.applyMenuScreenRegistrations(event::register);
    }

    @SubscribeEvent
    public static void onRegisterSpecialModelRenderer(RegisterSpecialModelRendererEvent event) {
        AncientCreatureClient.initSpecialModelRenderer();
        ServicesClient.REGISTRY.applySpecialModelRendererRegistrations(event::register);
    }

    @SubscribeEvent
    public static void onAddClientReloadListeners(AddClientReloadListenersEvent event) {
        AncientCreatureClient.initClientReloadListener();
        ServicesClient.REGISTRY.applyClientReloadListenerRegistrations(event::addListener);
    }

    @SubscribeEvent
    public static void onRegisterClientPayloadHandlers(RegisterClientPayloadHandlersEvent event) {
        AncientCreatureClient.initClientPayloadHandler();
        ServicesClient.REGISTRY.applyClientPayloadReceiverRegistrations(new NeoForgePayloadRegistrar(event)::register);
    }

    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(SpeciesJournalKeyHandler.OPEN_JOURNAL);
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        SpeciesJournalKeyHandler.clientTick(net.minecraft.client.Minecraft.getInstance());
    }

    private record NeoForgePayloadRegistrar(RegisterClientPayloadHandlersEvent event) {
        private <T extends HandledCustomPacketPayload> void register(CustomPacketPayload.Type<T> type) {
            event.register(type, (payload, context) -> payload.handle(new NeoForgePayloadContext(context)));
        }
    }
}
