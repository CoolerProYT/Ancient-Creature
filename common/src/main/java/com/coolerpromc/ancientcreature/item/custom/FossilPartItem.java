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

        MutableComponent state = Component.empty();
        if (isDirty){
            append(state, Component.translatable("name.ancientcreature.dirty"));
        }
        if (!identified){
            append(state, Component.translatable("name.ancientcreature.unidentified"));
        }
        Component partName = Component.translatable("fossilPart.ancientcreature." + (part == null ? "empty" : part.unwrapKey().orElse(FossilPart.key("empty")).identifier().getPath()));
        return state.getSiblings().isEmpty() ? partName : Component.translatable("name.ancientcreature.fossil_name", state, partName);
    }

    private static void append(MutableComponent text, Component word) {
        if (!text.getSiblings().isEmpty()) {
            text.append(" ");
        }
        text.append(word);
    }
}
