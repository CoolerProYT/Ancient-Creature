package com.coolerpromc.ancientcreature;

import com.coolerpromc.ancientcreature.platform.Services;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;

public class FabricAncientCreature implements ModInitializer {
    @Override
    public void onInitialize() {
        AncientCreature.init();

        Services.REGISTRY.applyEntityAttributeRegistrations(FabricDefaultAttributeRegistry::register);
    }
}
