package com.coolerpromc.ancientcreature.client.gui.guide;

import com.coolerpromc.ancientcreature.block.ModBlocks;
import com.coolerpromc.ancientcreature.client.SpeciesJournalKeyHandler;
import com.coolerpromc.ancientcreature.client.gui.guide.GuideElement.*;
import com.coolerpromc.ancientcreature.client.gui.screen.SpeciesJournalScreen;
import com.coolerpromc.ancientcreature.data.component.ModDataComponents;
import com.coolerpromc.ancientcreature.data.component.custom.DNAData;
import com.coolerpromc.ancientcreature.data.component.custom.FossilDamageRate;
import com.coolerpromc.ancientcreature.data.component.custom.FossilData;
import com.coolerpromc.ancientcreature.data.component.custom.GenomeData;
import com.coolerpromc.ancientcreature.entity.Species;
import com.coolerpromc.ancientcreature.item.DNAIntegrityLevel;
import com.coolerpromc.ancientcreature.item.FossilPart;
import com.coolerpromc.ancientcreature.item.ModItems;
import com.coolerpromc.ancientcreature.registry.ModRegistries;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockItemTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.ItemLike;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * The field guide's chapters. Built each time the guide opens, because fossil parts and species come
 * from datapacks and item stacks need the current world's registries.
 *
 * <p>The crafting grids mirror the datagen recipes in {@code ModRecipeProvider}; clients don't receive
 * the server's recipes, so they can't be looked up here.
 */
public final class GuideBook {
    private GuideBook() {
    }

    public record Chapter(String id, ItemStack icon, List<GuideElement> elements) {
        public Component title() {
            return Component.translatable("guide.ancientcreature." + this.id + ".title");
        }
    }

    public static List<Chapter> build(Consumer<String> openChapter) {
        Stacks stacks = new Stacks();
        List<Chapter> chapters = new ArrayList<>();
        chapters.add(introduction(stacks));
        chapters.add(fossils(stacks));
        chapters.add(chisels(stacks));
        chapters.add(cleaning(stacks));
        chapters.add(identification(stacks));
        chapters.add(dna(stacks));
        chapters.add(genome(stacks));
        chapters.add(embryogenesis(stacks));
        chapters.add(incubation(stacks));
        chapters.add(creatures(stacks));
        chapters.add(automation(stacks));

        // Each chapter ends with a link onward, so the guide can be read front to back.
        List<Chapter> linked = new ArrayList<>();
        for (int i = 0; i < chapters.size(); i++) {
            Chapter chapter = chapters.get(i);
            List<GuideElement> elements = new ArrayList<>(chapter.elements());
            if (i + 1 < chapters.size()) {
                Chapter next = chapters.get(i + 1);
                elements.add(new Spacer(4));
                elements.add(new Link(Component.translatable("guide.ancientcreature.next", next.title()), () -> () -> openChapter.accept(next.id())));
            }
            linked.add(new Chapter(chapter.id(), chapter.icon(), List.copyOf(elements)));
        }
        return linked;
    }

    private static Chapter introduction(Stacks s) {
        return new Chapter("introduction", ModItems.FIELD_GUIDE.toStack(), List.of(
            p("introduction.1"),
            h("introduction.journey"),
            new Showcase(List.of(
                Slot.of(ModItems.STONE_CHISEL.toStack()),
                Slot.of(s.fossil(FossilPart.SKULL, true, false)),
                Slot.of(ModBlocks.FOSSIL_CLEANING_TABLE.toStack()),
                Slot.of(ModBlocks.FOSSIL_IDENTIFICATION_CHAMBER.toStack()),
                Slot.of(ModBlocks.DNA_EXTRACTOR.toStack()),
                Slot.of(ModBlocks.GENOME_SEQUENCER.toStack()),
                Slot.of(ModBlocks.EMBRYOGENESIS_CHAMBER.toStack()),
                Slot.of(ModBlocks.INCUBATOR.toStack()),
                Slot.of(s.capsule())
            )),
            p("introduction.step1"),
            p("introduction.step2"),
            p("introduction.step3"),
            p("introduction.step4"),
            p("introduction.step5"),
            p("introduction.step6"),
            p("introduction.step7"),
            p("introduction.step8"),
            tip("introduction.tip"),
            h("introduction.journal"),
            new Paragraph(Component.translatable("guide.ancientcreature.introduction.journal.1", SpeciesJournalKeyHandler.OPEN_JOURNAL.getTranslatedKeyMessage())),
            journalLink()
        ));
    }

