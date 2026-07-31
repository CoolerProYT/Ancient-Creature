package com.coolerpromc.ancientcreature.block.custom;

import com.coolerpromc.ancientcreature.block.entity.custom.PlaceholderBlockEntity;
import com.coolerpromc.ancientcreature.platform.Services;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public class PlaceholderBlock extends BaseEntityBlock {
    public static final EnumProperty<Direction> ACTUAL_BLOCK = EnumProperty.create("actual_block", Direction.class);

    public PlaceholderBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(ACTUAL_BLOCK, Direction.DOWN));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(PlaceholderBlock::new);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (level.getBlockEntity(pos) instanceof PlaceholderBlockEntity blockEntity){
            try{
                Direction facing = level.getBlockState(pos.relative(state.getValue(ACTUAL_BLOCK))).getValue(BlockStateProperties.HORIZONTAL_FACING);
                return Shapes.rotateAll(blockEntity.getShape()).get(facing);
            }
            catch (Exception _){

            }
        }
        return Shapes.block();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ACTUAL_BLOCK);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (player instanceof ServerPlayer serverPlayer && level.getBlockEntity(pos) instanceof PlaceholderBlockEntity blockEntity && level.getBlockEntity(blockEntity.getActualPos()) instanceof MenuProvider menuProvider){
            Services.MENU.openMenu(serverPlayer, menuProvider, blockEntity.getActualPos());
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos worldPosition, BlockState blockState) {
        return new PlaceholderBlockEntity(worldPosition, blockState);
    }

    @Override
    protected void spawnDestroyParticles(Level level, Player player, BlockPos pos, BlockState state) {

    }

    @Override
    protected SoundType getSoundType(BlockState state) {
        return SoundType.EMPTY;
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (level.getBlockEntity(pos) instanceof PlaceholderBlockEntity blockEntity) {
            blockEntity.setShouldDrop(!player.isCreative());
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    protected float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof PlaceholderBlockEntity blockEntity){
            BlockPos actualPos = blockEntity.getActualPos();
            return level.getBlockState(actualPos).getDestroyProgress(player, level, actualPos);
        }
        return super.getDestroyProgress(state, player, level, pos);
    }
}
