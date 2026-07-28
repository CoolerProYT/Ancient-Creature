package com.coolerpromc.ancientcreature.client.screen;

import com.coolerpromc.ancientcreature.creativetab.ModSections;
import com.coolerpromc.ancientcreature.creativetab.TabLayout;
import com.coolerpromc.ancientcreature.creativetab.section.Section;
import com.coolerpromc.ancientcreature.creativetab.section.SectionColored;
import com.coolerpromc.ancientcreature.creativetab.section.SectionTextured;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.renderer.RenderPipelines;

public class BannerRenderer {
    public static int CURRENT_ROW = 0;

    private static final int ROW_HEIGHT    = 18;
    private static final int GRID_COLS     = 9;
    private static final int GRID_X_OFFSET = 10;
    private static final int GRID_Y_OFFSET = 17;

    public static void render(CreativeModeInventoryScreen screen, GuiGraphicsExtractor graphics) {
        int left = screen.leftPos + GRID_X_OFFSET;
        int top  = screen.topPos  + GRID_Y_OFFSET;
        int w    = GRID_COLS * ROW_HEIGHT - 4;

        Font font = Minecraft.getInstance().font;

        for (Section section : ModSections.ALL) {
            Integer sectionRow = TabLayout.SECTION_ROW.get(section.id());
            if (sectionRow == null) continue;

            int relativeRow = sectionRow - CURRENT_ROW;
            if (relativeRow < 0 || relativeRow >= 5) continue;

            int x = left;
            int y = top + relativeRow * ROW_HEIGHT + 1;
            int h = ROW_HEIGHT - 2;

            if (section instanceof SectionColored colored) {
                graphics.fill(x - 1, y, x + w + 1, y + h, colored.bannerColor());
                graphics.fill(x - 1, y - 1, x + w + 1, y, 0xFF373737);
                graphics.fill(x - 1, y + h, x + w + 1, y + h + 1, 0xFFFFFFFF);
            } else if (section instanceof SectionTextured textured) {
                graphics.blit(RenderPipelines.GUI_TEXTURED, textured.texture(), x - 1, y, 0, 0, w + 2, h, w + 2, h);
                graphics.fill(x - 1, y - 1, x + w + 1, y, 0xFF373737);
                graphics.fill(x - 1, y + h, x + w + 1, y + h + 1, 0xFFFFFFFF);
            }

            int textX = x + 4;
            int textY = y + (h - font.lineHeight) / 2 + 1;
            graphics.text(font, section.title(), textX, textY, section.textColor(), true);
        }
    }
}