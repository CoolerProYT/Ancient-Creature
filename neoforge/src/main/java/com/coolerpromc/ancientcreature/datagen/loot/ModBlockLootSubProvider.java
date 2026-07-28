package com.coolerpromc.ancientcreature.datagen.loot;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.block.ModBlocks;
import com.coolerpromc.ancientcreature.block.custom.RockPileBlock;
import com.coolerpromc.ancientcreature.item.ModItems;
import net.minecraft.advancements.criterion.StatePropertiesPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.Set;

public class ModBlockLootSubProvider extends BlockLootSubProvider {
    public ModBlockLootSubProvider(HolderLookup.Provider provider) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), provider);
    }

    @Override
    protected void generate() {
        this.add(ModBlocks.FOSSIL_ORE.get(), b -> createFossilDrop(b, FossilDropEntry.of(ModItems.RIB_FOSSIL_FRAGMENT.asItem()), FossilDropEntry.of(Items.DIRT)));
        this.add(ModBlocks.ROCK_PILE.get(), block -> LootTable.lootTable()
            .withPool(
                LootPool.lootPool()
                    .add(LootItem.lootTableItem(Items.COBBLESTONE))
            )
            .withPool(
                LootPool.lootPool()
                    .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(RockPileBlock.HAS_EGG, true)))
                    .add(LootItem.lootTableItem(ModItems.EGG_FOSSIL.get()))
            )
        );
    }

    private LootTable.Builder createFossilDrop(Block block, FossilDropEntry... drops) {
        HolderLookup.RegistryLookup<Enchantment> enchantments = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
        Holder<Enchantment> fortune = enchantments.getOrThrow(Enchantments.FORTUNE);
        LootPool.Builder fossilPool = LootPool.lootPool().setRolls(ConstantValue.exactly(1));

        for (FossilDropEntry drop : drops) {
            LootPoolSingletonContainer.Builder<?> entry = LootItem.lootTableItem(drop.drop()).setWeight(drop.weight()).apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)));

            if (drop.affectedByFortune()) {
                entry.apply(ApplyBonusCount.addUniformBonusCount(fortune, 1));
            }

            this.applyExplosionDecay(block, entry);
            fossilPool.add(entry);
        }

        LootTable.Builder fossilTable = LootTable.lootTable().withPool(fossilPool);

        return this.createSilkTouchDispatchTable(block, NestedLootTable.inlineLootTable(fossilTable.build()));
    }


    @Override
    protected Iterable<Block> getKnownBlocks() {
        return BuiltInRegistries.BLOCK.stream().filter(b -> b.builtInRegistryHolder().key().identifier().getNamespace().equals(Constants.MODID)).toList();
    }

    private record FossilDropEntry(Item drop, int weight, boolean affectedByFortune) {
        public static FossilDropEntry of(Item drop) {
            return of(drop, 30);
        }

        public static FossilDropEntry of(Item drop, int weight) {
            return of(drop, weight, true);
        }

        public static FossilDropEntry of(Item drop, int weight, boolean affectedByFortune) {
            return new FossilDropEntry(drop, weight, affectedByFortune);
        }
    }
}
