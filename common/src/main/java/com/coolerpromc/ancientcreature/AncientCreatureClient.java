package com.coolerpromc.ancientcreature;

import com.coolerpromc.ancientcreature.block.entity.ModBlockEntities;
import com.coolerpromc.ancientcreature.client.block.renderer.FossilCleaningTableBlockEntityRenderer;
import com.coolerpromc.ancientcreature.client.entity.model.FossilCleaningTableModel;
import com.coolerpromc.ancientcreature.client.entity.model.ModModelLayers;
import com.coolerpromc.ancientcreature.client.gui.screen.FossilCleaningTableScreen;
import com.coolerpromc.ancientcreature.client.item.FossilCleaningTableSpecialRenderer;
import com.coolerpromc.ancientcreature.client.model.condition.DirtyFossilFragmentCondition;
import com.coolerpromc.ancientcreature.client.model.select.FossilPartSelect;
import com.coolerpromc.ancientcreature.menu.ModMenus;
import com.coolerpromc.ancientcreature.platform.ServicesClient;
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
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jspecify.annotations.NonNull;

import java.util.function.Supplier;

public class AncientCreatureClient {
    public static void init(){

    }

    public static void initAll(){
        init();
        initRenderer();
        initModelLayer();
        initGuiLayer();
        initItemTintSource();
        initItemCondition();
        initItemSelect();
        initMenuScreen();
        initSpecialModelRenderer();
    }

    public static void initMenuScreen(){
        registerMenuScreen(ModMenus.FOSSIL_CLEANING_TABLE.get(), FossilCleaningTableScreen::new);
    }

    public static void initRenderer(){
        registerBlockEntityRenderer(ModBlockEntities.FOSSIL_CLEANING_TABLE.get(), FossilCleaningTableBlockEntityRenderer::new);
    }

    public static void initModelLayer(){
        registerEntityModelLayer(ModModelLayers.FOSSIL_CLEANING_TABLE, FossilCleaningTableModel::createBodyLayer);
    }

    public static void initGuiLayer(){

    }

    public static void initItemTintSource(){

    }

    public static void initItemCondition(){
        registerItemCondition(Constants.id("dirty_fossil_fragment"), DirtyFossilFragmentCondition.MAP_CODEC);
    }

    public static void initItemSelect(){
        registerItemSelect(Constants.id("fossil_part"), FossilPartSelect.TYPE);
    }

    public static void initSpecialModelRenderer(){
        registerSpecialModelRenderer(Constants.id("fossil_cleaning_table"), FossilCleaningTableSpecialRenderer.Unbaked.CODEC);
    }

    private static <T extends Entity> void registerEntityRenderer(EntityType<T> entityType, EntityRendererProvider<T> provider){
        ServicesClient.REGISTRY.registerEntityRenderer(entityType, provider);
    }

    private static void registerEntityModelLayer(ModelLayerLocation layer, Supplier<LayerDefinition> definition) {
        ServicesClient.REGISTRY.registerEntityModelLayer(layer, definition);
    }

    private static void registerGuiLayer(Identifier id, IRegistryHelper.ModGuiLayer layer){
        ServicesClient.REGISTRY.registerGuiLayer(id, layer);
    }

    private static void registerItemTintSource(Identifier id, MapCodec<? extends ItemTintSource> mapCodec){
        ServicesClient.REGISTRY.registerItemTintSource(id, mapCodec);
    }

    private static void registerItemCondition(Identifier id, MapCodec<? extends ConditionalItemModelProperty> mapCodec){
        ServicesClient.REGISTRY.registerItemCondition(id, mapCodec);
    }

    private static void registerItemSelect(Identifier id, SelectItemModelProperty.Type<?, ?> type){
        ServicesClient.REGISTRY.registerItemSelect(id, type);
    }

    private static <T extends BlockEntity, S extends BlockEntityRenderState> void registerBlockEntityRenderer(BlockEntityType<? extends T> blockEntityType, BlockEntityRendererProvider<T, S> provider){
        ServicesClient.REGISTRY.registerBlockEntityRenderer(blockEntityType, provider);
    }

    private static <M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>> void registerMenuScreen(MenuType<? extends M> menuType, MenuScreens.ScreenConstructor<M, @NonNull U> screenConstructor){
        ServicesClient.REGISTRY.registerMenuScreen(menuType, screenConstructor);
    }

    private static void registerSpecialModelRenderer(Identifier id, MapCodec<? extends SpecialModelRenderer.Unbaked<?>> mapCodec){
        ServicesClient.REGISTRY.registerSpecialModelRenderer(id, mapCodec);
    }
}