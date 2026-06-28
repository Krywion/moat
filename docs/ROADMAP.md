# Roadmapa produktowa — Analizator Spółek Giełdowych

Webowa aplikacja do analizy fundamentalnej spółek giełdowych. Użytkownik
wrzuca raport finansowy spółki, system parsuje go, liczy wskaźniki i
prezentuje gotową analizę wspierającą decyzję inwestycyjną.

**Stack:** Java 21 + Spring Boot 3 · Vue 3 · PostgreSQL 16 · JWT (HttpOnly cookie, rola w bazie) · Docker Compose

---

## Stan implementacji (2026-06)

End-to-end działa ścieżka: rejestracja → logowanie → dodanie spółki
(formularz lub upload ESEF `.xbri`) → pipeline (wskaźniki, flagi, Yahoo) →
podgląd analizy w UI z tooltipami. Uruchomienie: `docker compose up` —
frontend `:3000`, API `:8080`.

Fazy 1–4 ukończone. Faza 5 częściowo (CI, testy, obsługa błędów 4xx) —
szczegóły w sekcji 7.

Decyzje architektoniczne: [adr/](adr/) (8 wpisów ADR).

---

## 1. Wizja produktu

Drobny inwestor nie ma czasu ręcznie przekopywać sprawozdań finansowych.
Aplikacja robi mechaniczną część za niego: wyciąga dane z raportu, liczy
wskaźniki, pokazuje trendy i podświetla sygnały ostrzegawcze. Decyzję
podejmuje człowiek — narzędzie przyspiesza analizę, nie zastępuje jej.

**Zasada produktowa:** każdy wskaźnik jest wytłumaczony w UI (tooltip).
Aplikacja ma uczyć, nie tylko liczyć.

---

## 2. Przepływy użytkownika

**Onboarding:** rejestracja konta → logowanie → pusty panel z zachętą do
dodania pierwszej spółki.

**Dodanie spółki (główny przepływ):** użytkownik wrzuca raport roczny
w formacie ESEF (paczka `.xbri`) → pipeline rozpakowuje, parsuje i liczy →
użytkownik widzi wynik → spółka ląduje na jego liście.

**Dodanie spółki bez raportu ESEF:** dla spółek spoza GPW lub raportów
sprzed obowiązku ESEF — ręczne wprowadzenie danych finansowych formularzem.

**Przegląd:** panel z listą spółek → wejście w konkretną spółkę → pełna
analiza fundamentalna.

---

## 3. Ekrany

### 3.1 Auth (rejestracja / logowanie)
Formularz konta. Po zalogowaniu JWT w **HttpOnly cookie** `auth_token`
(zob. [ADR 0005](adr/0005-jwt-auth-strategy.md)).

### 3.2 Panel — lista spółek
Lista spółek użytkownika z kluczowym wskaźnikiem skrótowym przy każdej.
Przycisk „Dodaj spółkę do analizy".

### 3.3 Panel analizy spółki
Sekcja kluczowa ekranu. Dwa etapy, wizualnie rozdzielone.

**Etap 1 — kondycja firmy (góra ekranu):**

Dane surowe ze sprawozdania (pozycje wprost z raportu):
- Przychody
- Zysk operacyjny (EBIT)
- Amortyzacja
- Zysk netto
- Dług (całkowity / netto)
- Kapitał własny
- Przepływy operacyjne (operacyjny cash flow)

Wartości i wskaźniki liczone:
- EBITDA (kwota) — EBIT + amortyzacja
- Marża operacyjna — EBIT / przychody
- Marża EBITDA — EBITDA / przychody
- Marża netto — zysk netto / przychody
- ROE — zysk netto / kapitał własny
- Dług / kapitał własny
- Dynamika r/r przychodów i zysku

**Etap 2 — wycena (niżej na ekranie):**
- P/E — cena / zysk na akcję
- EV/EBITDA
- P/BV — cena / wartość księgowa
- Stopa dywidendy

Etap 2 wymaga danych rynkowych (kurs, kapitalizacja) — stąd osobno.

**Wymóg UI:** każdy wskaźnik ma ikonę „?" z tooltipem — krótki opis
(1-2 zdania), co wskaźnik oznacza i jak go czytać.

**Flagi ostrzegawcze:** widoczne sygnały, np. dodatni zysk netto przy
ujemnym cash flow, marża spadająca r/r, dług rosnący szybciej niż zysk.

---

## 4. Architektura backendu

Kontrakt API: [backend/openapi.yaml](../backend/openapi.yaml).

### Endpointy publiczne
- `POST /auth/register` — rejestracja
- `POST /auth/login` — logowanie, ustawia cookie `auth_token`

### Endpointy chronione (auth)
- `POST /auth/logout` — wylogowanie, kasuje cookie
- `GET /auth/me` — bieżący użytkownik

