package com.coolerpromc.ancientcreature;

import com.coolerpromc.ancientcreature.client.model.condition.DirtyFossilFragmentCondition;
import com.coolerpromc.ancientcreature.client.model.select.FossilPartSelect;
import com.coolerpromc.ancientcreature.platform.ServicesClient;
import com.coolerpromc.ancientcreature.platform.services.client.IRegistryHelper;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

public class AncientCreatureClient {
    public static void init(){

    }

    public static void initAll(){
        init();
        initRenderer();
        initGuiLayer();
        initItemTintSource();
        initItemCondition();
        initItemSelect();
    }

    public static void initRenderer(){

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

    private static <T extends Entity> void registerEntityRenderer(EntityType<T> entityType, EntityRendererProvider<T> provider){
        ServicesClient.REGISTRY.registerEntityRenderer(entityType, provider);
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
}