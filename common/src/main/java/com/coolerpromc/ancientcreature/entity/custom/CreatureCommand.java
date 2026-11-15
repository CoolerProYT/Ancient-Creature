package com.coolerpromc.ancientcreature.entity.custom;

import com.mojang.serialization.Codec;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public enum CreatureCommand implements StringRepresentable {
    ROAM, FOLLOW, STAY;

    public static final Codec<CreatureCommand> CODEC = StringRepresentable.fromEnum(CreatureCommand::values);

    public CreatureCommand next() {
        return switch (this) {
            case ROAM -> FOLLOW;
            case FOLLOW -> STAY;
            case STAY -> ROAM;
        };
    }

    public Component displayName() {
        return Component.translatable("command.ancientcreature." + this.getSerializedName());
    }

    @Override
    public String getSerializedName() {
        return this.name().toLowerCase(Locale.ROOT);
    }
}
