package com.coolerpromc.ancientcreature.compat.jade;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.block.entity.custom.PlaceholderBlockEntity;
import com.coolerpromc.ancientcreature.compat.jade.component.ModCreatureOwnerComponentProvider;
import com.coolerpromc.ancientcreature.compat.jade.data.ModCreatureOwnerDataProvider;
import com.coolerpromc.ancientcreature.entity.custom.OwnedAncientCreature;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import snownee.jade.api.*;

@WailaPlugin
public class ModJadePlugin implements IWailaPlugin {
    public static final Identifier OWNER_DATA = Constants.id("owner_data");

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
        registration.registerEntityComponent(ModCreatureOwnerComponentProvider.INSTANCE, OwnedAncientCreature.class);
    }

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerEntityDataProvider(ModCreatureOwnerDataProvider.INSTANCE, OwnedAncientCreature.class);
    }
}
