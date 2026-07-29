package com.coolerpromc.ancientcreature.entity;

import com.coolerpromc.ancientcreature.data.component.ModDataComponents;
import com.coolerpromc.ancientcreature.tag.ModBiomeTags;
import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;
import net.minecraft.world.level.biome.Biome;

import java.util.function.Consumer;

public enum Species implements StringRepresentable, TooltipProvider {
    TRICERATOPS("triceratops", ModBiomeTags.SPAWNS_TRICERATOPS);

    public static final Codec<Species> CODEC = StringRepresentable.fromEnum(Species::values);
    public static final StreamCodec<RegistryFriendlyByteBuf, Species> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(CODEC);

    final String name;
    final TagKey<Biome> biomeTag;

    Species(String name, TagKey<Biome> biomeTag){
        this.name = name;
        this.biomeTag = biomeTag;
    }

    @Override
    public String getSerializedName() {
        return name;
    }

    public boolean isValidBiome(Holder<Biome> holder){
        return holder.is(this.biomeTag);
    }

    @Override
    public void addToTooltip(Item.TooltipContext context, Consumer<Component> consumer, TooltipFlag flag, DataComponentGetter components) {
        boolean identified = components.getOrDefault(ModDataComponents.IDENTIFIED.get(), false);
        String name = "§9" + Component.translatable("species.ancientcreature." + this.name).getString();
        if (!identified){
            name = Component.translatable("species.ancientcreature.unidentified").getString();
        }
        consumer.accept(Component.translatable("tooltip.ancientcreature.species", name));
    }
}
