package com.coolerpromc.ancientcreature.datagen;

import com.coolerpromc.ancientcreature.Constants;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.data.recipes.SpecialRecipeBuilder;
import com.coolerpromc.ancientcreature.recipe.FossilReassemblyRecipe;
import com.coolerpromc.ancientcreature.block.ModBlocks;
import com.coolerpromc.ancientcreature.item.ModItems;
import net.minecraft.advancements.Advancement;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.tags.BlockItemTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.data.recipes.SmithingTransformRecipeBuilder;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.item.crafting.SmokingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;

public class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(BootstrapContext<Recipe<?>> output, BootstrapContext<Advancement> advancementOutput) {
        super(output, advancementOutput);
    }

    @Override
    protected void buildRecipes() {
        this.creatureMaterials();

        shapeless(RecipeCategory.MISC, ModItems.FIELD_GUIDE)
            .requires(Items.BOOK)
            .requires(Items.BONE)
            .unlockedBy(getHasName(Items.BONE), has(Items.BONE))
            .unlockedBy(getHasName(Items.BOOK), has(Items.BOOK))
            .save(output);

        shaped(RecipeCategory.DECORATIONS, ModBlocks.SIFTER.item())
            .pattern("P P")
            .pattern("###")
            .pattern("S S")
            .define('P', Ingredient.of(this.items.getOrThrow(ItemTags.PLANKS)))
            .define('#', Ingredient.of(Items.STRING))
            .define('S', Ingredient.of(Items.STICK))
            .unlockedBy(getHasName(Items.STRING), has(Items.STRING))
            .unlockedBy(getHasName(Items.GRAVEL), has(Items.GRAVEL))
            .save(output);

        SpecialRecipeBuilder.special(() -> FossilReassemblyRecipe.INSTANCE)
            .save(output, ResourceKey.create(Registries.RECIPE, Constants.id("fossil_reassembly")));

        chisel(ModItems.STONE_CHISEL, ItemTags.STONE_CRAFTING_MATERIALS);
        chisel(ModItems.COPPER_CHISEL, ItemTags.COPPER_TOOL_MATERIALS);
        chisel(ModItems.IRON_CHISEL, ItemTags.IRON_TOOL_MATERIALS);
        chisel(ModItems.GOLDEN_CHISEL, ItemTags.GOLD_TOOL_MATERIALS);
        chisel(ModItems.DIAMOND_CHISEL, ItemTags.DIAMOND_TOOL_MATERIALS);
        netheriteSmithing(ModItems.DIAMOND_CHISEL.get(), RecipeCategory.TOOLS, ModItems.NETHERITE_CHISEL.get());

        shaped(RecipeCategory.DECORATIONS, ModBlocks.FOSSIL_CLEANING_TABLE.item())
            .pattern("W W")
            .pattern("WBW")
            .pattern("SSS")
            .define('W', Ingredient.of(this.items.getOrThrow(ItemTags.PLANKS)))
            .define('B', Ingredient.of(Items.BRUSH))
            .define('S', Ingredient.of(this.items.getOrThrow(BlockItemTags.SLABS.item())))
            .unlockedBy(getHasName(Items.BRUSH), has(Items.BRUSH))
            .unlockedBy("has_planks", has(ItemTags.PLANKS))
            .unlockedBy("has_slabs", has(BlockItemTags.SLABS.item()))
            .save(output);

        shaped(RecipeCategory.DECORATIONS, ModBlocks.FOSSIL_IDENTIFICATION_CHAMBER.item())
            .pattern("PGP")
            .pattern("IBI")
            .pattern("III")
            .define('P', Ingredient.of(Items.GLASS_PANE))
            .define('G', Ingredient.of(Items.LAPIS_LAZULI))
            .define('I', Ingredient.of(Items.IRON_INGOT))
            .define('B', Ingredient.of(Items.BOOK))
            .unlockedBy(getHasName(Items.LAPIS_LAZULI), has(Items.LAPIS_LAZULI))
            .unlockedBy(getHasName(Items.IRON_INGOT), has(Items.IRON_INGOT))
            .unlockedBy(getHasName(ModBlocks.FOSSIL_CLEANING_TABLE.item()), has(ModBlocks.FOSSIL_CLEANING_TABLE.item()))
            .save(output);

        shaped(RecipeCategory.DECORATIONS, ModBlocks.DNA_EXTRACTOR.item())
            .pattern("IGI")
            .pattern("RER")
            .pattern("III")
            .define('I', Ingredient.of(Items.IRON_INGOT))
            .define('G', Ingredient.of(Items.GLASS))
            .define('R', Ingredient.of(Items.REDSTONE))
            .define('E', Ingredient.of(Items.FERMENTED_SPIDER_EYE))
            .unlockedBy(getHasName(Items.FERMENTED_SPIDER_EYE), has(Items.FERMENTED_SPIDER_EYE))
            .unlockedBy(getHasName(Items.REDSTONE), has(Items.REDSTONE))
            .unlockedBy(getHasName(ModBlocks.FOSSIL_IDENTIFICATION_CHAMBER.item()), has(ModBlocks.FOSSIL_IDENTIFICATION_CHAMBER.item()))
            .save(output);

        shaped(RecipeCategory.DECORATIONS, ModBlocks.GENOME_SEQUENCER.item())
            .pattern("CRC")
            .pattern("RLR")
            .pattern("CCC")
            .define('C', Ingredient.of(Items.COPPER_INGOT))
            .define('R', Ingredient.of(Items.REDSTONE))
            .define('L', Ingredient.of(Items.LAPIS_LAZULI))
            .unlockedBy(getHasName(Items.COPPER_INGOT), has(Items.COPPER_INGOT))
            .unlockedBy(getHasName(Items.LAPIS_LAZULI), has(Items.LAPIS_LAZULI))
            .unlockedBy(getHasName(ModBlocks.DNA_EXTRACTOR.item()), has(ModBlocks.DNA_EXTRACTOR.item()))
            .save(output);

        shaped(RecipeCategory.DECORATIONS, ModBlocks.EMBRYOGENESIS_CHAMBER.item())
            .pattern("GAG")
            .pattern("SCS")
            .pattern("GGG")
            .define('G', Ingredient.of(Items.GOLD_INGOT))
            .define('A', Ingredient.of(Items.AMETHYST_BLOCK))
            .define('S', Ingredient.of(Items.SLIME_BALL))
            .define('C', Ingredient.of(Items.GLASS))
            .unlockedBy(getHasName(Items.AMETHYST_BLOCK), has(Items.AMETHYST_BLOCK))
            .unlockedBy(getHasName(Items.SLIME_BALL), has(Items.SLIME_BALL))
            .unlockedBy(getHasName(ModBlocks.GENOME_SEQUENCER.item()), has(ModBlocks.GENOME_SEQUENCER.item()))
            .save(output);

        shaped(RecipeCategory.DECORATIONS, ModBlocks.INCUBATOR.item())
            .pattern("IMI")
            .pattern("MNM")
            .pattern("IGI")
            .define('I', Ingredient.of(Items.IRON_INGOT))
            .define('M', Ingredient.of(Items.MAGMA_BLOCK))
            .define('N', Ingredient.of(Items.NETHERITE_SCRAP))
            .define('G', Ingredient.of(Items.GLASS))
            .unlockedBy(getHasName(Items.NETHERITE_SCRAP), has(Items.NETHERITE_SCRAP))
            .unlockedBy(getHasName(Items.MAGMA_BLOCK), has(Items.MAGMA_BLOCK))
            .unlockedBy(getHasName(ModBlocks.EMBRYOGENESIS_CHAMBER.item()), has(ModBlocks.EMBRYOGENESIS_CHAMBER.item()))
            .save(output);

        shapeless(RecipeCategory.BUILDING_BLOCKS, Items.DIRT)
            .requires(ModItems.DIRT_FRAGMENT, 4)
            .unlockedBy(getHasName(ModItems.DIRT_FRAGMENT), has(ModItems.DIRT_FRAGMENT))
            .save(output);

        shapeless(RecipeCategory.BUILDING_BLOCKS, Items.COBBLESTONE)
            .requires(ModItems.ROCK_FRAGMENT, 4)
            .unlockedBy(getHasName(ModItems.ROCK_FRAGMENT), has(ModItems.ROCK_FRAGMENT))
            .save(output);

        shaped(RecipeCategory.MISC, ModItems.SAMPLE_VIAL)
            .pattern("G G")
            .pattern(" B ")
            .define('G', Ingredient.of(Items.GLASS))
            .define('B', Ingredient.of(Items.GLASS_BOTTLE))
            .unlockedBy(getHasName(Items.GLASS_BOTTLE), has(Items.GLASS_BOTTLE))
            .save(output);

        shaped(RecipeCategory.MISC, ModItems.GENOME_CARTRIDGE_BLANK)
            .pattern("CAC")
            .pattern("C C")
            .pattern("CCC")
            .define('C', Ingredient.of(Items.COPPER_INGOT))
            .define('A', Ingredient.of(Items.AMETHYST_SHARD))
            .unlockedBy(getHasName(Items.AMETHYST_SHARD), has(Items.AMETHYST_SHARD))
            .unlockedBy(getHasName(Items.COPPER_INGOT), has(Items.COPPER_INGOT))
            .save(output);

        shapeless(RecipeCategory.MISC, ModItems.ARTIFICIAL_EGG)
            .requires(ModItems.EGG_SHELL_FRAGMENT)
            .requires(ModItems.EGG_SHELL_FRAGMENT)
            .requires(ModItems.NUTRIENT_SOLUTION)
            .unlockedBy(getHasName(ModItems.EGG_SHELL_FRAGMENT), has(ModItems.EGG_SHELL_FRAGMENT))
            .unlockedBy(getHasName(ModItems.NUTRIENT_SOLUTION), has(ModItems.NUTRIENT_SOLUTION))
            .save(output);

        shaped(RecipeCategory.MISC, ModItems.EXTRACTION_FLUID)
            .pattern(" F ")
            .pattern("FPF")
            .pattern(" F ")
            .define('F', Items.POTION)
            .define('P', Items.FERMENTED_SPIDER_EYE)
            .unlockedBy(getHasName(Items.FERMENTED_SPIDER_EYE), has(Items.FERMENTED_SPIDER_EYE))
            .unlockedBy(getHasName(Items.POTION), has(Items.POTION))
            .save(output);

        shaped(RecipeCategory.MISC, ModItems.NUTRIENT_SOLUTION, 4)
            .pattern(" F ")
            .pattern("FPF")
            .pattern(" F ")
            .define('F', Items.POTION)
            .define('P', Items.BONE_MEAL)
            .unlockedBy(getHasName(Items.BONE_MEAL), has(Items.BONE_MEAL))
            .unlockedBy(getHasName(Items.POTION), has(Items.POTION))
            .save(output);
    }

    /** Vanilla uses for creature materials; equipment recipes that need them are defined with that equipment. */
    private void creatureMaterials() {
        cook(SmeltingRecipe::new, 200, "smelting");
        cook(SmokingRecipe::new, 100, "smoking");
        cook(CampfireCookingRecipe::new, 600, "campfire_cooking");

        shapeless(RecipeCategory.MISC, Items.LEATHER, 2)
            .requires(ModItems.THICK_HIDE)
            .unlockedBy(getHasName(ModItems.THICK_HIDE), has(ModItems.THICK_HIDE))
            .save(output, "ancientcreature:leather_from_thick_hide");
        shapeless(RecipeCategory.BUILDING_BLOCKS, Items.WOOL.white())
            .requires(ModItems.WOOLLY_FUR, 2)
            .unlockedBy(getHasName(ModItems.WOOLLY_FUR), has(ModItems.WOOLLY_FUR))
            .save(output, "ancientcreature:white_wool_from_woolly_fur");
        shapeless(RecipeCategory.MISC, Items.FEATHER, 2)
            .requires(ModItems.PREHISTORIC_FEATHER)
            .unlockedBy(getHasName(ModItems.PREHISTORIC_FEATHER), has(ModItems.PREHISTORIC_FEATHER))
            .save(output, "ancientcreature:feather_from_prehistoric_feather");
        shaped(RecipeCategory.TOOLS, ModItems.HORN_WHISTLE)
            .pattern("  S")
            .pattern(" G ")
            .pattern("H  ")
            .define('H', ModItems.PREHISTORIC_HORN)
            .define('G', Items.GOLD_NUGGET)
            .define('S', Items.STRING)
            .unlockedBy(getHasName(ModItems.PREHISTORIC_HORN), has(ModItems.PREHISTORIC_HORN))
            .save(output);
        // Lab upgrade modules: an iron frame around the effect's ingredient, wired with redstone.
        shaped(RecipeCategory.MISC, ModItems.SPEED_UPGRADE_MODULE)
            .pattern("IRI")
            .pattern("RGR")
            .pattern("IRI")
            .define('I', Items.IRON_INGOT)
            .define('R', Items.REDSTONE)
            .define('G', Items.GOLD_INGOT)
            .unlockedBy(getHasName(Items.REDSTONE), has(Items.REDSTONE))
            .save(output);
        shaped(RecipeCategory.MISC, ModItems.PRECISION_UPGRADE_MODULE)
            .pattern("IQI")
            .pattern("RDR")
            .pattern("IQI")
            .define('I', Items.IRON_INGOT)
            .define('Q', Items.QUARTZ)
            .define('R', Items.REDSTONE)
            .define('D', Items.DIAMOND)
            .unlockedBy(getHasName(Items.DIAMOND), has(Items.DIAMOND))
            .save(output);
        shaped(RecipeCategory.MISC, ModItems.EFFICIENCY_UPGRADE_MODULE)
            .pattern("ICI")
            .pattern("RER")
            .pattern("ICI")
            .define('I', Items.IRON_INGOT)
            .define('C', Items.COPPER_INGOT)
            .define('R', Items.REDSTONE)
            .define('E', Items.EMERALD)
            .unlockedBy(getHasName(Items.EMERALD), has(Items.EMERALD))
            .save(output);
        // A bone horn for creatures that never drop one: horns are rare, so the whistle has a fallback.
        shaped(RecipeCategory.TOOLS, ModItems.HORN_WHISTLE)
            .pattern("  S")
            .pattern(" G ")
            .pattern("B  ")
            .define('B', Items.GOAT_HORN)
            .define('G', Items.GOLD_NUGGET)
            .define('S', Items.STRING)
            .unlockedBy(getHasName(Items.GOAT_HORN), has(Items.GOAT_HORN))
            .save(output, "ancientcreature:horn_whistle_from_goat_horn");

        shaped(RecipeCategory.DECORATIONS, ModBlocks.REINFORCED_FENCE.item(), 6)
            .pattern("IBI")
            .pattern("IBI")
            .define('I', Items.IRON_INGOT)
            .define('B', Items.IRON_BARS)
            .unlockedBy(getHasName(Items.IRON_BARS), has(Items.IRON_BARS))
            .save(output);
        shaped(RecipeCategory.REDSTONE, ModBlocks.REINFORCED_FENCE_GATE.item())
            .pattern("BIB")
            .pattern("BIB")
            .define('I', Items.IRON_INGOT)
            .define('B', Items.IRON_BARS)
            .unlockedBy(getHasName(ModBlocks.REINFORCED_FENCE.item()), has(ModBlocks.REINFORCED_FENCE.item()))
            .save(output);
        shaped(RecipeCategory.REDSTONE, ModBlocks.ELECTRIC_FENCE.item(), 6)
            .pattern("CRC")
            .pattern("ICI")
            .define('C', Items.COPPER_INGOT)
            .define('R', Items.REDSTONE)
            .define('I', Items.IRON_INGOT)
            .unlockedBy(getHasName(Items.REDSTONE), has(Items.REDSTONE))
            .save(output);

        shaped(RecipeCategory.TRANSPORTATION, ModItems.CREATURE_SADDLE)
            .pattern("HLH")
            .pattern("LIL")
            .pattern("S S")
            .define('H', ModItems.THICK_HIDE)
            .define('L', Items.LEATHER)
            .define('I', Items.IRON_INGOT)
            .define('S', Items.STRING)
            .unlockedBy(getHasName(ModItems.THICK_HIDE), has(ModItems.THICK_HIDE))
            .save(output);
        barding(ModItems.IRON_CREATURE_ARMOR.get(), Items.IRON_BLOCK, Items.IRON_INGOT);
        barding(ModItems.GOLDEN_CREATURE_ARMOR.get(), Items.GOLD_BLOCK, Items.GOLD_INGOT);
        barding(ModItems.DIAMOND_CREATURE_ARMOR.get(), Items.DIAMOND, Items.DIAMOND);
        netheriteSmithing(ModItems.DIAMOND_CREATURE_ARMOR.get(), RecipeCategory.COMBAT, ModItems.NETHERITE_CREATURE_ARMOR.get());

        boneMeal(ModItems.PREDATOR_TOOTH, 3);
        boneMeal(ModItems.SICKLE_CLAW, 3);
        boneMeal(ModItems.PREHISTORIC_HORN, 4);
        boneMeal(ModItems.OSTEODERM, 4);
        boneMeal(ModItems.MAMMOTH_TUSK, 9);
    }

    /** Barding: metal plates over osteoderms on a thick-hide backing. */
    private void barding(Item result, Item core, Item metal) {
        shaped(RecipeCategory.COMBAT, result)
            .pattern("MCM")
            .pattern("OHO")
            .pattern("M M")
            .define('M', metal)
            .define('C', core)
            .define('O', ModItems.OSTEODERM)
            .define('H', ModItems.THICK_HIDE)
            .unlockedBy(getHasName(ModItems.OSTEODERM), has(ModItems.OSTEODERM))
            .save(output);
    }

    private void cook(AbstractCookingRecipe.Factory<? extends AbstractCookingRecipe> factory, int time, String source) {
        SimpleCookingRecipeBuilder.generic(Ingredient.of(ModItems.RAW_PREHISTORIC_MEAT.get()), RecipeCategory.FOOD, CookingBookCategory.FOOD, ModItems.COOKED_PREHISTORIC_MEAT.get(), 0.35F, time, factory)
            .unlockedBy(getHasName(ModItems.RAW_PREHISTORIC_MEAT), has(ModItems.RAW_PREHISTORIC_MEAT))
            .save(output, "ancientcreature:cooked_prehistoric_meat_from_" + source);
    }

    /** Vanilla names this recipe {@code minecraft:<item>_smithing}, which datagen drops as outside the mod's namespace. */
    @Override
    protected void netheriteSmithing(Item base, RecipeCategory category, Item result) {
        SmithingTransformRecipeBuilder.smithing(
                Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE), Ingredient.of(base), this.tag(ItemTags.NETHERITE_TOOL_MATERIALS), category, result
            )
            .unlocks("has_netherite_ingot", this.has(ItemTags.NETHERITE_TOOL_MATERIALS))
            .save(output, "ancientcreature:" + getItemName(result) + "_smithing");
    }

    private void boneMeal(ItemLike source, int count) {
        shapeless(RecipeCategory.MISC, Items.BONE_MEAL, count)
            .requires(source)
            .group("bonemeal")
            .unlockedBy(getHasName(source), has(source))
            .save(output, "ancientcreature:bone_meal_from_" + getItemName(source));
    }

    private void chisel(ItemLike chisel, TagKey<Item> material){
        shaped(RecipeCategory.TOOLS, chisel)
            .pattern("  M")
            .pattern(" M ")
            .pattern("S  ")
            .define('M', Ingredient.of(this.items.getOrThrow(material)))
            .define('S', Ingredient.of(Items.STICK))
            .unlockedBy(getHasName(Items.STICK), has(Items.STICK))
            .unlockedBy("has_" + material.location().getPath(), has(material))
            .save(output);
    }
}
