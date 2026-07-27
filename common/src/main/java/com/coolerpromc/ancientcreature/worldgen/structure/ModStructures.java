package com.coolerpromc.ancientcreature.worldgen.structure;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.tag.ModBiomeTags;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
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

    public static void boostrap(BootstrapContext<Structure> context){
        context.register(PLAINS_DIG_SITE, plainsDigSite(context));
    }

    private static Structure plainsDigSite(BootstrapContext<Structure> context){
        HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);
        HolderGetter<StructureTemplatePool> pools = context.lookup(Registries.TEMPLATE_POOL);

        HolderSet<Biome> biome = biomes.getOrThrow(ModBiomeTags.HAS_PLAINS_DIG_SITE);
        Holder<StructureTemplatePool> pool = pools.getOrThrow(ModStructureTemplatePools.PLAINS_DIG_SITE);

        return new JigsawStructure(new Structure.StructureSettings(biome, Map.of(), GenerationStep.Decoration.SURFACE_STRUCTURES, TerrainAdjustment.NONE), pool, Optional.empty(), 1, ConstantHeight.of(VerticalAnchor.absolute(-6)), false, Optional.of(Heightmap.Types.OCEAN_FLOOR_WG), new JigsawStructure.MaxDistance(116, 256), List.of(), DimensionPadding.ZERO, LiquidSettings.IGNORE_WATERLOGGING);
    }

    private static ResourceKey<Structure> register(String location){
        return ResourceKey.create(Registries.STRUCTURE, Constants.id(location));
    }
}
