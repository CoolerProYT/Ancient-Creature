package com.coolerpromc.ancientcreature.block.entity.custom;

import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import org.jspecify.annotations.Nullable;

public interface ICapabilityExposure {
    Container getContainerBySide(@Nullable Direction direction);
    Container[] getAllContainers();
}
