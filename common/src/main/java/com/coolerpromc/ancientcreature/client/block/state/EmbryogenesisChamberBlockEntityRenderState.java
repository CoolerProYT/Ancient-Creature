package com.coolerpromc.ancientcreature.client.block.state;

import com.coolerpromc.ancientcreature.client.entity.state.block.EmbryogenesisChamberRenderState;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.Direction;

public class EmbryogenesisChamberBlockEntityRenderState extends BlockEntityRenderState {
    public boolean isProcessing;
    public Direction facing;
    public EmbryogenesisChamberRenderState entityRenderState;
}
