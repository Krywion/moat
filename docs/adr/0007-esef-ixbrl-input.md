# ADR 0007 — ESEF/iXBRL jako format wejścia (zamiast PDF)

- **Status:** zaakceptowany
- **Data:** 2026-06-22

## Kontekst

Dane finansowe trzeba wyciągnąć z raportów spółek giełdowych. PDF wymagałby
kruchego parsowania layoutu (różnego dla każdej spółki). Spółki z UE publikują
raporty roczne w formacie ESEF (paczka `.xbri` z iXBRL otagowanym wg taksonomii
IFRS).

## Decyzja

- **Wejście: ESEF (`.xbri`)**, parsowane po nazwach tagów IFRS (np.
  `ifrs-full:Revenue`). Odczyt nie zależy od layoutu dokumentu — deterministyczny
  dla każdej spółki w UE.
- **Własny ekstraktor StAX** zamiast biblioteki XBRL: Java nie ma dobrej lekkiej
  biblioteki (Arelle to Python; XBRLAPI jest ciężki i nieutrzymywany), a my
  potrzebujemy tylko kilkunastu faktów z głównych sprawozdań. Strumieniowo
  (raport ~18 MB), bez DOM, XXE-safe.
- **Rozwiązanie kontekstu:** wartość bieżącego okresu, kontekst bez wymiarów
  (skonsolidowana pozycja główna).
- **Brakujący tag = brak danej** (`null`), obsłużony jawnie; formularz pozostaje
  wejściem zastępczym.

## Konsekwencje

- **+** Deterministyczny odczyt, niezależny od layoutu; jeden parser dla wszystkich
  spółek UE.
- **+** Brak ciężkiej zależności XBRL; pełna kontrola nad wydajnością (StAX).
- **−** Złożenie długu (brak czystego tagu) jest przybliżone i może wymagać
  korekty per spółka.
- **−** Sami utrzymujemy mapowanie tagów i heurystykę kontekstu — pokryte testami,
  z realną paczką (Rainbow Tours S.A.) jako wyrocznią.
- Niuanse formatu wychwycone na realnym pliku, np. NBSP (U+00A0) jako separator
  tysięcy — normalizowany przed parsowaniem liczby.
- Odrzucone: parsowanie PDF (kruche), biblioteka XBRLAPI (ciężka), Arelle jako
  serwis/CLI (narzut operacyjny: Python obok Javy) — możliwe do dołożenia później
  jako walidator dla nietypowych spółek.
