package com.coolerpromc.ancientcreature.compat.jade.component;

import com.coolerpromc.ancientcreature.compat.jade.ModJadePlugin;
import com.coolerpromc.ancientcreature.compat.jade.data.ModCreatureOwnerDataProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

import java.util.Optional;

public class ModCreatureOwnerComponentProvider implements IEntityComponentProvider {
    public static final ModCreatureOwnerComponentProvider INSTANCE = new ModCreatureOwnerComponentProvider();

    @Override
    public void appendTooltip(ITooltip iTooltip, EntityAccessor entityAccessor, IPluginConfig iPluginConfig) {
        Optional<String> owner = ModCreatureOwnerDataProvider.INSTANCE.decodeFromData(entityAccessor);
        owner.ifPresent(s -> iTooltip.add(Component.translatable("jade.ancientcreature.owner_name", s)));
    }

    @Override
    public Identifier getUid() {
        return ModJadePlugin.OWNER_DATA;
    }
}
