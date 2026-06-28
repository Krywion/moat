# Postman — Moat API

Kolekcja do ręcznego klikania API (Faza 3: auth + spółki + pipeline formularzowy).

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
4. **Companies → Create** — tworzy spółkę + pierwszy raport; skrypt zapisuje `id`
   do zmiennej `companyId`.
5. **Companies → Get by id / Update financials / List** — działają na `{{companyId}}`.
   `Update financials` z kolejnym rokiem pokaże dynamikę r/r.
6. **Companies → Create from ESEF (.xbri upload)** — alternatywa dla formularza:
   w zakładce Body → form-data wskaż plik `.xbri` w wierszu `file` (np.
   `data/raw/zal04_...xbri`). Parser czyta iXBRL po tagach IFRS, tworzy spółkę
   (nazwa z raportu) i raport. Zła paczka → `422`. Też zapisuje `companyId`.

## Uwagi
- Cookie `auth_token` ma `SameSite=Strict`; w Postmanie (klient, nie przeglądarka)
  nie ma to znaczenia — cookie jar działa per `baseUrl`.
- Wszystkie endpointy poza `register`/`login`/`logout` wymagają zalogowania (`401` bez cookie).
- Dostęp do cudzej spółki zwraca `404` (świadomie, żeby nie zdradzać istnienia zasobu).
