package com.coolerpromc.ancientcreature.loot.custom;

import com.coolerpromc.ancientcreature.data.component.ModDataComponents;
import com.coolerpromc.ancientcreature.data.component.custom.FossilData;
import com.coolerpromc.ancientcreature.item.FossilPart;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SetFossilPartFunction extends LootItemConditionalFunction {
    public static final MapCodec<SetFossilPartFunction> CODEC = RecordCodecBuilder.mapCodec(instance -> commonFields(instance).apply(instance, SetFossilPartFunction::new));

    private final List<FossilPart> validParts = new ArrayList<>();

    protected SetFossilPartFunction(List<LootItemCondition> predicates) {
        this(predicates, FossilPart.values());
    }

    protected SetFossilPartFunction(List<LootItemCondition> predicates, FossilPart... validParts) {
        super(predicates);
        this.validParts.addAll(Arrays.stream(validParts).toList());
    }

    @Override
    public MapCodec<? extends LootItemConditionalFunction> codec() {
        return CODEC;
    }

    @Override
    protected ItemStack run(ItemStack itemStack, LootContext context) {
        RandomSource random = context.getParameter(LootContextParams.THIS_ENTITY).level().getRandom();
        itemStack.set(ModDataComponents.FOSSIL_DATA.get(), FossilData.ofDefault(validParts.get(random.nextInt(validParts.size())), null, 0f));
        return itemStack;
    }

    public static LootItemConditionalFunction.Builder<?> setPart() {
        return simpleBuilder(SetFossilPartFunction::new);
    }

    public static LootItemConditionalFunction.Builder<?> setPart(FossilPart... validParts) {
        return simpleBuilder(l -> new SetFossilPartFunction(l, validParts));
    }
}
