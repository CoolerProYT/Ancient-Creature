package com.coolerpromc.ancientcreature.creativetab.section;

import com.coolerpromc.ancientcreature.Constants;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public record SectionTextured(String id, Component title, Identifier texture, int textColor, List<ItemStack> items) implements Section {
    public static SectionTextured of(String id, Component title, int textColor, List<ItemStack> items) {
        return new SectionTextured(id, title, Constants.id("textures/gui/tab_overlay/" + id + ".png"), textColor, items);
    }
}