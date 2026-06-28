# moat — Analizator Spółek Giełdowych

Webowa aplikacja do analizy fundamentalnej spółek giełdowych. Użytkownik
wrzuca raport finansowy (ESEF `.xbri`) lub wprowadza dane formularzem —
system parsuje sprawozdanie, liczy wskaźniki, wzbogaca danymi rynkowymi
i prezentuje analizę z flagami ostrzegawczymi. Szczegóły produktu:
[docs/ROADMAP.md](docs/ROADMAP.md).

**Stack:** Java 21 + Spring Boot 3 · Vue 3 + TypeScript · PostgreSQL 16 ·
Liquibase · Docker Compose · GitHub Actions CI

## Struktura

```
moat/
├── docker-compose.yml   # postgres + api + frontend
├── backend/             # Spring Boot REST API
├── frontend/            # Vue 3 SPA (Vite, PrimeVue)
├── data/raw/            # przykładowe paczki ESEF (.xbri) do testów
└── docs/
    ├── ROADMAP.md
    ├── adr/             # decyzje architektoniczne (ADR)
    └── postman/         # kolekcja do ręcznego testowania API
```

## Uruchomienie (Docker Compose)

```bash
cp .env.example .env      # ustaw POSTGRES_PASSWORD
docker compose up --build
```

Po starcie:

- **Frontend:** http://localhost:3000
- **API:** http://localhost:8080
- **Health-check:** http://localhost:8080/actuator/health → `{"status":"UP"}`

Migracje Liquibase tworzą schemat (`users`, `companies`,
`financial_reports`) przy starcie aplikacji.

## Development lokalny (bez pełnego compose)

**Backend** — wymaga JDK 21 i PostgreSQL (np. sam kontener `postgres` z compose):

```bash
cd backend
./mvnw spring-boot:run      # Windows: mvnw.cmd spring-boot:run
```

Domyślne połączenie: `jdbc:postgresql://localhost:5432/moat`.

**Frontend** — wymaga Node 24; proxy Vite przekierowuje `/auth/*` i `/companies`
na backend `:8080`:

```bash
npm install                 # w katalogu frontend/
npm run dev                 # lub z root: npm run dev
```

Aplikacja dev: http://localhost:5173 (Vite). Backend musi działać na `:8080`.

## Testy

```bash
cd backend
mvn test                    # lub: ./mvnw test
```

Testy integracyjne startują PostgreSQL przez **Testcontainers** —
wymaga dostępnego Dockera.

CI (push/PR na `main`): build + test backendu (`mvn verify`) oraz
type-check + build frontendu — patrz `.github/workflows/ci.yml`.

## Dokumentacja

| Zasób | Opis |
|-------|------|
| [docs/ROADMAP.md](docs/ROADMAP.md) | Wizja produktu, przepływy, roadmapa |
| [docs/adr/](docs/adr/) | Decyzje architektoniczne (ADR) |
| [backend/openapi.yaml](backend/openapi.yaml) | Kontrakt REST API |
| [docs/postman/](docs/postman/) | Kolekcja Postman + instrukcja |
