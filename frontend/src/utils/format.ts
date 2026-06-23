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
