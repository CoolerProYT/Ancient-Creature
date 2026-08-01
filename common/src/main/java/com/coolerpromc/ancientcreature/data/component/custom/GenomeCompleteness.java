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

public record GenomeCompleteness(float value) implements TooltipProvider {
    public static final Codec<GenomeCompleteness> CODEC = ExtraCodecs.floatRange(0.0F, 1.0F).xmap(GenomeCompleteness::new, GenomeCompleteness::value);
    public static final StreamCodec<RegistryFriendlyByteBuf, GenomeCompleteness> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.FLOAT, GenomeCompleteness::value, GenomeCompleteness::new);

    @Override
    public void addToTooltip(Item.TooltipContext context, Consumer<Component> consumer, TooltipFlag flag, DataComponentGetter components) {
        float completeness = components.getOrDefault(ModDataComponents.GENOME_COMPLETENESS.get(), new GenomeCompleteness(1f)).value();
        consumer.accept(Component.translatable("tooltip.ancientcreature.genome_completeness",  "§9" + String.format("%.0f", completeness * 100) + "%"));
    }
}
