# Cleaning & identifying

A fossil fresh out of the ground is caked in dirt and nobody knows what it is. Two machines fix that.

## Cleaning

<RecipeCard id="fossil_cleaning_table" />

Put a brush in the top slot and a dirty fossil in the fossil slot. The table brushes it clean.

<MachineRecipe
  machine="ancientcreature:fossil_cleaning_table"
  :inputs="['minecraft:brush|Uses 1 durability', 'ancientcreature:fossil_part/dirty_skull']"
  :outputs="['ancientcreature:fossil_part/skull', 'ancientcreature:dirt_fragment|0–2 per fossil']"
  config="FossilCleaningTable.cleaningTick"
/>

- Cleaning keeps the species, the part and the completeness exactly as they were. It never damages a fossil.
- Each fossil costs the brush 1 durability and drops 0–2 **Dirt Fragments** as a by-product.
- The table waits if the output slots are too full to take the fossil and two more dirt fragments.

## Identification

Once fossils of a species are identified, two to four of them can be [reassembled](./fossils#reassembling-fragments) on a crafting table into one more complete fossil before extracting DNA.

<RecipeCard id="fossil_identification_chamber" />

Put a clean fossil into the chamber. It scans the fossil and reveals its species, which shows in the tooltip from then on.

<MachineRecipe
  machine="ancientcreature:fossil_identification_chamber"
  :inputs="['ancientcreature:fossil_part/skull|Species: ???']"
  :outputs="['ancientcreature:fossil_part/skull|Species: Triceratops']"
  config="FossilIdentificationTable.unknownIdentifyingTick"
  note="20 s for a species nobody has identified yet, 5 s for a species that is already known."
/>

### The first of its kind

The first time a species is identified on a server is the hard one. Until then, every scan of that species can **fail**, with a chance that depends on the fossil part. A failed fossil:

- comes out still unidentified,
- loses a share of its completeness (see the table below),
- can be put back in to try again.

Once any player succeeds, the species is **known** for everyone on the server. Known species always identify successfully, in a quarter of the time, and unlock their page in the [Species Journal](./getting-started#species-journal).

<FossilPartTable mode="identify" />

::: tip
Use a tooth or skull to unlock a new species. Once it is known, even claws identify every time.
:::

## Upgrades

Every machine has two upgrade slots, on the panel to the right of its screen:

- **Speed**: each module cuts processing time by a third.
- **Efficiency**: on the Fossil Cleaning Table, each module gives a 25% chance to keep the brush use.
- **Precision**: on the Fossil Identification Chamber, each module halves the chance that identification fails.

See [Upgrade modules](./items#upgrade-modules).

## Automation

Both machines work with hoppers and pipes.

| Machine | Top | Sides | Bottom |
| --- | --- | --- | --- |
| Fossil Cleaning Table | Brush | Dirty fossil | Clean fossil and dirt fragments (extract) |
| Fossil Identification Chamber | Fossil | Fossil | Result (extract) |

Every machine except the Fossil Cleaning Table is two blocks tall, so leave a free block above it.
