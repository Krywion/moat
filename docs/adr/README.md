# Architecture Decision Records (ADR)

Decyzje architektoniczne projektu **moat** w formacie MADR-light (po polsku).
Każdy wpis opisuje kontekst, podjętą decyzję, rozważane alternatywy,
implementację w kodzie oraz konsekwencje.

## Szablon

1. **Status / Data**
2. **Kontekst**
3. **Decyzja**
4. **Alternatywy rozważane**
5. **Implementacja**
6. **Konsekwencje**

## Indeks

| Nr | Tytuł | Status | Data | Opis |
|----|-------|--------|------|------|
| [0001](0001-spring-boot.md) | Spring Boot jako framework backendu | zaakceptowany | 2026-06-20 | Java 21, Spring Boot 3.x, Maven |
| [0002](0002-postgresql.md) | PostgreSQL jako baza danych | zaakceptowany | 2026-06-20 | PostgreSQL 16, NUMERIC, UUID |
| [0003](0003-pipeline-executor.md) | Pipeline-executor jako rdzeń architektury | zaakceptowany | 2026-06-20 | Sekwencja kroków analizy finansowej |
| [0004](0004-liquibase-and-maven.md) | Liquibase + Maven/Java 21 | zaakceptowany | 2026-06-20 | Migracje, OpenAPI Generator |
| [0005](0005-jwt-auth-strategy.md) | JWT w HttpOnly cookie | zaakceptowany | 2026-06-22 | Uwierzytelnianie bez zewnętrznego IdP |
| [0006](0006-rest-and-owner-scoping.md) | REST i owner-scoping | zaakceptowany | 2026-06-22 | Kontrakt API, izolacja danych użytkownika |
| [0007](0007-esef-ixbrl-input.md) | ESEF/iXBRL jako format wejścia | zaakceptowany | 2026-06-22 | Parser `.xbri`, tagi IFRS |
| [0008](0008-vue-frontend.md) | Vue 3 jako frontend SPA | zaakceptowany | 2026-06-28 | Vue, Vite, Pinia, PrimeVue |

Powiązane dokumenty: [ROADMAP](../ROADMAP.md), [OpenAPI](../../backend/openapi.yaml),
[kolekcja Postman](../postman/).
