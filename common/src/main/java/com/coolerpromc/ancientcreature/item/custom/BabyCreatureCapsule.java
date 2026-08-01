package com.coolerpromc.ancientcreature.item.custom;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class BabyCreatureCapsule extends Item {
    public BabyCreatureCapsule(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (level instanceof ServerLevel serverLevel){

        }
        return InteractionResult.SUCCESS;
    }
}
