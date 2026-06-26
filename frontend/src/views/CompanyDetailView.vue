<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useToast } from 'primevue/usetoast'
import Button from 'primevue/button'
import Message from 'primevue/message'
import ProgressSpinner from 'primevue/progressspinner'
import Select from 'primevue/select'
import Tag from 'primevue/tag'
import Toast from 'primevue/toast'

import CompanyHealthSection from '@/components/CompanyHealthSection.vue'
import CompanyValuationSection from '@/components/CompanyValuationSection.vue'
import EditFinancialsDialog from '@/components/EditFinancialsDialog.vue'
import WarningFlags from '@/components/WarningFlags.vue'
import * as companiesApi from '@/api/companies'
import { useAuthStore } from '@/stores/auth'
import { ApiRequestError, type CompanyDetailResponse } from '@/types/api'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const toast = useToast()

type LoadStatus = 'loading' | 'ready' | 'not-found' | 'error'

const company = ref<CompanyDetailResponse | null>(null)
const status = ref<LoadStatus>('loading')
const errorMessage = ref<string | null>(null)
const selectedYear = ref<number | null>(null)
const refreshingMarket = ref(false)
const editDialogVisible = ref(false)
const editDialogMode = ref<'edit' | 'add'>('edit')

const companyId = computed(() => route.params.id as string)

const sortedReports = computed(() => {
  if (!company.value) {
    return []
  }

  return [...company.value.reports].sort((a, b) => b.fiscalYear - a.fiscalYear)
})

const yearOptions = computed(() =>
  sortedReports.value.map((report) => ({
    label: String(report.fiscalYear),
    value: report.fiscalYear,
  })),
)

const selectedReport = computed(() => {
  if (!selectedYear.value) {
    return sortedReports.value[0] ?? null
  }

  return sortedReports.value.find((report) => report.fiscalYear === selectedYear.value) ?? null
})

async function loadCompany(): Promise<void> {
  status.value = 'loading'
  errorMessage.value = null

  try {
    company.value = await companiesApi.getCompany(companyId.value)
    const latest = sortedReports.value[0]
    selectedYear.value = latest?.fiscalYear ?? null
    status.value = 'ready'
  } catch (error) {
    company.value = null
    if (error instanceof ApiRequestError && error.status === 404) {
      status.value = 'not-found'
      return
    }

    status.value = 'error'
    errorMessage.value = getErrorMessage(error)
  }
}

async function handleRefreshMarket(): Promise<void> {
  refreshingMarket.value = true
  try {
    company.value = await companiesApi.refreshMarket(companyId.value)
    toast.add({
      severity: 'success',
      summary: 'Dane rynkowe odświeżone',
      life: 3000,
    })
  } catch (error) {
    toast.add({
      severity: 'error',
      summary: 'Nie udało się odświeżyć danych',
      detail: getErrorMessage(error),
      life: 5000,
    })
  } finally {
    refreshingMarket.value = false
  }
}

function openEditDialog(mode: 'edit' | 'add'): void {
  editDialogMode.value = mode
  editDialogVisible.value = true
}

async function handleFinancialsSaved(): Promise<void> {
  const previousYear = selectedYear.value

  try {
    company.value = await companiesApi.getCompany(companyId.value)
    const years = sortedReports.value.map((report) => report.fiscalYear)
    selectedYear.value =
      previousYear != null && years.includes(previousYear)
        ? previousYear
        : (sortedReports.value[0]?.fiscalYear ?? null)

    toast.add({
      severity: 'success',
      summary: 'Dane finansowe zapisane',
      life: 3000,
    })
  } catch (error) {
    toast.add({
      severity: 'error',
      summary: 'Nie udało się odświeżyć danych spółki',
      detail: getErrorMessage(error),
      life: 5000,
    })
  }
}

function getErrorMessage(error: unknown): string {
  if (error instanceof ApiRequestError) {
    return error.message
  }

  if (error instanceof Error) {
    return error.message
  }

  return 'Wystąpił nieoczekiwany błąd.'
}

async function handleLogout(): Promise<void> {
  await authStore.logout()
  await router.push({ name: 'auth' })
}

function goToDashboard(): void {
  void router.push({ name: 'dashboard' })
}

onMounted(() => {
  void loadCompany()
})

watch(companyId, () => {
  void loadCompany()
})
</script>

