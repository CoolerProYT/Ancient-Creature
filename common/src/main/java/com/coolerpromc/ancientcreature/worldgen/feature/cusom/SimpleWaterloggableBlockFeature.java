package com.coolerpromc.ancientcreature.worldgen.feature.cusom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.MossyCarpetBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public record SimpleWaterloggableBlockFeature(Holder<BlockStateProvider> toPlace, boolean scheduleTick) implements Feature {
    public static final MapCodec<SimpleWaterloggableBlockFeature> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        BlockStateProvider.CODEC.fieldOf("state_provider").forGetter(SimpleWaterloggableBlockFeature::toPlace),
        Codec.BOOL.optionalFieldOf("schedule_tick", false).forGetter(SimpleWaterloggableBlockFeature::scheduleTick)
    ).apply(instance, SimpleWaterloggableBlockFeature::new));

    public SimpleWaterloggableBlockFeature(Holder<BlockStateProvider> toPlace) {
        this(toPlace, false);
    }

    public SimpleWaterloggableBlockFeature(BlockStateProvider toPlace) {
        this(Holder.direct(toPlace));
    }

    @Override
    public MapCodec<SimpleWaterloggableBlockFeature> codec() {
        return CODEC;
    }

    @Override
    public boolean place(WorldGenLevel level, ChunkGenerator generator, RandomSource random, BlockPos origin) {
        BlockState stateToPlace = toPlace.value().getOptionalState(level, random, origin);
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

            if (scheduleTick) {
                level.scheduleTick(origin, level.getBlockState(origin).getBlock(), 1);
            }

            return true;
        } else {
            return false;
        }
    }
}
