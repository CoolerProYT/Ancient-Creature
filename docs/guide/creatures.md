# Creatures

Ancient Creature ships with {{ data.species.length }} species. Each one is found as fossils in its own biomes, and each has its own size, strength, diet and temperament.

<script setup>
import { data } from '../.vitepress/theme/ancientcreature'
</script>

<SpeciesExplorer />

## Reading the cards

- **Fossils in**: the biomes where fossils of this species drop. See [Finding fossils](./fossils).
- **Eats**: food that tempts the creature, restores its hunger and makes two adults [breed](./reviving#breeding). Creatures with no diet can't be bred.
- **Incubation**: time in the [Incubator](./reviving#incubator), and for a bred egg to hatch.
- **Grows up**: how long a baby takes to become an adult. Only adults can be [ridden](./riding).
- **Size**: width × height of an adult, in blocks.

## Hunger

Every creature has a hunger meter that slowly drains. Predators only go looking for prey once they are hungry, and stop once they've eaten, so a well-fed T. rex won't chase down everything in sight. Hand-feeding a creature one of its diet items fills its hunger.

Hungry or not, creatures always defend themselves when attacked. With Jade installed, look at a creature to see its hunger.

## Your creatures

Creatures you release from a capsule, or hatch from a bred egg, are owned by you. Only the owner can ride them. Wild creatures and creatures you own behave the same way otherwise, so keep predators away from your farm animals.

::: tip Make your own
Every creature here is a datapack file plus a Blockbench model. You can add new species, or change these, without writing any code. See [Custom species](/species/).
:::