<template>
  <div class="company-detail">
    <Toast />

    <header class="company-detail__header">
      <div class="company-detail__header-left">
        <Button
          label="Powrót"
          icon="pi pi-arrow-left"
          severity="secondary"
          text
          @click="goToDashboard"
        />
        <div class="company-detail__brand">moat</div>
      </div>
      <div class="company-detail__user">
        <span class="company-detail__email">{{ authStore.user?.email }}</span>
        <Button
          label="Wyloguj"
          icon="pi pi-sign-out"
          severity="secondary"
          outlined
          @click="handleLogout"
        />
      </div>
    </header>

    <main class="company-detail__main">
      <div v-if="status === 'loading'" class="company-detail__loading">
        <ProgressSpinner />
      </div>

      <template v-else-if="status === 'not-found'">
        <Message severity="warn" :closable="false">
          Spółka nie znaleziona lub nie masz do niej dostępu.
        </Message>
        <Button
          label="Wróć do panelu"
          icon="pi pi-arrow-left"
          class="company-detail__back-btn"
          @click="goToDashboard"
        />
      </template>

      <Message v-else-if="status === 'error'" severity="error" :closable="false">
        <div class="company-detail__error">
          <span>{{ errorMessage }}</span>
          <Button
            label="Spróbuj ponownie"
            icon="pi pi-refresh"
            severity="secondary"
            outlined
            size="small"
            @click="loadCompany"
          />
        </div>
      </Message>

      <template v-else-if="company && selectedReport">
        <div class="company-detail__hero">
          <div>
            <h1 class="company-detail__title">{{ company.name }}</h1>
            <div class="company-detail__meta">
              <Tag v-if="company.ticker" :value="company.ticker" severity="secondary" />
              <span class="company-detail__year-label">Rok obrotowy:</span>
              <Select
                v-if="yearOptions.length > 1"
                v-model="selectedYear"
                :options="yearOptions"
                option-label="label"
                option-value="value"
                class="company-detail__year-select"
              />
              <span v-else class="company-detail__year-static">{{ selectedReport.fiscalYear }}</span>
            </div>
          </div>
          <div class="company-detail__actions">
            <Button
              label="Edytuj dane"
              icon="pi pi-pencil"
              severity="secondary"
              outlined
              @click="openEditDialog('edit')"
            />
            <Button
              label="Dodaj rok"
              icon="pi pi-plus"
              severity="secondary"
              outlined
              @click="openEditDialog('add')"
            />
          </div>
        </div>

        <EditFinancialsDialog
          v-model:visible="editDialogVisible"
          :company-id="companyId"
          :mode="editDialogMode"
          :report="selectedReport"
          :latest-report="sortedReports[0] ?? null"
          @saved="handleFinancialsSaved"
        />

        <WarningFlags :flags="selectedReport.flags" />

        <div class="company-detail__sections">
          <CompanyHealthSection :report="selectedReport" />
          <CompanyValuationSection
            :report="selectedReport"
            :refreshing="refreshingMarket"
            @refresh="handleRefreshMarket"
          />
        </div>
      </template>
    </main>
  </div>
</template>

<style scoped>
.company-detail {
  min-height: 100vh;
  background: linear-gradient(180deg, #0b1220 0%, #111827 100%);
  color: #e2e8f0;
}

.company-detail__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
  padding: 1rem 1.5rem;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
  background: rgba(15, 23, 42, 0.8);
  backdrop-filter: blur(8px);
}

.company-detail__header-left {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.company-detail__brand {
  font-size: 1.35rem;
  font-weight: 700;
  letter-spacing: 0.06em;
  text-transform: lowercase;
  color: #f8fafc;
}

.company-detail__user {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.company-detail__email {
  color: #94a3b8;
  font-size: 0.9rem;
}

.company-detail__main {
  max-width: 64rem;
  margin: 0 auto;
  padding: 2.5rem 1.5rem;
}

.company-detail__loading {
  display: flex;
  justify-content: center;
  padding: 4rem 0;
}

.company-detail__error {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
  flex-wrap: wrap;
}

.company-detail__back-btn {
  margin-top: 1rem;
}

.company-detail__hero {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 1rem;
  flex-wrap: wrap;
  margin-bottom: 1.5rem;
}

.company-detail__actions {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  flex-wrap: wrap;
}

.company-detail__title {
  margin: 0 0 0.75rem;
  font-size: 1.75rem;
  font-weight: 700;
  color: #f8fafc;
}

.company-detail__meta {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  flex-wrap: wrap;
}

.company-detail__year-label {
  color: #94a3b8;
  font-size: 0.9rem;
}

.company-detail__year-select {
  min-width: 6rem;
}

.company-detail__year-static {
  font-weight: 600;
  color: #e2e8f0;
}

.company-detail__sections {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

@media (max-width: 640px) {
  .company-detail__header {
    flex-direction: column;
    align-items: flex-start;
  }

  .company-detail__user {
    width: 100%;
    justify-content: space-between;
  }
}
</style>
