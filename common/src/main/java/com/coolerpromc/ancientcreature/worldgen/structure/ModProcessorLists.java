package com.coolerpromc.ancientcreature.worldgen.structure;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.block.ModBlocks;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.templatesystem.*;
import net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity.AppendLoot;

import java.util.List;

public final class ModProcessorLists {
    /** The bones of a buried fossil bed, a few already turned to fossil ore. */
    public static final ResourceKey<StructureProcessorList> FOSSIL_BED_BONES = create("fossil_bed_bones");
    /** The mineral overlay of a buried fossil bed: fossil ore instead of vanilla's coal. */
    public static final ResourceKey<StructureProcessorList> FOSSIL_BED_ORE = create("fossil_bed_ore");

    private ModProcessorLists() {
    }

    public static void bootstrap(BootstrapContext<StructureProcessorList> context) {
        HolderSet<Block> cannotReplace = context.lookup(Registries.BLOCK).getOrThrow(BlockTags.FEATURES_CANNOT_REPLACE);

        context.register(FOSSIL_BED_BONES, new StructureProcessorList(List.of(
            new BlockRotProcessor(0.9F),
            new RuleProcessor(List.of(new ProcessorRule(new RandomBlockMatchTest(Blocks.BONE_BLOCK, 0.15F), AlwaysTrueTest.INSTANCE, ModBlocks.FOSSIL_ORE.getBlock().defaultBlockState()))),
            new ProtectedBlockProcessor(cannotReplace)
        )));
        context.register(FOSSIL_BED_ORE, new StructureProcessorList(List.of(
            new BlockRotProcessor(0.25F),
            new RuleProcessor(List.of(new ProcessorRule(new BlockMatchTest(Blocks.COAL_ORE), AlwaysTrueTest.INSTANCE, ModBlocks.FOSSIL_ORE.getBlock().defaultBlockState()))),
            new ProtectedBlockProcessor(cannotReplace)
        )));

        // Each dig site piece turns a handful of its loose gravel or sand into brushable blocks,
        // so no two sites hide their finds in the same places.
        for (DigSiteType type : DigSiteType.values()) {
            context.register(type.archaeologyProcessors(), new StructureProcessorList(List.of(
                new CappedProcessor(
                    new RuleProcessor(List.of(new ProcessorRule(
                        new BlockMatchTest(type.looseBlock()),
                        AlwaysTrueTest.INSTANCE,
                        PosAlwaysTrueTest.INSTANCE,
                        type.suspiciousBlock().defaultBlockState(),
                        new AppendLoot(type.brushingLoot())
                    ))),
                    UniformInt.of(4, 8)
                )
            )));
        }
    }

    private static ResourceKey<StructureProcessorList> create(String name) {
        return ResourceKey.create(Registries.PROCESSOR_LIST, Constants.id(name));
    }
}
