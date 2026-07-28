package com.coolerpromc.ancientcreature.platform.client;

import com.coolerpromc.ancientcreature.platform.services.client.IRegistryHelper;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

public class NeoForgeRegistryHelper implements IRegistryHelper {
    private final List<EntityRendererEntry<?>> entityRenderers = new ArrayList<>();
    private final List<GuiLayerEntry> guiLayers = new ArrayList<>();
    private final List<ItemTintSourceEntry> itemTintSources = new ArrayList<>();
    private final List<ItemConditionEntry> itemConditions = new ArrayList<>();
    private final List<ItemSelectEntry> itemSelects = new ArrayList<>();

    @Override
    public <T extends Entity> void registerEntityRenderer(EntityType<T> entityType, EntityRendererProvider<T> provider) {
        this.entityRenderers.add(new EntityRendererEntry<>(entityType, provider));
    }

    @Override
    public void registerGuiLayer(Identifier id, ModGuiLayer layer) {
        this.guiLayers.add(new GuiLayerEntry(id, layer));
    }

    @Override
    public void registerItemTintSource(Identifier id, MapCodec<? extends ItemTintSource> mapCodec) {
        this.itemTintSources.add(new ItemTintSourceEntry(id, mapCodec));
    }

    @Override
    public void registerItemCondition(Identifier id, MapCodec<? extends ConditionalItemModelProperty> mapCodec) {
        this.itemConditions.add(new ItemConditionEntry(id, mapCodec));
    }

    @Override
    public void registerItemSelect(Identifier id, SelectItemModelProperty.Type<?, ?> type) {
        this.itemSelects.add(new ItemSelectEntry(id, type));
    }

    @Override
    public void applyEntityRendererRegistrations(EntityRendererRegistrar registrar) {
        for (EntityRendererEntry<?> entry : this.entityRenderers) {
            entry.register(registrar);
        }
    }

    @Override
    public void applyGuiLayerRegistrations(GuiLayerRegistrar registrar) {
        for (GuiLayerEntry entry : this.guiLayers) {
            entry.register(registrar);
        }
    }

    @Override
    public void applyItemTintSourceRegistrations(ItemTintSourceRegistrar registrar) {
        for (ItemTintSourceEntry entry : itemTintSources) {
            entry.register(registrar);
        }
    }

    @Override
    public void applyItemConditionRegistrations(ItemConditionRegistrar registrar) {
        for (ItemConditionEntry entry : itemConditions) {
            entry.register(registrar);
        }
    }

    @Override
    public void applyItemSelectRegistrations(ItemSelectRegistrar registrar) {
        for (ItemSelectEntry entry : itemSelects) {
            entry.register(registrar);
        }
    }

    private record EntityRendererEntry<T extends Entity>(EntityType<T> entityType, EntityRendererProvider<T> provider) {
        private void register(EntityRendererRegistrar registrar) {
            registrar.register(this.entityType, this.provider);
        }
    }

    private record GuiLayerEntry(Identifier id, ModGuiLayer layer) {
        private void register(GuiLayerRegistrar registrar) {
            registrar.register(this.id, this.layer);
        }
    }

    private record ItemTintSourceEntry(Identifier id, MapCodec<? extends ItemTintSource> mapCodec) {
        private void register(ItemTintSourceRegistrar registrar) {
            registrar.register(this.id, this.mapCodec);
        }
    }

    private record ItemConditionEntry(Identifier id, MapCodec<? extends ConditionalItemModelProperty> mapCodec) {
        private void register(ItemConditionRegistrar registrar) {
            registrar.register(this.id, this.mapCodec);
        }
    }

    private record ItemSelectEntry(Identifier id, SelectItemModelProperty.Type<?, ?> type){
        private void register(ItemSelectRegistrar registrar) {
            registrar.register(this.id, this.type);
        }
    }
}
