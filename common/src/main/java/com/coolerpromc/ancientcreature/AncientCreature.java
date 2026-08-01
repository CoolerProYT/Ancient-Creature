package com.coolerpromc.ancientcreature;

import com.coolerpromc.ancientcreature.block.ModBlocks;
import com.coolerpromc.ancientcreature.block.entity.ModBlockEntities;
import com.coolerpromc.ancientcreature.block.entity.custom.*;
import com.coolerpromc.ancientcreature.data.component.ModDataComponents;
import com.coolerpromc.ancientcreature.entity.ModEntities;
import com.coolerpromc.ancientcreature.creativetab.ModCreativeTabs;
import com.coolerpromc.ancientcreature.item.ModItems;
import com.coolerpromc.ancientcreature.loot.ModLootFunctions;
import com.coolerpromc.ancientcreature.menu.ModMenus;
import com.coolerpromc.ancientcreature.platform.Services;
import com.coolerpromc.ancientcreature.sound.ModSounds;
import com.coolerpromc.ancientcreature.worldgen.feature.ModPlacedFeatures;
import com.coolerpromc.ancientcreature.worldgen.structure.ModStructureTypes;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.jspecify.annotations.Nullable;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public class AncientCreature {
    public static void init() {
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
    }

    public static void initCapability(){
        registerCapability(ModBlockEntities.FOSSIL_CLEANING_TABLE, FossilCleaningTableBlockEntity::getContainerBySide);
        registerCapability(ModBlockEntities.FOSSIL_IDENTIFICATION_CHAMBER, FossilIdentificationChamberBlockEntity::getContainerBySide);
        registerCapability(ModBlockEntities.DNA_EXTRACTOR, DNAExtractorBlockEntity::getContainerBySide);
        registerCapability(ModBlockEntities.GENOME_SEQUENCER, GenomeSequencerBlockEntity::getContainerBySide);
        registerCapability(ModBlockEntities.EMBRYOGENESIS_CHAMBER, EmbryogenesisChamberBlockEntity::getContainerBySide);
        registerCapability(ModBlockEntities.INCUBATOR, IncubatorBlockEntity::getContainerBySide);
        registerCapability(ModBlockEntities.PLACEHOLDER, PlaceholderBlockEntity::getContainerBySide);
    }

    public static void initEntityAttribute(){

    }

    public static void initBiomeModifier(){
        registerBiomeModifier(BiomeTags.IS_FOREST, GenerationStep.Decoration.VEGETAL_DECORATION, ModPlacedFeatures.FOREST_ROCKS);
        registerBiomeModifier(BiomeTags.IS_OVERWORLD, GenerationStep.Decoration.UNDERGROUND_ORES, ModPlacedFeatures.FOSSIL_ORE);
    }

    private static <T extends BlockEntity> void registerCapability(Supplier<BlockEntityType<T>> type, BiFunction<T, @Nullable Direction, Container> provider){
        Services.CAPABILITIES.registerBlockEntityItemStorage(type, provider);
    }

    private static void registerEntityAttribute(EntityType<? extends LivingEntity> entityType, AttributeSupplier supplier){
        Services.REGISTRY.registerEntityAttribute(entityType, supplier);
    }

    private static void registerBiomeModifier(TagKey<Biome> biomeTagKey, GenerationStep.Decoration step, ResourceKey<PlacedFeature> placedFeatureKey){
        Services.REGISTRY.registerFeatureBiomeModifier(biomeTagKey, step, placedFeatureKey);
    }
}
