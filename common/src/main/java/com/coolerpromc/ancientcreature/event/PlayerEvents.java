package com.coolerpromc.ancientcreature.event;

import com.coolerpromc.ancientcreature.config.ModCommonConfig;
import com.coolerpromc.ancientcreature.item.ModItems;
import com.coolerpromc.ancientcreature.network.ClientboundIdentifiedSpeciesSyncPacket;
import com.coolerpromc.ancientcreature.network.ClientboundSpeciesSyncPacket;
import com.coolerpromc.ancientcreature.platform.Services;
import com.coolerpromc.ancientcreature.saveddata.IdentifiedSpeciesData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;

public class PlayerEvents {
    public static void onPlayerJoin(ServerPlayer player) {

        Services.NETWORK.sendToPlayer(player, ClientboundSpeciesSyncPacket.ofServerState());
        Services.NETWORK.sendToPlayer(player, new ClientboundIdentifiedSpeciesSyncPacket(IdentifiedSpeciesData.getIdentifiedSpeciesData(player.level().getServer()).getIdentifiedSpecies()));

        if (ModCommonConfig.CONFIG.giveFieldGuideOnFirstJoin.get() && player.getStats().getValue(Stats.CUSTOM.get(Stats.PLAY_TIME)) == 0) {
            player.getInventory().add(ModItems.FIELD_GUIDE.toStack());
        }
    }
}
