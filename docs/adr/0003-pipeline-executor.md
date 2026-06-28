# ADR 0003 — Pipeline-executor jako rdzeń architektury

- **Status:** zaakceptowany
- **Data:** 2026-06-20

## Kontekst

Dodanie lub aktualizacja danych finansowych spółki wymaga ciągu operacji:
agregacja do wspólnego modelu, obliczenie wskaźników i flag, wzbogacenie
danymi rynkowymi, zapis. Etapy mają różne ryzyka (parsowanie ESEF,
niestabilne API rynkowe) i muszą być testowalne niezależnie.

## Decyzja

Logikę analizy realizujemy jako **pipeline — sekwencję osobnych,
testowalnych kroków** w `PipelineExecutor`:

1. **Agregacja** — surowe dane (`FinancialForm` lub wynik parsowania ESEF)
   do jednego modelu `FinancialData`.
2. **Obliczenia** — wskaźniki i flagi ostrzegawcze.
3. **Wzbogacenie** — dane rynkowe z API zewnętrznego (Yahoo).
4. **Zapis** — raport finansowy do bazy.

**Przygotowanie wejścia** (poza pipeline executorem) odbywa się w
`CompanyService`:

- **Formularz** — dane z żądania REST trafiają bezpośrednio do `PipelineContext`.
- **ESEF** — `EsefParser` rozpakowuje `.xbri`, parsuje iXBRL i mapuje tagi IFRS
  na `FinancialForm` *przed* wywołaniem pipeline'u.

Oba warianty wejścia dają ten sam model na wejściu pipeline'u; od kroku 1
(agregacja) przepływ jest wspólny.

Pipeline uruchamiany jest przy:

- `createCompany` — formularz przy tworzeniu spółki
- `createCompanyFromEsef` — upload `.xbri` przy tworzeniu spółki
- `updateFinancials` — formularz dla istniejącej spółki
- `addFinancialsFromEsef` — upload `.xbri` dla istniejącej spółki

## Alternatywy rozważane

- **Monolityczna metoda w serwisie** — odrzucona; trudna do testowania
  i rozszerzania o kolejne kroki.
- **Wejście jako krok w pipeline** — odrzucone; parsowanie ESEF i walidacja
  HTTP (duplikaty, dopasowanie nazwy spółki) należą do warstwy serwisu,
  pipeline operuje na gotowym modelu danych.

## Implementacja

- Orkiestracja: `backend/src/main/java/com/moat/pipeline/PipelineExecutor.java`
- Kroki:
  - `AggregationStep.java` — normalizacja do `FinancialData`
  - `CalculationStep.java` — wskaźniki via `IndicatorCalculator`, flagi via `WarningFlagEvaluator`
  - `EnrichmentStep.java` — Yahoo Finance via `MarketEnricher`
  - `PersistenceStep.java` — zapis `FinancialReport`
- Kontekst przepływu: `PipelineContext.java`
- Wywołania z: `backend/src/main/java/com/moat/company/CompanyService.java`
- Parsowanie ESEF (przed pipeline): pakiet `com.moat.esef` (zob. [ADR 0007](0007-esef-ixbrl-input.md))

## Konsekwencje

- **+** Kroki testowane osobno; łatwo podmienić wariant wejścia.
- **+** Ryzykowne etapy izolowane — np. niedostępność API rynkowego
  (krok 3) nie blokuje analizy kondycji firmy (kroki 1–2).
- **+** Jasny podział: serwis obsługuje HTTP i wejście, executor — analizę.
- **−** Ryzyko over-engineeringu — mitygacja: kroki proste, nie dokładamy
  ich bez potrzeby (ROADMAP sekcja 6).
