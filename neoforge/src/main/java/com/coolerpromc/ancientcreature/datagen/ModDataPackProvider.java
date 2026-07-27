package com.coolerpromc.ancientcreature.datagen;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.worldgen.structure.ModStructureSets;
import com.coolerpromc.ancientcreature.worldgen.structure.ModStructureTemplatePools;
import com.coolerpromc.ancientcreature.worldgen.structure.ModStructures;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ModDataPackProvider extends DatapackBuiltinEntriesProvider {
    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
        .add(Registries.TEMPLATE_POOL, ModStructureTemplatePools::boostrap)
        .add(Registries.STRUCTURE, ModStructures::boostrap)
        .add(Registries.STRUCTURE_SET, ModStructureSets::boostrap);

    public ModDataPackProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, BUILDER, Set.of(Constants.MODID));
    }
}
