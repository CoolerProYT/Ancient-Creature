package com.coolerpromc.ancientcreature.client.gui.screen;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.client.entity.state.IdentifiedSpeciesRenderState;
import com.coolerpromc.ancientcreature.entity.Species;
import com.coolerpromc.ancientcreature.menu.custom.FossilIdentificationChamberMenu;
import com.coolerpromc.ancientcreature.network.ClientboundIdentifiedSpeciesSyncPacket;
import com.coolerpromc.ancientcreature.saveddata.IdentifiedSpeciesData;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;

public class FossilIdentificationChamberScreen extends AbstractContainerScreen<FossilIdentificationChamberMenu> {
    public static final Identifier TEXTURE = Constants.id("textures/gui/screen/fossil_identification_chamber.png");
    public static final Identifier ARROW = Constants.id("container/progress_arrow");
    private static final Identifier SLOT_HIGHLIGHT_BACK_SPRITE = Identifier.withDefaultNamespace("container/slot_highlight_back");
    private static final Identifier SLOT_HIGHLIGHT_FRONT_SPRITE = Identifier.withDefaultNamespace("container/slot_highlight_front");

    private final LinkedHashMap<Species, EntityIdentified> speciesStatuses = new LinkedHashMap<>();
    private int currentPage = 0;

    private Button previousPageButton;
    private Button nextPageButton;

