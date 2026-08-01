package com.coolerpromc.ancientcreature.client.block.state;

import com.coolerpromc.ancientcreature.client.entity.state.block.GenomeSequencerRenderState;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.Direction;

public class GenomeSequencerBlockEntityRenderState extends BlockEntityRenderState {
    public boolean isProcessing;
    public Direction facing;
    public GenomeSequencerRenderState entityRenderState;
}
