package com.coolerpromc.ancientcreature.worldgen.structure;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ModStructureTemplatePools {
    public static final ResourceKey<StructureTemplatePool> PLAINS_DIG_SITE = DigSiteType.PLAINS.startPool();
    public static final ResourceKey<StructureTemplatePool> ARID_DIG_SITE = DigSiteType.ARID.startPool();
    public static final ResourceKey<StructureTemplatePool> SAVANNA_DIG_SITE = DigSiteType.SAVANNA.startPool();
    public static final ResourceKey<StructureTemplatePool> BADLANDS_DIG_SITE = DigSiteType.BADLANDS.startPool();
    public static final ResourceKey<StructureTemplatePool> JUNGLE_DIG_SITE = DigSiteType.JUNGLE.startPool();
    public static final ResourceKey<StructureTemplatePool> COLD_DIG_SITE = DigSiteType.COLD.startPool();
    public static final ResourceKey<StructureTemplatePool> SWAMP_DIG_SITE = DigSiteType.SWAMP.startPool();
    public static final ResourceKey<StructureTemplatePool> HIGHLAND_DIG_SITE = DigSiteType.HIGHLAND.startPool();
    public static final ResourceKey<StructureTemplatePool> COASTAL_DIG_SITE = DigSiteType.COASTAL.startPool();

    /** The excavation pits that can open up beside a camp, with their relative weights. */
    private static final List<Pair<String, Integer>> PITS = List.of(
        Pair.of("trench", 3),
        Pair.of("quarry", 2),
        Pair.of("skeleton", 2),
        Pair.of("sinkhole", 1)
    );
    /** Weight of "no pit here" for each of the camp's three connectors. */
    private static final int NO_PIT_WEIGHT = 3;
    private static final int CAMP_WEIGHT = 7;
    private static final int ABANDONED_CAMP_WEIGHT = 1;

    public static void boostrap(BootstrapContext<StructureTemplatePool> context){
        HolderGetter<StructureTemplatePool> pools = context.lookup(Registries.TEMPLATE_POOL);
        HolderGetter<StructureProcessorList> processorLists = context.lookup(Registries.PROCESSOR_LIST);
        Holder<StructureTemplatePool> emptyPool = pools.getOrThrow(Pools.EMPTY);

        for (DigSiteType type : DigSiteType.values()) {
            Holder<StructureProcessorList> archaeology = processorLists.getOrThrow(type.archaeologyProcessors());

            // A working camp most of the time; now and then one that was overrun and left to rot.
            context.register(type.startPool(), new StructureTemplatePool(emptyPool, List.of(
                Pair.of(StructurePoolElement.single(type.campTemplate(), archaeology).apply(StructureTemplatePool.Projection.RIGID), CAMP_WEIGHT),
                Pair.of(StructurePoolElement.single(type.abandonedCampTemplate(), archaeology).apply(StructureTemplatePool.Projection.RIGID), ABANDONED_CAMP_WEIGHT)
            )));

            List<Pair<Function<StructureTemplatePool.Projection, ? extends StructurePoolElement>, Integer>> pits = new ArrayList<>();
            for (Pair<String, Integer> pit : PITS) {
                pits.add(Pair.of(StructurePoolElement.single(type.pitTemplate(pit.getFirst()), archaeology), pit.getSecond()));
            }
            pits.add(Pair.of(StructurePoolElement.empty(), NO_PIT_WEIGHT));
            context.register(type.pitPool(), new StructureTemplatePool(emptyPool, pits, StructureTemplatePool.Projection.RIGID));
        }
    }
}
