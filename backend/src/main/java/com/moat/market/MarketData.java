package com.moat.market;

import java.math.BigDecimal;

/** Policzone dane rynkowe (Etap 2 — wycena): kurs, kapitalizacja i wskaźniki. */
public record MarketData(
        BigDecimal sharePrice,
        BigDecimal marketCap,
        BigDecimal pe,
        BigDecimal evEbitda,
        BigDecimal pbv,
        BigDecimal dividendYield) {
}
