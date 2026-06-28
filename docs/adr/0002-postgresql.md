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
- Unikalność raportu: `(company_id, fiscal_year)` — jeden raport na rok obrotowy.
- Spółki są przypisane do właściciela (`owner_id` → `users`); owner-scoping
  wymuszany w warstwie serwisu (zob. [ADR 0006](0006-rest-and-owner-scoping.md)).

## Alternatywy rozważane

- **H2 / SQLite** — odrzucone; brak precyzji NUMERIC na poziomie produkcyjnym
  i słabsze wsparcie dla constraintów w skali projektu.
- **MySQL** — odrzucone; PostgreSQL lepiej wspiera UUID i NUMERIC w JPA.

## Implementacja

- Encje JPA:
  - `backend/src/main/java/com/moat/user/User.java`
  - `backend/src/main/java/com/moat/company/Company.java`
  - `backend/src/main/java/com/moat/report/FinancialReport.java`
- Migracje Liquibase: `backend/src/main/resources/db/changelog/`
  (master: `db.changelog-master.yaml`)
- Kontener Docker: serwis `postgres` w `docker-compose.yml`
- Testy integracyjne: Testcontainers PostgreSQL (`AbstractIntegrationTest`)

## Konsekwencje

- **+** Precyzyjna arytmetyka finansowa; silne ograniczenia integralności
  (FK, unikalność) wymuszane na poziomie bazy.
- **+** Świetne wsparcie w Spring Data JPA i Testcontainers.
- **−** Wymaga uruchomionego serwera bazy także lokalnie i w testach —
  rozwiązane przez Docker Compose oraz Testcontainers.
