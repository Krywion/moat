package com.moat.pipeline;

import com.moat.market.MarketData;
import com.moat.market.MarketDataService;
import com.moat.report.FinancialReport;
import com.moat.report.FinancialReportRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Slf4j
@Component
public class PersistenceStep implements PipelineStep {

    private final FinancialReportRepository reportRepository;

    public PersistenceStep(FinancialReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    @Override
    public void execute(PipelineContext context) {
        FinancialData d = context.getFinancialData();
        ComputedIndicators i = context.getIndicators();

        boolean[] created = {false};
        FinancialReport report = reportRepository
                .findByCompanyIdAndFiscalYear(context.getCompany().getId(), d.fiscalYear())
                .orElseGet(() -> {
                    created[0] = true;
                    FinancialReport r = new FinancialReport();
                    r.setCompany(context.getCompany());
                    r.setFiscalYear(d.fiscalYear());
                    r.setCreatedAt(OffsetDateTime.now());
                    return r;
                });

        report.setCurrency(d.currency());
        report.setRevenue(d.revenue());
        report.setEbit(d.ebit());
        report.setDepreciation(d.depreciation());
        report.setNetProfit(d.netProfit());
        report.setTotalDebt(d.totalDebt());
        report.setNetDebt(d.netDebt());
        report.setEquity(d.equity());
        report.setOperatingCashFlow(d.operatingCashFlow());

        report.setEbitda(i.ebitda());
        report.setOperatingMargin(i.operatingMargin());
        report.setEbitdaMargin(i.ebitdaMargin());
        report.setNetMargin(i.netMargin());
        report.setRoe(i.roe());
        report.setDebtToEquity(i.debtToEquity());
        report.setRevenueGrowthYoy(i.revenueGrowthYoy());
        report.setProfitGrowthYoy(i.profitGrowthYoy());

        MarketData market = context.getMarketData();
        if (market != null) {
            MarketDataService.applyTo(report, market);
        }

        FinancialReport saved = reportRepository.save(report);
        context.setReport(saved);
        log.debug("Persistence: company={} year={} reportId={} operation={}",
                context.getCompany().getId(), d.fiscalYear(), saved.getId(),
                created[0] ? "insert" : "update");
    }
}
