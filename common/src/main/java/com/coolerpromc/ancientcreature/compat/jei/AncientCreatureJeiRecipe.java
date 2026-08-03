package com.coolerpromc.ancientcreature.compat.jei;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public record AncientCreatureJeiRecipe(
    Identifier id,
    List<List<ItemStack>> inputs,
    List<List<ItemStack>> outputs,
    List<Component> notes,
    int processingTicks
) {
}
