package com.coolerpromc.ancientcreature;

import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Constants {
	public static final String MODID = "ancientcreature";
	public static final String MOD_NAME = "Ancient Creature";
	public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);

	public static final int LAST_INVENTORY_SLOT_INDEX = 35;

	public static Identifier id(String path){
		return Identifier.fromNamespaceAndPath(MODID, path);
	}
}