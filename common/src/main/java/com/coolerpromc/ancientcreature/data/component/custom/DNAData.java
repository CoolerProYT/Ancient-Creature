package com.coolerpromc.ancientcreature.data.component.custom;

import com.coolerpromc.ancientcreature.entity.Species;
import com.coolerpromc.ancientcreature.item.DNAIntegrityLevel;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;

import java.util.function.Consumer;

public record DNAData(DNAIntegrityLevel integrityLevel, Species species) implements TooltipProvider {
    public static final DNAData EMPTY = new DNAData(DNAIntegrityLevel.PRESERVED_EMBRYO, Species.TRICERATOPS);

    public static final Codec<DNAData> CODEC = RecordCodecBuilder.create(i -> i.group(
        DNAIntegrityLevel.CODEC.fieldOf("integrityLevel").forGetter(DNAData::integrityLevel),
        Species.CODEC.fieldOf("species").forGetter(DNAData::species)
    ).apply(i, DNAData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, DNAData> STREAM_CODEC = StreamCodec.composite(
        DNAIntegrityLevel.STREAM_CODEC,
        DNAData::integrityLevel,
        Species.STREAM_CODEC,
        DNAData::species,
        DNAData::new
    );

    @Override
    public void addToTooltip(Item.TooltipContext context, Consumer<Component> consumer, TooltipFlag flag, DataComponentGetter components) {
        String name = "§9" + Component.translatable("species.ancientcreature." + this.species.getSerializedName()).getString();
        consumer.accept(Component.translatable("tooltip.ancientcreature.species", name));
    }
}
