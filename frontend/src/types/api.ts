export interface UserResponse {
  id: string
  email: string
  role: string
}

export interface LoginRequest {
  email: string
  password: string
}

export interface RegisterRequest {
  email: string
  password: string
}

export interface CompanySummaryResponse {
  id: string
  name: string
  ticker: string | null
  latestFiscalYear: number | null
  netMargin: number | null
}

export interface FinancialForm {
  fiscalYear: number
  currency: string
  revenue?: number | null
  ebit?: number | null
  depreciation?: number | null
  netProfit?: number | null
  totalDebt?: number | null
  netDebt?: number | null
  equity?: number | null
  operatingCashFlow?: number | null
}

export interface CreateCompanyRequest {
  name: string
  ticker?: string | null
  financials: FinancialForm
}

export interface FinancialReportResponse {
  fiscalYear: number
  currency: string
  revenue: number | null
  ebit: number | null
  depreciation: number | null
  netProfit: number | null
  totalDebt: number | null
  netDebt: number | null
  equity: number | null
  operatingCashFlow: number | null
  ebitda: number | null
  operatingMargin: number | null
  ebitdaMargin: number | null
  netMargin: number | null
  roe: number | null
  debtToEquity: number | null
  revenueGrowthYoy: number | null
  profitGrowthYoy: number | null
  sharePrice: number | null
  marketCap: number | null
  pe: number | null
  evEbitda: number | null
  pbv: number | null
  dividendYield: number | null
  flags: string[]
}

export interface CompanyDetailResponse {
  id: string
  name: string
  ticker: string | null
  reports: FinancialReportResponse[]
}

export interface ApiError {
  timestamp: string
  status: number
  error: string
  message: string
}

export class ApiRequestError extends Error {
  readonly status: number
  readonly error: string
  readonly timestamp: string

  constructor(payload: ApiError) {
    super(payload.message)
    this.name = 'ApiRequestError'
    this.status = payload.status
    this.error = payload.error
    this.timestamp = payload.timestamp
  }
}
