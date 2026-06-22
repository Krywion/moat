# ADR 0006 — Komunikacja REST i owner-scoping

- **Status:** zaakceptowany
- **Data:** 2026-06-22

## Kontekst

Frontend (Vue) komunikuje się z backendem przez API. Zasoby (spółki, raporty)
należą do konkretnego użytkownika i nie mogą być widoczne dla innych.

## Decyzja

- **REST/JSON** jako kontrakt frontend–backend; zasoby pod `/companies`,
  czasowniki HTTP (`GET`/`POST`/`PUT`), kody statusu jako sygnał wyniku.
- **Owner-scoping w warstwie serwisu:** każdy dostęp do spółki filtruje po
  właścicielu (`findByIdAndOwnerId`). Brak dopasowania → **`404`** (nie `403`),
  żeby nie zdradzać istnienia cudzego zasobu. Id użytkownika z claima `sub` JWT.

## Konsekwencje

- **+** Prosty, przewidywalny kontrakt; standardowe kody błędów.
- **+** Izolacja danych między użytkownikami wymuszona w jednym miejscu (serwis).
- **−** `404` zamiast `403` może utrudniać debug („czy zasób istnieje?") —
  akceptowalny koszt za brak wycieku informacji.
- Odrzucone: filtrowanie w kontrolerze (rozproszone), `403` (zdradza istnienie).
