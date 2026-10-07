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

/**
 * A DNA sample. {@code quality} (0-1) is the exact integrity score the sample was extracted with; the
 * integrity level is its band. Quality carries through sequencing into the genome's fidelity, which
 * decides how healthy the hatchling is. Samples made before quality existed read as their band's middle.
 */
public record DNAData(DNAIntegrityLevel integrityLevel, Species species, float quality) implements TooltipProvider {
    public static final DNAData EMPTY = new DNAData(DNAIntegrityLevel.PRESERVED_EMBRYO, Species.TRICERATOPS);

    public DNAData(DNAIntegrityLevel integrityLevel, Species species) {
        this(integrityLevel, species, integrityLevel.typicalQuality());
    }

    public static final Codec<DNAData> CODEC = RecordCodecBuilder.create(i -> i.group(
        DNAIntegrityLevel.CODEC.fieldOf("integrityLevel").forGetter(DNAData::integrityLevel),
        Species.CODEC.fieldOf("species").forGetter(DNAData::species),
        com.mojang.serialization.Codec.floatRange(0.0F, 1.0F).optionalFieldOf("quality").forGetter(d -> java.util.Optional.of(d.quality()))
    ).apply(i, (level, species, quality) -> new DNAData(level, species, quality.orElse(level.typicalQuality()))));

    public static final StreamCodec<RegistryFriendlyByteBuf, DNAData> STREAM_CODEC = StreamCodec.composite(
        DNAIntegrityLevel.STREAM_CODEC,
        DNAData::integrityLevel,
        Species.STREAM_CODEC,
        DNAData::species,
        net.minecraft.network.codec.ByteBufCodecs.FLOAT,
        DNAData::quality,
        DNAData::new
    );

    @Override
    public void addToTooltip(Item.TooltipContext context, Consumer<Component> consumer, TooltipFlag flag, DataComponentGetter components) {
        String name = "§9" + this.species.displayName().getString();
        consumer.accept(Component.translatable("tooltip.ancientcreature.species", name));
        consumer.accept(Component.translatable("tooltip.ancientcreature.dna_quality", Math.round(this.quality * 100)));
    }
}
