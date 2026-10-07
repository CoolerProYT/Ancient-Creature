package com.coolerpromc.ancientcreature.block;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.block.custom.*;
import com.coolerpromc.ancientcreature.item.ModItems;
import com.coolerpromc.ancientcreature.platform.Services;
import com.coolerpromc.ancientcreature.platform.util.RegistryHandler;
import com.coolerpromc.ancientcreature.platform.util.BlockItemRegistryHandler;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.MapColor;

import java.util.function.Function;

public class ModBlocks {
    public static final BlockItemRegistryHandler<PlaceholderBlock> PLACEHOLDER = registerBlock("placeholder", PlaceholderBlock::new, BlockBehaviour.Properties.of().noOcclusion().noLootTable());
    public static final BlockItemRegistryHandler<DropExperienceBlock> FOSSIL_ORE = registerBlock("fossil_ore", p -> new DropExperienceBlock(ConstantInt.of(0), p), BlockBehaviour.Properties.ofFullCopy(Blocks.DIAMOND_ORE));
    public static final BlockItemRegistryHandler<DropExperienceBlock> DEEPSLATE_FOSSIL_ORE = registerBlock("deepslate_fossil_ore", p -> new DropExperienceBlock(ConstantInt.of(0), p), BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE_DIAMOND_ORE));
    public static final BlockItemRegistryHandler<DropExperienceBlock> AMBER_ORE = registerBlock("amber_ore", p -> new DropExperienceBlock(ConstantInt.of(0), p), BlockBehaviour.Properties.ofFullCopy(Blocks.DIAMOND_ORE).mapColor(MapColor.COLOR_ORANGE));
    public static final BlockItemRegistryHandler<Block> FROZEN_FOSSIL = registerBlock("frozen_fossil", Block::new, BlockBehaviour.Properties.ofFullCopy(Blocks.PACKED_ICE).strength(2.0F, 3.0F).requiresCorrectToolForDrops());
    public static final BlockItemRegistryHandler<SifterBlock> SIFTER = registerBlock("sifter", SifterBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(1.5F).sound(SoundType.WOOD).noOcclusion().ignitedByLava());
    public static final BlockItemRegistryHandler<RockPileBlock> ROCK_PILE = registerBlock("rock_pile", RockPileBlock::new, BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(2.0F, 6.0F).noOcclusion());
    public static final BlockItemRegistryHandler<FossilCleaningTableBlock> FOSSIL_CLEANING_TABLE = registerBlock("fossil_cleaning_table", FossilCleaningTableBlock::new, BlockBehaviour.Properties.of().strength(2.0F, 6.0F).noOcclusion());
    public static final BlockItemRegistryHandler<FossilIdentificationChamberBlock> FOSSIL_IDENTIFICATION_CHAMBER = registerBlock("fossil_identification_chamber", FossilIdentificationChamberBlock::new, BlockBehaviour.Properties.of().strength(2.0F, 6.0F).noOcclusion());
    public static final BlockItemRegistryHandler<DNAExtractorBlock> DNA_EXTRACTOR = registerBlock("dna_extractor", DNAExtractorBlock::new, BlockBehaviour.Properties.of().strength(2.0F, 6.0F).noOcclusion());
    public static final BlockItemRegistryHandler<GenomeSequencerBlock> GENOME_SEQUENCER = registerBlock("genome_sequencer", GenomeSequencerBlock::new, BlockBehaviour.Properties.of().strength(2.0F, 6.0F).noOcclusion());
    public static final BlockItemRegistryHandler<EmbryogenesisChamberBlock> EMBRYOGENESIS_CHAMBER = registerBlock("embryogenesis_chamber", EmbryogenesisChamberBlock::new, BlockBehaviour.Properties.of().strength(2.0F, 6.0F).noOcclusion());
    public static final BlockItemRegistryHandler<IncubatorBlock> INCUBATOR = registerBlock("incubator", IncubatorBlock::new, BlockBehaviour.Properties.of().strength(2.0F, 6.0F).noOcclusion());
    public static final BlockItemRegistryHandler<EggBlock> EGG = registerBlock("egg", EggBlock::new, BlockBehaviour.Properties.of().strength(2.0F, 6.0F).noOcclusion().noLootTable());

    // Containment: steel that charging or distressed creatures cannot smash.
    public static final WoodType REINFORCED = new WoodType("ancientcreature_reinforced", BlockSetType.IRON, SoundType.METAL, SoundType.METAL,
        SoundEvents.IRON_DOOR_CLOSE, SoundEvents.IRON_DOOR_OPEN);
    public static final BlockItemRegistryHandler<FenceBlock> REINFORCED_FENCE = registerBlock("reinforced_fence", FenceBlock::new,
        BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(6.0F, 12.0F).requiresCorrectToolForDrops().sound(SoundType.METAL));
    public static final BlockItemRegistryHandler<FenceGateBlock> REINFORCED_FENCE_GATE = registerBlock("reinforced_fence_gate", p -> new FenceGateBlock(REINFORCED, p),
        BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(6.0F, 12.0F).requiresCorrectToolForDrops().sound(SoundType.METAL).forceSolidOn());
    public static final BlockItemRegistryHandler<ElectricFenceBlock> ELECTRIC_FENCE = registerBlock("electric_fence", ElectricFenceBlock::new,
        BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(4.0F, 8.0F).requiresCorrectToolForDrops().sound(SoundType.METAL).noOcclusion());

    public static <B extends Block> BlockItemRegistryHandler<B> registerBlock(String name, Function<BlockBehaviour.Properties, B> func, BlockBehaviour.Properties properties){
        RegistryHandler.Blocks<B> block = Services.REGISTRY.registerBlock(name, func, properties);
        RegistryHandler.Items<BlockItem> item = ModItems.registerItem(name, p -> new BlockItem(block.get(), p.useBlockDescriptionPrefix()));
        return new BlockItemRegistryHandler<>(block, item);
    }

    public static void init(){
        Constants.LOG.info("Registering blocks.");
    }
}
