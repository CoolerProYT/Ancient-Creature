package com.coolerpromc.ancientcreature.species;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.attributes.Attribute;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public record SpeciesAttributeProperties(Map<Identifier, Double> values) {
    public static final SpeciesAttributeProperties EMPTY = new SpeciesAttributeProperties(Map.of());

    public static final Codec<SpeciesAttributeProperties> CODEC = Codec.unboundedMap(SpeciesCodecs.VANILLA_ID, Codec.DOUBLE).xmap(SpeciesAttributeProperties::new, SpeciesAttributeProperties::values);

    public static final StreamCodec<RegistryFriendlyByteBuf, SpeciesAttributeProperties> STREAM_CODEC = ByteBufCodecs.<RegistryFriendlyByteBuf, Identifier, Double, Map<Identifier, Double>>map(LinkedHashMap::new, Identifier.STREAM_CODEC, ByteBufCodecs.DOUBLE).map(SpeciesAttributeProperties::new, SpeciesAttributeProperties::values);

    public DataResult<List<Resolved>> resolve() {
        List<Resolved> resolved = new ArrayList<>(this.values.size());
        List<String> unknown = new ArrayList<>();

        for (Map.Entry<Identifier, Double> entry : this.values.entrySet()) {
            Optional<Holder.Reference<Attribute>> holder = BuiltInRegistries.ATTRIBUTE.get(entry.getKey());

            if (holder.isEmpty()) {
                unknown.add(entry.getKey().toString());
                continue;
            }

            double raw = entry.getValue();
            double sanitized = holder.get().value().sanitizeValue(raw);
            resolved.add(new Resolved(holder.get(), sanitized, sanitized != raw));
        }

        if (!unknown.isEmpty()) {
            return DataResult.error(() -> "unknown attribute(s): " + String.join(", ", unknown));
        }
        return DataResult.success(List.copyOf(resolved));
    }

    public record Resolved(Holder<Attribute> attribute, double value, boolean clamped) {
    }
}
