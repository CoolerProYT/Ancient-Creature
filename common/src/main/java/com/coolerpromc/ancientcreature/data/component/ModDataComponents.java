package com.coolerpromc.ancientcreature.data.component;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.data.component.custom.FossilCompleteness;
import com.coolerpromc.ancientcreature.data.component.custom.FossilDamageRate;
import com.coolerpromc.ancientcreature.data.component.custom.FossilPart;
import com.coolerpromc.ancientcreature.entity.Species;
import com.coolerpromc.ancientcreature.platform.Services;
import com.coolerpromc.ancientcreature.platform.util.RegistryHandler;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;

import java.util.function.UnaryOperator;

public class ModDataComponents {
    public static final RegistryHandler.Components<Boolean> IS_DIRTY = register("is_dirty", b -> b.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).cacheEncoding());
    public static final RegistryHandler.Components<FossilDamageRate> FOSSIL_DAMAGE_RATE = register("fossil_damage_rate", b -> b.persistent(FossilDamageRate.CODEC).networkSynchronized(FossilDamageRate.STREAM_CODEC).cacheEncoding());
    public static final RegistryHandler.Components<FossilCompleteness> FOSSIL_COMPLETENESS = register("fossil_completeness", b -> b.persistent(FossilCompleteness.CODEC).networkSynchronized(FossilCompleteness.STREAM_CODEC).cacheEncoding());
    public static final RegistryHandler.Components<Boolean> IDENTIFIED = register("identified", b -> b.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).cacheEncoding());
    public static final RegistryHandler.Components<FossilPart> FOSSIL_PART = register("fossil_part", b -> b.persistent(FossilPart.CODEC).networkSynchronized(FossilPart.STREAM_CODEC).cacheEncoding());
    public static final RegistryHandler.Components<Species> SPECIES = register("species", b -> b.persistent(Species.CODEC).networkSynchronized(Species.STREAM_CODEC).cacheEncoding());

    public static <T> RegistryHandler.Components<T> register(String name, UnaryOperator<DataComponentType.Builder<T>> unaryOperator){
        return Services.REGISTRY.registerDataComponent(name, unaryOperator);
    }

    public static void init(){
        Constants.LOG.info("Registering Data Components.");
    }
}
