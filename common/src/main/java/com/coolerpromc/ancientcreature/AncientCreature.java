package com.coolerpromc.ancientcreature;

import com.coolerpromc.ancientcreature.entity.ModEntities;
import com.coolerpromc.ancientcreature.platform.Services;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;

public class AncientCreature {
    public static void init() {
        ModEntities.init();
    }

    private static void registerEntityAttribute(EntityType<? extends LivingEntity> entityType, AttributeSupplier supplier){
        Services.REGISTRY.registerEntityAttribute(entityType, supplier);
    }
}