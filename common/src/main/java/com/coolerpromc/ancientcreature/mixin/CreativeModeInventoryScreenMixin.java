package com.coolerpromc.ancientcreature.mixin;

import com.coolerpromc.ancientcreature.client.gui.renderer.BannerRenderer;
import com.coolerpromc.ancientcreature.creativetab.ModCreativeTabs;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.CreativeModeTab;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CreativeModeInventoryScreen.class)
public abstract class CreativeModeInventoryScreenMixin extends AbstractContainerScreen<CreativeModeInventoryScreen.ItemPickerMenu> {

    @Shadow
    private static CreativeModeTab selectedTab;

    public CreativeModeInventoryScreenMixin(CreativeModeInventoryScreen.ItemPickerMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @WrapOperation(method = "extractRenderState", at = @At(target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIF)V", value = "INVOKE"))
    private void renderBanners(CreativeModeInventoryScreen instance, GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, Operation<Void> original) {
        if (selectedTab == ModCreativeTabs.TAB.get()) {
            this.extractContents(graphics, mouseX, mouseY, a);
            BannerRenderer.render((CreativeModeInventoryScreen)(Object)this, graphics);
            this.extractCarriedItem(graphics, mouseX, mouseY);
            this.extractSnapbackItem(graphics);
            this.extractTooltip(graphics, mouseX, mouseY);
        }
        else{
            original.call(instance, graphics, mouseX, mouseY, a);
        }
    }
}