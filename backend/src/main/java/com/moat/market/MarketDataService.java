package com.moat.market;

import com.moat.company.Company;
import com.moat.company.CompanyNotFoundException;
import com.moat.company.CompanyRepository;
import com.moat.report.FinancialReport;
import com.moat.report.FinancialReportRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Odświeżenie danych rynkowych najnowszego raportu spółki (bieżący kurs).
 * Owner-scoped. Best-effort: brak danych z API → pola rynkowe pozostają jak były.
 */
@Service
public class MarketDataService {

    private final CompanyRepository companyRepository;
    private final FinancialReportRepository reportRepository;
    private final MarketEnricher marketEnricher;

    public MarketDataService(CompanyRepository companyRepository,
                             FinancialReportRepository reportRepository,
                             MarketEnricher marketEnricher) {
        this.companyRepository = companyRepository;
        this.reportRepository = reportRepository;
        this.marketEnricher = marketEnricher;
    }

    @Transactional
    public void refreshLatest(UUID ownerId, UUID companyId) {
        Company company = companyRepository.findByIdAndOwnerId(companyId, ownerId)
                .orElseThrow(() -> new CompanyNotFoundException(companyId));

        reportRepository.findFirstByCompanyIdOrderByFiscalYearDesc(companyId).ifPresent(report ->
                marketEnricher.enrich(company.getTicker(), report.getNetProfit(), report.getEquity(),
                                report.getEbitda(), report.getNetDebt())
                        .ifPresent(market -> {
                            applyTo(report, market);
                            reportRepository.save(report);
                        }));
    }

    public static void applyTo(FinancialReport report, MarketData market) {
        report.setSharePrice(market.sharePrice());
        report.setMarketCap(market.marketCap());
        report.setPe(market.pe());
        report.setEvEbitda(market.evEbitda());
        report.setPbv(market.pbv());
        report.setDividendYield(market.dividendYield());
    }
}
