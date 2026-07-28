package com.coolerpromc.ancientcreature.item;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.item.custom.ChiselItem;
import com.coolerpromc.ancientcreature.item.custom.EggFossilItem;
import com.coolerpromc.ancientcreature.platform.Services;
import com.coolerpromc.ancientcreature.platform.util.RegistryHandler;
import com.coolerpromc.ancientcreature.tag.ModBlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;

import java.util.function.Function;

public class ModItems {
    public static final RegistryHandler.Items<ChiselItem> CHISEL = registerItem("chisel", p -> new ChiselItem(chisel(p, ToolMaterial.IRON, -2, -1).repairable(ItemTags.IRON_TOOL_MATERIALS)));
    public static final RegistryHandler.Items<Item> RIB_FOSSIL_FRAGMENT = registerItem("rib_fossil_fragment", Item::new);
    public static final RegistryHandler.Items<EggFossilItem> EGG_FOSSIL = registerItem("egg_fossil", EggFossilItem::new);

    public static <T extends Item> RegistryHandler.Items<T> registerItem(String name, Function<Item.Properties, T> func){
        return Services.REGISTRY.registerItem(name, func);
    }

    public static Item.Properties chisel(Item.Properties properties, ToolMaterial material, float attackDamageBaseline, float attackSpeedBaseline){
        return properties.tool(material, ModBlockTags.MINEABLE_WITH_CHISEL, attackDamageBaseline, attackSpeedBaseline, 0);
    }

    public static void init(){
        Constants.LOG.info("Registering items.");
    }
}
