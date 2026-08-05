package com.coolerpromc.ancientcreature.event;

import com.coolerpromc.ancientcreature.network.ClientboundIdentifiedSpeciesSyncPacket;
import com.coolerpromc.ancientcreature.platform.Services;
import com.coolerpromc.ancientcreature.saveddata.IdentifiedSpeciesData;
import net.minecraft.server.level.ServerPlayer;

public class PlayerEvents {
    public static void onPlayerJoin(ServerPlayer player) {
        Services.NETWORK.sendToPlayer(player, new ClientboundIdentifiedSpeciesSyncPacket(IdentifiedSpeciesData.getIdentifiedSpeciesData(player.level().getServer()).getIdentifiedSpecies()));
    }
}
