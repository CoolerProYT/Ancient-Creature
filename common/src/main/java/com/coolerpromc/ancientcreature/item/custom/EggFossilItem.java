package com.coolerpromc.ancientcreature.item.custom;

import com.coolerpromc.ancientcreature.data.component.ModDataComponents;
import com.coolerpromc.ancientcreature.data.component.custom.FossilData;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class EggFossilItem extends Item {
    public EggFossilItem(Properties properties) {
        super(properties);
    }

    @Override
    public Component getName(ItemStack itemStack) {
        FossilData fossilData = itemStack.get(ModDataComponents.FOSSIL_DATA.get());
        if (fossilData != null && fossilData.isDirty()){
            return Component.translatable("name.ancientcreature.dirty").append(" ").append(super.getName(itemStack));
        }
        return super.getName(itemStack);
    }
}
