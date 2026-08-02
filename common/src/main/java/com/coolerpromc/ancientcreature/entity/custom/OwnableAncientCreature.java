package com.coolerpromc.ancientcreature.entity.custom;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import org.jspecify.annotations.Nullable;

public interface OwnableAncientCreature extends OwnableEntity {
    void setOwner(@Nullable LivingEntity owner);

    boolean isOwnedBy(@Nullable LivingEntity entity);
}
