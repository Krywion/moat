package com.moat.pipeline;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class WarningFlagEvaluator {

    public List<WarningFlag> evaluate(FinancialData current, ComputedIndicators currentIndicators,
                                      FinancialData prior, ComputedIndicators priorIndicators) {
        List<WarningFlag> flags = new ArrayList<>();

        if (isPositive(current.netProfit()) && isNegative(current.operatingCashFlow())) {
            flags.add(WarningFlag.POSITIVE_PROFIT_NEGATIVE_CASHFLOW);
        }

        if (priorIndicators != null
                && currentIndicators.netMargin() != null
                && priorIndicators.netMargin() != null
                && currentIndicators.netMargin().compareTo(priorIndicators.netMargin()) < 0) {
            flags.add(WarningFlag.MARGIN_DECLINING_YOY);
        }

        if (prior != null) {
            BigDecimal debtGrowth = IndicatorCalculator.growth(current.totalDebt(), prior.totalDebt());
            BigDecimal profitGrowth = currentIndicators.profitGrowthYoy();
            if (debtGrowth != null && profitGrowth != null
                    && debtGrowth.compareTo(profitGrowth) > 0) {
                flags.add(WarningFlag.DEBT_GROWING_FASTER_THAN_PROFIT);
            }
        }

        return flags;
    }

    private static boolean isPositive(BigDecimal v) {
        return v != null && v.signum() > 0;
    }

    private static boolean isNegative(BigDecimal v) {
        return v != null && v.signum() < 0;
    }
}
