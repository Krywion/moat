import { apiFetch } from '@/api/client'
import type {
  CompanyDetailResponse,
  CompanySummaryResponse,
  CreateCompanyRequest,
} from '@/types/api'

export function listCompanies(): Promise<CompanySummaryResponse[]> {
  return apiFetch<CompanySummaryResponse[]>('/companies')
}

export function createCompany(request: CreateCompanyRequest): Promise<CompanyDetailResponse> {
  return apiFetch<CompanyDetailResponse>('/companies', {
    method: 'POST',
    body: JSON.stringify(request),
  })
}

export function createCompanyFromEsef(
  file: File,
  ticker?: string,
): Promise<CompanyDetailResponse> {
  const formData = new FormData()
  formData.append('file', file)
  if (ticker?.trim()) {
    formData.append('ticker', ticker.trim())
  }

  return apiFetch<CompanyDetailResponse>('/companies/esef', {
    method: 'POST',
    body: formData,
  })
}

export function getCompany(id: string): Promise<CompanyDetailResponse> {
  return apiFetch<CompanyDetailResponse>(`/companies/${id}`)
}

export function refreshMarket(id: string): Promise<CompanyDetailResponse> {
  return apiFetch<CompanyDetailResponse>(`/companies/${id}/refresh-market`, {
    method: 'POST',
  })
}
