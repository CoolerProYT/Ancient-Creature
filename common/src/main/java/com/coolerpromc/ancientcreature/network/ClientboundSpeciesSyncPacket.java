package com.coolerpromc.ancientcreature.network;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.platform.util.PayloadContext;
import com.coolerpromc.ancientcreature.species.SpeciesDefinition;
import com.coolerpromc.ancientcreature.species.SpeciesManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import java.util.LinkedHashMap;
import java.util.Map;

public record ClientboundSpeciesSyncPacket(Map<Identifier, SpeciesDefinition> definitions) implements HandledCustomPacketPayload {
    public static final Type<ClientboundSpeciesSyncPacket> TYPE = new Type<>(Constants.id("species_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundSpeciesSyncPacket> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.<RegistryFriendlyByteBuf, Identifier, SpeciesDefinition, Map<Identifier, SpeciesDefinition>>map(LinkedHashMap::new, Identifier.STREAM_CODEC, SpeciesDefinition.STREAM_CODEC),
        ClientboundSpeciesSyncPacket::definitions,
        ClientboundSpeciesSyncPacket::new
    );

    public static ClientboundSpeciesSyncPacket ofServerState() {
        return new ClientboundSpeciesSyncPacket(SpeciesManager.SERVER.all());
    }

    @Override
    public void handle(PayloadContext context) {
        context.execute(() -> SpeciesManager.CLIENT.replaceAll(this.definitions));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
