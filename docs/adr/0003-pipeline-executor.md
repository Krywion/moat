# ADR 0003 — Pipeline-executor jako rdzeń architektury

- **Status:** zaakceptowany
- **Data:** 2026-06-20

## Kontekst

Dodanie spółki (`POST /companies`) wymaga ciągu operacji: pobranie danych
(z paczki ESEF lub formularza), agregacja do wspólnego modelu, obliczenie
wskaźników i flag, wzbogacenie danymi rynkowymi, zapis. Etapy mają różne
ryzyka (parsowanie ESEF, niestabilne API rynkowe) i muszą być testowalne
niezależnie.

## Decyzja

Logikę dodania spółki realizujemy jako **pipeline — sekwencję osobnych,
testowalnych kroków**:

1. **Wejście** — dwa warianty dające ten sam model: ESEF (`.xbri` → iXBRL)
   albo formularz.
2. **Agregacja** — surowe dane do jednego modelu `FinancialData`.
3. **Obliczenia** — wskaźniki i flagi ostrzegawcze.
4. **Wzbogacenie** — dane rynkowe z API zewnętrznego (Yahoo).
5. **Zapis** — spółka + raport do bazy.

Każdy krok to niezależny komponent z jasnym interfejsem. Krok 1 ma dwa
warianty; od kroku 2 pipeline jest wspólny.

## Konsekwencje

- **+** Kroki testowane osobno; łatwo podmienić wariant wejścia.
- **+** Ryzykowne etapy izolowane — np. niedostępność API rynkowego
  (krok 4) nie blokuje analizy kondycji firmy (kroki 1–3).
- **+** Pozwala budować przyrostowo: Faza 3 startuje z wejściem
  formularzowym, Faza 4 dokłada parser ESEF.
- **−** Ryzyko over-engineeringu — mitygacja: kroki proste, nie dokładamy
  ich bez potrzeby (ROADMAP sekcja 6).
