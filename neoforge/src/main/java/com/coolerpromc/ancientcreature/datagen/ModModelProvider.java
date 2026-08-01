package com.coolerpromc.ancientcreature.datagen;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.block.ModBlocks;
import com.coolerpromc.ancientcreature.block.custom.DNAExtractorBlock;
import com.coolerpromc.ancientcreature.block.custom.FossilCleaningTableBlock;
import com.coolerpromc.ancientcreature.block.custom.FossilIdentificationChamberBlock;
import com.coolerpromc.ancientcreature.block.custom.EmbryogenesisChamberBlock;
import com.coolerpromc.ancientcreature.block.custom.RockPileBlock;
import com.coolerpromc.ancientcreature.client.item.DNAExtractorSpecialRenderer;
import com.coolerpromc.ancientcreature.client.item.FossilCleaningTableSpecialRenderer;
import com.coolerpromc.ancientcreature.client.item.FossilIdentificationChamberSpecialRenderer;
import com.coolerpromc.ancientcreature.client.item.GenomeSequencerSpecialRenderer;
import com.coolerpromc.ancientcreature.client.item.EmbryogenesisChamberSpecialRenderer;
import com.coolerpromc.ancientcreature.client.model.condition.DirtyCondition;
import com.coolerpromc.ancientcreature.client.model.select.DNAIntegritySelect;
import com.coolerpromc.ancientcreature.client.model.select.FossilPartSelect;
import com.coolerpromc.ancientcreature.data.component.custom.DNAIntegrityLevel;
import com.coolerpromc.ancientcreature.data.component.custom.FossilPart;
import com.coolerpromc.ancientcreature.item.ModItems;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.MultiVariant;
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
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.block.state.properties.EnumProperty;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.client.data.models.BlockModelGenerators.ROTATION_HORIZONTAL_FACING;

