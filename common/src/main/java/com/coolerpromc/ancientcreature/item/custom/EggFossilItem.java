package com.coolerpromc.ancientcreature.item.custom;

import com.coolerpromc.ancientcreature.data.component.ModDataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class EggFossilItem extends Item {
    public EggFossilItem(Properties properties) {
        super(properties);
    }

    @Override
    public Component getName(ItemStack itemStack) {
        if (itemStack.has(ModDataComponents.IS_DIRTY.get())){
            return Component.translatable("name.ancientcreature.dirty").append(" ").append(super.getName(itemStack));
        }
        return super.getName(itemStack);
    }
}
