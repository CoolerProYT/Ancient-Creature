package com.coolerpromc.ancientcreature.loot.custom;

import com.coolerpromc.ancientcreature.data.component.ModDataComponents;
import com.coolerpromc.ancientcreature.data.component.custom.FossilPart;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;

public class SetFossilPartFunction extends LootItemConditionalFunction {
    public static final MapCodec<SetFossilPartFunction> CODEC = RecordCodecBuilder.mapCodec(instance -> commonFields(instance).apply(instance, SetFossilPartFunction::new));

    protected SetFossilPartFunction(List<LootItemCondition> predicates) {
        super(predicates);
    }

    @Override
    public MapCodec<? extends LootItemConditionalFunction> codec() {
        return CODEC;
    }

    @Override
    protected ItemStack run(ItemStack itemStack, LootContext context) {
        RandomSource random = context.getParameter(LootContextParams.THIS_ENTITY).level().getRandom();
        FossilPart[] parts = FossilPart.values();
        itemStack.set(ModDataComponents.FOSSIL_PART.get(), parts[random.nextIntBetweenInclusive(0, parts.length - 1)]);
        return itemStack;
    }

    public static LootItemConditionalFunction.Builder<?> setPart() {
        return simpleBuilder(SetFossilPartFunction::new);
    }
}
