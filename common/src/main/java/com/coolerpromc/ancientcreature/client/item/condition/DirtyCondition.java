package com.coolerpromc.ancientcreature.client.item.condition;

import com.coolerpromc.ancientcreature.data.component.ModDataComponents;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public class DirtyCondition implements ConditionalItemModelProperty {
    public static final MapCodec<DirtyCondition> MAP_CODEC = MapCodec.unit(DirtyCondition::new);

    @Override
    public MapCodec<? extends ConditionalItemModelProperty> type() {
        return MAP_CODEC;
    }

    @Override
    public boolean get(ItemStack itemStack, @Nullable ClientLevel level, @Nullable LivingEntity owner, int seed, ItemDisplayContext displayContext) {
        return itemStack.get(ModDataComponents.FOSSIL_DATA.get()).isDirty();
    }
}
