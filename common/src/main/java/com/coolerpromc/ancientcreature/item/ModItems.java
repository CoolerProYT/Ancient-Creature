package com.coolerpromc.ancientcreature.item;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.data.component.ModDataComponents;
import com.coolerpromc.ancientcreature.data.component.custom.*;
import com.coolerpromc.ancientcreature.entity.Species;
import com.coolerpromc.ancientcreature.item.custom.BabyCreatureCapsule;
import com.coolerpromc.ancientcreature.item.custom.DNASampleItem;
import com.coolerpromc.ancientcreature.item.custom.EggFossilItem;
import com.coolerpromc.ancientcreature.item.custom.FieldGuideItem;
import com.coolerpromc.ancientcreature.item.custom.FossilPartItem;
import com.coolerpromc.ancientcreature.item.custom.HornWhistleItem;
import com.coolerpromc.ancientcreature.item.custom.MachineUpgradeItem;
import com.coolerpromc.ancientcreature.item.custom.CreatureArmorItem;
import com.coolerpromc.ancientcreature.item.custom.CreatureSaddleItem;
import com.coolerpromc.ancientcreature.platform.Services;
import com.coolerpromc.ancientcreature.platform.util.RegistryHandler;
import com.coolerpromc.ancientcreature.tag.ModBlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ToolMaterial;

import java.util.function.Function;

public class ModItems {
    public static final RegistryHandler.Items<FieldGuideItem> FIELD_GUIDE = registerItem("field_guide", p -> new FieldGuideItem(p.stacksTo(1)));

    public static final RegistryHandler.Items<Item> STONE_CHISEL = registerItem("stone_chisel", p -> new Item(chisel(p, ToolMaterial.STONE, -1, -2, 0.4f).repairable(ItemTags.STONE_TOOL_MATERIALS)));
    public static final RegistryHandler.Items<Item> COPPER_CHISEL = registerItem("copper_chisel", p -> new Item(chisel(p, ToolMaterial.COPPER, -1, -2, 0.35f).repairable(ItemTags.COPPER_TOOL_MATERIALS)));
    public static final RegistryHandler.Items<Item> IRON_CHISEL = registerItem("iron_chisel", p -> new Item(chisel(p, ToolMaterial.IRON, -2, -1, 0.25f).repairable(ItemTags.IRON_TOOL_MATERIALS)));
    public static final RegistryHandler.Items<Item> GOLDEN_CHISEL = registerItem("golden_chisel", p -> new Item(chisel(p, ToolMaterial.GOLD, 0, -3, 0.5f).repairable(ItemTags.GOLD_TOOL_MATERIALS)));
    public static final RegistryHandler.Items<Item> DIAMOND_CHISEL = registerItem("diamond_chisel", p -> new Item(chisel(p, ToolMaterial.DIAMOND, -3, 0, 0.1f).repairable(ItemTags.DIAMOND_TOOL_MATERIALS)));
    public static final RegistryHandler.Items<Item> NETHERITE_CHISEL = registerItem("netherite_chisel", p -> new Item(chisel(p, ToolMaterial.NETHERITE, -4, 0, 0).repairable(ItemTags.NETHERITE_TOOL_MATERIALS)));

    public static final RegistryHandler.Items<FossilPartItem> FOSSIL_PART = registerItem("fossil_part", p -> new FossilPartItem(p.stacksTo(1).component(ModDataComponents.FOSSIL_DATA.get(), FossilData.EMPTY)));
    public static final RegistryHandler.Items<Item> EGG_SHELL_FRAGMENT = registerItem("egg_shell_fragment", Item::new);

    public static final RegistryHandler.Items<Item> ROCK_FRAGMENT = registerItem("rock_fragment", Item::new);
    public static final RegistryHandler.Items<Item> DIRT_FRAGMENT = registerItem("dirt_fragment", Item::new);

    public static final RegistryHandler.Items<DNASampleItem> DNA_SAMPLE = registerItem("dna_sample", p -> new DNASampleItem(p.component(ModDataComponents.DNA_DATA.get(), DNAData.EMPTY)));
    public static final RegistryHandler.Items<Item> EXTRACTION_FLUID = registerItem("extraction_fluid", p -> new Item(p.durability(4)));
    public static final RegistryHandler.Items<Item> SAMPLE_VIAL = registerItem("sample_vial", Item::new);

    public static final RegistryHandler.Items<Item> GENOME_CARTRIDGE_BLANK = registerItem("genome_cartridge_blank", p -> new Item(p.stacksTo(1)));
    public static final RegistryHandler.Items<Item> GENOME_CARTRIDGE_FILLED = registerItem("genome_cartridge_filled", p -> new Item(p.stacksTo(1).component(ModDataComponents.GENOME_DATA.get(), GenomeData.EMPTY)));
    public static final RegistryHandler.Items<Item> GENOME_CARTRIDGE_COMPLETED = registerItem("genome_cartridge_completed", p -> new Item(p.stacksTo(1).component(ModDataComponents.SPECIES.get(), Species.TRICERATOPS)));

