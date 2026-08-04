package com.coolerpromc.ancientcreature.client.item.select;

import com.coolerpromc.ancientcreature.data.component.ModDataComponents;
import com.coolerpromc.ancientcreature.data.component.custom.FossilData;
import com.coolerpromc.ancientcreature.item.FossilPart;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public record FossilPartSelect() implements SelectItemModelProperty<Identifier> {
    public static final MapCodec<FossilPartSelect> CODEC = MapCodec.unit(FossilPartSelect::new);
    public static final Type<FossilPartSelect, Identifier> TYPE = Type.create(CODEC, Identifier.CODEC);

    @Override
    public @Nullable Identifier get(ItemStack itemStack, @Nullable ClientLevel level, @Nullable LivingEntity owner, int seed, ItemDisplayContext displayContext) {
        FossilData fossilData = itemStack.get(ModDataComponents.FOSSIL_DATA.get());

        if (fossilData == null) {
            return FossilPart.key("empty").identifier();
        }

        return fossilData.fossilPart()
            .unwrapKey()
            .orElse(FossilPart.key("empty"))
            .identifier();
    }

    @Override
    public Codec<Identifier> valueCodec() {
        return Identifier.CODEC;
    }

    @Override
    public Type<? extends SelectItemModelProperty<Identifier>, Identifier> type() {
        return TYPE;
    }
}
