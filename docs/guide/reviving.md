# Reviving a creature

With a completed genome you are two machines away from a living creature.

## Embryogenesis Chamber

<RecipeCard id="embryogenesis_chamber" />

The chamber combines a completed genome with an artificial egg and nutrients to make a fertilized egg of that species.

<MachineRecipe
  machine="ancientcreature:embryogenesis_chamber"
  :inputs="['ancientcreature:genome_cartridge_completed|Species: Triceratops', 'ancientcreature:artificial_egg', 'ancientcreature:nutrient_solution']"
  :outputs="['ancientcreature:fertilized_ancient_egg|Species: Triceratops']"
  config="EmbryogenesisChamber.embryogenesisTick"
/>

All three inputs are used up, including the cartridge. Each creature needs its own completed genome.

The slot below the genome is the **donor slot**. Leave it empty for a normal embryo, or put a second genome there to make a [hybrid](#hybrids).

### Genome traits

The chamber decides the embryo's traits from the genome's [fidelity](./dna-and-genomes#genome-fidelity), and they stay with the creature for life:

| Trait | Effect |
| --- | --- |
| **Size** | 80–120% of the species' normal size. Bigger animals also hit harder |
| **Vitality** | Maximum health |
| **Vigor** | Movement speed |
| **Temperament** | Calm, steady or fierce. Calm animals are easier to keep [comfortable](./keeping-creatures#comfort), fierce ones harder |
| **Sterile** | Can't breed |
| **Frail** | 30% less health |

A high-fidelity genome gives a healthy, fertile animal close to its species' norm. A poor one spreads the traits wider and makes a sterile or frail animal more likely. The fertilized egg, the capsule and (with Jade) the creature itself show its traits.

### Hybrids

Some species can be spliced together. Put a completed genome of one parent in the genome slot and a completed genome of the other in the donor slot (either way round). Both cartridges are used.

- Hybrids have no fossils of their own; this is the only way to make one.
- The hybrid's fidelity is 85% of its parents' average.
- Every hybrid is sterile.
- A hybrid enters the Species Journal the first time it is made.

The hybrids on the [Creatures](./creatures) page list their parents, and JEI shows every known pairing.

### Artificial Egg

<div class="ac-recipes">
<RecipeCard id="artificial_egg" />
<RecipeCard id="nutrient_solution" />
</div>

**Egg Shell Fragments** come from brushing suspicious sand and gravel at [dig sites](./fossils#dig-sites). Nutrient Solution is made from any potion (a Water Bottle is fine) and bone meal, and one craft makes 4.

## Incubator

<RecipeCard id="incubator" />

The incubator keeps the fertilized egg warm until it's ready to hatch. How long depends on the species; bigger creatures take longer.

<MachineRecipe
  machine="ancientcreature:incubator"
  :inputs="['ancientcreature:fertilized_ancient_egg|Species: Triceratops']"
  :outputs="['ancientcreature:baby_creature_capsule|Species: Triceratops']"
  :time="1000"
  note="Triceratops shown. Incubation ranges from 45 s for the smallest species to 140 s for Argentinosaurus. See each species on the Creatures page."
/>

## Upgrades

Every machine has two upgrade slots. In these two machines:

- **Precision**: the Embryogenesis Chamber treats the genome as 8% more faithful per module. The Incubator gives a frail embryo a 35% chance per module to come out healthy.
- **Efficiency**: in the Embryogenesis Chamber, each module gives a 25% chance to keep the Nutrient Solution.
- **Speed**: each module cuts processing time by a third.

See [Upgrade modules](./items#upgrade-modules).

## Releasing your creature

<ItemSlot id="ancientcreature:baby_creature_capsule" label size="lg" />

Use the **Baby Creature Capsule** on a block to release a baby of its species. You become its **owner**: it grows into an adult over time, and once grown you can [saddle and ride it](./riding). It starts out roaming around the spot where you released it; see [Keeping creatures](./keeping-creatures) for how to give it orders and keep it happy.

Capsules can't be used on Peaceful difficulty, or where too many creatures already live nearby (see [Population limit](./keeping-creatures#population-limit)).

## Breeding

Two adults of the same species can be bred by feeding them their [diet](./creatures) items. Instead of a baby appearing, they lay an **egg** on the spot. It hatches after the species' incubation time into a baby owned by the nearest player within 10 blocks. With Jade installed, look at the egg to see how long is left.

Both parents must be:

- **fertile**: sterile animals and hybrids can't breed,
- at least **settled** ([comfort](./keeping-creatures#comfort) 50 or more),
- below the [population limit](./keeping-creatures#population-limit). An egg also waits to hatch while the area is full.

The baby inherits the average of its parents' traits with a little variation, so a good bloodline stays good. It usually takes one parent's skin.

## Automation

| Machine | Top | North & south | East & west | Bottom |
| --- | --- | --- | --- | --- |
| Embryogenesis Chamber | Artificial Egg | Nutrient Solution | Completed cartridge | Fertilized egg (extract) |
| Incubator | Fertilized egg | Fertilized egg | Fertilized egg | Capsule (extract) |
