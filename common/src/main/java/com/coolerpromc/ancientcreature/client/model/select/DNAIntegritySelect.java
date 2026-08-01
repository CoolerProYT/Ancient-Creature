package com.coolerpromc.ancientcreature.client.model.select;

import com.coolerpromc.ancientcreature.data.component.ModDataComponents;
import com.coolerpromc.ancientcreature.data.component.custom.DNAIntegrityLevel;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public record DNAIntegritySelect() implements SelectItemModelProperty<DNAIntegrityLevel> {
    public static final MapCodec<DNAIntegritySelect> CODEC = MapCodec.unit(DNAIntegritySelect::new);
    public static final Type<DNAIntegritySelect, DNAIntegrityLevel> TYPE = Type.create(CODEC, DNAIntegrityLevel.CODEC);

    @Override
    public @Nullable DNAIntegrityLevel get(ItemStack itemStack, @Nullable ClientLevel level, @Nullable LivingEntity owner, int seed, ItemDisplayContext displayContext) {
        return itemStack.get(ModDataComponents.DNA_INTEGRITY_LEVEL.get());
    }

    @Override
    public Codec<DNAIntegrityLevel> valueCodec() {
        return DNAIntegrityLevel.CODEC;
    }

    @Override
    public Type<? extends SelectItemModelProperty<DNAIntegrityLevel>, DNAIntegrityLevel> type() {
        return TYPE;
    }
}
