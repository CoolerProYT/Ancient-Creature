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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.Comparator;
import java.util.List;

/**
 * Turns a fossil part item into a specific, dirty fossil.
 *
 * <p>The species is chosen from those living in the biome where the loot is generated. Loot can
 * also name its own species pool (e.g. marine species for coastal sites) and grant a completeness
 * bonus for well-preserved sources such as ice or deep rock.
 */
public class SetFossilDataFunction extends LootItemConditionalFunction {
    public static final MapCodec<SetFossilDataFunction> CODEC = RecordCodecBuilder.mapCodec(instance ->
        commonFields(instance)
            .and(Codec.list(FossilPart.CODEC).fieldOf("fossilParts").forGetter(s -> s.validParts))
            .and(Codec.list(Species.CODEC).optionalFieldOf("species", List.of()).forGetter(s -> s.speciesPool))
            .and(Codec.floatRange(-1.0f, 1.0f).optionalFieldOf("completeness_bonus", 0.0f).forGetter(s -> s.completenessBonus))
            .apply(instance, SetFossilDataFunction::new));

    private final List<Holder<FossilPart>> validParts;
    private final List<Species> speciesPool;
    private final float completenessBonus;

    public SetFossilDataFunction(List<LootItemCondition> condition, List<Holder<FossilPart>> fossilParts, List<Species> speciesPool, float completenessBonus) {
        super(condition);
        this.validParts = fossilParts.stream().sorted(Comparator.comparing(h -> h.unwrapKey().orElse(FossilPart.key("empty")).identifier())).toList();
        this.speciesPool = speciesPool;
        this.completenessBonus = completenessBonus;
    }

    @Override
    public MapCodec<? extends LootItemConditionalFunction> codec() {
        return CODEC;
    }

    @Override
    protected ItemStack run(ItemStack itemStack, LootContext context) {
        if (this.validParts.isEmpty()) {
            return ItemStack.EMPTY;
        }

        RandomSource random = context.getRandom();
        Species species = this.pickSpecies(context, random);
        if (species == null) {
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
        Holder<FossilPart> fossilPart = this.validParts.get(random.nextInt(this.validParts.size()));

        float base = fossilPart.value().completeness().getFloat(context);
        float bonus = fortuneLevel * 0.1F + this.completenessBonus;
        float value = Math.clamp(base + bonus, 0.0F, 1.0F) * (1f - damageRate);

        itemStack.set(ModDataComponents.FOSSIL_DATA.get(), FossilData.ofDefault(fossilPart, species, value));
        return itemStack;
    }

    /**
     * Picks the species: the loot's own pool if it names one, otherwise the species of the biome at
     * the loot's position, then of the surface biome above it (caves and the deep rock carry the
     * fossils of the land above), and finally any fossil species at all.
     */
    private @Nullable Species pickSpecies(LootContext context, RandomSource random) {
        List<Species> fossilSpecies = Species.fossilSpecies();
        if (fossilSpecies.isEmpty()) {
            return null;
        }

        if (!this.speciesPool.isEmpty()) {
            List<Species> pool = this.speciesPool.stream().filter(fossilSpecies::contains).toList();
            if (!pool.isEmpty()) {
                return pool.get(random.nextInt(pool.size()));
            }
        }

        BlockPos pos = position(context);
        if (pos != null) {
            ServerLevel level = context.getLevel();
            List<Species> local = fossilSpecies.stream().filter(s -> s.isValidBiome(level.getBiome(pos))).toList();
            if (local.isEmpty()) {
                BlockPos surface = level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, pos);
                local = fossilSpecies.stream().filter(s -> s.isValidBiome(level.getBiome(surface))).toList();
            }
            if (!local.isEmpty()) {
                return local.get(random.nextInt(local.size()));
            }
        }

        return fossilSpecies.get(random.nextInt(fossilSpecies.size()));
    }

    /** Where the loot is generated; chests opened by hoppers or minecarts have no entity, only an origin. */
    private static @Nullable BlockPos position(LootContext context) {
        Vec3 origin = context.getOptionalParameter(LootContextParams.ORIGIN);
        if (origin != null) {
            return BlockPos.containing(origin);
        }
        Entity entity = context.getOptionalParameter(LootContextParams.THIS_ENTITY);
        return entity != null ? entity.blockPosition() : null;
    }

    public static LootItemConditionalFunction.Builder<?> setData(List<Holder<FossilPart>> validParts) {
        return setData(validParts, List.of(), 0.0f);
    }

    public static LootItemConditionalFunction.Builder<?> setData(List<Holder<FossilPart>> validParts, List<Species> speciesPool, float completenessBonus) {
        return simpleBuilder(l -> new SetFossilDataFunction(l, validParts, speciesPool, completenessBonus));
    }
}
