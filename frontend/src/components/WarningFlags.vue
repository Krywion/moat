<script setup lang="ts">
import Message from 'primevue/message'

import { WARNING_FLAG_LABELS } from '@/data/indicators'

defineProps<{
  flags: string[]
}>()
</script>

<template>
  <div v-if="flags.length > 0" class="warning-flags">
    <Message
      v-for="flag in flags"
      :key="flag"
      severity="warn"
      :closable="false"
      class="warning-flags__item"
    >
      {{ WARNING_FLAG_LABELS[flag] ?? flag }}
    </Message>
  </div>
</template>

<style scoped>
.warning-flags {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
  margin-bottom: 1.5rem;
}

.warning-flags__item {
  margin: 0;
}
</style>
