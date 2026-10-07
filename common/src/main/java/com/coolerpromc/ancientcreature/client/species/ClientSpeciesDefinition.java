package com.coolerpromc.ancientcreature.client.species;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;

import java.util.List;
import java.util.Optional;

/**
 * The mod-specific half of a species' appearance: {@code assets/<ns>/ancientcreature/species/<name>.json}.
 *
 * <p><b>format_version 2</b> (current) points at a Bedrock client entity and adds the few things Bedrock
 * has no field for — shadow size, an overall render scale and how the species is framed in GUIs:
 * <pre>{@code
 * { "format_version": 2, "client_entity": "ancientcreature:triceratops",
 *   "shadow_radius": 0.9, "render_scale": 1.0, "gui": { "scale": 12.7, "rotation": [0, 215, 0] } }
 * }</pre>
 * {@code client_entity} defaults to the species id. The whole file is optional: a client entity whose
 * identifier is the species id is used with default shadow and GUI settings.
 *
 * <p><b>format_version 1</b> (earlier releases) names geometry, texture, animation file, controller and
 * texture variants directly; it is still read and converted to an equivalent client entity.
 */
public record ClientSpeciesDefinition(
    int formatVersion,
    Optional<Identifier> clientEntity,
    Optional<Identifier> geometry,
    Optional<Identifier> texture,
    Optional<Identifier> animations,
    Optional<Identifier> controller,
    float shadowRadius,
    float renderScale,
    ClientSpeciesGuiSettings gui,
    List<TextureVariant> variants
) {
    public static final int CURRENT_FORMAT_VERSION = 2;

    public static final ClientSpeciesDefinition DEFAULT = new ClientSpeciesDefinition(CURRENT_FORMAT_VERSION, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), 0.5F, 1.0F, ClientSpeciesGuiSettings.DEFAULT, List.of());

    private static final Codec<Integer> FORMAT_VERSION_CODEC = Codec.INT.validate(
        v -> v >= 1 && v <= CURRENT_FORMAT_VERSION
            ? DataResult.success(v)
            : DataResult.error(() -> "unsupported format_version " + v + " (this build understands 1.." + CURRENT_FORMAT_VERSION + ")"));

    public static final Codec<ClientSpeciesDefinition> CODEC = RecordCodecBuilder.<ClientSpeciesDefinition>create(i -> i.group(
        FORMAT_VERSION_CODEC.optionalFieldOf("format_version", CURRENT_FORMAT_VERSION).forGetter(ClientSpeciesDefinition::formatVersion),
        Identifier.CODEC.optionalFieldOf("client_entity").forGetter(ClientSpeciesDefinition::clientEntity),
        Identifier.CODEC.optionalFieldOf("geometry").forGetter(ClientSpeciesDefinition::geometry),
        Identifier.CODEC.optionalFieldOf("texture").forGetter(ClientSpeciesDefinition::texture),
        Identifier.CODEC.optionalFieldOf("animations").forGetter(ClientSpeciesDefinition::animations),
        Identifier.CODEC.optionalFieldOf("controller").forGetter(ClientSpeciesDefinition::controller),
        ExtraCodecs.floatRange(0.0F, 16.0F).optionalFieldOf("shadow_radius", 0.5F).forGetter(ClientSpeciesDefinition::shadowRadius),
        ExtraCodecs.floatRange(Float.MIN_NORMAL, 16.0F).optionalFieldOf("render_scale", 1.0F).forGetter(ClientSpeciesDefinition::renderScale),
        ClientSpeciesGuiSettings.CODEC.optionalFieldOf("gui", ClientSpeciesGuiSettings.DEFAULT).forGetter(ClientSpeciesDefinition::gui),
        TextureVariant.CODEC.listOf().optionalFieldOf("variants", List.of()).forGetter(ClientSpeciesDefinition::variants)
    ).apply(i, ClientSpeciesDefinition::new)).validate(ClientSpeciesDefinition::check);

    private static DataResult<ClientSpeciesDefinition> check(ClientSpeciesDefinition definition) {
        if (definition.formatVersion() == 1 && (definition.geometry().isEmpty() || definition.texture().isEmpty())) {
            return DataResult.error(() -> "format_version 1 needs 'geometry' and 'texture'; use format_version 2 with a client entity instead");
        }
        return DataResult.success(definition);
    }

    public boolean isLegacy() {
        return this.formatVersion == 1;
    }

    public record TextureVariant(String id, int weight, Identifier texture) {
        public static final Codec<TextureVariant> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.STRING.fieldOf("id").forGetter(TextureVariant::id),
            ExtraCodecs.POSITIVE_INT.optionalFieldOf("weight", 1).forGetter(TextureVariant::weight),
            Identifier.CODEC.fieldOf("texture").forGetter(TextureVariant::texture)
        ).apply(i, TextureVariant::new));
    }
}
