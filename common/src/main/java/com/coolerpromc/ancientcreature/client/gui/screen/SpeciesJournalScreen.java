package com.coolerpromc.ancientcreature.client.gui.screen;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.client.SpeciesJournalKeyHandler;
import com.coolerpromc.ancientcreature.client.species.ClientSpeciesDefinition;
import com.coolerpromc.ancientcreature.client.species.ClientSpeciesGuiSettings;
import com.coolerpromc.ancientcreature.client.species.ClientSpeciesManager;
import com.coolerpromc.ancientcreature.entity.Species;
import com.coolerpromc.ancientcreature.entity.custom.AncientCreatureEntity;
import com.coolerpromc.ancientcreature.saveddata.IdentifiedSpeciesData;
import com.coolerpromc.ancientcreature.species.SpeciesDefinition;
import com.coolerpromc.ancientcreature.species.SpeciesDietProperties;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.biome.Biome;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class SpeciesJournalScreen extends Screen {
    private static final Identifier TEXTURE = Constants.id("textures/gui/screen/species_journal.png");
    private static final int TEXTURE_WIDTH = 470;
    private static final int TEXTURE_HEIGHT = 258;
    private static final int DISPLAY_WIDTH = 420;
    private static final int DISPLAY_HEIGHT = 231;
    private static final int INK = 0xFFE8E3D4;
    private static final int MUTED = 0xFF9EA69E;
    private static final int GOLD = 0xFFE0A83B;
    private static final int GOLD_DARK = 0xAA76501E;
    private static final int PANEL_3 = 0x66303836;
    private static final int REDACTED = 0xFF69706C;

    private final List<Species> allSpecies = new ArrayList<>();
    private final List<Species> filteredSpecies = new ArrayList<>();
    private EditBox searchBox;
    private Species selected;
    private AncientCreatureEntity previewEntity;
    private int scrollOffset;
    private int panelX;
    private int panelY;
    private int panelWidth;
    private int panelHeight;
    private int listWidth;
    private float textureScale;
    private boolean selectedWasIdentified;

    public SpeciesJournalScreen() {
        super(Component.translatable("screen.ancientcreature.species_journal"));
    }

    @Override
    protected void init() {
        float screenFit = Math.min(1.0F, Math.min((this.width - 24) / (float) DISPLAY_WIDTH, (this.height - 24) / (float) DISPLAY_HEIGHT));
        this.panelWidth = Math.round(DISPLAY_WIDTH * screenFit);
        this.panelHeight = Math.round(DISPLAY_HEIGHT * screenFit);
        this.textureScale = this.panelWidth / (float) TEXTURE_WIDTH;
        this.panelX = (this.width - this.panelWidth) / 2;
        this.panelY = (this.height - this.panelHeight) / 2;
        this.listWidth = this.scaled(130);

        this.allSpecies.clear();
        this.allSpecies.addAll(Species.values());
        this.allSpecies.sort((a, b) -> a.displayName().getString().compareToIgnoreCase(b.displayName().getString()));

        this.searchBox = new EditBox(this.font, this.panelX + this.scaled(28), this.panelY + this.scaled(54), this.listWidth - this.scaled(22), Math.max(14, this.scaled(17)), Component.translatable("screen.ancientcreature.species_journal.search"));
        this.searchBox.setHint(Component.translatable("screen.ancientcreature.species_journal.search"));
        this.searchBox.setMaxLength(48);
        this.searchBox.setBordered(false);
        this.searchBox.setTextShadow(false);
        this.searchBox.setResponder(value -> this.rebuildFilteredSpecies());
        this.addRenderableWidget(this.searchBox);

        this.rebuildFilteredSpecies();
        if (this.selected == null || !this.allSpecies.contains(this.selected)) {
            this.selected = this.filteredSpecies.stream().filter(this::isIdentified).findFirst()
                .orElse(this.filteredSpecies.isEmpty() ? null : this.filteredSpecies.getFirst());
        }
        this.ensureSelectedVisible();
        this.updatePreviewEntity();
    }

    private void rebuildFilteredSpecies() {
        String query = this.searchBox == null ? "" : this.searchBox.getValue().trim().toLowerCase(Locale.ROOT);
        this.filteredSpecies.clear();
        for (Species species : this.allSpecies) {
            boolean identified = this.isIdentified(species);
            String searchableName = identified ? species.displayName().getString().toLowerCase(Locale.ROOT) : "";
            if (query.isEmpty() || searchableName.contains(query)) {
                this.filteredSpecies.add(species);
            }
        }
        this.scrollOffset = Mth.clamp(this.scrollOffset, 0, this.maxScroll());
    }

    private boolean isIdentified(Species species) {
        return IdentifiedSpeciesData.CLIENT_CACHE.getIdentifiedSpecies().contains(species);
    }

    private int visibleRows() {
        return Math.max(1, (this.scaled(244) - this.scaled(66)) / this.rowHeight());
    }

    private int maxScroll() {
        return Math.max(0, this.filteredSpecies.size() - this.visibleRows());
    }

    private int rowHeight() {
        return Math.max(14, this.scaled(18));
    }

    private void select(Species species) {
        if (!species.equals(this.selected)) {
            this.selected = species;
            this.updatePreviewEntity();
        }
    }

    private void ensureSelectedVisible() {
        int index = this.filteredSpecies.indexOf(this.selected);
        if (index < 0) return;
        if (index < this.scrollOffset) {
            this.scrollOffset = index;
        } else if (index >= this.scrollOffset + this.visibleRows()) {
            this.scrollOffset = index - this.visibleRows() + 1;
        }
        this.scrollOffset = Mth.clamp(this.scrollOffset, 0, this.maxScroll());
    }

    private void updatePreviewEntity() {
        this.previewEntity = null;
        this.selectedWasIdentified = this.selected != null && this.isIdentified(this.selected);
        if (this.selected == null || !this.isIdentified(this.selected) || this.minecraft.level == null) {
            return;
        }
        if (this.selected.getEntityType().create(this.minecraft.level, EntitySpawnReason.LOAD) instanceof AncientCreatureEntity creature) {
            creature.setSpecies(this.selected);
            creature.setYRot(0.0F);
            creature.yRotO = 0.0F;
            creature.yBodyRot = 0.0F;
            creature.yBodyRotO = 0.0F;
            creature.yHeadRot = 0.0F;
            creature.yHeadRotO = 0.0F;
            this.previewEntity = creature;
        }
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        this.extractTransparentBackground(graphics);
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, this.panelX, this.panelY, 0, 0,
            this.panelWidth, this.panelHeight, TEXTURE_WIDTH, TEXTURE_HEIGHT, TEXTURE_WIDTH, TEXTURE_HEIGHT);
        int searchX = this.panelX + this.scaled(25);
        int searchY = this.panelY + this.scaled(48);
        int searchHeight = Math.max(18, this.scaled(21));
        graphics.fill(searchX, searchY, this.panelX + this.listWidth + this.scaled(5), searchY + searchHeight, 0x990A100F);
        graphics.outline(searchX, searchY, this.listWidth - this.scaled(20), searchHeight, 0xFF684A25);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);

        long identified = this.allSpecies.stream().filter(this::isIdentified).count();
        graphics.text(this.font, Component.translatable("screen.ancientcreature.species_journal"), this.panelX + this.scaled(45), this.panelY + this.scaled(20), GOLD, false);
        String progress = Component.translatable("screen.ancientcreature.species_journal.progress", identified, this.allSpecies.size()).getString();
        graphics.text(this.font, progress, this.panelX + this.panelWidth - this.font.width(progress) - this.scaled(45), this.panelY + this.scaled(20), MUTED, false);

        this.extractSpeciesList(graphics, mouseX, mouseY);
        this.extractSelectedSpecies(graphics, mouseX, mouseY, partialTick);
    }

    private void extractSpeciesList(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        int listTop = this.panelY + this.scaled(70);
        int rows = this.visibleRows();
        int rowHeight = this.rowHeight();
        graphics.enableScissor(
            this.panelX + this.scaled(25),
            listTop,
            this.panelX + this.listWidth + this.scaled(5),
            this.panelY + this.scaled(244)
        );
        for (int row = 0; row < rows; row++) {
            int index = this.scrollOffset + row;
            if (index >= this.filteredSpecies.size()) break;
            Species species = this.filteredSpecies.get(index);
            int y = listTop + row * rowHeight;
            int textY = y + Math.max(2, (rowHeight - 9) / 2);
            boolean hovered = mouseX >= this.panelX + this.scaled(25) && mouseX < this.panelX + this.listWidth + this.scaled(5) && mouseY >= y && mouseY < y + rowHeight;
            if (species.equals(this.selected)) {
                graphics.fill(this.panelX + this.scaled(25), y, this.panelX + this.listWidth + this.scaled(25), y + rowHeight - 1, GOLD_DARK);
                graphics.fill(this.panelX + this.scaled(25), y, this.panelX + this.scaled(26), y + rowHeight - 1, GOLD);
            } else if (hovered) {
                graphics.fill(this.panelX + this.scaled(25), y, this.panelX + this.listWidth + this.scaled(25), y + rowHeight - 1, PANEL_3);
            }
            boolean known = this.isIdentified(species);
            String indexLabel = String.format(Locale.ROOT, "%03d", this.allSpecies.indexOf(species) + 1);
            graphics.text(this.font, indexLabel, this.panelX + this.scaled(28), textY, known ? GOLD : REDACTED, false);
            graphics.text(this.font, known ? species.displayName() : Component.translatable("species.ancientcreature.unidentified"), this.panelX + this.scaled(55), textY, known ? INK : REDACTED, false);
        }
        graphics.disableScissor();

        if (this.filteredSpecies.isEmpty()) {
            graphics.centeredText(this.font, Component.translatable("screen.ancientcreature.species_journal.no_results"),
                this.panelX + this.listWidth / 2, listTop + 12, MUTED);
        }
    }

    private void extractSelectedSpecies(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        int contentX = this.panelX + this.scaled(164);
        int contentWidth = this.panelWidth - this.scaled(174);
        int viewportTop = this.panelY + this.scaled(42);
        int viewportHeight = this.scaled(116);

        if (this.selected == null) return;
        if (!this.isIdentified(this.selected)) {
            graphics.centeredText(this.font, "?", contentX + contentWidth / 2, viewportTop + viewportHeight / 2 - 12, REDACTED);
            graphics.centeredText(this.font, Component.translatable("screen.ancientcreature.species_journal.classified"),
                contentX + contentWidth / 2, viewportTop + viewportHeight - 18, MUTED);
            return;
        }

        if (this.previewEntity != null) {
            EntityRenderDispatcher dispatcher = this.minecraft.getEntityRenderDispatcher();
            EntityRenderState state = dispatcher.extractEntity(this.previewEntity, partialTick);
            ClientSpeciesDefinition clientDefinition = ClientSpeciesManager.INSTANCE.get(this.selected.id());
            ClientSpeciesGuiSettings settings = clientDefinition == null ? ClientSpeciesGuiSettings.DEFAULT : clientDefinition.gui();
            Vector3f rotation = new Vector3f(settings.rotation());
            Quaternionf entityRotation = new Quaternionf().rotateZ((float) Math.PI).rotateXYZ(
                (float) Math.toRadians(rotation.x), (float) Math.toRadians(rotation.y), (float) Math.toRadians(rotation.z));
            Quaternionf cameraRotation = new Quaternionf().rotateX((float) Math.toRadians(-20.0F));
            float scale = settings.scale() * Math.min(1.55F, viewportHeight / 68.0F);
            graphics.entity(state, scale, new Vector3f(settings.translation()), entityRotation, cameraRotation,
                contentX + 1, viewportTop + 1, contentX + contentWidth - 1, viewportTop + viewportHeight - 1);
        }

        SpeciesDefinition definition = this.selected.definitionOrFallback();
        String category = Component.translatable("screen.ancientcreature.species_journal.category." + definition.category().getSerializedName()).getString();
        graphics.text(this.font, this.selected.displayName(), contentX + 8, viewportTop + 7, INK, true);
        graphics.text(this.font, category.toUpperCase(Locale.ROOT), contentX + 8, viewportTop + 19, GOLD, false);

        int infoY = this.panelY + this.scaled(167);
        int firstColumnX = this.panelX + this.scaled(163);
        int secondColumnX = this.panelX + this.scaled(260);
        int thirdColumnX = this.panelX + this.scaled(360);
        int rowStep = this.scaled(26);
        double health = attribute(definition, "max_health");
        double attack = attribute(definition, "attack_damage");
        double speed = attribute(definition, "movement_speed");
        String diet = this.dietNames(definition.diet());
        String habitat = this.habitatNames(definition);
        int firstColumnWidth = secondColumnX - firstColumnX - this.scaled(8);
        int secondColumnWidth = thirdColumnX - secondColumnX - this.scaled(8);

        field(graphics, firstColumnX, infoY, "size", format(definition.physical().width()) + " x " + format(definition.physical().height()) + " m");
        field(graphics, secondColumnX, infoY, "health", format(health));
        field(graphics, thirdColumnX, infoY, "attack", format(attack));
        field(graphics, firstColumnX, infoY + rowStep, "diet", diet, firstColumnWidth, mouseX, mouseY);
        field(graphics, secondColumnX, infoY + rowStep, "habitat", habitat, secondColumnWidth, mouseX, mouseY);
        field(graphics, thirdColumnX, infoY + rowStep, "speed", format(speed));
        field(graphics, firstColumnX, infoY + rowStep * 2, "maturity", ticksToTime(definition.growth().adultAge()));
        field(graphics, secondColumnX, infoY + rowStep * 2, "incubation", ticksToTime(definition.spawn().incubationTime()));
    }

    private void field(GuiGraphicsExtractor graphics, int x, int y, String key, String value) {
        graphics.text(this.font, Component.translatable("screen.ancientcreature.species_journal." + key), x, y, MUTED, false);
        graphics.text(this.font, value, x, y + 11, INK, false);
    }

    private void field(GuiGraphicsExtractor graphics, int x, int y, String key, String value, int maxWidth, int mouseX, int mouseY) {
        graphics.text(this.font, Component.translatable("screen.ancientcreature.species_journal." + key), x, y, MUTED, false);
        boolean overflow = this.font.width(value) > maxWidth;
        graphics.text(this.font, overflow ? this.ellipsize(value, maxWidth) : value, x, y + 11, INK, false);

        if (overflow && mouseX >= x && mouseX < x + maxWidth && mouseY >= y && mouseY < y + 22) {
            int tooltipWidth = Math.min(240, Math.max(120, this.width - 24));
            graphics.setTooltipForNextFrame(this.font, this.font.split(Component.literal(value), tooltipWidth), mouseX, mouseY);
        }
    }

    private String ellipsize(String value, int maxWidth) {
        String ellipsis = "...";
        int available = Math.max(0, maxWidth - this.font.width(ellipsis));
        return this.font.plainSubstrByWidth(value, available) + ellipsis;
    }

    private static double attribute(SpeciesDefinition definition, String path) {
        return definition.attributes().values().getOrDefault(Identifier.withDefaultNamespace(path), 0.0D);
    }

    private String dietNames(SpeciesDietProperties diet) {
        List<Component> names = new ArrayList<>();
        diet.tag().ifPresent(tagId -> {
            TagKey<Item> tag = TagKey.create(Registries.ITEM, tagId);
            for (Holder<Item> item : BuiltInRegistries.ITEM.getTagOrEmpty(tag)) {
                names.add(new ItemStack(item.value()).getHoverName());
            }
        });
        for (Identifier itemId : diet.items()) {
            if (BuiltInRegistries.ITEM.containsKey(itemId)) {
                names.add(new ItemStack(BuiltInRegistries.ITEM.getValue(itemId)).getHoverName());
            }
        }
        return joinedNames(names, diet.tag().map(id -> "#" + id).orElse("-"));
    }

    private String habitatNames(SpeciesDefinition definition) {
        if (this.minecraft.level == null) {
            return "#" + definition.spawn().biomeTag();
        }

        List<Component> names = new ArrayList<>();
        var biomes = this.minecraft.level.registryAccess().lookupOrThrow(Registries.BIOME).get(definition.spawn().biomeTagKey());
        biomes.ifPresent(entries -> {
            for (Holder<Biome> biome : entries) {
                biome.unwrapKey().ifPresent(key -> names.add(Component.translatable(key.identifier().toLanguageKey("biome"))));
            }
        });
        return joinedNames(names, "#" + definition.spawn().biomeTag());
    }

    private static String joinedNames(List<Component> components, String fallback) {
        List<String> names = components.stream()
            .map(Component::getString)
            .filter(name -> !name.isBlank())
            .distinct()
            .sorted(String.CASE_INSENSITIVE_ORDER)
            .toList();
        return names.isEmpty() ? fallback : String.join(", ", names);
    }

    private static String ticksToTime(int ticks) {
        int seconds = Math.max(1, ticks / 20);
        if (seconds >= 60) return (seconds / 60) + "m " + (seconds % 60) + "s";
        return seconds + "s";
    }

    private static String format(double value) {
        if (Math.abs(value - Math.rint(value)) < 0.001D) return Integer.toString((int) Math.rint(value));
        return String.format(Locale.ROOT, "%.2f", value).replaceAll("0+$", "").replaceAll("\\.$", "");
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (event.button() == InputConstants.MOUSE_BUTTON_LEFT) {
            int listTop = this.panelY + this.scaled(70);
            int rowHeight = this.rowHeight();
            if (event.x() >= this.panelX + this.scaled(25) && event.x() < this.panelX + this.listWidth + this.scaled(5)
                && event.y() >= listTop && event.y() < listTop + this.visibleRows() * rowHeight) {
                int row = (int) ((event.y() - listTop) / rowHeight);
                int index = this.scrollOffset + row;
                if (index >= 0 && index < this.filteredSpecies.size()) {
                    this.select(this.filteredSpecies.get(index));
                    return true;
                }
            }
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (mouseX >= this.panelX && mouseX < this.panelX + this.listWidth) {
            this.scrollOffset = Mth.clamp(this.scrollOffset - (int) Math.signum(verticalAmount), 0, this.maxScroll());
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (SpeciesJournalKeyHandler.OPEN_JOURNAL.matches(event)) {
            this.onClose();
            return true;
        }
        return super.keyPressed(event);
    }

    @Override
    public void tick() {
        super.tick();
        boolean identifiedNow = this.selected != null && this.isIdentified(this.selected);
        if (identifiedNow != this.selectedWasIdentified) {
            this.updatePreviewEntity();
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private int scaled(int pixels) {
        return Math.max(1, Math.round(pixels * this.textureScale));
    }
}
