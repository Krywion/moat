package com.moat.market;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Liczy wskaźniki wyceny z kapitalizacji i fundamentów. Czyste funkcje, null-safe.
 * EV = kapitalizacja + dług netto.
 */
@Component
public class MarketValuationCalculator {

    private static final int SCALE = 4;

    public MarketData calculate(MarketQuote quote, BigDecimal netProfit, BigDecimal equity,
                                BigDecimal ebitda, BigDecimal netDebt) {
        BigDecimal marketCap = quote.marketCap();
        BigDecimal ev = (marketCap != null && netDebt != null) ? marketCap.add(netDebt) : null;
        return new MarketData(
                quote.sharePrice(),
                marketCap,
                ratio(marketCap, netProfit),   // P/E = kapitalizacja / zysk netto
                ratio(ev, ebitda),             // EV/EBITDA
                ratio(marketCap, equity),      // P/BV = kapitalizacja / kapitał własny
                quote.dividendYield());
    }

    private static BigDecimal ratio(BigDecimal numerator, BigDecimal denominator) {
        if (numerator == null || denominator == null || denominator.signum() == 0) {
            return null;
        }
        return numerator.divide(denominator, SCALE, RoundingMode.HALF_UP);
    }
}
