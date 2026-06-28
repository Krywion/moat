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
`actuator` (health-check), `validation` (walidacja wejścia),
`security` + OAuth2 Resource Server (JWT — zob. [ADR 0005](0005-jwt-auth-strategy.md)).
Build: Maven (zob. [ADR 0004](0004-liquibase-and-maven.md)).

## Alternatywy rozważane

- **Quarkus / Micronaut** — szybszy start, mniejszy footprint; odrzucone na rzecz
  dojrzałości ekosystemu Spring i znajomości zespołu.
- **Node.js / Python** — odrzucone; wymóg spójności z Javą i Spring Data JPA.

## Implementacja

- Punkt wejścia: `backend/src/main/java/com/moat/MoatApplication.java`
- Zależności i wersje: `backend/pom.xml` (`spring-boot-starter-parent` 3.4.x)
- Pakiety domenowe: `com.moat.auth`, `com.moat.company`, `com.moat.pipeline`,
  `com.moat.esef`, `com.moat.market`, `com.moat.report`, `com.moat.user`
- Health-check: `GET /actuator/health` (Actuator)

## Konsekwencje

- **+** Dojrzały, dobrze udokumentowany ekosystem; autokonfiguracja
  skraca boilerplate; Actuator daje health-check „za darmo".
- **+** Spring Security obsługuje JWT i role (zaimplementowane — ADR 0005).
- **−** Większy obraz/zużycie pamięci niż lekkie frameworki — akceptowalne
  przy uruchomieniu w Docker Compose.
- Wymaga JDK 21 do builda; w obrazie Docker rozwiązane przez multi-stage.
