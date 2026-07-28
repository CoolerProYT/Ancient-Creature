package com.coolerpromc.ancientcreature.creativetab.section;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface Section {
    String id();
    Component title();
    int textColor();
    List<ItemStack> items();
}