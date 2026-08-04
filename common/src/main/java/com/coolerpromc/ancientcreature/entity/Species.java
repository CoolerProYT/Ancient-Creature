package com.coolerpromc.ancientcreature.entity;

import com.coolerpromc.ancientcreature.platform.util.RegistryHandler;
import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;
import net.minecraft.world.level.biome.Biome;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.function.Consumer;

public enum Species implements StringRepresentable, TooltipProvider {
    TRICERATOPS("triceratops", BiomeTags.IS_OVERWORLD, 1000, ModEntities.TRICERATOPS),
    TYRANNOSAURUS_REX("tyrannosaurus_rex", BiomeTags.IS_OVERWORLD, 1600, ModEntities.TYRANNOSAURUS_REX),
    MEGALODON("megalodon", BiomeTags.IS_OCEAN, 2000, ModEntities.MEGALODON);

    public static final Codec<Species> CODEC = StringRepresentable.fromEnum(Species::values);
    public static final StreamCodec<RegistryFriendlyByteBuf, Species> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(CODEC);

    final String name;
    final TagKey<Biome> biomeTag;
    final int incubationTime;
    final RegistryHandler.Entities<?> entityType;

    Species(String name, TagKey<Biome> biomeTag, int incubationTime, RegistryHandler.Entities<?> entityType){
        this.name = name;
        this.biomeTag = biomeTag;
        this.incubationTime = incubationTime;
        this.entityType = entityType;
    }

    @Override
    public String getSerializedName() {
        return name;
    }

    public boolean isValidBiome(Holder<Biome> holder){
        return holder.is(this.biomeTag);
    }

    public int getIncubationTime() {
        return incubationTime;
    }

    public EntityType<?> getEntityType() {
        return entityType.get();
    }

    @Override
    public void addToTooltip(Item.TooltipContext context, Consumer<Component> consumer, TooltipFlag flag, DataComponentGetter components) {
        String name = "§9" + Component.translatable("species.ancientcreature." + this.name).getString();
        consumer.accept(Component.translatable("tooltip.ancientcreature.species", name));
    }

    public static @Nullable Species byEntityType(EntityType<?> type){
        return Arrays.stream(values()).filter(e -> e.entityType.get().equals(type)).findFirst().orElse(null);
    }
}
