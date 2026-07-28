package com.coolerpromc.ancientcreature.client.model.condition;

import com.coolerpromc.ancientcreature.data.component.ModDataComponents;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public class DirtyFossilFragmentCondition implements ConditionalItemModelProperty {
    public static final MapCodec<DirtyFossilFragmentCondition> MAP_CODEC = MapCodec.unit(DirtyFossilFragmentCondition::new);

    @Override
    public MapCodec<? extends ConditionalItemModelProperty> type() {
        return MAP_CODEC;
    }

    @Override
    public boolean get(ItemStack itemStack, @Nullable ClientLevel level, @Nullable LivingEntity owner, int seed, ItemDisplayContext displayContext) {
        return itemStack.getOrDefault(ModDataComponents.IS_DIRTY.get(), true);
    }
}
