package com.coolerpromc.ancientcreature.compat.jei;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.data.component.ModDataComponents;
import com.coolerpromc.ancientcreature.item.ModItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.resources.Identifier;

@JeiPlugin
public class ModJEIPlugin implements IModPlugin {
    @Override
    public Identifier getPluginUid() {
        return Constants.id("jei_plugin");
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        registration.registerFromDataComponentTypes(ModItems.FOSSIL_FRAGMENT.get(), ModDataComponents.SPECIES.get(), ModDataComponents.FOSSIL_PART.get(), ModDataComponents.IDENTIFIED.get());
        registration.registerFromDataComponentTypes(ModItems.EGG_FOSSIL.get(), ModDataComponents.SPECIES.get(), ModDataComponents.IDENTIFIED.get());
        registration.registerFromDataComponentTypes(ModItems.DNA_SAMPLE.get(), ModDataComponents.SPECIES.get(), ModDataComponents.DNA_INTEGRITY_LEVEL.get());
        registration.registerFromDataComponentTypes(ModItems.GENOME_CARTRIDGE_FILLED.get(), ModDataComponents.SPECIES.get());
        registration.registerFromDataComponentTypes(ModItems.GENOME_CARTRIDGE_COMPLETED.get(), ModDataComponents.SPECIES.get());
        registration.registerFromDataComponentTypes(ModItems.FERTILIZED_ANCIENT_EGG.get(), ModDataComponents.SPECIES.get());
    }
}
