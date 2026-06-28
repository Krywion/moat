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

import * as companiesApi from '@/api/companies'
import { ApiRequestError, type FinancialForm, type FinancialReportResponse } from '@/types/api'

const visible = defineModel<boolean>('visible', { default: false })

const props = defineProps<{
  companyId: string
  mode: 'edit' | 'add'
  report: FinancialReportResponse | null
  latestReport: FinancialReportResponse | null
}>()

const emit = defineEmits<{
  saved: []
}>()

const submitError = ref('')
const isSubmitting = ref(false)
const activeTab = ref(0)
const esefFile = ref<File | null>(null)

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

const dialogHeader = computed(() =>
  props.mode === 'edit' ? 'Edytuj dane finansowe' : 'Dodaj rok obrotowy',
)

const submitLabel = computed(() =>
  props.mode === 'edit' ? 'Zapisz zmiany' : 'Dodaj rok',
)

const financialFields = computed(() => [
  revenue.value,
  ebit.value,
  depreciation.value,
  netProfit.value,
  totalDebt.value,
  netDebt.value,
  equity.value,
  operatingCashFlow.value,
])

const showFinancialHint = computed(() => financialFields.value.every((value) => value == null))

function resetForm(): void {
  submitError.value = ''
  activeTab.value = 0
  esefFile.value = null
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

async function handleEsefSubmit(): Promise<void> {
  submitError.value = ''

  const validationError = validateEsef()
  if (validationError) {
    submitError.value = validationError
    return
  }

  isSubmitting.value = true
  try {
    await companiesApi.uploadFinancialsEsef(props.companyId, esefFile.value!)
    emit('saved')
    visible.value = false
  } catch (error) {
    submitError.value = getErrorMessage(error)
  } finally {
    isSubmitting.value = false
  }
}

function prefillFromReport(report: FinancialReportResponse): void {
  fiscalYear.value = report.fiscalYear
  currency.value = report.currency
  revenue.value = report.revenue
  ebit.value = report.ebit
  depreciation.value = report.depreciation
  netProfit.value = report.netProfit
  totalDebt.value = report.totalDebt
  netDebt.value = report.netDebt
  equity.value = report.equity
  operatingCashFlow.value = report.operatingCashFlow
}

function prefillForAdd(): void {
  const latest = props.latestReport
  fiscalYear.value = (latest?.fiscalYear ?? new Date().getFullYear() - 1) + 1
  currency.value = latest?.currency ?? 'PLN'
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
    return
  }

  if (props.mode === 'edit' && props.report) {
    prefillFromReport(props.report)
    return
  }

  prefillForAdd()
})

function validate(): string | null {
  if (!fiscalYear.value || fiscalYear.value < 1900 || fiscalYear.value > 2100) {
    return 'Podaj poprawny rok obrotowy.'
  }

  if (currency.value.trim().length !== 3) {
    return 'Waluta musi mieć 3 znaki (np. PLN).'
  }

  return null
}

