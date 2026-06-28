# Postman — Moat API

Kolekcja do ręcznego testowania REST API (auth, spółki, pipeline formularzowy
i ESEF).

## Pliki

- `moat-api.postman_collection.json` — kolekcja (auth + companies)
- `moat-local.postman_environment.json` — środowisko lokalne (`baseUrl=http://localhost:8080`)

## Import

W Postmanie: **Import** → wskaż oba pliki. W prawym górnym rogu wybierz środowisko
**„Moat — local"** (albo użyj zmiennych z kolekcji — `baseUrl` jest w obu).

## Przepływ klikania

1. Uruchom backend: `docker compose up` (root) **lub** `cd backend && mvn spring-boot:run`.
2. **Auth → Register** — raz na dany email (ponowny ten sam email → `409`).
3. **Auth → Login** — JWT przychodzi jako **HttpOnly cookie** `auth_token`.
   Postman zapamiętuje je w cookie jar i automatycznie odsyła w kolejnych żądaniach —
   nie trzeba ręcznie kopiować tokenu.
4. **Auth → Me** — weryfikacja sesji (bez cookie → `401`).
5. **Companies → Create** — tworzy spółkę + pierwszy raport z formularza; skrypt zapisuje `id`
   do zmiennej `companyId`.
6. **Companies → Get by id / Update financials / List** — działają na `{{companyId}}`.
   `Update financials` z kolejnym rokiem pokaże dynamikę r/r.
7. **Companies → Create from ESEF (.xbri upload)** — alternatywa dla formularza przy
   **nowej** spółce: w Body → form-data wskaż plik `.xbri` w wierszu `file` (np.
   `data/raw/zal04_...xbri`). Opcjonalnie `ticker` (symbol giełdowy). Parser czyta iXBRL
   po tagach IFRS, tworzy spółkę (nazwa z raportu) i raport. Zła paczka → `422`.
   Też zapisuje `companyId`.
8. **Companies → Add ESEF year to existing company** — upload `.xbri` dla spółki
   już istniejącej (`POST /companies/{id}/financials/esef`). Nazwa w raporcie musi
   zgadzać się ze spółką; duplikat roku → `409`.
9. **Companies → Refresh market** — ponowne pobranie danych rynkowych Yahoo
   dla najnowszego raportu (`POST /companies/{id}/refresh-market`).
10. **Companies → Delete** — usuwa spółkę wraz z raportami (`204`).
11. **Auth → Logout** — kasuje cookie (`204`).

## Kody błędów

| Kod | Typowy powód |
|-----|----------------|
| `401` | Brak lub wygasły cookie `auth_token` |
| `404` | Spółka nie istnieje lub należy do innego użytkownika |
| `409` | Duplikat emaila (rejestracja), nazwy/tickera spółki lub raportu za ten sam rok |
| `422` | Błąd parsowania ESEF lub niedopasowanie nazwy spółki w `.xbri` |

## Uwagi

- Cookie `auth_token` ma `SameSite=Strict`; w Postmanie (klient, nie przeglądarka)
  nie ma to znaczenia — cookie jar działa per `baseUrl`.
- Wszystkie endpointy poza `register`/`login` wymagają zalogowania (`401` bez cookie).
- Dostęp do cudzej spółki zwraca `404` (świadomie, żeby nie zdradzać istnienia zasobu).
- Kontrakt API: [backend/openapi.yaml](../../backend/openapi.yaml).
