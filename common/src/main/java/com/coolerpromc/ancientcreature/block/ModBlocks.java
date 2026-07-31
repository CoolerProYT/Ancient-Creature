package com.coolerpromc.ancientcreature.block;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.block.custom.FossilCleaningTableBlock;
import com.coolerpromc.ancientcreature.block.custom.FossilIdentificationChamberBlock;
import com.coolerpromc.ancientcreature.block.custom.PlaceholderBlock;
import com.coolerpromc.ancientcreature.block.custom.RockPileBlock;
import com.coolerpromc.ancientcreature.item.ModItems;
import com.coolerpromc.ancientcreature.platform.Services;
import com.coolerpromc.ancientcreature.platform.util.RegistryHandler;
import com.coolerpromc.ancientcreature.platform.util.BlockItemRegistryHandler;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Function;

public class ModBlocks {
    public static final BlockItemRegistryHandler<PlaceholderBlock> PLACEHOLDER = registerBlock("placeholder", PlaceholderBlock::new, BlockBehaviour.Properties.of().noOcclusion().noLootTable());
    public static final BlockItemRegistryHandler<DropExperienceBlock> FOSSIL_ORE = registerBlock("fossil_ore", p -> new DropExperienceBlock(ConstantInt.of(0), p), BlockBehaviour.Properties.ofFullCopy(Blocks.DIAMOND_ORE));
    public static final BlockItemRegistryHandler<RockPileBlock> ROCK_PILE = registerBlock("rock_pile", RockPileBlock::new, BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(2.0F, 6.0F).noOcclusion());
    public static final BlockItemRegistryHandler<FossilCleaningTableBlock> FOSSIL_CLEANING_TABLE = registerBlock("fossil_cleaning_table", FossilCleaningTableBlock::new, BlockBehaviour.Properties.of().strength(2.0F, 6.0F).noOcclusion());
    public static final BlockItemRegistryHandler<FossilIdentificationChamberBlock> FOSSIL_IDENTIFICATION_CHAMBER = registerBlock("fossil_identification_chamber", FossilIdentificationChamberBlock::new, BlockBehaviour.Properties.of().strength(2.0F, 6.0F).noOcclusion());

    public static <B extends Block> BlockItemRegistryHandler<B> registerBlock(String name, Function<BlockBehaviour.Properties, B> func, BlockBehaviour.Properties properties){
        RegistryHandler.Blocks<B> block = Services.REGISTRY.registerBlock(name, func, properties);
        RegistryHandler.Items<BlockItem> item = ModItems.registerItem(name, p -> new BlockItem(block.get(), p.useBlockDescriptionPrefix()));
        return new BlockItemRegistryHandler<>(block, item);
    }

    public static void init(){
        Constants.LOG.info("Registering blocks.");
    }
}
