package com.coolerpromc.ancientcreature.compat.jei;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.block.ModBlocks;
import com.coolerpromc.ancientcreature.client.gui.screen.DNAExtractorScreen;
import com.coolerpromc.ancientcreature.client.gui.screen.EmbryogenesisChamberScreen;
import com.coolerpromc.ancientcreature.client.gui.screen.FossilCleaningTableScreen;
import com.coolerpromc.ancientcreature.client.gui.screen.FossilIdentificationChamberScreen;
import com.coolerpromc.ancientcreature.client.gui.screen.GenomeSequenceScreen;
import com.coolerpromc.ancientcreature.client.gui.screen.IncubatorScreen;
import com.coolerpromc.ancientcreature.data.component.ModDataComponents;
import com.coolerpromc.ancientcreature.item.ModItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.types.IRecipeType;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

@JeiPlugin
public class ModJEIPlugin implements IModPlugin {
    public static final IRecipeType<AncientCreatureJeiRecipe> FOSSIL_HUNTING = type("fossil_hunting");
    public static final IRecipeType<AncientCreatureJeiRecipe> FOSSIL_CLEANING = type("fossil_cleaning");
    public static final IRecipeType<AncientCreatureJeiRecipe> FOSSIL_IDENTIFICATION = type("fossil_identification");
    public static final IRecipeType<AncientCreatureJeiRecipe> DNA_EXTRACTION = type("dna_extraction");
    public static final IRecipeType<AncientCreatureJeiRecipe> GENOME_SEQUENCING = type("genome_sequencing");
    public static final IRecipeType<AncientCreatureJeiRecipe> EMBRYOGENESIS = type("embryogenesis");
    public static final IRecipeType<AncientCreatureJeiRecipe> INCUBATION = type("incubation");

    @Override
    public Identifier getPluginUid() {
        return Constants.id("jei_plugin");
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        registration.registerFromDataComponentTypes(ModItems.FOSSIL_FRAGMENT.get(), ModDataComponents.SPECIES.get(), ModDataComponents.FOSSIL_PART.get(), ModDataComponents.IS_DIRTY.get(), ModDataComponents.IDENTIFIED.get(), ModDataComponents.IDENTIFICATION_FAILED.get());
        registration.registerFromDataComponentTypes(ModItems.EGG_FOSSIL.get(), ModDataComponents.SPECIES.get(), ModDataComponents.IS_DIRTY.get(), ModDataComponents.IDENTIFIED.get(), ModDataComponents.IDENTIFICATION_FAILED.get());
        registration.registerFromDataComponentTypes(ModItems.DNA_SAMPLE.get(), ModDataComponents.SPECIES.get(), ModDataComponents.DNA_INTEGRITY_LEVEL.get());
        registration.registerFromDataComponentTypes(ModItems.GENOME_CARTRIDGE_FILLED.get(), ModDataComponents.SPECIES.get());
        registration.registerFromDataComponentTypes(ModItems.GENOME_CARTRIDGE_COMPLETED.get(), ModDataComponents.SPECIES.get());
        registration.registerFromDataComponentTypes(ModItems.FERTILIZED_ANCIENT_EGG.get(), ModDataComponents.SPECIES.get());
        registration.registerFromDataComponentTypes(ModItems.BABY_CREATURE_CAPSULE.get(), ModDataComponents.SPECIES.get());
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IGuiHelper gui = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(
            category(FOSSIL_HUNTING, "jei.ancientcreature.category.fossil_hunting", gui.createDrawableItemLike(ModBlocks.FOSSIL_ORE.getBlock())),
            category(FOSSIL_CLEANING, "block.ancientcreature.fossil_cleaning_table", gui.createDrawableItemLike(ModBlocks.FOSSIL_CLEANING_TABLE.getBlock())),
            category(FOSSIL_IDENTIFICATION, "block.ancientcreature.fossil_identification_chamber", gui.createDrawableItemLike(ModBlocks.FOSSIL_IDENTIFICATION_CHAMBER.getBlock())),
            category(DNA_EXTRACTION, "block.ancientcreature.dna_extractor", gui.createDrawableItemLike(ModBlocks.DNA_EXTRACTOR.getBlock())),
            category(GENOME_SEQUENCING, "block.ancientcreature.genome_sequencer", gui.createDrawableItemLike(ModBlocks.GENOME_SEQUENCER.getBlock())),
            category(EMBRYOGENESIS, "block.ancientcreature.embryogenesis_chamber", gui.createDrawableItemLike(ModBlocks.EMBRYOGENESIS_CHAMBER.getBlock())),
            category(INCUBATION, "block.ancientcreature.incubator", gui.createDrawableItemLike(ModBlocks.INCUBATOR.getBlock()))
        );
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(FOSSIL_HUNTING, AncientCreatureJeiRecipes.fossilHunting());
        registration.addRecipes(FOSSIL_CLEANING, AncientCreatureJeiRecipes.fossilCleaning());
        registration.addRecipes(FOSSIL_IDENTIFICATION, AncientCreatureJeiRecipes.fossilIdentification());
        registration.addRecipes(DNA_EXTRACTION, AncientCreatureJeiRecipes.dnaExtraction());
        registration.addRecipes(GENOME_SEQUENCING, AncientCreatureJeiRecipes.genomeSequencing());
        registration.addRecipes(EMBRYOGENESIS, AncientCreatureJeiRecipes.embryogenesis());
        registration.addRecipes(INCUBATION, AncientCreatureJeiRecipes.incubation());
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addCraftingStation(FOSSIL_HUNTING, ModBlocks.FOSSIL_ORE.getBlock(), ModBlocks.ROCK_PILE.getBlock());
        registration.addCraftingStation(FOSSIL_CLEANING, ModBlocks.FOSSIL_CLEANING_TABLE.getBlock());
        registration.addCraftingStation(FOSSIL_IDENTIFICATION, ModBlocks.FOSSIL_IDENTIFICATION_CHAMBER.getBlock());
        registration.addCraftingStation(DNA_EXTRACTION, ModBlocks.DNA_EXTRACTOR.getBlock());
        registration.addCraftingStation(GENOME_SEQUENCING, ModBlocks.GENOME_SEQUENCER.getBlock());
        registration.addCraftingStation(EMBRYOGENESIS, ModBlocks.EMBRYOGENESIS_CHAMBER.getBlock());
        registration.addCraftingStation(INCUBATION, ModBlocks.INCUBATOR.getBlock());
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(FossilCleaningTableScreen.class, 76, 34, 24, 16, FOSSIL_CLEANING);
        registration.addRecipeClickArea(FossilIdentificationChamberScreen.class, 76, 34, 24, 16, FOSSIL_IDENTIFICATION);
        registration.addRecipeClickArea(DNAExtractorScreen.class, 93, 34, 24, 16, DNA_EXTRACTION);
        registration.addRecipeClickArea(GenomeSequenceScreen.class, 77, 34, 24, 16, GENOME_SEQUENCING);
        registration.addRecipeClickArea(EmbryogenesisChamberScreen.class, 93, 34, 24, 16, EMBRYOGENESIS);
        registration.addRecipeClickArea(IncubatorScreen.class, 77, 34, 24, 16, INCUBATION);
    }

    private static IRecipeType<AncientCreatureJeiRecipe> type(String path) {
        return IRecipeType.create(Constants.id(path), AncientCreatureJeiRecipe.class);
    }

    private static AncientCreatureRecipeCategory category(IRecipeType<AncientCreatureJeiRecipe> type, String titleKey, IDrawable icon) {
        return new AncientCreatureRecipeCategory(type, Component.translatable(titleKey), icon);
    }
}
