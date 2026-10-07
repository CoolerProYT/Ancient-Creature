package com.coolerpromc.ancientcreature.client.model.bedrock;

import org.jspecify.annotations.Nullable;

import java.util.Locale;

/**
 * Cube face names as Bedrock files use them.
 *
 * <p>Note that Bedrock's {@code east} is the face on the <em>negative</em> X side of the cube in file
 * coordinates and {@code west} the positive side; this is how Blockbench reads and writes them, and
 * the baker maps them accordingly.
 */
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
