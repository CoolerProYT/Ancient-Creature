package com.coolerpromc.ancientcreature.worldgen.structure;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.tag.ModBiomeTags;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.heightproviders.ConstantHeight;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.pools.DimensionPadding;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ModStructures {
    public static final ResourceKey<Structure> PLAINS_DIG_SITE = register("plains_dig_site");
    public static final ResourceKey<Structure> ARID_DIG_SITE = register("arid_dig_site");
    public static final ResourceKey<Structure> SAVANNA_DIG_SITE = register("savanna_dig_site");
    public static final ResourceKey<Structure> BADLANDS_DIG_SITE = register("badlands_dig_site");
    public static final ResourceKey<Structure> JUNGLE_DIG_SITE = register("jungle_dig_site");
    public static final ResourceKey<Structure> COLD_DIG_SITE = register("cold_dig_site");
    public static final ResourceKey<Structure> SWAMP_DIG_SITE = register("swamp_dig_site");
    public static final ResourceKey<Structure> HIGHLAND_DIG_SITE = register("highland_dig_site");
    public static final ResourceKey<Structure> COASTAL_DIG_SITE = register("coastal_dig_site");

    public static void boostrap(BootstrapContext<Structure> context){
        context.register(PLAINS_DIG_SITE, digSite(context, ModBiomeTags.HAS_PLAINS_DIG_SITE, ModStructureTemplatePools.PLAINS_DIG_SITE));
        context.register(ARID_DIG_SITE, digSite(context, ModBiomeTags.HAS_ARID_DIG_SITE, ModStructureTemplatePools.ARID_DIG_SITE));
        context.register(SAVANNA_DIG_SITE, digSite(context, ModBiomeTags.HAS_SAVANNA_DIG_SITE, ModStructureTemplatePools.SAVANNA_DIG_SITE));
        context.register(BADLANDS_DIG_SITE, digSite(context, ModBiomeTags.HAS_BADLANDS_DIG_SITE, ModStructureTemplatePools.BADLANDS_DIG_SITE));
        context.register(JUNGLE_DIG_SITE, digSite(context, ModBiomeTags.HAS_JUNGLE_DIG_SITE, ModStructureTemplatePools.JUNGLE_DIG_SITE));
        context.register(COLD_DIG_SITE, digSite(context, ModBiomeTags.HAS_COLD_DIG_SITE, ModStructureTemplatePools.COLD_DIG_SITE));
        context.register(SWAMP_DIG_SITE, digSite(context, ModBiomeTags.HAS_SWAMP_DIG_SITE, ModStructureTemplatePools.SWAMP_DIG_SITE));
        context.register(HIGHLAND_DIG_SITE, digSite(context, ModBiomeTags.HAS_HIGHLAND_DIG_SITE, ModStructureTemplatePools.HIGHLAND_DIG_SITE));
        context.register(COASTAL_DIG_SITE, digSite(context, ModBiomeTags.HAS_COASTAL_DIG_SITE, ModStructureTemplatePools.COASTAL_DIG_SITE));
    }

    private static Structure digSite(BootstrapContext<Structure> context, TagKey<Biome> biomeTag, ResourceKey<StructureTemplatePool> poolKey){
        HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);
        HolderGetter<StructureTemplatePool> pools = context.lookup(Registries.TEMPLATE_POOL);

        HolderSet<Biome> biome = biomes.getOrThrow(biomeTag);
        Holder<StructureTemplatePool> pool = pools.getOrThrow(poolKey);

        return new FlatJigsawStructure(new Structure.StructureSettings(biome, Map.of(), GenerationStep.Decoration.SURFACE_STRUCTURES, TerrainAdjustment.NONE), pool, Optional.empty(), 1, ConstantHeight.of(VerticalAnchor.absolute(-6)), false, Optional.of(Heightmap.Types.OCEAN_FLOOR_WG), new JigsawStructure.MaxDistance(116, 256), List.of(), DimensionPadding.ZERO, LiquidSettings.IGNORE_WATERLOGGING);
    }

    private static ResourceKey<Structure> register(String location){
        return ResourceKey.create(Registries.STRUCTURE, Constants.id(location));
    }
}
