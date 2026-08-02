package com.coolerpromc.ancientcreature.entity;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.entity.custom.Triceratops;
import com.coolerpromc.ancientcreature.entity.custom.TyrannosaurusRex;
import com.coolerpromc.ancientcreature.entity.custom.Megalodon;
import com.coolerpromc.ancientcreature.platform.Services;
import com.coolerpromc.ancientcreature.platform.util.RegistryHandler;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import java.util.function.UnaryOperator;

public class ModEntities {
    public static final RegistryHandler.Entities<Triceratops> TRICERATOPS = register("triceratops", Triceratops::new, MobCategory.CREATURE, b -> b.sized(1.2f, 1.8f).eyeHeight(1.4f).clientTrackingRange(10).updateInterval(3));
    public static final RegistryHandler.Entities<TyrannosaurusRex> TYRANNOSAURUS_REX = register("tyrannosaurus_rex", TyrannosaurusRex::new, MobCategory.CREATURE, b -> b.sized(1.5F, 2.7F).eyeHeight(2.25F).clientTrackingRange(12).updateInterval(3));
    public static final RegistryHandler.Entities<Megalodon> MEGALODON = register("megalodon", Megalodon::new, MobCategory.WATER_CREATURE, b -> b.sized(2.8F, 1.6F).eyeHeight(1.0F).clientTrackingRange(12).updateInterval(3));

    private static <T extends Entity> RegistryHandler.Entities<T> register(String name, EntityType.EntityFactory<T> factory, MobCategory category, UnaryOperator<EntityType.Builder<T>> builder){
        return Services.REGISTRY.registerEntity(name, factory, category, builder);
    }

    public static void init(){
        Constants.LOG.info("Registering entities.");
    }
}
