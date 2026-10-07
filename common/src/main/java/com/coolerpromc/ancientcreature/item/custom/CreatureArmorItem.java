package com.coolerpromc.ancientcreature.item.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

/**
 * Barding for a revived creature. Fits any species; the owner puts it on by using it on the creature.
 * The tier decides the protection and which overlay the creature's render controller shows.
 */
public class CreatureArmorItem extends Item {
    public enum Tier {
        IRON(1, 6.0, 0.0, 0.0),
        GOLDEN(2, 8.0, 0.0, 0.0),
        DIAMOND(3, 12.0, 2.0, 0.0),
        NETHERITE(4, 15.0, 3.0, 0.2);

        /** Synced to clients and exposed to animations as {@code query.armor_tier}; 0 means none. */
        public final int index;
        public final double armor;
        public final double toughness;
        public final double knockbackResistance;

        Tier(int index, double armor, double toughness, double knockbackResistance) {
            this.index = index;
            this.armor = armor;
            this.toughness = toughness;
            this.knockbackResistance = knockbackResistance;
        }
    }

    private final Tier tier;

    public CreatureArmorItem(Tier tier, Properties properties) {
        super(properties);
        this.tier = tier;
    }

    public Tier tier() {
        return this.tier;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag flag) {
        builder.accept(Component.translatable("tooltip.ancientcreature.creature_armor.use").withStyle(ChatFormatting.GRAY));
        builder.accept(Component.translatable("tooltip.ancientcreature.creature_armor.armor", (int) this.tier.armor).withStyle(ChatFormatting.BLUE));
        if (this.tier.toughness > 0) {
            builder.accept(Component.translatable("tooltip.ancientcreature.creature_armor.toughness", (int) this.tier.toughness).withStyle(ChatFormatting.BLUE));
        }
        if (this.tier.knockbackResistance > 0) {
            builder.accept(Component.translatable("tooltip.ancientcreature.creature_armor.knockback", Math.round(this.tier.knockbackResistance * 100)).withStyle(ChatFormatting.BLUE));
        }
    }
}
