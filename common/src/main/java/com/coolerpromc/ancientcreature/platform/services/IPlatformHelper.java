package com.coolerpromc.ancientcreature.platform.services;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public interface IPlatformHelper {
    String getPlatformName();
    boolean isModLoaded(String modId);
    boolean isDevelopmentEnvironment();
    default String getEnvironmentName() {
        return isDevelopmentEnvironment() ? "development" : "production";
    }

    TagKey<Item> brushToolTag();
}