package com.coolerpromc.ancientcreature.datagen.advancement;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.block.ModBlocks;
import com.coolerpromc.ancientcreature.data.component.ModDataComponents;
import com.coolerpromc.ancientcreature.data.component.custom.FossilData;
import com.coolerpromc.ancientcreature.entity.Species;
import com.coolerpromc.ancientcreature.item.FossilPart;
import com.coolerpromc.ancientcreature.item.ModItems;
import com.coolerpromc.ancientcreature.tag.ModItemTags;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;

import java.util.function.Consumer;

public class ModProgressAdvancement implements AdvancementSubProvider {
    @Override
    public void generate(HolderLookup.Provider registries, Consumer<AdvancementHolder> consumer) {
        HolderGetter<Item> items = registries.lookupOrThrow(Registries.ITEM);

        AdvancementHolder root = Advancement.Builder.advancement()
            .display(
                ModItems.NETHERITE_CHISEL,
                title("root"),
                description("root"),
                Constants.id("gui/advancements/backgrounds/fossil"),
                AdvancementType.TASK, false, false, false
            )
            .addCriterion("crafting_table", InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.CRAFTING_TABLE))
            .save(consumer, Constants.id("progression/root"));

        AdvancementHolder craftChisel = itemAdvancement(
            root, ModItems.STONE_CHISEL, "craft_chisel", AdvancementType.TASK,
            "obtain_chisel",
            InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(items, ModItemTags.CHISELS).build()), consumer
        );

        AdvancementHolder obtainFossil = Advancement.Builder.advancement()
            .parent(craftChisel)
            .display(
                new ItemStackTemplate(ModItems.FOSSIL_FRAGMENT.get(), 1, DataComponentPatch.builder().set(ModDataComponents.FOSSIL_DATA.get(), FossilData.ofDefault(FossilPart.SKULL, Species.TRICERATOPS, 1f)).build()),
                title("obtain_fossil"),
                description("obtain_fossil"),
                null,
                AdvancementType.TASK, true, true, false
            )
            .addCriterion("fossil_fragment", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.FOSSIL_FRAGMENT))
            .addCriterion("egg_fossil", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.EGG_FOSSIL))
            .requirements(AdvancementRequirements.Strategy.OR)
            .save(consumer, Constants.id("progression/obtain_fossil"));

        AdvancementHolder cleaningTable = itemAdvancement(
            obtainFossil, ModBlocks.FOSSIL_CLEANING_TABLE.getItem(), "craft_cleaning_table", AdvancementType.TASK,
            "obtain_cleaning_table", InventoryChangeTrigger.TriggerInstance.hasItems(ModBlocks.FOSSIL_CLEANING_TABLE.getItem()), consumer
        );

        AdvancementHolder identificationChamber = itemAdvancement(
            cleaningTable, ModBlocks.FOSSIL_IDENTIFICATION_CHAMBER.getItem(), "craft_identification_chamber", AdvancementType.TASK,
            "obtain_identification_chamber", InventoryChangeTrigger.TriggerInstance.hasItems(ModBlocks.FOSSIL_IDENTIFICATION_CHAMBER.getItem()), consumer
        );

        AdvancementHolder dnaExtractor = itemAdvancement(
            identificationChamber, ModBlocks.DNA_EXTRACTOR.getItem(), "craft_dna_extractor", AdvancementType.TASK,
            "obtain_dna_extractor", InventoryChangeTrigger.TriggerInstance.hasItems(ModBlocks.DNA_EXTRACTOR.getItem()), consumer
        );

        AdvancementHolder extractDna = itemAdvancement(
            dnaExtractor, ModItems.DNA_SAMPLE, "extract_dna", AdvancementType.GOAL,
            "obtain_dna_sample", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.DNA_SAMPLE), consumer
        );

        AdvancementHolder genomeSequencer = itemAdvancement(
            extractDna, ModBlocks.GENOME_SEQUENCER.getItem(), "craft_genome_sequencer", AdvancementType.TASK,
            "obtain_genome_sequencer", InventoryChangeTrigger.TriggerInstance.hasItems(ModBlocks.GENOME_SEQUENCER.getItem()), consumer
        );

        AdvancementHolder completeGenome = itemAdvancement(
            genomeSequencer, ModItems.GENOME_CARTRIDGE_COMPLETED, "complete_genome", AdvancementType.GOAL,
            "obtain_completed_genome", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.GENOME_CARTRIDGE_COMPLETED), consumer
        );

        AdvancementHolder embryogenesisChamber = itemAdvancement(
            completeGenome, ModBlocks.EMBRYOGENESIS_CHAMBER.getItem(), "craft_embryogenesis_chamber", AdvancementType.TASK,
            "obtain_embryogenesis_chamber", InventoryChangeTrigger.TriggerInstance.hasItems(ModBlocks.EMBRYOGENESIS_CHAMBER.getItem()), consumer
        );

        AdvancementHolder fertilizedEgg = itemAdvancement(
            embryogenesisChamber, ModItems.FERTILIZED_ANCIENT_EGG, "create_fertilized_egg", AdvancementType.GOAL,
            "obtain_fertilized_egg", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.FERTILIZED_ANCIENT_EGG), consumer
        );

        AdvancementHolder incubator = itemAdvancement(
            fertilizedEgg, ModBlocks.INCUBATOR.getItem(), "craft_incubator", AdvancementType.TASK,
            "obtain_incubator", InventoryChangeTrigger.TriggerInstance.hasItems(ModBlocks.INCUBATOR.getItem()), consumer
        );

        itemAdvancement(
            incubator, ModItems.BABY_CREATURE_CAPSULE, "revive_creature", AdvancementType.CHALLENGE,
            "obtain_baby_creature", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.BABY_CREATURE_CAPSULE), consumer
        );
    }

    private static AdvancementHolder itemAdvancement(AdvancementHolder parent, ItemLike icon, String name, AdvancementType type, String criterionName, Criterion<?> criterion, Consumer<AdvancementHolder> consumer) {
        return Advancement.Builder.advancement()
            .parent(parent)
            .display(icon, title(name), description(name), null, type, true, true, false)
            .addCriterion(criterionName, criterion)
            .save(consumer, Constants.id("progression/" + name));
    }

    private static Component title(String name) {
        return Component.translatable("advancements.ancientcreature.progression." + name + ".title");
    }

    private static Component description(String name) {
        return Component.translatable("advancements.ancientcreature.progression." + name + ".description");
    }
}
