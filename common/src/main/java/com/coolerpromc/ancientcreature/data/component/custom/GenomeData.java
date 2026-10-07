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

/**
 * A genome being assembled. {@code fidelity} is the completeness-weighted average quality of the DNA that
 * went into it, so a few excellent samples make a better genome than many poor ones.
 */
public record GenomeData(float completeness, Species species, float fidelity) implements TooltipProvider {
    public static final float LEGACY_FIDELITY = 0.5F;
    public static final GenomeData EMPTY = new GenomeData(0f, Species.TRICERATOPS);

    public GenomeData(float completeness, Species species) {
        this(completeness, species, LEGACY_FIDELITY);
    }

    public static final Codec<GenomeData> CODEC = RecordCodecBuilder.create(i -> i.group(
        ExtraCodecs.floatRange(0.0f, 1.0f).fieldOf("completeness").forGetter(GenomeData::completeness),
        Species.CODEC.fieldOf("species").forGetter(GenomeData::species),
        ExtraCodecs.floatRange(0.0f, 1.0f).optionalFieldOf("fidelity", LEGACY_FIDELITY).forGetter(GenomeData::fidelity)
    ).apply(i, GenomeData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, GenomeData> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.FLOAT,
        GenomeData::completeness,
        Species.STREAM_CODEC,
        GenomeData::species,
        ByteBufCodecs.FLOAT,
        GenomeData::fidelity,
        GenomeData::new
    );

    public GenomeData setCompleteness(float completeness){
        return new GenomeData(completeness, species, fidelity);
    }

    /** Adds {@code added} completeness of DNA with {@code quality}, re-weighting fidelity by contribution. */
    public GenomeData withSample(float added, float quality) {
        float next = Math.min(1.0F, this.completeness + added);
        float used = next - this.completeness;
        float weight = this.completeness + used;
        float mixed = weight <= 0 ? quality : (this.fidelity * this.completeness + quality * used) / weight;
        return new GenomeData(next, this.species, Math.max(0.0F, Math.min(1.0F, mixed)));
    }

    @Override
    public void addToTooltip(Item.TooltipContext context, Consumer<Component> consumer, TooltipFlag flag, DataComponentGetter components) {
        consumer.accept(Component.translatable("tooltip.ancientcreature.genome_completeness",  "§9" + String.format("%.0f", completeness * 100) + "%"));
        String name = "§9" + this.species.displayName().getString();
        consumer.accept(Component.translatable("tooltip.ancientcreature.species", name));
        consumer.accept(Component.translatable("tooltip.ancientcreature.genome_fidelity", Math.round(this.fidelity * 100)));
    }
}
