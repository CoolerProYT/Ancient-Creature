package com.coolerpromc.ancientcreature.client.block.state;

import com.coolerpromc.ancientcreature.client.entity.state.block.FossilIdentificationChamberRenderState;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.Direction;

public class FossilIdentificationChamberBlockEntityRenderState extends BlockEntityRenderState {
    public boolean isIdentifying;
    public Direction facing;
    public FossilIdentificationChamberRenderState entityRenderState;
}