function buildForm(): FinancialForm {
  const form: FinancialForm = {
    fiscalYear: fiscalYear.value,
    currency: currency.value.trim().toUpperCase(),
  }

  if (revenue.value != null) form.revenue = revenue.value
  if (ebit.value != null) form.ebit = ebit.value
  if (depreciation.value != null) form.depreciation = depreciation.value
  if (netProfit.value != null) form.netProfit = netProfit.value
  if (totalDebt.value != null) form.totalDebt = totalDebt.value
  if (netDebt.value != null) form.netDebt = netDebt.value
  if (equity.value != null) form.equity = equity.value
  if (operatingCashFlow.value != null) form.operatingCashFlow = operatingCashFlow.value

  return form
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

async function handleSubmit(): Promise<void> {
  submitError.value = ''

  const validationError = validate()
  if (validationError) {
    submitError.value = validationError
    return
  }

  isSubmitting.value = true
  try {
    await companiesApi.updateFinancials(props.companyId, buildForm())
    emit('saved')
    visible.value = false
  } catch (error) {
    submitError.value = getErrorMessage(error)
  } finally {
    isSubmitting.value = false
  }
}
</script>

<template>
  <Dialog
    v-model:visible="visible"
    modal
    :header="dialogHeader"
    class="edit-financials-dialog"
    :style="{ width: 'min(100%, 36rem)' }"
    :draggable="false"
  >
    <!-- Edit mode: direct form without tabs -->
    <form v-if="mode === 'edit'" class="edit-financials-dialog__form" @submit.prevent="handleSubmit">
      <Message v-if="submitError" severity="error" :closable="false">
        {{ submitError }}
      </Message>

      <p class="edit-financials-dialog__hint">
        Zmień dane finansowe za wybrany rok obrotowy. Wskaźniki zostaną przeliczone automatycznie.
      </p>

      <div class="edit-financials-dialog__grid">
        <FloatLabel variant="on">
          <InputNumber
            id="edit-fiscal-year"
            v-model="fiscalYear"
            :use-grouping="false"
            :min="1900"
            :max="2100"
            input-class="edit-financials-dialog__input"
            :disabled="true"
            fluid
          />
          <label for="edit-fiscal-year">Rok obrotowy</label>
        </FloatLabel>

        <FloatLabel variant="on">
          <InputText
            id="edit-currency"
            v-model="currency"
            maxlength="3"
            class="edit-financials-dialog__input"
            :disabled="isSubmitting"
          />
          <label for="edit-currency">Waluta</label>
        </FloatLabel>
      </div>

      <Message v-if="showFinancialHint" severity="warn" :closable="false">
        Warto podać przynajmniej kilka pozycji finansowych, aby wskaźniki miały sens.
      </Message>

      <div class="edit-financials-dialog__section-title">Dane finansowe</div>

      <div class="edit-financials-dialog__grid">
        <FloatLabel variant="on">
          <InputNumber
            id="edit-revenue"
            v-model="revenue"
            input-class="edit-financials-dialog__input"
            :disabled="isSubmitting"
            fluid
          />
          <label for="edit-revenue">Przychody</label>
        </FloatLabel>

        <FloatLabel variant="on">
          <InputNumber
            id="edit-ebit"
            v-model="ebit"
            input-class="edit-financials-dialog__input"
            :disabled="isSubmitting"
            fluid
          />
          <label for="edit-ebit">EBIT</label>
        </FloatLabel>

        <FloatLabel variant="on">
          <InputNumber
            id="edit-depreciation"
            v-model="depreciation"
            input-class="edit-financials-dialog__input"
            :disabled="isSubmitting"
            fluid
          />
          <label for="edit-depreciation">Amortyzacja</label>
        </FloatLabel>

        <FloatLabel variant="on">
          <InputNumber
            id="edit-net-profit"
            v-model="netProfit"
            input-class="edit-financials-dialog__input"
            :disabled="isSubmitting"
            fluid
          />
          <label for="edit-net-profit">Zysk netto</label>
        </FloatLabel>

        <FloatLabel variant="on">
          <InputNumber
            id="edit-total-debt"
            v-model="totalDebt"
            input-class="edit-financials-dialog__input"
            :disabled="isSubmitting"
            fluid
          />
          <label for="edit-total-debt">Dług całkowity</label>
        </FloatLabel>

        <FloatLabel variant="on">
          <InputNumber
            id="edit-net-debt"
            v-model="netDebt"
            input-class="edit-financials-dialog__input"
            :disabled="isSubmitting"
            fluid
          />
          <label for="edit-net-debt">Dług netto</label>
        </FloatLabel>

        <FloatLabel variant="on">
          <InputNumber
            id="edit-equity"
            v-model="equity"
            input-class="edit-financials-dialog__input"
            :disabled="isSubmitting"
            fluid
          />
          <label for="edit-equity">Kapitał własny</label>
        </FloatLabel>

        <FloatLabel variant="on">
          <InputNumber
            id="edit-operating-cash-flow"
            v-model="operatingCashFlow"
            input-class="edit-financials-dialog__input"
            :disabled="isSubmitting"
            fluid
          />
          <label for="edit-operating-cash-flow">Przepływy operacyjne</label>
        </FloatLabel>
      </div>

      <Button
        type="submit"
        :label="submitLabel"
        icon="pi pi-check"
        :loading="isSubmitting"
      />
    </form>

    <!-- Add mode: tabbed UI with ESEF upload and manual form -->
    <TabView v-else v-model:active-index="activeTab" class="edit-financials-dialog__tabs">
      <TabPanel header="Raport ESEF" value="esef">
        <form class="edit-financials-dialog__form" @submit.prevent="handleEsefSubmit">
          <Message v-if="submitError && activeTab === 0" severity="error" :closable="false">
            {{ submitError }}
          </Message>

          <p class="edit-financials-dialog__hint">
            Wrzuć paczkę raportu rocznego w formacie ESEF (.xbri). System sparsuje dane i policzy
            wskaźniki. Plik musi dotyczyć tej samej spółki.
          </p>

          <FileUpload
            mode="basic"
            name="file"
            accept=".xbri"
            choose-label="Wybierz plik .xbri"
            :disabled="isSubmitting"
            @select="onEsefSelect"
          />

          <p v-if="esefFile" class="edit-financials-dialog__file-name">
            Wybrany plik: {{ esefFile.name }}
          </p>

          <Button
            type="submit"
            label="Dodaj z raportu ESEF"
            icon="pi pi-upload"
            :loading="isSubmitting"
          />
        </form>
      </TabPanel>

      <TabPanel header="Ręcznie" value="manual">
        <form class="edit-financials-dialog__form" @submit.prevent="handleSubmit">
          <Message v-if="submitError && activeTab === 1" severity="error" :closable="false">
            {{ submitError }}
          </Message>

          <p class="edit-financials-dialog__hint">
            Dodaj dane finansowe za nowy rok obrotowy. Dynamika r/r zostanie policzona względem
            poprzedniego roku, jeśli istnieje.
          </p>

          <div class="edit-financials-dialog__grid">
            <FloatLabel variant="on">
              <InputNumber
                id="edit-fiscal-year"
                v-model="fiscalYear"
                :use-grouping="false"
                :min="1900"
                :max="2100"
                input-class="edit-financials-dialog__input"
                :disabled="isSubmitting"
                fluid
              />
              <label for="edit-fiscal-year">Rok obrotowy</label>
            </FloatLabel>

            <FloatLabel variant="on">
              <InputText
                id="edit-currency"
                v-model="currency"
                maxlength="3"
                class="edit-financials-dialog__input"
                :disabled="isSubmitting"
              />
              <label for="edit-currency">Waluta</label>
            </FloatLabel>
          </div>

          <Message v-if="showFinancialHint" severity="warn" :closable="false">
            Warto podać przynajmniej kilka pozycji finansowych, aby wskaźniki miały sens.
          </Message>

          <div class="edit-financials-dialog__section-title">Dane finansowe</div>

          <div class="edit-financials-dialog__grid">
            <FloatLabel variant="on">
              <InputNumber
                id="edit-revenue"
                v-model="revenue"
                input-class="edit-financials-dialog__input"
                :disabled="isSubmitting"
                fluid
              />
              <label for="edit-revenue">Przychody</label>
            </FloatLabel>

            <FloatLabel variant="on">
              <InputNumber
                id="edit-ebit"
                v-model="ebit"
                input-class="edit-financials-dialog__input"
                :disabled="isSubmitting"
                fluid
              />
              <label for="edit-ebit">EBIT</label>
            </FloatLabel>

            <FloatLabel variant="on">
              <InputNumber
                id="edit-depreciation"
                v-model="depreciation"
                input-class="edit-financials-dialog__input"
                :disabled="isSubmitting"
                fluid
              />
              <label for="edit-depreciation">Amortyzacja</label>
            </FloatLabel>

            <FloatLabel variant="on">
              <InputNumber
                id="edit-net-profit"
                v-model="netProfit"
                input-class="edit-financials-dialog__input"
                :disabled="isSubmitting"
                fluid
              />
              <label for="edit-net-profit">Zysk netto</label>
            </FloatLabel>

            <FloatLabel variant="on">
              <InputNumber
                id="edit-total-debt"
                v-model="totalDebt"
                input-class="edit-financials-dialog__input"
                :disabled="isSubmitting"
                fluid
              />
              <label for="edit-total-debt">Dług całkowity</label>
            </FloatLabel>

            <FloatLabel variant="on">
              <InputNumber
                id="edit-net-debt"
                v-model="netDebt"
                input-class="edit-financials-dialog__input"
                :disabled="isSubmitting"
                fluid
              />
              <label for="edit-net-debt">Dług netto</label>
            </FloatLabel>

            <FloatLabel variant="on">
              <InputNumber
                id="edit-equity"
                v-model="equity"
                input-class="edit-financials-dialog__input"
                :disabled="isSubmitting"
                fluid
              />
              <label for="edit-equity">Kapitał własny</label>
            </FloatLabel>

            <FloatLabel variant="on">
              <InputNumber
                id="edit-operating-cash-flow"
                v-model="operatingCashFlow"
                input-class="edit-financials-dialog__input"
                :disabled="isSubmitting"
                fluid
              />
              <label for="edit-operating-cash-flow">Przepływy operacyjne</label>
            </FloatLabel>
          </div>

          <Button
            type="submit"
            :label="submitLabel"
            icon="pi pi-check"
            :loading="isSubmitting"
          />
        </form>
      </TabPanel>
    </TabView>
  </Dialog>
</template>

<style scoped>
.edit-financials-dialog__tabs :deep(.p-tabview-panels) {
  padding: 0.5rem 0 0;
}

.edit-financials-dialog__form {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.edit-financials-dialog__file-name {
  margin: 0;
  font-size: 0.85rem;
  color: #cbd5e1;
}

.edit-financials-dialog__hint {
  margin: 0;
  line-height: 1.5;
  color: #94a3b8;
  font-size: 0.9rem;
}

.edit-financials-dialog__input {
  width: 100%;
}

.edit-financials-dialog__grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
}

.edit-financials-dialog__section-title {
  font-size: 0.85rem;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.04em;
  color: #94a3b8;
}

@media (max-width: 640px) {
  .edit-financials-dialog__grid {
    grid-template-columns: 1fr;
  }
}
</style>
