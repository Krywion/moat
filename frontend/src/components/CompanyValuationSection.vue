<script setup lang="ts">
import Button from 'primevue/button'
import Card from 'primevue/card'
import Message from 'primevue/message'

import IndicatorRow from '@/components/IndicatorRow.vue'
import { VALUATION_INDICATORS, VALUATION_KEYS } from '@/data/indicators'
import type { FinancialReportResponse } from '@/types/api'

defineProps<{
  report: FinancialReportResponse
  refreshing?: boolean
}>()

const emit = defineEmits<{
  refresh: []
}>()

function hasMarketData(report: FinancialReportResponse): boolean {
  return VALUATION_KEYS.some((key) => report[key] != null)
}
</script>

<template>
  <Card class="valuation-section">
    <template #title>
      <div class="valuation-section__title-row">
        <span>Wycena</span>
        <Button
          label="Odśwież dane rynkowe"
          icon="pi pi-refresh"
          severity="secondary"
          outlined
          size="small"
          :loading="refreshing"
          @click="emit('refresh')"
        />
      </div>
    </template>
    <template #content>
      <Message v-if="!hasMarketData(report)" severity="info" :closable="false">
        Brak danych rynkowych. Upewnij się, że spółka ma ticker, a następnie odśwież dane.
      </Message>

      <div class="valuation-section__grid">
        <IndicatorRow
          v-for="key in VALUATION_KEYS"
          :key="key"
          :label="VALUATION_INDICATORS[key]!.label"
          :tooltip="VALUATION_INDICATORS[key]!.tooltip"
          :format="VALUATION_INDICATORS[key]!.format"
          :value="report[key]"
          :currency="report.currency"
        />
      </div>
    </template>
  </Card>
</template>

<style scoped>
.valuation-section {
  border: 1px solid rgba(255, 255, 255, 0.08);
  background: rgba(15, 23, 42, 0.72);
  color: #e2e8f0;
}

.valuation-section :deep(.p-card-title) {
  color: #f8fafc !important;
  font-weight: 600;
}

.valuation-section__title-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
  flex-wrap: wrap;
  width: 100%;
  color: #f8fafc;
}

.valuation-section__grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 0 1.5rem;
}

@media (max-width: 768px) {
  .valuation-section__title-row {
    flex-direction: column;
    align-items: flex-start;
  }

  .valuation-section__grid {
    grid-template-columns: 1fr;
  }
}
</style>
