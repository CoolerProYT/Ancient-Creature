package com.coolerpromc.ancientcreature.client.model.select;

import com.coolerpromc.ancientcreature.data.component.ModDataComponents;
import com.coolerpromc.ancientcreature.data.component.custom.FossilPart;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public record FossilPartSelect() implements SelectItemModelProperty<FossilPart> {
    public static final MapCodec<FossilPartSelect> CODEC = MapCodec.unit(FossilPartSelect::new);
    public static final Type<FossilPartSelect, FossilPart> TYPE = Type.create(CODEC, FossilPart.CODEC);

    @Override
    public @Nullable FossilPart get(ItemStack itemStack, @Nullable ClientLevel level, @Nullable LivingEntity owner, int seed, ItemDisplayContext displayContext) {
        return itemStack.get(ModDataComponents.FOSSIL_PART.get());
    }

    @Override
    public Codec<FossilPart> valueCodec() {
        return FossilPart.CODEC;
    }

    @Override
    public Type<? extends SelectItemModelProperty<FossilPart>, FossilPart> type() {
        return TYPE;
    }
}
