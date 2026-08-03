package com.coolerpromc.ancientcreature.compat.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;

public class AncientCreatureRecipeCategory extends AbstractRecipeCategory<AncientCreatureJeiRecipe> {
    private static final int WIDTH = 150;
    private static final int SLOT_Y = 10;
    private static final int SLOT_SIZE = 18;
    private static final int ARROW_WIDTH = 24;
    private static final int ELEMENT_GAP = 8;
    private static final List<ChatFormatting> NOTE_COLORS = List.of(
        ChatFormatting.GOLD,
        ChatFormatting.GREEN,
        ChatFormatting.AQUA,
        ChatFormatting.YELLOW,
        ChatFormatting.BLUE,
        ChatFormatting.LIGHT_PURPLE,
        ChatFormatting.DARK_PURPLE
    );

    public AncientCreatureRecipeCategory(IRecipeType<AncientCreatureJeiRecipe> recipeType, Component title, IDrawable icon) {
        super(recipeType, title, icon, WIDTH, 124);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, AncientCreatureJeiRecipe recipe, IFocusGroup focuses) {
        RowLayout layout = getRowLayout(recipe);
        int inputCount = recipe.inputs().size();
        for (int i = 0; i < inputCount; i++) {
            builder.addInputSlot(layout.inputStart() + i * (SLOT_SIZE + ELEMENT_GAP), SLOT_Y)
                .setStandardSlotBackground()
                .addItemStacks(recipe.inputs().get(i));
        }

        int outputCount = recipe.outputs().size();
        for (int i = 0; i < outputCount; i++) {
            builder.addOutputSlot(layout.outputStart() + i * (SLOT_SIZE + ELEMENT_GAP), SLOT_Y)
                .setStandardSlotBackground()
                .addItemStacks(recipe.outputs().get(i));
        }
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, AncientCreatureJeiRecipe recipe, IFocusGroup focuses) {
        RowLayout layout = getRowLayout(recipe);
        builder.addAnimatedRecipeArrow(Math.max(20, recipe.processingTicks())).setPosition(layout.arrowX(), SLOT_Y);
        if (!recipe.notes().isEmpty()) {
            List<FormattedText> coloredNotes = new ArrayList<>(recipe.notes().size());
            for (int i = 0; i < recipe.notes().size(); i++) {
                ChatFormatting color = NOTE_COLORS.get(Math.min(i, NOTE_COLORS.size() - 1));
                coloredNotes.add(recipe.notes().get(i).copy().withStyle(color));
            }
            builder.addText(coloredNotes, 144, 88)
                .setPosition(3, 36)
                .setLineSpacing(1)
                .setColor(0xFFAAAAAA);
        }
    }

    private static RowLayout getRowLayout(AncientCreatureJeiRecipe recipe) {
        int inputCount = recipe.inputs().size();
        int outputCount = recipe.outputs().size();
        int slotCount = inputCount + outputCount;
        int rowWidth = slotCount * SLOT_SIZE + ARROW_WIDTH + slotCount * ELEMENT_GAP;
        int rowStart = (WIDTH - rowWidth) / 2;

        int inputStart = rowStart + 1;
        int arrowX = rowStart + inputCount * (SLOT_SIZE + ELEMENT_GAP);
        int outputStart = arrowX + ARROW_WIDTH + ELEMENT_GAP + 1;
        return new RowLayout(inputStart, arrowX, outputStart);
    }

    @Override
    public Identifier getIdentifier(AncientCreatureJeiRecipe recipe) {
        return recipe.id();
    }

    private record RowLayout(int inputStart, int arrowX, int outputStart) {
    }
}
