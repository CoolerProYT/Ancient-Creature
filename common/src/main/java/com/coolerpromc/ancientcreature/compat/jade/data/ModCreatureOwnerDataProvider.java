package com.coolerpromc.ancientcreature.compat.jade.data;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.compat.jade.ModJadePlugin;
import com.coolerpromc.ancientcreature.entity.custom.OwnedAncientCreature;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.StreamServerDataProvider;

public class ModCreatureOwnerDataProvider implements StreamServerDataProvider<EntityAccessor, String> {
    public static final ModCreatureOwnerDataProvider INSTANCE = new ModCreatureOwnerDataProvider();

    @Override
    public Identifier getUid() {
        return ModJadePlugin.OWNER_DATA;
    }

    @Override
    public @Nullable String streamData(EntityAccessor entityAccessor) {
        Level level = entityAccessor.getLevel();
        if (entityAccessor.getEntity() instanceof OwnedAncientCreature creature && level.getPlayerByUUID(creature.getUUID()) instanceof Player player){
            return player.getDisplayName().getString();
        }
        return null;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, String> streamCodec() {
        return ByteBufCodecs.STRING_UTF8.cast();
    }
}
