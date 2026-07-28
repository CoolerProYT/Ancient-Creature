package com.coolerpromc.ancientcreature.loot.custom;

import com.coolerpromc.ancientcreature.data.component.ModDataComponents;
import com.coolerpromc.ancientcreature.data.component.custom.FossilCompleteness;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

import java.util.List;

public class SetFossilCompletenessFunction extends LootItemConditionalFunction {
    public static final MapCodec<SetFossilCompletenessFunction> CODEC = RecordCodecBuilder.mapCodec(instance ->
        commonFields(instance).and(
            NumberProviders.CODEC.fieldOf("completeness").forGetter(f -> f.completeness)
        ).apply(instance, SetFossilCompletenessFunction::new)
    );

    private final NumberProvider completeness;

    protected SetFossilCompletenessFunction(List<LootItemCondition> conditions, NumberProvider completeness) {
        super(conditions);
        this.completeness = completeness;
    }

    @Override
    public MapCodec<? extends LootItemConditionalFunction> codec() {
        return CODEC;
    }

    @Override
    protected ItemStack run(ItemStack stack, LootContext context) {
        ItemInstance tool = context.getOptionalParameter(LootContextParams.TOOL);
        int fortuneLevel = 0;
        if (tool != null) {
            Holder<Enchantment> fortune = context.getLevel().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.FORTUNE);
            ItemEnchantments enchantments = tool.get(DataComponents.ENCHANTMENTS);
            if (enchantments != null) {
                fortuneLevel = enchantments.getLevel(fortune);
            }
        }

        float base = this.completeness.getFloat(context);
        float bonus = fortuneLevel * 0.1F;
        float value = Math.min(1.0F, base + bonus);
        stack.set(ModDataComponents.FOSSIL_COMPLETENESS.get(), new FossilCompleteness(value));
        return stack;
    }

    public static LootItemConditionalFunction.Builder<?> setCompleteness(NumberProvider completeness) {
        return simpleBuilder(conditions -> new SetFossilCompletenessFunction(conditions, completeness));
    }
}
