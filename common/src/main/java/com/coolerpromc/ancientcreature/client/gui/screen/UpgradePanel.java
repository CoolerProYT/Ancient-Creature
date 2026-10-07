package com.coolerpromc.ancientcreature.client.gui.screen;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.menu.MachineMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

/** The upgrade panel every lab machine screen shows to its right. */
public final class UpgradePanel {
    public static final Identifier TEXTURE = Constants.id("textures/gui/upgrade_panel.png");

    private UpgradePanel() {
    }

    public static void draw(GuiGraphicsExtractor graphics, int leftPos, int topPos) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, leftPos + MachineMenu.PANEL_X, topPos + MachineMenu.PANEL_Y, 0, 0,
            MachineMenu.PANEL_WIDTH, MachineMenu.PANEL_HEIGHT, 32, 64);
    }

    public static boolean contains(double mouseX, double mouseY, int leftPos, int topPos) {
        double x = mouseX - leftPos - MachineMenu.PANEL_X;
        double y = mouseY - topPos - MachineMenu.PANEL_Y;
        return x >= 0 && y >= 0 && x < MachineMenu.PANEL_WIDTH && y < MachineMenu.PANEL_HEIGHT;
    }

    public static Component title() {
        return Component.translatable("gui.ancientcreature.upgrades");
    }
}
