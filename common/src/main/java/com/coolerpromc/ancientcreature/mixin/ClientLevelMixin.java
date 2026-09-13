package com.coolerpromc.ancientcreature.mixin;

import com.coolerpromc.ancientcreature.block.entity.custom.PlaceholderBlockEntity;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ClientLevel.class)
public class ClientLevelMixin {
    @ModifyVariable(method = "destroyBlockProgress", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private BlockPos ancientCreature$redirectPlaceholderBreakProgress(BlockPos pos) {
        ClientLevel self = (ClientLevel) (Object) this;
        if (self.getBlockEntity(pos) instanceof PlaceholderBlockEntity placeholder) {
            return placeholder.getActualPos();
        }
        return pos;
    }
}
