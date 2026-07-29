package com.coolerpromc.ancientcreature.data.component.custom;

import com.coolerpromc.ancientcreature.data.component.ModDataComponents;
import com.mojang.serialization.Codec;
import net.minecraft.ChatFormatting;
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

public record FossilDamageRate(float value) implements TooltipProvider {
    public static final Codec<FossilDamageRate> CODEC = ExtraCodecs.floatRange(0.0F, 1.0F).xmap(FossilDamageRate::new, FossilDamageRate::value);
    public static final StreamCodec<RegistryFriendlyByteBuf, FossilDamageRate> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.FLOAT, FossilDamageRate::value, FossilDamageRate::new);


    @Override
    public void addToTooltip(Item.TooltipContext context, Consumer<Component> consumer, TooltipFlag flag, DataComponentGetter components) {
        float damageRate = components.getOrDefault(ModDataComponents.FOSSIL_DAMAGE_RATE.get(), new FossilDamageRate(0f)).value();
        consumer.accept(Component.translatable("tooltip.ancientcreature.fossil_damage_rate",  String.format("%.0f", damageRate * 100)).withStyle(ChatFormatting.BLUE));
    }
}
