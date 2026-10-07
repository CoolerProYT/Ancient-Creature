package com.coolerpromc.ancientcreature.recipe;

import com.coolerpromc.ancientcreature.data.component.ModDataComponents;
import com.coolerpromc.ancientcreature.data.component.custom.FossilData;
import com.coolerpromc.ancientcreature.item.ModItems;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Pieces together two to four identified fossils of one species. The most complete piece is kept
 * and every other piece adds half of its own completeness to it, so a pile of scrappy fragments
 * can still become a usable specimen.
 */
public class FossilReassemblyRecipe extends CustomRecipe {
    public static final FossilReassemblyRecipe INSTANCE = new FossilReassemblyRecipe();
    public static final MapCodec<FossilReassemblyRecipe> MAP_CODEC = MapCodec.unit(INSTANCE);
    public static final StreamCodec<RegistryFriendlyByteBuf, FossilReassemblyRecipe> STREAM_CODEC = StreamCodec.unit(INSTANCE);
    public static final RecipeSerializer<FossilReassemblyRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

    public static final int MIN_PIECES = 2;
    public static final int MAX_PIECES = 4;
    public static final float FRAGMENT_SHARE = 0.5F;

    @Override
    public boolean matches(CraftingInput input, Level level) {
        return pieces(input) != null;
    }

    @Override
    public ItemStack assemble(CraftingInput input) {
        List<ItemStack> pieces = pieces(input);
        if (pieces == null) {
            return ItemStack.EMPTY;
        }

        pieces.sort(Comparator.comparingDouble((ItemStack s) -> data(s).completeness()).reversed());
        ItemStack best = pieces.getFirst();
        FossilData bestData = data(best);
        float completeness = bestData.completeness();
        for (int i = 1; i < pieces.size(); i++) {
            completeness += data(pieces.get(i)).completeness() * FRAGMENT_SHARE;
        }

        ItemStack result = best.copyWithCount(1);
        result.set(ModDataComponents.FOSSIL_DATA.get(), bestData.setCompleteness(Math.min(1.0F, completeness)));
        return result;
    }

    /** The fossils in the grid if they can be reassembled, otherwise null. */
    private static @Nullable List<ItemStack> pieces(CraftingInput input) {
        List<ItemStack> pieces = new ArrayList<>();
        for (int slot = 0; slot < input.size(); slot++) {
            ItemStack stack = input.getItem(slot);
            if (stack.isEmpty()) {
                continue;
            }
            FossilData fossil = stack.get(ModDataComponents.FOSSIL_DATA.get());
            if (!stack.is(ModItems.FOSSIL_PART.get()) || fossil == null || !fossil.identified() || fossil.fossilPart().unwrapKey().isEmpty()) {
                return null;
            }
            if (!pieces.isEmpty() && !data(pieces.getFirst()).species().equals(fossil.species())) {
                return null;
            }
            pieces.add(stack);
        }
        return pieces.size() >= MIN_PIECES && pieces.size() <= MAX_PIECES ? pieces : null;
    }

    private static FossilData data(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.FOSSIL_DATA.get(), FossilData.EMPTY);
    }

    @Override
    public RecipeSerializer<FossilReassemblyRecipe> getSerializer() {
        return SERIALIZER;
    }
}
