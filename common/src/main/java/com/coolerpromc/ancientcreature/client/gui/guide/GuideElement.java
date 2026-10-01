package com.coolerpromc.ancientcreature.client.gui.guide;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * One block of content on a field guide page. Elements are laid out top to bottom and are told the
 * width they may use; they report their height so the screen can scroll the page.
 */
public interface GuideElement {
    int INK = 0xFFE8E3D4;
    int MUTED = 0xFF9EA69E;
    int GOLD = 0xFFE0A83B;
    int SLOT_BACKGROUND = 0xFF0E1412;
    int SLOT_BORDER = 0xFF4A3A22;
    int SLOT_SIZE = 18;

    int height(Font font, int width);

    void render(Context context, int x, int y, int width);

    default boolean mouseClicked(Context context, int x, int y, int width, double mouseX, double mouseY) {
        return false;
    }

    /**
     * @param mouseInView whether the mouse is over the visible part of the page, so hover effects and
     *                    tooltips are not triggered by content scrolled out of view.
     */
    record Context(GuiGraphicsExtractor graphics, Font font, Screen screen, int mouseX, int mouseY, boolean mouseInView, int tick) {
        public boolean hovered(int x, int y, int width, int height) {
            return this.mouseInView && this.mouseX >= x && this.mouseX < x + width && this.mouseY >= y && this.mouseY < y + height;
        }
    }

    /** A slot that cycles through its alternatives once a second, like JEI does for tags. */
    record Slot(List<ItemStack> stacks) {
        public static final Slot EMPTY = new Slot(List.of());

        public static Slot of(ItemStack... stacks) {
            return new Slot(List.of(stacks));
        }

        public ItemStack current(int tick) {
            return this.stacks.isEmpty() ? ItemStack.EMPTY : this.stacks.get((tick / 20) % this.stacks.size());
        }

        public void render(Context context, int x, int y) {
            context.graphics().fill(x, y, x + SLOT_SIZE, y + SLOT_SIZE, SLOT_BORDER);
            context.graphics().fill(x + 1, y + 1, x + SLOT_SIZE - 1, y + SLOT_SIZE - 1, SLOT_BACKGROUND);
            ItemStack stack = this.current(context.tick());
            if (stack.isEmpty()) return;
            context.graphics().item(stack, x + 1, y + 1);
            context.graphics().itemDecorations(context.font(), stack, x + 1, y + 1);
            if (context.hovered(x, y, SLOT_SIZE, SLOT_SIZE)) {
                context.graphics().fill(x + 1, y + 1, x + SLOT_SIZE - 1, y + SLOT_SIZE - 1, 0x40FFFFFF);
                context.graphics().setTooltipForNextFrame(context.font(), stack, context.mouseX(), context.mouseY());
            }
        }
    }

    record Heading(Component text) implements GuideElement {
        @Override
        public int height(Font font, int width) {
            return 16;
        }

        @Override
        public void render(Context context, int x, int y, int width) {
            context.graphics().text(context.font(), this.text, x, y + 3, GOLD, false);
            context.graphics().fill(x, y + 13, x + width, y + 14, 0x6676501E);
        }
    }

    record Paragraph(Component text, int color) implements GuideElement {
        public Paragraph(Component text) {
            this(text, INK);
        }

        @Override
        public int height(Font font, int width) {
            return font.split(this.text, width).size() * (font.lineHeight + 1) + 5;
        }

        @Override
        public void render(Context context, int x, int y, int width) {
            int lineY = y;
            for (FormattedCharSequence line : context.font().split(this.text, width)) {
                context.graphics().text(context.font(), line, x, lineY, this.color, false);
                lineY += context.font().lineHeight + 1;
            }
        }
    }

    /** A paragraph with a gold bar down its left edge, for tips and warnings. */
    record Tip(Component text) implements GuideElement {
        @Override
        public int height(Font font, int width) {
            return font.split(this.text, width - 8).size() * (font.lineHeight + 1) + 9;
        }

        @Override
        public void render(Context context, int x, int y, int width) {
            int height = this.height(context.font(), width) - 5;
            context.graphics().fill(x, y, x + width, y + height, 0x40E0A83B);
            context.graphics().fill(x, y, x + 2, y + height, GOLD);
            int lineY = y + 2;
            for (FormattedCharSequence line : context.font().split(this.text, width - 8)) {
                context.graphics().text(context.font(), line, x + 6, lineY, INK, false);
                lineY += context.font().lineHeight + 1;
            }
        }
    }

    /** A row of item slots, wrapping onto more rows when the page is narrow. */
    record Showcase(List<Slot> slots) implements GuideElement {
        private int perRow(int width) {
            return Math.max(1, (width + 2) / (SLOT_SIZE + 2));
        }

        @Override
        public int height(Font font, int width) {
            int rows = (this.slots.size() + this.perRow(width) - 1) / this.perRow(width);
            return rows * (SLOT_SIZE + 2) + 4;
        }

        @Override
        public void render(Context context, int x, int y, int width) {
            int perRow = this.perRow(width);
            for (int i = 0; i < this.slots.size(); i++) {
                this.slots.get(i).render(context, x + (i % perRow) * (SLOT_SIZE + 2), y + (i / perRow) * (SLOT_SIZE + 2));
            }
        }
    }

    /** A 3x3 crafting grid and its result. Slots are listed row by row; missing slots are empty. */
    record Crafting(List<Slot> grid, Slot result, boolean shapeless) implements GuideElement {
        @Override
        public int height(Font font, int width) {
            return 3 * SLOT_SIZE + 8;
        }

