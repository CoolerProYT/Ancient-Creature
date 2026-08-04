package com.coolerpromc.ancientcreature.platform.services;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.platform.util.BlockEntityTypeFactory;
import com.coolerpromc.ancientcreature.platform.util.CreativeTabOutput;
import com.coolerpromc.ancientcreature.platform.util.MenuFactory;
import com.coolerpromc.ancientcreature.platform.util.RegistryHandler;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public interface IRegistryHelper {
    default <T extends Block> RegistryHandler.Blocks<T> registerBlock(String name, Function<BlockBehaviour.Properties, T> func){
        return registerBlock(name, func, BlockBehaviour.Properties.of());
    }
    <T extends Block> RegistryHandler.Blocks<T> registerBlock(String name, Function<BlockBehaviour.Properties, T> func, BlockBehaviour.Properties p);
    default <T extends Item> RegistryHandler.Items<T> registerItem(String name, Function<Item.Properties, T> func){
        return registerItem(name, func, new Item.Properties());
    }
    <T extends Item> RegistryHandler.Items<T> registerItem(String name, Function<Item.Properties, T> func, Item.Properties p);
    <T extends BlockEntity> RegistryHandler<BlockEntityType<?>, BlockEntityType<T>> registerBlockEntityType(String name, BlockEntityTypeFactory<T> factory, List<Supplier<? extends Block>> blocks);
    <T extends Entity> RegistryHandler.Entities<T> registerEntity(String name, EntityType.EntityFactory<T> factory, MobCategory category, UnaryOperator<EntityType.Builder<T>> builder);
    RegistryHandler<CreativeModeTab, CreativeModeTab> registerCreativeTab(String name, Supplier<ItemStack> icon, Component title, BiConsumer<CreativeTabOutput, CreativeModeTab.ItemDisplayParameters> entries);
    <T extends Recipe<?>> RegistryHandler<RecipeType<?>, RecipeType<T>> registerRecipeType(String name);
    <T extends Recipe<?>> RegistryHandler<RecipeSerializer<?>, RecipeSerializer<T>> registerRecipeSerializer(String name, RecipeSerializer<T> serializer);
    <T extends AbstractContainerMenu> RegistryHandler<MenuType<?>, MenuType<T>> registerMenuType(String name, MenuFactory<T> factory);
    <T> RegistryHandler.Components<T> registerDataComponent(String name, UnaryOperator<DataComponentType.Builder<T>> builder);
    <T> RegistryHandler<EntityDataSerializer<?>, EntityDataSerializer<T>> registerEntityDataSerializer(String name, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec);
    RegistryHandler<Identifier, Identifier> registerStat(String name);
    RegistryHandler<Attribute, Attribute> registerAttribute(String name, Attribute attribute);
    <T extends Structure> RegistryHandler<StructureType<?>, StructureType<T>> registerStructureType(String name, MapCodec<T> mapCodec);
    <T extends LootItemFunction> RegistryHandler<MapCodec<? extends LootItemFunction>, MapCodec<T>> registerLootItemFunction(String name, MapCodec<T> mapCodec);
    RegistryHandler<SoundEvent, SoundEvent> registerSoundEvent(String name);
    <T extends FeatureConfiguration> RegistryHandler<Feature<?>, Feature<T>> registerFeature(String name, Feature<T> feature);

    void registerEntityAttribute(EntityType<? extends LivingEntity> entityType, AttributeSupplier supplier);
    void applyEntityAttributeRegistrations(EntityAttributeRegistrar registrar);

    void registerFeatureBiomeModifier(TagKey<Biome> biomeTagKey, GenerationStep.Decoration step, ResourceKey<PlacedFeature> placedFeatureKey);
    void applyBiomeModifierRegistrations(FeatureBiomeModifierRegistrar registrar);

    void registerBrewingRecipe(Item from, Item ingredient, Item to);
    void applyBrewingRecipeRegistrations(BrewingRecipeRegistrar registrar);

    interface EntityAttributeRegistrar {
        void register(EntityType<? extends LivingEntity> entityType, AttributeSupplier supplier);
    }
    interface FeatureBiomeModifierRegistrar {
        void register(TagKey<Biome> biomeTagKey, GenerationStep.Decoration step, ResourceKey<PlacedFeature> placedFeatureKey);
    }
    interface BrewingRecipeRegistrar {
        void register(Item from, Item ingredient, Item to);
    }

    static ResourceKey<Block> blockKey(String name) {
        return ResourceKey.create(Registries.BLOCK, Constants.id(name));
    }
    static ResourceKey<Item> itemKey(String name) {
        return ResourceKey.create(Registries.ITEM, Constants.id(name));
    }
    static ResourceKey<EntityType<?>> entityKey(String name) {
        return ResourceKey.create(Registries.ENTITY_TYPE, Constants.id(name));
    }
}
