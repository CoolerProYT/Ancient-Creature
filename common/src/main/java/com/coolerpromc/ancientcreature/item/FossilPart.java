package com.coolerpromc.ancientcreature.item;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.registry.ModRegistries;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.*;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.ArrayList;
import java.util.List;

public record FossilPart(float dnaExtractingBonus, float identifyFailChance, float fossilDamageRate, UniformGenerator completeness) {
    public static final Codec<FossilPart> DIRECT_CODEC = RecordCodecBuilder.create(i -> i.group(
        Codec.FLOAT.fieldOf("dnaExtractingBonus").forGetter(FossilPart::dnaExtractingBonus),
        Codec.FLOAT.fieldOf("identifyFailChance").forGetter(FossilPart::identifyFailChance),
        Codec.FLOAT.fieldOf("fossilDamageRate").forGetter(FossilPart::fossilDamageRate),
        UniformGenerator.MAP_CODEC.fieldOf("completeness").forGetter(FossilPart::completeness)
    ).apply(i, FossilPart::new));
    public static final Codec<Holder<FossilPart>> CODEC = RegistryFileCodec.create(ModRegistries.FOSSIL_PART, DIRECT_CODEC);
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<FossilPart>> STREAM_CODEC = ByteBufCodecs.holderRegistry(ModRegistries.FOSSIL_PART);

    public static final FossilPart EMPTY = new FossilPart(0, 1, 1, UniformGenerator.between(0, 0.01f));
    
    public static final List<ResourceKey<FossilPart>> builtinKeys = new ArrayList<>();

    public static final ResourceKey<FossilPart> RIB = key("rib");
    public static final ResourceKey<FossilPart> TOOTH = key("tooth");
    public static final ResourceKey<FossilPart> SKULL = key("skull");
    public static final ResourceKey<FossilPart> VERTEBRA = key("vertebra");
    public static final ResourceKey<FossilPart> LIMB = key("limb");
    public static final ResourceKey<FossilPart> CLAW = key("claw");
    public static final ResourceKey<FossilPart> EGG = key("egg");

    public static void bootstrap(BootstrapContext<FossilPart> context){
        register(context, RIB, new FossilPart(0.04f, 0.3f, 0.1f, UniformGenerator.between(0.05f, 0.2f)));
        register(context, TOOTH, new FossilPart(0.16f, 0.01f, 0.1f, UniformGenerator.between(0.3f, 0.6f)));
        register(context, SKULL, new FossilPart(0.12f, 0.1f, 0.1f, UniformGenerator.between(0.2f, 0.4f)));
        register(context, VERTEBRA, new FossilPart(0.08f, 0.2f, 0.1f, UniformGenerator.between(0.1f, 0.3f)));
        register(context, LIMB, new FossilPart(0.0f, 0.35f, 0.1f, UniformGenerator.between(0.01f, 0.15f)));
        register(context, CLAW, new FossilPart(-0.04f, 0.5f, 0.1f, UniformGenerator.between(0.01f, 0.1f)));
        register(context, EGG, new FossilPart(0.2f, 0.2f, 0.1f, UniformGenerator.between(0.6f, 0.8f)));
    }
    
    private static void register(BootstrapContext<FossilPart> context, ResourceKey<FossilPart> key, FossilPart fossilPart){
        context.register(key, fossilPart);
        builtinKeys.add(key);
    }

    public static ResourceKey<FossilPart> key(String name){
        return ResourceKey.create(ModRegistries.FOSSIL_PART, Constants.id(name));
    }

    public static List<Holder<FossilPart>> all(HolderLookup.Provider registryAccess){
        HolderLookup.RegistryLookup<FossilPart> fossilParts = registryAccess.lookupOrThrow(ModRegistries.FOSSIL_PART);
        return fossilParts.listElements().<Holder<FossilPart>>map(holder -> holder).toList();
    }
}