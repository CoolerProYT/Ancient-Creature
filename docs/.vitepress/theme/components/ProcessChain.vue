<script setup lang="ts">
import { withBase } from 'vitepress'
import ItemSlot from './ItemSlot.vue'

/** The road from a buried bone to a living creature, one machine per step. */
const steps = [
  {
    title: 'Dig',
    station: 'ancientcreature:iron_chisel',
    inputs: ['ancientcreature:fossil_ore', 'ancientcreature:rock_pile'],
    output: 'ancientcreature:fossil_part/dirty_skull',
    link: '/guide/fossils',
  },
  {
    title: 'Clean',
    station: 'ancientcreature:fossil_cleaning_table',
    inputs: ['minecraft:brush'],
    output: 'ancientcreature:fossil_part/skull',
    link: '/guide/cleaning-and-identifying',
  },
  {
    title: 'Identify',
    station: 'ancientcreature:fossil_identification_chamber',
    inputs: [],
    output: 'ancientcreature:fossil_part/skull',
    link: '/guide/cleaning-and-identifying#identification',
  },
  {
    title: 'Extract DNA',
    station: 'ancientcreature:dna_extractor',
    inputs: ['ancientcreature:extraction_fluid', 'ancientcreature:sample_vial'],
    output: 'ancientcreature:dna_sample/stable',
    link: '/guide/dna-and-genomes',
  },
  {
    title: 'Sequence',
    station: 'ancientcreature:genome_sequencer',
    inputs: ['ancientcreature:genome_cartridge_blank'],
    output: 'ancientcreature:genome_cartridge_completed',
    link: '/guide/dna-and-genomes#genome-sequencer',
  },
  {
    title: 'Grow an embryo',
    station: 'ancientcreature:embryogenesis_chamber',
    inputs: ['ancientcreature:artificial_egg', 'ancientcreature:nutrient_solution'],
    output: 'ancientcreature:fertilized_ancient_egg',
    link: '/guide/reviving#embryogenesis-chamber',
  },
  {
    title: 'Incubate',
    station: 'ancientcreature:incubator',
    inputs: [],
    output: 'ancientcreature:baby_creature_capsule',
    link: '/guide/reviving#incubator',
  },
]
</script>

<template>
  <ol class="ac-chain">
    <li v-for="(step, index) in steps" :key="step.title">
      <a :href="withBase(step.link)" class="step">
        <span class="number">{{ index + 1 }}</span>
        <span class="title">{{ step.title }}</span>
        <span class="machine"><ItemSlot :id="step.station" size="lg" /></span>
        <span class="io">
          <span v-if="step.inputs.length" class="inputs">
            <ItemSlot v-for="input in step.inputs" :id="input" :key="input" size="sm" />
          </span>
          <span class="arrow">➜</span>
          <ItemSlot :id="step.output" size="sm" />
        </span>
      </a>
    </li>
  </ol>
</template>

<style scoped>
.ac-chain {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 12px;
  margin: 20px 0;
  padding: 0 !important;
  list-style: none;
  counter-reset: step;
}

.ac-chain li {
  margin: 0 !important;
}

.step {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  height: 100%;
  padding: 14px 10px 12px;
  border: 1px solid var(--vp-c-divider);
  border-radius: 12px;
  background: var(--vp-c-bg-soft);
  color: inherit !important;
  text-decoration: none !important;
  transition:
    border-color 0.2s,
    transform 0.2s;
}

.step:hover {
  border-color: var(--vp-c-brand-1);
  transform: translateY(-2px);
}

.number {
  position: absolute;
  top: 8px;
  left: 10px;
  font: 700 12px/1 var(--vp-font-family-mono);
  color: var(--vp-c-brand-1);
}

.title {
  font-weight: 600;
  font-size: 14px;
}

.io {
  display: flex;
  align-items: center;
  gap: 4px;
}

.inputs {
  display: inline-flex;
  gap: 2px;
}

.arrow {
  color: var(--vp-c-text-3);
  font-size: 14px;
}
</style>
