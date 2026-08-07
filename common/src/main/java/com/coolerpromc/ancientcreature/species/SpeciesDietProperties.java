package com.coolerpromc.ancientcreature.species;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public record SpeciesDietProperties(Optional<Identifier> tag, List<Identifier> items) implements Predicate<ItemStack> {
    public static final SpeciesDietProperties EMPTY = new SpeciesDietProperties(Optional.empty(), List.of());

    private static final Codec<SpeciesDietProperties> TAG_CODEC = Codec.STRING.comapFlatMap(
        s -> s.startsWith("#")
            ? Identifier.read(s.substring(1)).map(id -> new SpeciesDietProperties(Optional.of(id), List.of()))
            : DataResult.error(() -> "expected a '#tag' string, got '" + s + "'"),
        diet -> "#" + diet.tag().orElseThrow()
    );

    private static final Codec<SpeciesDietProperties> LIST_CODEC = SpeciesCodecs.VANILLA_ID.listOf()
        .xmap(items -> new SpeciesDietProperties(Optional.empty(), items), SpeciesDietProperties::items);

    private static final Codec<SpeciesDietProperties> ENTRY_CODEC = Codec.either(TAG_CODEC, LIST_CODEC)
        .xmap(
            either -> either.map(t -> t, l -> l),
            diet -> diet.tag().isPresent() ? Either.left(diet) : Either.right(diet)
        );

    public static final Codec<SpeciesDietProperties> CODEC = RecordCodecBuilder.create(i -> i.group(
        ENTRY_CODEC.fieldOf("items").forGetter(diet -> diet)
    ).apply(i, diet -> diet));

    public static final StreamCodec<RegistryFriendlyByteBuf, SpeciesDietProperties> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.optional(Identifier.STREAM_CODEC), SpeciesDietProperties::tag,
        Identifier.STREAM_CODEC.apply(ByteBufCodecs.list()), SpeciesDietProperties::items,
        SpeciesDietProperties::new
    );

    public boolean isEmpty() {
        return this.tag.isEmpty() && this.items.isEmpty();
    }

    @Override
    public boolean test(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        if (this.tag.isPresent() && stack.is(TagKey.create(Registries.ITEM, this.tag.get()))) {
            return true;
        }
        for (Identifier id : this.items) {
            if (BuiltInRegistries.ITEM.containsKey(id) && stack.is(BuiltInRegistries.ITEM.getValue(id))) {
                return true;
            }
        }
        return false;
    }
}
