package com.coolerpromc.ancientcreature.network;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.entity.Species;
import com.coolerpromc.ancientcreature.platform.util.PayloadContext;
import com.coolerpromc.ancientcreature.saveddata.IdentifiedSpeciesData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.ArrayList;
import java.util.List;

public record ClientboundIdentifiedSpeciesSyncPacket(List<Species> identifiedSpecies) implements HandledCustomPacketPayload{
    public static final Type<ClientboundIdentifiedSpeciesSyncPacket> TYPE = new Type<>(Constants.id("identified_species_sync"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundIdentifiedSpeciesSyncPacket> STREAM_CODEC = StreamCodec.composite(
        Species.STREAM_CODEC.apply(ByteBufCodecs.list()),
        ClientboundIdentifiedSpeciesSyncPacket::identifiedSpecies,
        ClientboundIdentifiedSpeciesSyncPacket::new
    );
    public static final List<OnHandle> onHandles = new ArrayList<>();

    public static void addListener(OnHandle onHandle){
        onHandles.add(onHandle);
    }

    @Override
    public void handle(PayloadContext context) {
        context.execute(() -> {
            IdentifiedSpeciesData.setClientCache(this.identifiedSpecies);
            onHandles.forEach(o -> o.onHandle(this.identifiedSpecies));
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @FunctionalInterface
    public interface OnHandle{
        void onHandle(List<Species> identifiedSpecies);
    }
}
