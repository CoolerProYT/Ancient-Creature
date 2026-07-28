package com.coolerpromc.ancientcreature;

import com.coolerpromc.ancientcreature.platform.ServicesClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.color.item.ItemTintSources;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperties;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperties;

public class FabricAncientCreatureClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        AncientCreatureClient.initAll();
        ServicesClient.REGISTRY.applyEntityRendererRegistrations(EntityRenderers::register);
        ServicesClient.REGISTRY.applyGuiLayerRegistrations((id, layer) -> HudElementRegistry.addLast(id, layer::render));
        ServicesClient.REGISTRY.applyItemTintSourceRegistrations(ItemTintSources.ID_MAPPER::put);
        ServicesClient.REGISTRY.applyItemConditionRegistrations(ConditionalItemModelProperties.ID_MAPPER::put);
        ServicesClient.REGISTRY.applyItemSelectRegistrations(SelectItemModelProperties.ID_MAPPER::put);
    }
}
