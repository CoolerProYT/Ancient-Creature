<script setup lang="ts">
import { computed } from 'vue'
import { data, percent } from '../ancientcreature'
import ItemSlot from './ItemSlot.vue'

// Best chisel (least damage) first.
const chisels = computed(() => [...data.chisels].sort((a, b) => a.damageRate - b.damageRate))
</script>

<template>
  <table class="ac-chisels">
    <thead>
      <tr>
        <th>Chisel</th>
        <th>Durability</th>
        <th>Completeness lost</th>
        <th>Completeness kept</th>
      </tr>
    </thead>
    <tbody>
      <tr v-for="chisel in chisels" :key="chisel.id">
        <td><ItemSlot :id="chisel.id" label :lore="`-${percent(chisel.damageRate)} Completeness`" /></td>
        <td>{{ chisel.durability }}</td>
        <td :class="{ bad: chisel.damageRate >= 0.4, good: chisel.damageRate === 0 }">{{ chisel.damageRate === 0 ? 'None' : `-${percent(chisel.damageRate)}` }}</td>
        <td>
          <div class="keep">
            <span class="bar"><span :style="{ width: percent(1 - chisel.damageRate) }" /></span>
            <span class="value">{{ percent(1 - chisel.damageRate) }}</span>
          </div>
        </td>
      </tr>
    </tbody>
  </table>
</template>

<style scoped>
td {
  vertical-align: middle;
}

.keep {
  display: flex;
  align-items: center;
  gap: 8px;
  min-width: 140px;
}

.bar {
  flex: 1;
  height: 8px;
  border-radius: 4px;
  background: var(--vp-c-divider);
  overflow: hidden;
}

.bar span {
  display: block;
  height: 100%;
  background: var(--vp-c-brand-1);
}

.value {
  font-family: var(--vp-font-family-mono);
  font-size: 13px;
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
