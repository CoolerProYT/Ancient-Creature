<script setup lang="ts">
import { computed } from 'vue'
import { data, fossilItem, percent, signedPercent } from '../ancientcreature'
import ItemSlot from './ItemSlot.vue'

// "identify" shows only the identification odds, safest part first; the default shows every stat, most complete first.
const props = withDefaults(defineProps<{ mode?: 'all' | 'identify' }>(), { mode: 'all' })
const parts = computed(() =>
  [...data.fossilParts].sort((a, b) =>
    props.mode === 'identify' ? a.identifyFailChance - b.identifyFailChance : b.completenessMax - a.completenessMax,
  ),
)
</script>

<template>
  <table class="ac-parts">
    <thead>
      <tr>
        <th>Fossil</th>
        <th v-if="mode === 'all'">Completeness</th>
        <th>{{ mode === 'identify' ? 'Fail chance for a new species' : 'Identify fail chance' }}</th>
        <th v-if="mode === 'identify'">Completeness lost on a fail</th>
        <th v-if="mode === 'all'">DNA bonus</th>
      </tr>
    </thead>
    <tbody>
      <tr v-for="part in parts" :key="part.id">
        <td>
          <span class="pair">
            <ItemSlot v-if="mode === 'all'" :id="fossilItem(part.id, true)" size="sm" />
            <ItemSlot :id="fossilItem(part.id)" label />
          </span>
        </td>
        <td v-if="mode === 'all'">
          <div class="range">
            <span class="track"><span :style="{ left: percent(part.completenessMin), width: percent(part.completenessMax - part.completenessMin) }" /></span>
            <span class="value">{{ percent(part.completenessMin) }}–{{ percent(part.completenessMax) }}</span>
          </div>
        </td>
        <td :class="{ bad: part.identifyFailChance >= 0.35, good: part.identifyFailChance <= 0.05 }">{{ percent(part.identifyFailChance) }}</td>
        <td v-if="mode === 'identify'">{{ percent(part.damageRate) }}</td>
        <td v-if="mode === 'all'" :class="{ bad: part.dnaBonus < 0, good: part.dnaBonus > 0 }">{{ signedPercent(part.dnaBonus) }}</td>
      </tr>
    </tbody>
  </table>
</template>

<style scoped>
td {
  vertical-align: middle;
}

.pair {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  white-space: nowrap;
}

.range {
  display: flex;
  align-items: center;
  gap: 8px;
  min-width: 150px;
}

.track {
  position: relative;
  flex: 1;
  height: 8px;
  border-radius: 4px;
  background: var(--vp-c-divider);
}

.track span {
  position: absolute;
  top: 0;
  bottom: 0;
  border-radius: 4px;
  background: var(--vp-c-brand-1);
}

.value {
  font-family: var(--vp-font-family-mono);
  font-size: 13px;
  white-space: nowrap;
}

.good {
  color: var(--vp-c-green-1);
  font-weight: 600;
}

.bad {
  color: var(--vp-c-red-1);
  font-weight: 600;
}
</style>
