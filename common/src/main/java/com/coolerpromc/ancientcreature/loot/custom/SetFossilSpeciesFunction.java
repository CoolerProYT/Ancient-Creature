package com.coolerpromc.ancientcreature.loot.custom;

import com.coolerpromc.ancientcreature.data.component.ModDataComponents;
import com.coolerpromc.ancientcreature.data.component.custom.FossilData;
import com.coolerpromc.ancientcreature.entity.Species;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.Arrays;
import java.util.List;

public class SetFossilSpeciesFunction extends LootItemConditionalFunction {
    public static final MapCodec<SetFossilSpeciesFunction> CODEC = RecordCodecBuilder.mapCodec(instance -> commonFields(instance).apply(instance, SetFossilSpeciesFunction::new));

    protected SetFossilSpeciesFunction(List<LootItemCondition> predicates) {
        super(predicates);
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
        List<Species> parts = Arrays.stream(Species.values()).filter(s -> s.isValidBiome(biome)).toList();
        if (parts.isEmpty()){
            return ItemStack.EMPTY;
        }
        FossilData fossilData = itemStack.get(ModDataComponents.FOSSIL_DATA.get());
        if (fossilData == null){
            return ItemStack.EMPTY;
        }
        itemStack.set(ModDataComponents.FOSSIL_DATA.get(), fossilData.setSpecies(parts.get(random.nextInt(parts.size()))));
        return itemStack;
    }

    public static Builder<?> setSpecies() {
        return simpleBuilder(SetFossilSpeciesFunction::new);
    }
}
