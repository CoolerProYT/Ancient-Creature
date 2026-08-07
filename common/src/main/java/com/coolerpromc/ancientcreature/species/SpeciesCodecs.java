package com.coolerpromc.ancientcreature.species;

import com.coolerpromc.ancientcreature.Constants;
import com.mojang.serialization.Codec;
import net.minecraft.resources.Identifier;

public final class SpeciesCodecs {
    public static Codec<Identifier> defaultNamespace(String namespace) {
        return Codec.STRING.comapFlatMap(
            s -> s.indexOf(':') < 0
                ? Identifier.read(namespace + ":" + s)
                : Identifier.read(s),
            Identifier::toString
        );
    }

    public static final Codec<Identifier> SPECIES_ID = defaultNamespace(Constants.MODID);

    public static final Codec<Identifier> VANILLA_ID = defaultNamespace(Identifier.DEFAULT_NAMESPACE);

    private SpeciesCodecs() {
    }
}
