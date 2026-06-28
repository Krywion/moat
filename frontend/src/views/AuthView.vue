<script setup lang="ts">
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useToast } from 'primevue/usetoast'
import Button from 'primevue/button'
import Card from 'primevue/card'
import FloatLabel from 'primevue/floatlabel'
import IconField from 'primevue/iconfield'
import InputIcon from 'primevue/inputicon'
import InputText from 'primevue/inputtext'
import Message from 'primevue/message'
import Password from 'primevue/password'
import TabPanel from 'primevue/tabpanel'
import TabView from 'primevue/tabview'
import Toast from 'primevue/toast'

import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()
const toast = useToast()

const activeTab = ref(0)

const loginEmail = ref('')
const loginPassword = ref('')
const registerEmail = ref('')
const registerPassword = ref('')
const registerConfirmPassword = ref('')

const loginError = ref('')
const registerError = ref('')

const isSubmitting = computed(() => authStore.status === 'loading')

const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/

function validateEmail(email: string): string | null {
  if (!email.trim()) {
    return 'Podaj adres e-mail.'
  }

  if (!emailPattern.test(email)) {
    return 'Podaj poprawny adres e-mail.'
  }

  return null
}

function validateLogin(): string | null {
  const emailError = validateEmail(loginEmail.value)
  if (emailError) {
    return emailError
  }

  if (!loginPassword.value) {
    return 'Podaj hasło.'
  }

  return null
}

function validateRegister(): string | null {
  const emailError = validateEmail(registerEmail.value)
  if (emailError) {
    return emailError
  }

  if (registerPassword.value.length < 8) {
    return 'Hasło musi mieć co najmniej 8 znaków.'
  }

  if (registerPassword.value !== registerConfirmPassword.value) {
    return 'Hasła nie są identyczne.'
  }

  return null
}

async function handleLogin(): Promise<void> {
  loginError.value = ''

  const validationError = validateLogin()
  if (validationError) {
    loginError.value = validationError
    return
  }

  try {
    await authStore.login(loginEmail.value.trim(), loginPassword.value)
    await router.push({ name: 'dashboard' })
  } catch (error) {
    loginError.value = authStore.getErrorMessage(error)
  }
}

async function handleRegister(): Promise<void> {
  registerError.value = ''

  const validationError = validateRegister()
  if (validationError) {
    registerError.value = validationError
    return
  }

  try {
    const email = registerEmail.value.trim()
    await authStore.register(email, registerPassword.value)

    loginEmail.value = email
    loginPassword.value = ''
    registerPassword.value = ''
    registerConfirmPassword.value = ''
    activeTab.value = 0

    toast.add({
      severity: 'success',
      summary: 'Konto utworzone',
      detail: 'Zaloguj się, aby kontynuować.',
      life: 4000,
    })
  } catch (error) {
    registerError.value = authStore.getErrorMessage(error)
  }
}
</script>

