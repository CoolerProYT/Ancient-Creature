package com.coolerpromc.ancientcreature.block.custom;

import com.coolerpromc.ancientcreature.Constants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

/**
 * A wooden sieve: load it with gravel, sand or mud, then work it by hand a few times to shake out
 * whatever was buried in it. A slow but renewable trickle of fossil fragments.
 */
public class SifterBlock extends Block {
    public static final EnumProperty<Material> CONTENT = EnumProperty.create("content", Material.class);
    public static final int SIFTS = 4;
    public static final IntegerProperty STAGE = IntegerProperty.create("sift_stage", 0, SIFTS - 1);

    private static final VoxelShape SHAPE = Shapes.or(
        Block.box(0, 10, 0, 16, 14, 16),
        Block.box(0, 0, 0, 2, 10, 2),
        Block.box(14, 0, 0, 16, 10, 2),
        Block.box(0, 0, 14, 2, 10, 16),
        Block.box(14, 0, 14, 16, 10, 16)
    );

    public SifterBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(CONTENT, Material.EMPTY).setValue(STAGE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(CONTENT, STAGE);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType type) {
        return false;
    }

    @Override
    protected InteractionResult useItemOn(ItemStack itemStack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (state.getValue(CONTENT) != Material.EMPTY) {
            // Already loaded: any click works the sieve, whatever is in hand.
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }

        Material material = Material.byItem(itemStack);
        if (material == null) {
            return super.useItemOn(itemStack, state, level, pos, player, hand, hitResult);
        }

        if (!level.isClientSide()) {
            level.setBlock(pos, state.setValue(CONTENT, material).setValue(STAGE, 0), Block.UPDATE_ALL);
            level.playSound(null, pos, material.block().defaultBlockState().getSoundType().getPlaceSound(), SoundSource.BLOCKS, 1.0F, 1.0F);
            itemStack.consume(1, player);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        Material material = state.getValue(CONTENT);
        if (material == Material.EMPTY) {
            return InteractionResult.PASS;
        }

        if (level instanceof ServerLevel serverLevel) {
            int stage = state.getValue(STAGE) + 1;
            serverLevel.playSound(null, pos, material.sound(), SoundSource.BLOCKS, 1.0F, 0.9F + serverLevel.getRandom().nextFloat() * 0.2F);
            BlockParticleOption dust = new BlockParticleOption(ParticleTypes.FALLING_DUST, material.block().defaultBlockState());
            serverLevel.sendParticles(dust, pos.getX() + 0.5, pos.getY() + 0.6, pos.getZ() + 0.5, 6, 0.3, 0.05, 0.3, 0.0);
            player.causeFoodExhaustion(0.05F);

            if (stage >= SIFTS) {
                this.siftOut(serverLevel, pos, player, material);
                serverLevel.setBlock(pos, state.setValue(CONTENT, Material.EMPTY).setValue(STAGE, 0), Block.UPDATE_ALL);
            } else {
                serverLevel.setBlock(pos, state.setValue(STAGE, stage), Block.UPDATE_ALL);
            }
        }
        return InteractionResult.SUCCESS;
    }

    private void siftOut(ServerLevel level, BlockPos pos, Player player, Material material) {
        LootTable table = level.getServer().reloadableRegistries().getLootTable(material.lootTable());
        LootParams params = new LootParams.Builder(level)
            .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
            .withParameter(LootContextParams.THIS_ENTITY, player)
            .withParameter(LootContextParams.TOOL, ItemStack.EMPTY)
            .create(LootContextParamSets.ARCHAEOLOGY);
        for (ItemStack stack : table.getRandomItems(params)) {
            Block.popResource(level, pos.above(), stack);
        }
    }

    public enum Material implements StringRepresentable {
        EMPTY("empty", Blocks.AIR, Items.AIR, SoundEvents.BRUSH_GENERIC),
        GRAVEL("gravel", Blocks.GRAVEL, Items.GRAVEL, SoundEvents.BRUSH_GRAVEL),
        SAND("sand", Blocks.SAND, Items.SAND, SoundEvents.BRUSH_SAND),
        RED_SAND("red_sand", Blocks.RED_SAND, Items.RED_SAND, SoundEvents.BRUSH_SAND),
        MUD("mud", Blocks.MUD, Items.MUD, SoundEvents.BRUSH_GENERIC),
        DIRT("dirt", Blocks.DIRT, Items.DIRT, SoundEvents.BRUSH_GENERIC);

        private final String name;
        private final Block block;
        private final Item item;
        private final SoundEvent sound;

        Material(String name, Block block, Item item, SoundEvent sound) {
            this.name = name;
            this.block = block;
            this.item = item;
            this.sound = sound;
        }

        public Block block() {
            return this.block;
        }

        public SoundEvent sound() {
            return this.sound;
        }

        public ResourceKey<LootTable> lootTable() {
            return lootTable(this);
        }

        public static ResourceKey<LootTable> lootTable(Material material) {
            return ResourceKey.create(Registries.LOOT_TABLE, Constants.id("gameplay/sifting/" + material.name));
        }

        public static @Nullable Material byItem(ItemStack stack) {
            for (Material material : values()) {
                if (material != EMPTY && stack.is(material.item)) {
                    return material;
                }
            }
            return null;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }
}
