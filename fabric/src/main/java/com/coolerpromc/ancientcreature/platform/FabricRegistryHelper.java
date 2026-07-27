package com.coolerpromc.ancientcreature.platform;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.platform.services.IRegistryHelper;
import com.coolerpromc.ancientcreature.platform.util.BlockEntityTypeFactory;
import com.coolerpromc.ancientcreature.platform.util.CreativeTabOutput;
import com.coolerpromc.ancientcreature.platform.util.MenuFactory;
import com.coolerpromc.ancientcreature.platform.util.RegistryHandler;
import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuType;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityDataRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class FabricRegistryHelper implements IRegistryHelper {
    private final List<EntityAttributeEntry> entityAttributes = new ArrayList<>();

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

    @SafeVarargs
    @Override
    public final <T extends BlockEntity> RegistryHandler<BlockEntityType<?>, BlockEntityType<T>> registerBlockEntityType(String name, BlockEntityTypeFactory<T> factory, Supplier<? extends Block>... blocks) {
        Holder<BlockEntityType<?>> holder = Registry.registerForHolder(BuiltInRegistries.BLOCK_ENTITY_TYPE, Constants.id(name), FabricBlockEntityTypeBuilder.create(factory::create, Arrays.stream(blocks).map(Supplier::get).toArray(Block[]::new)).build());
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
    public <T extends AbstractContainerMenu, D> RegistryHandler<MenuType<?>, MenuType<T>> registerMenuType(String name, MenuFactory<T, D> factory, StreamCodec<? super RegistryFriendlyByteBuf, D> data) {
        Holder<MenuType<?>> holder = Registry.registerForHolder(BuiltInRegistries.MENU, Constants.id(name), new ExtendedMenuType<>(factory::create, data));
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
    public void registerEntityAttribute(EntityType<? extends LivingEntity> entityType, AttributeSupplier supplier) {
        this.entityAttributes.add(new EntityAttributeEntry(entityType, supplier));
    }

    @Override
    public void applyEntityAttributeRegistrations(EntityAttributeRegistrar registrar) {
        for (EntityAttributeEntry entry : this.entityAttributes) {
            entry.register(registrar);
        }
    }

    private record EntityAttributeEntry(EntityType<? extends LivingEntity> entityType, AttributeSupplier supplier) {
        private void register(EntityAttributeRegistrar registrar) {
            registrar.register(this.entityType, this.supplier);
        }
    }
}