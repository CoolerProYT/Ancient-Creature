package com.coolerpromc.ancientcreature.client.gui.screen;

import com.coolerpromc.ancientcreature.menu.MachineMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import java.util.Locale;

/** A lab machine screen: the machine's own background plus the upgrade panel on its right. */
public abstract class MachineScreen<M extends MachineMenu> extends AbstractContainerScreen<M> {
    protected static final int STATUS_COLOR = 0xFF404040;
    protected static final int WARNING_COLOR = 0xFFAA2A2A;

    protected MachineScreen(M menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    /** Screen area of the upgrade panel, which sits outside the main window (recipe viewers must not draw over it). */
    public Rect2i getUpgradePanelArea() {
        return new Rect2i(this.leftPos + MachineMenu.PANEL_X, this.topPos + MachineMenu.PANEL_Y, MachineMenu.PANEL_WIDTH, MachineMenu.PANEL_HEIGHT);
    }

    /** A short status line in the free strip between the machine's slots and the inventory label. */
    protected void statusLine(GuiGraphicsExtractor graphics, Component text, int color) {
        int centerX = this.leftPos + 110;
        graphics.text(this.font, text, centerX - this.font.width(text) / 2, this.topPos + 58, color, false);
    }

    /** Ticks as m:ss. */
    protected static Component duration(int ticks) {
        int seconds = Math.max(0, (ticks + 19) / 20);
        return Component.literal(String.format(Locale.ROOT, "%d:%02d", seconds / 60, seconds % 60));
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        UpgradePanel.draw(graphics, this.leftPos, this.topPos);
    }

    @Override
    protected boolean hasClickedOutside(double mouseX, double mouseY, int leftPos, int topPos) {
        return super.hasClickedOutside(mouseX, mouseY, leftPos, topPos) && !UpgradePanel.contains(mouseX, mouseY, leftPos, topPos);
    }
}
