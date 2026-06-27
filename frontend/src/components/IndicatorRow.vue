<script setup lang="ts">
import Button from 'primevue/button'
import Popover from 'primevue/popover'
import { ref } from 'vue'

import type { IndicatorFormat } from '@/data/indicators'
import { formatCurrency, formatNumber, formatPercent, formatRatio } from '@/utils/format'

const props = defineProps<{
  label: string
  tooltip: string
  value: number | null | undefined
  format: IndicatorFormat
  currency?: string
}>()

const popoverRef = ref<InstanceType<typeof Popover> | null>(null)

function formattedValue(): string {
  switch (props.format) {
    case 'currency':
      return formatCurrency(props.value, props.currency ?? 'PLN')
    case 'percent':
      return formatPercent(props.value)
    case 'ratio':
      return formatRatio(props.value)
    default:
      return formatNumber(props.value)
  }
}

function togglePopover(event: Event): void {
  popoverRef.value?.toggle(event)
}
</script>

<template>
  <div class="indicator-row">
    <div class="indicator-row__label-wrap">
      <span class="indicator-row__label">{{ label }}</span>
      <Button
        type="button"
        icon="pi pi-question-circle"
        severity="secondary"
        text
        rounded
        size="small"
        class="indicator-row__help"
        aria-label="Wyjaśnienie wskaźnika"
        @click="togglePopover"
      />
      <Popover ref="popoverRef">
        <p class="indicator-row__tooltip">{{ tooltip }}</p>
      </Popover>
    </div>
    <span class="indicator-row__value">{{ formattedValue() }}</span>
  </div>
</template>

<style scoped>
.indicator-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
  padding: 0.75rem 0;
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
}

.indicator-row__label-wrap {
  display: flex;
  align-items: center;
  gap: 0.25rem;
  min-width: 0;
}

.indicator-row__label {
  color: #cbd5e1;
  font-size: 0.9rem;
}

.indicator-row__help {
  flex-shrink: 0;
  width: 1.75rem;
  height: 1.75rem;
}

.indicator-row__tooltip {
  margin: 0;
  max-width: 16rem;
  line-height: 1.5;
  font-size: 0.875rem;
}

.indicator-row__value {
  font-weight: 600;
  color: #f8fafc;
  white-space: nowrap;
}
</style>
