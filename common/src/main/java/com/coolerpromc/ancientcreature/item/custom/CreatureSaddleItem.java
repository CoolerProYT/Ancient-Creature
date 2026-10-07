package com.coolerpromc.ancientcreature.item.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

/** A saddle sized for revived creatures. The owner puts it on an adult creature to ride it. */
public class CreatureSaddleItem extends Item {
    public CreatureSaddleItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag flag) {
        builder.accept(Component.translatable("tooltip.ancientcreature.creature_saddle.use").withStyle(ChatFormatting.GRAY));
        builder.accept(Component.translatable("tooltip.ancientcreature.creature_saddle.remove").withStyle(ChatFormatting.GRAY));
    }
}
