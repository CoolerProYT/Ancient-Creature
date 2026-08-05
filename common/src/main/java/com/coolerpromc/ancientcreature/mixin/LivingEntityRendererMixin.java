package com.coolerpromc.ancientcreature.mixin;

import com.coolerpromc.ancientcreature.client.entity.state.IdentifiedSpeciesRenderState;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin {
    @Inject(method = "getModelTint", at = @At("HEAD"), cancellable = true)
    private void ancientCreature$makeSilhouette(LivingEntityRenderState renderState, CallbackInfoReturnable<Integer> cir) {
        IdentifiedSpeciesRenderState silhouetteState = (IdentifiedSpeciesRenderState) renderState;

        if (!silhouetteState.isIdentified()) {
            cir.setReturnValue(0xFF000000);
        }
    }
}