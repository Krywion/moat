package com.moat.company.dto;

import com.moat.pipeline.ComputedIndicators;
import com.moat.pipeline.FinancialData;
import com.moat.pipeline.WarningFlag;
import com.moat.pipeline.WarningFlagEvaluator;
import com.moat.report.FinancialReport;

import java.math.BigDecimal;
import java.util.List;

public record FinancialReportResponse(
        int fiscalYear,
        String currency,
        BigDecimal revenue,
        BigDecimal ebit,
        BigDecimal depreciation,
        BigDecimal netProfit,
        BigDecimal totalDebt,
        BigDecimal netDebt,
        BigDecimal equity,
        BigDecimal operatingCashFlow,
        BigDecimal ebitda,
        BigDecimal operatingMargin,
        BigDecimal ebitdaMargin,
        BigDecimal netMargin,
        BigDecimal roe,
        BigDecimal debtToEquity,
        BigDecimal revenueGrowthYoy,
        BigDecimal profitGrowthYoy,
        BigDecimal sharePrice,
        BigDecimal marketCap,
        BigDecimal pe,
        BigDecimal evEbitda,
        BigDecimal pbv,
        BigDecimal dividendYield,
        List<WarningFlag> flags) {

    public static FinancialReportResponse from(FinancialReport report, FinancialReport prior,
                                               WarningFlagEvaluator evaluator) {
        FinancialData currentData = FinancialData.from(report);
        ComputedIndicators currentInd = ComputedIndicators.from(report);
        FinancialData priorData = prior == null ? null : FinancialData.from(prior);
        ComputedIndicators priorInd = prior == null ? null : ComputedIndicators.from(prior);
        List<WarningFlag> flags = evaluator.evaluate(currentData, currentInd, priorData, priorInd);

        return new FinancialReportResponse(
                report.getFiscalYear(), report.getCurrency(), report.getRevenue(), report.getEbit(),
                report.getDepreciation(), report.getNetProfit(), report.getTotalDebt(),
                report.getNetDebt(), report.getEquity(), report.getOperatingCashFlow(),
                report.getEbitda(), report.getOperatingMargin(), report.getEbitdaMargin(),
                report.getNetMargin(), report.getRoe(), report.getDebtToEquity(),
                report.getRevenueGrowthYoy(), report.getProfitGrowthYoy(),
                report.getSharePrice(), report.getMarketCap(), report.getPe(),
                report.getEvEbitda(), report.getPbv(), report.getDividendYield(), flags);
    }
}
