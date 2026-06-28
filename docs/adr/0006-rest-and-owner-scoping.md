# ADR 0006 — Komunikacja REST i owner-scoping

- **Status:** zaakceptowany
- **Data:** 2026-06-22

## Kontekst

Frontend (Vue) komunikuje się z backendem przez API. Zasoby (spółki, raporty)
należą do konkretnego użytkownika i nie mogą być widoczne dla innych.

## Decyzja

- **REST/JSON** jako kontrakt frontend–backend; kontrakt opisany w
  `backend/openapi.yaml` i generowany do DTO (`com.moat.api.model.*`).
- Zasoby pod `/companies`, czasowniki HTTP (`GET`/`POST`/`PUT`/`DELETE`),
  kody statusu jako sygnał wyniku.
- **Owner-scoping w warstwie serwisu:** każdy dostęp do spółki filtruje po
  właścicielu (`findByIdAndOwnerId`). Brak dopasowania → **`404`** (nie `403`),
  żeby nie zdradzać istnienia cudzego zasobu. Id użytkownika z claima `sub` JWT.

### Endpointy (pełna lista)

**Auth** (zob. [ADR 0005](0005-jwt-auth-strategy.md)): `POST /auth/register`,
`POST /auth/login`, `POST /auth/logout`, `GET /auth/me`.

**Companies** (wymagają zalogowania, poza wyjątkami auth):

| Metoda | Ścieżka | Opis |
|--------|---------|------|
| `GET` | `/companies` | Lista spółek użytkownika |
| `POST` | `/companies` | Nowa spółka + raport z formularza |
| `GET` | `/companies/{id}` | Szczegóły i analiza |
| `DELETE` | `/companies/{id}` | Usunięcie spółki z raportami |
| `PUT` | `/companies/{id}/financials` | Dodanie/edycja roku (formularz) |
| `POST` | `/companies/esef` | Nowa spółka z uploadu `.xbri` |
| `POST` | `/companies/{id}/financials/esef` | Nowy rok z uploadu `.xbri` |
| `POST` | `/companies/{id}/refresh-market` | Odświeżenie danych rynkowych |

### Kody błędów biznesowych

| Kod | Znaczenie |
|-----|-----------|
| `404` | Spółka nie istnieje lub należy do innego użytkownika |
| `409` | Duplikat spółki (nazwa/ticker u tego samego ownera) lub duplikat raportu za rok |
| `422` | Błąd parsowania ESEF lub niedopasowanie nazwy spółki w `.xbri` |

## Alternatywy rozważane

- **Filtrowanie w kontrolerze** — odrzucone; logika rozproszona, łatwo o błąd.
- **`403` zamiast `404`** — odrzucone; zdradza istnienie cudzego zasobu.
- **GraphQL** — odrzucone; nadmiarowy dla prostego CRUD i jednego klienta SPA.

## Implementacja

- Kontroler REST: `backend/src/main/java/com/moat/company/CompanyController.java`
- Logika biznesowa i owner-scoping: `CompanyService.java`
- Repozytorium: `CompanyRepository.findByIdAndOwnerId`, `existsByOwnerIdAndName`
- Obsługa błędów: `CompanyExceptionHandler.java`
- Kontrakt OpenAPI: `backend/openapi.yaml`
- Frontend API client: `frontend/src/api/companies.ts`, `frontend/src/api/auth.ts`

## Konsekwencje

- **+** Prosty, przewidywalny kontrakt; standardowe kody błędów.
- **+** Izolacja danych między użytkownikami wymuszona w jednym miejscu (serwis).
- **−** `404` zamiast `403` może utrudniać debug („czy zasób istnieje?") —
  akceptowalny koszt za brak wycieku informacji.
