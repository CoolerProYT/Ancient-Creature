package com.coolerpromc.ancientcreature.event;

import com.coolerpromc.ancientcreature.data.component.ModDataComponents;
import com.coolerpromc.ancientcreature.data.component.custom.FossilCompleteness;
import com.coolerpromc.ancientcreature.data.component.custom.FossilDamageRate;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class ItemEvents {
    public static void onItemTooltip(ItemStack stack, Item.TooltipContext tooltipContext, TooltipFlag tooltipFlag, List<Component> components) {
        FossilCompleteness completeness = stack.get(ModDataComponents.FOSSIL_COMPLETENESS.get());
        if (completeness != null){
            completeness.addToTooltip(tooltipContext, components::add, tooltipFlag, stack.getComponents());
        }
        FossilDamageRate damageRate = stack.get(ModDataComponents.FOSSIL_DAMAGE_RATE.get());
        if (damageRate != null){
            damageRate.addToTooltip(tooltipContext, components::add, tooltipFlag, stack.getComponents());
        }
    }
}
