package com.coolerpromc.ancientcreature.client.gui.screen;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.menu.custom.FossilIdentificationChamberMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class FossilIdentificationChamberScreen extends AbstractContainerScreen<FossilIdentificationChamberMenu> {
    public static final Identifier TEXTURE = Constants.id("textures/gui/screen/fossil_identification_chamber.png");
    public static final Identifier ARROW = Constants.id("container/progress_arrow");

    public FossilIdentificationChamberScreen(FossilIdentificationChamberMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractRenderState(graphics, mouseX, mouseY, a);
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, ARROW, 24, 16, 0, 0, this.leftPos + 76, this.topPos + 34, this.menu.getProgressWidth(), 16);
    }
}
