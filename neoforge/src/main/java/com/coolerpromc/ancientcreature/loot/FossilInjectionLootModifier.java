package com.coolerpromc.ancientcreature.loot;

import com.coolerpromc.ancientcreature.Constants;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.Optional;

/** Hands every generated drop list to {@link FossilLootInjections}, which knows which vanilla tables to touch. */
public class FossilInjectionLootModifier extends LootModifier {
    public static final MapCodec<FossilInjectionLootModifier> CODEC = RecordCodecBuilder.mapCodec(instance ->
        codecStart(instance).apply(instance, FossilInjectionLootModifier::new));

    private static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> SERIALIZERS =
        DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, Constants.MODID);

    static {
        SERIALIZERS.register("fossil_injection", () -> CODEC);
    }

    public FossilInjectionLootModifier(LootItemCondition[] condition, int priority) {
        super(condition, priority);
    }

    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        FossilLootInjections.apply(context.getQueriedLootTableId(), context, generatedLoot);
        return generatedLoot;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}
