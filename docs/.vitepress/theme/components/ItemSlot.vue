<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { itemIcon, itemName } from '../ancientcreature'

const props = withDefaults(
  defineProps<{ id?: string | null; count?: number; label?: boolean; size?: 'sm' | 'md' | 'lg'; lore?: string | string[] }>(),
  { id: null, count: 1, label: false, size: 'md', lore: () => [] },
)

const name = computed(() => (props.id ? itemName(props.id) : ''))
const src = computed(() => (props.id ? itemIcon(props.id) : null))
const loreLines = computed(() => [props.lore].flat().filter(Boolean))

// Falls back to initials when an item has no icon or the hosted icon fails to load.
const failed = ref(false)
watch(src, () => (failed.value = false))
// The tooltip floats above the page (teleported to <body>) so tables and scrolling cards don't clip it.
const mounted = ref(false)
onMounted(() => (mounted.value = true))
const tooltip = ref<{ x: number; y: number } | null>(null)
function showTooltip(event: Event) {
  if (!props.id) return
  const rect = (event.currentTarget as HTMLElement).getBoundingClientRect()
  tooltip.value = { x: rect.left + rect.width / 2, y: rect.top }
}
const hideTooltip = () => (tooltip.value = null)

const initials = computed(() =>
  name.value
    .split(' ')
    .filter((word) => /^[A-Z]/.test(word))
    .slice(0, 2)
    .map((word) => word[0])
    .join(''),
)
</script>

<template>
  <span class="ac-item" :class="[`size-${size}`, { 'with-label': label }]">
    <span
      class="ac-slot"
      :aria-label="name"
      role="img"
      :tabindex="id ? 0 : undefined"
      @mouseenter="showTooltip"
      @mouseleave="hideTooltip"
      @focus="showTooltip"
      @blur="hideTooltip"
    >
      <img v-if="src && !failed" class="pixelated" :src="src" alt="" loading="lazy" @error="failed = true" />
      <span v-else-if="id" class="ac-initials">{{ initials }}</span>
      <span v-if="count > 1" class="ac-count">{{ count }}</span>
    </span>
    <Teleport v-if="mounted && tooltip" to="body">
      <span class="ac-tooltip" role="tooltip" :style="{ left: `${tooltip.x}px`, top: `${tooltip.y}px` }">
        <span class="ac-tooltip-name">{{ name }}</span>
        <span v-for="line in loreLines" :key="line" class="ac-tooltip-lore">{{ line }}</span>
        <span class="ac-tooltip-id">{{ id }}</span>
      </span>
    </Teleport>
    <span v-if="label && id" class="ac-label">{{ name }}</span>
  </span>
</template>

<style scoped>
.ac-item {
  --slot: 36px;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  vertical-align: middle;
}

.ac-item.size-sm {
  --slot: 26px;
  gap: 6px;
}

.ac-item.size-lg {
  --slot: 56px;
  gap: 12px;
}

.ac-slot {
  position: relative;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: var(--slot);
  height: var(--slot);
  flex: none;
  background: var(--ac-slot-bg);
  border: 2px solid;
  border-color: var(--ac-slot-dark) var(--ac-slot-light) var(--ac-slot-light) var(--ac-slot-dark);
  outline: none;
}

.ac-slot:hover,
.ac-slot:focus-visible {
  background: var(--ac-slot-hover);
}

.ac-slot img {
  width: calc(var(--slot) - 4px);
  height: calc(var(--slot) - 4px);
}

.ac-initials {
  font: 600 12px/1 var(--vp-font-family-mono);
  color: #fff;
  text-shadow: 1px 1px 0 #3f3f3f;
}

.size-sm .ac-initials {
  font-size: 10px;
}

.ac-count {
  position: absolute;
  right: 1px;
  bottom: -1px;
  font: 700 12px/1 var(--vp-font-family-mono);
  color: #fff;
  text-shadow: 1px 1px 0 #3f3f3f;
}

/* Minecraft's item tooltip: near-black panel with a purple gradient border. */
.ac-tooltip {
  position: fixed;
  z-index: 100;
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-width: max-content;
  max-width: 280px;
  padding: 5px 8px;
  transform: translate(-50%, calc(-100% - 8px));
  background: rgba(16, 0, 16, 0.94);
  border: 2px solid transparent;
  border-radius: 3px;
  background-clip: padding-box;
  box-shadow:
    0 0 0 1px rgba(16, 0, 16, 0.94),
    inset 0 0 0 1px rgba(80, 0, 255, 0.35);
  font: 500 13px/1.35 var(--vp-font-family-base);
  text-align: left;
  white-space: nowrap;
  pointer-events: none;
}

.ac-tooltip-name {
  color: #fff;
}

.ac-tooltip-lore {
  color: #aaa;
  white-space: normal;
}

.ac-tooltip-id {
  color: #8a8a8a;
  font: 11px/1.3 var(--vp-font-family-mono);
}

.ac-label {
  font-weight: 500;
}
</style>
