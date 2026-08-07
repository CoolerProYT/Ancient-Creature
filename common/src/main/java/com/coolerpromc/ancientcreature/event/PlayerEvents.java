package com.coolerpromc.ancientcreature.event;

import com.coolerpromc.ancientcreature.network.ClientboundIdentifiedSpeciesSyncPacket;
import com.coolerpromc.ancientcreature.network.ClientboundSpeciesSyncPacket;
import com.coolerpromc.ancientcreature.platform.Services;
import com.coolerpromc.ancientcreature.saveddata.IdentifiedSpeciesData;
import net.minecraft.server.level.ServerPlayer;

public class PlayerEvents {
    public static void onPlayerJoin(ServerPlayer player) {
        // Species definitions first: the identified-species list is meaningless to the client until it
        // knows which species exist.
        Services.NETWORK.sendToPlayer(player, ClientboundSpeciesSyncPacket.ofServerState());
        Services.NETWORK.sendToPlayer(player, new ClientboundIdentifiedSpeciesSyncPacket(IdentifiedSpeciesData.getIdentifiedSpeciesData(player.level().getServer()).getIdentifiedSpecies()));
    }
}