    private static Chapter fossils(Stacks s) {
        List<Table.Row> parts = new ArrayList<>();
        for (Holder<FossilPart> part : s.fossilParts()) {
            String name = part.unwrapKey().map(key -> key.identifier().getPath()).orElse("?");
            FossilPart value = part.value();
            parts.add(new Table.Row(
                Slot.of(s.fossil(part, false, false)),
                Component.translatable("fossilPart.ancientcreature." + name),
                Component.translatable("guide.ancientcreature.fossils.part_stats", percent(value.identifyFailChance()), signedPercent(value.dnaExtractingBonus()))
            ));
        }

        return new Chapter("fossils", s.fossil(FossilPart.SKULL, true, false), List.of(
            p("fossils.1"),
            p("fossils.2"),
            h("fossils.ore"),
            new Showcase(List.of(Slot.of(ModBlocks.FOSSIL_ORE.toStack()), Slot.of(ModItems.STONE_CHISEL.toStack()))),
            p("fossils.ore.1"),
            h("fossils.rock_pile"),
            new Showcase(List.of(Slot.of(ModBlocks.ROCK_PILE.toStack()), Slot.of(ModItems.ROCK_FRAGMENT.toStack()), Slot.of(s.fossil(FossilPart.EGG, true, false)))),
            p("fossils.rock_pile.1"),
            p("fossils.rock_pile.2"),
            h("fossils.dig_site"),
            new Showcase(List.of(Slot.of(new ItemStack(Items.SUSPICIOUS_SAND)), Slot.of(new ItemStack(Items.SUSPICIOUS_GRAVEL)), Slot.of(new ItemStack(Items.BRUSH)), Slot.of(ModItems.EGG_SHELL_FRAGMENT.toStack()))),
            p("fossils.dig_site.1"),
            h("fossils.parts"),
            p("fossils.parts.1"),
            new Table(parts),
            tip("fossils.tip")
        ));
    }

    private static Chapter chisels(Stacks s) {
        List<ItemStack> chiselStacks = List.of(
            ModItems.STONE_CHISEL.toStack(), ModItems.COPPER_CHISEL.toStack(), ModItems.IRON_CHISEL.toStack(),
            ModItems.GOLDEN_CHISEL.toStack(), ModItems.DIAMOND_CHISEL.toStack(), ModItems.NETHERITE_CHISEL.toStack()
        );
        List<Table.Row> rows = new ArrayList<>();
        for (ItemStack chisel : chiselStacks) {
            FossilDamageRate rate = chisel.get(ModDataComponents.FOSSIL_DAMAGE_RATE.get());
            float damage = rate == null ? 0 : rate.value();
            rows.add(new Table.Row(Slot.of(chisel), chisel.getHoverName(), Component.translatable("guide.ancientcreature.chisels.damage", percent(damage))));
        }

        // One grid cycling through every material; the material and result lists line up so they
        // change together.
        Slot material = new Slot(List.of(stack(Items.COBBLESTONE), stack(Items.COPPER_INGOT), stack(Items.IRON_INGOT), stack(Items.GOLD_INGOT), stack(Items.DIAMOND)));
        Slot result = new Slot(chiselStacks.subList(0, 5));
        Slot stick = Slot.of(stack(Items.STICK));

        return new Chapter("chisels", ModItems.IRON_CHISEL.toStack(), List.of(
            p("chisels.1"),
            new Table(rows),
            h("chisels.crafting"),
            new Crafting(List.of(Slot.EMPTY, Slot.EMPTY, material, Slot.EMPTY, material, Slot.EMPTY, stick, Slot.EMPTY, Slot.EMPTY), result, false),
            p("chisels.netherite"),
            new MachineStep(
                List.of(Slot.of(stack(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE)), Slot.of(ModItems.DIAMOND_CHISEL.toStack()), Slot.of(stack(Items.NETHERITE_INGOT))),
                Slot.of(stack(Items.SMITHING_TABLE)),
                List.of(Slot.of(ModItems.NETHERITE_CHISEL.toStack()))
            ),
            p("chisels.2")
        ));
    }

