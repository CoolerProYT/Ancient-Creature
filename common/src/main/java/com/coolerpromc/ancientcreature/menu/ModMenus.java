package com.coolerpromc.ancientcreature.menu;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.menu.custom.FossilCleaningTableMenu;
import com.coolerpromc.ancientcreature.menu.custom.FossilIdentificationChamberMenu;
import com.coolerpromc.ancientcreature.platform.Services;
import com.coolerpromc.ancientcreature.platform.util.MenuFactory;
import com.coolerpromc.ancientcreature.platform.util.RegistryHandler;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

public class ModMenus {
    public static final RegistryHandler<MenuType<?>, MenuType<FossilCleaningTableMenu>> FOSSIL_CLEANING_TABLE = register("fossil_cleaning_table", FossilCleaningTableMenu::new);
    public static final RegistryHandler<MenuType<?>, MenuType<FossilIdentificationChamberMenu>> FOSSIL_IDENTIFICATION_CHAMBER = register("fossil_identification_chamber", FossilIdentificationChamberMenu::new);

    public static <T extends AbstractContainerMenu, D> RegistryHandler<MenuType<?>, MenuType<T>> register(String name, MenuFactory<T> factory){
        return Services.REGISTRY.registerMenuType(name, factory);
    }

    public static void init(){
        Constants.LOG.info("Registering menus.");
    }
}
