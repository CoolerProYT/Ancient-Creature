# Creatures

Ancient Creature ships with {{ data.species.length }} species. Each one is found as fossils in its own biomes, and each has its own size, strength, diet and temperament.

<script setup>
import { data } from '../.vitepress/theme/ancientcreature'
</script>

<SpeciesExplorer />

## Reading the cards

- **Fossils in**: the biomes where fossils of this species drop. See [Finding fossils](./fossils).
- **Hybrid of**: hybrids have no fossils. They're made by [splicing](./reviving#hybrids) the genomes of the two species named.
- **Eats**: food that tempts the creature, restores its hunger and makes two adults [breed](./reviving#breeding). Creatures with no diet can't be bred.
- **Incubation**: time in the [Incubator](./reviving#incubator), and for a bred egg to hatch.
- **Grows up**: how long a baby takes to become an adult. Only adults can be [ridden](./riding).
- **Size**: width × height of an adult, in blocks.
- **Social** and **Climate**: what the creature needs to stay [comfortable](./keeping-creatures#comfort): alone, in a pair or in a herd of a given size, and in which kind of biome.
- **Mount ability**: what happens when you press <kbd>R</kbd> while [riding](./riding#mount-abilities) it.

## Hunger

Every creature has a hunger meter that slowly drains. Predators only go looking for prey once they are hungry, and stop once they've eaten, so a well-fed T. rex won't chase down everything in sight. Hand-feeding a creature one of its diet items fills its hunger.

Hungry or not, creatures always defend themselves when attacked. With Jade installed, look at a creature to see its hunger.

## Your creatures

Creatures you release from a capsule, or hatch from a bred egg, are owned by you. Only the owner can ride them, equip them and give them orders with the Horn Whistle. Hungry predators hunt whether they're yours or not, so keep them away from your farm animals. See [Keeping creatures](./keeping-creatures) for orders, comfort and enclosures.

## Individuals

No two creatures are quite alike:

- **Skins**: every species has a rarer **mottled** skin. About 18% of creatures hatch with it, and bred babies usually take a parent's skin.
- **Genome**: size, health, speed and temperament vary with the quality of the genome a creature was made from, and some are sterile or frail. See [Genome traits](./reviving#genome-traits).

## Drops

Creatures drop **Raw Prehistoric Meat** (cook it like any meat) and materials that depend on the species: thick hide, osteoderms, horns, teeth, claws, feathers, fur and tusks. See [Creature materials](./items#creature-materials) for what each one is used for. Looting increases the drops, and creatures killed by fire drop cooked meat.

::: tip Make your own
Every creature here is a datapack file plus a Blockbench model. You can add new species, or change these, without writing any code. See [Custom species](/species/).
:::
