package com.coolerpromc.ancientcreature.datagen;

import com.coolerpromc.ancientcreature.block.ModBlocks;
import com.coolerpromc.ancientcreature.item.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider {
    protected ModRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @Override
    protected void buildRecipes() {
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
            .define('S', Ingredient.of(this.items.getOrThrow(ItemTags.SLABS)))
            .unlockedBy(getHasName(Items.BRUSH), has(Items.BRUSH))
            .unlockedBy("has_planks", has(ItemTags.PLANKS))
            .unlockedBy("has_slabs", has(ItemTags.SLABS))
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

    public static class Runner extends RecipeProvider.Runner{
        protected Runner(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
            super(packOutput, registries);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
            return new ModRecipeProvider(registries, output);
        }

        @Override
        public String getName() {
            return "Ancient Creature Recipes";
        }
    }
}
