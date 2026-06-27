<script setup lang="ts">
import Card from 'primevue/card'

import IndicatorRow from '@/components/IndicatorRow.vue'
import {
  HEALTH_COMPUTED_KEYS,
  HEALTH_INDICATORS,
  HEALTH_RAW_KEYS,
} from '@/data/indicators'
import type { FinancialReportResponse } from '@/types/api'

defineProps<{
  report: FinancialReportResponse
}>()
</script>

<template>
  <Card class="health-section">
    <template #title>Kondycja firmy</template>
    <template #content>
      <p class="health-section__subtitle">Dane ze sprawozdania</p>
      <div class="health-section__grid">
        <IndicatorRow
          v-for="key in HEALTH_RAW_KEYS"
          :key="key"
          :label="HEALTH_INDICATORS[key]!.label"
          :tooltip="HEALTH_INDICATORS[key]!.tooltip"
          :format="HEALTH_INDICATORS[key]!.format"
          :value="report[key]"
          :currency="report.currency"
        />
      </div>

      <p class="health-section__subtitle health-section__subtitle--computed">Wskaźniki</p>
      <div class="health-section__grid">
        <IndicatorRow
          v-for="key in HEALTH_COMPUTED_KEYS"
          :key="key"
          :label="HEALTH_INDICATORS[key]!.label"
          :tooltip="HEALTH_INDICATORS[key]!.tooltip"
          :format="HEALTH_INDICATORS[key]!.format"
          :value="report[key]"
          :currency="report.currency"
        />
      </div>
    </template>
  </Card>
</template>

<style scoped>
.health-section {
  border: 1px solid rgba(255, 255, 255, 0.08);
  background: rgba(15, 23, 42, 0.72);
  color: #e2e8f0;
}

.health-section :deep(.p-card-title) {
  color: #f8fafc !important;
  font-weight: 600;
}

.health-section__subtitle {
  margin: 0 0 0.5rem;
  font-size: 0.8rem;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.04em;
  color: #cbd5e1;
}

.health-section__subtitle--computed {
  margin-top: 1.5rem;
}

.health-section__grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 0 1.5rem;
}

@media (max-width: 768px) {
  .health-section__grid {
    grid-template-columns: 1fr;
  }
}
</style>
