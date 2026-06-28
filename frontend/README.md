# moat — frontend

Interfejs użytkownika aplikacji **moat** (Analizator Spółek Giełdowych).
SPA w Vue 3 komunikujące się z backendem Spring Boot przez REST API
z uwierzytelnianiem cookie (JWT HttpOnly).

## Stack

- Vue 3, TypeScript, Vite 8
- Pinia (stan), Vue Router (trasy)
- PrimeVue + PrimeIcons (komponenty UI)
- Node **24** (patrz `engines` w `package.json`)

## Uruchomienie

```bash
npm install
npm run dev          # http://localhost:5173, proxy API → :8080
```

Z katalogu root repozytorium: `npm run dev` (deleguje do `frontend/`).

Backend musi działać na `http://localhost:8080` — np. `cd backend && ./mvnw spring-boot:run`
lub `docker compose up postgres api`.

### Produkcja / Docker

```bash
npm run build        # type-check (vue-tsc) + vite build → dist/
```

W Docker Compose frontend jest serwowany przez nginx (port `3000` na hoście);
nginx proxy'uje `/auth/` i `/companies` do kontenera API.

## Trasy

| Ścieżka | Widok | Opis |
|---------|-------|------|
| `/auth` | `AuthView` | Rejestracja i logowanie |
| `/dashboard` | `DashboardView` | Lista spółek, dodawanie spółki |
| `/companies/:id` | `CompanyDetailView` | Analiza: kondycja, wycena, flagi |

Niezalogowany użytkownik na trasie chronionej jest przekierowywany na `/auth`
(guard w `src/router/index.ts`).

## Struktura kodu

```
src/
├── api/           # client.ts, auth.ts, companies.ts
├── stores/        # Pinia: auth, companies
├── views/         # ekrany (Auth, Dashboard, CompanyDetail)
├── components/    # sekcje analizy, dialogi, lista spółek
├── data/          # opisy wskaźników (tooltipy)
├── types/         # typy odpowiedzi API
└── router/        # definicja tras
```

## Integracja z API

- Wszystkie żądania przez `apiFetch` (`src/api/client.ts`) z `credentials: 'include'`.
- Token JWT **nie** jest przechowywany w JS — przeglądarka wysyła cookie `auth_token`
  ustawione przez `POST /auth/login`.
- Upload ESEF: `FormData` z polem `file` (multipart) na `/companies/esef`
  lub `/companies/{id}/financials/esef`.

W dev Vite proxy (`vite.config.ts`) przekierowuje ścieżki API na backend.
W produkcji to samo robi nginx (`nginx.conf`).

## Komendy

```bash
npm run dev       # serwer deweloperski z HMR
npm run build     # build produkcyjny
npm run preview   # podgląd buildu
npm run lint      # ESLint + oxlint
npm run format    # Prettier
```

## IDE

Zalecane: [VS Code](https://code.visualstudio.com/) + rozszerzenie
[Vue (Official)](https://marketplace.visualstudio.com/items?itemName=Vue.volar)
(wyłącz Vetur, jeśli zainstalowany).

Decyzja architektoniczna frontendu: [docs/adr/0008-vue-frontend.md](../docs/adr/0008-vue-frontend.md).