    private static Chapter cleaning(Stacks s) {
        return new Chapter("cleaning", ModBlocks.FOSSIL_CLEANING_TABLE.toStack(), List.of(
            p("cleaning.1"),
            s.shaped(ModBlocks.FOSSIL_CLEANING_TABLE.toStack(), new String[]{"W W", "WBW", "SSS"},
                'W', tag(ItemTags.PLANKS), 'B', slot(Items.BRUSH), 'S', tag(BlockItemTags.SLABS.item())),
            p("cleaning.2"),
            new MachineStep(
                List.of(Slot.of(stack(Items.BRUSH)), Slot.of(s.fossil(FossilPart.RIB, true, false))),
                Slot.of(ModBlocks.FOSSIL_CLEANING_TABLE.toStack()),
                List.of(Slot.of(s.fossil(FossilPart.RIB, false, false)), Slot.of(ModItems.DIRT_FRAGMENT.toStack()))
            ),
            p("cleaning.3"),
            p("cleaning.4")
        ));
    }

    private static Chapter identification(Stacks s) {
        return new Chapter("identification", ModBlocks.FOSSIL_IDENTIFICATION_CHAMBER.toStack(), List.of(
            p("identification.1"),
            s.shaped(ModBlocks.FOSSIL_IDENTIFICATION_CHAMBER.toStack(), new String[]{"PGP", "IBI", "III"},
                'P', slot(Items.GLASS_PANE), 'G', slot(Items.LAPIS_LAZULI), 'I', slot(Items.IRON_INGOT), 'B', slot(Items.BOOK)),
            new MachineStep(
                List.of(Slot.of(s.fossil(FossilPart.TOOTH, false, false))),
                Slot.of(ModBlocks.FOSSIL_IDENTIFICATION_CHAMBER.toStack()),
                List.of(Slot.of(s.fossil(FossilPart.TOOTH, false, true)))
            ),
            h("identification.first"),
            p("identification.first.1"),
            p("identification.first.2"),
            tip("identification.tip")
        ));
    }

