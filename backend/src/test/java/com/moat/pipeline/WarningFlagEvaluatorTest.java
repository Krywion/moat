package com.moat.pipeline;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class WarningFlagEvaluatorTest {

    private final WarningFlagEvaluator evaluator = new WarningFlagEvaluator();
    private final IndicatorCalculator calc = new IndicatorCalculator();

    private FinancialData fd(int year, BigDecimal revenue, BigDecimal ebit, BigDecimal depreciation,
                             BigDecimal netProfit, BigDecimal totalDebt, BigDecimal equity,
                             BigDecimal operatingCashFlow) {
        return new FinancialData(year, "PLN", revenue, ebit, depreciation, netProfit,
                totalDebt, null, equity, operatingCashFlow);
    }

    @Test
    void flagsPositiveProfitWithNegativeCashFlow() {
        FinancialData current = fd(2024, bd(1000), bd(200), bd(50), bd(100), bd(300), bd(500), bd(-20));
        ComputedIndicators ind = calc.calculate(current, null);

        List<WarningFlag> flags = evaluator.evaluate(current, ind, null, null);

        assertThat(flags).contains(WarningFlag.POSITIVE_PROFIT_NEGATIVE_CASHFLOW);
    }

    @Test
    void flagsMarginDecliningYoy() {
        FinancialData prior = fd(2023, bd(1000), bd(200), bd(50), bd(150), bd(300), bd(500), bd(80));
        FinancialData current = fd(2024, bd(1000), bd(200), bd(50), bd(100), bd(300), bd(500), bd(80));
        ComputedIndicators priorInd = calc.calculate(prior, null);
        ComputedIndicators ind = calc.calculate(current, prior);

        List<WarningFlag> flags = evaluator.evaluate(current, ind, prior, priorInd);

        assertThat(flags).contains(WarningFlag.MARGIN_DECLINING_YOY); // 0.10 < 0.15
    }

    @Test
    void flagsDebtGrowingFasterThanProfit() {
        FinancialData prior = fd(2023, bd(1000), bd(200), bd(50), bd(100), bd(100), bd(500), bd(80));
        FinancialData current = fd(2024, bd(1000), bd(200), bd(50), bd(110), bd(200), bd(500), bd(80));
        ComputedIndicators priorInd = calc.calculate(prior, null);
        ComputedIndicators ind = calc.calculate(current, prior);

        List<WarningFlag> flags = evaluator.evaluate(current, ind, prior, priorInd);

        // debt growth 1.00 (100->200) > profit growth 0.10 (100->110)
        assertThat(flags).contains(WarningFlag.DEBT_GROWING_FASTER_THAN_PROFIT);
    }

    @Test
    void healthyCompanyHasNoFlags() {
        FinancialData prior = fd(2023, bd(1000), bd(200), bd(50), bd(100), bd(300), bd(500), bd(120));
        FinancialData current = fd(2024, bd(1200), bd(260), bd(60), bd(150), bd(310), bd(600), bd(140));
        ComputedIndicators priorInd = calc.calculate(prior, null);
        ComputedIndicators ind = calc.calculate(current, prior);

        List<WarningFlag> flags = evaluator.evaluate(current, ind, prior, priorInd);

        assertThat(flags).isEmpty();
    }

    private static BigDecimal bd(long v) {
        return BigDecimal.valueOf(v);
    }
}