    public static final RegistryHandler.Items<Item> ARTIFICIAL_EGG = registerItem("artificial_egg", p -> new Item(p.stacksTo(16)));
    public static final RegistryHandler.Items<Item> NUTRIENT_SOLUTION = registerItem("nutrient_solution", p -> new Item(p.stacksTo(16)));
    public static final RegistryHandler.Items<Item> FERTILIZED_ANCIENT_EGG = registerItem("fertilized_ancient_egg", p -> new Item(p.stacksTo(1).component(ModDataComponents.SPECIES.get(), Species.TRICERATOPS)));

    // Materials from revived creatures (see the species loot tables).
    public static final RegistryHandler.Items<Item> RAW_PREHISTORIC_MEAT = registerItem("raw_prehistoric_meat", p -> new Item(p.food(new FoodProperties.Builder().nutrition(4).saturationModifier(0.3F).build())));
    public static final RegistryHandler.Items<Item> COOKED_PREHISTORIC_MEAT = registerItem("cooked_prehistoric_meat", p -> new Item(p.food(new FoodProperties.Builder().nutrition(9).saturationModifier(0.8F).build())));
    public static final RegistryHandler.Items<Item> THICK_HIDE = registerItem("thick_hide", Item::new);
    public static final RegistryHandler.Items<Item> WOOLLY_FUR = registerItem("woolly_fur", Item::new);
    public static final RegistryHandler.Items<Item> PREDATOR_TOOTH = registerItem("predator_tooth", Item::new);
    public static final RegistryHandler.Items<Item> SICKLE_CLAW = registerItem("sickle_claw", Item::new);
    public static final RegistryHandler.Items<Item> MEGALODON_TOOTH = registerItem("megalodon_tooth", p -> new Item(p.rarity(Rarity.UNCOMMON)));
    public static final RegistryHandler.Items<Item> PREHISTORIC_HORN = registerItem("prehistoric_horn", Item::new);
    public static final RegistryHandler.Items<Item> OSTEODERM = registerItem("osteoderm", Item::new);
    public static final RegistryHandler.Items<Item> MAMMOTH_TUSK = registerItem("mammoth_tusk", p -> new Item(p.rarity(Rarity.UNCOMMON)));
    public static final RegistryHandler.Items<Item> PREHISTORIC_FEATHER = registerItem("prehistoric_feather", Item::new);
    public static final RegistryHandler.Items<Item> ARTHROPLEURA_CHITIN = registerItem("arthropleura_chitin", Item::new);

    public static final RegistryHandler.Items<HornWhistleItem> HORN_WHISTLE = registerItem("horn_whistle", p -> new HornWhistleItem(p.stacksTo(1)));

    public static final RegistryHandler.Items<CreatureSaddleItem> CREATURE_SADDLE = registerItem("creature_saddle", p -> new CreatureSaddleItem(p.stacksTo(1)));
    public static final RegistryHandler.Items<CreatureArmorItem> IRON_CREATURE_ARMOR = registerItem("iron_creature_armor", p -> new CreatureArmorItem(CreatureArmorItem.Tier.IRON, p.stacksTo(1)));
    public static final RegistryHandler.Items<CreatureArmorItem> GOLDEN_CREATURE_ARMOR = registerItem("golden_creature_armor", p -> new CreatureArmorItem(CreatureArmorItem.Tier.GOLDEN, p.stacksTo(1)));
    public static final RegistryHandler.Items<CreatureArmorItem> DIAMOND_CREATURE_ARMOR = registerItem("diamond_creature_armor", p -> new CreatureArmorItem(CreatureArmorItem.Tier.DIAMOND, p.stacksTo(1)));
    public static final RegistryHandler.Items<CreatureArmorItem> NETHERITE_CREATURE_ARMOR = registerItem("netherite_creature_armor", p -> new CreatureArmorItem(CreatureArmorItem.Tier.NETHERITE, p.stacksTo(1).fireResistant()));

    public static final RegistryHandler.Items<MachineUpgradeItem> SPEED_UPGRADE_MODULE = registerItem("speed_upgrade_module", p -> new MachineUpgradeItem(MachineUpgradeItem.Kind.SPEED, p.stacksTo(16)));
    public static final RegistryHandler.Items<MachineUpgradeItem> PRECISION_UPGRADE_MODULE = registerItem("precision_upgrade_module", p -> new MachineUpgradeItem(MachineUpgradeItem.Kind.PRECISION, p.stacksTo(16)));
    public static final RegistryHandler.Items<MachineUpgradeItem> EFFICIENCY_UPGRADE_MODULE = registerItem("efficiency_upgrade_module", p -> new MachineUpgradeItem(MachineUpgradeItem.Kind.EFFICIENCY, p.stacksTo(16)));

    public static final RegistryHandler.Items<BabyCreatureCapsule> BABY_CREATURE_CAPSULE = registerItem("baby_creature_capsule", p -> new BabyCreatureCapsule(p.stacksTo(1).component(ModDataComponents.SPECIES.get(), Species.TRICERATOPS)));

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