    private static Chapter dna(Stacks s) {
        List<Table.Row> levels = new ArrayList<>();
        String[] scores = {"5-15%", "16-30%", "31-60%", "61%+"};
        String[] added = {"1-4%", "4-8%", "8-15%", "15-30%"};
        DNAIntegrityLevel[] values = DNAIntegrityLevel.values();
        for (int i = 0; i < values.length; i++) {
            levels.add(new Table.Row(
                Slot.of(s.dna(values[i])),
                Component.translatable("dna.ancientcreature." + values[i].getSerializedName()),
                Component.translatable("guide.ancientcreature.dna.level", scores[i], added[i])
            ));
        }

        return new Chapter("dna", ModBlocks.DNA_EXTRACTOR.toStack(), List.of(
            p("dna.1"),
            s.shaped(ModBlocks.DNA_EXTRACTOR.toStack(), new String[]{"IGI", "RER", "III"},
                'I', slot(Items.IRON_INGOT), 'G', slot(Items.GLASS), 'R', slot(Items.REDSTONE), 'E', slot(Items.FERMENTED_SPIDER_EYE)),
            new MachineStep(
                List.of(Slot.of(ModItems.EXTRACTION_FLUID.toStack()), Slot.of(ModItems.SAMPLE_VIAL.toStack()), Slot.of(s.fossil(FossilPart.TOOTH, false, true))),
                Slot.of(ModBlocks.DNA_EXTRACTOR.toStack()),
                List.of(Slot.of(s.dna(DNAIntegrityLevel.STABLE)))
            ),
            p("dna.2"),
            h("dna.supplies"),
            s.shaped(ModItems.EXTRACTION_FLUID.toStack(), new String[]{" F ", "FPF", " F "}, 'F', s.waterBottle(), 'P', slot(Items.FERMENTED_SPIDER_EYE)),
            s.shaped(ModItems.SAMPLE_VIAL.toStack(), new String[]{"G G", " B "}, 'G', slot(Items.GLASS), 'B', slot(Items.GLASS_BOTTLE)),
            h("dna.quality"),
            p("dna.quality.1"),
            new Table(levels),
            p("dna.quality.2")
        ));
    }

    private static Chapter genome(Stacks s) {
        return new Chapter("genome", ModBlocks.GENOME_SEQUENCER.toStack(), List.of(
            p("genome.1"),
            s.shaped(ModBlocks.GENOME_SEQUENCER.toStack(), new String[]{"CRC", "RLR", "CCC"},
                'C', slot(Items.COPPER_INGOT), 'R', slot(Items.REDSTONE), 'L', slot(Items.LAPIS_LAZULI)),
            s.shaped(ModItems.GENOME_CARTRIDGE_BLANK.toStack(), new String[]{"CAC", "C C", "CCC"},
                'C', slot(Items.COPPER_INGOT), 'A', slot(Items.AMETHYST_SHARD)),
            p("genome.2"),
            new MachineStep(
                List.of(Slot.of(s.dna(DNAIntegrityLevel.STABLE)), Slot.of(ModItems.GENOME_CARTRIDGE_BLANK.toStack())),
                Slot.of(ModBlocks.GENOME_SEQUENCER.toStack()),
                List.of(Slot.of(s.filledCartridge()))
            ),
            p("genome.3"),
            new MachineStep(
                List.of(Slot.of(s.dna(DNAIntegrityLevel.PRESERVED_EMBRYO)), Slot.of(s.filledCartridge())),
                Slot.of(ModBlocks.GENOME_SEQUENCER.toStack()),
                List.of(Slot.of(s.withSpecies(ModItems.GENOME_CARTRIDGE_COMPLETED.toStack())))
            ),
            p("genome.4"),
            tip("genome.tip")
        ));
    }

    private static Chapter embryogenesis(Stacks s) {
        return new Chapter("embryogenesis", ModBlocks.EMBRYOGENESIS_CHAMBER.toStack(), List.of(
            p("embryogenesis.1"),
            s.shaped(ModBlocks.EMBRYOGENESIS_CHAMBER.toStack(), new String[]{"GAG", "SCS", "GGG"},
                'G', slot(Items.GOLD_INGOT), 'A', slot(Items.AMETHYST_BLOCK), 'S', slot(Items.SLIME_BALL), 'C', slot(Items.GLASS)),
            new MachineStep(
                List.of(Slot.of(s.withSpecies(ModItems.GENOME_CARTRIDGE_COMPLETED.toStack())), Slot.of(ModItems.ARTIFICIAL_EGG.toStack()), Slot.of(ModItems.NUTRIENT_SOLUTION.toStack())),
                Slot.of(ModBlocks.EMBRYOGENESIS_CHAMBER.toStack()),
                List.of(Slot.of(s.withSpecies(ModItems.FERTILIZED_ANCIENT_EGG.toStack())))
            ),
            p("embryogenesis.2"),
            h("embryogenesis.supplies"),
            new Crafting(List.of(Slot.of(ModItems.EGG_SHELL_FRAGMENT.toStack()), Slot.of(ModItems.EGG_SHELL_FRAGMENT.toStack()), Slot.of(ModItems.NUTRIENT_SOLUTION.toStack())), Slot.of(ModItems.ARTIFICIAL_EGG.toStack()), true),
            s.shaped(new ItemStack(ModItems.NUTRIENT_SOLUTION.get(), 4), new String[]{" F ", "FPF", " F "}, 'F', s.waterBottle(), 'P', slot(Items.BONE_MEAL)),
            p("embryogenesis.3")
        ));
    }

