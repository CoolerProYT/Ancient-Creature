package com.coolerpromc.ancientcreature.client.gui.screen;

import com.coolerpromc.ancientcreature.client.gui.guide.GuideBook;
import com.coolerpromc.ancientcreature.client.gui.guide.GuideElement;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;

import java.util.List;

public final class FieldGuideScreen extends Screen {
    private static final int MAX_WIDTH = 400;
    private static final int MAX_HEIGHT = 240;
    private static final int SIDEBAR_WIDTH = 118;
    private static final int HEADER_HEIGHT = 24;
    private static final int PADDING = 8;
    private static final int CHAPTER_ROW_HEIGHT = 18;
    private static final int SCROLL_STEP = 24;

    private static final int FRAME = 0xFF3B2414;
    private static final int FRAME_EDGE = 0xFF684A25;
    private static final int BACKGROUND = 0xF0141B19;
    private static final int SIDEBAR = 0xF01B2422;
    private static final int GOLD = GuideElement.GOLD;
    private static final int GOLD_DARK = 0xAA76501E;
    private static final int HOVER = 0x66303836;
    private static final int INK = GuideElement.INK;
    private static final int MUTED = GuideElement.MUTED;

    // Remembered for the rest of the session, so reopening the guide returns to where you left off.
    private static String lastChapter;
    private static int lastScroll;

    private List<GuideBook.Chapter> chapters = List.of();
    private int selected;
    private int scroll;
    private int chapterScroll;
    private int contentHeight;
    private int tick;

    private int panelX;
    private int panelY;
    private int panelWidth;
    private int panelHeight;

    public FieldGuideScreen() {
        super(Component.translatable("screen.ancientcreature.field_guide"));
    }

    @Override
    protected void init() {
        this.panelWidth = Math.min(MAX_WIDTH, this.width - 16);
        this.panelHeight = Math.min(MAX_HEIGHT, this.height - 16);
        this.panelX = (this.width - this.panelWidth) / 2;
        this.panelY = (this.height - this.panelHeight) / 2;

        // init() also runs on resize, where the page being read should stay put.
        String chapterId = this.chapters.isEmpty() ? lastChapter : this.chapter().id();
        int previousScroll = this.chapters.isEmpty() ? lastScroll : this.scroll;
        this.chapters = GuideBook.build(this::openChapter);
        this.selected = 0;
        for (int i = 0; i < this.chapters.size(); i++) {
            if (this.chapters.get(i).id().equals(chapterId)) {
                this.selected = i;
            }
        }
        this.layout();
        this.scroll = Mth.clamp(previousScroll, 0, this.maxScroll());
        this.chapterScroll = Mth.clamp(this.selected - this.visibleChapterRows() + 1, 0, this.maxChapterScroll());
    }

    private void openChapter(String id) {
        for (int i = 0; i < this.chapters.size(); i++) {
            if (this.chapters.get(i).id().equals(id)) {
                this.select(i);
                return;
            }
        }
    }

