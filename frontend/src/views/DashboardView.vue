<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useToast } from 'primevue/usetoast'
import Button from 'primevue/button'
import Card from 'primevue/card'
import Message from 'primevue/message'
import ProgressSpinner from 'primevue/progressspinner'
import Toast from 'primevue/toast'

import AddCompanyDialog from '@/components/AddCompanyDialog.vue'
import CompanyList from '@/components/CompanyList.vue'
import { useAuthStore } from '@/stores/auth'
import { useCompaniesStore } from '@/stores/companies'

const router = useRouter()
const authStore = useAuthStore()
const companiesStore = useCompaniesStore()
const toast = useToast()

const addDialogVisible = ref(false)

onMounted(() => {
  void companiesStore.loadCompanies()
})

async function handleLogout(): Promise<void> {
  await authStore.logout()
  await router.push({ name: 'auth' })
}

function openAddDialog(): void {
  addDialogVisible.value = true
}

function handleCompanyCreated(): void {
  toast.add({
    severity: 'success',
    summary: 'Spółka dodana',
    detail: 'Analiza została przygotowana i pojawi się na liście.',
    life: 4000,
  })
}
</script>

<template>
  <div class="dashboard">
    <Toast />

    <header class="dashboard__header">
      <div class="dashboard__brand">moat</div>
      <div class="dashboard__user">
        <span class="dashboard__email">{{ authStore.user?.email }}</span>
        <Button
          label="Wyloguj"
          icon="pi pi-sign-out"
          severity="secondary"
          outlined
          @click="handleLogout"
        />
      </div>
    </header>

    <main class="dashboard__main">
      <div v-if="companiesStore.status === 'loading'" class="dashboard__loading">
        <ProgressSpinner />
      </div>

      <Message v-else-if="companiesStore.status === 'error'" severity="error" :closable="false">
        <div class="dashboard__error">
          <span>{{ companiesStore.errorMessage }}</span>
          <Button
            label="Spróbuj ponownie"
            icon="pi pi-refresh"
            severity="secondary"
            outlined
            size="small"
            @click="companiesStore.loadCompanies()"
          />
        </div>
      </Message>

      <template v-else-if="companiesStore.isEmpty">
        <Card class="dashboard__empty">
          <template #title>Nie masz jeszcze żadnych spółek</template>
          <template #content>
            <p class="dashboard__copy">
              Dodaj pierwszą spółkę, aby rozpocząć analizę fundamentalną. Wrzuć raport ESEF lub
              wprowadź dane finansowe ręcznie — system policzy wskaźniki i pokaże gotową analizę.
            </p>
            <Button label="Dodaj spółkę" icon="pi pi-plus" @click="openAddDialog" />
          </template>
        </Card>
      </template>

      <template v-else>
        <div class="dashboard__toolbar">
          <h1 class="dashboard__title">Twoje spółki</h1>
          <Button label="Dodaj spółkę" icon="pi pi-plus" @click="openAddDialog" />
        </div>

        <CompanyList :companies="companiesStore.companies" />
      </template>
    </main>

    <AddCompanyDialog v-model:visible="addDialogVisible" @created="handleCompanyCreated" />
  </div>
</template>

<style scoped>
.dashboard {
  min-height: 100vh;
  background: linear-gradient(180deg, #0b1220 0%, #111827 100%);
  color: #e2e8f0;
}

.dashboard__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
  padding: 1rem 1.5rem;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
  background: rgba(15, 23, 42, 0.8);
  backdrop-filter: blur(8px);
}

.dashboard__brand {
  font-size: 1.35rem;
  font-weight: 700;
  letter-spacing: 0.06em;
  text-transform: lowercase;
  color: #f8fafc;
}

.dashboard__user {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.dashboard__email {
  color: #cbd5e1;
  font-size: 0.9rem;
}

.dashboard__main {
  max-width: 64rem;
  margin: 0 auto;
  padding: 2.5rem 1.5rem;
}

.dashboard__loading {
  display: flex;
  justify-content: center;
  padding: 4rem 0;
}

.dashboard__error {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
  flex-wrap: wrap;
}

.dashboard__empty {
  border: 1px solid rgba(255, 255, 255, 0.08);
  background: rgba(15, 23, 42, 0.72);
  color: #e2e8f0;
}

.dashboard__empty :deep(.p-card-title) {
  color: #f8fafc !important;
  font-weight: 600;
}

.dashboard__copy {
  margin: 0 0 1.25rem;
  line-height: 1.6;
  color: #e2e8f0;
}

.dashboard__toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.dashboard__title {
  margin: 0;
  font-size: 1.5rem;
  font-weight: 700;
  color: #f8fafc;
}

@media (max-width: 640px) {
  .dashboard__header {
    flex-direction: column;
    align-items: flex-start;
  }

  .dashboard__user {
    width: 100%;
    justify-content: space-between;
  }

  .dashboard__toolbar {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
