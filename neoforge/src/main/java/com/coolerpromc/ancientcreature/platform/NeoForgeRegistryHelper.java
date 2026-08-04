package com.coolerpromc.ancientcreature.platform;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.platform.services.IRegistryHelper;
import com.coolerpromc.ancientcreature.platform.util.BlockEntityTypeFactory;
import com.coolerpromc.ancientcreature.platform.util.CreativeTabOutput;
import com.coolerpromc.ancientcreature.platform.util.MenuFactory;
import com.coolerpromc.ancientcreature.platform.util.RegistryHandler;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
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
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class NeoForgeRegistryHelper implements IRegistryHelper {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Constants.MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Constants.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Constants.MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(BuiltInRegistries.CREATIVE_MODE_TAB, Constants.MODID);
    public static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, Constants.MODID);
    public static final DeferredRegister.Entities ENTITIES = DeferredRegister.createEntities(Constants.MODID);
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, Constants.MODID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, Constants.MODID);
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, Constants.MODID);
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Constants.MODID);
    public static final DeferredRegister<EntityDataSerializer<?>> ENTITY_DATA_SERIALIZERS = DeferredRegister.create(NeoForgeRegistries.ENTITY_DATA_SERIALIZERS, Constants.MODID);
    public static final DeferredRegister<Identifier> STATS = DeferredRegister.create(BuiltInRegistries.CUSTOM_STAT, Constants.MODID);
    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(BuiltInRegistries.ATTRIBUTE, Constants.MODID);
    public static final DeferredRegister<StructureType<?>> STRUCTURE_TYPES = DeferredRegister.create(BuiltInRegistries.STRUCTURE_TYPE, Constants.MODID);
    public static final DeferredRegister<MapCodec<? extends LootItemFunction>> LOOT_FUNCTIONS = DeferredRegister.create(BuiltInRegistries.LOOT_FUNCTION_TYPE, Constants.MODID);
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, Constants.MODID);
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(BuiltInRegistries.FEATURE, Constants.MODID);

    private final List<EntityAttributeEntry> entityAttributes = new ArrayList<>();
    private final List<FeatureBiomeModifierEntry> featureBiomeModifiers = new ArrayList<>();
    private final List<BrewingRecipeEntry> brewingRecipes = new ArrayList<>();

    @Override
    public <T extends Block> RegistryHandler.Blocks<T> registerBlock(String name, Function<BlockBehaviour.Properties, T> func, BlockBehaviour.Properties p) {
        DeferredBlock<T> deferredBlock = BLOCKS.registerBlock(name, func, () -> p);
        return () -> deferredBlock;
    }

    @Override
    public <T extends Item> RegistryHandler.Items<T> registerItem(String name, Function<Item.Properties, T> func, Item.Properties p) {
        DeferredItem<T> deferredItem = ITEMS.registerItem(name, func, () -> p);
        return () -> deferredItem;
    }

    @Override
    public final <T extends BlockEntity> RegistryHandler<BlockEntityType<?>, BlockEntityType<T>> registerBlockEntityType(String name, BlockEntityTypeFactory<T> factory, List<Supplier<? extends Block>> blocks) {
        DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> deferredHolder = BLOCK_ENTITIES.register(name, () -> new BlockEntityType<>(factory::create, blocks.stream().map(Supplier::get).collect(Collectors.toSet())));
        return () -> deferredHolder;
    }

    @Override
    public <T extends Entity> RegistryHandler.Entities<T> registerEntity(String name, EntityType.EntityFactory<T> factory, MobCategory category, UnaryOperator<EntityType.Builder<T>> builder) {
        DeferredHolder<EntityType<?>, EntityType<T>> deferredHolder = ENTITIES.registerEntityType(name, factory, category, builder);
        return () -> deferredHolder;
    }

    @Override
    public RegistryHandler<CreativeModeTab, CreativeModeTab> registerCreativeTab(String name, Supplier<ItemStack> icon, Component title, BiConsumer<CreativeTabOutput, CreativeModeTab.ItemDisplayParameters> entries) {
        DeferredHolder<CreativeModeTab, CreativeModeTab> deferredHolder = CREATIVE_TABS.register(name, () -> CreativeModeTab.builder().icon(icon).title(title).displayItems((p, o) -> entries.accept(o::accept, p)).build());
        return () -> deferredHolder;
    }

    @Override
    public <T extends Recipe<?>> RegistryHandler<RecipeType<?>, RecipeType<T>> registerRecipeType(String name) {
        DeferredHolder<RecipeType<?>, RecipeType<T>> deferredHolder = RECIPE_TYPES.register(name, () -> RecipeType.simple(Constants.id(name)));
        return () -> deferredHolder;
    }

    @Override
    public <T extends Recipe<?>> RegistryHandler<RecipeSerializer<?>, RecipeSerializer<T>> registerRecipeSerializer(String name, RecipeSerializer<T> serializer) {
        DeferredHolder<RecipeSerializer<?>, RecipeSerializer<T>> deferredHolder = RECIPE_SERIALIZERS.register(name, () -> serializer);
        return () -> deferredHolder;
    }

    @Override
    public <T extends AbstractContainerMenu> RegistryHandler<MenuType<?>, MenuType<T>> registerMenuType(String name, MenuFactory<T> factory) {
        DeferredHolder<MenuType<?>, MenuType<T>> deferredHolder = MENUS.register(name, () -> IMenuTypeExtension.create((id, inv, buf) -> factory.create(id, inv, BlockPos.STREAM_CODEC.decode(buf))));
        return () -> deferredHolder;
    }

    @Override
    public <T> RegistryHandler.Components<T> registerDataComponent(String name, UnaryOperator<DataComponentType.Builder<T>> builder) {
        DeferredHolder<DataComponentType<?>, DataComponentType<T>> deferredHolder = DATA_COMPONENTS.registerComponentType(name, builder);
        return () -> deferredHolder;
    }

    @Override
    public <T> RegistryHandler<EntityDataSerializer<?>, EntityDataSerializer<T>> registerEntityDataSerializer(String name, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec) {
        DeferredHolder<EntityDataSerializer<?>, EntityDataSerializer<T>> deferredHolder = ENTITY_DATA_SERIALIZERS.register(name, () -> EntityDataSerializer.forValueType(streamCodec));
        return () -> deferredHolder;
    }

    @Override
    public RegistryHandler<Identifier, Identifier> registerStat(String name) {
        DeferredHolder<Identifier, Identifier> deferredHolder = STATS.register(name, () -> Constants.id(name));
        return () -> deferredHolder;
    }

    @Override
    public RegistryHandler<Attribute, Attribute> registerAttribute(String name, Attribute attribute) {
        DeferredHolder<Attribute, Attribute> deferredHolder = ATTRIBUTES.register(name, () -> attribute);
        return () -> deferredHolder;
    }

    @Override
    public <T extends Structure> RegistryHandler<StructureType<?>, StructureType<T>> registerStructureType(String name, MapCodec<T> mapCodec) {
        DeferredHolder<StructureType<?>, StructureType<T>> deferredHolder = STRUCTURE_TYPES.register(name, () -> () -> mapCodec);
        return () -> deferredHolder;
    }

    @Override
    public <T extends LootItemFunction> RegistryHandler<MapCodec<? extends LootItemFunction>, MapCodec<T>> registerLootItemFunction(String name, MapCodec<T> mapCodec) {
        DeferredHolder<MapCodec<? extends LootItemFunction>, MapCodec<T>> deferredHolder = LOOT_FUNCTIONS.register(name, () -> mapCodec);
        return () -> deferredHolder;
    }

    @Override
    public RegistryHandler<SoundEvent, SoundEvent> registerSoundEvent(String name) {
        DeferredHolder<SoundEvent, SoundEvent> deferredHolder = SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(Constants.id(name)));
        return () -> deferredHolder;
    }

    @Override
    public <T extends FeatureConfiguration> RegistryHandler<Feature<?>, Feature<T>> registerFeature(String name, Feature<T> feature) {
        DeferredHolder<Feature<?>, Feature<T>> deferredHolder = FEATURES.register(name, () -> feature);
        return () -> deferredHolder;
    }

    @Override
    public void registerEntityAttribute(EntityType<? extends LivingEntity> entityType, AttributeSupplier supplier) {
        this.entityAttributes.add(new EntityAttributeEntry(entityType, supplier));
    }

    @Override
    public void applyEntityAttributeRegistrations(EntityAttributeRegistrar registrar) {
        for (EntityAttributeEntry entry : this.entityAttributes) {
            entry.register(registrar);
        }
    }

    @Override
    public void registerFeatureBiomeModifier(TagKey<Biome> biomeTagKey, GenerationStep.Decoration step, ResourceKey<PlacedFeature> placedFeatureKey) {
        this.featureBiomeModifiers.add(new FeatureBiomeModifierEntry(biomeTagKey, step, placedFeatureKey));
    }

    @Override
    public void applyBiomeModifierRegistrations(FeatureBiomeModifierRegistrar registrar) {
        for (FeatureBiomeModifierEntry entry : featureBiomeModifiers) {
            entry.register(registrar);
        }
    }

    @Override
    public void registerBrewingRecipe(Item from, Item ingredient, Item to) {
        this.brewingRecipes.add(new BrewingRecipeEntry(from, ingredient, to));
    }

    @Override
    public void applyBrewingRecipeRegistrations(BrewingRecipeRegistrar registrar) {
        for (BrewingRecipeEntry entry : brewingRecipes) {
            entry.register(registrar);
        }
    }

    private record EntityAttributeEntry(EntityType<? extends LivingEntity> entityType, AttributeSupplier supplier) {
        private void register(EntityAttributeRegistrar registrar) {
            registrar.register(this.entityType, this.supplier);
        }
    }

    private record FeatureBiomeModifierEntry(TagKey<Biome> biomeTagKey, GenerationStep.Decoration step, ResourceKey<PlacedFeature> placedFeatureKey){
        private void register(FeatureBiomeModifierRegistrar registrar){
            registrar.register(this.biomeTagKey, this.step, this.placedFeatureKey);
        }
    }

    private record BrewingRecipeEntry(Item from, Item ingredient, Item to){
        private void register(BrewingRecipeRegistrar registrar){
            registrar.register(this.from, this.ingredient, this.to);
        }
    }

    public static void register(IEventBus eventBus){
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
        BLOCK_ENTITIES.register(eventBus);
        CREATIVE_TABS.register(eventBus);
        DATA_COMPONENTS.register(eventBus);
        ENTITIES.register(eventBus);
        RECIPE_TYPES.register(eventBus);
        RECIPE_SERIALIZERS.register(eventBus);
        MENUS.register(eventBus);
        ATTACHMENTS.register(eventBus);
        ENTITY_DATA_SERIALIZERS.register(eventBus);
        STATS.register(eventBus);
        ATTRIBUTES.register(eventBus);
        STRUCTURE_TYPES.register(eventBus);
        LOOT_FUNCTIONS.register(eventBus);
        SOUND_EVENTS.register(eventBus);
        FEATURES.register(eventBus);
    }
}