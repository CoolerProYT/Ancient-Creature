package com.coolerpromc.ancientcreature.species;

import com.coolerpromc.ancientcreature.entity.behavior.BehaviorProfiles;
import com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorComponent;
import com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public record SpeciesBehaviorDefinition(Optional<Identifier> profile, List<CreatureBehaviorComponent> components) {
    public static final SpeciesBehaviorDefinition EMPTY = new SpeciesBehaviorDefinition(Optional.empty(), List.of());

    public static final Codec<SpeciesBehaviorDefinition> CODEC = RecordCodecBuilder.create(i -> i.group(
        BehaviorProfiles.PROFILE_ID_CODEC.optionalFieldOf("profile").forGetter(SpeciesBehaviorDefinition::profile),
        CreatureBehaviorComponent.CODEC.listOf().optionalFieldOf("components", List.of()).forGetter(SpeciesBehaviorDefinition::components)
    ).apply(i, SpeciesBehaviorDefinition::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, SpeciesBehaviorDefinition> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.optional(Identifier.STREAM_CODEC), SpeciesBehaviorDefinition::profile,
        CreatureBehaviorComponent.STREAM_CODEC.apply(ByteBufCodecs.list()), SpeciesBehaviorDefinition::components,
        SpeciesBehaviorDefinition::new
    );

    public List<CreatureBehaviorComponent> resolve() {
        if (this.profile.isEmpty()) {
            return List.copyOf(this.components);
        }

        Map<CreatureBehaviorType<?>, CreatureBehaviorComponent> merged = new LinkedHashMap<>();
        for (CreatureBehaviorComponent component : BehaviorProfiles.get(this.profile.get()).orElse(List.of())) {
            merged.put(component.config().type(), component);
        }
        for (CreatureBehaviorComponent component : this.components) {
            merged.put(component.config().type(), component);
        }
        return List.copyOf(new ArrayList<>(merged.values()));
    }

    public boolean isEmpty() {
        return this.profile.isEmpty() && this.components.isEmpty();
    }
}
