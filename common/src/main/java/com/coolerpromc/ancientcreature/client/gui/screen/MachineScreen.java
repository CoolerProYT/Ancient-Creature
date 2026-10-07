package com.coolerpromc.ancientcreature.client.gui.screen;

import com.coolerpromc.ancientcreature.menu.MachineMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

/** A lab machine screen: the machine's own background plus the upgrade panel on its right. */
public abstract class MachineScreen<M extends MachineMenu> extends AbstractContainerScreen<M> {
    protected MachineScreen(M menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
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
