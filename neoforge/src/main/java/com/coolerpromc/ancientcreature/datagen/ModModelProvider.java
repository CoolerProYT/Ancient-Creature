package com.coolerpromc.ancientcreature.datagen;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.block.ModBlocks;
import com.coolerpromc.ancientcreature.block.custom.RockPileBlock;
import com.coolerpromc.ancientcreature.item.ModItems;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.renderer.block.dispatch.Variant;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.util.random.WeightedList;

import static net.minecraft.client.data.models.BlockModelGenerators.ROTATION_HORIZONTAL_FACING;

public class ModModelProvider extends ModelProvider {
    public ModModelProvider(PackOutput output) {
        super(output, Constants.MODID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        blockModels.createTrivialCube(ModBlocks.FOSSIL_ORE.get());
        this.generateRockPileBlockState(blockModels);

        itemModels.generateFlatItem(ModItems.CHISEL.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModels.generateFlatItem(ModItems.RIB_FOSSIL_FRAGMENT.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.EGG_FOSSIL.get(), ModelTemplates.FLAT_ITEM);
    }

    private void generateRockPileBlockState(BlockModelGenerators blockModels){
        Identifier base = Constants.id("block/rock_pile");
        Identifier egg = Constants.id("block/rock_pile_with_egg_fossil");

        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(ModBlocks.ROCK_PILE.get())
            .with(PropertyDispatch.initial(RockPileBlock.HAS_EGG)
                .select(false, new MultiVariant(WeightedList.of(new Variant(base))))
                .select(true, new MultiVariant(WeightedList.of(new Variant(egg))))
            )
            .with(ROTATION_HORIZONTAL_FACING)
        );
    }
}
