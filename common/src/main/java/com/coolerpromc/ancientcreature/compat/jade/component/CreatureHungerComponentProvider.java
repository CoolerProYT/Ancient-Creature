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

public class CreatureHungerComponentProvider implements IEntityComponentProvider {
    public static final CreatureHungerComponentProvider INSTANCE = new CreatureHungerComponentProvider();

    @Override
    public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig config) {
        if (!(accessor.getEntity() instanceof AncientCreatureEntity creature)) {
            return;
        }

        if (!creature.hasResolvedSpecies()) {
            return;
        }

        SpeciesHungerProperties hunger = creature.hungerProperties();
        float current = creature.getHunger();

        tooltip.add(Component.translatable("jade.ancientcreature.hunger",
            format(current), format(hunger.max())));

        if (hunger.isHungryAt(current)) {
            tooltip.add(Component.translatable("jade.ancientcreature.hungry")
                .withStyle(ChatFormatting.RED));
        } else if (hunger.isSatedAt(current)) {
            tooltip.add(Component.translatable("jade.ancientcreature.fed")
                .withStyle(ChatFormatting.GREEN));
        }

        if (AncientCreatureEntity.comfortEnabled()) {
            float comfort = creature.getComfort();
            ChatFormatting colour = comfort >= AncientCreatureEntity.CONTENT_COMFORT ? ChatFormatting.GREEN
                : comfort >= AncientCreatureEntity.BREEDING_COMFORT ? ChatFormatting.YELLOW
                : comfort >= AncientCreatureEntity.DISTRESS_COMFORT ? ChatFormatting.GOLD : ChatFormatting.RED;
            String mood = comfort >= AncientCreatureEntity.CONTENT_COMFORT ? "content"
                : comfort >= AncientCreatureEntity.BREEDING_COMFORT ? "settled"
                : comfort >= AncientCreatureEntity.DISTRESS_COMFORT ? "uneasy" : "distressed";
            tooltip.add(Component.translatable("jade.ancientcreature.comfort", Math.round(comfort),
                Component.translatable("comfort.ancientcreature." + mood)).withStyle(colour));
            if (creature.getComfortReason() != com.coolerpromc.ancientcreature.entity.comfort.ComfortTracker.Factor.NONE) {
                tooltip.add(Component.translatable(creature.getComfortReason().key()).withStyle(ChatFormatting.GRAY));
            }
        }
        tooltip.add(Component.translatable("jade.ancientcreature.genome", creature.getGenomeFidelityPercent(),
            Component.translatable("temperament.ancientcreature." + creature.getTemperament().getSerializedName())).withStyle(ChatFormatting.GRAY));
        if (!creature.isFertile()) {
            tooltip.add(Component.translatable("jade.ancientcreature.sterile").withStyle(ChatFormatting.RED));
        }
        if (creature.isFrail()) {
            tooltip.add(Component.translatable("jade.ancientcreature.frail").withStyle(ChatFormatting.RED));
        }
        if (creature.hasOwnerClientSide()) {
            tooltip.add(Component.translatable("jade.ancientcreature.command", creature.isSitting()
                ? Component.translatable("command.ancientcreature.stay") : Component.translatable("jade.ancientcreature.owned")).withStyle(ChatFormatting.GRAY));
        }
    }

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
