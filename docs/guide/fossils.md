# Finding fossils

Every fossil belongs to a species, and the species is decided by **the biome where the fossil is found**. A fossil dug in a snowy plains could be a Woolly Mammoth or a Woolly Rhinoceros; the same find in a warm ocean could be a Megalodon. The [Creatures](./creatures) page lists the biomes for each species.

Underground, or in a biome no species calls home, a fossil takes the species of the land above it, and failing that it can be any species at all.

Fresh fossils are always **dirty** and **unidentified**. Their tooltip shows `???` for the species until you [identify](./cleaning-and-identifying#identification) them.

## Where to look

### Fossil Ore

<ItemSlot id="ancientcreature:fossil_ore" label size="lg" />

Small veins of fossil ore generate in stone in every Overworld biome, between Y -64 and Y 50, in about one chunk out of 64. Mining one with a [chisel](#chisels) drops a single dirty fossil of any bone part, including egg fossils. A pickaxe won't drop anything.

### Deepslate Fossil Ore

<ItemSlot id="ancientcreature:deepslate_fossil_ore" label size="lg" />

Below Y 0, in about one chunk out of 12. Only skulls, vertebrae, teeth and egg fossils, and the deep rock keeps them **15% more complete**.

### Amber

<ItemSlot id="ancientcreature:amber_ore" label size="lg" />

Amber ore forms in the stone under jungles, dark forests and mangrove swamps, and in lush caves, between Y 8 and Y 96. Chisel it for **Amber with Insect**, a fossil part that is cleaned, identified and extracted like any other. The insect's last blood meal gives it the best DNA bonus of any part, and it almost never fails identification.

### Frozen fossils

<ItemSlot id="ancientcreature:frozen_fossil" label size="lg" />

Found in the packed and blue ice of ice spikes, icebergs, frozen peaks and snowy slopes. Ice keeps a carcass almost whole, so these come out **25% more complete**, and they are always ice-age animals: Woolly Mammoth, Woolly Rhinoceros, Dire Wolf or Smilodon.

### Fossil seams and beds

On bare cliff faces in badlands, stony shores, stony peaks, windswept hills and savanna plateaus, look for pale bands of fossil ore and bone. Seams only form where they can be seen.

In deserts, badlands, savannas, plains, snowy plains and swamps, whole skeletons lie buried 15 to 25 blocks down. They look like vanilla fossils, but with fossil ore where vanilla puts coal and in some of the bones.

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

Dig sites are expedition camps with up to three excavation pits around them: trenches, stepped quarries, half-uncovered skeletons and collapsed sinkholes. Brush the suspicious sand and gravel with a vanilla brush; the deeper layers also hold fossil ore. Brushing never damages a fossil, and a site has around 30 to 40 brushable blocks. Dig sites are also the main source of **Egg Shell Fragments**, which you need for [Artificial Eggs](./reviving#artificial-egg).

Each biome's site turns up its own mix of parts: badland and highland sites give more skulls, savannas more limbs and vertebrae, jungle sites hide amber, cold sites keep their bones 10% more complete, and coastal sites give sea creatures. Pick a site below to see its loot.

The camp chest holds fossils, tools, the expedition's **Field Notes** (a book listing the species recorded in that region, with a few tips), and sometimes a **Dig Site Map** to another site. About one camp in eight was overrun long ago. Its undead crew still guards the main pit from a spawner, but its chest holds rarer, better-preserved finds.

<DigSites />

### Sifting

<ItemSlot id="ancientcreature:sifter" label size="lg" />

<RecipeCard id="sifter" />

Use gravel, sand, red sand, mud or dirt on a Sifter, then use the Sifter four times to shake out what was in it. About one load in 30 gives a fossil; the rest is flint, rock fragments, egg shell and the odd nugget. Slow, but it never runs out.

### Everywhere else

- **Vanilla ruins:** brushing desert pyramids and desert wells (8%), trail ruins (5%, 12% for the rare loot) and ocean ruins (6%, sea creatures only) can turn up a fossil instead of the usual find.
- **Fishing:** about one catch in a hundred is a fossil or a piece of amber.
- **Mobs:** husks and drowned killed by a player have a 2.5% chance to drop a fossil (+1% per level of Looting).
- **Trading:** wandering traders sometimes sell an uncleaned fossil (12 emeralds) or amber (20 emeralds). Journeyman cartographers sell Dig Site Maps.

### Reassembling fragments

Two to four **identified** fossils of the same species can be combined on a crafting table. The most complete piece is kept, and every other piece adds half of its own completeness to it, up to 100%.

## Fossil parts

Eight kinds of fossil can turn up. They differ in how much of the animal survived (**completeness**), how likely they are to fail [identification](./cleaning-and-identifying#identification), and how much they help when [extracting DNA](./dna-and-genomes#dna-extractor).

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
