package com.coolerpromc.ancientcreature.worldgen.structure;

import com.coolerpromc.ancientcreature.Constants;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.storage.loot.LootTable;

/** The biome flavours of the dig site; each has its own buildings, loot and excavation pits. */
public enum DigSiteType implements StringRepresentable {
    PLAINS("plains", false),
    ARID("arid", true),
    SAVANNA("savanna", false),
    BADLANDS("badlands", false),
    JUNGLE("jungle", false),
    COLD("cold", false),
    SWAMP("swamp", false),
    HIGHLAND("highland", false),
    COASTAL("coastal", true);

    private final String name;
    private final boolean sandy;

    DigSiteType(String name, boolean sandy) {
        this.name = name;
        this.sandy = sandy;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    /** Sites dug into sand brush suspicious sand; every other site brushes suspicious gravel. */
    public boolean sandy() {
        return this.sandy;
    }

    public Block looseBlock() {
        return this.sandy ? Blocks.SAND : Blocks.GRAVEL;
    }

    public Block suspiciousBlock() {
        return this.sandy ? Blocks.SUSPICIOUS_SAND : Blocks.SUSPICIOUS_GRAVEL;
    }

    public ResourceKey<LootTable> brushingLoot() {
        return ResourceKey.create(Registries.LOOT_TABLE, Constants.id("archaeology/" + this.name + "_dig_site"));
    }

    public ResourceKey<LootTable> chestLoot() {
        return ResourceKey.create(Registries.LOOT_TABLE, Constants.id("chests/" + this.name + "_dig_site"));
    }

    public ResourceKey<StructureProcessorList> archaeologyProcessors() {
        return ResourceKey.create(Registries.PROCESSOR_LIST, Constants.id("dig_site/" + this.name + "_archaeology"));
    }

    public ResourceKey<StructureTemplatePool> startPool() {
        return ResourceKey.create(Registries.TEMPLATE_POOL, Constants.id(this.name + "_dig_site"));
    }

    public ResourceKey<StructureTemplatePool> pitPool() {
        return ResourceKey.create(Registries.TEMPLATE_POOL, Constants.id("dig_site/" + this.name + "/pits"));
    }

    /** Template ids: {@code <type>_dig_site} (the camp), {@code dig_site/<type>/abandoned_camp} and {@code dig_site/<type>/<pit>}. */
    public String campTemplate() {
        return Constants.id(this.name + "_dig_site").toString();
    }

    public String abandonedCampTemplate() {
        return Constants.id("dig_site/" + this.name + "/abandoned_camp").toString();
    }

    public String pitTemplate(String pit) {
        return Constants.id("dig_site/" + this.name + "/" + pit).toString();
    }
}
