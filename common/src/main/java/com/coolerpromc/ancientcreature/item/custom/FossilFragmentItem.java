package com.coolerpromc.ancientcreature.item.custom;

import com.coolerpromc.ancientcreature.data.component.ModDataComponents;
import com.coolerpromc.ancientcreature.data.component.custom.FossilPart;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class FossilFragmentItem extends Item {
    public FossilFragmentItem(Properties properties) {
        super(properties);
    }

    @Override
    public Component getName(ItemStack itemStack) {
        boolean isDirty = itemStack.getOrDefault(ModDataComponents.IS_DIRTY.get(), false);
        boolean identified = itemStack.getOrDefault(ModDataComponents.IDENTIFIED.get(), false);
        FossilPart part = itemStack.get(ModDataComponents.FOSSIL_PART.get());

        MutableComponent component = Component.empty();

        if (isDirty){
            component.append(Component.translatable("name.ancientcreature.dirty")).append(" ");
        }
        if (!identified){
            component.append(Component.translatable("name.ancientcreature.unidentified")).append(" ");
        }
        if (part != null){
            component.append(Component.translatable("fossilPart.ancientcreature." + part.getSerializedName())).append(" ");
        }

        component.append(super.getName(itemStack));

        return component;
    }
}
