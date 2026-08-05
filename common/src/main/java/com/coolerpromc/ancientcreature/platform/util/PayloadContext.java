package com.coolerpromc.ancientcreature.platform.util;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public interface PayloadContext {
    Player player();
    Level level();
    void execute(Runnable runnable);
    void disconnect(Component reason);
}