package com.coolerpromc.ancientcreature.worldgen.structure.custom;

import com.coolerpromc.ancientcreature.worldgen.structure.ModStructureTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.PiecesContainer;
import net.minecraft.world.level.levelgen.structure.pools.DimensionPadding;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasBinding;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasLookup;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;

import java.util.List;
import java.util.Optional;

/**
 * A jigsaw structure that only generates on a dry, nearly level footprint.
 */
public class FlatJigsawStructure extends Structure {
    private static final int FOUNDATION_HEIGHT = 6;
    private static final int MAX_DYNAMIC_SUPPORT_DEPTH = 16;

    public static final MapCodec<FlatJigsawStructure> CODEC = RecordCodecBuilder.mapCodec(instance ->
        instance.group(
            settingsCodec(instance),
            StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter(structure -> structure.startPool),
            Identifier.CODEC.optionalFieldOf("start_jigsaw_name").forGetter(structure -> structure.startJigsawName),
            Codec.intRange(0, 20).fieldOf("size").forGetter(structure -> structure.maxDepth),
            HeightProvider.CODEC.fieldOf("start_height").forGetter(structure -> structure.startHeight),
            Codec.BOOL.fieldOf("use_expansion_hack").forGetter(structure -> structure.useExpansionHack),
            Heightmap.Types.CODEC.optionalFieldOf("project_start_to_heightmap").forGetter(structure -> structure.projectStartToHeightmap),
            JigsawStructure.MaxDistance.CODEC.fieldOf("max_distance_from_center").forGetter(structure -> structure.maxDistanceFromCenter),
            Codec.list(PoolAliasBinding.CODEC).optionalFieldOf("pool_aliases", List.of()).forGetter(structure -> structure.poolAliases),
            DimensionPadding.CODEC.optionalFieldOf("dimension_padding", DimensionPadding.ZERO).forGetter(structure -> structure.dimensionPadding),
            LiquidSettings.CODEC.optionalFieldOf("liquid_settings", LiquidSettings.APPLY_WATERLOGGING).forGetter(structure -> structure.liquidSettings)
        ).apply(instance, FlatJigsawStructure::new)
    );

    private final Holder<StructureTemplatePool> startPool;
    private final Optional<Identifier> startJigsawName;
    private final int maxDepth;
    private final HeightProvider startHeight;
    private final boolean useExpansionHack;
    private final Optional<Heightmap.Types> projectStartToHeightmap;
    private final JigsawStructure.MaxDistance maxDistanceFromCenter;
    private final List<PoolAliasBinding> poolAliases;
    private final DimensionPadding dimensionPadding;
    private final LiquidSettings liquidSettings;

    public FlatJigsawStructure(
        StructureSettings settings,
        Holder<StructureTemplatePool> startPool,
        Optional<Identifier> startJigsawName,
        int maxDepth,
        HeightProvider startHeight,
        boolean useExpansionHack,
        Optional<Heightmap.Types> projectStartToHeightmap,
        JigsawStructure.MaxDistance maxDistanceFromCenter,
        List<PoolAliasBinding> poolAliases,
        DimensionPadding dimensionPadding,
        LiquidSettings liquidSettings
    ) {
        super(settings);
        this.startPool = startPool;
        this.startJigsawName = startJigsawName;
        this.maxDepth = maxDepth;
        this.startHeight = startHeight;
        this.useExpansionHack = useExpansionHack;
        this.projectStartToHeightmap = projectStartToHeightmap;
        this.maxDistanceFromCenter = maxDistanceFromCenter;
        this.poolAliases = poolAliases;
        this.dimensionPadding = dimensionPadding;
        this.liquidSettings = liquidSettings;
    }

    @Override
    protected Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        ChunkPos chunkPos = context.chunkPos();
        int height = this.startHeight.sample(
            context.random(),
            new WorldGenerationContext(context.chunkGenerator(), context.heightAccessor())
        );
        BlockPos startPos = new BlockPos(chunkPos.getMinBlockX(), height, chunkPos.getMinBlockZ());

        return JigsawPlacement.addPieces(
            context,
            this.startPool,
            this.startJigsawName,
            this.maxDepth,
            startPos,
            this.useExpansionHack,
            this.projectStartToHeightmap,
            this.maxDistanceFromCenter,
            PoolAliasLookup.create(this.poolAliases, startPos, context.seed()),
            this.dimensionPadding,
            this.liquidSettings
        );
    }

    @Override
    public void afterPlace(
        WorldGenLevel level,
        StructureManager structureManager,
        ChunkGenerator generator,
        RandomSource random,
        BoundingBox chunkBounds,
        ChunkPos chunkPos,
        PiecesContainer pieces
    ) {
        BoundingBox structureBounds = pieces.calculateBoundingBox();
        int foundationY = structureBounds.minY();
        int minimumX = Math.max(structureBounds.minX(), chunkBounds.minX());
        int maximumX = Math.min(structureBounds.maxX(), chunkBounds.maxX());
        int minimumZ = Math.max(structureBounds.minZ(), chunkBounds.minZ());
        int maximumZ = Math.min(structureBounds.maxZ(), chunkBounds.maxZ());
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();

        for (int x = minimumX; x <= maximumX; x++) {
            for (int z = minimumZ; z <= maximumZ; z++) {
                BlockState foundation = level.getBlockState(cursor.set(x, foundationY, z));
                if (!isFoundationColumn(level, cursor, foundation)) {
                    continue;
                }

                int minimumSupportY = Math.max(level.getMinY(), foundationY - MAX_DYNAMIC_SUPPORT_DEPTH);
                for (int y = foundationY - 1; y >= minimumSupportY; y--) {
                    BlockState existing = level.getBlockState(cursor.set(x, y, z));
                    if (!existing.isAir() && existing.getFluidState().isEmpty() && !existing.canBeReplaced()) {
                        break;
                    }
                    level.setBlock(cursor, foundation, 2);
                }
            }
        }
    }

    private static boolean isFoundationColumn(
        WorldGenLevel level,
        BlockPos.MutableBlockPos cursor,
        BlockState foundation
    ) {
        if (foundation.isAir() || !foundation.getFluidState().isEmpty()) {
            return false;
        }

        int x = cursor.getX();
        int foundationY = cursor.getY();
        int z = cursor.getZ();
        for (int offset = 1; offset < FOUNDATION_HEIGHT; offset++) {
            if (!level.getBlockState(cursor.set(x, foundationY + offset, z)).is(foundation.getBlock())) {
                return false;
            }
        }

        return !level.getBlockState(cursor.set(x, foundationY + FOUNDATION_HEIGHT, z)).isAir();
    }

    @Override
    public StructureType<?> type() {
        return ModStructureTypes.FLAT_JIGSAW.get();
    }
}
