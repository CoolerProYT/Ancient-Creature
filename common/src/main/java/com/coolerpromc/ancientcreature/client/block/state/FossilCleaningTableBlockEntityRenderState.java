package com.coolerpromc.ancientcreature.client.block.state;

import com.coolerpromc.ancientcreature.client.entity.state.block.FossilCleaningTableRenderState;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.Direction;

public class FossilCleaningTableBlockEntityRenderState extends BlockEntityRenderState {
    public boolean isBrushing;
    public Direction facing;
    public FossilCleaningTableRenderState entityRenderState;
}
