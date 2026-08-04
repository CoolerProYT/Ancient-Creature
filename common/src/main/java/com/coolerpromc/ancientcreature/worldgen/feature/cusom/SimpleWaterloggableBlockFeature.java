package com.coolerpromc.ancientcreature.worldgen.feature.cusom;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.MossyCarpetBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.SimpleBlockFeature;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;

public class SimpleWaterloggableBlockFeature extends SimpleBlockFeature {
    public SimpleWaterloggableBlockFeature(Codec<SimpleBlockConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<SimpleBlockConfiguration> context) {
        SimpleBlockConfiguration config = context.config();
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        BlockState stateToPlace = config.toPlace().getOptionalState(level, context.random(), origin);
        BlockState original = level.getBlockState(origin);
        if (stateToPlace == null) {
            return false;
        } else if (stateToPlace.canSurvive(level, origin)) {
            if (original.getFluidState().is(FluidTags.WATER) || original.getOptionalValue(BlockStateProperties.WATERLOGGED).orElse(false)){
                stateToPlace = stateToPlace.setValue(BlockStateProperties.WATERLOGGED, true);
            }

            if (stateToPlace.getBlock() instanceof DoublePlantBlock) {
                if (!level.isEmptyBlock(origin.above())) {
                    return false;
                }

                DoublePlantBlock.placeAt(level, stateToPlace, origin, 2);
            } else if (stateToPlace.getBlock() instanceof MossyCarpetBlock) {
                MossyCarpetBlock.placeAt(level, origin, level.getRandom(), 2);
            } else {
                level.setBlock(origin, stateToPlace, 2);
            }

            if (config.scheduleTick()) {
                level.scheduleTick(origin, level.getBlockState(origin).getBlock(), 1);
            }

            return true;
        } else {
            return false;
        }
    }
}
