package com.coolerpromc.ancientcreature.entity.behavior;

import com.mojang.serialization.MapCodec;
import net.minecraft.resources.Identifier;

/**
 * A registered kind of behavior component.
 *
 * @param id              the id species JSON refers to, e.g. {@code ancientcreature:charge_attack}
 * @param codec           parses this component's configuration fields
 * @param defaultPriority goal priority used when the JSON omits {@code "priority"}
 */
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
