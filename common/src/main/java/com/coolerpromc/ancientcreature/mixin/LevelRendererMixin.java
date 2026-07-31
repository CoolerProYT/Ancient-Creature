package com.coolerpromc.ancientcreature.mixin;

import com.coolerpromc.ancientcreature.block.entity.custom.PlaceholderBlockEntity;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
    @Shadow
    private @Nullable ClientLevel level;

    @ModifyVariable(method = "destroyBlockProgress", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private BlockPos ancientCreature$redirectPlaceholderBreakProgress(BlockPos pos) {
        if (level != null && level.getBlockEntity(pos) instanceof PlaceholderBlockEntity placeholder) {
            return placeholder.getActualPos();
        }
        return pos;
    }
}
