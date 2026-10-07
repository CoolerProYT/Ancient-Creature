package com.coolerpromc.ancientcreature.datagen.loot;

import com.coolerpromc.ancientcreature.entity.Species;
import com.coolerpromc.ancientcreature.item.FossilPart;
import com.coolerpromc.ancientcreature.item.ModItems;
import com.coolerpromc.ancientcreature.loot.custom.SetFossilDataFunction;
import com.coolerpromc.ancientcreature.worldgen.structure.DigSiteType;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.coolerpromc.ancientcreature.item.FossilPart.*;

/** Shared building blocks for the loot tables that hand out fossils. */
final class FossilLoot {
    /** Sea creatures: what coastal sites, ocean ruins and the drowned turn up. */
    static final List<Species> MARINE = List.of(Species.of("plesiosaurus"), Species.of("mosasaurus"), Species.of("dunkleosteus"), Species.MEGALODON, Species.PTERANODON);
    /** Ice-age animals: what frozen specimens preserve. */
    static final List<Species> ICE_AGE = List.of(Species.of("woolly_mammoth"), Species.of("woolly_rhinoceros"), Species.of("dire_wolf"), Species.of("smilodon"));

    private FossilLoot() {
    }

    /** A fossil part of any of the given kinds, chosen evenly. */
    static LootPoolSingletonContainer.Builder<?> fossil(HolderGetter<FossilPart> parts, List<ResourceKey<FossilPart>> kinds, List<Species> species, float bonus) {
        return LootItem.lootTableItem(ModItems.FOSSIL_PART).apply(SetFossilDataFunction.setData(holders(parts, kinds), species, bonus));
    }

    static LootPoolSingletonContainer.Builder<?> fossil(HolderGetter<FossilPart> parts, List<ResourceKey<FossilPart>> kinds) {
        return fossil(parts, kinds, List.of(), 0.0f);
    }

    /** One weighted entry per part, so a table can favour skulls in one biome and claws in another. */
    static void addWeightedFossils(LootPool.Builder pool, HolderGetter<FossilPart> parts, Map<ResourceKey<FossilPart>, Integer> weights, List<Species> species, float bonus) {
        weights.forEach((part, weight) -> pool.add(fossil(parts, List.of(part), species, bonus).setWeight(weight)));
    }

    /** Insertion-ordered map so generated JSON stays stable between datagen runs. */
    static <K> Map<K, Integer> ordered(Object... keysAndWeights) {
        Map<K, Integer> map = new LinkedHashMap<>();
        for (int i = 0; i < keysAndWeights.length; i += 2) {
            @SuppressWarnings("unchecked")
            K key = (K) keysAndWeights[i];
            map.put(key, (Integer) keysAndWeights[i + 1]);
        }
        return map;
    }

    static List<Holder<FossilPart>> holders(HolderGetter<FossilPart> parts, List<ResourceKey<FossilPart>> kinds) {
        return kinds.stream().<Holder<FossilPart>>map(parts::getOrThrow).toList();
    }

    static Map<ResourceKey<FossilPart>, Integer> weights(Object... partsAndWeights) {
        Map<ResourceKey<FossilPart>, Integer> map = new LinkedHashMap<>();
        for (int i = 0; i < partsAndWeights.length; i += 2) {
            @SuppressWarnings("unchecked")
            ResourceKey<FossilPart> part = (ResourceKey<FossilPart>) partsAndWeights[i];
            map.put(part, (Integer) partsAndWeights[i + 1]);
        }
        return map;
    }

    /** Which parts each dig site favours; the shape of the local fauna shows in what gets dug up. */
    static Map<ResourceKey<FossilPart>, Integer> digSiteParts(DigSiteType type) {
        return switch (type) {
            case PLAINS -> weights(RIB, 4, VERTEBRA, 4, LIMB, 4, TOOTH, 3, CLAW, 2, SKULL, 1, EGG, 1);
            case ARID -> weights(CLAW, 5, TOOTH, 5, LIMB, 3, RIB, 2, SKULL, 1, EGG, 1);
            case SAVANNA -> weights(LIMB, 5, VERTEBRA, 5, RIB, 3, TOOTH, 2, SKULL, 1, EGG, 1);
            case BADLANDS -> weights(SKULL, 4, CLAW, 4, TOOTH, 3, RIB, 2, LIMB, 2, EGG, 1);
            case JUNGLE -> weights(TOOTH, 4, CLAW, 3, RIB, 3, AMBER, 3, LIMB, 2, VERTEBRA, 2, EGG, 1);
            case COLD -> weights(VERTEBRA, 4, TOOTH, 4, LIMB, 3, SKULL, 2, RIB, 2, EGG, 1);
            case SWAMP -> weights(RIB, 5, VERTEBRA, 4, LIMB, 3, TOOTH, 2, SKULL, 1, EGG, 1);
            case HIGHLAND -> weights(SKULL, 5, VERTEBRA, 4, TOOTH, 3, RIB, 2, LIMB, 2, EGG, 1);
            case COASTAL -> weights(TOOTH, 6, VERTEBRA, 4, RIB, 3, SKULL, 2, EGG, 1);
        };
    }

    static List<Species> digSiteSpecies(DigSiteType type) {
        return type == DigSiteType.COASTAL ? MARINE : List.of();
    }

    /** Cold sites keep their bones frozen and noticeably better preserved. */
    static float digSiteBonus(DigSiteType type) {
        return type == DigSiteType.COLD ? 0.1f : 0.0f;
    }
}
