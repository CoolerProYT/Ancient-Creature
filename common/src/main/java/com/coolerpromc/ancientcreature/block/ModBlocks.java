package com.coolerpromc.ancientcreature.block;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.block.custom.RockPileBlock;
import com.coolerpromc.ancientcreature.item.ModItems;
import com.coolerpromc.ancientcreature.platform.Services;
import com.coolerpromc.ancientcreature.platform.util.RegistryHandler;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Function;

public class ModBlocks {
    public static final RegistryHandler.Blocks<DropExperienceBlock> FOSSIL_ORE = registerBlock("fossil_ore", p -> new DropExperienceBlock(ConstantInt.of(0), p), BlockBehaviour.Properties.ofFullCopy(Blocks.DIAMOND_ORE));
    public static final RegistryHandler.Blocks<RockPileBlock> ROCK_PILE = registerBlock("rock_pile", RockPileBlock::new, BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(2.0F, 6.0F).noOcclusion());

    public static <T extends Block>RegistryHandler.Blocks<T> registerBlock(String name, Function<BlockBehaviour.Properties, T> func, BlockBehaviour.Properties properties){
        RegistryHandler.Blocks<T> handler = Services.REGISTRY.registerBlock(name, func, properties);
        ModItems.registerItem(name, p -> new BlockItem(handler.get(), p));
        return handler;
    }

    public static void init(){
        Constants.LOG.info("Registering blocks.");
    }
}
