<script setup lang="ts">
import { computed, ref } from 'vue'
import { data, dnaLevelFor, dugCompleteness, fossilItem, itemName, percent } from '../ancientcreature'
import ItemSlot from './ItemSlot.vue'

const parts = [...data.fossilParts].sort((a, b) => b.completenessMax - a.completenessMax)
const chisels = [...data.chisels].sort((a, b) => a.damageRate - b.damageRate)

const selectedPart = ref(parts.find((p) => p.id === 'skull')?.id ?? parts[0].id)
const selectedChisel = ref(chisels.find((c) => c.id.endsWith('iron_chisel'))?.id ?? chisels[0].id)
const fortune = ref(0)
const brushed = ref(false)

const part = computed(() => parts.find((p) => p.id === selectedPart.value)!)
const chisel = computed(() => chisels.find((c) => c.id === selectedChisel.value)!)
// Brushing a dig site uses no chisel, so nothing is lost; only claws, teeth, ribs and limbs turn up that way.
const brushParts = computed(() => new Set(Object.values(data.archaeology).flat().flatMap((entry) => entry.parts ?? [])))
const canBrush = computed(() => brushParts.value.has(part.value.id))
const damage = computed(() => (brushed.value && canBrush.value ? 0 : chisel.value.damageRate))
const fortuneLevel = computed(() => (brushed.value && canBrush.value ? 0 : fortune.value))

const low = computed(() => dugCompleteness(part.value.completenessMin, damage.value, fortuneLevel.value))
const high = computed(() => dugCompleteness(part.value.completenessMax, damage.value, fortuneLevel.value))
// A failed identification knocks the part's damage rate off again; it only happens for a species nobody has identified yet.
const lowAfterFail = computed(() => low.value * (1 - part.value.damageRate))

const scoreLow = computed(() => Math.max(0.01, low.value + part.value.dnaBonus))
const scoreHigh = computed(() => Math.max(0.01, high.value + part.value.dnaBonus))
const dnaLow = computed(() => dnaLevelFor(low.value, part.value))
const dnaHigh = computed(() => dnaLevelFor(high.value, part.value))

/** Coloured bands on the integrity scale, one per DNA level. */
const zones = computed(() => [
  { id: 'none', from: 0, to: data.dnaLevels[0].scoreMin, name: 'No DNA' },
  ...data.dnaLevels.map((level) => ({ id: level.id, from: level.scoreMin, to: level.scoreMax, name: level.name })),
])
const clamp = (value: number) => Math.min(1, Math.max(0, value))
</script>

