package com.coolerpromc.ancientcreature.worldgen.feature.cusom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviders;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * A fossil seam: a thin horizontal band of fossil-bearing rock showing on a cliff face.
 *
 * <p>Starting from the surface, it looks a few blocks down for rock that is open to the air on
 * one side, then lays a band along that face. Bands that would stay hidden inside the hill are
 * dropped, so the seams only ever appear where a player can spot them.
 */
public record FossilSeamFeature(Holder<BlockStateProvider> state, TagKey<Block> replaceable, IntProvider length, int maxDepth, int minExposed) implements Feature {
    public static final MapCodec<FossilSeamFeature> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        BlockStateProvider.CODEC.fieldOf("state_provider").forGetter(FossilSeamFeature::state),
        TagKey.hashedCodec(Registries.BLOCK).fieldOf("replaceable").forGetter(FossilSeamFeature::replaceable),
        IntProviders.codec(1, 16).fieldOf("length").forGetter(FossilSeamFeature::length),
        Codec.intRange(1, 48).fieldOf("max_depth").forGetter(FossilSeamFeature::maxDepth),
        Codec.intRange(1, 16).fieldOf("min_exposed").forGetter(FossilSeamFeature::minExposed)
    ).apply(instance, FossilSeamFeature::new));

    @Override
    public MapCodec<FossilSeamFeature> codec() {
        return CODEC;
    }

    @Override
    public boolean place(WorldGenLevel level, ChunkGenerator generator, RandomSource random, BlockPos origin) {
        BlockPos.MutableBlockPos cursor = origin.mutable();
        for (int depth = 1; depth <= this.maxDepth; depth++) {
            cursor.move(Direction.DOWN);
            BlockState state = level.getBlockState(cursor);
            if (!state.is(this.replaceable)) {
                continue;
            }
            Direction face = this.openFace(level, cursor);
            if (face != null) {
                return this.placeBand(level, random, cursor.immutable(), face.getClockWise());
            }
        }
        return false;
    }

    private boolean placeBand(WorldGenLevel level, RandomSource random, BlockPos anchor, Direction along) {
        int length = this.length.sample(random);
        int start = -length / 2;
        boolean thick = random.nextInt(3) == 0;
        List<BlockPos> band = new ArrayList<>();
        int exposed = 0;

        for (int i = start; i < start + length; i++) {
            // Real seams wander a little: step up or down now and then.
            int drift = random.nextInt(5) == 0 ? (random.nextBoolean() ? 1 : -1) : 0;
            BlockPos pos = anchor.relative(along, i).above(drift);
            for (int row = 0; row < (thick ? 2 : 1); row++) {
                BlockPos target = pos.below(row);
                if (level.getBlockState(target).is(this.replaceable)) {
                    band.add(target);
                    if (this.openFace(level, target) != null) {
                        exposed++;
                    }
                }
            }
        }

        if (exposed < this.minExposed) {
            return false;
        }
        for (BlockPos pos : band) {
            level.setBlock(pos, this.state.value().getState(level, random, pos), Block.UPDATE_CLIENTS);
        }
        return true;
    }

    private Direction openFace(WorldGenLevel level, BlockPos pos) {
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            if (level.getBlockState(pos.relative(direction)).isAir()) {
                return direction;
            }
        }
        return null;
    }
}
