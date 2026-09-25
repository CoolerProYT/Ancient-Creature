<script setup lang="ts">
import { computed } from 'vue'
import { data, type Advancement } from '../ancientcreature'
import ItemSlot from './ItemSlot.vue'

interface Node {
  advancement: Advancement
  /** Only branches indent, so a long single chain reads as one timeline instead of a staircase. */
  depth: number
}

/** Depth-first order so each advancement appears under its parent. */
const nodes = computed<Node[]>(() => {
  const children = new Map<string | null, Advancement[]>()
  const ids = new Set(data.advancements.map((a) => a.id))
  for (const advancement of data.advancements) {
    const parent = advancement.parent && ids.has(advancement.parent) ? advancement.parent : null
    children.set(parent, [...(children.get(parent) ?? []), advancement])
  }
  const result: Node[] = []
  const visit = (parent: string | null, depth: number) => {
    const siblings = children.get(parent) ?? []
    siblings.forEach((advancement, index) => {
      const branchDepth = depth + (siblings.length > 1 && index > 0 ? 1 : 0)
      result.push({ advancement, depth: branchDepth })
      visit(advancement.id, branchDepth)
    })
  }
  visit(null, 0)
  return result
})
</script>

<template>
  <ol class="ac-advancements">
    <li v-for="{ advancement, depth } in nodes" :key="advancement.id" :class="advancement.frame" :style="{ '--depth': depth }">
      <span class="icon" :class="advancement.frame"><ItemSlot :id="advancement.icon" /></span>
      <div class="text">
        <div class="title">
          {{ advancement.title }}
          <span v-if="advancement.frame !== 'task'" class="frame" :class="advancement.frame">{{ advancement.frame }}</span>
        </div>
        <div class="description">{{ advancement.description }}</div>
      </div>
    </li>
  </ol>
</template>

<style scoped>
.ac-advancements {
  position: relative;
  list-style: none;
  margin: 16px 0;
  padding: 0 !important;
}

.ac-advancements li {
  position: relative;
  display: flex;
  gap: 14px;
  align-items: center;
  margin: 0 !important;
  padding: 8px 0 8px calc(var(--depth) * 28px);
}

/* The connecting line between icons. */
.ac-advancements li::before {
  content: '';
  position: absolute;
  left: calc(var(--depth) * 28px + 21px);
  top: 0;
  bottom: 0;
  width: 2px;
  background: var(--vp-c-divider);
}

.ac-advancements li:first-child::before {
  top: 50%;
}

.ac-advancements li:last-child::before {
  bottom: 50%;
}

.icon {
  position: relative;
  z-index: 1;
  padding: 2px;
  border-radius: 6px;
  background: var(--vp-c-bg);
}

.icon.goal {
  box-shadow: 0 0 0 2px var(--vp-c-brand-1);
}

.icon.challenge {
  box-shadow: 0 0 0 2px var(--ac-accent);
}

.title {
  font-weight: 600;
}

.description {
  font-size: 14px;
  color: var(--vp-c-text-2);
}

.frame {
  margin-left: 6px;
  padding: 1px 8px;
  border-radius: 999px;
  font-size: 11px;
  font-weight: 600;
  text-transform: uppercase;
  background: var(--vp-c-brand-soft);
  color: var(--vp-c-brand-1);
}

.frame.challenge {
  background: var(--ac-accent-soft);
  color: var(--ac-accent);
}
</style>
