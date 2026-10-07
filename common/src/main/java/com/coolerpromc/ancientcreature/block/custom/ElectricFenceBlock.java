package com.coolerpromc.ancientcreature.block.custom;

import com.mojang.serialization.MapCodec;
import com.coolerpromc.ancientcreature.config.ModCommonConfig;
import com.coolerpromc.ancientcreature.damage.ModDamageTypes;
import com.coolerpromc.ancientcreature.entity.custom.AncientCreatureEntity;
import com.coolerpromc.ancientcreature.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CrossCollisionBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

/**
 * A wire fence that shocks whatever touches it while it carries a charge.
 *
 * <p>Charge works like redstone dust: a fence segment that receives a redstone signal is at 15, and
 * every connected segment carries one less than its strongest neighbour, so one power source energises
 * up to 15 segments in each direction. A charged fence hurts and throws back anything that touches it,
 * players included. Creatures will not path through it, and it is never smashed by charging creatures.
 */
public class ElectricFenceBlock extends CrossCollisionBlock {
    public static final IntegerProperty CHARGE = BlockStateProperties.POWER;

    private static final int SHOCK_COOLDOWN = 10;

    public static final MapCodec<ElectricFenceBlock> CODEC = simpleCodec(ElectricFenceBlock::new);

    public ElectricFenceBlock(BlockBehaviour.Properties properties) {
        // post 2 wide, wires 1 wide, 1.5 blocks of collision like any fence
        super(3.0F, 16.0F, 1.0F, 16.0F, 24.0F, properties);
        this.registerDefaultState(this.stateDefinition.any()
            .setValue(NORTH, false).setValue(EAST, false).setValue(SOUTH, false).setValue(WEST, false)
            .setValue(WATERLOGGED, false).setValue(CHARGE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, WEST, SOUTH, WATERLOGGED, CHARGE);
    }

    public static boolean isCharged(BlockState state) {
        return state.getValue(CHARGE) > 0;
    }

    private boolean connectsTo(BlockState state, boolean sturdy, Direction towards) {
        return state.getBlock() instanceof ElectricFenceBlock
            || (state.getBlock() instanceof FenceGateBlock && FenceGateBlock.connectsToDirection(state, towards))
            || (!isExceptionForConnection(state) && sturdy);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        FluidState fluid = level.getFluidState(pos);
        BlockState state = this.defaultBlockState().setValue(WATERLOGGED, fluid.is(Fluids.WATER));
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockPos neighbour = pos.relative(direction);
            BlockState other = level.getBlockState(neighbour);
            state = state.setValue(PROPERTY_BY_DIRECTION.get(direction), this.connectsTo(other, other.isFaceSturdy(level, neighbour, direction.getOpposite()), direction.getOpposite()));
        }
        return state.setValue(CHARGE, this.computeCharge(level, pos));
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos, Direction direction, BlockPos neighbourPos, BlockState neighbour, RandomSource random) {
        if (state.getValue(WATERLOGGED)) {
            ticks.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        if (direction.getAxis().isHorizontal()) {
            return state.setValue(PROPERTY_BY_DIRECTION.get(direction), this.connectsTo(neighbour, neighbour.isFaceSturdy(level, neighbourPos, direction.getOpposite()), direction.getOpposite()));
        }
        return super.updateShape(state, level, ticks, pos, direction, neighbourPos, neighbour, random);
    }

    /** 15 with a redstone signal here; otherwise one less than the best adjacent fence. */
    private int computeCharge(Level level, BlockPos pos) {
        if (level.hasNeighborSignal(pos)) {
            return 15;
        }
        int best = 0;
        for (Direction direction : Direction.values()) {
            BlockState neighbour = level.getBlockState(pos.relative(direction));
            if (neighbour.getBlock() instanceof ElectricFenceBlock) {
                best = Math.max(best, neighbour.getValue(CHARGE) - 1);
            }
        }
        return Math.max(0, best);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, @Nullable Orientation orientation, boolean movedByPiston) {
        if (level.isClientSide()) {
            return;
        }
        int charge = this.computeCharge(level, pos);
        if (charge != state.getValue(CHARGE)) {
            // UPDATE_ALL notifies the neighbouring segments, which recompute in turn. Every step either
            // raises a segment to its source distance or lowers it by one, so the network settles.
            level.setBlock(pos, state.setValue(CHARGE, charge), Block.UPDATE_ALL);
        }
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if (!oldState.is(state.getBlock()) && !level.isClientSide()) {
            level.updateNeighborsAt(pos, this);
        }
    }

    @Override
    protected VoxelShape getEntityInsideCollisionShape(BlockState state, BlockGetter level, BlockPos pos, Entity entity) {
        // A touch is enough: the shock reaches slightly past the collision box, which an entity pushing
        // against the fence overlaps.
        VoxelShape reach = Shapes.empty();
        for (AABB box : this.getCollisionShape(state, level, pos, CollisionContext.empty()).toAabbs()) {
            reach = Shapes.or(reach, Shapes.create(box.inflate(0.08)));
        }
        return reach;
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier effects, boolean isPrecise) {
        if (!(level instanceof ServerLevel server) || !isCharged(state) || !(entity instanceof LivingEntity living) || living.isSpectator()) {
            return;
        }
        if (living.tickCount - lastShock(living) < SHOCK_COOLDOWN) {
            return;
        }
        markShock(living);
        float strength = state.getValue(CHARGE) / 15.0F;
        float damage = ModCommonConfig.CONFIG.electricFenceDamage.get().floatValue() * (0.5F + 0.5F * strength);
        if (damage > 0.0F) {
            living.hurtServer(server, ModDamageTypes.source(server, ModDamageTypes.ELECTRIC_FENCE), damage);
        }
        Vec3 away = living.position().subtract(Vec3.atCenterOf(pos)).multiply(1, 0, 1);
        if (away.lengthSqr() < 1.0E-4) {
            away = new Vec3(server.getRandom().nextDouble() - 0.5, 0, server.getRandom().nextDouble() - 0.5);
        }
        double push = 0.6 * (living instanceof AncientCreatureEntity creature ? 1.0 / Math.max(1.0, creature.getBbWidth() * 0.5) : 1.0);
        Vec3 shove = away.normalize().scale(push);
        living.push(shove.x, 0.25, shove.z);
        if (living instanceof AncientCreatureEntity creature) {
            creature.onShocked();
        }
        server.playSound(null, pos, ModSounds.ELECTRIC_FENCE_ZAP.get(), SoundSource.BLOCKS, 0.8F, 0.9F + server.getRandom().nextFloat() * 0.2F);
        server.sendParticles(ParticleTypes.ELECTRIC_SPARK, living.getX(), living.getY() + living.getBbHeight() * 0.5, living.getZ(), 12, 0.3, 0.3, 0.3, 0.2);
    }

    private static int lastShock(LivingEntity entity) {
        return SHOCKS.getOrDefault(entity, -1000);
    }

    private static void markShock(LivingEntity entity) {
        SHOCKS.put(entity, entity.tickCount);
    }

    /** Last shock tick per entity, so a creature leaning on the fence is not shocked every tick. */
    private static final java.util.Map<LivingEntity, Integer> SHOCKS = java.util.Collections.synchronizedMap(new java.util.WeakHashMap<>());

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (!isCharged(state)) {
            return;
        }
        if (random.nextInt(40) == 0) {
            level.addParticle(ParticleTypes.ELECTRIC_SPARK, pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.4, pos.getY() + 0.3 + random.nextDouble() * 0.6, pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.4, 0, 0, 0);
        }
        if (random.nextInt(120) == 0) {
            level.playLocalSound(pos, ModSounds.ELECTRIC_FENCE_HUM.get(), SoundSource.BLOCKS, 0.25F, 0.9F + random.nextFloat() * 0.2F, false);
        }
    }

    @Override
    protected MapCodec<ElectricFenceBlock> codec() {
        return CODEC;
    }
}
