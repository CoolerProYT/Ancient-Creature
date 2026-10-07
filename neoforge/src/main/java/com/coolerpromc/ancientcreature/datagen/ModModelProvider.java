package com.coolerpromc.ancientcreature.datagen;

import java.util.Optional;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.data.models.model.ModelTemplate;
import com.coolerpromc.ancientcreature.block.custom.SifterBlock;
import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.block.ModBlocks;
import com.coolerpromc.ancientcreature.block.custom.*;
import com.coolerpromc.ancientcreature.client.item.select.DNAIntegritySelect;
import com.coolerpromc.ancientcreature.client.item.special.*;
import com.coolerpromc.ancientcreature.item.DNAIntegrityLevel;
import com.coolerpromc.ancientcreature.item.FossilPart;
import com.coolerpromc.ancientcreature.item.ModItems;
import com.coolerpromc.ancientcreature.registry.ModRegistries;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.ConditionBuilder;
import net.minecraft.client.data.models.blockstates.MultiPartGenerator;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.renderer.block.dispatch.Variant;
import net.minecraft.client.renderer.block.dispatch.VariantMutator;
import net.minecraft.client.renderer.item.SelectItemModel;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static net.minecraft.client.data.models.BlockModelGenerators.ROTATION_HORIZONTAL_FACING;

public class ModModelProvider extends ModelProvider {
    private final HolderLookup.Provider registries;

