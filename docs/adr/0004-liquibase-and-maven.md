# ADR 0004 — Liquibase do migracji + Maven/Java 21

- **Status:** zaakceptowany
- **Data:** 2026-06-20

## Kontekst

Schemat bazy musi być wersjonowany i odtwarzalny na czystej maszynie.
Potrzebny też spójny build i wersja Javy dla zespołu i obrazu Docker.
Kontrakt REST musi być zsynchronizowany z kodem backendu i frontendem.

## Decyzja

- **Migracje: Liquibase.** Changelog YAML (`db.changelog-master.yaml`
  → `changes/`), uruchamiany automatycznie przy starcie aplikacji.
  Hibernate działa w trybie `ddl-auto: validate` — Liquibase jest jedynym
  źródłem prawdy o schemacie, a Hibernate jedynie weryfikuje zgodność encji.
- **Build: Maven** z `spring-boot-starter-parent` (zarządzanie wersjami
  zależności).
- **Java 21 LTS.**
- **OpenAPI Generator** w fazie `generate-sources`: plik `backend/openapi.yaml`
  generuje DTO w `com.moat.api.model.*` — kontrakt API jako źródło prawdy
  dla typów żądań i odpowiedzi (zob. [ADR 0006](0006-rest-and-owner-scoping.md)).

## Alternatywy rozważane

- **Flyway** (migracje czysto SQL) — odrzucone na rzecz changelogów YAML
  niezależnych od dialektu; różnica drugorzędna dla skali projektu.
- **Ręczne DTO bez OpenAPI** — odrzucone; ryzyko rozjazdu kontraktu
  między backendem a dokumentacją.

## Implementacja

- Liquibase master: `backend/src/main/resources/db/changelog/db.changelog-master.yaml`
- Changesety: `backend/src/main/resources/db/changelog/changes/`
- OpenAPI spec: `backend/openapi.yaml`
- Generator: `openapi-generator-maven-plugin` w `backend/pom.xml`
  (wyjście: `target/generated-sources/openapi/`)
- Testy integracyjne na Testcontainers: `backend/src/test/java/com/moat/support/AbstractIntegrationTest.java`

## Konsekwencje

- **+** Schemat odtwarzalny i wersjonowany; `validate` wykrywa rozjazd
  encje ↔ migracje (weryfikowane testem integracyjnym na Testcontainers).
- **+** Zmiany schematu jako jawne changesety w review.
- **+** Jedna specyfikacja OpenAPI dla API, Postmana i typów frontendowych.
- **−** Każda zmiana modelu wymaga ręcznego dopisania changeseta (brak
  auto-generacji DDL) — świadomy koszt w zamian za kontrolę.
- **−** Zmiana kontraktu API wymaga edycji `openapi.yaml` i regeneracji DTO przy buildzie.
