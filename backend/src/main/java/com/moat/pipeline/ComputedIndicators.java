package com.moat.pipeline;

import com.moat.report.FinancialReport;

import java.math.BigDecimal;

public record ComputedIndicators(
        BigDecimal ebitda,
        BigDecimal operatingMargin,
        BigDecimal ebitdaMargin,
        BigDecimal netMargin,
        BigDecimal roe,
        BigDecimal debtToEquity,
        BigDecimal revenueGrowthYoy,
        BigDecimal profitGrowthYoy) {

    public static ComputedIndicators from(FinancialReport r) {
        return new ComputedIndicators(
                r.getEbitda(), r.getOperatingMargin(), r.getEbitdaMargin(), r.getNetMargin(),
                r.getRoe(), r.getDebtToEquity(), r.getRevenueGrowthYoy(), r.getProfitGrowthYoy());
    }
}
