package com.moat.company;

import com.moat.api.model.CompanyDetailResponse;
import com.moat.api.model.CompanySummaryResponse;
import com.moat.api.model.FinancialReportResponse;
import com.moat.api.model.WarningFlag;
import com.moat.pipeline.ComputedIndicators;
import com.moat.pipeline.FinancialData;
import com.moat.pipeline.WarningFlagEvaluator;
import com.moat.report.FinancialReport;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CompanyMapper {

    private final WarningFlagEvaluator flagEvaluator;

    public CompanyMapper(WarningFlagEvaluator flagEvaluator) {
        this.flagEvaluator = flagEvaluator;
    }

    public CompanySummaryResponse toSummary(Company company, FinancialReport latest) {
        return new CompanySummaryResponse()
                .id(company.getId())
                .name(company.getName())
                .ticker(company.getTicker())
                .latestFiscalYear(latest == null ? null : latest.getFiscalYear())
                .netMargin(latest == null ? null : latest.getNetMargin());
    }

    public CompanyDetailResponse toDetail(Company company, List<FinancialReport> reports) {
        List<FinancialReportResponse> mapped = new ArrayList<>();
        FinancialReport prior = null;
        for (FinancialReport report : reports) {
            FinancialReport priorForThis =
                    (prior != null && prior.getFiscalYear() == report.getFiscalYear() - 1) ? prior : null;
            mapped.add(toReport(report, priorForThis));
            prior = report;
        }
        return new CompanyDetailResponse()
                .id(company.getId())
                .name(company.getName())
                .ticker(company.getTicker())
                .reports(mapped);
    }

    public FinancialReportResponse toReport(FinancialReport report, FinancialReport prior) {
        FinancialData currentData = FinancialData.from(report);
        ComputedIndicators currentInd = ComputedIndicators.from(report);
        FinancialData priorData = prior == null ? null : FinancialData.from(prior);
        ComputedIndicators priorInd = prior == null ? null : ComputedIndicators.from(prior);
        List<WarningFlag> flags = flagEvaluator.evaluate(currentData, currentInd, priorData, priorInd)
                .stream().map(f -> WarningFlag.valueOf(f.name())).toList();

        return new FinancialReportResponse()
                .fiscalYear(report.getFiscalYear())
                .currency(report.getCurrency())
                .revenue(report.getRevenue())
                .ebit(report.getEbit())
                .depreciation(report.getDepreciation())
                .netProfit(report.getNetProfit())
                .totalDebt(report.getTotalDebt())
                .netDebt(report.getNetDebt())
                .equity(report.getEquity())
                .operatingCashFlow(report.getOperatingCashFlow())
                .ebitda(report.getEbitda())
                .operatingMargin(report.getOperatingMargin())
                .ebitdaMargin(report.getEbitdaMargin())
                .netMargin(report.getNetMargin())
                .roe(report.getRoe())
                .debtToEquity(report.getDebtToEquity())
                .revenueGrowthYoy(report.getRevenueGrowthYoy())
                .profitGrowthYoy(report.getProfitGrowthYoy())
                .sharePrice(report.getSharePrice())
                .marketCap(report.getMarketCap())
                .pe(report.getPe())
                .evEbitda(report.getEvEbitda())
                .pbv(report.getPbv())
                .dividendYield(report.getDividendYield())
                .flags(flags);
    }
}
