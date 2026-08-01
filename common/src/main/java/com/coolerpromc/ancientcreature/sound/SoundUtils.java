package com.coolerpromc.ancientcreature.sound;

import com.coolerpromc.ancientcreature.platform.util.RegistryHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

public class SoundUtils {
    public static void stopSound(ServerLevel level, BlockPos pos, RegistryHandler<SoundEvent, SoundEvent> event) {
        ClientboundStopSoundPacket packet = new ClientboundStopSoundPacket(event.id(), SoundSource.BLOCKS);

        for (ServerPlayer player : level.getServer().getPlayerList().getPlayers()) {
            if (player.level() == level && player.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()) < 64 * 64) {
                player.connection.send(packet);
            }
        }
    }
}
