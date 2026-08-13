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

    private SpeciesJournalKeyHandler() {
    }

    public static void clientTick(Minecraft minecraft) {
        while (OPEN_JOURNAL.consumeClick()) {
            if (minecraft.level != null && minecraft.player != null && minecraft.screen == null) {
                minecraft.setScreen(new SpeciesJournalScreen());
            }
        }
    }
}
