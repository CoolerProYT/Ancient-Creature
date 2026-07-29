package com.coolerpromc.ancientcreature.client.block.state;

import com.coolerpromc.ancientcreature.client.entity.state.FossilCleaningTableRenderState;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.Direction;

public class FossilCleaningBlockEntityRenderState extends BlockEntityRenderState {
    public boolean isBrushing;
    public Direction facing;
    public FossilCleaningTableRenderState entityRenderState;
}
