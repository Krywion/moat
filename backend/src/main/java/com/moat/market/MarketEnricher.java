package com.moat.market;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Best-effort wzbogacenie danymi rynkowymi. Brak tickera, wyłączone API lub
 * dowolny błąd → {@link Optional#empty()} (Etap 1 analizy nie zależy od API).
 */
@Component
public class MarketEnricher {

    private final MarketDataProvider provider;
    private final MarketValuationCalculator calculator;
    private final MarketProperties properties;

    public MarketEnricher(MarketDataProvider provider, MarketValuationCalculator calculator,
                          MarketProperties properties) {
        this.provider = provider;
        this.calculator = calculator;
        this.properties = properties;
    }

    public Optional<MarketData> enrich(String ticker, BigDecimal netProfit, BigDecimal equity,
                                       BigDecimal ebitda, BigDecimal netDebt) {
        if (!properties.enabled() || ticker == null || ticker.isBlank()) {
            return Optional.empty();
        }
        try {
            String symbol = toSymbol(ticker.trim());
            return provider.fetch(symbol)
                    .map(quote -> calculator.calculate(quote, netProfit, equity, ebitda, netDebt));
        } catch (RuntimeException e) {
            return Optional.empty();
        }
    }

    private String toSymbol(String ticker) {
        return ticker.contains(".") ? ticker : ticker + properties.defaultSuffix();
    }
}
