package com.coolerpromc.ancientcreature.data.component.custom;

import com.coolerpromc.ancientcreature.entity.Species;
import com.coolerpromc.ancientcreature.item.FossilPart;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
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

public record FossilData(Holder<FossilPart> fossilPart, Species species, float completeness, boolean isDirty, boolean identified, boolean identificationFailed) implements TooltipProvider {
    public static final FossilData EMPTY = ofDefault(Holder.direct(FossilPart.EMPTY), Species.TRICERATOPS, 0f);

    public static final Codec<FossilData> CODEC = RecordCodecBuilder.create(i -> i.group(
        FossilPart.CODEC.fieldOf("fossilPart").forGetter(FossilData::fossilPart),
        Species.CODEC.fieldOf("species").forGetter(FossilData::species),
        ExtraCodecs.floatRange(0.0f, 1.0f).fieldOf("completeness").forGetter(FossilData::completeness),
        Codec.BOOL.fieldOf("isDirty").forGetter(FossilData::isDirty),
        Codec.BOOL.fieldOf("identified").forGetter(FossilData::identified),
        Codec.BOOL.fieldOf("identificationFailed").forGetter(FossilData::identificationFailed)
    ).apply(i, FossilData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, FossilData> STREAM_CODEC = StreamCodec.composite(
        FossilPart.STREAM_CODEC,
        FossilData::fossilPart,
        Species.STREAM_CODEC,
        FossilData::species,
        ByteBufCodecs.FLOAT,
        FossilData::completeness,
        ByteBufCodecs.BOOL,
        FossilData::isDirty,
        ByteBufCodecs.BOOL,
        FossilData::identified,
        ByteBufCodecs.BOOL,
        FossilData::identificationFailed,
        FossilData::new
    );

    public FossilData(Holder<FossilPart> fossilPart, Species species, float completeness, boolean isDirty, boolean identified){
        this(fossilPart, species, completeness, isDirty, identified, false);
    }

    public static FossilData ofDefault(Holder<FossilPart> fossilPart, Species species, float completeness){
        return new FossilData(fossilPart, species, completeness, true, false, false);
    }

    public FossilData clean(){
        return new FossilData(fossilPart, species, completeness, false, identified, identificationFailed);
    }

    public FossilData identify(){
        return new FossilData(fossilPart, species, completeness, isDirty, true, false);
    }

    public FossilData identifyFailed(){
        return new FossilData(fossilPart, species, completeness, isDirty, false, true);
    }

    public FossilData setCompleteness(float completeness){
        return new FossilData(fossilPart, species, completeness, isDirty, identified, identificationFailed);
    }

    public FossilData setSpecies(Species species){
        return new FossilData(fossilPart, species, completeness, isDirty, identified, identificationFailed);
    }

    public Species getSpecies(){
        return species;
    }

    @Override
    public void addToTooltip(Item.TooltipContext context, Consumer<Component> consumer, TooltipFlag flag, DataComponentGetter components) {
        consumer.accept(Component.translatable("tooltip.ancientcreature.fossil_completeness",  "§9" + String.format("%.0f", completeness * 100) + "%"));
        String name = "§9" + this.getSpecies().displayName().getString();
        if (!identified){
            name = Component.translatable("species.ancientcreature.unidentified").getString();
        }
        consumer.accept(Component.translatable("tooltip.ancientcreature.species", name));
    }
}
