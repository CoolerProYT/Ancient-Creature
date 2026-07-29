package com.coolerpromc.ancientcreature.platform.services.client;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.gui.GuiGraphicsExtractor;
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
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jspecify.annotations.NonNull;

import java.util.function.Supplier;

public interface IRegistryHelper {
    <T extends Entity> void registerEntityRenderer(EntityType<T> entityType, EntityRendererProvider<T> provider);
    void registerEntityModelLayer(ModelLayerLocation layer, Supplier<LayerDefinition> definition);
    void registerGuiLayer(Identifier id, ModGuiLayer layer);
    void registerItemTintSource(Identifier id, MapCodec<? extends ItemTintSource> mapCodec);
    void registerItemCondition(Identifier id, MapCodec<? extends ConditionalItemModelProperty> mapCodec);
    void registerItemSelect(Identifier id, SelectItemModelProperty.Type<?, ?> type);
    <T extends BlockEntity, S extends BlockEntityRenderState> void registerBlockEntityRenderer(BlockEntityType<? extends T> blockEntityType, BlockEntityRendererProvider<T, S> provider);
    <M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>> void registerMenuScreen(MenuType<? extends M> menuType, MenuScreens.ScreenConstructor<M, @NonNull U> screenConstructor);
    void registerSpecialModelRenderer(Identifier id, MapCodec<? extends SpecialModelRenderer.Unbaked<?>> mapCodec);

    void applyEntityRendererRegistrations(EntityRendererRegistrar registrar);
    void applyEntityModelLayerRegistrations(EntityModelLayerRegistrar registrar);
    void applyGuiLayerRegistrations(GuiLayerRegistrar registrar);
    void applyItemTintSourceRegistrations(ItemTintSourceRegistrar registrar);
    void applyItemConditionRegistrations(ItemConditionRegistrar registrar);
    void applyItemSelectRegistrations(ItemSelectRegistrar registrar);
    void applyBlockEntityRendererRegistrations(BlockEntityRendererRegistrar registrar);
    void applyMenuScreenRegistrations(MenuScreenRegistrar registrar);
    void applySpecialModelRendererRegistrations(SpecialModelRendererRegistrar registrar);

    interface EntityRendererRegistrar {
        <T extends Entity> void register(EntityType<T> entityType, EntityRendererProvider<T> provider);
    }
    interface EntityModelLayerRegistrar {
        void register(ModelLayerLocation layer, Supplier<LayerDefinition> definition);
    }
    interface GuiLayerRegistrar {
        void register(Identifier id, ModGuiLayer layer);
    }
    interface ItemTintSourceRegistrar {
        void register(Identifier id, MapCodec<? extends ItemTintSource> mapCodec);
    }
    interface ItemConditionRegistrar {
        void register(Identifier id, MapCodec<? extends ConditionalItemModelProperty> mapCodec);
    }
    interface ItemSelectRegistrar {
        void register(Identifier id, SelectItemModelProperty.Type<?, ?> type);
    }
    interface BlockEntityRendererRegistrar{
        <T extends BlockEntity, S extends BlockEntityRenderState> void register(BlockEntityType<? extends T> blockEntityType, BlockEntityRendererProvider<T, S> provider);
    }
    interface MenuScreenRegistrar{
        <M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>> void register(MenuType<? extends M> menuType, MenuScreens.ScreenConstructor<M, @NonNull U> screenConstructor);
    }
    interface SpecialModelRendererRegistrar{
        void register(Identifier id, MapCodec<? extends SpecialModelRenderer.Unbaked<?>> mapCodec);
    }

    @FunctionalInterface
    interface ModGuiLayer {
        void render(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker);
    }
}
