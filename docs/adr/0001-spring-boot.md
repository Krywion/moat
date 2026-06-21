# ADR 0001 — Spring Boot jako framework backendu

- **Status:** zaakceptowany
- **Data:** 2026-06-20

## Kontekst

Backend ma udostępniać REST API (auth, lista i szczegóły spółek,
uruchomienie pipeline'u), łączyć się z PostgreSQL, obsługiwać migracje
i być uruchamialny w Dockerze. Zespół pracuje w ekosystemie Java.

## Decyzja

Backend budujemy na **Spring Boot 3.x (Java 21)**.

Korzystamy ze starterów: `web` (REST), `data-jpa` (dostęp do bazy),
`actuator` (health-check), `validation` (walidacja wejścia). Build:
Maven (zob. [ADR 0004](0004-liquibase-and-maven.md)).

## Konsekwencje

- **+** Dojrzały, dobrze udokumentowany ekosystem; autokonfiguracja
  skraca boilerplate; Actuator daje health-check „za darmo".
- **+** Spring Security w Fazie 3 naturalnie obsłuży JWT i role.
- **−** Większy obraz/zużycie pamięci niż lekkie frameworki — akceptowalne
  przy uruchomieniu w Docker Compose.
- Wymaga JDK 21 do builda; w obrazie Docker rozwiązane przez multi-stage.
