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

## Releasing your creature

<ItemSlot id="ancientcreature:baby_creature_capsule" label size="lg" />

Use the **Baby Creature Capsule** on a block to release a baby of its species. You become its **owner**: it grows into an adult over time, and once grown you can [ride it](./riding).

Capsules can't be used on Peaceful difficulty.

## Breeding

Two adults of the same species can be bred by feeding them their [diet](./creatures) items. Instead of a baby appearing, they lay an **egg** on the spot. It hatches after the species' incubation time into a baby owned by the nearest player within 10 blocks. With Jade installed, look at the egg to see how long is left.

## Automation

| Machine | Top | North & south | East & west | Bottom |
| --- | --- | --- | --- | --- |
| Embryogenesis Chamber | Artificial Egg | Nutrient Solution | Completed cartridge | Fertilized egg (extract) |
| Incubator | Fertilized egg | Fertilized egg | Fertilized egg | Capsule (extract) |
