package com.coolerpromc.ancientcreature.mixin;

import com.coolerpromc.ancientcreature.client.gui.renderer.BannerRenderer;
import com.coolerpromc.ancientcreature.creativetab.ModCreativeTabs;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.item.CreativeModeTab;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreativeModeInventoryScreen.class)
public class CreativeModeInventoryScreenMixin {

    @Shadow
    private static CreativeModeTab selectedTab;

    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void renderBanners(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (selectedTab == ModCreativeTabs.TAB.get()) {
            BannerRenderer.render((CreativeModeInventoryScreen)(Object)this, graphics);
        }
    }
}