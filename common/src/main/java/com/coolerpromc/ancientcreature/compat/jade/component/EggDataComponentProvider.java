package com.coolerpromc.ancientcreature.compat.jade.component;

import com.coolerpromc.ancientcreature.compat.jade.ModJadePlugin;
import com.coolerpromc.ancientcreature.compat.jade.data.EggDataProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

import java.util.Optional;

public class EggDataComponentProvider implements IBlockComponentProvider {
    public static final EggDataComponentProvider INSTANCE = new EggDataComponentProvider();

    @Override
    public void appendTooltip(ITooltip iTooltip, BlockAccessor blockAccessor, IPluginConfig iPluginConfig) {
        Optional<Integer> remainingTime = EggDataProvider.INSTANCE.decodeFromData(blockAccessor);
        if (remainingTime.isPresent()){
            iTooltip.add(Component.translatable("jade.ancientcreature.remaining_time", remainingTime.get()));
        }
    }

    @Override
    public Identifier getUid() {
        return ModJadePlugin.EGG_DATA;
    }
}
