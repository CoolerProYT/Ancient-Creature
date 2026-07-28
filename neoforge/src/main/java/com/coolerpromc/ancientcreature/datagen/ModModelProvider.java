package com.coolerpromc.ancientcreature.datagen;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.block.ModBlocks;
import com.coolerpromc.ancientcreature.block.custom.RockPileBlock;
import com.coolerpromc.ancientcreature.client.model.condition.DirtyFossilFragmentCondition;
import com.coolerpromc.ancientcreature.client.model.select.FossilPartSelect;
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
import net.minecraft.client.renderer.item.SelectItemModel;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.util.random.WeightedList;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.client.data.models.BlockModelGenerators.ROTATION_HORIZONTAL_FACING;

public class ModModelProvider extends ModelProvider {
    public ModModelProvider(PackOutput output) {
        super(output, Constants.MODID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        blockModels.createTrivialCube(ModBlocks.FOSSIL_ORE.get());
        this.generateRockPileBlockState(blockModels);

        itemModels.generateFlatItem(ModItems.STONE_CHISEL.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModels.generateFlatItem(ModItems.COPPER_CHISEL.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModels.generateFlatItem(ModItems.IRON_CHISEL.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModels.generateFlatItem(ModItems.GOLDEN_CHISEL.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModels.generateFlatItem(ModItems.DIAMOND_CHISEL.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModels.generateFlatItem(ModItems.NETHERITE_CHISEL.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateRibFossilFragmentItem(itemModels);
        itemModels.generateFlatItem(ModItems.EGG_FOSSIL.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.ROCK_FRAGMENT.get(), ModelTemplates.FLAT_ITEM);
    }

    private void generateRockPileBlockState(BlockModelGenerators blockModels){
        Identifier base = Constants.id("block/rock_pile");
        Identifier fossil = Constants.id("block/rock_pile_with_fossil_fragments");
        Identifier egg = Constants.id("block/rock_pile_with_egg_fossil");
        Identifier eggFossil = Constants.id("block/rock_pile_with_fossil_fragments_and_egg");

        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(ModBlocks.ROCK_PILE.get())
            .with(PropertyDispatch.initial(RockPileBlock.HAS_EGG, RockPileBlock.HAS_FOSSIL)
                .select(false, false, new MultiVariant(WeightedList.of(new Variant(base))))
                .select(false, true, new MultiVariant(WeightedList.of(new Variant(fossil))))
                .select(true, false, new MultiVariant(WeightedList.of(new Variant(egg))))
                .select(true, true, new MultiVariant(WeightedList.of(new Variant(eggFossil))))
            )
            .with(ROTATION_HORIZONTAL_FACING)
        );
    }

    private void generateRibFossilFragmentItem(ItemModelGenerators itemModels){
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
            new DirtyFossilFragmentCondition(),
            ItemModelUtils.select(new FossilPartSelect(), dirties),
            ItemModelUtils.select(new FossilPartSelect(), normals))
        );
    }
}
