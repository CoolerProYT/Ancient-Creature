package com.coolerpromc.ancientcreature.data.component.custom;

import com.coolerpromc.ancientcreature.data.component.ModDataComponents;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;

import java.util.function.Consumer;

public record FossilCompleteness(float value) implements TooltipProvider {
    public static final Codec<FossilCompleteness> CODEC = ExtraCodecs.floatRange(0.0F, 1.0F).xmap(FossilCompleteness::new, FossilCompleteness::value);
    public static final StreamCodec<RegistryFriendlyByteBuf, FossilCompleteness> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.FLOAT, FossilCompleteness::value, FossilCompleteness::new);

    @Override
    public void addToTooltip(Item.TooltipContext context, Consumer<Component> consumer, TooltipFlag flag, DataComponentGetter components) {
        float completeness = components.getOrDefault(ModDataComponents.FOSSIL_COMPLETENESS.get(), new FossilCompleteness(1f)).value();
        consumer.accept(Component.translatable("tooltip.ancientcreature.fossil_completeness",  "§9" + String.format("%.0f", completeness * 100) + "%"));
    }
}
