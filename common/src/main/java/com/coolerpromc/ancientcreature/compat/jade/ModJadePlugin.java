package com.coolerpromc.ancientcreature.compat.jade;

import com.coolerpromc.ancientcreature.block.entity.custom.PlaceholderBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class ModJadePlugin implements IWailaPlugin {
    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.addRayTraceCallback((hitResult, accessor, originalAccessor) -> {

            if (accessor instanceof BlockAccessor blockAccessor && blockAccessor.getBlockEntity() instanceof PlaceholderBlockEntity blockEntity){
                Level level = accessor.getLevel();
                BlockState state = level.getBlockState(blockEntity.getActualPos());
                return registration.blockAccessor().from(blockAccessor).blockState(state).build();
            }
            return accessor;
        });
    }
}
