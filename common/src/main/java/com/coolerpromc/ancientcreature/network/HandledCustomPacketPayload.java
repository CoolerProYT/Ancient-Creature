package com.coolerpromc.ancientcreature.network;

import com.coolerpromc.ancientcreature.platform.util.PayloadContext;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public interface HandledCustomPacketPayload extends CustomPacketPayload {
    void handle(PayloadContext context);
}
