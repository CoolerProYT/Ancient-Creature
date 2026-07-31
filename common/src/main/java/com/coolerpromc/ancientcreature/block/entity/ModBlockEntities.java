package com.coolerpromc.ancientcreature.block.entity;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.block.ModBlocks;
import com.coolerpromc.ancientcreature.block.entity.custom.FossilCleaningTableBlockEntity;
import com.coolerpromc.ancientcreature.block.entity.custom.FossilIdentificationChamberBlockEntity;
import com.coolerpromc.ancientcreature.block.entity.custom.PlaceholderBlockEntity;
import com.coolerpromc.ancientcreature.platform.Services;
import com.coolerpromc.ancientcreature.platform.util.BlockEntityTypeFactory;
import com.coolerpromc.ancientcreature.platform.util.RegistryHandler;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.List;
import java.util.function.Supplier;

public class ModBlockEntities {
    public static final RegistryHandler<BlockEntityType<?>, BlockEntityType<FossilCleaningTableBlockEntity>> FOSSIL_CLEANING_TABLE = register("fossil_cleaning_table", FossilCleaningTableBlockEntity::new, List.of(ModBlocks.FOSSIL_CLEANING_TABLE.block()));
    public static final RegistryHandler<BlockEntityType<?>, BlockEntityType<FossilIdentificationChamberBlockEntity>> FOSSIL_IDENTIFICATION_CHAMBER = register("fossil_identification_chamber", FossilIdentificationChamberBlockEntity::new, List.of(ModBlocks.FOSSIL_IDENTIFICATION_CHAMBER.block()));
    public static final RegistryHandler<BlockEntityType<?>, BlockEntityType<PlaceholderBlockEntity>> PLACEHOLDER = register("placeholder", PlaceholderBlockEntity::new, List.of(ModBlocks.PLACEHOLDER.block()));

    public static <T extends BlockEntity> RegistryHandler<BlockEntityType<?>, BlockEntityType<T>> register(String name, BlockEntityTypeFactory<T> factory, List<Supplier<? extends Block>> blocks){
        return Services.REGISTRY.registerBlockEntityType(name, factory, blocks);
    }

    public static void init(){
        Constants.LOG.info("Registering Block Entities.");
    }
}