<template>
  <div class="ac-calc">
    <div class="controls">
      <label>
        Fossil
        <select v-model="selectedPart">
          <option v-for="p in parts" :key="p.id" :value="p.id">{{ p.name }}</option>
        </select>
      </label>
      <label>
        Dug with
        <select v-model="selectedChisel" :disabled="brushed && canBrush">
          <option v-for="c in chisels" :key="c.id" :value="c.id">{{ itemName(c.id) }}</option>
        </select>
      </label>
      <label>
        Fortune
        <select v-model.number="fortune" :disabled="brushed && canBrush">
          <option v-for="level in [0, 1, 2, 3]" :key="level" :value="level">{{ level === 0 ? 'None' : ['I', 'II', 'III'][level - 1] }}</option>
        </select>
      </label>
      <label class="check" :class="{ disabled: !canBrush }" :title="canBrush ? '' : 'Only claw, tooth, rib and limb fossils come from brushing'">
        <input v-model="brushed" type="checkbox" :disabled="!canBrush" />
        Brushed at a dig site
      </label>
    </div>

    <div class="result">
      <div class="item">
        <ItemSlot :id="fossilItem(part.id, true)" size="lg" />
      </div>
      <dl>
        <div>
          <dt>Completeness</dt>
          <dd>{{ percent(low) }}–{{ percent(high) }}</dd>
        </div>
        <div>
          <dt>Identify fail chance</dt>
          <dd>{{ percent(part.identifyFailChance) }} <span class="ac-muted">(new species only)</span></dd>
        </div>
        <div>
          <dt>After a failed identify</dt>
          <dd>from {{ percent(lowAfterFail) }}</dd>
        </div>
        <div>
          <dt>DNA sample</dt>
          <dd>
            <template v-if="dnaLow?.id === dnaHigh?.id">{{ dnaHigh?.name ?? 'None' }}</template>
            <template v-else>{{ dnaLow?.name ?? 'None' }} to {{ dnaHigh?.name ?? 'None' }}</template>
          </dd>
        </div>
      </dl>
    </div>

    <div class="scale" aria-hidden="true">
      <div class="zones">
        <span
          v-for="zone in zones"
          :key="zone.id"
          :class="['zone', zone.id]"
          :style="{ left: percent(zone.from, 1), width: percent(zone.to - zone.from, 1) }"
          :title="zone.name"
        />
        <span class="span" :style="{ left: percent(clamp(scoreLow), 1), width: `max(4px, ${percent(clamp(scoreHigh) - clamp(scoreLow), 1)})` }" />
      </div>
      <div class="labels">
        <span v-for="zone in zones" :key="zone.id" :style="{ left: percent(zone.from, 1) }">{{ percent(zone.from) }}</span>
        <span style="left: 100%">100%</span>
      </div>
    </div>
    <p class="legend">
      DNA integrity score = completeness {{ part.dnaBonus >= 0 ? '+' : '−' }} {{ percent(Math.abs(part.dnaBonus)) }} ({{ part.name }} bonus). The
      highlighted band is where this fossil lands.
    </p>
  </div>
</template>

<style scoped>
.ac-calc {
  margin: 16px 0;
  padding: 16px;
  border: 1px solid var(--vp-c-divider);
  border-radius: 12px;
  background: var(--vp-c-bg-soft);
}

.controls {
  display: flex;
  flex-wrap: wrap;
  gap: 10px 18px;
  align-items: center;
  margin-bottom: 16px;
  font-size: 14px;
}

.controls label {
  display: flex;
  align-items: center;
  gap: 8px;
}

.controls select {
  padding: 4px 8px;
  border: 1px solid var(--vp-c-divider);
  border-radius: 6px;
  background: var(--vp-c-bg);
}

.controls select:disabled,
.check.disabled {
  opacity: 0.5;
}

.result {
  display: flex;
  gap: 18px;
  align-items: center;
  flex-wrap: wrap;
}

dl {
  flex: 1;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(170px, 1fr));
  gap: 10px 18px;
  margin: 0;
}

dt {
  font-size: 12px;
  text-transform: uppercase;
  letter-spacing: 0.04em;
  color: var(--vp-c-text-3);
}

dd {
  margin: 0;
  font-weight: 600;
}

.scale {
  margin: 20px 4px 4px;
}

.zones {
  position: relative;
  height: 14px;
  border-radius: 7px;
  overflow: hidden;
}

.zone {
  position: absolute;
  top: 0;
  bottom: 0;
}

.zone.none {
  background: var(--vp-c-default-soft);
}

.zone.degraded {
  background: rgba(180, 83, 9, 0.35);
}

.zone.partial {
  background: rgba(202, 138, 4, 0.4);
}

.zone.stable {
  background: rgba(22, 163, 74, 0.4);
}

.zone.preserved_embryo {
  background: rgba(13, 148, 136, 0.5);
}

.span {
  position: absolute;
  top: 2px;
  bottom: 2px;
  border-radius: 5px;
  background: var(--vp-c-brand-1);
  box-shadow: 0 0 0 2px var(--vp-c-bg);
  transition:
    left 0.25s,
    width 0.25s;
}

.labels {
  position: relative;
  height: 18px;
  font: 11px/18px var(--vp-font-family-mono);
  color: var(--vp-c-text-3);
}

.labels span {
  position: absolute;
  transform: translateX(-50%);
}

.labels span:first-child {
  transform: none;
}

.labels span:last-child {
  transform: translateX(-100%);
}

.legend {
  margin: 8px 0 0;
  font-size: 13px;
  color: var(--vp-c-text-2);
}
</style>
