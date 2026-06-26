export type IndicatorFormat = 'currency' | 'percent' | 'ratio' | 'number'

export interface IndicatorMeta {
  label: string
  tooltip: string
  format: IndicatorFormat
}

export const WARNING_FLAG_LABELS: Record<string, string> = {
  POSITIVE_PROFIT_NEGATIVE_CASHFLOW:
    'Dodatni zysk netto przy ujemnym przepływie operacyjnym — zysk może nie być pokryty gotówką.',
  MARGIN_DECLINING_YOY: 'Marża netto spadła rok do roku — rentowność się pogarsza.',
  DEBT_GROWING_FASTER_THAN_PROFIT:
    'Dług rośnie szybciej niż zysk — firma może się nadmiernie zadłużać.',
}

export const HEALTH_INDICATORS: Record<string, IndicatorMeta> = {
  revenue: {
    label: 'Przychody',
    tooltip: 'Łączne przychody ze sprzedaży w danym roku obrotowym.',
    format: 'currency',
  },
  ebit: {
    label: 'Zysk operacyjny (EBIT)',
    tooltip: 'Zysk przed odsetkami i podatkami — pokazuje wynik operacyjny bez wpływu finansowania.',
    format: 'currency',
  },
  depreciation: {
    label: 'Amortyzacja',
    tooltip: 'Roczny koszt zużycia środków trwałych i wartości niematerialnych.',
    format: 'currency',
  },
  netProfit: {
    label: 'Zysk netto',
    tooltip: 'Zysk po wszystkich kosztach, odsetkach i podatkach — końcowy wynik finansowy.',
    format: 'currency',
  },
  totalDebt: {
    label: 'Dług całkowity',
    tooltip: 'Suma zobowiązań finansowych spółki na koniec roku.',
    format: 'currency',
  },
  netDebt: {
    label: 'Dług netto',
    tooltip: 'Dług całkowity pomniejszony o środki pieniężne — pokazuje realne zadłużenie.',
    format: 'currency',
  },
  equity: {
    label: 'Kapitał własny',
    tooltip: 'Wartość aktywów finansowanych przez właścicieli — bufor bezpieczeństwa firmy.',
    format: 'currency',
  },
  operatingCashFlow: {
    label: 'Przepływy operacyjne',
    tooltip: 'Gotówka wygenerowana z działalności operacyjnej — miara jakości zysków.',
    format: 'currency',
  },
  ebitda: {
    label: 'EBITDA',
    tooltip: 'EBIT plus amortyzacja — zysk operacyjny bez wpływu polityki amortyzacji.',
    format: 'currency',
  },
  operatingMargin: {
    label: 'Marża operacyjna',
    tooltip: 'EBIT / przychody — ile groszy z każdej złotówki sprzedaży zostaje po kosztach operacyjnych.',
    format: 'percent',
  },
  ebitdaMargin: {
    label: 'Marża EBITDA',
    tooltip: 'EBITDA / przychody — rentowność operacyjna bez amortyzacji.',
    format: 'percent',
  },
  netMargin: {
    label: 'Marża netto',
    tooltip: 'Zysk netto / przychody — jaka część sprzedaży zamienia się w zysk po wszystkich kosztach.',
    format: 'percent',
  },
  roe: {
    label: 'ROE',
    tooltip: 'Zysk netto / kapitał własny — rentowność kapitału włożonego przez akcjonariuszy.',
    format: 'percent',
  },
  debtToEquity: {
    label: 'Dług / kapitał własny',
    tooltip: 'Stosunek zadłużenia do kapitału własnego — wyższa wartość oznacza większe ryzyko finansowe.',
    format: 'ratio',
  },
  revenueGrowthYoy: {
    label: 'Dynamika przychodów r/r',
    tooltip: 'Zmiana przychodów rok do roku — pokazuje tempo wzrostu lub spadku sprzedaży.',
    format: 'percent',
  },
  profitGrowthYoy: {
    label: 'Dynamika zysku r/r',
    tooltip: 'Zmiana zysku netto rok do roku — czy firma zarabia więcej niż rok wcześniej.',
    format: 'percent',
  },
}

export const VALUATION_INDICATORS: Record<string, IndicatorMeta> = {
  sharePrice: {
    label: 'Kurs akcji',
    tooltip: 'Aktualna cena jednej akcji na giełdzie.',
    format: 'currency',
  },
  marketCap: {
    label: 'Kapitalizacja',
    tooltip: 'Łączna wartość rynkowa spółki (kurs × liczba akcji).',
    format: 'currency',
  },
  pe: {
    label: 'P/E',
    tooltip: 'Cena / zysk na akcję — ile lat zysku „kosztuje” jedna akcja. Niższe P/E często oznacza tańszą wycenę.',
    format: 'ratio',
  },
  evEbitda: {
    label: 'EV/EBITDA',
    tooltip: 'Wartość przedsiębiorstwa względem EBITDA — popularna miara wyceny przy porównywaniu spółek.',
    format: 'ratio',
  },
  pbv: {
    label: 'P/BV',
    tooltip: 'Cena / wartość księgowa — porównuje kurs akcji z wartością aktywów netto na akcję.',
    format: 'ratio',
  },
  dividendYield: {
    label: 'Stopa dywidendy',
    tooltip: 'Dywidenda względem ceny akcji — ile procent zwraca spółka w formie wypłat.',
    format: 'percent',
  },
}

export const HEALTH_RAW_KEYS = [
  'revenue',
  'ebit',
  'depreciation',
  'netProfit',
  'totalDebt',
  'netDebt',
  'equity',
  'operatingCashFlow',
] as const

export const HEALTH_COMPUTED_KEYS = [
  'ebitda',
  'operatingMargin',
  'ebitdaMargin',
  'netMargin',
  'roe',
  'debtToEquity',
  'revenueGrowthYoy',
  'profitGrowthYoy',
] as const

export const VALUATION_KEYS = [
  'sharePrice',
  'marketCap',
  'pe',
  'evEbitda',
  'pbv',
  'dividendYield',
] as const
