package com.coolerpromc.ancientcreature.data.component.custom;

import com.coolerpromc.ancientcreature.entity.Species;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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

public record GenomeData(float completeness, Species species) implements TooltipProvider {
    public static final GenomeData EMPTY = new GenomeData(0f, Species.TRICERATOPS);

    public static final Codec<GenomeData> CODEC = RecordCodecBuilder.create(i -> i.group(
        ExtraCodecs.floatRange(0.0f, 1.0f).fieldOf("completeness").forGetter(GenomeData::completeness),
        Species.CODEC.fieldOf("species").forGetter(GenomeData::species)
    ).apply(i, GenomeData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, GenomeData> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.FLOAT,
        GenomeData::completeness,
        Species.STREAM_CODEC,
        GenomeData::species,
        GenomeData::new
    );

    public GenomeData setCompleteness(float completeness){
        return new GenomeData(completeness, species);
    }

    @Override
    public void addToTooltip(Item.TooltipContext context, Consumer<Component> consumer, TooltipFlag flag, DataComponentGetter components) {
        consumer.accept(Component.translatable("tooltip.ancientcreature.genome_completeness",  "§9" + String.format("%.0f", completeness * 100) + "%"));
        String name = "§9" + this.species.displayName().getString();
        consumer.accept(Component.translatable("tooltip.ancientcreature.species", name));
    }
}
