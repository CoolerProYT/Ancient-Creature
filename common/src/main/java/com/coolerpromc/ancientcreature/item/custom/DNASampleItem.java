package com.coolerpromc.ancientcreature.item.custom;

import com.coolerpromc.ancientcreature.data.component.ModDataComponents;
import com.coolerpromc.ancientcreature.data.component.custom.DNAIntegrityLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class DNASampleItem extends Item {
    public DNASampleItem(Properties properties) {
        super(properties);
    }

    @Override
    public Component getName(ItemStack itemStack) {
        MutableComponent component = Component.literal(super.getName(itemStack).getString());
        DNAIntegrityLevel level = itemStack.get(ModDataComponents.DNA_INTEGRITY_LEVEL.get());
        if (level != null){
            component.append(" (").append(Component.translatable("dna.ancientcreature." + level.getSerializedName())).append(")");
        }
        return component;
    }
}
