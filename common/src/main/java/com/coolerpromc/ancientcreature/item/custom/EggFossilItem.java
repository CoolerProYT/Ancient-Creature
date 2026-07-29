package com.coolerpromc.ancientcreature.item.custom;

import com.coolerpromc.ancientcreature.block.ModBlocks;
import com.coolerpromc.ancientcreature.block.custom.RockPileBlock;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class EggFossilItem extends Item {
    public EggFossilItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockState state = level.getBlockState(context.getClickedPos());
        Player player = context.getPlayer();
        if (state.is(ModBlocks.ROCK_PILE.blockHolder()) && !state.getValue(RockPileBlock.HAS_EGG) && player != null){
            level.setBlockAndUpdate(context.getClickedPos(), state.setValue(RockPileBlock.HAS_EGG, true));
            if (!player.isCreative()){
                player.getMainHandItem().shrink(1);
            }
            return InteractionResult.SUCCESS;
        }
        return super.useOn(context);
    }
}