<template>
  <div class="auth-page">
    <Toast />

    <div class="auth-page__glow" aria-hidden="true" />

    <div class="auth-page__content">
      <header class="auth-brand">
        <div class="auth-brand__mark">moat</div>
        <p class="auth-brand__tagline">Analizator spółek giełdowych</p>
      </header>

      <Card class="auth-card">
        <template #content>
          <TabView v-model:active-index="activeTab" class="auth-tabs">
            <TabPanel header="Logowanie" value="login">
              <form class="auth-form" @submit.prevent="handleLogin">
                <Message v-if="loginError" severity="error" :closable="false">
                  {{ loginError }}
                </Message>

                <FloatLabel variant="on">
                  <IconField>
                    <InputIcon class="pi pi-envelope" />
                    <InputText
                      id="login-email"
                      v-model="loginEmail"
                      type="email"
                      autocomplete="email"
                      class="auth-input"
                      :disabled="isSubmitting"
                    />
                  </IconField>
                  <label for="login-email">E-mail</label>
                </FloatLabel>

                <FloatLabel variant="on">
                  <Password
                    id="login-password"
                    v-model="loginPassword"
                    :feedback="false"
                    toggle-mask
                    input-class="auth-input"
                    :input-props="{
                      autocomplete: 'current-password',
                      disabled: isSubmitting,
                    }"
                    fluid
                  />
                  <label for="login-password">Hasło</label>
                </FloatLabel>

                <Button
                  type="submit"
                  label="Zaloguj się"
                  icon="pi pi-sign-in"
                  class="auth-submit"
                  :loading="isSubmitting"
                />
              </form>
            </TabPanel>

            <TabPanel header="Rejestracja" value="register">
              <form class="auth-form" @submit.prevent="handleRegister">
                <Message v-if="registerError" severity="error" :closable="false">
                  {{ registerError }}
                </Message>

                <FloatLabel variant="on">
                  <IconField>
                    <InputIcon class="pi pi-envelope" />
                    <InputText
                      id="register-email"
                      v-model="registerEmail"
                      type="email"
                      autocomplete="email"
                      class="auth-input"
                      :disabled="isSubmitting"
                    />
                  </IconField>
                  <label for="register-email">E-mail</label>
                </FloatLabel>

                <FloatLabel variant="on">
                  <Password
                    id="register-password"
                    v-model="registerPassword"
                    :feedback="false"
                    toggle-mask
                    input-class="auth-input"
                    :input-props="{
                      autocomplete: 'new-password',
                      disabled: isSubmitting,
                    }"
                    fluid
                  />
                  <label for="register-password">Hasło</label>
                </FloatLabel>
                <p class="auth-hint">Minimum 8 znaków</p>

                <FloatLabel variant="on">
                  <Password
                    id="register-confirm-password"
                    v-model="registerConfirmPassword"
                    :feedback="false"
                    toggle-mask
                    input-class="auth-input"
                    :input-props="{
                      autocomplete: 'new-password',
                      disabled: isSubmitting,
                    }"
                    fluid
                  />
                  <label for="register-confirm-password">Potwierdź hasło</label>
                </FloatLabel>

                <Button
                  type="submit"
                  label="Utwórz konto"
                  icon="pi pi-user-plus"
                  class="auth-submit"
                  :loading="isSubmitting"
                />
              </form>
            </TabPanel>
          </TabView>
        </template>
      </Card>
    </div>
  </div>
</template>

<style scoped>
.auth-page {
  position: relative;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 2rem 1rem;
  overflow: hidden;
  background:
    linear-gradient(rgba(15, 23, 42, 0.92), rgba(15, 23, 42, 0.92)),
    linear-gradient(90deg, rgba(45, 212, 191, 0.06) 1px, transparent 1px),
    linear-gradient(rgba(45, 212, 191, 0.06) 1px, transparent 1px),
    radial-gradient(circle at top, #1e293b 0%, #0b1220 55%, #050a12 100%);
  background-size:
    auto,
    48px 48px,
    48px 48px,
    auto;
}

.auth-page__glow {
  position: absolute;
  inset: 0;
  background:
    radial-gradient(circle at 50% 35%, rgba(45, 212, 191, 0.14), transparent 38%),
    radial-gradient(circle at 20% 80%, rgba(251, 191, 36, 0.08), transparent 30%);
  pointer-events: none;
}

.auth-page__content {
  position: relative;
  z-index: 1;
  width: min(100%, 28rem);
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.auth-brand {
  text-align: center;
}

.auth-brand__mark {
  font-size: 2.5rem;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: lowercase;
  color: #f8fafc;
}

.auth-brand__tagline {
  margin: 0.35rem 0 0;
  color: #cbd5e1;
  font-size: 0.95rem;
}

.auth-card {
  border: 1px solid rgba(255, 255, 255, 0.08);
  background: rgba(15, 23, 42, 0.72);
  backdrop-filter: blur(12px);
  box-shadow:
    0 24px 60px rgba(0, 0, 0, 0.35),
    inset 0 1px 0 rgba(255, 255, 255, 0.04);
}

.auth-card :deep(.p-card-body) {
  padding: 0;
}

.auth-card :deep(.p-card-content) {
  padding: 0;
}

.auth-tabs :deep(.p-tabview-panels) {
  padding: 1.25rem 1.5rem 1.5rem;
}

.auth-form {
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
}

.auth-input {
  width: 100%;
}

.auth-hint {
  margin: -0.75rem 0 0;
  font-size: 0.8rem;
  color: #cbd5e1;
}

.auth-submit {
  margin-top: 0.25rem;
}
</style>
