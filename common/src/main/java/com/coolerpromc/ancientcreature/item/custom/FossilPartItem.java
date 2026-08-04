package com.coolerpromc.ancientcreature.item.custom;

import com.coolerpromc.ancientcreature.data.component.ModDataComponents;
import com.coolerpromc.ancientcreature.data.component.custom.FossilData;
import com.coolerpromc.ancientcreature.item.FossilPart;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class FossilPartItem extends Item {
    public FossilPartItem(Properties properties) {
        super(properties);
    }

    @Override
    public Component getName(ItemStack itemStack) {
        FossilData fossilData = itemStack.getOrDefault(ModDataComponents.FOSSIL_DATA.get(), FossilData.EMPTY);
        boolean isDirty = fossilData.isDirty();
        boolean identified = fossilData.identified();
        Holder<FossilPart> part = fossilData.fossilPart();

        MutableComponent component = Component.empty();

        if (isDirty){
            component.append(Component.translatable("name.ancientcreature.dirty")).append(" ");
        }
        if (!identified){
            component.append(Component.translatable("name.ancientcreature.unidentified")).append(" ");
        }
        if (part != null){
            component.append(Component.translatable("fossilPart.ancientcreature." + part.unwrapKey().orElse(FossilPart.key("empty")).identifier().getPath())).append(" ");
        }

        return component;
    }
}
