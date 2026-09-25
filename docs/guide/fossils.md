# Finding fossils

Every fossil belongs to a species, and the species is decided by **the biome you are standing in** when the fossil drops. A fossil dug in a snowy plains could be a Woolly Mammoth or a Woolly Rhinoceros; the same find in a warm ocean could be a Megalodon. The [Creatures](./creatures) page lists the biomes for each species.

If no species lives in that biome, nothing drops.

Fresh fossils are always **dirty** and **unidentified**. Their tooltip shows `???` for the species until you [identify](./cleaning-and-identifying#identification) them.

## Where to look

### Fossil Ore

<ItemSlot id="ancientcreature:fossil_ore" label size="lg" />

Single blocks of fossil ore generate in stone in every Overworld biome, between Y -64 and Y 50, in about one chunk out of 64. Mining one with a [chisel](#chisels) drops a single dirty fossil of any part, including egg fossils. A pickaxe won't drop anything.

### Rock piles

<ItemSlot id="ancientcreature:rock_pile" label size="lg" />

Small piles of stone scattered on the surface of Overworld biomes. Most are just rocks, but some have bones or an egg poking out, and you can see which before you break them:

| Rock pile looks like | Chance | Drops (with a chisel) |
| --- | --- | --- |
| Plain rocks | 74% | 1–4 <ItemSlot id="ancientcreature:rock_fragment" size="sm" /> Rock Fragments |
| Rocks with fossil fragments | 22% | Rock Fragments and a dirty fossil of any part |
| Rocks with an egg | 3% | Rock Fragments and a dirty <ItemSlot id="ancientcreature:fossil_part/dirty_egg" size="sm" /> Egg Fossil |
| Rocks with fragments and an egg | under 1% | Rock Fragments, a dirty fossil and a dirty Egg Fossil |

Rock piles also need a chisel to drop anything.

### Dig sites

Dig sites are small excavations with suspicious sand or gravel. Brush the suspicious blocks with a vanilla brush. Brushing never damages a fossil, but it only turns up claws, teeth, ribs and limbs, and it's the only natural source of **Egg Shell Fragments**, which you need for [Artificial Eggs](./reviving#artificial-egg).

<DigSites />

## Fossil parts

Seven kinds of fossil can turn up. They differ in how much of the animal survived (**completeness**), how likely they are to fail [identification](./cleaning-and-identifying#identification), and how much they help when [extracting DNA](./dna-and-genomes#dna-extractor).

<FossilPartTable />

The left icon is the dirty fossil as dug up, the right is the cleaned one. Completeness is rolled when the fossil drops, then reduced by the chisel.

## Chisels

Chisels mine fossil ore and rock piles. Each chisel chips off a share of the completeness of every fossil it digs up, so a better chisel means better fossils.

<ChiselTable />

All chisels share one recipe shape, a stick and two of the material:

<div class="ac-recipes">
<RecipeCard id="stone_chisel" />
<RecipeCard id="copper_chisel" />
<RecipeCard id="iron_chisel" />
<RecipeCard id="golden_chisel" />
<RecipeCard id="diamond_chisel" />
</div>

The <ItemSlot id="ancientcreature:netherite_chisel" size="sm" /> Netherite Chisel is the only one that does no damage. It has no crafting recipe yet.

Chisels can be repaired in an anvil with their material, like vanilla tools. A chisel with **Fortune** adds 10% completeness per level before the chisel's damage is taken off, but chisels can't be enchanted in survival yet.

## Try it

Pick a fossil and a chisel to see what you can expect, all the way to the DNA it will give.

<FossilCalculator />
