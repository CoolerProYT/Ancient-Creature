package com.coolerpromc.ancientcreature.species;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.EntityDimensions;

import java.util.Optional;

public record SpeciesPhysicalProperties(float width, float height, Optional<Float> eyeHeight) {
    public static final float MAX_SIZE = 64.0F;

    public static final SpeciesPhysicalProperties DEFAULT = new SpeciesPhysicalProperties(1.0F, 1.0F, Optional.empty());

    public static final Codec<SpeciesPhysicalProperties> CODEC = RecordCodecBuilder.create(i -> i.group(
        ExtraCodecs.floatRange(Float.MIN_NORMAL, MAX_SIZE).fieldOf("width").forGetter(SpeciesPhysicalProperties::width),
        ExtraCodecs.floatRange(Float.MIN_NORMAL, MAX_SIZE).fieldOf("height").forGetter(SpeciesPhysicalProperties::height),
        ExtraCodecs.floatRange(0.0F, MAX_SIZE).optionalFieldOf("eye_height").forGetter(SpeciesPhysicalProperties::eyeHeight)
    ).apply(i, SpeciesPhysicalProperties::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, SpeciesPhysicalProperties> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.FLOAT, SpeciesPhysicalProperties::width,
        ByteBufCodecs.FLOAT, SpeciesPhysicalProperties::height,
        ByteBufCodecs.FLOAT.apply(ByteBufCodecs::optional), SpeciesPhysicalProperties::eyeHeight,
        SpeciesPhysicalProperties::new
    );

    public float resolvedEyeHeight() {
        return this.eyeHeight.orElse(this.height * 0.85F);
    }

    public EntityDimensions toDimensions(float scale) {
        float w = Math.max(Float.MIN_NORMAL, this.width * scale);
        float h = Math.max(Float.MIN_NORMAL, this.height * scale);
        return EntityDimensions.scalable(w, h).withEyeHeight(this.resolvedEyeHeight() * scale);
    }
}
