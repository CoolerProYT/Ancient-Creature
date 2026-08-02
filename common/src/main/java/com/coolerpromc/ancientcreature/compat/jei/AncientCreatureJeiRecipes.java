package com.coolerpromc.ancientcreature.compat.jei;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.block.ModBlocks;
import com.coolerpromc.ancientcreature.data.component.ModDataComponents;
import com.coolerpromc.ancientcreature.data.component.custom.DNAIntegrityLevel;
import com.coolerpromc.ancientcreature.data.component.custom.FossilCompleteness;
import com.coolerpromc.ancientcreature.data.component.custom.FossilPart;
import com.coolerpromc.ancientcreature.data.component.custom.GenomeCompleteness;
import com.coolerpromc.ancientcreature.entity.Species;
import com.coolerpromc.ancientcreature.item.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class AncientCreatureJeiRecipes {
    private static final List<ChiselData> CHISELS = List.of(
        new ChiselData(ModItems.STONE_CHISEL.get(), 0.40f, "stone"),
        new ChiselData(ModItems.COPPER_CHISEL.get(), 0.35f, "copper"),
        new ChiselData(ModItems.IRON_CHISEL.get(), 0.25f, "iron"),
        new ChiselData(ModItems.GOLDEN_CHISEL.get(), 0.50f, "golden"),
        new ChiselData(ModItems.DIAMOND_CHISEL.get(), 0.10f, "diamond"),
        new ChiselData(ModItems.NETHERITE_CHISEL.get(), 0.0f, "netherite")
    );

    private AncientCreatureJeiRecipes() {
    }

    public static List<AncientCreatureJeiRecipe> fossilHunting() {
        List<AncientCreatureJeiRecipe> recipes = new ArrayList<>();
        for (Species species : Species.values()) {
            for (ChiselData chisel : CHISELS) {
                float min = 0.01f * (1.0f - chisel.damageRate());
                float max = 0.30f * (1.0f - chisel.damageRate());
                recipes.add(recipe(
                    "fossil_hunting/ore/" + species.getSerializedName() + "/" + chisel.id(),
                    List.of(slot(ModBlocks.FOSSIL_ORE.getBlock().asItem().getDefaultInstance()), slot(chisel.item().getDefaultInstance())),
                    List.of(fossilAlternatives(species, List.of(FossilPart.values()), min, max)),
                    20,
                    Component.translatable("jei.ancientcreature.method.chiseling"),
                    biomeNote(species),
                    speciesNote(species),
                    partsNote(List.of(FossilPart.values())),
                    Component.translatable("jei.ancientcreature.completeness_range", percent(min), percent(max)),
                    Component.translatable("jei.ancientcreature.fortune_bonus")
                ));

                float rockMin = 0.01f * (1.0f - chisel.damageRate());
                float rockMax = 0.20f * (1.0f - chisel.damageRate());
                recipes.add(recipe(
                    "fossil_hunting/rock_pile/fragment/" + species.getSerializedName() + "/" + chisel.id(),
                    List.of(slot(ModBlocks.ROCK_PILE.getBlock().asItem().getDefaultInstance()), slot(chisel.item().getDefaultInstance())),
                    List.of(fossilAlternatives(species, List.of(FossilPart.values()), rockMin, rockMax)),
                    20,
                    Component.translatable("jei.ancientcreature.method.rock_pile"),
                    biomeNote(species),
                    speciesNote(species),
                    partsNote(List.of(FossilPart.values())),
                    Component.translatable("jei.ancientcreature.completeness_range", percent(rockMin), percent(rockMax)),
                    Component.translatable("jei.ancientcreature.rock_pile_fossil_condition"),
                    Component.translatable("jei.ancientcreature.fortune_bonus")
                ));

                float eggMin = 0.70f * (1.0f - chisel.damageRate());
                float eggMax = 0.90f * (1.0f - chisel.damageRate());
                recipes.add(recipe(
                    "fossil_hunting/rock_pile/egg/" + species.getSerializedName() + "/" + chisel.id(),
                    List.of(slot(ModBlocks.ROCK_PILE.getBlock().asItem().getDefaultInstance()), slot(chisel.item().getDefaultInstance())),
                    List.of(slot(eggFossil(species, eggMin, true, false), eggFossil(species, eggMax, true, false))),
                    20,
                    Component.translatable("jei.ancientcreature.method.rock_pile"),
                    biomeNote(species),
                    speciesNote(species),
                    Component.translatable("jei.ancientcreature.parts_egg_only"),
                    Component.translatable("jei.ancientcreature.completeness_range", percent(eggMin), percent(eggMax)),
                    Component.translatable("jei.ancientcreature.rock_pile_egg_condition"),
                    Component.translatable("jei.ancientcreature.fortune_bonus")
                ));
            }

            List<FossilPart> archaeologyParts = List.of(FossilPart.CLAW, FossilPart.TOOTH, FossilPart.RIB, FossilPart.LIMB);
            recipes.add(recipe(
                "fossil_hunting/archaeology/" + species.getSerializedName(),
                List.of(slot(Items.SUSPICIOUS_GRAVEL.getDefaultInstance(), Items.SUSPICIOUS_SAND.getDefaultInstance()), slot(Items.BRUSH.getDefaultInstance())),
                List.of(fossilAlternatives(species, archaeologyParts, 0.10f, 0.25f)),
                20,
                Component.translatable("jei.ancientcreature.method.brushing"),
                biomeNote(species),
                speciesNote(species),
                partsNote(archaeologyParts),
                Component.translatable("jei.ancientcreature.completeness_range", 10, 25),
                Component.translatable("jei.ancientcreature.archaeology_chance")
            ));
        }
        return recipes;
    }

    public static List<AncientCreatureJeiRecipe> fossilCleaning() {
        List<AncientCreatureJeiRecipe> recipes = new ArrayList<>();
        for (Species species : Species.values()) {
            for (FossilPart part : FossilPart.values()) {
                ItemStack input = fossil(species, part, 0.50f, true, false);
                ItemStack output = fossil(species, part, 0.50f, false, false);
                recipes.add(recipe("fossil_cleaning/" + species.getSerializedName() + "/" + part.getSerializedName(),
                    List.of(slot(Items.BRUSH.getDefaultInstance()), slot(input)), List.of(slot(output), slot(new ItemStack(ModItems.DIRT_FRAGMENT.get(), 2))), 100,
                    Component.translatable("jei.ancientcreature.cleaning_preserves"),
                    Component.translatable("jei.ancientcreature.dirt_output")));
            }
            ItemStack input = eggFossil(species, 0.80f, true, false);
            ItemStack output = eggFossil(species, 0.80f, false, false);
            recipes.add(recipe("fossil_cleaning/" + species.getSerializedName() + "/egg",
                List.of(slot(Items.BRUSH.getDefaultInstance()), slot(input)), List.of(slot(output), slot(new ItemStack(ModItems.DIRT_FRAGMENT.get(), 2))), 100,
                Component.translatable("jei.ancientcreature.cleaning_preserves"),
                Component.translatable("jei.ancientcreature.dirt_output")));
        }
        return recipes;
    }

    public static List<AncientCreatureJeiRecipe> fossilIdentification() {
        List<AncientCreatureJeiRecipe> recipes = new ArrayList<>();
        for (Species species : Species.values()) {
            for (FossilPart part : FossilPart.values()) {
                ItemStack input = fossil(species, part, 0.50f, false, false);
                ItemStack success = input.copy();
                success.set(ModDataComponents.IDENTIFIED.get(), true);
                ItemStack failure = input.copy();
                failure.set(ModDataComponents.IDENTIFICATION_FAILED.get(), true);
                failure.set(ModDataComponents.FOSSIL_COMPLETENESS.get(), new FossilCompleteness(0.50f * (1.0f - species.getFossilDamageRate())));
                recipes.add(identificationRecipe(species, part.getSerializedName(), input, success, failure));
            }
            ItemStack input = eggFossil(species, 0.80f, false, false);
            ItemStack success = input.copy();
            success.set(ModDataComponents.IDENTIFIED.get(), true);
            ItemStack failure = input.copy();
            failure.set(ModDataComponents.IDENTIFICATION_FAILED.get(), true);
            failure.set(ModDataComponents.FOSSIL_COMPLETENESS.get(), new FossilCompleteness(0.80f * (1.0f - species.getFossilDamageRate())));
            recipes.add(identificationRecipe(species, "egg", input, success, failure));
        }
        return recipes;
    }

    public static List<AncientCreatureJeiRecipe> dnaExtraction() {
        List<AncientCreatureJeiRecipe> recipes = new ArrayList<>();
        for (Species species : Species.values()) {
            for (FossilPart part : FossilPart.values()) {
                for (DNAIntegrityLevel integrity : DNAIntegrityLevel.values()) {
                    float completeness = representativeCompleteness(part, integrity);
                    if (DNAIntegrityLevel.byScore(Math.max(0.01f, completeness + part.getDnaExtractingBonus())) != integrity) continue;
                    ItemStack fossil = fossil(species, part, completeness, false, true);
                    recipes.add(recipe("dna_extraction/" + species.getSerializedName() + "/" + part.getSerializedName() + "/" + integrity.getSerializedName(),
                        List.of(slot(ModItems.EXTRACTION_FLUID.toStack()), slot(fossil), slot(ModItems.SAMPLE_VIAL.toStack())),
                        List.of(slot(dnaSample(species, integrity))), 100,
                        Component.translatable("jei.ancientcreature.dna_formula"),
                        Component.translatable("jei.ancientcreature.integrity_result", Component.translatable("dna.ancientcreature." + integrity.getSerializedName())),
                        Component.translatable("jei.ancientcreature.extraction_consumption")));
                }
            }
            for (DNAIntegrityLevel integrity : List.of(DNAIntegrityLevel.STABLE, DNAIntegrityLevel.PRESERVED_EMBRYO)) {
                float completeness = integrity == DNAIntegrityLevel.STABLE ? 0.45f : 0.80f;
                ItemStack egg = eggFossil(species, completeness, false, true);
                recipes.add(recipe("dna_extraction/" + species.getSerializedName() + "/egg/" + integrity.getSerializedName(),
                    List.of(slot(ModItems.EXTRACTION_FLUID.toStack()), slot(egg), slot(ModItems.SAMPLE_VIAL.toStack())),
                    List.of(slot(dnaSample(species, integrity))), 100,
                    Component.translatable("jei.ancientcreature.dna_formula"),
                    Component.translatable("jei.ancientcreature.egg_limb_bonus"),
                    Component.translatable("jei.ancientcreature.integrity_result", Component.translatable("dna.ancientcreature." + integrity.getSerializedName())),
                    Component.translatable("jei.ancientcreature.extraction_consumption")));
            }
        }
        return recipes;
    }

    public static List<AncientCreatureJeiRecipe> genomeSequencing() {
        List<AncientCreatureJeiRecipe> recipes = new ArrayList<>();
        for (Species species : Species.values()) {
            for (DNAIntegrityLevel integrity : DNAIntegrityLevel.values()) {
                int[] range = genomeRange(integrity);
                ItemStack dna = dnaSample(species, integrity);
                recipes.add(recipe("genome_sequencing/blank/" + species.getSerializedName() + "/" + integrity.getSerializedName(),
                    List.of(slot(dna), slot(ModItems.GENOME_CARTRIDGE_BLANK.toStack())),
                    List.of(slot(filledGenome(species, range[0] / 100f), filledGenome(species, range[1] / 100f))), 100,
                    Component.translatable("jei.ancientcreature.genome_added", range[0], range[1]),
                    Component.translatable("jei.ancientcreature.same_species")));

                ItemStack existing = filledGenome(species, 0.90f);
                List<ItemStack> outputs = new ArrayList<>();
                float min = Math.min(1f, 0.90f + range[0] / 100f);
                float max = Math.min(1f, 0.90f + range[1] / 100f);
                if (min < 1f) outputs.add(filledGenome(species, min));
                if (max < 1f) outputs.add(filledGenome(species, max));
                else outputs.add(completedGenome(species));
                recipes.add(recipe("genome_sequencing/filled/" + species.getSerializedName() + "/" + integrity.getSerializedName(),
                    List.of(slot(dna), slot(existing)), List.of(outputs), 100,
                    Component.translatable("jei.ancientcreature.genome_added", range[0], range[1]),
                    Component.translatable("jei.ancientcreature.genome_caps"),
                    Component.translatable("jei.ancientcreature.same_species")));
            }
        }
        return recipes;
    }

    public static List<AncientCreatureJeiRecipe> embryogenesis() {
        List<AncientCreatureJeiRecipe> recipes = new ArrayList<>();
        for (Species species : Species.values()) {
            ItemStack output = ModItems.FERTILIZED_ANCIENT_EGG.toStack();
            output.set(ModDataComponents.SPECIES.get(), species);
            recipes.add(recipe("embryogenesis/" + species.getSerializedName(),
                List.of(slot(ModItems.NUTRIENT_SOLUTION.toStack()), slot(ModItems.ARTIFICIAL_EGG.toStack()), slot(completedGenome(species))),
                List.of(slot(output)), 100));
        }
        return recipes;
    }

    public static List<AncientCreatureJeiRecipe> incubation() {
        List<AncientCreatureJeiRecipe> recipes = new ArrayList<>();
        for (Species species : Species.values()) {
            ItemStack input = ModItems.FERTILIZED_ANCIENT_EGG.toStack();
            input.set(ModDataComponents.SPECIES.get(), species);
            ItemStack output = ModItems.BABY_CREATURE_CAPSULE.toStack();
            output.set(ModDataComponents.SPECIES.get(), species);
            recipes.add(recipe("incubation/" + species.getSerializedName(), List.of(slot(input)), List.of(slot(output)), species.getIncubationTime(),
                Component.translatable("jei.ancientcreature.incubation_time", ticksToSeconds(species.getIncubationTime()))));
        }
        return recipes;
    }

    private static AncientCreatureJeiRecipe identificationRecipe(Species species, String suffix, ItemStack input, ItemStack success, ItemStack failure) {
        return recipe("fossil_identification/" + species.getSerializedName() + "/" + suffix,
            List.of(slot(input)), List.of(slot(success, failure)), 400,
            Component.translatable("jei.ancientcreature.identification_chance", percent(species.getIdentifyFailChance())),
            Component.translatable("jei.ancientcreature.identification_damage", percent(species.getFossilDamageRate())),
            Component.translatable("jei.ancientcreature.identification_time"));
    }

    private static AncientCreatureJeiRecipe recipe(String path, List<List<ItemStack>> inputs, List<List<ItemStack>> outputs, int ticks, Component... notes) {
        return new AncientCreatureJeiRecipe(Constants.id(path), inputs, outputs, List.of(notes), ticks);
    }

    private static List<ItemStack> slot(ItemStack... stacks) {
        return List.of(stacks);
    }

    private static List<ItemStack> fossilAlternatives(Species species, List<FossilPart> parts, float min, float max) {
        List<ItemStack> stacks = new ArrayList<>();
        for (FossilPart part : parts) {
            stacks.add(fossil(species, part, min, true, false));
            stacks.add(fossil(species, part, max, true, false));
        }
        return stacks;
    }

    private static ItemStack fossil(Species species, FossilPart part, float completeness, boolean dirty, boolean identified) {
        ItemStack stack = ModItems.FOSSIL_FRAGMENT.toStack();
        stack.set(ModDataComponents.SPECIES.get(), species);
        stack.set(ModDataComponents.FOSSIL_PART.get(), part);
        stack.set(ModDataComponents.FOSSIL_COMPLETENESS.get(), new FossilCompleteness(Math.clamp(completeness, 0f, 1f)));
        stack.set(ModDataComponents.IS_DIRTY.get(), dirty);
        stack.set(ModDataComponents.IDENTIFIED.get(), identified);
        return stack;
    }

    private static ItemStack eggFossil(Species species, float completeness, boolean dirty, boolean identified) {
        ItemStack stack = ModItems.EGG_FOSSIL.toStack();
        stack.set(ModDataComponents.SPECIES.get(), species);
        stack.set(ModDataComponents.FOSSIL_COMPLETENESS.get(), new FossilCompleteness(Math.clamp(completeness, 0f, 1f)));
        stack.set(ModDataComponents.IS_DIRTY.get(), dirty);
        stack.set(ModDataComponents.IDENTIFIED.get(), identified);
        return stack;
    }

    private static ItemStack dnaSample(Species species, DNAIntegrityLevel integrity) {
        ItemStack stack = ModItems.DNA_SAMPLE.toStack();
        stack.set(ModDataComponents.SPECIES.get(), species);
        stack.set(ModDataComponents.DNA_INTEGRITY_LEVEL.get(), integrity);
        return stack;
    }

    private static ItemStack filledGenome(Species species, float completeness) {
        ItemStack stack = ModItems.GENOME_CARTRIDGE_FILLED.toStack();
        stack.set(ModDataComponents.SPECIES.get(), species);
        stack.set(ModDataComponents.GENOME_COMPLETENESS.get(), new GenomeCompleteness(Math.clamp(completeness, 0f, 1f)));
        return stack;
    }

    private static ItemStack completedGenome(Species species) {
        ItemStack stack = ModItems.GENOME_CARTRIDGE_COMPLETED.toStack();
        stack.set(ModDataComponents.SPECIES.get(), species);
        return stack;
    }

    private static float representativeCompleteness(FossilPart part, DNAIntegrityLevel integrity) {
        float target = switch (integrity) {
            case DEGRADED -> 0.10f;
            case PARTIAL -> 0.225f;
            case STABLE -> 0.45f;
            case PRESERVED_EMBRYO -> 0.80f;
        };
        return Math.clamp(target - part.getDnaExtractingBonus(), 0f, 1f);
    }

    private static int[] genomeRange(DNAIntegrityLevel integrity) {
        return switch (integrity) {
            case DEGRADED -> new int[]{1, 4};
            case PARTIAL -> new int[]{4, 8};
            case STABLE -> new int[]{8, 15};
            case PRESERVED_EMBRYO -> new int[]{15, 30};
        };
    }

    private static Component biomeNote(Species species) {
        String biome = species == Species.MEGALODON ? "ocean" : "overworld";
        return Component.translatable("jei.ancientcreature.biomes", Component.translatable("jei.ancientcreature.biome." + biome));
    }

    private static Component speciesNote(Species species) {
        return Component.translatable("jei.ancientcreature.species_result", Component.translatable("species.ancientcreature." + species.getSerializedName()));
    }

    private static Component partsNote(List<FossilPart> parts) {
        String joined = parts.stream()
            .map(part -> Component.translatable("fossilPart.ancientcreature." + part.getSerializedName()).getString())
            .reduce((left, right) -> left + ", " + right)
            .orElse("");
        return Component.translatable("jei.ancientcreature.parts", joined);
    }

    private static int percent(float value) {
        return Math.round(value * 100f);
    }

    private static String ticksToSeconds(int ticks) {
        return String.format(Locale.ROOT, "%.1f", ticks / 20f);
    }

    private record ChiselData(Item item, float damageRate, String id) {
    }
}
