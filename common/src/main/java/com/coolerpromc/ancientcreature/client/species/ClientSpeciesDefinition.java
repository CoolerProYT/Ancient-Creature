package com.coolerpromc.ancientcreature.client.species;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;

import java.util.List;
import java.util.Optional;

public record ClientSpeciesDefinition(int formatVersion, Identifier geometry, Identifier texture, Optional<Identifier> animations, Optional<Identifier> controller, float shadowRadius, float renderScale, ClientSpeciesGuiSettings gui, List<TextureVariant> variants) {
    public static final int CURRENT_FORMAT_VERSION = 1;

    private static final Codec<Integer> FORMAT_VERSION_CODEC = Codec.INT.validate(
        v -> v >= 1 && v <= CURRENT_FORMAT_VERSION
            ? DataResult.success(v)
            : DataResult.error(() -> "unsupported format_version " + v + " (this build understands 1.." + CURRENT_FORMAT_VERSION + ")"));

    public static final Codec<ClientSpeciesDefinition> CODEC = RecordCodecBuilder.create(i -> i.group(
        FORMAT_VERSION_CODEC.optionalFieldOf("format_version", CURRENT_FORMAT_VERSION).forGetter(ClientSpeciesDefinition::formatVersion),
        Identifier.CODEC.fieldOf("geometry").forGetter(ClientSpeciesDefinition::geometry),
        Identifier.CODEC.fieldOf("texture").forGetter(ClientSpeciesDefinition::texture),
        Identifier.CODEC.optionalFieldOf("animations").forGetter(ClientSpeciesDefinition::animations),
        Identifier.CODEC.optionalFieldOf("controller").forGetter(ClientSpeciesDefinition::controller),
        ExtraCodecs.floatRange(0.0F, 16.0F).optionalFieldOf("shadow_radius", 0.5F).forGetter(ClientSpeciesDefinition::shadowRadius),
        ExtraCodecs.floatRange(Float.MIN_NORMAL, 16.0F).optionalFieldOf("render_scale", 1.0F).forGetter(ClientSpeciesDefinition::renderScale),
        ClientSpeciesGuiSettings.CODEC.optionalFieldOf("gui", ClientSpeciesGuiSettings.DEFAULT).forGetter(ClientSpeciesDefinition::gui),
        TextureVariant.CODEC.listOf().optionalFieldOf("variants", List.of()).forGetter(ClientSpeciesDefinition::variants)
    ).apply(i, ClientSpeciesDefinition::new));

    public Identifier textureFor(String variantId) {
        for (TextureVariant variant : this.variants) {
            if (variant.id().equals(variantId)) {
                return variant.texture();
            }
        }
        return this.texture;
    }

    public String rollVariant(RandomSource random) {
        int total = this.variants.stream().mapToInt(TextureVariant::weight).sum();
        if (total <= 0) {
            return "default";
        }
        int roll = random.nextInt(total);
        for (TextureVariant variant : this.variants) {
            roll -= variant.weight();
            if (roll < 0) {
                return variant.id();
            }
        }
        return this.variants.getLast().id();
    }

    public record TextureVariant(String id, int weight, Identifier texture) {
        public static final Codec<TextureVariant> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.STRING.fieldOf("id").forGetter(TextureVariant::id),
            ExtraCodecs.POSITIVE_INT.optionalFieldOf("weight", 1).forGetter(TextureVariant::weight),
            Identifier.CODEC.fieldOf("texture").forGetter(TextureVariant::texture)
        ).apply(i, TextureVariant::new));
    }
}
