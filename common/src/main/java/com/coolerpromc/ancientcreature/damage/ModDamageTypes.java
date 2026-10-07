package com.coolerpromc.ancientcreature.damage;

import com.coolerpromc.ancientcreature.Constants;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.level.Level;

/** Damage types this mod defines in its datapack ({@code data/ancientcreature/damage_type/}). */
public final class ModDamageTypes {
    public static final ResourceKey<DamageType> ELECTRIC_FENCE = ResourceKey.create(Registries.DAMAGE_TYPE, Constants.id("electric_fence"));

    public static DamageSource source(Level level, ResourceKey<DamageType> key) {
        return new DamageSource(level.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(key));
    }

    public static void bootstrap(BootstrapContext<DamageType> context) {
        context.register(ELECTRIC_FENCE, new DamageType("ancientcreature.electric_fence", DamageScaling.NEVER, 0.1F));
    }

    private ModDamageTypes() {
    }
}
