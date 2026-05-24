# Roadmapa produktowa — Analizator Spółek Giełdowych

Webowa aplikacja do analizy fundamentalnej spółek giełdowych. Użytkownik
wrzuca raport finansowy spółki, system parsuje go, liczy wskaźniki i
prezentuje gotową analizę wspierającą decyzję inwestycyjną.

**Stack:** Java + Spring Boot · Vue · PostgreSQL · JWT (rola w bazie) · Docker Compose

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

**Dodanie spółki (główny przepływ):** użytkownik wrzuca raport PDF →
pipeline parsuje i liczy → użytkownik widzi wynik i może skorygować dane →
spółka ląduje na jego liście.

**Przegląd:** panel z listą spółek → wejście w konkretną spółkę → pełna
analiza fundamentalna.

---

## 3. Ekrany

### 3.1 Auth (rejestracja / logowanie)
Formularz konta. Po zalogowaniu JWT (token lub ciasteczko z JWT).

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

### Endpointy publiczne
- `POST /auth/register` — rejestracja
- `POST /auth/login` — logowanie, zwraca JWT

### Endpointy chronione
- `GET /companies` — lista spółek użytkownika
- `GET /companies/{id}` — szczegóły i analiza spółki
- `POST /companies` — dodanie spółki (uruchamia pipeline)
- `PUT /companies/{id}/financials` — ręczna korekta danych finansowych

### Pipeline-executor (rdzeń)
`POST /companies` uruchamia pipeline jako sekwencję kroków:

1. **Wejście** — parsowanie PDF (rdzeń) LUB dane z formularza (korekta / fallback)
2. **Agregacja** — surowe dane do jednego modelu `FinancialData`
3. **Obliczenia** — wskaźniki i flagi ostrzegawcze
4. **Wzbogacenie** — uzupełnienie danymi rynkowymi z API zewnętrznego (Yahoo)
5. **Zapis** — spółka + analiza do bazy

Każdy krok to osobny, testowalny komponent. Krok 1 ma dwa warianty
(PDF / formularz) — reszta pipeline'u wspólna.

---

## 5. Model danych

Trzy główne encje w relacjach:
- **User** — konto, rola (np. `USER` / `ADMIN`)
- **Company** — spółka na liście użytkownika (relacja do User)
- **FinancialReport** — sprawozdanie za dany rok + policzone wskaźniki
  (relacja do Company; spółka ma wiele raportów → trendy r/r)

Zasada: wskaźnik nigdy nie jest zapisany bez surowych danych, z których
powstał — umożliwia weryfikację i ochronę przed błędem parsera.

---

## 6. Ryzyka

| Ryzyko | Wpływ | Mitygacja |
|--------|-------|-----------|
| Parser PDF zawodzi na nietypowym raporcie | Wysoki — rdzeń projektu | Ręczna korekta danych jako drugie wejście pipeline'u; testy na wielu realnych raportach wcześnie |
| Yahoo API niestabilne / niedostępne | Średni | Etap 2 jako funkcja dodatkowa; etap 1 nie zależy od API |
| Over-engineering pipeline'u | Średni | Każdy krok prosty; nie dokładać kroków bez potrzeby |

---

## 7. Roadmapa wykonawcza

### Faza 1 — Fundament
- [ ] Repo, README, szkic architektury
- [ ] Decyzje stacku spisane jako pierwsze wpisy ADR
- [ ] Pierwszy commit od obu osób

### Faza 2 — Szkielet
- [ ] docker-compose: PostgreSQL + API, health-check
- [ ] Model danych (User / Company / FinancialReport), pierwsza migracja
- [ ] ADR: pipeline-executor, Spring Boot, PostgreSQL

### Faza 3 — Rdzeń
- [ ] Auth: register / login, JWT, rola w bazie
- [ ] Endpointy listy i szczegółów spółki
- [ ] Pipeline — szkielet z krokami, na razie wejście formularzowe
- [ ] Frontend: auth, panel z listą spółek
- [ ] ADR: Vue, JWT zamiast Keycloaka, komunikacja REST

### Faza 4 — Parser i analiza
- [ ] Parsowanie PDF — krok 1 pipeline'u
- [ ] Obliczenia wskaźników + flagi (krok 3)
- [ ] Wzbogacenie danymi rynkowymi (krok 4)
- [ ] Panel analizy spółki: etap 1, etap 2, tooltipy

### Faza 5 — Szlif
- [ ] Obsługa błędów, walidacja wejścia
- [ ] Seed data, min. 10 testów (rdzeń: obliczenia)
- [ ] CI: lint + testy
- [ ] README, finalizacja ADR (6+ wpisów)
- [ ] Test docker-compose na czystej maszynie

---

## 8. Definicja sukcesu

Produktowo: użytkownik wrzuca raport i w kilka sekund dostaje czytelną,
zrozumiałą analizę — z wytłumaczeniem każdego wskaźnika.
