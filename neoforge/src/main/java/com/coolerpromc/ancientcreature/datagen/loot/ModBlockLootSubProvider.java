package com.coolerpromc.ancientcreature.datagen.loot;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.block.ModBlocks;
import com.coolerpromc.ancientcreature.block.custom.RockPileBlock;
import com.coolerpromc.ancientcreature.item.ModItems;
import com.coolerpromc.ancientcreature.loot.custom.SetFossilCompletenessFunction;
import com.coolerpromc.ancientcreature.loot.custom.SetFossilPartFunction;
import com.coolerpromc.ancientcreature.loot.custom.SetFossilSpeciesFunction;
import net.minecraft.advancements.criterion.StatePropertiesPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.Set;

public class ModBlockLootSubProvider extends BlockLootSubProvider {
    public ModBlockLootSubProvider(HolderLookup.Provider provider) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), provider);
    }

    @Override
    protected void generate() {
        this.add(ModBlocks.FOSSIL_ORE.getBlock(), this::createFossilDrop);
        this.add(ModBlocks.ROCK_PILE.getBlock(), this::createRockPileDrop);
        this.dropSelf(ModBlocks.FOSSIL_CLEANING_TABLE.getBlock());
        this.dropSelf(ModBlocks.FOSSIL_IDENTIFICATION_CHAMBER.getBlock());
        this.dropSelf(ModBlocks.DNA_EXTRACTOR.getBlock());
        this.dropSelf(ModBlocks.GENOME_SEQUENCER.getBlock());
        this.dropSelf(ModBlocks.EMBRYOGENESIS_CHAMBER.getBlock());
        this.dropSelf(ModBlocks.INCUBATOR.getBlock());
    }

    private LootTable.Builder createFossilDrop(Block block) {
        LootTable.Builder fossilTable = LootTable.lootTable().withPool(
            LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                .add(LootItem.lootTableItem(ModItems.FOSSIL_FRAGMENT).apply(SetFossilCompletenessFunction.setCompleteness(UniformGenerator.between(0.01f, 0.3f))).apply(SetFossilPartFunction.setPart()).apply(SetFossilSpeciesFunction.setSpecies()))
        );

        return this.createSilkTouchDispatchTable(block, NestedLootTable.inlineLootTable(fossilTable.build()));
    }

    private LootTable.Builder createRockPileDrop(Block block){
        return LootTable.lootTable()
            .withPool(
                LootPool.lootPool()
                    .add(LootItem.lootTableItem(ModItems.ROCK_FRAGMENT).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 4))))
            )
            .withPool(
                LootPool.lootPool()
                    .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(RockPileBlock.HAS_EGG, true)))
                    .add(LootItem.lootTableItem(ModItems.EGG_FOSSIL.get()).apply(SetFossilCompletenessFunction.setCompleteness(UniformGenerator.between(0.7f, 0.9f))).apply(SetFossilSpeciesFunction.setSpecies()))
            )
            .withPool(
                LootPool.lootPool()
                    .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(RockPileBlock.HAS_FOSSIL, true)))
                    .add(LootItem.lootTableItem(ModItems.FOSSIL_FRAGMENT).apply(SetFossilCompletenessFunction.setCompleteness(UniformGenerator.between(0.01f, 0.2f))).apply(SetFossilPartFunction.setPart()).apply(SetFossilSpeciesFunction.setSpecies()))
            );
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return BuiltInRegistries.BLOCK.stream().filter(b -> b.builtInRegistryHolder().key().identifier().getNamespace().equals(Constants.MODID)).toList();
    }
}
