package com.coolerpromc.ancientcreature.client.model.bedrock;

import org.jspecify.annotations.Nullable;

import java.util.Locale;

public enum BedrockFace {
    NORTH, SOUTH, EAST, WEST, UP, DOWN;

    public static @Nullable BedrockFace byName(String name) {
        return switch (name.toLowerCase(Locale.ROOT)) {
            case "north" -> NORTH;
            case "south" -> SOUTH;
            case "east" -> EAST;
            case "west" -> WEST;
            case "up" -> UP;
            case "down" -> DOWN;
            default -> null;
        };
    }

    public String serializedName() {
        return this.name().toLowerCase(Locale.ROOT);
    }
}
