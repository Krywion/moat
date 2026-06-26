<script setup lang="ts">
import { useRouter } from 'vue-router'
import DataTable from 'primevue/datatable'
import Column from 'primevue/column'
import Tag from 'primevue/tag'

import { formatPercent } from '@/utils/format'
import type { CompanySummaryResponse } from '@/types/api'

defineProps<{
  companies: CompanySummaryResponse[]
}>()

const router = useRouter()

function handleRowClick(event: { data: CompanySummaryResponse }): void {
  void router.push({ name: 'company-detail', params: { id: event.data.id } })
}
</script>

<template>
  <DataTable
    :value="companies"
    class="company-list"
    @row-click="handleRowClick"
    :pt="{
      root: { class: 'company-list__table' },
      header: { class: 'company-list__header' },
      bodyRow: { class: 'company-list__row' },
    }"
  >
    <Column field="name" header="Spółka">
      <template #body="{ data }">
        <div class="company-list__name-cell">
          <span class="company-list__name">{{ data.name }}</span>
          <Tag v-if="data.ticker" :value="data.ticker" severity="secondary" class="company-list__ticker" />
        </div>
      </template>
    </Column>
    <Column field="latestFiscalYear" header="Rok">
      <template #body="{ data }">
        {{ data.latestFiscalYear ?? '—' }}
      </template>
    </Column>
    <Column field="netMargin" header="Marża netto">
      <template #body="{ data }">
        {{ formatPercent(data.netMargin) }}
      </template>
    </Column>
  </DataTable>
</template>

<style scoped>
.company-list :deep(.company-list__table) {
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 0.75rem;
  overflow: hidden;
  background: rgba(15, 23, 42, 0.72);
}

.company-list :deep(.p-datatable-thead > tr > th) {
  background: rgba(15, 23, 42, 0.95);
  color: #94a3b8;
  border-color: rgba(255, 255, 255, 0.08);
  font-weight: 600;
  font-size: 0.85rem;
  text-transform: uppercase;
  letter-spacing: 0.04em;
}

.company-list :deep(.p-datatable-tbody > tr) {
  background: rgba(15, 23, 42, 0.72);
  cursor: pointer;
  transition: background-color 0.15s ease;
}

.company-list :deep(.p-datatable-tbody > tr > td) {
  background: rgba(15, 23, 42, 0.72);
  color: #e2e8f0;
  border-color: rgba(255, 255, 255, 0.06);
  transition: background-color 0.15s ease;
}

.company-list :deep(.p-datatable-tbody > tr:nth-child(even) > td) {
  background: rgba(255, 255, 255, 0.04);
}

.company-list :deep(.p-datatable-tbody > tr:hover),
.company-list :deep(.p-datatable-tbody > tr:hover > td),
.company-list :deep(.p-datatable-tbody > tr.p-row-hover),
.company-list :deep(.p-datatable-tbody > tr.p-row-hover > td) {
  background: rgba(30, 41, 59, 0.98) !important;
  color: #f1f5f9;
}

.company-list__name-cell {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  flex-wrap: wrap;
}

.company-list__name {
  font-weight: 600;
  color: #f8fafc;
}

.company-list__ticker {
  font-size: 0.75rem;
}
</style>
