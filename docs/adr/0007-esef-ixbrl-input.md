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

### Dwa przepływy API

1. **Nowa spółka** — `POST /companies/esef` (multipart: `file`, opcjonalnie `ticker`).
   Parser wyciąga nazwę spółki z raportu, tworzy encję `Company` i uruchamia pipeline.
2. **Istniejąca spółka, nowy rok** — `POST /companies/{id}/financials/esef`.
   Nazwa w `.xbri` musi zgadzać się z nazwą spółki w bazie; duplikat roku → `409`.

Parsowanie odbywa się w `CompanyService` przez `EsefParser` *przed*
`PipelineExecutor` (zob. [ADR 0003](0003-pipeline-executor.md)).

## Alternatywy rozważane

- **Parsowanie PDF** — odrzucone; kruche, zależne od layoutu.
- **XBRLAPI** — odrzucone; ciężka, nieutrzymywana zależność.
- **Arelle jako serwis/CLI** — odrzucone na razie; narzut operacyjny (Python obok Javy);
  możliwe do dołożenia później jako walidator dla nietypowych spółek.

## Implementacja

- Fasada parsera: `backend/src/main/java/com/moat/esef/EsefParser.java`
- Rozpakowanie ZIP: `EsefPackageReader.java`
- Parsowanie iXBRL (StAX): `IxbrlParser.java`, `IxbrlValueParser.java`
- Wybór kontekstu sprawozdawczego: `ContextResolver.java`
- Mapowanie tagów IFRS → model: `EsefFinancialMapper.java`
- Wywołania z serwisu: `CompanyService.createCompanyFromEsef`,
  `CompanyService.addFinancialsFromEsef`
- Fixture testowy: paczka Rainbow Tours S.A. w `data/raw/` (test integracyjny parsera)
- Niuanse: NBSP (U+00A0) jako separator tysięcy — normalizowany przed parsowaniem liczby;
  złożenie długu (brak czystego tagu) jest przybliżone i może wymagać korekty per spółka.

## Konsekwencje

- **+** Deterministyczny odczyt, niezależny od layoutu; jeden parser dla wszystkich
  spółek UE.
- **+** Brak ciężkiej zależności XBRL; pełna kontrola nad wydajnością (StAX).
- **−** Złożenie długu (brak czystego tagu) jest przybliżone i może wymagać
  korekty per spółka.
- **−** Sami utrzymujemy mapowanie tagów i heurystykę kontekstu — pokryte testami,
  z realną paczką jako wyrocznią.
