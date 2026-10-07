package com.coolerpromc.ancientcreature.item.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.Locale;
import java.util.function.Consumer;

/**
 * A module that slots into any lab machine's upgrade panel. Two fit per machine, and two of the same
 * kind stack their effect.
 */
public class MachineUpgradeItem extends Item {
    public enum Kind {
        /** Each module cuts processing time by a third (two: to 44%). */
        SPEED,
        /** Each module improves the result: see each machine's effect in the tooltip. */
        PRECISION,
        /** Each module gives a 25% chance to keep a consumable instead of using it. */
        EFFICIENCY;

        public String key() {
            return this.name().toLowerCase(Locale.ROOT);
        }
    }

    private final Kind kind;

    public MachineUpgradeItem(Kind kind, Properties properties) {
        super(properties);
        this.kind = kind;
    }

    public Kind kind() {
        return this.kind;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag flag) {
        builder.accept(Component.translatable("tooltip.ancientcreature.upgrade." + this.kind.key()).withStyle(ChatFormatting.BLUE));
        if (this.kind == Kind.PRECISION) {
            for (String machine : new String[]{"identification", "extraction", "sequencing", "embryogenesis", "incubation"}) {
                builder.accept(Component.translatable("tooltip.ancientcreature.upgrade.precision." + machine).withStyle(ChatFormatting.GRAY));
            }
        }
        builder.accept(Component.translatable("tooltip.ancientcreature.upgrade.slot").withStyle(ChatFormatting.DARK_GRAY));
    }
}
