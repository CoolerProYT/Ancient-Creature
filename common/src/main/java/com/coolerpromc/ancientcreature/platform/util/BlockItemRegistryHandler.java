package com.coolerpromc.ancientcreature.platform.util;

import net.minecraft.core.Holder;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public record BlockItemRegistryHandler<B extends Block>(RegistryHandler.Blocks<B> block, RegistryHandler.Items<? extends BlockItem> item) {
    public Holder<Block> blockHolder(){
        return block.holder();
    }

    public Holder<Item> itemHolder(){
        return item.holder();
    }

    public B getBlock(){
        return block.get();
    }

    public BlockItem getItem(){
        return item.get();
    }

    public BlockState defaultBlockState(){
        return getBlock().defaultBlockState();
    }

    public ItemStack toStack(){
        return item.toStack();
    }
}
