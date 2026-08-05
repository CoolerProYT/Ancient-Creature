package com.coolerpromc.ancientcreature;

import com.coolerpromc.ancientcreature.block.entity.ModBlockEntities;
import com.coolerpromc.ancientcreature.client.block.renderer.*;
import com.coolerpromc.ancientcreature.client.entity.model.MegalodonModel;
import com.coolerpromc.ancientcreature.client.entity.model.ModModelLayers;
import com.coolerpromc.ancientcreature.client.entity.model.TriceratopsModel;
import com.coolerpromc.ancientcreature.client.entity.model.TyrannosaurusRexModel;
import com.coolerpromc.ancientcreature.client.entity.model.block.*;
import com.coolerpromc.ancientcreature.client.entity.renderer.MegalodonRenderer;
import com.coolerpromc.ancientcreature.client.entity.renderer.TriceratopsRenderer;
import com.coolerpromc.ancientcreature.client.entity.renderer.TyrannosaurusRexRenderer;
import com.coolerpromc.ancientcreature.client.gui.screen.*;
import com.coolerpromc.ancientcreature.client.item.condition.DirtyCondition;
import com.coolerpromc.ancientcreature.client.item.select.DNAIntegritySelect;
import com.coolerpromc.ancientcreature.client.item.select.FossilPartSelect;
import com.coolerpromc.ancientcreature.client.item.special.*;
import com.coolerpromc.ancientcreature.entity.ModEntities;
import com.coolerpromc.ancientcreature.menu.ModMenus;
import com.coolerpromc.ancientcreature.network.ClientboundIdentifiedSpeciesSyncPacket;
import com.coolerpromc.ancientcreature.network.HandledCustomPacketPayload;
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
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
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
        initClientPayloadHandler();
    }

    public static void initMenuScreen(){
        registerMenuScreen(ModMenus.FOSSIL_CLEANING_TABLE.get(), FossilCleaningTableScreen::new);
        registerMenuScreen(ModMenus.FOSSIL_IDENTIFICATION_CHAMBER.get(), FossilIdentificationChamberScreen::new);
        registerMenuScreen(ModMenus.DNA_EXTRACTOR.get(), DNAExtractorScreen::new);
        registerMenuScreen(ModMenus.GENOME_SEQUENCER.get(), GenomeSequenceScreen::new);
        registerMenuScreen(ModMenus.EMBRYOGENESIS_CHAMBER.get(), EmbryogenesisChamberScreen::new);
        registerMenuScreen(ModMenus.INCUBATOR.get(), IncubatorScreen::new);
    }

    public static void initRenderer(){
        registerEntityRenderer(ModEntities.TRICERATOPS.get(), TriceratopsRenderer::new);
        registerEntityRenderer(ModEntities.TYRANNOSAURUS_REX.get(), TyrannosaurusRexRenderer::new);
        registerEntityRenderer(ModEntities.MEGALODON.get(), MegalodonRenderer::new);
        registerBlockEntityRenderer(ModBlockEntities.FOSSIL_CLEANING_TABLE.get(), FossilCleaningTableBlockEntityRenderer::new);
        registerBlockEntityRenderer(ModBlockEntities.FOSSIL_IDENTIFICATION_CHAMBER.get(), FossilIdentificationChamberBlockEntityRenderer::new);
        registerBlockEntityRenderer(ModBlockEntities.DNA_EXTRACTOR.get(), DNAExtractorBlockEntityRenderer::new);
        registerBlockEntityRenderer(ModBlockEntities.GENOME_SEQUENCER.get(), GenomeSequencerBlockEntityRenderer::new);
        registerBlockEntityRenderer(ModBlockEntities.EMBRYOGENESIS_CHAMBER.get(), EmbryogenesisChamberBlockEntityRenderer::new);
        registerBlockEntityRenderer(ModBlockEntities.INCUBATOR.get(), IncubatorBlockEntityRenderer::new);
    }

    public static void initModelLayer(){
        registerEntityModelLayer(ModModelLayers.TRICERATOPS, TriceratopsModel::createBodyLayer);
        registerEntityModelLayer(ModModelLayers.TYRANNOSAURUS_REX, TyrannosaurusRexModel::createBodyLayer);
        registerEntityModelLayer(ModModelLayers.MEGALODON, MegalodonModel::createBodyLayer);
        registerEntityModelLayer(ModModelLayers.FOSSIL_CLEANING_TABLE, FossilCleaningTableModel::createBodyLayer);
        registerEntityModelLayer(ModModelLayers.FOSSIL_IDENTIFYING_CHAMBER, FossilIdentificationChamberModel::createBodyLayer);
        registerEntityModelLayer(ModModelLayers.DNA_EXTRACTOR, DNAExtractorModel::createBodyLayer);
        registerEntityModelLayer(ModModelLayers.GENOME_SEQUENCER, GenomeSequencerModel::createBodyLayer);
        registerEntityModelLayer(ModModelLayers.EMBRYOGENESIS_CHAMBER, EmbryogenesisChamberModel::createBodyLayer);
        registerEntityModelLayer(ModModelLayers.INCUBATOR, IncubatorModel::createBodyLayer);
    }

    public static void initGuiLayer(){

    }

    public static void initItemTintSource(){

    }

    public static void initItemCondition(){
        registerItemCondition(Constants.id("dirty"), DirtyCondition.MAP_CODEC);
    }

    public static void initItemSelect(){
        registerItemSelect(Constants.id("fossil_part"), FossilPartSelect.TYPE);
        registerItemSelect(Constants.id("dna_integrity"), DNAIntegritySelect.TYPE);
    }

    public static void initSpecialModelRenderer(){
        registerSpecialModelRenderer(Constants.id("fossil_cleaning_table"), FossilCleaningTableSpecialRenderer.Unbaked.CODEC);
        registerSpecialModelRenderer(Constants.id("fossil_identification_chamber"), FossilIdentificationChamberSpecialRenderer.Unbaked.CODEC);
        registerSpecialModelRenderer(Constants.id("dna_extractor"), DNAExtractorSpecialRenderer.Unbaked.CODEC);
        registerSpecialModelRenderer(Constants.id("genome_sequencer"), GenomeSequencerSpecialRenderer.Unbaked.CODEC);
        registerSpecialModelRenderer(Constants.id("embryogenesis_chamber"), EmbryogenesisChamberSpecialRenderer.Unbaked.CODEC);
        registerSpecialModelRenderer(Constants.id("incubator"), IncubatorSpecialRenderer.Unbaked.CODEC);
    }

    public static void initClientPayloadHandler(){
        registerClientPayloadReceiver(ClientboundIdentifiedSpeciesSyncPacket.TYPE);
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

    private static <T extends HandledCustomPacketPayload> void registerClientPayloadReceiver(CustomPacketPayload.Type<T> type){
        ServicesClient.REGISTRY.registerClientPayloadReceiver(type);
    }
}
