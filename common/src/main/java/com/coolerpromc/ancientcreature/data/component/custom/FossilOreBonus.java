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

/**
 * Completeness a chisel adds to the fossils it digs from plain fossil ore and rock piles. These
 * shallow sources roll low, so a better chisel lifts the whole roll instead of shaving less off it.
 */
public record FossilOreBonus(float value) implements TooltipProvider {
    public static final Codec<FossilOreBonus> CODEC = ExtraCodecs.floatRange(0.0F, 1.0F).xmap(FossilOreBonus::new, FossilOreBonus::value);
    public static final StreamCodec<RegistryFriendlyByteBuf, FossilOreBonus> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.FLOAT, FossilOreBonus::value, FossilOreBonus::new);

    @Override
    public void addToTooltip(Item.TooltipContext context, Consumer<Component> consumer, TooltipFlag flag, DataComponentGetter components) {
        float bonus = components.getOrDefault(ModDataComponents.FOSSIL_ORE_BONUS.get(), new FossilOreBonus(0f)).value();
        consumer.accept(Component.translatable("tooltip.ancientcreature.fossil_ore_bonus", String.format("%.0f", bonus * 100)).withStyle(ChatFormatting.BLUE));
    }
}
