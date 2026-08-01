package com.coolerpromc.ancientcreature.client.block.state;

import com.coolerpromc.ancientcreature.client.entity.state.block.IncubatorRenderState;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.Direction;

public class IncubatorBlockEntityRenderState extends BlockEntityRenderState {
    public boolean isProcessing;
    public Direction facing;
    public IncubatorRenderState entityRenderState;
}
