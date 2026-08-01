package com.coolerpromc.ancientcreature.client.block.state;

import com.coolerpromc.ancientcreature.client.entity.state.block.DNAExtractorRenderState;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.Direction;

public class DNAExtractorBlockEntityRenderState extends BlockEntityRenderState {
    public boolean isExtracting;
    public Direction facing;
    public DNAExtractorRenderState entityRenderState;
}
