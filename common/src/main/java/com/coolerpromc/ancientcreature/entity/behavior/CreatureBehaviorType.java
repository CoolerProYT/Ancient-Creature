package com.coolerpromc.ancientcreature.entity.behavior;

import com.mojang.serialization.MapCodec;
import net.minecraft.resources.Identifier;

public record CreatureBehaviorType<C extends CreatureBehaviorConfig>(
    Identifier id,
    MapCodec<C> codec,
    int defaultPriority
) {
    @Override
    public String toString() {
        return this.id.toString();
    }
}