    private static Chapter incubation(Stacks s) {
        return new Chapter("incubation", ModBlocks.INCUBATOR.toStack(), List.of(
            p("incubation.1"),
            s.shaped(ModBlocks.INCUBATOR.toStack(), new String[]{"IMI", "MNM", "IGI"},
                'I', slot(Items.IRON_INGOT), 'M', slot(Items.MAGMA_BLOCK), 'N', slot(Items.NETHERITE_SCRAP), 'G', slot(Items.GLASS)),
            new MachineStep(
                List.of(Slot.of(s.withSpecies(ModItems.FERTILIZED_ANCIENT_EGG.toStack()))),
                Slot.of(ModBlocks.INCUBATOR.toStack()),
                List.of(Slot.of(s.capsule()))
            ),
            p("incubation.2"),
            h("incubation.release"),
            new Showcase(List.of(Slot.of(s.capsule()))),
            p("incubation.release.1"),
            p("incubation.release.2")
        ));
    }

    private static Chapter creatures(Stacks s) {
        return new Chapter("creatures", s.capsule(), List.of(
            p("creatures.1"),
            h("creatures.hunger"),
            p("creatures.hunger.1"),
            p("creatures.hunger.2"),
            h("creatures.breeding"),
            p("creatures.breeding.1"),
            h("creatures.riding"),
            p("creatures.riding.1"),
            p("creatures.riding.2"),
            p("creatures.riding.3"),
            tip("creatures.tip"),
            journalLink()
        ));
    }

    private static Chapter automation(Stacks s) {
        return new Chapter("automation", new ItemStack(Items.HOPPER), List.of(
            p("automation.1"),
            new Table(List.of(
                automationRow(ModBlocks.FOSSIL_CLEANING_TABLE.toStack(), "cleaning"),
                automationRow(ModBlocks.FOSSIL_IDENTIFICATION_CHAMBER.toStack(), "identification"),
                automationRow(ModBlocks.DNA_EXTRACTOR.toStack(), "dna"),
                automationRow(ModBlocks.GENOME_SEQUENCER.toStack(), "genome"),
                automationRow(ModBlocks.EMBRYOGENESIS_CHAMBER.toStack(), "embryogenesis"),
                automationRow(ModBlocks.INCUBATOR.toStack(), "incubation")
            )),
            tip("automation.tip")
        ));
    }

    private static Table.Row automationRow(ItemStack machine, String key) {
        return new Table.Row(Slot.of(machine), machine.getHoverName(), Component.translatable("guide.ancientcreature.automation." + key));
    }

    private static Link journalLink() {
        return new Link(Component.translatable("guide.ancientcreature.open_journal"),
            () -> () -> Minecraft.getInstance().gui.setScreen(new SpeciesJournalScreen()));
    }

    private static Heading h(String key) {
        return new Heading(Component.translatable("guide.ancientcreature." + key));
    }

    private static Paragraph p(String key) {
        return new Paragraph(Component.translatable("guide.ancientcreature." + key));
    }

    private static Tip tip(String key) {
        return new Tip(Component.translatable("guide.ancientcreature." + key));
    }

    private static ItemStack stack(ItemLike item) {
        return new ItemStack(item);
    }

