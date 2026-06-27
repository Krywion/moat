<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import Button from 'primevue/button'
import Dialog from 'primevue/dialog'
import FileUpload, { type FileUploadSelectEvent } from 'primevue/fileupload'
import FloatLabel from 'primevue/floatlabel'
import InputNumber from 'primevue/inputnumber'
import InputText from 'primevue/inputtext'
import Message from 'primevue/message'
import TabPanel from 'primevue/tabpanel'
import TabView from 'primevue/tabview'

import { useCompaniesStore } from '@/stores/companies'
import type { CreateCompanyRequest } from '@/types/api'

const visible = defineModel<boolean>('visible', { default: false })

const emit = defineEmits<{
  created: []
}>()

const companiesStore = useCompaniesStore()

const activeTab = ref(0)
const submitError = ref('')

const esefFile = ref<File | null>(null)
const esefTicker = ref('')

const name = ref('')
const ticker = ref('')
const fiscalYear = ref(new Date().getFullYear() - 1)
const currency = ref('PLN')
const revenue = ref<number | null>(null)
const ebit = ref<number | null>(null)
const depreciation = ref<number | null>(null)
const netProfit = ref<number | null>(null)
const totalDebt = ref<number | null>(null)
const netDebt = ref<number | null>(null)
const equity = ref<number | null>(null)
const operatingCashFlow = ref<number | null>(null)

const isSubmitting = computed(() => companiesStore.isSubmitting)

const manualFinancialFields = computed(() => [
  revenue.value,
  ebit.value,
  depreciation.value,
  netProfit.value,
  totalDebt.value,
  netDebt.value,
  equity.value,
  operatingCashFlow.value,
])

const showManualFinancialHint = computed(
  () => manualFinancialFields.value.every((value) => value == null),
)

function resetForm(): void {
  activeTab.value = 0
  submitError.value = ''
  esefFile.value = null
  esefTicker.value = ''
  name.value = ''
  ticker.value = ''
  fiscalYear.value = new Date().getFullYear() - 1
  currency.value = 'PLN'
  revenue.value = null
  ebit.value = null
  depreciation.value = null
  netProfit.value = null
  totalDebt.value = null
  netDebt.value = null
  equity.value = null
  operatingCashFlow.value = null
}

watch(visible, (isOpen) => {
  if (!isOpen) {
    resetForm()
  }
})

function onEsefSelect(event: FileUploadSelectEvent): void {
  const file = event.files[0]
  esefFile.value = file ?? null
}

function validateEsef(): string | null {
  if (!esefFile.value) {
    return 'Wybierz plik raportu ESEF (.xbri).'
  }

  return null
}

function validateManual(): string | null {
  if (!name.value.trim()) {
    return 'Podaj nazwę spółki.'
  }

  if (!fiscalYear.value || fiscalYear.value < 1900 || fiscalYear.value > 2100) {
    return 'Podaj poprawny rok obrotowy.'
  }

  if (currency.value.trim().length !== 3) {
    return 'Waluta musi mieć 3 znaki (np. PLN).'
  }

  return null
}

function buildManualRequest(): CreateCompanyRequest {
  const financials: CreateCompanyRequest['financials'] = {
    fiscalYear: fiscalYear.value,
    currency: currency.value.trim().toUpperCase(),
  }

  if (revenue.value != null) financials.revenue = revenue.value
  if (ebit.value != null) financials.ebit = ebit.value
  if (depreciation.value != null) financials.depreciation = depreciation.value
  if (netProfit.value != null) financials.netProfit = netProfit.value
  if (totalDebt.value != null) financials.totalDebt = totalDebt.value
  if (netDebt.value != null) financials.netDebt = netDebt.value
  if (equity.value != null) financials.equity = equity.value
  if (operatingCashFlow.value != null) financials.operatingCashFlow = operatingCashFlow.value

  const request: CreateCompanyRequest = {
    name: name.value.trim(),
    financials,
  }

  if (ticker.value.trim()) {
    request.ticker = ticker.value.trim().toUpperCase()
  }

  return request
}

async function handleEsefSubmit(): Promise<void> {
  submitError.value = ''

  const validationError = validateEsef()
  if (validationError) {
    submitError.value = validationError
    return
  }

  try {
    await companiesStore.createFromEsef(esefFile.value!, esefTicker.value || undefined)
    emit('created')
    visible.value = false
  } catch (error) {
    submitError.value = companiesStore.getErrorMessage(error)
  }
}

async function handleManualSubmit(): Promise<void> {
  submitError.value = ''

  const validationError = validateManual()
  if (validationError) {
    submitError.value = validationError
    return
  }

  try {
    await companiesStore.createFromForm(buildManualRequest())
    emit('created')
    visible.value = false
  } catch (error) {
    submitError.value = companiesStore.getErrorMessage(error)
  }
}
</script>

