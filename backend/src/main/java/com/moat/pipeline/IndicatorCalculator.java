package com.moat.pipeline;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class IndicatorCalculator {

    private static final int SCALE = 4;

    public ComputedIndicators calculate(FinancialData current, FinancialData prior) {
        BigDecimal ebitda = add(current.ebit(), current.depreciation());
        return new ComputedIndicators(
                ebitda,
                ratio(current.ebit(), current.revenue()),
                ratio(ebitda, current.revenue()),
                ratio(current.netProfit(), current.revenue()),
                ratio(current.netProfit(), current.equity()),
                ratio(current.totalDebt(), current.equity()),
                prior == null ? null : growth(current.revenue(), prior.revenue()),
                prior == null ? null : growth(current.netProfit(), prior.netProfit()));
    }

    /** Suma; null jeśli którykolwiek składnik null. */
    private static BigDecimal add(BigDecimal a, BigDecimal b) {
        if (a == null || b == null) {
            return null;
        }
        return a.add(b);
    }

    /** Iloraz zaokrąglony do scale; null jeśli brak danej lub mianownik zerowy. */
    private static BigDecimal ratio(BigDecimal numerator, BigDecimal denominator) {
        if (numerator == null || denominator == null || denominator.signum() == 0) {
            return null;
        }
        return numerator.divide(denominator, SCALE, RoundingMode.HALF_UP);
    }

    /** Dynamika (current - prior) / prior; null jeśli brak danej lub prior zerowy. */
    public static BigDecimal growth(BigDecimal current, BigDecimal prior) {
        if (current == null || prior == null || prior.signum() == 0) {
            return null;
        }
        return current.subtract(prior).divide(prior, SCALE, RoundingMode.HALF_UP);
    }
}
