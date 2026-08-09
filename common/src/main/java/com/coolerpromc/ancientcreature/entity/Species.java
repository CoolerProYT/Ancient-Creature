package com.coolerpromc.ancientcreature.entity;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.species.SpeciesCodecs;
import com.coolerpromc.ancientcreature.species.SpeciesDefinition;
import com.coolerpromc.ancientcreature.species.SpeciesManager;
import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;
import net.minecraft.world.level.biome.Biome;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public record Species(Identifier id) implements TooltipProvider, Comparable<Species> {
    public static final Codec<Species> CODEC = SpeciesCodecs.SPECIES_ID.xmap(Species::new, Species::id);
    public static final StreamCodec<RegistryFriendlyByteBuf, Species> STREAM_CODEC = Identifier.STREAM_CODEC.map(Species::new, Species::id).cast();

    public static final Species TRICERATOPS = of("triceratops");
    public static final Species TYRANNOSAURUS_REX = of("tyrannosaurus_rex");
    public static final Species MEGALODON = of("megalodon");
    public static final Species PTERANODON = of("pteranodon");
    public static final Species ANKYLOSAURUS = of("ankylosaurus");
    public static final Species DEINONYCHUS = of("deinonychus");
    public static final Species BRACHIOSAURUS = of("brachiosaurus");

    public static Species of(String path) {
        return new Species(Constants.id(path));
    }

    public static List<Species> values() {
        SpeciesManager manager = SpeciesManager.SERVER.isEmpty() ? SpeciesManager.CLIENT : SpeciesManager.SERVER;
        return manager.ids().stream().map(Species::new).sorted().toList();
    }

    public Optional<SpeciesDefinition> definition() {
        Optional<SpeciesDefinition> server = SpeciesManager.SERVER.getOptional(this.id);
        return server.isPresent() ? server : SpeciesManager.CLIENT.getOptional(this.id);
    }

    public SpeciesDefinition definitionOrFallback() {
        return this.definition().orElse(SpeciesDefinition.FALLBACK);
    }

    public boolean isLoaded() {
        return this.definition().isPresent();
    }

    public String getSerializedName() {
        return this.id.getPath();
    }

    public String name() {
        return this.id.getPath();
    }

    public String translationKey() {
        return "species." + this.id.getNamespace() + "." + this.id.getPath();
    }

    public Component displayName() {
        return Component.translatable(this.translationKey());
    }

    public boolean isValidBiome(Holder<Biome> holder) {
        return this.definitionOrFallback().spawn().isValidBiome(holder);
    }

    public int getIncubationTime() {
        return this.definitionOrFallback().spawn().incubationTime();
    }

    public EntityType<?> getEntityType() {
        return ModEntities.ANCIENT_CREATURE.get();
    }

    @Override
    public int compareTo(Species other) {
        return COMPARATOR.compare(this, other);
    }

    private static final Comparator<Species> COMPARATOR =
        Comparator.comparing((Species s) -> s.id.getNamespace()).thenComparing(s -> s.id.getPath());

    @Override
    public void addToTooltip(Item.TooltipContext context, Consumer<Component> consumer, TooltipFlag flag, DataComponentGetter components) {
        String name = "§9" + this.displayName().getString();
        consumer.accept(Component.translatable("tooltip.ancientcreature.species", name));
    }

    @Override
    public String toString() {
        return this.id.toString();
    }
}
