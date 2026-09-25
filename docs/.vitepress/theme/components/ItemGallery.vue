<script setup lang="ts">
import ItemSlot from './ItemSlot.vue'

/** Every item a player handles, grouped the way the creative tab groups them. */
const groups: { title: string; items: string[] }[] = [
  {
    title: 'Machines',
    items: [
      'ancientcreature:fossil_cleaning_table',
      'ancientcreature:fossil_identification_chamber',
      'ancientcreature:dna_extractor',
      'ancientcreature:genome_sequencer',
      'ancientcreature:embryogenesis_chamber',
      'ancientcreature:incubator',
    ],
  },
  {
    title: 'Chisels',
    items: ['stone', 'copper', 'iron', 'golden', 'diamond', 'netherite'].map((m) => `ancientcreature:${m}_chisel`),
  },
  {
    title: 'Fossils',
    items: [
      'ancientcreature:fossil_ore',
      'ancientcreature:rock_pile',
      ...['skull', 'tooth', 'vertebra', 'rib', 'limb', 'claw', 'egg'].flatMap((p) => [`ancientcreature:fossil_part/dirty_${p}`, `ancientcreature:fossil_part/${p}`]),
    ],
  },
  {
    title: 'DNA & genomes',
    items: [
      'ancientcreature:extraction_fluid',
      'ancientcreature:sample_vial',
      ...['degraded', 'partial', 'stable', 'preserved_embryo'].map((l) => `ancientcreature:dna_sample/${l}`),
      'ancientcreature:genome_cartridge_blank',
      'ancientcreature:genome_cartridge_filled',
      'ancientcreature:genome_cartridge_completed',
    ],
  },
  {
    title: 'Eggs & creatures',
    items: [
      'ancientcreature:egg_shell_fragment',
      'ancientcreature:nutrient_solution',
      'ancientcreature:artificial_egg',
      'ancientcreature:fertilized_ancient_egg',
      'ancientcreature:baby_creature_capsule',
    ],
  },
  {
    title: 'By-products',
    items: ['ancientcreature:rock_fragment', 'ancientcreature:dirt_fragment'],
  },
]
</script>

<template>
  <div class="ac-gallery">
    <section v-for="group in groups" :key="group.title">
      <h4>{{ group.title }}</h4>
      <div class="slots">
        <ItemSlot v-for="item in group.items" :id="item" :key="item" size="lg" />
      </div>
    </section>
    <p class="ac-muted hint">Hover or tap an item to see its name.</p>
  </div>
</template>

<style scoped>
.ac-gallery {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 6px 24px;
  margin: 16px 0;
  padding: 8px 18px 14px;
  border: 1px solid var(--vp-c-divider);
  border-radius: 12px;
  background: var(--vp-c-bg-soft);
}

h4 {
  margin: 10px 0 8px !important;
  font-size: 13px !important;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  color: var(--vp-c-text-2);
}

.slots {
  display: flex;
  flex-wrap: wrap;
  gap: 0;
}

.hint {
  grid-column: 1 / -1;
  margin: 6px 0 0 !important;
}
</style>
