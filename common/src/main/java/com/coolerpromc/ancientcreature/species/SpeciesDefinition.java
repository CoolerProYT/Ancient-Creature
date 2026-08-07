package com.coolerpromc.ancientcreature.species;

import com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorComponent;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.EntityDimensions;

import java.util.List;

public record SpeciesDefinition(
    int formatVersion,
    SpeciesEntityCategory category,
    SpeciesPhysicalProperties physical,
    SpeciesAttributeProperties attributes,
    SpeciesGrowthProperties growth,
    SpeciesBehaviorDefinition behavior,
    SpeciesDietProperties diet,
    SpeciesSpawnProperties spawn,
    SpeciesSoundDefinition sounds
) {
    public static final int CURRENT_FORMAT_VERSION = 1;

    public static final SpeciesDefinition FALLBACK = new SpeciesDefinition(
        CURRENT_FORMAT_VERSION,
        SpeciesEntityCategory.LAND,
        SpeciesPhysicalProperties.DEFAULT,
        SpeciesAttributeProperties.EMPTY,
        SpeciesGrowthProperties.DEFAULT,
        SpeciesBehaviorDefinition.EMPTY,
        SpeciesDietProperties.EMPTY,
        SpeciesSpawnProperties.DEFAULT,
        SpeciesSoundDefinition.EMPTY
    );

    private static final Codec<Integer> FORMAT_VERSION_CODEC = Codec.INT.validate(
        v -> v >= 1 && v <= CURRENT_FORMAT_VERSION
            ? DataResult.success(v)
            : DataResult.error(() -> "unsupported format_version " + v + " (this build understands 1.." + CURRENT_FORMAT_VERSION + ")"));

    public static final Codec<SpeciesDefinition> CODEC = RecordCodecBuilder.create(i -> i.group(
        FORMAT_VERSION_CODEC.optionalFieldOf("format_version", CURRENT_FORMAT_VERSION).forGetter(SpeciesDefinition::formatVersion),
        SpeciesEntityCategory.CODEC.optionalFieldOf("entity_category", SpeciesEntityCategory.LAND).forGetter(SpeciesDefinition::category),
        SpeciesPhysicalProperties.CODEC.fieldOf("physical").forGetter(SpeciesDefinition::physical),
        SpeciesAttributeProperties.CODEC.optionalFieldOf("attributes", SpeciesAttributeProperties.EMPTY).forGetter(SpeciesDefinition::attributes),
        SpeciesGrowthProperties.CODEC.optionalFieldOf("growth", SpeciesGrowthProperties.DEFAULT).forGetter(SpeciesDefinition::growth),
        SpeciesBehaviorDefinition.CODEC.optionalFieldOf("behavior", SpeciesBehaviorDefinition.EMPTY).forGetter(SpeciesDefinition::behavior),
        SpeciesDietProperties.CODEC.optionalFieldOf("diet", SpeciesDietProperties.EMPTY).forGetter(SpeciesDefinition::diet),
        SpeciesSpawnProperties.CODEC.optionalFieldOf("spawn", SpeciesSpawnProperties.DEFAULT).forGetter(SpeciesDefinition::spawn),
        SpeciesSoundDefinition.CODEC.optionalFieldOf("sounds", SpeciesSoundDefinition.EMPTY).forGetter(SpeciesDefinition::sounds)
    ).apply(i, SpeciesDefinition::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, SpeciesDefinition> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_INT, SpeciesDefinition::formatVersion,
        SpeciesEntityCategory.STREAM_CODEC, SpeciesDefinition::category,
        SpeciesPhysicalProperties.STREAM_CODEC, SpeciesDefinition::physical,
        SpeciesAttributeProperties.STREAM_CODEC, SpeciesDefinition::attributes,
        SpeciesGrowthProperties.STREAM_CODEC, SpeciesDefinition::growth,
        SpeciesBehaviorDefinition.STREAM_CODEC, SpeciesDefinition::behavior,
        SpeciesDietProperties.STREAM_CODEC, SpeciesDefinition::diet,
        SpeciesSpawnProperties.STREAM_CODEC, SpeciesDefinition::spawn,
        SpeciesSoundDefinition.STREAM_CODEC, SpeciesDefinition::sounds,
        SpeciesDefinition::new
    );

    public DataResult<SpeciesDefinition> validate() {
        DataResult<List<SpeciesAttributeProperties.Resolved>> attributeResult = this.attributes.resolve();
        if (attributeResult.error().isPresent()) {
            return DataResult.error(() -> attributeResult.error().get().message());
        }
        if (this.behavior.isEmpty()) {
            return DataResult.error(() -> "no behavior: set 'behavior.profile' or a non-empty 'behavior.components'");
        }
        return DataResult.success(this);
    }

    public List<CreatureBehaviorComponent> resolvedBehaviors() {
        return this.behavior.resolve();
    }

    public EntityDimensions dimensionsFor(float scale) {
        return this.physical.toDimensions(scale);
    }

    public float scaleFor(boolean baby) {
        return this.growth.scaleFor(baby);
    }
}