### Endpointy chronione (spółki)
- `GET /companies` — lista spółek użytkownika
- `POST /companies` — dodanie spółki z formularzem (uruchamia pipeline)
- `POST /companies/esef` — dodanie spółki z uploadu `.xbri`
- `GET /companies/{id}` — szczegóły i analiza spółki
- `DELETE /companies/{id}` — usunięcie spółki z raportami
- `PUT /companies/{id}/financials` — ręczne wprowadzenie/edycja danych finansowych
- `POST /companies/{id}/financials/esef` — dodanie roku z uploadu `.xbri`
- `POST /companies/{id}/refresh-market` — odświeżenie danych rynkowych

### Pipeline-executor (rdzeń)

**Przygotowanie wejścia** (warstwa serwisu, przed pipeline):

- **ESEF** — `POST /companies/esef` lub `POST /companies/{id}/financials/esef`:
  rozpakowanie `.xbri` → iXBRL → tagi IFRS → model danych
- **Formularz** — `POST /companies` lub `PUT /companies/{id}/financials`:
  dane z żądania JSON

**Pipeline** (`PipelineExecutor`) — wspólny dla obu wejść:

1. **Agregacja** — surowe dane do jednego modelu `FinancialData`
2. **Obliczenia** — wskaźniki i flagi ostrzegawcze
3. **Wzbogacenie** — dane rynkowe z Yahoo Finance
4. **Zapis** — raport finansowy do bazy

Szczegóły: [ADR 0003](adr/0003-pipeline-executor.md), [ADR 0007](adr/0007-esef-ixbrl-input.md).

Parsowanie iXBRL jest deterministyczne: dane w raporcie ESEF są otagowane
wg taksonomii IFRS, więc odczyt nie zależy od layoutu dokumentu i działa
identycznie dla każdej spółki giełdowej w UE.

---

## 5. Model danych

Trzy główne encje w relacjach:
- **User** — konto, rola (np. `USER` / `ADMIN`)
- **Company** — spółka na liście użytkownika (relacja do User)
- **FinancialReport** — sprawozdanie za dany rok + policzone wskaźniki
  (relacja do Company; spółka ma wiele raportów → trendy r/r)

Zasada: wskaźnik nigdy nie jest zapisany bez surowych danych, z których
powstał — umożliwia weryfikację i wychwycenie błędu parsowania.

---

## 6. Ryzyka

| Ryzyko | Wpływ | Mitygacja |
|--------|-------|-----------|
| Trudności techniczne z parsowaniem ESEF (ZIP, namespace'y XML, taksonomia) | Niski — format jest ustrukturyzowany i deterministyczny | Wczesny prototyp parsera na realnej paczce `.xbri`; iXBRL nie zależy od layoutu |
| Nietypowe/niekompletne otagowanie w raporcie konkretnej spółki | Niski | Brakujący tag = brak danej, jawnie obsłużony; formularz jako wejście zastępcze |
| Yahoo API niestabilne / niedostępne | Średni | Etap 2 jako funkcja dodatkowa; etap 1 nie zależy od API |
| Over-engineering pipeline'u | Średni | Każdy krok prosty; nie dokładać kroków bez potrzeby |

---

## 7. Roadmapa wykonawcza

### Faza 1 — Fundament
- [x] Repo, README, szkic architektury
- [x] Decyzje stacku spisane jako pierwsze wpisy ADR
- [ ] Pierwszy commit od obu osób

### Faza 2 — Szkielet
- [x] docker-compose: PostgreSQL + API + frontend, health-check
- [x] Model danych (User / Company / FinancialReport), pierwsza migracja
- [x] ADR: pipeline-executor, Spring Boot, PostgreSQL

### Faza 3 — Rdzeń
- [x] Auth: register / login / logout / me, JWT, rola w bazie
- [x] Endpointy listy i szczegółów spółki
- [x] Pipeline — kroki agregacja/kalkulacja/wzbogacenie/zapis, wejście formularzowe
- [x] Frontend: auth, panel z listą spółek
- [x] ADR: Vue, JWT zamiast Keycloaka, komunikacja REST

### Faza 4 — Parser i analiza
- [x] Parser ESEF — rozpakowanie `.xbri`, odnalezienie pliku raportu
- [x] Parsowanie iXBRL — odczyt wartości po tagach IFRS
- [x] Obliczenia wskaźników + flagi
- [x] Wzbogacenie danymi rynkowymi (Yahoo)
- [x] Panel analizy spółki: etap 1, etap 2, tooltipy
- [x] ADR: ESEF/iXBRL jako format wejścia (zamiast parsowania PDF)

### Faza 5 — Szlif
- [x] Obsługa błędów, walidacja wejścia (`CompanyExceptionHandler`, kody 4xx)
- [ ] Seed data
- [x] Min. 10 testów (rdzeń: obliczenia, pipeline, ESEF, auth)
- [x] CI: build + testy (backend `verify`, frontend build)
- [x] README, finalizacja ADR (8 wpisów)
- [ ] Test docker-compose na czystej maszynie

---

## 8. Definicja sukcesu

Produktowo: użytkownik wrzuca raport i w kilka sekund dostaje czytelną,
zrozumiałą analizę — z wytłumaczeniem każdego wskaźnika.
