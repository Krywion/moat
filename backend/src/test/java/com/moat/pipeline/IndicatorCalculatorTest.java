package com.moat.pipeline;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class IndicatorCalculatorTest {

    private final IndicatorCalculator calc = new IndicatorCalculator();

    private FinancialData data(BigDecimal revenue, BigDecimal ebit, BigDecimal depreciation,
                               BigDecimal netProfit, BigDecimal totalDebt, BigDecimal equity) {
        return new FinancialData(2024, "PLN", revenue, ebit, depreciation, netProfit,
                totalDebt, null, equity, null);
    }

    @Test
    void computesEbitdaAndMargins() {
        FinancialData d = data(bd(1000), bd(200), bd(50), bd(100), bd(300), bd(500));

        ComputedIndicators i = calc.calculate(d, null);

        assertThat(i.ebitda()).isEqualByComparingTo("250");        // 200 + 50
        assertThat(i.operatingMargin()).isEqualByComparingTo("0.2000"); // 200/1000
        assertThat(i.ebitdaMargin()).isEqualByComparingTo("0.2500");    // 250/1000
        assertThat(i.netMargin()).isEqualByComparingTo("0.1000");       // 100/1000
        assertThat(i.roe()).isEqualByComparingTo("0.2000");             // 100/500
        assertThat(i.debtToEquity()).isEqualByComparingTo("0.6000");    // 300/500
    }

    @Test
    void ratioWithZeroDenominatorIsNull() {
        FinancialData d = data(BigDecimal.ZERO, bd(200), bd(50), bd(100), bd(300), BigDecimal.ZERO);

        ComputedIndicators i = calc.calculate(d, null);

        assertThat(i.netMargin()).isNull();   // /0 revenue
        assertThat(i.roe()).isNull();          // /0 equity
    }

    @Test
    void nullInputYieldsNullIndicator() {
        FinancialData d = data(bd(1000), null, bd(50), null, null, bd(500));

        ComputedIndicators i = calc.calculate(d, null);

        assertThat(i.ebitda()).isNull();          // ebit null
        assertThat(i.operatingMargin()).isNull(); // ebit null
        assertThat(i.netMargin()).isNull();       // netProfit null
    }

    @Test
    void yoyGrowthUsesPriorYear() {
        FinancialData prior = data(bd(1000), bd(200), bd(50), bd(100), bd(300), bd(500));
        FinancialData current = data(bd(1200), bd(240), bd(60), bd(150), bd(330), bd(600));

        ComputedIndicators i = calc.calculate(current, prior);

        assertThat(i.revenueGrowthYoy()).isEqualByComparingTo("0.2000"); // (1200-1000)/1000
        assertThat(i.profitGrowthYoy()).isEqualByComparingTo("0.5000");  // (150-100)/100
    }

    @Test
    void yoyGrowthNullWhenNoPrior() {
        FinancialData current = data(bd(1200), bd(240), bd(60), bd(150), bd(330), bd(600));

        ComputedIndicators i = calc.calculate(current, null);

        assertThat(i.revenueGrowthYoy()).isNull();
        assertThat(i.profitGrowthYoy()).isNull();
    }

    private static BigDecimal bd(long v) {
        return BigDecimal.valueOf(v);
    }
}
