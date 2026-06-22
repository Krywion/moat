package com.moat.market;

import com.moat.company.Company;
import com.moat.company.CompanyNotFoundException;
import com.moat.company.CompanyRepository;
import com.moat.report.FinancialReport;
import com.moat.report.FinancialReportRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MarketDataServiceTest {

    private final CompanyRepository companyRepository = mock(CompanyRepository.class);
    private final FinancialReportRepository reportRepository = mock(FinancialReportRepository.class);
    private final MarketEnricher marketEnricher = mock(MarketEnricher.class);
    private final MarketDataService service =
            new MarketDataService(companyRepository, reportRepository, marketEnricher);

    @Test
    void refreshLatest_writesMarketFieldsToLatestReport() {
        UUID ownerId = UUID.randomUUID();
        UUID companyId = UUID.randomUUID();
        Company company = new Company();
        company.setId(companyId);
        company.setTicker("RBW");
        FinancialReport report = new FinancialReport();
        report.setNetProfit(BigDecimal.valueOf(100));
        report.setEquity(BigDecimal.valueOf(500));

        when(companyRepository.findByIdAndOwnerId(companyId, ownerId)).thenReturn(Optional.of(company));
        when(reportRepository.findFirstByCompanyIdOrderByFiscalYearDesc(companyId))
                .thenReturn(Optional.of(report));
        MarketData market = new MarketData(BigDecimal.valueOf(12.5), BigDecimal.valueOf(2000),
                BigDecimal.valueOf(20), null, BigDecimal.valueOf(4), BigDecimal.valueOf(0.03));
        when(marketEnricher.enrich(any(), any(), any(), any(), any())).thenReturn(Optional.of(market));

        service.refreshLatest(ownerId, companyId);

        assertThat(report.getMarketCap()).isEqualByComparingTo("2000");
        assertThat(report.getPe()).isEqualByComparingTo("20");
        assertThat(report.getDividendYield()).isEqualByComparingTo("0.03");
        verify(reportRepository).save(report);
    }

    @Test
    void refreshLatest_unknownOrForeignCompany_throwsNotFound() {
        UUID ownerId = UUID.randomUUID();
        UUID companyId = UUID.randomUUID();
        when(companyRepository.findByIdAndOwnerId(companyId, ownerId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.refreshLatest(ownerId, companyId))
                .isInstanceOf(CompanyNotFoundException.class);
    }
}
