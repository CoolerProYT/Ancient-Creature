package com.coolerpromc.ancientcreature.recipe;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.platform.Services;
import com.coolerpromc.ancientcreature.platform.util.RegistryHandler;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class ModRecipes {
    public static final RegistryHandler<RecipeSerializer<?>, RecipeSerializer<FossilReassemblyRecipe>> FOSSIL_REASSEMBLY = Services.REGISTRY.registerRecipeSerializer("fossil_reassembly", FossilReassemblyRecipe.SERIALIZER);

    public static void init(){
        Constants.LOG.info("Registering recipes.");
    }
}
