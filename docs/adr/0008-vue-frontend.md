# ADR 0008 — Vue 3 jako frontend SPA

- **Status:** zaakceptowany
- **Data:** 2026-06-28

## Kontekst

Użytkownik potrzebuje interfejsu do rejestracji, przeglądania listy spółek
i analizy fundamentalnej (kondycja firmy, wycena, flagi ostrzegawcze z tooltipami).
Frontend musi komunikować się z REST API backendu z uwierzytelnianiem przez
HttpOnly cookie (zob. [ADR 0005](0005-jwt-auth-strategy.md)).

## Decyzja

- **Vue 3 + TypeScript + Vite** jako SPA; stan globalny w **Pinia**;
  routing w **Vue Router**; komponenty UI z **PrimeVue**.
- Trasy aplikacji:
  - `/auth` — rejestracja i logowanie
  - `/dashboard` — lista spółek użytkownika, dodawanie spółki
  - `/companies/:id` — pełna analiza spółki (zdrowie, wycena, flagi)
- **Komunikacja z API:** `fetch` z `credentials: 'include'` — przeglądarka
  automatycznie dołącza cookie `auth_token`; brak ręcznego nagłówka `Authorization`.
- **Proxy w dev:** Vite przekierowuje `/auth/*` i `/companies` na `localhost:8080`
  (`frontend/vite.config.ts`).
- **Produkcja (Docker):** nginx serwuje zbudowane statyczne pliki i proxy'uje
  `/auth/` oraz `/companies` do kontenera `api:8080` (`frontend/nginx.conf`);
  limit uploadu `25m` dla plików ESEF.
- **Tooltips wskaźników:** opisy w `frontend/src/data/indicators.ts` — zgodnie
  z zasadą produktową „aplikacja ma uczyć, nie tylko liczyć".

## Alternatywy rozważane

- **SSR (Nuxt)** — odrzucone; brak wymagań SEO, SPA wystarcza.
- **Osobny BFF** — odrzucone; nginx/Vite proxy wystarcza do tej samej domeny/site.
- **Token w localStorage** — odrzucone; sprzeczne z ADR 0005 (XSS).
- **React** — odrzucone; znajomość Vue w zespole i szybszy start z Vite.

## Implementacja

- Wejście: `frontend/src/main.ts`, `frontend/src/App.vue`
- Routing i guard auth: `frontend/src/router/index.ts`
- Store auth: `frontend/src/stores/auth.ts`
- Store spółek: `frontend/src/stores/companies.ts`
- Klient HTTP: `frontend/src/api/client.ts`
- Widoki: `views/AuthView.vue`, `views/DashboardView.vue`, `views/CompanyDetailView.vue`
- Sekcje analizy: `components/CompanyHealthSection.vue`,
  `components/CompanyValuationSection.vue`, `components/WarningFlags.vue`
- Dialogi: `components/AddCompanyDialog.vue`, `components/EditFinancialsDialog.vue`
- Build produkcyjny: `frontend/Dockerfile` (multi-stage: Node build → nginx)
- Uruchomienie w compose: serwis `frontend` w `docker-compose.yml` (port `3000` → `80`)

## Konsekwencje

- **+** Szybki dev z HMR (Vite); spójny stack TypeScript z typami API.
- **+** Cookie auth bez logiki tokenu po stronie klienta.
- **−** W dev backend musi działać na `:8080` (proxy Vite) lub używać pełnego compose.
- **−** Przy wdrożeniu na osobne domeny frontend/backend wymagana zmiana strategii
  CORS i cookie (poza zakresem obecnego projektu).
