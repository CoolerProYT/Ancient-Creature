package com.coolerpromc.ancientcreature.datagen.loot;

import java.util.function.BiConsumer;
import net.minecraft.core.HolderLookup;
import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.block.ModBlocks;
import com.coolerpromc.ancientcreature.block.custom.RockPileBlock;
import com.coolerpromc.ancientcreature.entity.Species;
import com.coolerpromc.ancientcreature.item.FossilPart;
import net.minecraft.resources.ResourceKey;
import com.coolerpromc.ancientcreature.item.ModItems;
import com.coolerpromc.ancientcreature.loot.custom.SetFossilDataFunction;
import com.coolerpromc.ancientcreature.registry.ModRegistries;
import net.minecraft.advancements.criterion.StatePropertiesPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableSubProvider;
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

import java.util.List;
import java.util.Set;

public class ModBlockLootSubProvider extends BlockLootSubProvider {
    public ModBlockLootSubProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        this.add(ModBlocks.FOSSIL_ORE.getBlock(), block -> this.createFossilDrop(block, FossilPart.BONE_PARTS, List.of(), 0.0f));
        // Deep, undisturbed rock: the rarer parts, and better preserved.
        this.add(ModBlocks.DEEPSLATE_FOSSIL_ORE.getBlock(), block -> this.createFossilDrop(block, List.of(FossilPart.SKULL, FossilPart.VERTEBRA, FossilPart.TOOTH, FossilPart.EGG), List.of(), 0.15f));
        this.add(ModBlocks.AMBER_ORE.getBlock(), block -> this.createFossilDrop(block, List.of(FossilPart.AMBER), List.of(), 0.0f));
        // Ice keeps a carcass almost whole; the ice-age animals are the ones it caught.
        this.add(ModBlocks.FROZEN_FOSSIL.getBlock(), block -> this.createFossilDrop(block, List.of(FossilPart.SKULL, FossilPart.VERTEBRA, FossilPart.TOOTH, FossilPart.LIMB, FossilPart.RIB), FossilLoot.ICE_AGE, 0.25f));
        this.dropSelf(ModBlocks.SIFTER.getBlock());
        this.add(ModBlocks.ROCK_PILE.getBlock(), this::createRockPileDrop);
        this.dropSelf(ModBlocks.FOSSIL_CLEANING_TABLE.getBlock());
        this.dropSelf(ModBlocks.FOSSIL_IDENTIFICATION_CHAMBER.getBlock());
        this.dropSelf(ModBlocks.DNA_EXTRACTOR.getBlock());
        this.dropSelf(ModBlocks.GENOME_SEQUENCER.getBlock());
        this.dropSelf(ModBlocks.EMBRYOGENESIS_CHAMBER.getBlock());
        this.dropSelf(ModBlocks.INCUBATOR.getBlock());
        this.dropSelf(ModBlocks.REINFORCED_FENCE.getBlock());
        this.dropSelf(ModBlocks.REINFORCED_FENCE_GATE.getBlock());
        this.dropSelf(ModBlocks.ELECTRIC_FENCE.getBlock());
    }

    private LootTable.Builder createFossilDrop(Block block, List<ResourceKey<FossilPart>> parts, List<Species> species, float bonus) {
        LootTable.Builder fossilTable = LootTable.lootTable().withPool(
            LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                .add(FossilLoot.fossil(this.registries.lookupOrThrow(ModRegistries.FOSSIL_PART), parts, species, bonus))
        );

        return this.createSilkTouchDispatchTable(block, NestedLootTable.inlineLootTable(fossilTable.build()));
    }

    private LootTable.Builder createRockPileDrop(Block block){
        HolderGetter<FossilPart> fossilParts = this.registries.lookupOrThrow(ModRegistries.FOSSIL_PART);

        return LootTable.lootTable()
            .withPool(
                LootPool.lootPool()
                    .add(LootItem.lootTableItem(ModItems.ROCK_FRAGMENT).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 4))))
            )
            .withPool(
                LootPool.lootPool()
                    .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(RockPileBlock.HAS_EGG, true)))
                    .add(LootItem.lootTableItem(ModItems.FOSSIL_PART).apply(SetFossilDataFunction.setData(List.of(fossilParts.getOrThrow(FossilPart.EGG)))))
            )
            .withPool(
                LootPool.lootPool()
                    .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(RockPileBlock.HAS_FOSSIL, true)))
                    .add(FossilLoot.fossil(fossilParts, FossilPart.BONE_PARTS))
            );
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return BuiltInRegistries.BLOCK.stream().filter(b -> b.builtInRegistryHolder().key().identifier().getNamespace().equals(Constants.MODID)).toList();
    }
}
