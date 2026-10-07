package com.coolerpromc.ancientcreature.network;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.entity.custom.AncientCreatureEntity;
import com.coolerpromc.ancientcreature.platform.util.PayloadContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * The rider pressed the mount ability key. Carries nothing: the server works out which creature from the
 * player's vehicle and checks ownership and cooldown itself, so a modified client cannot trigger abilities
 * it should not.
 */
public record ServerboundMountAbilityPacket() implements HandledCustomPacketPayload {
    public static final ServerboundMountAbilityPacket INSTANCE = new ServerboundMountAbilityPacket();
    public static final Type<ServerboundMountAbilityPacket> TYPE = new Type<>(Constants.id("mount_ability"));
    public static final StreamCodec<ByteBuf, ServerboundMountAbilityPacket> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public void handle(PayloadContext context) {
        context.execute(() -> {
            if (context.player().getVehicle() instanceof AncientCreatureEntity creature) {
                creature.tryMountAbility(context.player());
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
