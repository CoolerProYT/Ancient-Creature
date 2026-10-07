package com.coolerpromc.ancientcreature.species;

import com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorComponent;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityDimensions;

import java.util.List;
import java.util.Optional;

public record SpeciesDefinition(
    int formatVersion,
    SpeciesEntityCategory category,
    SpeciesPhysicalProperties physical,
    SpeciesAttributeProperties attributes,
    SpeciesGrowthProperties growth,
    SpeciesBehaviorDefinition behavior,
    SpeciesDietProperties diet,
    SpeciesSpawnProperties spawn,
    SpeciesSoundDefinition sounds,
    SpeciesHungerProperties hunger,
    Optional<Identifier> lootTable,
    List<SpeciesVariant> variants,
    Optional<SpeciesRespiration> respirationOverride,
    SpeciesCareProperties care,
    SpeciesRidingProperties riding,
    Optional<SpeciesHybridProperties> hybrid
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
        SpeciesSoundDefinition.EMPTY,
        SpeciesHungerProperties.DEFAULT,
        Optional.empty(),
        List.of(),
        Optional.empty(),
        SpeciesCareProperties.DEFAULT,
        SpeciesRidingProperties.DEFAULT,
        Optional.empty()
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
        SpeciesSoundDefinition.CODEC.optionalFieldOf("sounds", SpeciesSoundDefinition.EMPTY).forGetter(SpeciesDefinition::sounds),
        SpeciesHungerProperties.CODEC.optionalFieldOf("hunger", SpeciesHungerProperties.DEFAULT).forGetter(SpeciesDefinition::hunger),
        SpeciesCodecs.SPECIES_ID.optionalFieldOf("loot_table").forGetter(SpeciesDefinition::lootTable),
        SpeciesVariant.CODEC.listOf().optionalFieldOf("variants", List.of()).forGetter(SpeciesDefinition::variants),
        SpeciesRespiration.CODEC.optionalFieldOf("respiration").forGetter(SpeciesDefinition::respirationOverride),
        SpeciesCareProperties.CODEC.optionalFieldOf("care", SpeciesCareProperties.DEFAULT).forGetter(SpeciesDefinition::care),
        SpeciesRidingProperties.CODEC.optionalFieldOf("riding", SpeciesRidingProperties.DEFAULT).forGetter(SpeciesDefinition::riding),
        SpeciesHybridProperties.CODEC.optionalFieldOf("hybrid").forGetter(SpeciesDefinition::hybrid)
    ).apply(i, SpeciesDefinition::new));

    /**
     * Synced as the same structure the datapack file has, so new fields only need adding to {@link #CODEC}.
     */
    public static final StreamCodec<RegistryFriendlyByteBuf, SpeciesDefinition> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(CODEC);

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

    /** Hybrids have no fossils; they are only made by splicing their parents' genomes. */
    public boolean isHybrid() {
        return this.hybrid.isPresent();
    }

    public SpeciesRespiration respiration() {
        return this.respirationOverride.orElseGet(() -> SpeciesRespiration.defaultFor(this.category));
    }

    /** Position of {@code variant} in {@link #variants()}, or -1. Exposed to animations as {@code query.variant}. */
    public int variantIndex(String variant) {
        for (int i = 0; i < this.variants.size(); i++) {
            if (this.variants.get(i).id().equals(variant)) {
                return i;
            }
        }
        return -1;
    }

    public String rollVariant(net.minecraft.util.RandomSource random) {
        return SpeciesVariant.roll(this.variants, random);
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
