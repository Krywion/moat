# ADR 0002 — PostgreSQL jako baza danych

- **Status:** zaakceptowany
- **Data:** 2026-06-20

## Kontekst

Model danych to trzy encje w relacjach (User → Company → FinancialReport)
z wymogiem przechowywania kwot finansowych z dokładnością (bez błędów
zmiennoprzecinkowych) oraz unikalnych ograniczeń (np. jeden raport na
spółkę i rok). Potrzebna relacyjna baza z mocnym wsparciem typów liczbowych.

## Decyzja

Używamy **PostgreSQL 16**.

- Kwoty: typ `NUMERIC(19, 4)` ↔ `BigDecimal` w Javie — brak błędów
  zaokrągleń typowych dla `float`/`double`.
- Klucze główne: `UUID`.
- Daty: `TIMESTAMP WITH TIME ZONE`.
- Baza działa jako kontener w Docker Compose z health-checkiem `pg_isready`.

## Konsekwencje

- **+** Precyzyjna arytmetyka finansowa; silne ograniczenia integralności
  (FK, unikalność) wymuszane na poziomie bazy.
- **+** Świetne wsparcie w Spring Data JPA i Testcontainers.
- **−** Wymaga uruchomionego serwera bazy także lokalnie i w testach —
  rozwiązane przez Docker Compose oraz Testcontainers.
