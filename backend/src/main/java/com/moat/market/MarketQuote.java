package com.moat.market;

import java.math.BigDecimal;

/** Surowe dane rynkowe od dostawcy (np. Yahoo). */
public record MarketQuote(BigDecimal sharePrice, BigDecimal marketCap, BigDecimal dividendYield) {
}
