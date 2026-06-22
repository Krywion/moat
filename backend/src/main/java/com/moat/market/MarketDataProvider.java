package com.moat.market;

import java.util.Optional;

/** Dostawca danych rynkowych dla symbolu giełdowego. Implementacje best-effort. */
public interface MarketDataProvider {

    Optional<MarketQuote> fetch(String symbol);
}
