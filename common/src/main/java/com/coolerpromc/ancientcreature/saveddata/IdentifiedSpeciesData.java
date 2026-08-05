package com.coolerpromc.ancientcreature.saveddata;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.entity.Species;
import com.coolerpromc.ancientcreature.network.ClientboundIdentifiedSpeciesSyncPacket;
import com.coolerpromc.ancientcreature.platform.Services;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.ArrayList;
import java.util.List;

public class IdentifiedSpeciesData extends SavedData {
    public static final Codec<IdentifiedSpeciesData> CODEC = RecordCodecBuilder.create(i -> i.group(
        Codec.list(Species.CODEC).fieldOf("identifiedSpecies").forGetter(IdentifiedSpeciesData::getIdentifiedSpecies)
    ).apply(i, IdentifiedSpeciesData::new));

    public static final SavedDataType<IdentifiedSpeciesData> TYPE = new SavedDataType<>(Constants.id("identified_species"), IdentifiedSpeciesData::new, CODEC, null);

    public static final IdentifiedSpeciesData CLIENT_CACHE = new IdentifiedSpeciesData();

    private List<Species> identifiedSpecies = new ArrayList<>();

    public IdentifiedSpeciesData(){}
    public IdentifiedSpeciesData(List<Species> identifiedSpecies){
        this.identifiedSpecies = new ArrayList<>(identifiedSpecies);
    }

    public List<Species> getIdentifiedSpecies() {
        return identifiedSpecies;
    }

    public void addIdentifiedSpecies(Species species, ServerLevel level){
        this.identifiedSpecies.add(species);
        setDirty();
        level.getServer().getPlayerList().getPlayers().forEach(p -> Services.NETWORK.sendToPlayer(p, new ClientboundIdentifiedSpeciesSyncPacket(this.identifiedSpecies)));
    }

    public static void setClientCache(List<Species> identifiedSpecies){
        CLIENT_CACHE.identifiedSpecies = new ArrayList<>(identifiedSpecies);
    }

    public static IdentifiedSpeciesData getIdentifiedSpeciesData(MinecraftServer server){
        ServerLevel level = server.getLevel(ServerLevel.OVERWORLD);

        if (level == null){
            return new IdentifiedSpeciesData();
        }

        return level.getDataStorage().computeIfAbsent(TYPE);
    }
}