    private void select(int index) {
        if (index == this.selected) return;
        this.selected = index;
        this.scroll = 0;
        this.layout();
        if (this.selected < this.chapterScroll) {
            this.chapterScroll = this.selected;
        } else if (this.selected >= this.chapterScroll + this.visibleChapterRows()) {
            this.chapterScroll = this.selected - this.visibleChapterRows() + 1;
        }
        this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.0F));
    }

    private void layout() {
        this.contentHeight = 0;
        for (GuideElement element : this.chapter().elements()) {
            this.contentHeight += element.height(this.font, this.contentWidth());
        }
    }

    private GuideBook.Chapter chapter() {
        return this.chapters.get(this.selected);
    }

    private int contentX() {
        return this.panelX + SIDEBAR_WIDTH + PADDING + 4;
    }

    private int contentY() {
        return this.panelY + HEADER_HEIGHT + 4;
    }

    private int contentWidth() {
        // Leave room on the right for the scrollbar.
        return this.panelX + this.panelWidth - PADDING - 6 - this.contentX();
    }

    private int contentViewHeight() {
        return this.panelY + this.panelHeight - PADDING - this.contentY();
    }

    private int maxScroll() {
        return Math.max(0, this.contentHeight - this.contentViewHeight());
    }

    private int chapterListY() {
        return this.panelY + HEADER_HEIGHT + 4;
    }

    private int visibleChapterRows() {
        return Math.max(1, (this.panelY + this.panelHeight - PADDING - this.chapterListY()) / CHAPTER_ROW_HEIGHT);
    }

    private int maxChapterScroll() {
        return Math.max(0, this.chapters.size() - this.visibleChapterRows());
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        this.extractTransparentBackground(graphics);
        int x0 = this.panelX;
        int y0 = this.panelY;
        int x1 = this.panelX + this.panelWidth;
        int y1 = this.panelY + this.panelHeight;
        graphics.fill(x0 - 3, y0 - 3, x1 + 3, y1 + 3, FRAME);
        graphics.outline(x0 - 3, y0 - 3, this.panelWidth + 6, this.panelHeight + 6, 0xFF1E120A);
        graphics.fill(x0, y0, x1, y1, BACKGROUND);
        graphics.outline(x0, y0, this.panelWidth, this.panelHeight, FRAME_EDGE);
        graphics.fill(x0 + 1, y0 + 1, x0 + SIDEBAR_WIDTH, y1 - 1, SIDEBAR);
        graphics.fill(x0 + SIDEBAR_WIDTH, y0 + 1, x0 + SIDEBAR_WIDTH + 1, y1 - 1, FRAME_EDGE);
        graphics.fill(x0 + 1, y0 + HEADER_HEIGHT, x1 - 1, y0 + HEADER_HEIGHT + 1, FRAME_EDGE);
        // Brass corner studs, echoing the Species Journal's frame.
        for (int[] corner : new int[][]{{x0 - 3, y0 - 3}, {x1 - 2, y0 - 3}, {x0 - 3, y1 - 2}, {x1 - 2, y1 - 2}}) {
            graphics.fill(corner[0], corner[1], corner[0] + 5, corner[1] + 5, GOLD);
            graphics.fill(corner[0] + 1, corner[1] + 1, corner[0] + 4, corner[1] + 4, 0xFFA8741F);
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);

        int headerTextY = this.panelY + (HEADER_HEIGHT - this.font.lineHeight) / 2 + 1;
        graphics.text(this.font, this.title, this.panelX + PADDING, headerTextY, GOLD, false);
        GuideBook.Chapter chapter = this.chapter();
        graphics.text(this.font, chapter.title(), this.contentX(), headerTextY, INK, false);
        String page = (this.selected + 1) + " / " + this.chapters.size();
        graphics.text(this.font, page, this.panelX + this.panelWidth - PADDING - this.font.width(page), headerTextY, MUTED, false);

        this.extractChapterList(graphics, mouseX, mouseY);
        this.extractContent(graphics, mouseX, mouseY);
    }

    private void extractChapterList(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        int listX = this.panelX + 3;
        int listRight = this.panelX + SIDEBAR_WIDTH - 2;
        int listY = this.chapterListY();
        int rows = this.visibleChapterRows();
        for (int row = 0; row < rows; row++) {
            int index = this.chapterScroll + row;
            if (index >= this.chapters.size()) break;
            GuideBook.Chapter chapter = this.chapters.get(index);
            int y = listY + row * CHAPTER_ROW_HEIGHT;
            boolean hovered = mouseX >= listX && mouseX < listRight && mouseY >= y && mouseY < y + CHAPTER_ROW_HEIGHT;
            if (index == this.selected) {
                graphics.fill(listX, y, listRight, y + CHAPTER_ROW_HEIGHT - 1, GOLD_DARK);
                graphics.fill(listX, y, listX + 1, y + CHAPTER_ROW_HEIGHT - 1, GOLD);
            } else if (hovered) {
                graphics.fill(listX, y, listRight, y + CHAPTER_ROW_HEIGHT - 1, HOVER);
            }
            graphics.item(chapter.icon(), listX + 2, y + 1);
            int textX = listX + 21;
            String title = chapter.title().getString();
            int maxWidth = listRight - textX - 2;
            if (this.font.width(title) > maxWidth) {
                title = this.font.plainSubstrByWidth(title, maxWidth - this.font.width("...")) + "...";
                if (hovered) {
                    graphics.setTooltipForNextFrame(this.font, chapter.title(), mouseX, mouseY);
                }
            }
            graphics.text(this.font, title, textX, y + (CHAPTER_ROW_HEIGHT - this.font.lineHeight) / 2 + 1, index == this.selected ? INK : MUTED, false);
        }
        if (this.maxChapterScroll() > 0) {
            this.extractScrollbar(graphics, this.panelX + SIDEBAR_WIDTH - 3, listY, rows * CHAPTER_ROW_HEIGHT,
                this.chapterScroll, this.maxChapterScroll(), rows, this.chapters.size());
        }
    }

    private void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        int x = this.contentX();
        int top = this.contentY();
        int width = this.contentWidth();
        int viewHeight = this.contentViewHeight();
        boolean mouseInView = mouseX >= x - 2 && mouseX < x + width + 2 && mouseY >= top && mouseY < top + viewHeight;
        GuideElement.Context context = new GuideElement.Context(graphics, this.font, this, mouseX, mouseY, mouseInView, this.tick);

        graphics.enableScissor(x - 2, top, x + width + 2, top + viewHeight);
        int y = top - this.scroll;
        for (GuideElement element : this.chapter().elements()) {
            int height = element.height(this.font, width);
            if (y + height > top && y < top + viewHeight) {
                element.render(context, x, y, width);
            }
            y += height;
        }
        graphics.disableScissor();

        if (this.maxScroll() > 0) {
            this.extractScrollbar(graphics, x + width + 3, top, viewHeight, this.scroll, this.maxScroll(), viewHeight, this.contentHeight);
        }
    }

    private void extractScrollbar(GuiGraphicsExtractor graphics, int x, int y, int trackHeight, int offset, int maxOffset, int visible, int total) {
        graphics.fill(x, y, x + 2, y + trackHeight, 0x40000000);
        int thumbHeight = Math.max(10, trackHeight * visible / Math.max(1, total));
        int thumbY = y + (trackHeight - thumbHeight) * offset / Math.max(1, maxOffset);
        graphics.fill(x, thumbY, x + 2, thumbY + thumbHeight, GOLD);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (event.button() == InputConstants.MOUSE_BUTTON_LEFT) {
            double mouseX = event.x();
            double mouseY = event.y();
            int listY = this.chapterListY();
            if (mouseX >= this.panelX + 3 && mouseX < this.panelX + SIDEBAR_WIDTH - 2
                && mouseY >= listY && mouseY < listY + this.visibleChapterRows() * CHAPTER_ROW_HEIGHT) {
                int index = this.chapterScroll + (int) ((mouseY - listY) / CHAPTER_ROW_HEIGHT);
                if (index >= 0 && index < this.chapters.size()) {
                    this.select(index);
                    return true;
                }
            }

            int x = this.contentX();
            int top = this.contentY();
            int width = this.contentWidth();
            if (mouseX >= x && mouseX < x + width && mouseY >= top && mouseY < top + this.contentViewHeight()) {
                GuideElement.Context context = new GuideElement.Context(null, this.font, this, (int) mouseX, (int) mouseY, true, this.tick);
                int y = top - this.scroll;
                // Copy the list: a link may switch chapters while we are iterating.
                for (GuideElement element : List.copyOf(this.chapter().elements())) {
                    int height = element.height(this.font, width);
                    if (mouseY >= y && mouseY < y + height && element.mouseClicked(context, x, y, width, mouseX, mouseY)) {
                        return true;
                    }
                    y += height;
                }
            }
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (mouseX >= this.panelX && mouseX < this.panelX + SIDEBAR_WIDTH) {
            this.chapterScroll = Mth.clamp(this.chapterScroll - (int) Math.signum(verticalAmount), 0, this.maxChapterScroll());
            return true;
        }
        if (mouseX >= this.panelX + SIDEBAR_WIDTH && mouseX < this.panelX + this.panelWidth) {
            this.scroll = Mth.clamp(this.scroll - (int) Math.round(verticalAmount * SCROLL_STEP), 0, this.maxScroll());
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        switch (event.key()) {
            case InputConstants.KEY_UP -> this.scroll = Mth.clamp(this.scroll - SCROLL_STEP, 0, this.maxScroll());
            case InputConstants.KEY_DOWN -> this.scroll = Mth.clamp(this.scroll + SCROLL_STEP, 0, this.maxScroll());
            case InputConstants.KEY_PAGEUP, InputConstants.KEY_LEFT -> this.select(Math.max(0, this.selected - 1));
            case InputConstants.KEY_PAGEDOWN, InputConstants.KEY_RIGHT -> this.select(Math.min(this.chapters.size() - 1, this.selected + 1));
            default -> {
                return super.keyPressed(event);
            }
        }
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        this.tick++;
    }

    @Override
    public void removed() {
        super.removed();
        if (!this.chapters.isEmpty()) {
            lastChapter = this.chapter().id();
            lastScroll = this.scroll;
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
