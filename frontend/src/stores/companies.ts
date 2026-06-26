import { computed, ref } from 'vue'
import { defineStore } from 'pinia'

import * as companiesApi from '@/api/companies'
import { ApiRequestError, type CompanySummaryResponse, type CreateCompanyRequest } from '@/types/api'

export type CompaniesStatus = 'idle' | 'loading' | 'ready' | 'error'

export const useCompaniesStore = defineStore('companies', () => {
  const companies = ref<CompanySummaryResponse[]>([])
  const status = ref<CompaniesStatus>('idle')
  const errorMessage = ref<string | null>(null)
  const isSubmitting = ref(false)

  const isEmpty = computed(() => status.value === 'ready' && companies.value.length === 0)

  async function loadCompanies(): Promise<void> {
    status.value = 'loading'
    errorMessage.value = null

    try {
      companies.value = await companiesApi.listCompanies()
      status.value = 'ready'
    } catch (error) {
      companies.value = []
      status.value = 'error'
      errorMessage.value = getErrorMessage(error)
    }
  }

  async function createFromForm(request: CreateCompanyRequest): Promise<void> {
    isSubmitting.value = true
    try {
      await companiesApi.createCompany(request)
      await loadCompanies()
    } finally {
      isSubmitting.value = false
    }
  }

  async function createFromEsef(file: File, ticker?: string): Promise<void> {
    isSubmitting.value = true
    try {
      await companiesApi.createCompanyFromEsef(file, ticker)
      await loadCompanies()
    } finally {
      isSubmitting.value = false
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

  return {
    companies,
    status,
    errorMessage,
    isSubmitting,
    isEmpty,
    loadCompanies,
    createFromForm,
    createFromEsef,
    getErrorMessage,
  }
})