    public FossilIdentificationChamberScreen(FossilIdentificationChamberMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, 254, 166);
    }

    @Override
    protected void init() {
        super.init();
        inventoryLabelX = 86;
        titleLabelX = 86;

        if (minecraft.level == null) return;
        for (Species species : Arrays.stream(Species.values()).sorted(Comparator.comparing(Species::name)).toList()) {
            LivingEntity entity = (LivingEntity) species.getEntityType().create(this.minecraft.level, EntitySpawnReason.LOAD);

            if (entity == null) {
                continue;
            }

            preparePreviewEntity(entity);

            boolean identified = IdentifiedSpeciesData.CLIENT_CACHE.getIdentifiedSpecies().contains(species);

            this.speciesStatuses.put(species, new EntityIdentified(entity, identified));
        }
        ClientboundIdentifiedSpeciesSyncPacket.addListener(identifiedSpecies -> {
            for (Species species : Species.values()) {
                LivingEntity entity = speciesStatuses.get(species).entity;
                speciesStatuses.put(species, new EntityIdentified(entity, identifiedSpecies.contains(species)));
            }
        });

        int buttonY = this.topPos + this.imageHeight - 21;

        this.previousPageButton = this.addRenderableWidget(Button.builder(Component.literal("<"), button -> {
            if (this.currentPage > 0) {
                this.currentPage--;
                this.updatePaginationButtons();
            }
        }).bounds(this.leftPos + 5, buttonY, 16, 16).build());

        this.nextPageButton = this.addRenderableWidget(Button.builder(Component.literal(">"), button -> {
            if (this.currentPage < this.getPageCount() - 1) {
                this.currentPage++;
                this.updatePaginationButtons();
            }
        }).bounds(this.leftPos + 57, buttonY, 16, 16).build());

        this.updatePaginationButtons();
    }

    private int getEntriesPerPage() {
        int listTop = this.topPos + 5;
        int listBottom = this.topPos + this.imageHeight - 25;
        int availableHeight = listBottom - listTop;

        return Math.max(1, availableHeight / 68);
    }

    private int getPageCount() {
        int entriesPerPage = this.getEntriesPerPage();
        int entryCount = this.speciesStatuses.size();

        return Math.max(1, (entryCount + entriesPerPage - 1) / entriesPerPage);
    }

    private void updatePaginationButtons() {
        int pageCount = this.getPageCount();

        this.currentPage = Mth.clamp(this.currentPage, 0, Math.max(0, pageCount - 1));

        if (this.previousPageButton != null) {
            this.previousPageButton.active = this.currentPage > 0;
        }

        if (this.nextPageButton != null) {
            this.nextPageButton.active = this.currentPage < pageCount - 1;
        }
    }

    private static void preparePreviewEntity(LivingEntity entity) {
        float yaw = 0.0F;

        entity.setYRot(yaw);
        entity.yRotO = yaw;
        entity.setXRot(0.0F);
        entity.xRotO = 0.0F;

        entity.yBodyRot = yaw;
        entity.yBodyRotO = yaw;
        entity.yHeadRot = yaw;
        entity.yHeadRotO = yaw;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, 512, 512);
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, ARROW, 24, 16, 0, 0, this.leftPos + 76 + 78, this.topPos + 34, this.menu.getProgressWidth(), 16);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractRenderState(graphics, mouseX, mouseY, a);
        this.extractSpecies(graphics, mouseX, mouseY, a);
    }

    private void extractSpecies(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        EntityRenderDispatcher dispatcher = this.minecraft.getEntityRenderDispatcher();

        int startX = this.leftPos + 5;
        int startY = this.topPos + 5;

        int cellHeight = 68;
        int renderWidth = 68;
        float scale = 12.0F;

        Quaternionf entityRotation = new Quaternionf().rotateZ((float) Math.PI).rotateY((float) Math.toRadians(215.0F));

        Quaternionf cameraRotation = new Quaternionf().rotateX((float) Math.toRadians(-25.0F));

        int entriesPerPage = this.getEntriesPerPage();
        int firstIndex = this.currentPage * entriesPerPage;
        int endIndex = Math.min(firstIndex + entriesPerPage, this.speciesStatuses.size());

        String pageText = (this.currentPage + 1) + "/" + this.getPageCount();

        graphics.centeredText(this.font, pageText, this.leftPos + 38, this.topPos + this.imageHeight - 18, 0xFFFFFFFF);

        int absoluteIndex = 0;
        int visibleIndex = 0;

        for (Map.Entry<Species, EntityIdentified> entry : this.speciesStatuses.entrySet()) {
            if (absoluteIndex < firstIndex) {
                absoluteIndex++;
                continue;
            }

            if (absoluteIndex >= endIndex) {
                break;
            }

            LivingEntity entity = entry.getValue().entity();
            float entityHeight = entity.getBbHeight();

            int bottomY = startY + ((visibleIndex + 1) * cellHeight);

            EntityRenderState renderState = dispatcher.extractEntity(entity, partialTick);

            ((IdentifiedSpeciesRenderState) renderState).setIdentified(entry.getValue().identified());

            int y0 = startY + (visibleIndex * cellHeight) + (visibleIndex);
            int x1 = startX + renderWidth;

            boolean isHovered = mouseX >= startX && mouseX <= x1 && mouseY >= y0 && mouseY <= bottomY;
            if (isHovered){
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, SLOT_HIGHLIGHT_BACK_SPRITE, startX - 4, y0 - 4, x1 - startX + 8, bottomY - y0 + 8);
                graphics.setTooltipForNextFrame(Component.translatable("species.ancientcreature." + (entry.getValue().identified ? entry.getKey().getSerializedName() : "unidentified")), mouseX, mouseY);
            }

            graphics.entity(renderState, scale, new Vector3f(0.0F, entityHeight * 0.5F, 0.0F), entityRotation, cameraRotation, startX, y0, x1, bottomY);

            visibleIndex++;
            absoluteIndex++;

            if (isHovered){
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, SLOT_HIGHLIGHT_FRONT_SPRITE, startX - 4, y0 - 4, x1 - startX + 8, bottomY - y0 + 8);
                graphics.setTooltipForNextFrame(Component.translatable("species.ancientcreature." + (entry.getValue().identified ? entry.getKey().getSerializedName() : "unidentified")), mouseX, mouseY);
            }
        }
    }

    private record EntityIdentified(LivingEntity entity, boolean identified) {
    }
}
