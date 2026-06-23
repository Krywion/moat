<script setup lang="ts">
import { useRouter } from 'vue-router'
import Button from 'primevue/button'
import Card from 'primevue/card'

import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

async function handleLogout(): Promise<void> {
  await authStore.logout()
  await router.push({ name: 'auth' })
}
</script>

<template>
  <div class="dashboard">
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
      <Card class="dashboard__empty">
        <template #title>Nie masz jeszcze żadnych spółek</template>
        <template #content>
          <p class="dashboard__copy">
            Dodaj pierwszą spółkę, aby rozpocząć analizę fundamentalną. Wrzuć raport ESEF lub
            wprowadź dane finansowe ręcznie — system policzy wskaźniki i pokaże gotową analizę.
          </p>
          <Button label="Dodaj spółkę" icon="pi pi-plus" disabled />
        </template>
      </Card>
    </main>
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
  color: #94a3b8;
  font-size: 0.9rem;
}

.dashboard__main {
  max-width: 48rem;
  margin: 0 auto;
  padding: 2.5rem 1.5rem;
}

.dashboard__empty {
  border: 1px solid rgba(255, 255, 255, 0.08);
  background: rgba(15, 23, 42, 0.72);
}

.dashboard__copy {
  margin: 0 0 1.25rem;
  line-height: 1.6;
  color: #cbd5e1;
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
}
</style>
