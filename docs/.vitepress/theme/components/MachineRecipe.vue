<script setup lang="ts">
import { computed } from 'vue'
import { data, itemName, seconds } from '../ancientcreature'
import ItemSlot from './ItemSlot.vue'

/**
 * One machine step: the machine, what goes in, what comes out and how long it takes.
 * Slots are written as "item id" or "item id|tooltip line".
 */
const props = defineProps<{ machine: string; inputs: string[]; outputs: string[]; time?: number; config?: string; note?: string }>()

const parse = (slot: string) => {
  const [id, ...lore] = slot.split('|')
  return { id, lore }
}
const inputSlots = computed(() => props.inputs.map(parse))
const outputSlots = computed(() => props.outputs.map(parse))
// A config key wins over a fixed time, so the card shows the default the mod actually ships.
const ticks = computed(() => (props.config ? data.config.find((c) => c.key === props.config)?.default : undefined) ?? props.time)
</script>

<template>
  <div class="ac-machine">
    <div class="station">
      <ItemSlot :id="machine" size="lg" />
      <span class="name">{{ itemName(machine) }}</span>
    </div>
    <div class="flow">
      <span class="slots">
        <ItemSlot v-for="slot in inputSlots" :id="slot.id" :key="slot.id" :lore="slot.lore" />
      </span>
      <span class="arrow">
        <span v-if="ticks" class="time">{{ seconds(ticks) }}</span>
        <span class="glyph">➜</span>
      </span>
      <span class="slots">
        <ItemSlot v-for="slot in outputSlots" :id="slot.id" :key="slot.id" :lore="slot.lore" />
      </span>
    </div>
    <p v-if="note" class="note">{{ note }}</p>
  </div>
</template>

<style scoped>
.ac-machine {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 12px 24px;
  margin: 16px 0;
  padding: 14px 16px;
  border: 1px solid var(--vp-c-divider);
  border-left: 3px solid var(--ac-accent);
  border-radius: 10px;
  background: var(--vp-c-bg-soft);
}

.station {
  display: flex;
  align-items: center;
  gap: 12px;
  min-width: 200px;
}

.name {
  font-weight: 600;
}

.flow {
  display: flex;
  align-items: center;
  gap: 12px;
}

.slots {
  display: flex;
  flex-wrap: wrap;
}

.arrow {
  display: flex;
  flex-direction: column;
  align-items: center;
  line-height: 1;
  color: var(--vp-c-text-2);
}

.glyph {
  font-size: 22px;
}

.time {
  font: 600 11px/1.4 var(--vp-font-family-mono);
}

.note {
  flex-basis: 100%;
  margin: 0 !important;
  font-size: 13px;
  color: var(--vp-c-text-2);
}
</style>
