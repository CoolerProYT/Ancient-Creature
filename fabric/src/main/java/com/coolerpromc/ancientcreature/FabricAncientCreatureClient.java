package com.coolerpromc.ancientcreature;

import com.coolerpromc.ancientcreature.platform.ServicesClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.color.item.ItemTintSources;
import net.minecraft.client.renderer.entity.EntityRenderers;

public class FabricAncientCreatureClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        AncientCreatureClient.initAll();
        ServicesClient.REGISTRY.applyEntityRendererRegistrations(EntityRenderers::register);
        ServicesClient.REGISTRY.applyGuiLayerRegistrations((id, layer) -> HudElementRegistry.addLast(id, layer::render));
        ServicesClient.REGISTRY.applyItemTintSourceRegistrations(ItemTintSources.ID_MAPPER::put);
    }
}
