# ADR 0004 — Liquibase do migracji + Maven/Java 21

- **Status:** zaakceptowany
- **Data:** 2026-06-20

## Kontekst

Schemat bazy musi być wersjonowany i odtwarzalny na czystej maszynie
(wymóg Fazy 5: test `docker-compose` na świeżym środowisku). Potrzebny też
spójny build i wersja Javy dla zespołu i obrazu Docker.

## Decyzja

- **Migracje: Liquibase.** Changelog YAML (`db.changelog-master.yaml`
  → `changes/`), uruchamiany automatycznie przy starcie aplikacji.
  Hibernate działa w trybie `ddl-auto: validate` — Liquibase jest jedynym
  źródłem prawdy o schemacie, a Hibernate jedynie weryfikuje zgodność encji.
- **Build: Maven** z `spring-boot-starter-parent` (zarządzanie wersjami
  zależności).
- **Java 21 LTS.**

## Konsekwencje

- **+** Schemat odtwarzalny i wersjonowany; `validate` wykrywa rozjazd
  encje ↔ migracje (weryfikowane testem integracyjnym na Testcontainers).
- **+** Zmiany schematu jako jawne changesety w review.
- **−** Każda zmiana modelu wymaga ręcznego dopisania changeseta (brak
  auto-generacji DDL) — świadomy koszt w zamian za kontrolę.
- Alternatywa Flyway (migracje czysto SQL) odrzucona na rzecz changelogów
  niezależnych od dialektu; różnica drugorzędna dla skali projektu.
