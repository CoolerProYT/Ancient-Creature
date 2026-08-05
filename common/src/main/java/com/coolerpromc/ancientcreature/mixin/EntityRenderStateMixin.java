package com.coolerpromc.ancientcreature.mixin;

import com.coolerpromc.ancientcreature.client.entity.state.IdentifiedSpeciesRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(EntityRenderState.class)
public abstract class EntityRenderStateMixin implements IdentifiedSpeciesRenderState {
    @Unique
    private boolean identified = true;

    @Override
    public boolean isIdentified() {
        return this.identified;
    }

    @Override
    public void setIdentified(boolean identified) {
        this.identified = identified;
    }
}