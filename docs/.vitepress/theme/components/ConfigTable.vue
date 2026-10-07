<script setup lang="ts">
import { data, seconds } from '../ancientcreature'
</script>

<template>
  <table>
    <thead>
      <tr>
        <th>Key</th>
        <th>Default</th>
        <th>What it does</th>
      </tr>
    </thead>
    <tbody>
      <tr v-for="value in data.config" :key="value.key">
        <td><code>{{ value.key }}</code></td>
        <td>
          <template v-if="value.type === 'ticks'">{{ value.default }} <span class="ac-muted">({{ seconds(value.default as number) }})</span></template>
          <template v-else-if="value.type === 'multiplier'">{{ value.default }}×</template>
          <template v-else-if="value.type === 'list'"><code>[]</code></template>
          <template v-else><code v-if="value.type === 'boolean'">{{ value.default }}</code><template v-else>{{ value.default }}</template></template>
        </td>
        <td>{{ value.comment }}</td>
      </tr>
    </tbody>
  </table>
</template>
