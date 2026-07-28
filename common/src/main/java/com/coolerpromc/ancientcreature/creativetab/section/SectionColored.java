package com.coolerpromc.ancientcreature.creativetab.section;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public record SectionColored(String id, Component title, int bannerColor, int textColor, List<ItemStack> items) implements Section {}