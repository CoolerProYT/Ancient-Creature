package com.coolerpromc.ancientcreature.species;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.network.ClientboundSpeciesSyncPacket;
import com.coolerpromc.ancientcreature.platform.Services;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class SpeciesSyncHandler {
    private static volatile @Nullable MinecraftServer server;

    private static final List<Runnable> RELOAD_LISTENERS = new CopyOnWriteArrayList<>();

    public static void addReloadListener(Runnable listener) {
        RELOAD_LISTENERS.add(listener);
    }

    public static void onServerStarted(MinecraftServer startedServer) {
        server = startedServer;
    }

    public static void onServerStopped() {
        server = null;
        SpeciesManager.SERVER.clear();
    }

    public static @Nullable MinecraftServer currentServer() {
        return server;
    }

    public static void onSpeciesReloaded() {
        for (Runnable listener : RELOAD_LISTENERS) {
            try {
                listener.run();
            } catch (RuntimeException e) {
                Constants.LOG.error("A species reload listener threw; continuing with the rest", e);
            }
        }

        MinecraftServer current = server;
        if (current == null) {
            return;
        }

        List<ServerPlayer> players = current.getPlayerList().getPlayers();
        if (!players.isEmpty()) {
            ClientboundSpeciesSyncPacket packet = ClientboundSpeciesSyncPacket.ofServerState();
            for (ServerPlayer player : players) {
                Services.NETWORK.sendToPlayer(player, packet);
            }
        }

        int refreshed = 0;
        for (ServerLevel level : current.getAllLevels()) {
            for (var entity : level.getAllEntities()) {
                if (entity instanceof com.coolerpromc.ancientcreature.entity.custom.AncientCreatureEntity creature) {
                    creature.onSpeciesDataReloaded();
                    refreshed++;
                }
            }
        }

        if (refreshed > 0) {
            Constants.LOG.info("Refreshed {} loaded Ancient Creature entities after a species reload", refreshed);
        }
    }

    private SpeciesSyncHandler() {
    }
}
