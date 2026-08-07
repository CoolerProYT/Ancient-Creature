package com.coolerpromc.ancientcreature.entity;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.entity.custom.AncientCreatureEntity;
import com.coolerpromc.ancientcreature.platform.Services;
import com.coolerpromc.ancientcreature.platform.util.RegistryHandler;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import java.util.List;
import java.util.function.UnaryOperator;

public class ModEntities {
    /**
     * The one generic creature type. Every species — shipped or datapack-added — spawns as this; its
     * size, attributes, AI, sounds and appearance come from its species definition, not from its type.
     *
     * <p>Size here is only the pre-species fallback: {@code AncientCreatureEntity} overrides
     * {@code getDefaultDimensions} with the species' physical properties. Tracking range is generous
     * because a datapack species may be far larger than anything shipped, and it cannot be data-driven
     * ({@code EntityType} properties are frozen at registration).
     */
    public static final RegistryHandler.Entities<AncientCreatureEntity> ANCIENT_CREATURE =
        register("ancient_creature", AncientCreatureEntity::new, MobCategory.CREATURE,
            b -> b.sized(1.2f, 1.8f).eyeHeight(1.4f).clientTrackingRange(12).updateInterval(3));

    /**
     * Every registered creature type, for attribute and renderer registration.
     *
     * <p>The retired {@code ancientcreature:triceratops}, {@code :tyrannosaurus_rex} and
     * {@code :megalodon} ids are no longer registered. Minecraft silently drops entities whose type is
     * unknown, so creatures saved under those ids in a pre-26.1.2.4 world are lost on load — everything
     * else about them (fossils, DNA, eggs, capsules, identified species) still reads correctly.
     */
    public static List<RegistryHandler.Entities<AncientCreatureEntity>> allCreatureTypes() {
        return List.of(ANCIENT_CREATURE);
    }

    private static <T extends Entity> RegistryHandler.Entities<T> register(String name, EntityType.EntityFactory<T> factory, MobCategory category, UnaryOperator<EntityType.Builder<T>> builder){
        return Services.REGISTRY.registerEntity(name, factory, category, builder);
    }

    public static void init(){
        Constants.LOG.info("Registering entities.");
    }
}
