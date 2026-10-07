package com.coolerpromc.ancientcreature.loot.custom;

import com.coolerpromc.ancientcreature.entity.Species;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.network.Filterable;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.WrittenBookContent;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Writes a dig site's field notes into a book: the region surveyed, the species whose remains have
 * been recorded there, and a couple of excavation tips.
 */
public class FieldNotesFunction extends LootItemConditionalFunction {
    public static final MapCodec<FieldNotesFunction> CODEC = RecordCodecBuilder.mapCodec(instance -> commonFields(instance).apply(instance, FieldNotesFunction::new));

    private static final int TIP_COUNT = 8;
    private static final int TIPS_PER_BOOK = 2;

    public FieldNotesFunction(List<LootItemCondition> condition) {
        super(condition);
    }

    @Override
    public MapCodec<? extends LootItemConditionalFunction> codec() {
        return CODEC;
    }

    @Override
    protected ItemStack run(ItemStack itemStack, LootContext context) {
        ServerLevel level = context.getLevel();
        RandomSource random = context.getRandom();
        BlockPos pos = position(context);
        Holder<Biome> biome = level.getBiome(pos);

        List<Filterable<Component>> pages = new ArrayList<>();

        Component biomeName = biome.unwrapKey()
            .map(key -> (Component) Component.translatable(Util.makeDescriptionId("biome", key.identifier())))
            .orElse(Component.translatable("book.ancientcreature.field_notes.unknown_region"));
        pages.add(Filterable.passThrough(Component.translatable("book.ancientcreature.field_notes.survey", biomeName)));

        List<Species> recorded = Species.fossilSpecies().stream().filter(s -> s.isValidBiome(biome)).toList();
        MutableComponent specimens = Component.translatable("book.ancientcreature.field_notes.specimens").withStyle(ChatFormatting.BOLD);
        if (recorded.isEmpty()) {
            specimens.append(Component.literal("\n\n").withStyle(ChatFormatting.RESET))
                .append(Component.translatable("book.ancientcreature.field_notes.no_specimens").withStyle(ChatFormatting.RESET));
        } else {
            for (Species species : recorded) {
                specimens.append(Component.literal("\n- ").withStyle(ChatFormatting.RESET)).append(species.displayName().copy().withStyle(ChatFormatting.RESET));
            }
        }
        pages.add(Filterable.passThrough(specimens));

        List<Integer> tips = new ArrayList<>();
        while (tips.size() < TIPS_PER_BOOK) {
            int tip = random.nextInt(TIP_COUNT);
            if (!tips.contains(tip)) {
                tips.add(tip);
            }
        }
        MutableComponent tipPage = Component.translatable("book.ancientcreature.field_notes.tips").withStyle(ChatFormatting.BOLD);
        for (int tip : tips) {
            tipPage.append(Component.literal("\n\n").withStyle(ChatFormatting.RESET))
                .append(Component.translatable("book.ancientcreature.field_notes.tip." + tip).withStyle(ChatFormatting.RESET));
        }
        pages.add(Filterable.passThrough(tipPage));

        itemStack.set(DataComponents.WRITTEN_BOOK_CONTENT, new WrittenBookContent(Filterable.passThrough("Field Notes"), "Expedition Lead", 0, pages, true));
        return itemStack;
    }

    private static BlockPos position(LootContext context) {
        Vec3 origin = context.getOptionalParameter(LootContextParams.ORIGIN);
        if (origin != null) {
            return BlockPos.containing(origin);
        }
        Entity entity = context.getOptionalParameter(LootContextParams.THIS_ENTITY);
        return entity != null ? entity.blockPosition() : BlockPos.ZERO;
    }

    public static LootItemConditionalFunction.Builder<?> fieldNotes() {
        return simpleBuilder(FieldNotesFunction::new);
    }
}
