package com.coolerpromc.ancientcreature.platform.client;

import com.coolerpromc.ancientcreature.network.HandledCustomPacketPayload;
import com.coolerpromc.ancientcreature.platform.services.client.IRegistryHelper;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class NeoForgeRegistryHelper implements IRegistryHelper {
    private final List<EntityRendererEntry<?>> entityRenderers = new ArrayList<>();
    private final List<EntityModelLayerEntry> entityModelLayers = new ArrayList<>();
    private final List<GuiLayerEntry> guiLayers = new ArrayList<>();
    private final List<ItemTintSourceEntry> itemTintSources = new ArrayList<>();
    private final List<ItemConditionEntry> itemConditions = new ArrayList<>();
    private final List<ItemSelectEntry> itemSelects = new ArrayList<>();
    private final List<BlockEntityRendererEntry<?, ?>> blockEntityRenders = new ArrayList<>();
    private final List<MenuScreenEntry<?, ?>> menuScreens = new ArrayList<>();
    private final List<SpecialModelRendererEntry> specialModelRenderers = new ArrayList<>();
    private final List<ClientPayloadReceiverEntry<?>> clientPayloadReceivers = new ArrayList<>();


    @Override
    public <T extends Entity> void registerEntityRenderer(EntityType<T> entityType, EntityRendererProvider<T> provider) {
        this.entityRenderers.add(new EntityRendererEntry<>(entityType, provider));
    }

    @Override
    public void registerEntityModelLayer(ModelLayerLocation layer, Supplier<LayerDefinition> definition) {
        this.entityModelLayers.add(new EntityModelLayerEntry(layer, definition));
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
    public <T extends BlockEntity, S extends BlockEntityRenderState> void registerBlockEntityRenderer(BlockEntityType<? extends T> blockEntityType, BlockEntityRendererProvider<T, S> provider) {
        this.blockEntityRenders.add(new BlockEntityRendererEntry<>(blockEntityType, provider));
    }

    @Override
    public <M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>> void registerMenuScreen(MenuType<? extends M> menuType, MenuScreens.ScreenConstructor<M, U> screenConstructor) {
        this.menuScreens.add(new MenuScreenEntry<>(menuType, screenConstructor));
    }

    @Override
    public void registerSpecialModelRenderer(Identifier id, MapCodec<? extends SpecialModelRenderer.Unbaked<?>> mapCodec) {
        this.specialModelRenderers.add(new SpecialModelRendererEntry(id, mapCodec));
    }

    @Override
    public <T extends HandledCustomPacketPayload> void registerClientPayloadReceiver(CustomPacketPayload.Type<T> type) {
        this.clientPayloadReceivers.add(new ClientPayloadReceiverEntry<>(type));
    }

    @Override
    public void applyEntityRendererRegistrations(EntityRendererRegistrar registrar) {
        for (EntityRendererEntry<?> entry : this.entityRenderers) {
            entry.register(registrar);
        }
    }

    @Override
    public void applyEntityModelLayerRegistrations(EntityModelLayerRegistrar registrar) {
        for (EntityModelLayerEntry entry : this.entityModelLayers) {
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

    @Override
    public void applyBlockEntityRendererRegistrations(BlockEntityRendererRegistrar registrar) {
        for (BlockEntityRendererEntry<?, ?> entry : blockEntityRenders) {
            entry.register(registrar);
        }
    }

    @Override
    public void applyMenuScreenRegistrations(MenuScreenRegistrar registrar) {
        for (MenuScreenEntry<?, ?> entry : menuScreens) {
            entry.register(registrar);
        }
    }

    @Override
    public void applySpecialModelRendererRegistrations(SpecialModelRendererRegistrar registrar) {
        for (SpecialModelRendererEntry entry : specialModelRenderers) {
            entry.register(registrar);
        }
    }

    @Override
    public void applyClientPayloadReceiverRegistrations(ClientPayloadReceiverRegistrar registrar) {
        for (ClientPayloadReceiverEntry<?> entry : clientPayloadReceivers) {
            entry.register(registrar);
        }
    }

    private record EntityRendererEntry<T extends Entity>(EntityType<T> entityType, EntityRendererProvider<T> provider) {
        private void register(EntityRendererRegistrar registrar) {
            registrar.register(this.entityType, this.provider);
        }
    }

    private record EntityModelLayerEntry(ModelLayerLocation layer, Supplier<LayerDefinition> definition) {
        private void register(EntityModelLayerRegistrar registrar) {
            registrar.register(this.layer, this.definition);
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

    private record BlockEntityRendererEntry<T extends BlockEntity, S extends BlockEntityRenderState>(
        BlockEntityType<? extends T> blockEntityType, BlockEntityRendererProvider<T, S> provider) {
        private void register(BlockEntityRendererRegistrar registrar) {
            registrar.register(this.blockEntityType, this.provider);
        }
    }

    private record MenuScreenEntry<M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>> (
        MenuType<? extends M> menuType, MenuScreens.ScreenConstructor<M, @NonNull U> screenConstructor){
        private void register(MenuScreenRegistrar registrar){
            registrar.register(this.menuType, this.screenConstructor);
        }
    }

    private record SpecialModelRendererEntry(Identifier id, MapCodec<? extends SpecialModelRenderer.Unbaked<?>> mapCodec){
        private void register(SpecialModelRendererRegistrar registrar){
            registrar.register(this.id, this.mapCodec);
        }
    }

    private record ClientPayloadReceiverEntry<T extends HandledCustomPacketPayload>(CustomPacketPayload.Type<T> type){
        private void register(ClientPayloadReceiverRegistrar registrar){
            registrar.register(this.type);
        }
    }
}