        @Override
        public void render(Context context, int x, int y, int width) {
            int gridWidth = 3 * SLOT_SIZE;
            int totalWidth = gridWidth + 28 + SLOT_SIZE;
            int left = x + Math.max(0, (width - totalWidth) / 2);
            for (int i = 0; i < 9; i++) {
                Slot slot = i < this.grid.size() ? this.grid.get(i) : Slot.EMPTY;
                slot.render(context, left + (i % 3) * SLOT_SIZE, y + (i / 3) * SLOT_SIZE);
            }
            int arrowX = left + gridWidth + 6;
            int middle = y + SLOT_SIZE + SLOT_SIZE / 2;
            drawArrow(context, arrowX, middle, 16);
            if (this.shapeless) {
                context.graphics().text(context.font(), "~", arrowX + 5, middle - 13, MUTED, false);
            }
            this.result.render(context, left + gridWidth + 28, middle - SLOT_SIZE / 2);
        }
    }

    /** A machine step: inputs, the machine that runs it, and what comes out. */
    record MachineStep(List<Slot> inputs, Slot machine, List<Slot> outputs) implements GuideElement {
        @Override
        public int height(Font font, int width) {
            return SLOT_SIZE + 8;
        }

        @Override
        public void render(Context context, int x, int y, int width) {
            int arrow = 14;
            int totalWidth = (this.inputs.size() + this.outputs.size()) * SLOT_SIZE + 2 * (arrow + 6) + SLOT_SIZE;
            int cursor = x + Math.max(0, (width - totalWidth) / 2);
            int middle = y + SLOT_SIZE / 2;
            for (Slot input : this.inputs) {
                input.render(context, cursor, y);
                cursor += SLOT_SIZE;
            }
            drawArrow(context, cursor + 3, middle, arrow);
            cursor += arrow + 6;
            // The machine is drawn without a slot frame, so it reads as "done in" rather than "put in".
            ItemStack machine = this.machine.current(context.tick());
            context.graphics().item(machine, cursor + 1, y + 1);
            if (context.hovered(cursor, y, SLOT_SIZE, SLOT_SIZE)) {
                context.graphics().setTooltipForNextFrame(context.font(), machine, context.mouseX(), context.mouseY());
            }
            cursor += SLOT_SIZE;
            drawArrow(context, cursor + 3, middle, arrow);
            cursor += arrow + 6;
            for (Slot output : this.outputs) {
                output.render(context, cursor, y);
                cursor += SLOT_SIZE;
            }
        }
    }

    /** Rows of an icon, a name and a description that wraps beside it. */
    record Table(List<Row> rows) implements GuideElement {
        public record Row(Slot icon, Component name, Component value) {
        }

        private static final int TEXT_OFFSET = SLOT_SIZE + 6;

        private int rowHeight(Font font, Row row, int width) {
            int lines = 1 + font.split(row.value, width - TEXT_OFFSET).size();
            return Math.max(SLOT_SIZE, lines * (font.lineHeight + 1)) + 3;
        }

        @Override
        public int height(Font font, int width) {
            int height = 2;
            for (Row row : this.rows) {
                height += this.rowHeight(font, row, width);
            }
            return height;
        }

        @Override
        public void render(Context context, int x, int y, int width) {
            int rowY = y;
            for (Row row : this.rows) {
                row.icon.render(context, x, rowY);
                context.graphics().text(context.font(), row.name, x + TEXT_OFFSET, rowY, GOLD, false);
                int lineY = rowY + context.font().lineHeight + 1;
                for (FormattedCharSequence line : context.font().split(row.value, width - TEXT_OFFSET)) {
                    context.graphics().text(context.font(), line, x + TEXT_OFFSET, lineY, INK, false);
                    lineY += context.font().lineHeight + 1;
                }
                rowY += this.rowHeight(context.font(), row, width);
            }
        }
    }

    /** Clickable text, used to jump between chapters or open the Species Journal. */
    record Link(Component text, Supplier<Runnable> action) implements GuideElement {
        @Override
        public int height(Font font, int width) {
            return font.lineHeight + 7;
        }

        @Override
        public void render(Context context, int x, int y, int width) {
            Component label = Component.literal("» ").append(this.text);
            boolean hovered = context.hovered(x, y, context.font().width(label), context.font().lineHeight + 2);
            context.graphics().text(context.font(), hovered ? label.copy().withStyle(style -> style.withUnderlined(true)) : label, x, y + 2, hovered ? 0xFFFFD27A : GOLD, false);
        }

        @Override
        public boolean mouseClicked(Context context, int x, int y, int width, double mouseX, double mouseY) {
            Component label = Component.literal("» ").append(this.text);
            if (mouseX >= x && mouseX < x + context.font().width(label) && mouseY >= y && mouseY < y + context.font().lineHeight + 4) {
                this.action.get().run();
                return true;
            }
            return false;
        }
    }

    record Spacer(int size) implements GuideElement {
        @Override
        public int height(Font font, int width) {
            return this.size;
        }

        @Override
        public void render(Context context, int x, int y, int width) {
        }
    }

    private static void drawArrow(Context context, int x, int middle, int length) {
        context.graphics().fill(x, middle - 1, x + length - 3, middle + 1, MUTED);
        for (int i = 0; i < 4; i++) {
            context.graphics().fill(x + length - 4 + i, middle - 3 + i, x + length - 3 + i, middle + 3 - i, MUTED);
        }
    }

    static List<Slot> slots(ItemStack... stacks) {
        List<Slot> slots = new ArrayList<>();
        for (ItemStack stack : stacks) {
            slots.add(stack.isEmpty() ? Slot.EMPTY : Slot.of(stack));
        }
        return slots;
    }
}
