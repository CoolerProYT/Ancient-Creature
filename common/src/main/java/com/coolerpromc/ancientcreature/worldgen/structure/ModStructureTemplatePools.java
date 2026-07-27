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
    public static final ResourceKey<StructureTemplatePool> ARID_DIG_SITE = register("arid_dig_site");
    public static final ResourceKey<StructureTemplatePool> SAVANNA_DIG_SITE = register("savanna_dig_site");
    public static final ResourceKey<StructureTemplatePool> BADLANDS_DIG_SITE = register("badlands_dig_site");
    public static final ResourceKey<StructureTemplatePool> JUNGLE_DIG_SITE = register("jungle_dig_site");
    public static final ResourceKey<StructureTemplatePool> COLD_DIG_SITE = register("cold_dig_site");
    public static final ResourceKey<StructureTemplatePool> SWAMP_DIG_SITE = register("swamp_dig_site");
    public static final ResourceKey<StructureTemplatePool> HIGHLAND_DIG_SITE = register("highland_dig_site");
    public static final ResourceKey<StructureTemplatePool> COASTAL_DIG_SITE = register("coastal_dig_site");

    public static void boostrap(BootstrapContext<StructureTemplatePool> context){
        context.register(PLAINS_DIG_SITE, digSite(context, "plains_dig_site"));
        context.register(ARID_DIG_SITE, digSite(context, "arid_dig_site"));
        context.register(SAVANNA_DIG_SITE, digSite(context, "savanna_dig_site"));
        context.register(BADLANDS_DIG_SITE, digSite(context, "badlands_dig_site"));
        context.register(JUNGLE_DIG_SITE, digSite(context, "jungle_dig_site"));
        context.register(COLD_DIG_SITE, digSite(context, "cold_dig_site"));
        context.register(SWAMP_DIG_SITE, digSite(context, "swamp_dig_site"));
        context.register(HIGHLAND_DIG_SITE, digSite(context, "highland_dig_site"));
        context.register(COASTAL_DIG_SITE, digSite(context, "coastal_dig_site"));
    }

    private static StructureTemplatePool digSite(BootstrapContext<StructureTemplatePool> context, String location){
        HolderGetter<StructureTemplatePool> pools = context.lookup(Registries.TEMPLATE_POOL);
        HolderGetter<StructureProcessorList> processorLists = context.lookup(Registries.PROCESSOR_LIST);
        Holder<StructureTemplatePool> emptyPool = pools.getOrThrow(Pools.EMPTY);
        Holder<StructureProcessorList> emptyProcessorList = processorLists.getOrThrow(ProcessorLists.EMPTY);

        return new StructureTemplatePool(emptyPool, List.of(
            Pair.of(StructurePoolElement.single(Constants.id(location).toString(), emptyProcessorList).apply(StructureTemplatePool.Projection.RIGID), 1)
        ));
    }

    private static ResourceKey<StructureTemplatePool> register(String location){
        return ResourceKey.create(Registries.TEMPLATE_POOL, Constants.id(location));
    }
}
