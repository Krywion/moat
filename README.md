# moat — Analizator Spółek Giełdowych

Webowa aplikacja do analizy fundamentalnej spółek giełdowych. Użytkownik
wrzuca raport finansowy, system parsuje go, liczy wskaźniki i prezentuje
gotową analizę. Szczegóły: [docs/ROADMAP.md](docs/ROADMAP.md).

**Stack:** Java 21 + Spring Boot · Vue (Faza 3) · PostgreSQL · Liquibase ·
Docker Compose

## Struktura

```
moat/
├── docker-compose.yml   # postgres + api
├── backend/             # Spring Boot API
├── frontend/            # Vue SPA (placeholder, Faza 3)
└── docs/
    ├── ROADMAP.md
    ├── adr/             # decyzje architektoniczne
    └── superpowers/specs/
```

## Uruchomienie (Docker Compose)

```bash
cp .env.example .env      # ustaw POSTGRES_PASSWORD
docker compose up --build
```

Po starcie:

- API: http://localhost:8080
- Health-check: http://localhost:8080/actuator/health → `{"status":"UP"}`

Migracje Liquibase tworzą schemat (`users`, `companies`,
`financial_reports`) przy starcie aplikacji.

## Backend — lokalnie (bez Dockera)

Wymaga JDK 21 i działającego PostgreSQL (np. tylko kontener `postgres`
z compose). Domyślne połączenie: `jdbc:postgresql://localhost:5432/moat`.

```bash
cd backend
./mvnw spring-boot:run      # lub: mvn spring-boot:run
```

## Testy

```bash
cd backend
mvn test
```

Test integracyjny startuje PostgreSQL przez **Testcontainers** —
wymaga dostępnego Dockera.

## Dokumentacja architektury

Decyzje projektowe spisane w [docs/adr/](docs/adr/) (format ADR/MADR).
Stan prac: [docs/ROADMAP.md](docs/ROADMAP.md).
