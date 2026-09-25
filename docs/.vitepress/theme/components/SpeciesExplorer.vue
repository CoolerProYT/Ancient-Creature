<script setup lang="ts">
import { computed, ref } from 'vue'
import { CATEGORY_NAMES, PROFILE_BLURBS, data, duration, hearts, prettify, profileName, seconds } from '../ancientcreature'
import ItemSlot from './ItemSlot.vue'

const species = [...data.species].sort((a, b) => a.name.localeCompare(b.name))
const categories = ['all', ...new Set(species.map((s) => s.category))]
const category = ref('all')
const query = ref('')

const shown = computed(() => {
  const q = query.value.trim().toLowerCase()
  return species.filter(
    (s) =>
      (category.value === 'all' || s.category === category.value) &&
      (!q || s.name.toLowerCase().includes(q) || s.biomes.some((b) => prettify(b).toLowerCase().includes(q)) || profileName(s.profile).toLowerCase().includes(q)),
  )
})

const maxHealth = Math.max(...species.map((s) => s.health))
const maxAttack = Math.max(...species.map((s) => s.attack))

const count = (name: string) => species.filter((s) => name === 'all' || s.category === name).length
</script>

<template>
  <div class="ac-species">
    <div class="toolbar">
      <div class="tabs" role="tablist" aria-label="Habitat">
        <button
          v-for="c in categories"
          :key="c"
          role="tab"
          :aria-selected="c === category"
          :class="{ active: c === category }"
          @click="category = c"
        >
          {{ c === 'all' ? 'All' : CATEGORY_NAMES[c] ?? c }} <span class="n">{{ count(c) }}</span>
        </button>
      </div>
      <input v-model="query" type="search" placeholder="Search name, biome or behaviour…" aria-label="Search species" />
    </div>

    <div class="grid">
      <article v-for="s in shown" :id="s.id.split(':')[1]" :key="s.id" class="card" :class="s.category">
        <header>
          <h3>{{ s.name }}</h3>
          <span class="badge" :class="s.category">{{ CATEGORY_NAMES[s.category] ?? s.category }}</span>
        </header>
        <p class="profile" :title="profileName(s.profile)">{{ PROFILE_BLURBS[s.profile] ?? profileName(s.profile) }}</p>

        <div class="stats">
          <div class="stat">
            <span class="label">Health</span>
            <span class="meter"><span class="health" :style="{ width: `${(s.health / maxHealth) * 100}%` }" /></span>
            <span class="value">{{ hearts(s.health) }} ♥</span>
          </div>
          <div class="stat">
            <span class="label">Attack</span>
            <span class="meter"><span class="attack" :style="{ width: `${(s.attack / maxAttack) * 100}%` }" /></span>
            <span class="value">{{ s.attack }}</span>
          </div>
        </div>

        <dl class="facts">
          <div><dt>Incubation</dt><dd>{{ seconds(s.incubationTime) }}</dd></div>
          <div><dt>Grows up</dt><dd>{{ duration(s.adultAge) }}</dd></div>
          <div><dt>Size</dt><dd>{{ s.width }} × {{ s.height }}</dd></div>
          <div v-if="s.armor"><dt>Armor</dt><dd>{{ s.armor }}</dd></div>
        </dl>

        <div class="row">
          <span class="row-label">Eats</span>
          <span v-if="s.diet.length" class="diet">
            <ItemSlot v-for="item in s.diet" :id="item" :key="item" size="sm" />
          </span>
          <span v-else class="ac-muted">Nothing — can't be tempted or bred</span>
        </div>
        <div class="row">
          <span class="row-label">Fossils in</span>
          <span class="biomes">
            <span v-for="biome in s.biomes" :key="biome" class="biome">{{ prettify(biome) }}</span>
          </span>
        </div>
      </article>
    </div>
    <p v-if="!shown.length" class="ac-muted empty">No species match.</p>
  </div>
</template>

<style scoped>
.ac-species {
  margin: 16px 0;
}

.toolbar {
  display: flex;
  flex-wrap: wrap;
  gap: 10px 16px;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 14px;
}

.tabs {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.tabs button {
  padding: 4px 12px;
  border: 1px solid var(--vp-c-divider);
  border-radius: 999px;
  font-size: 14px;
  color: var(--vp-c-text-2);
}

.tabs button.active {
  border-color: var(--vp-c-brand-1);
  background: var(--vp-c-brand-soft);
  color: var(--vp-c-brand-1);
  font-weight: 600;
}

.n {
  margin-left: 2px;
  font-size: 12px;
  opacity: 0.7;
}

.toolbar input {
  flex: 1;
  min-width: 200px;
  max-width: 320px;
  padding: 6px 10px;
  border: 1px solid var(--vp-c-divider);
  border-radius: 8px;
  background: var(--vp-c-bg);
  font-size: 14px;
}

.grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 14px;
}

.card {
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 14px 16px;
  border: 1px solid var(--vp-c-divider);
  border-top: 3px solid var(--cat);
  border-radius: 12px;
  background: var(--vp-c-bg-soft);
  scroll-margin-top: 90px;
  --cat: var(--ac-land);
}

.card.aquatic {
  --cat: var(--ac-aquatic);
}

.card.flying {
  --cat: var(--ac-flying);
}

header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
}

h3 {
  margin: 0 !important;
  padding: 0 !important;
  border: 0 !important;
  font-size: 17px !important;
  line-height: 1.3 !important;
}

.badge {
  padding: 1px 8px;
  border-radius: 999px;
  font-size: 11px;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.04em;
  color: var(--cat);
  background: color-mix(in srgb, var(--cat) 14%, transparent);
}

.profile {
  margin: -6px 0 0 !important;
  font-size: 13px;
  color: var(--vp-c-text-2);
}

.stats {
  display: grid;
  gap: 4px;
}

.stat {
  display: grid;
  grid-template-columns: 52px 1fr 52px;
  align-items: center;
  gap: 8px;
  font-size: 13px;
}

.label,
.row-label,
dt {
  font-size: 11px;
  text-transform: uppercase;
  letter-spacing: 0.04em;
  color: var(--vp-c-text-3);
}

.meter {
  height: 7px;
  border-radius: 4px;
  background: var(--vp-c-divider);
  overflow: hidden;
}

.meter span {
  display: block;
  height: 100%;
}

.meter .health {
  background: var(--vp-c-red-1);
}

.meter .attack {
  background: var(--vp-c-brand-1);
}

.value {
  font-family: var(--vp-font-family-mono);
  text-align: right;
}

.facts {
  display: flex;
  flex-wrap: wrap;
  gap: 6px 18px;
  margin: 0;
}

dd {
  margin: 0;
  font-size: 14px;
  font-weight: 600;
}

.row {
  display: flex;
  align-items: flex-start;
  gap: 10px;
}

.row-label {
  flex: none;
  width: 66px;
  padding-top: 4px;
}

.diet {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
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

.empty {
  text-align: center;
}
</style>
