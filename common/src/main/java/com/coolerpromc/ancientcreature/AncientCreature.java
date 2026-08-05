package com.coolerpromc.ancientcreature;

import com.coolerpromc.ancientcreature.block.ModBlocks;
import com.coolerpromc.ancientcreature.block.entity.ModBlockEntities;
import com.coolerpromc.ancientcreature.block.entity.custom.*;
import com.coolerpromc.ancientcreature.config.ModCommonConfig;
import com.coolerpromc.ancientcreature.creativetab.ModCreativeTabs;
import com.coolerpromc.ancientcreature.data.component.ModDataComponents;
import com.coolerpromc.ancientcreature.entity.ModEntities;
import com.coolerpromc.ancientcreature.entity.custom.Megalodon;
import com.coolerpromc.ancientcreature.entity.custom.Triceratops;
import com.coolerpromc.ancientcreature.entity.custom.TyrannosaurusRex;
import com.coolerpromc.ancientcreature.item.FossilPart;
import com.coolerpromc.ancientcreature.item.ModItems;
import com.coolerpromc.ancientcreature.loot.ModLootFunctions;
import com.coolerpromc.ancientcreature.menu.ModMenus;
import com.coolerpromc.ancientcreature.network.ClientboundIdentifiedSpeciesSyncPacket;
import com.coolerpromc.ancientcreature.network.HandledCustomPacketPayload;
import com.coolerpromc.ancientcreature.platform.Services;
import com.coolerpromc.ancientcreature.registry.ModRegistries;
import com.coolerpromc.ancientcreature.sound.ModSounds;
import com.coolerpromc.ancientcreature.worldgen.feature.ModFeatures;
import com.coolerpromc.ancientcreature.worldgen.feature.ModPlacedFeatures;
import com.coolerpromc.ancientcreature.worldgen.structure.ModStructureTypes;
import com.mojang.serialization.Codec;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.jspecify.annotations.Nullable;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class AncientCreature {
    public static void init() {
        ModCommonConfig.init();
        ModItems.init();
        ModBlocks.init();
        ModBlockEntities.init();
        ModDataComponents.init();
        ModCreativeTabs.init();
        ModEntities.init();
        ModMenus.init();
        ModStructureTypes.init();
        ModLootFunctions.init();
        ModSounds.init();
        ModFeatures.init();
    }

    public static void initCapability(){
        registerCapability(ModBlockEntities.FOSSIL_CLEANING_TABLE, FossilCleaningTableBlockEntity::getContainerBySide, FossilCleaningTableBlockEntity::getAllContainers);
        registerCapability(ModBlockEntities.FOSSIL_IDENTIFICATION_CHAMBER, FossilIdentificationChamberBlockEntity::getContainerBySide, FossilIdentificationChamberBlockEntity::getAllContainers);
        registerCapability(ModBlockEntities.DNA_EXTRACTOR, DNAExtractorBlockEntity::getContainerBySide, DNAExtractorBlockEntity::getAllContainers);
        registerCapability(ModBlockEntities.GENOME_SEQUENCER, GenomeSequencerBlockEntity::getContainerBySide, GenomeSequencerBlockEntity::getAllContainers);
        registerCapability(ModBlockEntities.EMBRYOGENESIS_CHAMBER, EmbryogenesisChamberBlockEntity::getContainerBySide, EmbryogenesisChamberBlockEntity::getAllContainers);
        registerCapability(ModBlockEntities.INCUBATOR, IncubatorBlockEntity::getContainerBySide, IncubatorBlockEntity::getAllContainers);
        registerCapability(ModBlockEntities.PLACEHOLDER, PlaceholderBlockEntity::getContainerBySide, PlaceholderBlockEntity::getAllContainers);
    }

    public static void initEntityAttribute(){
        registerEntityAttribute(ModEntities.TRICERATOPS.get(), Triceratops.createAttributes().build());
        registerEntityAttribute(ModEntities.TYRANNOSAURUS_REX.get(), TyrannosaurusRex.createAttributes().build());
        registerEntityAttribute(ModEntities.MEGALODON.get(), Megalodon.createAttributes().build());
    }

    public static void initBiomeModifier(){
        registerBiomeModifier(BiomeTags.IS_OVERWORLD, GenerationStep.Decoration.VEGETAL_DECORATION, ModPlacedFeatures.FOREST_ROCKS);
        registerBiomeModifier(BiomeTags.IS_OVERWORLD, GenerationStep.Decoration.UNDERGROUND_ORES, ModPlacedFeatures.FOSSIL_ORE);
    }

    public static void initBrewingRecipe(){

    }

    public static void initDatapackRegistry(){
        registerDatapackRegistry(ModRegistries.FOSSIL_PART, FossilPart.DIRECT_CODEC, FossilPart.DIRECT_CODEC);
    }

    public static void initPayloadType(){
        registerClientboundPayload(ClientboundIdentifiedSpeciesSyncPacket.TYPE, ClientboundIdentifiedSpeciesSyncPacket.STREAM_CODEC);
    }

    private static <T extends BlockEntity> void registerCapability(Supplier<BlockEntityType<T>> type, BiFunction<T, @Nullable Direction, Container> provider, Function<T, Container[]> containers){
        Services.CAPABILITIES.registerBlockEntityItemStorage(type, provider, containers);
    }

    private static void registerEntityAttribute(EntityType<? extends LivingEntity> entityType, AttributeSupplier supplier){
        Services.REGISTRY.registerEntityAttribute(entityType, supplier);
    }

    private static void registerBiomeModifier(TagKey<Biome> biomeTagKey, GenerationStep.Decoration step, ResourceKey<PlacedFeature> placedFeatureKey){
        Services.REGISTRY.registerFeatureBiomeModifier(biomeTagKey, step, placedFeatureKey);
    }

    private static void registerBrewingRecipe(Item from, Item ingredient, Item to){
        Services.REGISTRY.registerBrewingRecipe(from, ingredient, to);
    }

    private static <T> void registerDatapackRegistry(ResourceKey<Registry<T>> resourceKey, Codec<T> serverCodec, Codec<T> clientCodec){
        Services.REGISTRY.registerDatapackRegistry(resourceKey, serverCodec, clientCodec);
    }

    private static <T extends HandledCustomPacketPayload> void registerClientboundPayload(CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec){
        Services.REGISTRY.registerClientboundPayload(type, streamCodec);
    }
}
