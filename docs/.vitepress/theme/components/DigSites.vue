<script setup lang="ts">
import { computed, ref } from 'vue'
import { data, fossilItem, itemName, percent, prettify } from '../ancientcreature'
import ItemSlot from './ItemSlot.vue'

// Each biome's dig site brushes its own loot table; pick a site to see what it turns up.
const selected = ref(data.digSites[0]?.id ?? '')
const loot = computed(() => {
  const entries = data.archaeology[selected.value] ?? []
  const total = entries.reduce((sum, entry) => sum + entry.weight, 0)
  return entries.map((entry) => ({ ...entry, chance: entry.weight / total }))
})
const fossilChance = computed(() => loot.value.filter((entry) => entry.parts).reduce((sum, entry) => sum + entry.chance, 0))
const selectedName = computed(() => data.digSites.find((site) => site.id === selected.value)?.name ?? '')
</script>

<template>
  <div class="ac-digsites">
    <div class="sites">
      <button v-for="site in data.digSites" :key="site.id" class="site" :class="{ active: site.id === selected }" type="button" @click="selected = site.id">
        <div class="name">{{ site.name }}</div>
        <div class="biomes">
          <span v-for="biome in site.biomes" :key="biome" class="biome">{{ prettify(biome) }}</span>
        </div>
      </button>
    </div>

    <h4>Brushing at the {{ selectedName }} <span class="ac-muted">({{ percent(fossilChance, 1) }} fossils)</span></h4>
    <ul class="loot">
      <li v-for="(entry, index) in loot" :key="index">
        <template v-if="entry.parts">
          <span class="parts">
            <ItemSlot v-for="part in entry.parts" :id="fossilItem(part, true)" :key="part" size="sm" />
          </span>
          <span class="what">{{ entry.parts.map((p) => itemName(fossilItem(p))).join(', ') }}</span>
        </template>
        <template v-else-if="entry.item">
          <ItemSlot :id="entry.item" size="sm" />
          <span class="what">{{ itemName(entry.item) }}</span>
        </template>
        <template v-else>
          <span class="empty-slot" />
          <span class="what ac-muted">Nothing</span>
        </template>
        <span class="bar"><span :style="{ width: percent(entry.chance / Math.max(...loot.map((l) => l.chance)), 1) }" /></span>
        <span class="pct">{{ percent(entry.chance, 1) }}</span>
      </li>
    </ul>
  </div>
</template>

<style scoped>
.sites {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 10px;
  margin: 16px 0;
}

.site {
  font: inherit;
  color: inherit;
  text-align: left;
  cursor: pointer;
  padding: 10px 12px;
  border: 1px solid var(--vp-c-divider);
  border-left: 3px solid var(--vp-c-brand-1);
  border-radius: 8px;
  background: var(--vp-c-bg-soft);
}

.name {
  font-weight: 600;
  margin-bottom: 6px;
}

.biomes {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}

.biome {
  padding: 1px 8px;
  border-radius: 6px;
  font-size: 12px;
  background: var(--vp-c-default-soft);
  color: var(--vp-c-text-2);
}

.loot {
  list-style: none;
  padding: 0 !important;
  margin: 8px 0 16px;
}

.loot li {
  display: grid;
  grid-template-columns: auto minmax(0, 1fr) minmax(60px, 160px) 52px;
  align-items: center;
  gap: 10px;
  margin: 6px 0 !important;
}

.parts {
  display: inline-flex;
  gap: 2px;
}

.empty-slot {
  width: 26px;
  height: 26px;
  border: 2px dashed var(--vp-c-divider);
}

.what {
  font-size: 14px;
}

.bar {
  height: 7px;
  border-radius: 4px;
  background: var(--vp-c-divider);
  overflow: hidden;
}

.bar span {
  display: block;
  height: 100%;
  background: var(--vp-c-brand-1);
}

.pct {
  font: 600 13px var(--vp-font-family-mono);
  text-align: right;
}
.site.active {
  border-color: var(--vp-c-brand-1);
  box-shadow: 0 0 0 1px var(--vp-c-brand-1);
}
</style>
