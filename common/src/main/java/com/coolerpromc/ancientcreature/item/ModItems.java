package com.coolerpromc.ancientcreature.item;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.data.component.ModDataComponents;
import com.coolerpromc.ancientcreature.data.component.custom.*;
import com.coolerpromc.ancientcreature.entity.Species;
import com.coolerpromc.ancientcreature.item.custom.BabyCreatureCapsule;
import com.coolerpromc.ancientcreature.item.custom.DNASampleItem;
import com.coolerpromc.ancientcreature.item.custom.EggFossilItem;
import com.coolerpromc.ancientcreature.item.custom.FossilFragmentItem;
import com.coolerpromc.ancientcreature.platform.Services;
import com.coolerpromc.ancientcreature.platform.util.RegistryHandler;
import com.coolerpromc.ancientcreature.tag.ModBlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;

import java.util.function.Function;

public class ModItems {
    public static final RegistryHandler.Items<Item> STONE_CHISEL = registerItem("stone_chisel", p -> new Item(chisel(p, ToolMaterial.STONE, -1, -2, 0.4f).repairable(ItemTags.STONE_TOOL_MATERIALS)));
    public static final RegistryHandler.Items<Item> COPPER_CHISEL = registerItem("copper_chisel", p -> new Item(chisel(p, ToolMaterial.COPPER, -1, -2, 0.35f).repairable(ItemTags.COPPER_TOOL_MATERIALS)));
    public static final RegistryHandler.Items<Item> IRON_CHISEL = registerItem("iron_chisel", p -> new Item(chisel(p, ToolMaterial.IRON, -2, -1, 0.25f).repairable(ItemTags.IRON_TOOL_MATERIALS)));
    public static final RegistryHandler.Items<Item> GOLDEN_CHISEL = registerItem("golden_chisel", p -> new Item(chisel(p, ToolMaterial.GOLD, 0, -3, 0.5f).repairable(ItemTags.GOLD_TOOL_MATERIALS)));
    public static final RegistryHandler.Items<Item> DIAMOND_CHISEL = registerItem("diamond_chisel", p -> new Item(chisel(p, ToolMaterial.DIAMOND, -3, 0, 0.1f).repairable(ItemTags.DIAMOND_TOOL_MATERIALS)));
    public static final RegistryHandler.Items<Item> NETHERITE_CHISEL = registerItem("netherite_chisel", p -> new Item(chisel(p, ToolMaterial.NETHERITE, -4, 0, 0).repairable(ItemTags.NETHERITE_TOOL_MATERIALS)));

    public static final RegistryHandler.Items<FossilFragmentItem> FOSSIL_FRAGMENT = registerItem("fossil_fragment", p -> new FossilFragmentItem(p.stacksTo(1).component(ModDataComponents.IDENTIFICATION_FAILED.get(), false).component(ModDataComponents.SPECIES.get(), Species.TRICERATOPS).component(ModDataComponents.FOSSIL_PART.get(), FossilPart.RIB).component(ModDataComponents.IDENTIFIED.get(), false).component(ModDataComponents.IS_DIRTY.get(), true).component(ModDataComponents.FOSSIL_COMPLETENESS.get(), new FossilCompleteness(0f))));
    public static final RegistryHandler.Items<Item> EGG_SHELL_FRAGMENT = registerItem("egg_shell_fragment", Item::new);
    public static final RegistryHandler.Items<EggFossilItem> EGG_FOSSIL = registerItem("egg_fossil", p -> new EggFossilItem(p.stacksTo(1).component(ModDataComponents.IDENTIFICATION_FAILED.get(), false).component(ModDataComponents.SPECIES.get(), Species.TRICERATOPS).component(ModDataComponents.IDENTIFIED.get(), false).component(ModDataComponents.IS_DIRTY.get(), true).component(ModDataComponents.FOSSIL_COMPLETENESS.get(), new FossilCompleteness(1f))));

    public static final RegistryHandler.Items<Item> ROCK_FRAGMENT = registerItem("rock_fragment", Item::new);
    public static final RegistryHandler.Items<Item> DIRT_FRAGMENT = registerItem("dirt_fragment", Item::new);

    public static final RegistryHandler.Items<DNASampleItem> DNA_SAMPLE = registerItem("dna_sample", p -> new DNASampleItem(p.component(ModDataComponents.IDENTIFIED.get(), true).component(ModDataComponents.DNA_INTEGRITY_LEVEL.get(), DNAIntegrityLevel.PRESERVED_EMBRYO).component(ModDataComponents.SPECIES.get(), Species.TRICERATOPS)));
    public static final RegistryHandler.Items<Item> EXTRACTION_FLUID = registerItem("extraction_fluid", p -> new Item(p.durability(4)));
    public static final RegistryHandler.Items<Item> SAMPLE_VIAL = registerItem("sample_vial", Item::new);

    public static final RegistryHandler.Items<Item> GENOME_CARTRIDGE_BLANK = registerItem("genome_cartridge_blank", p -> new Item(p.stacksTo(1)));
    public static final RegistryHandler.Items<Item> GENOME_CARTRIDGE_FILLED = registerItem("genome_cartridge_filled", p -> new Item(p.stacksTo(1).component(ModDataComponents.IDENTIFIED.get(), true).component(ModDataComponents.SPECIES.get(), Species.TRICERATOPS).component(ModDataComponents.GENOME_COMPLETENESS.get(), new GenomeCompleteness(0f))));
    public static final RegistryHandler.Items<Item> GENOME_CARTRIDGE_COMPLETED = registerItem("genome_cartridge_completed", p -> new Item(p.stacksTo(1).component(ModDataComponents.SPECIES.get(), Species.TRICERATOPS).component(ModDataComponents.IDENTIFIED.get(), true)));

    public static final RegistryHandler.Items<Item> ARTIFICIAL_EGG = registerItem("artificial_egg", p -> new Item(p.stacksTo(16)));
    public static final RegistryHandler.Items<Item> NUTRIENT_SOLUTION = registerItem("nutrient_solution", p -> new Item(p.stacksTo(16)));
    public static final RegistryHandler.Items<Item> FERTILIZED_ANCIENT_EGG = registerItem("fertilized_ancient_egg", p -> new Item(p.stacksTo(1).component(ModDataComponents.SPECIES.get(), Species.TRICERATOPS).component(ModDataComponents.IDENTIFIED.get(), true)));

    public static final RegistryHandler.Items<BabyCreatureCapsule> BABY_CREATURE_CAPSULE = registerItem("baby_creature_capsule", p -> new BabyCreatureCapsule(p.stacksTo(1).component(ModDataComponents.SPECIES.get(), Species.TRICERATOPS).component(ModDataComponents.IDENTIFIED.get(), true)));

    public static <T extends Item> RegistryHandler.Items<T> registerItem(String name, Function<Item.Properties, T> func){
        return Services.REGISTRY.registerItem(name, func);
    }

    public static Item.Properties chisel(Item.Properties properties, ToolMaterial material, float attackDamageBaseline, float attackSpeedBaseline, float damageToFossil){
        return properties.tool(material, ModBlockTags.MINEABLE_WITH_CHISEL, attackDamageBaseline, attackSpeedBaseline, 0).component(ModDataComponents.FOSSIL_DAMAGE_RATE.get(), new FossilDamageRate(damageToFossil)).stacksTo(1);
    }

    public static void init(){
        Constants.LOG.info("Registering items.");
    }
}
