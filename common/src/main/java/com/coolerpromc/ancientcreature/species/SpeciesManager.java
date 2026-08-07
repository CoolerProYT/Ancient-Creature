package com.coolerpromc.ancientcreature.species;

import com.coolerpromc.ancientcreature.Constants;
import net.minecraft.resources.Identifier;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public final class SpeciesManager {
    public static final SpeciesManager SERVER = new SpeciesManager("server");
    public static final SpeciesManager CLIENT = new SpeciesManager("client");

    private final String side;
    private volatile Map<Identifier, SpeciesDefinition> definitions = Map.of();
    private volatile int generation;

    private SpeciesManager(String side) {
        this.side = side;
    }

    public SpeciesDefinition get(Identifier id) {
        SpeciesDefinition definition = this.definitions.get(id);
        return definition != null ? definition : SpeciesDefinition.FALLBACK;
    }

    public Optional<SpeciesDefinition> getOptional(Identifier id) {
        return Optional.ofNullable(this.definitions.get(id));
    }

    public boolean contains(Identifier id) {
        return this.definitions.containsKey(id);
    }

    public Collection<SpeciesDefinition> values() {
        return this.definitions.values();
    }

    public Set<Identifier> ids() {
        return this.definitions.keySet();
    }

    public Map<Identifier, SpeciesDefinition> all() {
        return this.definitions;
    }

    public boolean isEmpty() {
        return this.definitions.isEmpty();
    }

    public int generation() {
        return this.generation;
    }

    public void replaceAll(Map<Identifier, SpeciesDefinition> loaded) {
        this.definitions = Map.copyOf(loaded);
        this.generation++;
        Constants.LOG.info("Loaded {} Ancient Creature species definitions ({} side)", this.definitions.size(), this.side);
    }

    public void clear() {
        this.definitions = Map.of();
        this.generation++;
    }
}
