package com.coolerpromc.ancientcreature.datagen.loot;

import java.util.function.BiConsumer;
import net.minecraft.core.HolderLookup;
import com.coolerpromc.ancientcreature.item.FossilPart;
import com.coolerpromc.ancientcreature.loot.FossilLootInjections;
import com.coolerpromc.ancientcreature.loot.ModLootTables;
import com.coolerpromc.ancientcreature.registry.ModRegistries;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.SetComponentsFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceWithEnchantedBonusCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.List;

import static com.coolerpromc.ancientcreature.item.FossilPart.*;

/** Loot tables that are rolled with fishing, mob-drop and equipment parameters. */
public final class ModInjectLootSubProviders {
    private ModInjectLootSubProviders() {
    }

    /** About one catch in a hundred is a fossil washed out of a riverbank or sea floor, or a lump of amber. */
    public static final class Fishing implements LootTableSubProvider {
        private final HolderLookup.Provider registries;
        private SubContext context;

        public Fishing(HolderLookup.Provider registries) {
            this.registries = registries;
        }

        @Override
        public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
            this.context = new SubContext(this.registries, output);
            this.run();
        }

        private void run() {
            HolderGetter<FossilPart> parts = this.context.lookup(ModRegistries.FOSSIL_PART);
            this.context.accept(FossilLootInjections.FISHING, LootTable.lootTable().withPool(LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .when(LootItemRandomChanceCondition.randomChance(0.01f))
                .add(FossilLoot.fossil(parts, List.of(TOOTH, VERTEBRA, RIB, SKULL)).setWeight(3))
                .add(FossilLoot.fossil(parts, List.of(AMBER)).setWeight(1))));
        }
    }

    /** Husks and drowned sometimes carry a fossil they picked up in the sand or silt. */
    public static final class Entities implements LootTableSubProvider {
        private final HolderLookup.Provider registries;
        private SubContext context;

        public Entities(HolderLookup.Provider registries) {
            this.registries = registries;
        }

        @Override
        public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
            this.context = new SubContext(this.registries, output);
            this.run();
        }

        private void run() {
            HolderGetter<FossilPart> parts = this.context.lookup(ModRegistries.FOSSIL_PART);
            this.mobFossil(FossilLootInjections.HUSK, FossilLoot.fossil(parts, List.of(CLAW, TOOTH, RIB, LIMB)));
            this.mobFossil(FossilLootInjections.DROWNED, FossilLoot.fossil(parts, List.of(TOOTH, VERTEBRA, RIB)));
        }

        private void mobFossil(ResourceKey<LootTable> key, LootPoolSingletonContainer.Builder<?> fossil) {
            HolderGetter<Enchantment> enchantments = this.context.lookup(Registries.ENCHANTMENT);
            this.context.accept(key, LootTable.lootTable().withPool(LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .when(LootItemKilledByPlayerCondition.killedByPlayer())
                .when(LootItemRandomChanceWithEnchantedBonusCondition.randomChanceAndLootingBoost(this.registries, 0.025f, 0.01f))
                .add(fossil)));
        }
    }

    /** The undead crew of an abandoned dig site: a field hat and a brush still in hand. */
    public static final class Equipment implements LootTableSubProvider {
        private final HolderLookup.Provider registries;
        private SubContext context;

        public Equipment(HolderLookup.Provider registries) {
            this.registries = registries;
        }

        @Override
        public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
            this.context = new SubContext(this.registries, output);
            this.run();
        }

        private static final int FIELD_HAT_BROWN = 0x8B5A2B;

        private void run() {
            this.context.accept(ModLootTables.ARCHAEOLOGIST_EQUIPMENT, LootTable.lootTable()
                .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                    .add(LootItem.lootTableItem(Items.LEATHER_HELMET).apply(SetComponentsFunction.setComponent(DataComponents.DYED_COLOR, new DyedItemColor(FIELD_HAT_BROWN)))))
                .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                    .add(LootItem.lootTableItem(Items.BRUSH))));
        }
    }
}
