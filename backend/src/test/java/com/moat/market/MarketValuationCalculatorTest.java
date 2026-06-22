package com.moat.market;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class MarketValuationCalculatorTest {

    private final MarketValuationCalculator calc = new MarketValuationCalculator();

    private MarketQuote quote(long marketCap) {
        return new MarketQuote(BigDecimal.valueOf(12.5), BigDecimal.valueOf(marketCap), BigDecimal.valueOf(0.03));
    }

    @Test
    void computesValuationRatios() {
        MarketData m = calc.calculate(quote(2000),
                BigDecimal.valueOf(100),   // netProfit
                BigDecimal.valueOf(500),   // equity
                BigDecimal.valueOf(250),   // ebitda
                BigDecimal.valueOf(300));  // netDebt

        assertThat(m.sharePrice()).isEqualByComparingTo("12.5");
        assertThat(m.marketCap()).isEqualByComparingTo("2000");
        assertThat(m.pe()).isEqualByComparingTo("20.0000");      // 2000 / 100
        assertThat(m.pbv()).isEqualByComparingTo("4.0000");       // 2000 / 500
        assertThat(m.evEbitda()).isEqualByComparingTo("9.2000");  // (2000 + 300) / 250
        assertThat(m.dividendYield()).isEqualByComparingTo("0.03");
    }

    @Test
    void nullOrZeroDenominatorYieldsNull() {
        MarketData m = calc.calculate(quote(2000), null, BigDecimal.ZERO, null, null);

        assertThat(m.pe()).isNull();        // null netProfit
        assertThat(m.pbv()).isNull();        // zero equity
        assertThat(m.evEbitda()).isNull();   // null netDebt → EV null, and null ebitda
        assertThat(m.marketCap()).isEqualByComparingTo("2000");
    }
}