public class ModModelProvider extends ModelProvider {
    public ModModelProvider(PackOutput output) {
        super(output, Constants.MODID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        blockModels.createTrivialCube(ModBlocks.FOSSIL_ORE.getBlock());
        this.generateRockPileBlockState(blockModels);
        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(ModBlocks.FOSSIL_CLEANING_TABLE.getBlock(), new MultiVariant(WeightedList.of(new Variant(Constants.id("block/fossil_cleaning_table"))))).with(horizontalFacing(FossilCleaningTableBlock.FACING)));
        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(ModBlocks.FOSSIL_IDENTIFICATION_CHAMBER.getBlock(), new MultiVariant(WeightedList.of(new Variant(Constants.id("block/fossil_identification_chamber"))))).with(horizontalFacing(FossilIdentificationChamberBlock.FACING)));
        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(ModBlocks.DNA_EXTRACTOR.getBlock(), new MultiVariant(WeightedList.of(new Variant(Constants.id("block/dna_extractor"))))).with(horizontalFacing(DNAExtractorBlock.FACING)));
        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(ModBlocks.GENOME_SEQUENCER.getBlock(), new MultiVariant(WeightedList.of(new Variant(Constants.id("block/genome_sequencer"))))).with(horizontalFacing(DNAExtractorBlock.FACING)));
        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(ModBlocks.EMBRYOGENESIS_CHAMBER.getBlock(), new MultiVariant(WeightedList.of(new Variant(Constants.id("block/embryogenesis_chamber"))))).with(horizontalFacing(EmbryogenesisChamberBlock.FACING)));
        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(ModBlocks.PLACEHOLDER.getBlock(), new MultiVariant(WeightedList.of(new Variant(Constants.id("block/placeholder"))))));

        itemModels.generateFlatItem(ModItems.STONE_CHISEL.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModels.generateFlatItem(ModItems.COPPER_CHISEL.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModels.generateFlatItem(ModItems.IRON_CHISEL.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModels.generateFlatItem(ModItems.GOLDEN_CHISEL.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModels.generateFlatItem(ModItems.DIAMOND_CHISEL.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModels.generateFlatItem(ModItems.NETHERITE_CHISEL.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFossilFragmentItem(itemModels);
        this.generateEggFossilItem(itemModels);
        itemModels.generateFlatItem(ModItems.ROCK_FRAGMENT.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.DIRT_FRAGMENT.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.EGG_SHELL_FRAGMENT.get(), ModelTemplates.FLAT_ITEM);
        itemModels.itemModelOutput.accept(ModBlocks.FOSSIL_CLEANING_TABLE.getItem(), ItemModelUtils.specialModel(Constants.id("block/fossil_cleaning_table"), new FossilCleaningTableSpecialRenderer.Unbaked()));
        itemModels.itemModelOutput.accept(ModBlocks.FOSSIL_IDENTIFICATION_CHAMBER.getItem(), ItemModelUtils.specialModel(Constants.id("block/fossil_identification_chamber"), new FossilIdentificationChamberSpecialRenderer.Unbaked()));
        itemModels.itemModelOutput.accept(ModBlocks.DNA_EXTRACTOR.getItem(), ItemModelUtils.specialModel(Constants.id("block/dna_extractor"), new DNAExtractorSpecialRenderer.Unbaked()));
        itemModels.itemModelOutput.accept(ModBlocks.GENOME_SEQUENCER.getItem(), ItemModelUtils.specialModel(Constants.id("block/genome_sequencer"), new GenomeSequencerSpecialRenderer.Unbaked()));
        itemModels.itemModelOutput.accept(ModBlocks.EMBRYOGENESIS_CHAMBER.getItem(), ItemModelUtils.specialModel(Constants.id("block/embryogenesis_chamber"), new EmbryogenesisChamberSpecialRenderer.Unbaked()));
        this.generateDNASampleItem(itemModels);
        itemModels.generateFlatItem(ModItems.EXTRACTION_FLUID.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.SAMPLE_VIAL.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.GENOME_CARTRIDGE_BLANK.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.GENOME_CARTRIDGE_FILLED.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.GENOME_CARTRIDGE_COMPLETED.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.ARTIFICIAL_EGG.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.NUTRIENT_SOLUTION.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.FERTILIZED_ANCIENT_EGG.get(), ModelTemplates.FLAT_ITEM);
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

    private void generateFossilFragmentItem(ItemModelGenerators itemModels){
        List<SelectItemModel.SwitchCase<FossilPart>> dirties = new ArrayList<>();
        List<SelectItemModel.SwitchCase<FossilPart>> normals = new ArrayList<>();

        for (FossilPart value : FossilPart.values()) {
            String name = value.getSerializedName();
            Identifier dirtyLoc = Constants.id("item/fossil_fragment/dirty_" + name);
            Identifier normalLoc = Constants.id("item/fossil_fragment/" + name);
            Identifier dirty = ModelTemplates.FLAT_ITEM.create(dirtyLoc, TextureMapping.layer0(new Material(dirtyLoc)), itemModels.modelOutput);
            Identifier normal = ModelTemplates.FLAT_ITEM.create(normalLoc, TextureMapping.layer0(new Material(normalLoc)), itemModels.modelOutput);
            dirties.add(ItemModelUtils.when(value, ItemModelUtils.plainModel(dirty)));
            normals.add(ItemModelUtils.when(value, ItemModelUtils.plainModel(normal)));
        }

        itemModels.itemModelOutput.accept(ModItems.FOSSIL_FRAGMENT.get(), ItemModelUtils.conditional(
            new DirtyCondition(),
            ItemModelUtils.select(new FossilPartSelect(), dirties),
            ItemModelUtils.select(new FossilPartSelect(), normals))
        );
    }

    private void generateEggFossilItem(ItemModelGenerators itemModels){
        Identifier dirtyLoc = Constants.id("item/dirty_egg_fossil");
        Identifier normalLoc = Constants.id("item/egg_fossil");

        Identifier dirty = ModelTemplates.FLAT_ITEM.create(dirtyLoc, TextureMapping.layer0(new Material(dirtyLoc)), itemModels.modelOutput);
        Identifier normal = ModelTemplates.FLAT_ITEM.create(normalLoc, TextureMapping.layer0(new Material(normalLoc)), itemModels.modelOutput);

        itemModels.itemModelOutput.accept(ModItems.EGG_FOSSIL.get(), ItemModelUtils.conditional(new DirtyCondition(), ItemModelUtils.plainModel(dirty), ItemModelUtils.plainModel(normal)));
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
}
