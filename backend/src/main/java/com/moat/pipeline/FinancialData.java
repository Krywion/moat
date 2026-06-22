package com.moat.pipeline;

import com.moat.report.FinancialReport;

import java.math.BigDecimal;

public record FinancialData(
        int fiscalYear,
        String currency,
        BigDecimal revenue,
        BigDecimal ebit,
        BigDecimal depreciation,
        BigDecimal netProfit,
        BigDecimal totalDebt,
        BigDecimal netDebt,
        BigDecimal equity,
        BigDecimal operatingCashFlow) {

    public static FinancialData from(FinancialReport r) {
        return new FinancialData(
                r.getFiscalYear(), r.getCurrency(), r.getRevenue(), r.getEbit(),
                r.getDepreciation(), r.getNetProfit(), r.getTotalDebt(), r.getNetDebt(),
                r.getEquity(), r.getOperatingCashFlow());
    }
}
