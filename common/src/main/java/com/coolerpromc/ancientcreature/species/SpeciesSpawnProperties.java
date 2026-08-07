package com.coolerpromc.ancientcreature.species;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.biome.Biome;

public record SpeciesSpawnProperties(Identifier biomeTag, int incubationTime) {
    public static final int DEFAULT_INCUBATION_TIME = 1000;

    public static final SpeciesSpawnProperties DEFAULT = new SpeciesSpawnProperties(BiomeTags.IS_OVERWORLD.location(), DEFAULT_INCUBATION_TIME);

    private static final Codec<Identifier> TAG_CODEC = Codec.STRING.comapFlatMap(
        s -> s.startsWith("#") ? Identifier.read(s.substring(1)) : DataResult.error(() -> "expected a '#tag' string, got '" + s + "'"),
        id -> "#" + id
    );

    public static final Codec<SpeciesSpawnProperties> CODEC = RecordCodecBuilder.create(i -> i.group(
        TAG_CODEC.optionalFieldOf("biomes", DEFAULT.biomeTag()).forGetter(SpeciesSpawnProperties::biomeTag),
        ExtraCodecs.POSITIVE_INT.optionalFieldOf("incubation_time", DEFAULT_INCUBATION_TIME).forGetter(SpeciesSpawnProperties::incubationTime)
    ).apply(i, SpeciesSpawnProperties::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, SpeciesSpawnProperties> STREAM_CODEC = StreamCodec.composite(
        Identifier.STREAM_CODEC, SpeciesSpawnProperties::biomeTag,
        ByteBufCodecs.VAR_INT, SpeciesSpawnProperties::incubationTime,
        SpeciesSpawnProperties::new
    );

    public TagKey<Biome> biomeTagKey() {
        return TagKey.create(Registries.BIOME, this.biomeTag);
    }

    public boolean isValidBiome(Holder<Biome> biome) {
        return biome.is(this.biomeTagKey());
    }
}
