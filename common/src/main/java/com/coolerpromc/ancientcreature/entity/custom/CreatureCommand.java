package com.coolerpromc.ancientcreature.entity.custom;

import com.mojang.serialization.Codec;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

import java.util.Locale;

/**
 * What an owned creature has been told to do with the Horn Whistle.
 *
 * <ul>
 *   <li>{@link #ROAM}: lives normally, but stays within {@link AncientCreatureEntity#ROAM_RADIUS} blocks of
 *       the spot where it was told to roam.</li>
 *   <li>{@link #FOLLOW}: follows its owner, catching up by teleporting when left far behind, and defends
 *       its owner if it is a species that fights.</li>
 *   <li>{@link #STAY}: stays put until told otherwise.</li>
 * </ul>
 */
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
