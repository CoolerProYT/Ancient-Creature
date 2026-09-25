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
          <template v-if="value.type === 'int'">{{ value.default }} <span class="ac-muted">({{ seconds(value.default) }})</span></template>
          <template v-else>{{ value.default }}×</template>
        </td>
        <td>{{ value.comment }}</td>
      </tr>
    </tbody>
  </table>
</template>
