package com.coolerpromc.ancientcreature.platform.util;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/** Server-side context for payloads sent by a client. */
public record FabricServerPayloadContext(ServerPlayNetworking.Context context) implements PayloadContext {
    @Override
    public Player player() {
        return this.context.player();
    }

    @Override
    public Level level() {
        return this.context.player().level();
    }

    @Override
    public void execute(Runnable runnable) {
        this.context.server().execute(runnable);
    }

    @Override
    public void disconnect(Component reason) {
        this.context.responseSender().disconnect(reason);
    }
}
