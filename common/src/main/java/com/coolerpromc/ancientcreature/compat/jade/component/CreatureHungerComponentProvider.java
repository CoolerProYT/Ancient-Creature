package com.coolerpromc.ancientcreature.compat.jade.component;

import com.coolerpromc.ancientcreature.compat.jade.ModJadePlugin;
import com.coolerpromc.ancientcreature.entity.custom.AncientCreatureEntity;
import com.coolerpromc.ancientcreature.species.SpeciesHungerProperties;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

/**
 * Shows how fed a creature is, and whether that is enough to make it hunt.
 *
 * <p>Hunger decides whether a predator will attack, and it is otherwise completely invisible — there is no
 * way for a player to tell a Tyrannosaurus Rex that is ignoring them from one that is about to charge. This
 * is the readout that makes the mechanic legible.
 *
 * <p>No server data provider is needed. Unlike the egg block's countdown, hunger is synchronised entity
 * data, so the client already has the current value and the species definition holding the thresholds.
 */
public class CreatureHungerComponentProvider implements IEntityComponentProvider {
    public static final CreatureHungerComponentProvider INSTANCE = new CreatureHungerComponentProvider();

    @Override
    public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig config) {
        if (!(accessor.getEntity() instanceof AncientCreatureEntity creature)) {
            return;
        }
        // An unresolved species falls back to defaults, which would report a meter the creature is not
        // actually running on. Say nothing until its datapack is loaded.
        if (!creature.hasResolvedSpecies()) {
            return;
        }

        SpeciesHungerProperties hunger = creature.hungerProperties();
        float current = creature.getHunger();

        tooltip.add(Component.translatable("jade.ancientcreature.hunger",
            format(current), format(hunger.max())));

        // Derived from the value and thresholds rather than the creature's current target, because the
        // target is server-only state that never reaches the client.
        if (hunger.isHungryAt(current)) {
            tooltip.add(Component.translatable("jade.ancientcreature.hungry")
                .withStyle(ChatFormatting.RED));
        } else if (hunger.isSatedAt(current)) {
            tooltip.add(Component.translatable("jade.ancientcreature.fed")
                .withStyle(ChatFormatting.GREEN));
        }
    }

    /** Trims the trailing {@code .0} that hunger values almost always have, since decay is by whole points. */
    private static String format(float value) {
        return value == Math.rint(value)
            ? String.valueOf((int) value)
            : String.format(java.util.Locale.ROOT, "%.1f", value);
    }

    @Override
    public Identifier getUid() {
        return ModJadePlugin.CREATURE_HUNGER;
    }
}
