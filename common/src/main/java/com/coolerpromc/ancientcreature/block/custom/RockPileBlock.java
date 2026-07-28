package com.coolerpromc.ancientcreature.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

import java.util.stream.Stream;

public class RockPileBlock extends HorizontalDirectionalBlock {
    public static final BooleanProperty HAS_EGG = BooleanProperty.create("has_egg");
    public static final BooleanProperty HAS_FOSSIL = BooleanProperty.create("has_fossil");

    public static final VoxelShape WITH_EGG = Stream.of(
        Stream.of(
            Block.box(0.5, 0, 3.5, 5.5, 2.5, 8),
            Block.box(1, 0, 9, 6, 3, 14),
            Block.box(4.5, 0, 5, 10.5, 3, 10.5),
            Block.box(5, 0, 0.5, 10.5, 2.25, 5),
            Block.box(6, 0, 11.5, 11.5, 2.25, 15.5),
            Block.box(11, 0, 2.5, 15.5, 2.75, 7),
            Block.box(12, 0, 8, 15.75, 3.25, 13),
            Block.box(3.25, 2, 5.5, 7.5, 4.25, 9),
            Block.box(2, 0.25, 13, 4.5, 1.5, 15.5),
            Block.box(2, 0.25, 1.5, 4.5, 1.75, 4),
            Block.box(13.5, 2.5, 5.5, 15.5, 4.25, 8)
        ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get(),
        Stream.of(
            Block.box(8.25, 2, 6.25, 12.25, 3.5, 10.75),
            Block.box(7.25, 3, 5.25, 13.25, 5.5, 11.75),
            Block.box(6.75, 5, 4.75, 13.75, 7.5, 12.25),
            Block.box(7.25, 7, 5.25, 13.25, 9, 11.75),
            Block.box(8, 8.5, 6, 12.5, 10.25, 11),
            Block.box(9, 9.75, 7, 11.5, 11, 10),
            Block.box(10.05, 4.35, 12.15, 10.45, 6.8, 12.4),
            Block.box(9.8, 6.25, 11.7, 10.2, 8.5, 11.95),
            Block.box(10.45, 6.35, 11.9, 10.8, 8, 12.15),
            Block.box(9.15, 7.75, 11.55, 10, 8.1, 11.8)
        ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get(),
        Stream.of(
            Block.box(8.25, 0.4, 13.2, 10.25, 0.9, 14.75),
            Block.box(5.6, 0.45, 10.9, 7.35, 1.05, 12.4),
            Block.box(13.1, 0.5, 6.9, 14.7, 1.1, 8.25),
            Block.box(11.3, 1.7, 11.75, 12.5, 2.25, 13.25)
        ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get()
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public static final VoxelShape WITH_FOSSIL = Shapes.join(Stream.of(
        Block.box(0.5, 0, 2, 6, 2.5, 7),
        Block.box(5, 0, 0.5, 11, 3, 5.5),
        Block.box(10.5, 0, 2.5, 15.5, 2.5, 7.5),
        Block.box(1, 0, 8.5, 6.5, 3, 14.5),
        Block.box(5.5, 0, 10, 11.5, 2.5, 15.5),
        Block.box(11, 0, 8, 15.5, 3, 13),
        Block.box(4, 1.5, 4.5, 11, 4.5, 10.5),
        Block.box(7.5, 3.5, 3.5, 12.5, 6, 8),
        Block.box(0.75, 0.5, 12.5, 3.5, 2, 15.25),
        Block.box(13, 2, 5.5, 15.5, 4, 8.5),
        Block.box(4, 3.2, 6.5, 7, 5, 9.5),
        Block.box(9.5, 2, 11, 13, 4, 13.5)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get(), Stream.of(
        Block.box(5.2, 2.35, 12.35, 8.6, 2.95, 13.1),
        Block.box(4.8, 2.4, 11.9, 5.5, 4.3, 12.6),
        Block.box(7.9, 2.3, 12.1, 8.6, 3.9, 12.8),
        Block.box(2.5, 2.2, 6.6, 5.7, 2.85, 7.35),
        Block.box(12.2, 2.8, 7.2, 13.6, 3.7, 8.4),
        Block.box(12.65, 2.6, 7.5, 13.15, 4.25, 8.05),
        Block.box(3.1, 2.7, 9.2, 4.4, 3.35, 10.6)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get(), BooleanOp.OR);

    public static final VoxelShape WITH_FOSSIL_AND_EGG = Stream.of(
        Stream.of(
            Block.box(0.5, 0, 2, 6, 2.5, 7),
            Block.box(5, 0, 0.5, 11, 3, 5.5),
            Block.box(10.5, 0, 2.5, 15.5, 2.5, 7.5),
            Block.box(1, 0, 8.5, 6.5, 3, 14.5),
            Block.box(5.5, 0, 10, 11.5, 2.5, 15.5),
            Block.box(11, 0, 8, 15.5, 3, 13),
            Block.box(4, 1.5, 4.5, 11, 4.5, 10.5),
            Block.box(0.75, 0.5, 12.5, 3.5, 2, 15.25),
            Block.box(13, 2, 5.5, 15.5, 4, 8.5),
            Block.box(9.5, 2, 11, 13, 4, 13.5)
        ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get(),
        Stream.of(
            Block.box(5.2, 2.35, 12.35, 8.6, 2.95, 13.1),
            Block.box(4.8, 2.4, 11.9, 5.5, 4.3, 12.6),
            Block.box(7.9, 2.3, 12.1, 8.6, 3.9, 12.8),
            Block.box(2.5, 2.2, 6.6, 5.7, 2.85, 7.35),
            Block.box(12.2, 2.8, 7.2, 13.6, 3.7, 8.4),
            Block.box(12.65, 2.6, 7.5, 13.15, 4.25, 8.05),
            Block.box(3.1, 2.7, 9.2, 4.4, 3.35, 10.6)
        ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get(),
        Stream.of(
            Block.box(7.5, 2.4, 5, 11, 3.8, 9),
            Block.box(6.5, 3.2, 4, 12, 5.5, 10),
            Block.box(6.25, 5, 3.75, 12.25, 7.5, 10.25),
            Block.box(6.75, 7, 4.25, 11.75, 9, 9.75),
            Block.box(7.5, 8.5, 5, 11, 10.2, 9),
            Block.box(8.4, 9.7, 5.9, 10.2, 11, 8.2),
            Block.box(8.95, 4.8, 10.18, 9.3, 6.8, 10.43),
            Block.box(8.75, 6.2, 9.72, 9.1, 8.15, 9.97),
            Block.box(9.35, 6.2, 9.95, 9.7, 7.55, 10.2)
        ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get()
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public static final VoxelShape NORMAL = Stream.of(
        Block.box(0.5, 0, 3.5, 5.5, 2.5, 8),
        Block.box(1, 0, 9, 6, 3, 14),
        Block.box(4.5, 0, 5, 10.5, 3, 10.5),
        Block.box(5, 0, 0.5, 10.5, 2.25, 5),
        Block.box(6, 0, 11.5, 11.5, 2.25, 15.5),
        Block.box(11, 0, 2.5, 15.5, 2.75, 7),
        Block.box(12, 0, 8, 15.75, 3.25, 13),
        Block.box(3.25, 2, 5.5, 7.5, 4.25, 9),
        Block.box(2, 0.25, 13, 4.5, 1.5, 15.5),
        Block.box(2, 0.25, 1.5, 4.5, 1.75, 4),
        Block.box(13.5, 2.5, 5.5, 15.5, 4.25, 8),
        Block.box(6.25, 2.4, 5.75, 11.75, 5.2, 11),
        Block.box(7.4, 4.7, 6.5, 10.75, 6.5, 9.75),
        Block.box(5.3, 3.4, 9.5, 7.5, 5, 11.6)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public RockPileBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(HAS_EGG, false).setValue(HAS_FOSSIL, false).setValue(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return simpleCodec(RockPileBlock::new);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HAS_EGG, HAS_FOSSIL, FACING);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction facing = state.getValue(FACING);
        boolean hasEgg = state.getValue(HAS_EGG);
        boolean hasFossil = state.getValue(HAS_FOSSIL);
        if (hasEgg && hasFossil){
            return Shapes.rotateAll(WITH_FOSSIL_AND_EGG).get(facing);
        }
        if (hasEgg){
            return Shapes.rotateAll(WITH_EGG).get(facing);
        }
        if (hasFossil){
            return Shapes.rotateAll(WITH_FOSSIL).get(facing);
        }
        return Shapes.rotateAll(NORMAL).get(facing);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection());
    }
}
