package com.coolerpromc.ancientcreature.mixin;

import com.coolerpromc.ancientcreature.creativetab.ModCreativeTabs;
import com.coolerpromc.ancientcreature.creativetab.TabLayout;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

@Mixin(CreativeModeTab.class)
public class CreativeModeTabMixin {
    @Inject(method = "buildContents", at = @At("RETURN"))
    private void buildContents(CreativeModeTab.ItemDisplayParameters parameters, CallbackInfo ci) {
        CreativeModeTab self = (CreativeModeTab)(Object) this;
        if (self != ModCreativeTabs.TAB.get()) {
            return;
        }

        List<ItemStack> display = new ArrayList<>(TabLayout.CACHED_ITEMS);
        self.displayItems = display;
        self.displayItemsSearchTab = display.stream().filter(s -> !s.isEmpty()).collect(java.util.stream.Collectors.toCollection(LinkedHashSet::new));
    }
}