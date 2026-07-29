package com.coolerpromc.ancientcreature.platform;

import com.coolerpromc.ancientcreature.platform.services.IPlatformHelper;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.common.Tags;

public class NeoForgePlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {

        return "NeoForge";
    }

    @Override
    public boolean isModLoaded(String modId) {

        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {

        return !FMLLoader.getCurrent().isProduction();
    }

    @Override
    public TagKey<Item> brushToolTag() {
        return Tags.Items.TOOLS_BRUSH;
    }
}