package com.coolerpromc.ancientcreature;

import com.coolerpromc.ancientcreature.block.ModBlocks;
import com.coolerpromc.ancientcreature.block.entity.ModBlockEntities;
import com.coolerpromc.ancientcreature.block.entity.custom.FossilCleaningTableBlockEntity;
import com.coolerpromc.ancientcreature.data.component.ModDataComponents;
import com.coolerpromc.ancientcreature.entity.ModEntities;
import com.coolerpromc.ancientcreature.creativetab.ModCreativeTabs;
import com.coolerpromc.ancientcreature.item.ModItems;
import com.coolerpromc.ancientcreature.loot.ModLootFunctions;
import com.coolerpromc.ancientcreature.menu.ModMenus;
import com.coolerpromc.ancientcreature.platform.Services;
import com.coolerpromc.ancientcreature.worldgen.structure.ModStructureTypes;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jspecify.annotations.Nullable;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public class AncientCreature {
    public static void init() {
        ModItems.init();
        ModBlocks.init();
        ModBlockEntities.init();
        ModDataComponents.init();
        ModCreativeTabs.init();
        ModEntities.init();
        ModMenus.init();
        ModStructureTypes.init();
        ModLootFunctions.init();
    }

    public static void initCapability(){
        registerCapability(ModBlockEntities.FOSSIL_CLEANING_TABLE, FossilCleaningTableBlockEntity::getContainerBySide);
    }

    private static <T extends BlockEntity> void registerCapability(Supplier<BlockEntityType<T>> type, BiFunction<T, @Nullable Direction, Container> provider){
        Services.CAPABILITIES.registerBlockEntityItemStorage(type, provider);
    }

    private static void registerEntityAttribute(EntityType<? extends LivingEntity> entityType, AttributeSupplier supplier){
        Services.REGISTRY.registerEntityAttribute(entityType, supplier);
    }
}