package com.coolerpromc.ancientcreature.datagen.loot;

import com.coolerpromc.ancientcreature.block.custom.SifterBlock;
import com.coolerpromc.ancientcreature.item.FossilPart;
import com.coolerpromc.ancientcreature.item.ModItems;
import com.coolerpromc.ancientcreature.loot.FossilLootInjections;
import com.coolerpromc.ancientcreature.registry.ModRegistries;
import com.coolerpromc.ancientcreature.worldgen.structure.DigSiteType;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.UniformContainerBase;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders;

import java.util.List;
import java.util.Map;

import static com.coolerpromc.ancientcreature.item.FossilPart.*;

/**
 * Loot rolled with the archaeology parameters: brushing dig sites, brushing vanilla ruins (via
 * {@link FossilLootInjections}) and working a sifter.
 */
public record ModArchaeologyLootSubProvider(LootTableSubProvider.Context context) implements LootTableSubProvider {
    @Override
    public void run() {
        HolderGetter<FossilPart> parts = this.context.lookup(ModRegistries.FOSSIL_PART);

        for (DigSiteType type : DigSiteType.values()) {
            this.addDigSite(parts, type);
        }

        for (SifterBlock.Material material : SifterBlock.Material.values()) {
            if (material != SifterBlock.Material.EMPTY) {
                this.addSifting(parts, material);
            }
        }

        this.addRuin(FossilLootInjections.ARCHAEOLOGY_DESERT, 0.08f, FossilLoot.fossil(parts, List.of(CLAW, TOOTH, SKULL, LIMB, RIB)));
        this.addRuin(FossilLootInjections.ARCHAEOLOGY_TRAIL_RUINS, 0.05f, FossilLoot.fossil(parts, List.of(RIB, VERTEBRA, LIMB, TOOTH, CLAW, AMBER)));
        this.addRuin(FossilLootInjections.ARCHAEOLOGY_TRAIL_RUINS_RARE, 0.12f, FossilLoot.fossil(parts, List.of(SKULL, EGG, AMBER)));
        this.addRuin(FossilLootInjections.ARCHAEOLOGY_OCEAN_RUIN, 0.06f, FossilLoot.fossil(parts, List.of(TOOTH, VERTEBRA, RIB, SKULL), FossilLoot.MARINE, 0.0f));
    }

    /** Roughly one brushed block in six holds a fossil, with the mix of parts set by the biome. */
    private void addDigSite(HolderGetter<FossilPart> parts, DigSiteType type) {
        LootPool.Builder pool = LootPool.lootPool().setRolls(ContextIntProviders.exactly(1));
        FossilLoot.addWeightedFossils(pool, parts, FossilLoot.digSiteParts(type), FossilLoot.digSiteSpecies(type), FossilLoot.digSiteBonus(type));
        pool.add(LootItem.lootTableItem(ModItems.EGG_SHELL_FRAGMENT).setWeight(8))
            .add(LootItem.lootTableItem(ModItems.ROCK_FRAGMENT).setWeight(12))
            .add(LootItem.lootTableItem(ModItems.DIRT_FRAGMENT).setWeight(10))
            .add(LootItem.lootTableItem(Items.BONE).setWeight(12))
            .add(LootItem.lootTableItem(Items.COAL).setWeight(8))
            .add(LootItem.lootTableItem(Items.STICK).setWeight(8))
            .add(EmptyLootItem.emptyItem().setWeight(6));
        for (Map.Entry<Item, Integer> flavour : flavour(type).entrySet()) {
            pool.add(LootItem.lootTableItem(flavour.getKey()).setWeight(flavour.getValue()));
        }
        this.context.accept(type.brushingLoot(), LootTable.lootTable().withPool(pool));
    }

    /** Odds and ends that say where the site is. */
    private static Map<Item, Integer> flavour(DigSiteType type) {
        return switch (type) {
            case PLAINS -> FossilLoot.ordered(Items.FLINT, 4, Items.WHEAT_SEEDS, 4);
            case ARID -> FossilLoot.ordered(Items.GOLD_NUGGET, 4, Items.DEAD_BUSH, 4);
            case SAVANNA -> FossilLoot.ordered(Items.FLINT, 4, Items.BONE_MEAL, 4);
            case BADLANDS -> FossilLoot.ordered(Items.GOLD_NUGGET, 6, Items.FLINT, 4);
            case JUNGLE -> FossilLoot.ordered(Items.COCOA_BEANS, 4, Items.BAMBOO, 4);
            case COLD -> FossilLoot.ordered(Items.SNOWBALL, 4, Items.FLINT, 4);
            case SWAMP -> FossilLoot.ordered(Items.CLAY_BALL, 5, Items.SLIME_BALL, 2);
            case HIGHLAND -> FossilLoot.ordered(Items.FLINT, 5, Items.EMERALD, 1);
            case COASTAL -> FossilLoot.ordered(Items.PRISMARINE_SHARD, 3, Items.KELP, 4);
        };
    }

    /** About one load in thirty gives up a fossil; the rest is rubble and the odd useful scrap. */
    private void addSifting(HolderGetter<FossilPart> parts, SifterBlock.Material material) {
        LootPool.Builder pool = LootPool.lootPool().setRolls(ContextIntProviders.exactly(1))
            .add(FossilLoot.fossil(parts, List.of(CLAW, TOOTH, RIB, LIMB, VERTEBRA)).setWeight(3))
            .add(LootItem.lootTableItem(ModItems.EGG_SHELL_FRAGMENT).setWeight(4))
            .add(LootItem.lootTableItem(ModItems.ROCK_FRAGMENT).setWeight(15))
            .add(LootItem.lootTableItem(Items.BONE_MEAL).setWeight(6))
            .add(EmptyLootItem.emptyItem().setWeight(40));
        switch (material) {
            case GRAVEL -> pool.add(LootItem.lootTableItem(Items.FLINT).setWeight(25)).add(LootItem.lootTableItem(Items.IRON_NUGGET).setWeight(4)).add(LootItem.lootTableItem(ModItems.DIRT_FRAGMENT).setWeight(6));
            case SAND -> pool.add(LootItem.lootTableItem(Items.FLINT).setWeight(8)).add(LootItem.lootTableItem(Items.GOLD_NUGGET).setWeight(3)).add(LootItem.lootTableItem(Items.CACTUS).setWeight(2));
            case RED_SAND -> pool.add(LootItem.lootTableItem(Items.FLINT).setWeight(8)).add(LootItem.lootTableItem(Items.GOLD_NUGGET).setWeight(6));
            case MUD -> pool.add(LootItem.lootTableItem(Items.CLAY_BALL).setWeight(15)).add(LootItem.lootTableItem(ModItems.DIRT_FRAGMENT).setWeight(10));
            case DIRT -> pool.add(LootItem.lootTableItem(ModItems.DIRT_FRAGMENT).setWeight(20)).add(LootItem.lootTableItem(Items.WHEAT_SEEDS).setWeight(5)).add(LootItem.lootTableItem(Items.BEETROOT_SEEDS).setWeight(3));
            default -> {
            }
        }
        this.context.accept(material.lootTable(), LootTable.lootTable().withPool(pool));
    }

    /** Swapped in for a vanilla ruin's brushing loot when the chance comes up. */
    private void addRuin(ResourceKey<LootTable> key, float chance, UniformContainerBase.Builder<?> fossil) {
        this.context.accept(key, LootTable.lootTable().withPool(LootPool.lootPool()
            .setRolls(ContextIntProviders.exactly(1))
            .when(LootItemRandomChanceCondition.randomChance(chance))
            .add(fossil)));
    }
}
