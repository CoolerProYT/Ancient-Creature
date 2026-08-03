package com.coolerpromc.ancientcreature.data.component.custom;

import com.coolerpromc.ancientcreature.entity.Species;
import com.coolerpromc.ancientcreature.item.FossilPart;
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
import org.jspecify.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;

public record FossilData(Optional<FossilPart> fossilPart, Optional<Species> species, float completeness, boolean isDirty, boolean identified, boolean identificationFailed) implements TooltipProvider {
    public static final FossilData EMPTY = ofDefault(null, null, 0f);

    public static final Codec<FossilData> CODEC = RecordCodecBuilder.create(i -> i.group(
        FossilPart.CODEC.optionalFieldOf("fossilPart").forGetter(FossilData::fossilPart),
        Species.CODEC.optionalFieldOf("species").forGetter(FossilData::species),
        ExtraCodecs.floatRange(0.0f, 1.0f).fieldOf("completeness").forGetter(FossilData::completeness),
        Codec.BOOL.fieldOf("isDirty").forGetter(FossilData::isDirty),
        Codec.BOOL.fieldOf("identified").forGetter(FossilData::identified),
        Codec.BOOL.fieldOf("identificationFailed").forGetter(FossilData::identificationFailed)
    ).apply(i, FossilData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, FossilData> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.optional(FossilPart.STREAM_CODEC),
        FossilData::fossilPart,
        ByteBufCodecs.optional(Species.STREAM_CODEC),
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

    public FossilData(@Nullable FossilPart fossilPart, @Nullable Species species, float completeness, boolean isDirty, boolean identified, boolean identificationFailed){
        this(Optional.ofNullable(fossilPart), Optional.ofNullable(species), completeness, isDirty, identified, identificationFailed);
    }

    public FossilData(@Nullable FossilPart fossilPart, @Nullable Species species, float completeness, boolean isDirty, boolean identified){
        this(Optional.ofNullable(fossilPart), Optional.ofNullable(species), completeness, isDirty, identified, false);
    }

    public static FossilData ofDefault(FossilPart fossilPart, Species species, float completeness){
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
        return new FossilData(fossilPart, Optional.of(species), completeness, isDirty, identified, identificationFailed);
    }

    public @Nullable FossilPart getFossilPart(){
        return fossilPart.orElse(null);
    }

    public @Nullable Species getSpecies(){
        return species.orElse(null);
    }

    @Override
    public void addToTooltip(Item.TooltipContext context, Consumer<Component> consumer, TooltipFlag flag, DataComponentGetter components) {
        consumer.accept(Component.translatable("tooltip.ancientcreature.fossil_completeness",  "§9" + String.format("%.0f", completeness * 100) + "%"));
        String name = "§9" + Component.translatable("species.ancientcreature." + this.getSpecies().getSerializedName()).getString();
        if (!identified){
            name = Component.translatable("species.ancientcreature.unidentified").getString();
        }
        consumer.accept(Component.translatable("tooltip.ancientcreature.species", name));
    }
}