    public ModModelProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
        super(output, Constants.MODID);
        this.registries = provider.copy().join();
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        blockModels.createTrivialCube(ModBlocks.FOSSIL_ORE.getBlock());
        blockModels.createTrivialCube(ModBlocks.DEEPSLATE_FOSSIL_ORE.getBlock());
        blockModels.createTrivialCube(ModBlocks.AMBER_ORE.getBlock());
        blockModels.createTrivialCube(ModBlocks.FROZEN_FOSSIL.getBlock());
        this.generateSifter(blockModels, itemModels);
        this.generateRockPileBlockState(blockModels);
        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(ModBlocks.FOSSIL_CLEANING_TABLE.getBlock(), new MultiVariant(WeightedList.of(new Variant(Constants.id("block/fossil_cleaning_table"))))).with(horizontalFacing(FossilCleaningTableBlock.FACING)));
        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(ModBlocks.FOSSIL_IDENTIFICATION_CHAMBER.getBlock(), new MultiVariant(WeightedList.of(new Variant(Constants.id("block/fossil_identification_chamber"))))).with(horizontalFacing(FossilIdentificationChamberBlock.FACING)));
        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(ModBlocks.DNA_EXTRACTOR.getBlock(), new MultiVariant(WeightedList.of(new Variant(Constants.id("block/dna_extractor"))))).with(horizontalFacing(DNAExtractorBlock.FACING)));
        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(ModBlocks.GENOME_SEQUENCER.getBlock(), new MultiVariant(WeightedList.of(new Variant(Constants.id("block/genome_sequencer"))))).with(horizontalFacing(DNAExtractorBlock.FACING)));
        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(ModBlocks.EMBRYOGENESIS_CHAMBER.getBlock(), new MultiVariant(WeightedList.of(new Variant(Constants.id("block/embryogenesis_chamber"))))).with(horizontalFacing(EmbryogenesisChamberBlock.FACING)));
        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(ModBlocks.INCUBATOR.getBlock(), new MultiVariant(WeightedList.of(new Variant(Constants.id("block/incubator"))))).with(horizontalFacing(IncubatorBlock.FACING)));
        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(ModBlocks.PLACEHOLDER.getBlock(), new MultiVariant(WeightedList.of(new Variant(Constants.id("block/placeholder"))))));
        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(ModBlocks.EGG.getBlock(), new MultiVariant(WeightedList.of(new Variant(Constants.id("block/egg"))))));

        this.generateContainment(blockModels, itemModels);
        itemModels.generateFlatItem(ModItems.FIELD_GUIDE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.STONE_CHISEL.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModels.generateFlatItem(ModItems.COPPER_CHISEL.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModels.generateFlatItem(ModItems.IRON_CHISEL.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModels.generateFlatItem(ModItems.GOLDEN_CHISEL.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModels.generateFlatItem(ModItems.DIAMOND_CHISEL.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModels.generateFlatItem(ModItems.NETHERITE_CHISEL.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFossilPartItem(itemModels);
        itemModels.generateFlatItem(ModItems.ROCK_FRAGMENT.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.DIRT_FRAGMENT.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.EGG_SHELL_FRAGMENT.get(), ModelTemplates.FLAT_ITEM);
        itemModels.itemModelOutput.accept(ModBlocks.FOSSIL_CLEANING_TABLE.getItem(), ItemModelUtils.specialModel(Constants.id("block/fossil_cleaning_table"), new FossilCleaningTableSpecialRenderer.Unbaked()));
        itemModels.itemModelOutput.accept(ModBlocks.FOSSIL_IDENTIFICATION_CHAMBER.getItem(), ItemModelUtils.specialModel(Constants.id("block/fossil_identification_chamber"), new FossilIdentificationChamberSpecialRenderer.Unbaked()));
        itemModels.itemModelOutput.accept(ModBlocks.DNA_EXTRACTOR.getItem(), ItemModelUtils.specialModel(Constants.id("block/dna_extractor"), new DNAExtractorSpecialRenderer.Unbaked()));
        itemModels.itemModelOutput.accept(ModBlocks.GENOME_SEQUENCER.getItem(), ItemModelUtils.specialModel(Constants.id("block/genome_sequencer"), new GenomeSequencerSpecialRenderer.Unbaked()));
        itemModels.itemModelOutput.accept(ModBlocks.EMBRYOGENESIS_CHAMBER.getItem(), ItemModelUtils.specialModel(Constants.id("block/embryogenesis_chamber"), new EmbryogenesisChamberSpecialRenderer.Unbaked()));
        itemModels.itemModelOutput.accept(ModBlocks.INCUBATOR.getItem(), ItemModelUtils.specialModel(Constants.id("block/incubator"), new IncubatorSpecialRenderer.Unbaked()));
        this.generateDNASampleItem(itemModels);
        itemModels.generateFlatItem(ModItems.EXTRACTION_FLUID.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.SAMPLE_VIAL.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.GENOME_CARTRIDGE_BLANK.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.GENOME_CARTRIDGE_FILLED.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.GENOME_CARTRIDGE_COMPLETED.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.ARTIFICIAL_EGG.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.NUTRIENT_SOLUTION.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.FERTILIZED_ANCIENT_EGG.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.BABY_CREATURE_CAPSULE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.RAW_PREHISTORIC_MEAT.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.COOKED_PREHISTORIC_MEAT.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.THICK_HIDE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.WOOLLY_FUR.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.PREDATOR_TOOTH.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.SICKLE_CLAW.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.MEGALODON_TOOTH.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.PREHISTORIC_HORN.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.OSTEODERM.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.MAMMOTH_TUSK.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.PREHISTORIC_FEATHER.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.ARTHROPLEURA_CHITIN.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.HORN_WHISTLE.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModels.generateFlatItem(ModItems.SPEED_UPGRADE_MODULE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.PRECISION_UPGRADE_MODULE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.EFFICIENCY_UPGRADE_MODULE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.CREATURE_SADDLE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.IRON_CREATURE_ARMOR.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.GOLDEN_CREATURE_ARMOR.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.DIAMOND_CREATURE_ARMOR.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.NETHERITE_CREATURE_ARMOR.get(), ModelTemplates.FLAT_ITEM);
        itemModels.itemModelOutput.accept(ModBlocks.EGG.getItem(), ItemModelUtils.plainModel(Constants.id("block/egg")));
    }

    private static MultiVariant plain(Identifier model) {
        return new MultiVariant(WeightedList.of(new Variant(model)));
    }

    private void generateContainment(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        TextureMapping steel = TextureMapping.defaultTexture(new Material(Constants.id("block/reinforced_fence")));
        Block fence = ModBlocks.REINFORCED_FENCE.getBlock();
        MultiVariant post = plain(ModelTemplates.FENCE_POST.create(fence, steel, blockModels.modelOutput));
        MultiVariant side = plain(ModelTemplates.FENCE_SIDE.create(fence, steel, blockModels.modelOutput));
        blockModels.blockStateOutput.accept(MultiPartGenerator.multiPart(fence)
            .with(post)
            .with(new ConditionBuilder().term(BlockStateProperties.NORTH, true), side.with(BlockModelGenerators.UV_LOCK))
            .with(new ConditionBuilder().term(BlockStateProperties.EAST, true), side.with(BlockModelGenerators.Y_ROT_90).with(BlockModelGenerators.UV_LOCK))
            .with(new ConditionBuilder().term(BlockStateProperties.SOUTH, true), side.with(BlockModelGenerators.Y_ROT_180).with(BlockModelGenerators.UV_LOCK))
            .with(new ConditionBuilder().term(BlockStateProperties.WEST, true), side.with(BlockModelGenerators.Y_ROT_270).with(BlockModelGenerators.UV_LOCK)));
        itemModels.itemModelOutput.accept(fence.asItem(), ItemModelUtils.plainModel(ModelTemplates.FENCE_INVENTORY.create(fence, steel, blockModels.modelOutput)));

        Block gate = ModBlocks.REINFORCED_FENCE_GATE.getBlock();
        MultiVariant open = plain(ModelTemplates.FENCE_GATE_OPEN.create(gate, steel, blockModels.modelOutput));
        MultiVariant closed = plain(ModelTemplates.FENCE_GATE_CLOSED.create(gate, steel, blockModels.modelOutput));
        MultiVariant openWall = plain(ModelTemplates.FENCE_GATE_WALL_OPEN.create(gate, steel, blockModels.modelOutput));
        MultiVariant closedWall = plain(ModelTemplates.FENCE_GATE_WALL_CLOSED.create(gate, steel, blockModels.modelOutput));
        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(gate)
            .with(PropertyDispatch.initial(BlockStateProperties.IN_WALL, BlockStateProperties.OPEN)
                .select(false, false, closed).select(true, false, closedWall).select(false, true, open).select(true, true, openWall))
            .with(BlockModelGenerators.UV_LOCK)
            .with(PropertyDispatch.modify(BlockStateProperties.HORIZONTAL_FACING)
                .select(Direction.SOUTH, BlockModelGenerators.NOP).select(Direction.WEST, BlockModelGenerators.Y_ROT_90)
                .select(Direction.NORTH, BlockModelGenerators.Y_ROT_180).select(Direction.EAST, BlockModelGenerators.Y_ROT_270)));
        itemModels.itemModelOutput.accept(gate.asItem(), ItemModelUtils.plainModel(Constants.id("block/reinforced_fence_gate")));

        // The electric fence uses hand-made models; its wires glow and change texture while charged.
        Block electric = ModBlocks.ELECTRIC_FENCE.getBlock();
        MultiVariant ePost = plain(Constants.id("block/electric_fence_post"));
        MultiVariant eSide = plain(Constants.id("block/electric_fence_side"));
        MultiVariant eSideCharged = plain(Constants.id("block/electric_fence_side_charged"));
        MultiPartGenerator electricParts = MultiPartGenerator.multiPart(electric).with(ePost);
        BooleanProperty[] sides = {BlockStateProperties.NORTH, BlockStateProperties.EAST, BlockStateProperties.SOUTH, BlockStateProperties.WEST};
        VariantMutator[] turns = {BlockModelGenerators.NOP, BlockModelGenerators.Y_ROT_90, BlockModelGenerators.Y_ROT_180, BlockModelGenerators.Y_ROT_270};
        for (int i = 0; i < 4; i++) {
            electricParts.with(new ConditionBuilder().term(sides[i], true).term(BlockStateProperties.POWER, 0), eSide.with(turns[i]));
            electricParts.with(new ConditionBuilder().term(sides[i], true).term(BlockStateProperties.POWER, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15), eSideCharged.with(turns[i]));
        }
        blockModels.blockStateOutput.accept(electricParts);
        itemModels.itemModelOutput.accept(electric.asItem(), ItemModelUtils.plainModel(Constants.id("block/electric_fence_inventory")));
    }

    /** The sieve frame always shows; the load on its mesh shrinks with each sift. */
    private void generateSifter(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        Block sifter = ModBlocks.SIFTER.getBlock();
        Identifier frame = Constants.id("block/sifter");
        TextureSlot fill = TextureSlot.create("fill");
        MultiPartGenerator parts = MultiPartGenerator.multiPart(sifter).with(plain(frame));
        for (SifterBlock.Material material : SifterBlock.Material.values()) {
            if (material == SifterBlock.Material.EMPTY) {
                continue;
            }
            Material texture = new Material(material.block().builtInRegistryHolder().key().identifier().withPrefix("block/"));
            for (int stage = 0; stage < SifterBlock.SIFTS; stage++) {
                ModelTemplate template = new ModelTemplate(Optional.of(Constants.id("block/sifter_fill_" + stage)), Optional.empty(), fill);
                Identifier model = template.create(Constants.id("block/sifter_fill_" + stage + "_" + material.getSerializedName()), new TextureMapping().put(fill, texture), blockModels.modelOutput);
                parts.with(new ConditionBuilder().term(SifterBlock.CONTENT, material).term(SifterBlock.STAGE, stage), plain(model));
            }
        }
        blockModels.blockStateOutput.accept(parts);
        itemModels.itemModelOutput.accept(sifter.asItem(), ItemModelUtils.plainModel(frame));
    }

    private void generateRockPileBlockState(BlockModelGenerators blockModels){
        Identifier base = Constants.id("block/rock_pile");
        Identifier fossil = Constants.id("block/rock_pile_with_fossil_fragments");
        Identifier egg = Constants.id("block/rock_pile_with_egg_fossil");
        Identifier eggFossil = Constants.id("block/rock_pile_with_fossil_fragments_and_egg");

        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(ModBlocks.ROCK_PILE.getBlock())
            .with(PropertyDispatch.initial(RockPileBlock.HAS_EGG, RockPileBlock.HAS_FOSSIL)
                .select(false, false, new MultiVariant(WeightedList.of(new Variant(base))))
                .select(false, true, new MultiVariant(WeightedList.of(new Variant(fossil))))
                .select(true, false, new MultiVariant(WeightedList.of(new Variant(egg))))
                .select(true, true, new MultiVariant(WeightedList.of(new Variant(eggFossil))))
            )
            .with(ROTATION_HORIZONTAL_FACING)
        );
    }

    private void generateFossilPartItem(ItemModelGenerators itemModels){
        for (ResourceKey<FossilPart> value : this.registries.lookupOrThrow(ModRegistries.FOSSIL_PART).listElementIds().toList()) {
            String name = value.identifier().getPath();
            Identifier dirtyLoc = Constants.id("item/fossil_part/dirty_" + name);
            Identifier normalLoc = Constants.id("item/fossil_part/" + name);
            ModelTemplates.FLAT_ITEM.create(dirtyLoc, TextureMapping.layer0(new Material(dirtyLoc)), itemModels.modelOutput);
            ModelTemplates.FLAT_ITEM.create(normalLoc, TextureMapping.layer0(new Material(normalLoc)), itemModels.modelOutput);
        }
    }

    private void generateDNASampleItem(ItemModelGenerators itemModels){
        List<SelectItemModel.SwitchCase<DNAIntegrityLevel>> cases = new ArrayList<>();

        for (DNAIntegrityLevel value : DNAIntegrityLevel.values()) {
            String name = value.getSerializedName();
            Identifier loc = Constants.id("item/dna_sample_" + name);
            Identifier id = ModelTemplates.FLAT_ITEM.create(loc, TextureMapping.layer0(new Material(loc)), itemModels.modelOutput);
            cases.add(ItemModelUtils.when(value, ItemModelUtils.plainModel(id)));
        }

        itemModels.itemModelOutput.accept(ModItems.DNA_SAMPLE.get(), ItemModelUtils.select(new DNAIntegritySelect(), cases));
    }

    private PropertyDispatch<VariantMutator> horizontalFacing(EnumProperty<Direction> property){
        return PropertyDispatch.modify(property)
            .select(Direction.EAST, BlockModelGenerators.Y_ROT_90)
            .select(Direction.SOUTH, BlockModelGenerators.Y_ROT_180)
            .select(Direction.WEST, BlockModelGenerators.Y_ROT_270)
            .select(Direction.NORTH, BlockModelGenerators.NOP);
    }

    @Override
    protected Stream<? extends Holder<Item>> getKnownItems() {
        return super.getKnownItems().filter(h -> !h.is(ModItems.FOSSIL_PART.key()));
    }
}