    private static Slot slot(ItemLike item) {
        return Slot.of(new ItemStack(item));
    }

    private static Slot tag(TagKey<Item> tag) {
        List<ItemStack> stacks = new ArrayList<>();
        for (Holder<Item> item : BuiltInRegistries.ITEM.getTagOrEmpty(tag)) {
            stacks.add(new ItemStack(item));
        }
        return new Slot(stacks);
    }

    private static String percent(float value) {
        return Math.round(value * 100) + "%";
    }

    private static String signedPercent(float value) {
        int rounded = Math.round(value * 100);
        return (rounded > 0 ? "+" : "") + rounded + "%";
    }

    /** Builds example stacks against the current world, so data-driven fossil parts resolve. */
    private static final class Stacks {
        private final Optional<Registry<FossilPart>> fossilParts;
        private final Species species;

        Stacks() {
            Minecraft minecraft = Minecraft.getInstance();
            this.fossilParts = minecraft.level == null ? Optional.empty() : minecraft.level.registryAccess().lookup(ModRegistries.FOSSIL_PART);
            List<Species> all = Species.values();
            this.species = all.contains(Species.TRICERATOPS) || all.isEmpty() ? Species.TRICERATOPS : all.getFirst();
        }

        List<Holder<FossilPart>> fossilParts() {
            return this.fossilParts.map(lookup -> lookup.listElements().<Holder<FossilPart>>map(holder -> holder)
                .sorted((a, b) -> Float.compare(a.value().identifyFailChance(), b.value().identifyFailChance()))
                .toList()).orElse(List.of());
        }

        ItemStack fossil(ResourceKey<FossilPart> key, boolean dirty, boolean identified) {
            return this.fossilParts.flatMap(lookup -> lookup.get(key))
                .map(holder -> this.fossil(holder, dirty, identified))
                .orElseGet(ModItems.FOSSIL_PART::toStack);
        }

        ItemStack fossil(Holder<FossilPart> part, boolean dirty, boolean identified) {
            ItemStack stack = ModItems.FOSSIL_PART.toStack();
            stack.set(ModDataComponents.FOSSIL_DATA.get(), new FossilData(part, this.species, 0.5f, dirty, identified, false));
            return stack;
        }

        ItemStack dna(DNAIntegrityLevel level) {
            ItemStack stack = ModItems.DNA_SAMPLE.toStack();
            stack.set(ModDataComponents.DNA_DATA.get(), new DNAData(level, this.species));
            return stack;
        }

        ItemStack filledCartridge() {
            ItemStack stack = ModItems.GENOME_CARTRIDGE_FILLED.toStack();
            stack.set(ModDataComponents.GENOME_DATA.get(), new GenomeData(0.9f, this.species));
            return stack;
        }

        ItemStack withSpecies(ItemStack stack) {
            stack.set(ModDataComponents.SPECIES.get(), this.species);
            return stack;
        }

        ItemStack capsule() {
            return this.withSpecies(ModItems.BABY_CREATURE_CAPSULE.toStack());
        }

        Slot waterBottle() {
            return Slot.of(PotionContents.createItemStack(Items.POTION, Potions.WATER));
        }

        /** A shaped recipe from the same pattern/key form the datagen uses. */
        Crafting shaped(ItemStack result, String[] pattern, Object... keys) {
            Map<Character, Slot> key = new HashMap<>();
            for (int i = 0; i < keys.length; i += 2) {
                key.put((Character) keys[i], (Slot) keys[i + 1]);
            }
            List<Slot> grid = new ArrayList<>();
            for (int row = 0; row < 3; row++) {
                String line = row < pattern.length ? pattern[row] : "";
                for (int column = 0; column < 3; column++) {
                    char c = column < line.length() ? line.charAt(column) : ' ';
                    grid.add(key.getOrDefault(c, Slot.EMPTY));
                }
            }
            return new Crafting(grid, Slot.of(result), false);
        }
    }
}
