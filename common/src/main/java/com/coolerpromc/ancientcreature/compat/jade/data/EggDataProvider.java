package com.coolerpromc.ancientcreature.compat.jade.data;

import com.coolerpromc.ancientcreature.block.entity.custom.EggBlockEntity;
import com.coolerpromc.ancientcreature.compat.jade.ModJadePlugin;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.StreamServerDataProvider;

public class EggDataProvider implements StreamServerDataProvider<BlockAccessor, Integer> {
    public static final EggDataProvider INSTANCE = new EggDataProvider();

    @Override
    public @Nullable Integer streamData(BlockAccessor blockAccessor) {
        if (blockAccessor.getBlockEntity() instanceof EggBlockEntity blockEntity){
            return Math.round(blockEntity.getRemainingTime() / 20f);
        }
        return null;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, Integer> streamCodec() {
        return ByteBufCodecs.INT.cast();
    }

    @Override
    public Identifier getUid() {
        return ModJadePlugin.EGG_DATA;
    }
}
