package com.coolerpromc.ancientcreature.platform.services.client;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

public interface IRegistryHelper {
    <T extends Entity> void registerEntityRenderer(EntityType<T> entityType, EntityRendererProvider<T> provider);
    void registerGuiLayer(Identifier id, ModGuiLayer layer);
    void registerItemTintSource(Identifier id, MapCodec<? extends ItemTintSource> mapCodec);

    void applyEntityRendererRegistrations(EntityRendererRegistrar registrar);
    void applyGuiLayerRegistrations(GuiLayerRegistrar registrar);
    void applyItemTintSourceRegistrations(ItemTintSourceRegistrar registrar);

    interface EntityRendererRegistrar {
        <T extends Entity> void register(EntityType<T> entityType, EntityRendererProvider<T> provider);
    }
    interface GuiLayerRegistrar {
        void register(Identifier id, ModGuiLayer layer);
    }
    interface ItemTintSourceRegistrar {
        void register(Identifier id, MapCodec<? extends ItemTintSource> mapCodec);
    }

    @FunctionalInterface
    interface ModGuiLayer {
        void render(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker);
    }
}