<template>
  <Dialog
    v-model:visible="visible"
    modal
    header="Dodaj spółkę"
    class="add-company-dialog"
    :style="{ width: 'min(100%, 36rem)' }"
    :draggable="false"
  >
    <TabView v-model:active-index="activeTab" class="add-company-dialog__tabs">
      <TabPanel header="Raport ESEF" value="esef">
        <form class="add-company-dialog__form" @submit.prevent="handleEsefSubmit">
          <Message v-if="submitError && activeTab === 0" severity="error" :closable="false">
            {{ submitError }}
          </Message>

          <p class="add-company-dialog__hint">
            Wrzuć paczkę raportu rocznego w formacie ESEF (.xbri). System sparsuje dane i policzy
            wskaźniki.
          </p>

          <FileUpload
            mode="basic"
            name="file"
            accept=".xbri"
            choose-label="Wybierz plik .xbri"
            :disabled="isSubmitting"
            @select="onEsefSelect"
          />

          <p v-if="esefFile" class="add-company-dialog__file-name">
            Wybrany plik: {{ esefFile.name }}
          </p>

          <FloatLabel variant="on">
            <InputText
              id="esef-ticker"
              v-model="esefTicker"
              class="add-company-dialog__input"
              :disabled="isSubmitting"
            />
            <label for="esef-ticker">Ticker (opcjonalnie)</label>
          </FloatLabel>

          <Button
            type="submit"
            label="Dodaj z raportu ESEF"
            icon="pi pi-upload"
            :loading="isSubmitting"
          />
        </form>
      </TabPanel>

      <TabPanel header="Ręcznie" value="manual">
        <form class="add-company-dialog__form" @submit.prevent="handleManualSubmit">
          <Message v-if="submitError && activeTab === 1" severity="error" :closable="false">
            {{ submitError }}
          </Message>

          <p class="add-company-dialog__hint">
            Wprowadź dane finansowe ręcznie — dla spółek spoza GPW lub raportów sprzed obowiązku
            ESEF.
          </p>

          <div class="add-company-dialog__grid">
            <FloatLabel variant="on">
              <InputText
                id="company-name"
                v-model="name"
                class="add-company-dialog__input"
                :disabled="isSubmitting"
              />
              <label for="company-name">Nazwa spółki</label>
            </FloatLabel>

            <FloatLabel variant="on">
              <InputText
                id="company-ticker"
                v-model="ticker"
                class="add-company-dialog__input"
                :disabled="isSubmitting"
              />
              <label for="company-ticker">Ticker (opcjonalnie)</label>
            </FloatLabel>

            <FloatLabel variant="on">
              <InputNumber
                id="fiscal-year"
                v-model="fiscalYear"
                :use-grouping="false"
                :min="1900"
                :max="2100"
                input-class="add-company-dialog__input"
                :disabled="isSubmitting"
                fluid
              />
              <label for="fiscal-year">Rok obrotowy</label>
            </FloatLabel>

            <FloatLabel variant="on">
              <InputText
                id="currency"
                v-model="currency"
                maxlength="3"
                class="add-company-dialog__input"
                :disabled="isSubmitting"
              />
              <label for="currency">Waluta</label>
            </FloatLabel>
          </div>

          <Message v-if="showManualFinancialHint" severity="warn" :closable="false">
            Warto podać przynajmniej kilka pozycji finansowych, aby wskaźniki miały sens.
          </Message>

          <div class="add-company-dialog__section-title">Dane finansowe</div>

          <div class="add-company-dialog__grid">
            <FloatLabel variant="on">
              <InputNumber
                id="revenue"
                v-model="revenue"
                input-class="add-company-dialog__input"
                :disabled="isSubmitting"
                fluid
              />
              <label for="revenue">Przychody</label>
            </FloatLabel>

            <FloatLabel variant="on">
              <InputNumber
                id="ebit"
                v-model="ebit"
                input-class="add-company-dialog__input"
                :disabled="isSubmitting"
                fluid
              />
              <label for="ebit">EBIT</label>
            </FloatLabel>

            <FloatLabel variant="on">
              <InputNumber
                id="depreciation"
                v-model="depreciation"
                input-class="add-company-dialog__input"
                :disabled="isSubmitting"
                fluid
              />
              <label for="depreciation">Amortyzacja</label>
            </FloatLabel>

            <FloatLabel variant="on">
              <InputNumber
                id="net-profit"
                v-model="netProfit"
                input-class="add-company-dialog__input"
                :disabled="isSubmitting"
                fluid
              />
              <label for="net-profit">Zysk netto</label>
            </FloatLabel>

            <FloatLabel variant="on">
              <InputNumber
                id="total-debt"
                v-model="totalDebt"
                input-class="add-company-dialog__input"
                :disabled="isSubmitting"
                fluid
              />
              <label for="total-debt">Dług całkowity</label>
            </FloatLabel>

            <FloatLabel variant="on">
              <InputNumber
                id="net-debt"
                v-model="netDebt"
                input-class="add-company-dialog__input"
                :disabled="isSubmitting"
                fluid
              />
              <label for="net-debt">Dług netto</label>
            </FloatLabel>

            <FloatLabel variant="on">
              <InputNumber
                id="equity"
                v-model="equity"
                input-class="add-company-dialog__input"
                :disabled="isSubmitting"
                fluid
              />
              <label for="equity">Kapitał własny</label>
            </FloatLabel>

            <FloatLabel variant="on">
              <InputNumber
                id="operating-cash-flow"
                v-model="operatingCashFlow"
                input-class="add-company-dialog__input"
                :disabled="isSubmitting"
                fluid
              />
              <label for="operating-cash-flow">Przepływy operacyjne</label>
            </FloatLabel>
          </div>

          <Button
            type="submit"
            label="Dodaj spółkę"
            icon="pi pi-plus"
            :loading="isSubmitting"
          />
        </form>
      </TabPanel>
    </TabView>
  </Dialog>
</template>

<style scoped>
.add-company-dialog__tabs :deep(.p-tabview-panels) {
  padding: 0.5rem 0 0;
}

.add-company-dialog__form {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.add-company-dialog__hint {
  margin: 0;
  line-height: 1.5;
  color: #64748b;
  font-size: 0.9rem;
}

.add-company-dialog__file-name {
  margin: 0;
  font-size: 0.85rem;
  color: #cbd5e1;
}

.add-company-dialog__input {
  width: 100%;
}

.add-company-dialog__grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
}

.add-company-dialog__section-title {
  font-size: 0.85rem;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.04em;
  color: #64748b;
}

@media (max-width: 640px) {
  .add-company-dialog__grid {
    grid-template-columns: 1fr;
  }
}
</style>
