package com.coolerpromc.ancientcreature.compat.jade;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.block.custom.EggBlock;
import com.coolerpromc.ancientcreature.block.entity.custom.EggBlockEntity;
import com.coolerpromc.ancientcreature.block.entity.custom.PlaceholderBlockEntity;
import com.coolerpromc.ancientcreature.compat.jade.component.CreatureHungerComponentProvider;
import com.coolerpromc.ancientcreature.compat.jade.component.EggDataComponentProvider;
import com.coolerpromc.ancientcreature.compat.jade.data.EggDataProvider;
import com.coolerpromc.ancientcreature.entity.custom.AncientCreatureEntity;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import snownee.jade.api.*;

@WailaPlugin
public class ModJadePlugin implements IWailaPlugin {
    public static final Identifier EGG_DATA = Constants.id("egg_data");
    public static final Identifier CREATURE_HUNGER = Constants.id("creature_hunger");

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
        registration.registerBlockComponent(EggDataComponentProvider.INSTANCE, EggBlock.class);
        registration.registerEntityComponent(CreatureHungerComponentProvider.INSTANCE, AncientCreatureEntity.class);
    }

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(EggDataProvider.INSTANCE, EggBlockEntity.class);
    }
}
