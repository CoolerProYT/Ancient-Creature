# Items & recipes

Every item in the mod at a glance. Hover over (or tap) an item for its name.

<ItemGallery />

## Machines

The six lab machines, in the order you'll use them. All of them are mined with a pickaxe and keep their contents safe: breaking one drops everything inside.

<div class="ac-recipes">
<RecipeCard id="fossil_cleaning_table" />
<RecipeCard id="fossil_identification_chamber" />
<RecipeCard id="dna_extractor" />
<RecipeCard id="genome_sequencer" />
<RecipeCard id="embryogenesis_chamber" />
<RecipeCard id="incubator" />
</div>

## Tools

<div class="ac-recipes">
<RecipeCard id="stone_chisel" />
<RecipeCard id="copper_chisel" />
<RecipeCard id="iron_chisel" />
<RecipeCard id="golden_chisel" />
<RecipeCard id="diamond_chisel" />
<RecipeCard id="netherite_chisel_smithing" />
</div>

See [Chisels](./fossils#chisels) for how much each one damages fossils.

## Finding fossils

<div class="ac-recipes">
<RecipeCard id="sifter" />
</div>

Load the Sifter with gravel, sand, red sand, mud or dirt and work it to shake out fragments. Two to four identified fossils of the same species can also be pieced together on a crafting table into one more complete fossil. See [Finding fossils](./fossils#sifting).

Dig site chests also hold two vanilla items with mod content: **Field Notes**, a written book listing the species recorded in that region, and a **Dig Site Map** that leads to another, unexplored dig site. Cartographers sell the maps too.

## Upgrade modules

<div class="ac-recipes">
<RecipeCard id="speed_upgrade_module" />
<RecipeCard id="precision_upgrade_module" />
<RecipeCard id="efficiency_upgrade_module" />
</div>

Every lab machine has an upgrade panel to the right of its screen with two module slots. Two modules of the same kind stack their effect. Modules drop with the machine when it's broken, and shift-clicking one from your inventory puts it straight into the panel.

| Module | Machine | Effect per module |
| --- | --- | --- |
| **Speed** | All | Cuts processing time by a third (two modules: 44% of the time) |
| **Efficiency** | Fossil Cleaning Table | 25% chance to keep the brush use |
| | DNA Extractor | 25% chance to keep the Extraction Fluid use, and separately the Sample Vial |
| | Embryogenesis Chamber | 25% chance to keep the Nutrient Solution |
| **Precision** | Fossil Identification Chamber | Halves the chance that identification fails |
| | DNA Extractor | +5% DNA integrity |
| | Genome Sequencer | +15% genome and +5% [fidelity](./dna-and-genomes#genome-fidelity) from every sample |
| | Embryogenesis Chamber | +8% effective fidelity, for better [traits](./reviving#genome-traits) |
| | Incubator | 35% chance to steady a frail embryo |

## Containment

<div class="ac-recipes">
<RecipeCard id="electric_fence" />
<RecipeCard id="reinforced_fence" />
<RecipeCard id="reinforced_fence_gate" />
</div>

Distressed large creatures smash wooden fences, walls and glass, but not these. The electric fence shocks anything that touches it while it carries a redstone charge. See [Enclosures](./keeping-creatures#enclosures).

## Creature gear

<div class="ac-recipes">
<RecipeCard id="creature_saddle" />
<RecipeCard id="horn_whistle" />
<RecipeCard id="horn_whistle_from_goat_horn" />
<RecipeCard id="iron_creature_armor" />
<RecipeCard id="golden_creature_armor" />
<RecipeCard id="diamond_creature_armor" />
<RecipeCard id="netherite_creature_armor_smithing" />
</div>

The saddle and armor go on your adult creatures; see [Riding creatures](./riding). The Horn Whistle gives them orders; see [Giving orders](./keeping-creatures#giving-orders).

## Creature materials

Creatures drop these when killed. Looting increases the drops.

| Item | Dropped by | Used for |
| --- | --- | --- |
| <ItemSlot id="ancientcreature:raw_prehistoric_meat" label size="sm" /> | Almost every creature | Food (4 hunger). Cooks into <ItemSlot id="ancientcreature:cooked_prehistoric_meat" label size="sm" /> (9 hunger). Both count as meat, so predators eat them |
| <ItemSlot id="ancientcreature:thick_hide" label size="sm" /> | Large herbivores and predators | Creature Saddle, creature armor, or 2 Leather |
| <ItemSlot id="ancientcreature:osteoderm" label size="sm" /> | Ankylosaurus, Stegosaurus, Stegoceratops, Dunkleosteus | Creature armor, or 4 Bone Meal |
| <ItemSlot id="ancientcreature:prehistoric_horn" label size="sm" /> | Triceratops, Carnotaurus, Woolly Rhinoceros, Stegoceratops | Horn Whistle, or 4 Bone Meal |
| <ItemSlot id="ancientcreature:predator_tooth" label size="sm" /> | Predators | 3 Bone Meal |
| <ItemSlot id="ancientcreature:sickle_claw" label size="sm" /> | Velociraptor, Deinonychus, Tyrannoraptor | 3 Bone Meal |
| <ItemSlot id="ancientcreature:mammoth_tusk" label size="sm" /> | Woolly Mammoth | 9 Bone Meal |
| <ItemSlot id="ancientcreature:prehistoric_feather" label size="sm" /> | Velociraptor, Deinonychus | 2 Feathers |
| <ItemSlot id="ancientcreature:woolly_fur" label size="sm" /> | Woolly Mammoth, Woolly Rhinoceros | White Wool (2 fur make 1) |
| <ItemSlot id="ancientcreature:megalodon_tooth" label size="sm" /> | Megalodon | A trophy |
| <ItemSlot id="ancientcreature:arthropleura_chitin" label size="sm" /> | Arthropleura | A trophy |

## Lab supplies

<div class="ac-recipes">
<RecipeCard id="extraction_fluid" />
<RecipeCard id="sample_vial" />
<RecipeCard id="genome_cartridge_blank" />
<RecipeCard id="nutrient_solution" />
<RecipeCard id="artificial_egg" />
</div>

## Items with data

Some items carry information in their tooltip, and the same item can look different depending on it.

| Item | Tooltip shows |
| --- | --- |
| <ItemSlot id="ancientcreature:fossil_part/dirty_rib" size="sm" /> <ItemSlot id="ancientcreature:fossil_part/rib" label size="sm" /> | Species (`???` until identified), completeness. Dirty fossils are named *Dirty …* |
| <ItemSlot id="ancientcreature:dna_sample/partial" label size="sm" /> | Species and integrity level. Its colour shows the integrity |
| <ItemSlot id="ancientcreature:genome_cartridge_filled" label size="sm" /> | Species and completeness |
| <ItemSlot id="ancientcreature:genome_cartridge_completed" label size="sm" /> | Species |
| <ItemSlot id="ancientcreature:fertilized_ancient_egg" label size="sm" /> | Species |
| <ItemSlot id="ancientcreature:baby_creature_capsule" label size="sm" /> | Species |
| <ItemSlot id="ancientcreature:stone_chisel" label size="sm" /> | How much completeness it removes from fossils |

## By-products

<ItemSlot id="ancientcreature:rock_fragment" label /> drops from rock piles, dig sites and the Sifter, and <ItemSlot id="ancientcreature:dirt_fragment" label /> from the Fossil Cleaning Table. They have no use yet.
