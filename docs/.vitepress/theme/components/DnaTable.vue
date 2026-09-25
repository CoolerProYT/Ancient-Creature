<script setup lang="ts">
import { data, percent } from '../ancientcreature'
import ItemSlot from './ItemSlot.vue'

const levels = data.dnaLevels
const first = levels[0]
</script>

<template>
  <table class="ac-dna">
    <thead>
      <tr>
        <th>DNA sample</th>
        <th>Integrity score</th>
        <th>Genome added</th>
        <th>Samples to complete</th>
      </tr>
    </thead>
    <tbody>
      <tr class="none">
        <td><span class="ac-muted">No sample</span></td>
        <td>below {{ percent(first.scoreMin) }}</td>
        <td colspan="2"><span class="ac-muted">The fossil and extraction fluid are used up, the vial is kept</span></td>
      </tr>
      <tr v-for="level in levels" :key="level.id">
        <td><ItemSlot :id="`ancientcreature:dna_sample/${level.id}`" label /></td>
        <td>
          {{ level.scoreMax >= 1 ? `above ${percent(level.scoreMin)}` : `${percent(level.scoreMin)}–${percent(level.scoreMax)}` }}
        </td>
        <td>
          <span class="chip">+{{ level.genomeMin }}–{{ level.genomeMax }}%</span>
        </td>
        <td>{{ Math.ceil(100 / level.genomeMax) }}–{{ Math.ceil(100 / level.genomeMin) }}</td>
      </tr>
    </tbody>
  </table>
</template>

<style scoped>
td {
  vertical-align: middle;
}

.chip {
  display: inline-block;
  padding: 1px 8px;
  border-radius: 999px;
  background: var(--ac-accent-soft);
  color: var(--ac-accent);
  font: 600 13px/1.6 var(--vp-font-family-mono);
}
</style>
