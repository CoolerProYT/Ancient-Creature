package com.coolerpromc.ancientcreature.client;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.client.gui.screen.SpeciesJournalScreen;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

public final class SpeciesJournalKeyHandler {
    public static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(Constants.id("ancient_creature"));
    public static final KeyMapping OPEN_JOURNAL = new KeyMapping(
        "key.ancientcreature.open_species_journal",
        InputConstants.Type.KEYSYM,
        InputConstants.KEY_J,
        CATEGORY
    );

    /** While riding one of your creatures: use its mount ability. */
    public static final KeyMapping MOUNT_ABILITY = new KeyMapping(
        "key.ancientcreature.mount_ability",
        InputConstants.Type.KEYSYM,
        InputConstants.KEY_R,
        CATEGORY
    );

    private SpeciesJournalKeyHandler() {
    }

    public static void clientTick(Minecraft minecraft) {
        while (MOUNT_ABILITY.consumeClick()) {
            if (minecraft.player != null && minecraft.screen == null
                && minecraft.player.getVehicle() instanceof com.coolerpromc.ancientcreature.entity.custom.AncientCreatureEntity) {
                com.coolerpromc.ancientcreature.platform.Services.NETWORK.sendToServer(com.coolerpromc.ancientcreature.network.ServerboundMountAbilityPacket.INSTANCE);
            }
        }
        while (OPEN_JOURNAL.consumeClick()) {
            if (minecraft.level != null && minecraft.player != null && minecraft.screen == null) {
                minecraft.setScreen(new SpeciesJournalScreen());
            }
        }
    }
}
