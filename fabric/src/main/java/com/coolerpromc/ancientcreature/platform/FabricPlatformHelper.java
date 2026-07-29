package com.coolerpromc.ancientcreature.platform;

import com.coolerpromc.ancientcreature.platform.services.IPlatformHelper;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class FabricPlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public boolean isModLoaded(String modId) {

        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {

        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public TagKey<Item> brushToolTag() {
        return ConventionalItemTags.BRUSH_TOOLS;
    }
}
