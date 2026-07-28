package com.coolerpromc.ancientcreature;

import com.coolerpromc.ancientcreature.block.ModBlocks;
import com.coolerpromc.ancientcreature.data.component.ModDataComponents;
import com.coolerpromc.ancientcreature.entity.ModEntities;
import com.coolerpromc.ancientcreature.creativetab.ModCreativeTabs;
import com.coolerpromc.ancientcreature.item.ModItems;
import com.coolerpromc.ancientcreature.loot.ModLootFunctions;
import com.coolerpromc.ancientcreature.platform.Services;
import com.coolerpromc.ancientcreature.worldgen.structure.ModStructureTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;

public class AncientCreature {
    public static void init() {
        ModItems.init();
        ModBlocks.init();
        ModDataComponents.init();
        ModCreativeTabs.init();
        ModEntities.init();
        ModStructureTypes.init();
        ModLootFunctions.init();
    }

    private static void registerEntityAttribute(EntityType<? extends LivingEntity> entityType, AttributeSupplier supplier){
        Services.REGISTRY.registerEntityAttribute(entityType, supplier);
    }
}