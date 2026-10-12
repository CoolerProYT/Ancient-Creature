package com.coolerpromc.ancientcreature.data.component;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.data.component.custom.CreatureGenome;
import com.coolerpromc.ancientcreature.data.component.custom.DNAData;
import com.coolerpromc.ancientcreature.data.component.custom.FossilDamageRate;
import com.coolerpromc.ancientcreature.data.component.custom.FossilOreBonus;
import com.coolerpromc.ancientcreature.data.component.custom.FossilData;
import com.coolerpromc.ancientcreature.data.component.custom.GenomeData;
import com.coolerpromc.ancientcreature.entity.Species;
import com.coolerpromc.ancientcreature.platform.Services;
import com.coolerpromc.ancientcreature.platform.util.RegistryHandler;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

import java.util.function.UnaryOperator;

public class ModDataComponents {
    public static final RegistryHandler.Components<FossilData> FOSSIL_DATA = register("fossil_data", b -> b.persistent(FossilData.CODEC).networkSynchronized(FossilData.STREAM_CODEC).cacheEncoding());
    public static final RegistryHandler.Components<DNAData> DNA_DATA = register("dna_data", b -> b.persistent(DNAData.CODEC).networkSynchronized(DNAData.STREAM_CODEC).cacheEncoding());
    public static final RegistryHandler.Components<GenomeData> GENOME_DATA = register("genome_data", b -> b.persistent(GenomeData.CODEC).networkSynchronized(GenomeData.STREAM_CODEC).cacheEncoding());
    public static final RegistryHandler.Components<Species> SPECIES = register("species", b -> b.persistent(Species.CODEC).networkSynchronized(Species.STREAM_CODEC).cacheEncoding());
    /** The genome a creature will hatch from: on completed cartridges, fertilized eggs and capsules. */
    public static final RegistryHandler.Components<CreatureGenome> GENOME = register("genome", b -> b.persistent(CreatureGenome.CODEC).networkSynchronized(CreatureGenome.STREAM_CODEC).cacheEncoding());
    public static final RegistryHandler.Components<FossilDamageRate> FOSSIL_DAMAGE_RATE = register("fossil_damage_rate", b -> b.persistent(FossilDamageRate.CODEC).networkSynchronized(FossilDamageRate.STREAM_CODEC).cacheEncoding());
    public static final RegistryHandler.Components<FossilOreBonus> FOSSIL_ORE_BONUS = register("fossil_ore_bonus", b -> b.persistent(FossilOreBonus.CODEC).networkSynchronized(FossilOreBonus.STREAM_CODEC).cacheEncoding());

    /**
     * The species an item stands for. A genome (cartridge data, or an embryo's genome) wins over the plain
     * species component, whose item default is Triceratops, so a stack missing one of them is not
     * silently turned into a Triceratops.
     */
    public static @Nullable Species speciesOf(ItemStack stack) {
        GenomeData genomeData = stack.get(GENOME_DATA.get());
        if (genomeData != null && genomeData.species() != null) {
            return genomeData.species();
        }
        CreatureGenome genome = stack.get(GENOME.get());
        if (genome != null && genome.species() != null) {
            return genome.species();
        }
        return stack.get(SPECIES.get());
    }

    public static <T> RegistryHandler.Components<T> register(String name, UnaryOperator<DataComponentType.Builder<T>> unaryOperator){
        return Services.REGISTRY.registerDataComponent(name, unaryOperator);
    }

    public static void init(){
        Constants.LOG.info("Registering Data Components.");
    }
}
