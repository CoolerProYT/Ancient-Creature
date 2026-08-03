package com.coolerpromc.ancientcreature.data.component;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.data.component.custom.DNAData;
import com.coolerpromc.ancientcreature.data.component.custom.FossilDamageRate;
import com.coolerpromc.ancientcreature.data.component.custom.FossilData;
import com.coolerpromc.ancientcreature.data.component.custom.GenomeData;
import com.coolerpromc.ancientcreature.entity.Species;
import com.coolerpromc.ancientcreature.platform.Services;
import com.coolerpromc.ancientcreature.platform.util.RegistryHandler;
import net.minecraft.core.component.DataComponentType;

import java.util.function.UnaryOperator;

public class ModDataComponents {
    public static final RegistryHandler.Components<FossilData> FOSSIL_DATA = register("fossil_data", b -> b.persistent(FossilData.CODEC).networkSynchronized(FossilData.STREAM_CODEC).cacheEncoding());
    public static final RegistryHandler.Components<DNAData> DNA_DATA = register("dna_data", b -> b.persistent(DNAData.CODEC).networkSynchronized(DNAData.STREAM_CODEC).cacheEncoding());
    public static final RegistryHandler.Components<GenomeData> GENOME_DATA = register("genome_data", b -> b.persistent(GenomeData.CODEC).networkSynchronized(GenomeData.STREAM_CODEC).cacheEncoding());
    public static final RegistryHandler.Components<Species> SPECIES = register("species", b -> b.persistent(Species.CODEC).networkSynchronized(Species.STREAM_CODEC).cacheEncoding());
    public static final RegistryHandler.Components<FossilDamageRate> FOSSIL_DAMAGE_RATE = register("fossil_damage_rate", b -> b.persistent(FossilDamageRate.CODEC).networkSynchronized(FossilDamageRate.STREAM_CODEC).cacheEncoding());

    public static <T> RegistryHandler.Components<T> register(String name, UnaryOperator<DataComponentType.Builder<T>> unaryOperator){
        return Services.REGISTRY.registerDataComponent(name, unaryOperator);
    }

    public static void init(){
        Constants.LOG.info("Registering Data Components.");
    }
}
