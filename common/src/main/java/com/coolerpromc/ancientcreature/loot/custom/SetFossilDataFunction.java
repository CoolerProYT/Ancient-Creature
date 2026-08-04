package com.coolerpromc.ancientcreature.loot.custom;

import com.coolerpromc.ancientcreature.data.component.ModDataComponents;
import com.coolerpromc.ancientcreature.data.component.custom.FossilDamageRate;
import com.coolerpromc.ancientcreature.data.component.custom.FossilData;
import com.coolerpromc.ancientcreature.entity.Species;
import com.coolerpromc.ancientcreature.item.FossilPart;
import com.coolerpromc.ancientcreature.tag.ModItemTags;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.Arrays;
import java.util.List;

public class SetFossilDataFunction extends LootItemConditionalFunction {
    public static final MapCodec<SetFossilDataFunction> CODEC = RecordCodecBuilder.mapCodec(instance ->
        commonFields(instance).and(Codec.list(FossilPart.CODEC).fieldOf("fossilParts").forGetter(s -> s.validParts))
        .apply(instance, SetFossilDataFunction::new));

    private final List<Holder<FossilPart>> validParts;

    public SetFossilDataFunction(List<LootItemCondition> lootItemConditions, List<Holder<FossilPart>> fossilParts) {
        super(lootItemConditions);
        this.validParts = fossilParts;
    }

    @Override
    public MapCodec<? extends LootItemConditionalFunction> codec() {
        return CODEC;
    }

    @Override
    protected ItemStack run(ItemStack itemStack, LootContext context) {
        Entity entity = context.getParameter(LootContextParams.THIS_ENTITY);
        BlockPos pos = entity.getOnPos();
        RandomSource random = entity.level().getRandom();
        Holder<Biome> biome = entity.level().getBiome(pos);
        List<Species> species = Arrays.stream(Species.values()).filter(s -> s.isValidBiome(biome)).toList();
        if (species.isEmpty()){
            return ItemStack.EMPTY;
        }

        ItemInstance tool = context.getOptionalParameter(LootContextParams.TOOL);
        int fortuneLevel = 0;
        float damageRate = 0f;
        if (tool != null && tool.is(ModItemTags.CHISELS)) {
            damageRate = tool.getOrDefault(ModDataComponents.FOSSIL_DAMAGE_RATE.get(), new FossilDamageRate(0f)).value();
            Holder<Enchantment> fortune = context.getLevel().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.FORTUNE);
            ItemEnchantments enchantments = tool.get(DataComponents.ENCHANTMENTS);
            if (enchantments != null) {
                fortuneLevel = enchantments.getLevel(fortune);
            }
        }
        Holder<FossilPart> fossilPart = validParts.get(random.nextInt(validParts.size()));

        float base = fossilPart.value().completeness().getFloat(context);
        float bonus = fortuneLevel * 0.1F;
        float value = Math.min(1.0F, base + bonus) * (1f - damageRate);

        itemStack.set(ModDataComponents.FOSSIL_DATA.get(), FossilData.ofDefault(fossilPart, species.get(random.nextInt(species.size())), value));
        return itemStack;
    }

    public static LootItemConditionalFunction.Builder<?> setData(List<Holder<FossilPart>> validParts) {
        return simpleBuilder(l -> new SetFossilDataFunction(l, validParts));
    }
}
