export function formatNumber(value: number | null | undefined): string {
  if (value == null) {
    return '—'
  }

  return new Intl.NumberFormat('pl-PL', {
    maximumFractionDigits: 2,
  }).format(value)
}

export function formatPercent(value: number | null | undefined): string {
  if (value == null) {
    return '—'
  }

  return new Intl.NumberFormat('pl-PL', {
    style: 'percent',
    minimumFractionDigits: 1,
    maximumFractionDigits: 1,
  }).format(value)
}

export function formatCurrency(
  value: number | null | undefined,
  currency: string,
): string {
  if (value == null) {
    return '—'
  }

  return new Intl.NumberFormat('pl-PL', {
    style: 'currency',
    currency,
    maximumFractionDigits: 0,
  }).format(value)
}

export function formatRatio(value: number | null | undefined): string {
  if (value == null) {
    return '—'
  }

  return new Intl.NumberFormat('pl-PL', {
    minimumFractionDigits: 1,
    maximumFractionDigits: 2,
  }).format(value)
}
