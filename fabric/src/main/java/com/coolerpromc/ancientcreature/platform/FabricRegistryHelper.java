package com.coolerpromc.ancientcreature.platform;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.packs.resources.PreparableReloadListener;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.network.HandledCustomPacketPayload;
import com.coolerpromc.ancientcreature.platform.services.IRegistryHelper;
import com.coolerpromc.ancientcreature.platform.util.BlockEntityTypeFactory;
import com.coolerpromc.ancientcreature.platform.util.CreativeTabOutput;
import com.coolerpromc.ancientcreature.platform.util.MenuFactory;
import com.coolerpromc.ancientcreature.platform.util.RegistryHandler;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuType;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityDataRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class FabricRegistryHelper implements IRegistryHelper {
    private final Map<Identifier, PreparableReloadListener> serverReloadListeners = new LinkedHashMap<>();
    private final List<CommandBuilder> commands = new ArrayList<>();
    private final List<EntityAttributeEntry> entityAttributes = new ArrayList<>();
    private final List<FeatureBiomeModifierEntry> featureBiomeModifiers = new ArrayList<>();
    private final List<BrewingRecipeEntry> brewingRecipes = new ArrayList<>();
    private final List<DatapackRegistryEntry<?>> datapackRegistries = new ArrayList<>();
    private final List<ClientboundPayloadEntry<?>> clientboundPayloads = new ArrayList<>();

    @Override
    public <T extends Block> RegistryHandler.Blocks<T> registerBlock(String name, Function<BlockBehaviour.Properties, T> func, BlockBehaviour.Properties p) {
        ResourceKey<Block> key = IRegistryHelper.blockKey(name);
        Holder<Block> holder = Registry.registerForHolder(BuiltInRegistries.BLOCK, key, func.apply(p.setId(key)));

        return () -> holder;
    }

    @Override
    public <T extends Item> RegistryHandler.Items<T> registerItem(String name, Function<Item.Properties, T> func, Item.Properties p) {
        ResourceKey<Item> key = IRegistryHelper.itemKey(name);
        Holder<Item> holder = Registry.registerForHolder(BuiltInRegistries.ITEM, key, func.apply(p.setId(key)));

        return () -> holder;
    }

    @Override
    public final <T extends BlockEntity> RegistryHandler<BlockEntityType<?>, BlockEntityType<T>> registerBlockEntityType(String name, BlockEntityTypeFactory<T> factory, List<Supplier<? extends Block>> blocks) {
        Holder<BlockEntityType<?>> holder = Registry.registerForHolder(BuiltInRegistries.BLOCK_ENTITY_TYPE, Constants.id(name), FabricBlockEntityTypeBuilder.create(factory::create, blocks.stream().map(Supplier::get).toArray(Block[]::new)).build());
        return () -> holder;
    }

    @Override
    public <T extends Entity> RegistryHandler.Entities<T> registerEntity(String name, EntityType.EntityFactory<T> factory, MobCategory category, UnaryOperator<EntityType.Builder<T>> builder) {
        ResourceKey<EntityType<?>> key = IRegistryHelper.entityKey(name);
        Holder<EntityType<?>> holder = Registry.registerForHolder(BuiltInRegistries.ENTITY_TYPE, key, builder.apply(EntityType.Builder.of(factory, category)).build(key));

        return () -> holder;
    }

    @Override
    public RegistryHandler<CreativeModeTab, CreativeModeTab> registerCreativeTab(String name, Supplier<ItemStack> icon, Component title, BiConsumer<CreativeTabOutput, CreativeModeTab.ItemDisplayParameters> entries) {
        Holder<CreativeModeTab> holder = Registry.registerForHolder(BuiltInRegistries.CREATIVE_MODE_TAB, Constants.id(name), FabricCreativeModeTab.builder().icon(icon).title(title).displayItems((p, o) -> entries.accept(o::accept, p)).build());
        return () -> holder;
    }

    @Override
    public <T extends Recipe<?>> RegistryHandler<RecipeType<?>, RecipeType<T>> registerRecipeType(String name) {
        Identifier id = Constants.id(name);
        Holder<RecipeType<?>> holder = Registry.registerForHolder(BuiltInRegistries.RECIPE_TYPE, id, new RecipeType<>() {
            @Override
            public String toString() {
                return id.toString();
            }
        });

        return () -> holder;
    }

    @Override
    public <T extends Recipe<?>> RegistryHandler<RecipeSerializer<?>, RecipeSerializer<T>> registerRecipeSerializer(String name, RecipeSerializer<T> serializer) {
        Holder<RecipeSerializer<?>> holder = Registry.registerForHolder(BuiltInRegistries.RECIPE_SERIALIZER, Constants.id(name), serializer);
        return () -> holder;
    }

    @Override
    public <T extends AbstractContainerMenu> RegistryHandler<MenuType<?>, MenuType<T>> registerMenuType(String name, MenuFactory<T> factory) {
        Holder<MenuType<?>> holder = Registry.registerForHolder(BuiltInRegistries.MENU, Constants.id(name), new ExtendedMenuType<>(factory::create, BlockPos.STREAM_CODEC));
        return () -> holder;
    }

    @Override
    public <T> RegistryHandler.Components<T> registerDataComponent(String name, UnaryOperator<DataComponentType.Builder<T>> builder) {
        Holder<DataComponentType<?>> holder = Registry.registerForHolder(BuiltInRegistries.DATA_COMPONENT_TYPE, Constants.id(name), builder.apply(new DataComponentType.Builder<>()).build());
        return () -> holder;
    }

    @Override
    public <T> RegistryHandler<EntityDataSerializer<?>, EntityDataSerializer<T>> registerEntityDataSerializer(String name, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec) {
        Identifier id = Constants.id(name);
        EntityDataSerializer<T> serializer = EntityDataSerializer.forValueType(streamCodec);
        FabricEntityDataRegistry.register(id, serializer);

        return new RegistryHandler<>() {
            @Override
            public Holder<EntityDataSerializer<?>> holder() {
                return Holder.direct(serializer);
            }

            @Override
            public EntityDataSerializer<T> get() {
                return serializer;
            }
        };
    }

    @Override
    public RegistryHandler<Identifier, Identifier> registerStat(String name) {
        Identifier id = Constants.id(name);
        Holder<Identifier> holder = Registry.registerForHolder(BuiltInRegistries.CUSTOM_STAT, id, id);
        return () -> holder;
    }

    @Override
    public RegistryHandler<Attribute, Attribute> registerAttribute(String name, Attribute attribute) {
        Identifier id = Constants.id(name);
        Holder<Attribute> holder = Registry.registerForHolder(BuiltInRegistries.ATTRIBUTE, id, attribute);
        return () -> holder;
    }

    @Override
    public <T extends Structure> RegistryHandler<StructureType<?>, StructureType<T>> registerStructureType(String name, MapCodec<T> mapCodec) {
        Identifier id = Constants.id(name);
        StructureType<T> structureType = () -> mapCodec;
        Holder<StructureType<?>> holder = Registry.registerForHolder(BuiltInRegistries.STRUCTURE_TYPE, id, structureType);
        return () -> holder;
    }

    @Override
    public <T extends LootItemFunction> RegistryHandler<MapCodec<? extends LootItemFunction>, MapCodec<T>> registerLootItemFunction(String name, MapCodec<T> mapCodec) {
        Identifier id = Constants.id(name);
        Holder<MapCodec<? extends LootItemFunction>> holder = Registry.registerForHolder(BuiltInRegistries.LOOT_FUNCTION_TYPE, id, mapCodec);
        return () -> holder;
    }

    @Override
    public RegistryHandler<SoundEvent, SoundEvent> registerSoundEvent(String name) {
        Identifier id = Constants.id(name);
        Holder<SoundEvent> holder = Registry.registerForHolder(BuiltInRegistries.SOUND_EVENT, id, SoundEvent.createVariableRangeEvent(id));
        return () -> holder;
    }

    @Override
    public <T extends FeatureConfiguration> RegistryHandler<Feature<?>, Feature<T>> registerFeature(String name, Feature<T> feature) {
        Identifier id = Constants.id(name);
        Holder<Feature<?>> holder = Registry.registerForHolder(BuiltInRegistries.FEATURE, id, feature);
        return () -> holder;
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

    @Override
    public <T> void registerDatapackRegistry(ResourceKey<Registry<T>> key, Codec<T> serverCodec, Codec<T> clientCodec) {
        this.datapackRegistries.add(new DatapackRegistryEntry<>(key, serverCodec, clientCodec));
    }

    @Override
    public void applyDatapackRegistryRegistrations(DatapackRegistryRegistrar registrar) {
        for (DatapackRegistryEntry<?> entry : datapackRegistries) {
            entry.register(registrar);
        }
    }

    @Override
    public <T extends HandledCustomPacketPayload> void registerClientboundPayload(CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec) {
        this.clientboundPayloads.add(new ClientboundPayloadEntry<>(type, streamCodec));
    }

    @Override
    public void applyClientboundPayloadRegistrations(ClientboundPayloadRegistrar registrar) {
        for (ClientboundPayloadEntry<?> entry : clientboundPayloads) {
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

    private record DatapackRegistryEntry<T>(ResourceKey<Registry<T>> key, Codec<T> serverCodec, Codec<T> clientCodec){
        private void register(DatapackRegistryRegistrar registrar){
            registrar.register(this.key, this.serverCodec, this.clientCodec);
        }
    }

    private record ClientboundPayloadEntry<T extends HandledCustomPacketPayload>(CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec){
        private void register(ClientboundPayloadRegistrar registrar){
            registrar.register(this.type, this.streamCodec);
        }
    }

    /** Keyed by id, so collecting the same registration twice cannot produce a duplicate listener. */
    @Override
    public void registerServerReloadListener(Identifier id, PreparableReloadListener listener) {
        this.serverReloadListeners.put(id, listener);
    }

    @Override
    public void applyServerReloadListenerRegistrations(ReloadListenerRegistrar registrar) {
        this.serverReloadListeners.forEach(registrar::register);
    }

    @Override
    public void registerCommand(CommandBuilder builder) {
        this.commands.add(builder);
    }

    @Override
    public void applyCommandRegistrations(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
        for (CommandBuilder builder : commands) {
            builder.build(dispatcher, context);
        }
    }

}