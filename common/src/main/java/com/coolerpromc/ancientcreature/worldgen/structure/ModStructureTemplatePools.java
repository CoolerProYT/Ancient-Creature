package com.coolerpromc.ancientcreature.worldgen.structure;

import com.coolerpromc.ancientcreature.Constants;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.data.worldgen.ProcessorLists;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;

import java.util.List;

public class ModStructureTemplatePools {
    public static final ResourceKey<StructureTemplatePool> PLAINS_DIG_SITE = register("plains_dig_site");

    public static void boostrap(BootstrapContext<StructureTemplatePool> context){
        context.register(PLAINS_DIG_SITE, plainsDigSite(context));
    }

    private static StructureTemplatePool plainsDigSite(BootstrapContext<StructureTemplatePool> context){
        HolderGetter<StructureTemplatePool> pools = context.lookup(Registries.TEMPLATE_POOL);
        HolderGetter<StructureProcessorList> processorLists = context.lookup(Registries.PROCESSOR_LIST);
        Holder<StructureTemplatePool> emptyPool = pools.getOrThrow(Pools.EMPTY);
        Holder<StructureProcessorList> emptyProcessorList = processorLists.getOrThrow(ProcessorLists.EMPTY);

        return new StructureTemplatePool(emptyPool, List.of(
            Pair.of(StructurePoolElement.single("ancientcreature:plains_dig_site", emptyProcessorList).apply(StructureTemplatePool.Projection.RIGID), 1)
        ));
    }

    private static ResourceKey<StructureTemplatePool> register(String location){
        return ResourceKey.create(Registries.TEMPLATE_POOL, Constants.id(location));
    }
}
