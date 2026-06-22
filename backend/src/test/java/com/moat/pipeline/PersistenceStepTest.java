package com.moat.pipeline;

import com.moat.company.Company;
import com.moat.company.dto.FinancialForm;
import com.moat.market.MarketData;
import com.moat.report.FinancialReport;
import com.moat.report.FinancialReportRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PersistenceStepTest {

    private final FinancialReportRepository reportRepository = mock(FinancialReportRepository.class);
    private final PersistenceStep step = new PersistenceStep(reportRepository);

    @Test
    void buildsAndSavesReportFromContext() {
        Company company = new Company();
        company.setId(UUID.randomUUID());
        when(reportRepository.findByCompanyIdAndFiscalYear(any(), anyInt())).thenReturn(Optional.empty());
        when(reportRepository.save(any(FinancialReport.class))).thenAnswer(i -> i.getArgument(0));

        FinancialForm form = new FinancialForm(2024, "PLN", BigDecimal.valueOf(1000), null, null,
                BigDecimal.valueOf(100), null, null, BigDecimal.valueOf(500), null);
        PipelineContext ctx = new PipelineContext(company, form);
        ctx.setFinancialData(new FinancialData(2024, "PLN", BigDecimal.valueOf(1000), null, null,
                BigDecimal.valueOf(100), null, null, BigDecimal.valueOf(500), null));
        ctx.setIndicators(new ComputedIndicators(null, null, null, BigDecimal.valueOf(0.1),
                BigDecimal.valueOf(0.2), null, null, null));
        ctx.setFlags(List.of());

        step.execute(ctx);

        FinancialReport saved = ctx.getReport();
        assertThat(saved.getFiscalYear()).isEqualTo(2024);
        assertThat(saved.getRevenue()).isEqualByComparingTo("1000");
        assertThat(saved.getNetMargin()).isEqualByComparingTo("0.1");
        assertThat(saved.getCreatedAt()).isNotNull();
    }

    @Test
    void persistsMarketDataWhenPresent() {
        Company company = new Company();
        company.setId(UUID.randomUUID());
        when(reportRepository.findByCompanyIdAndFiscalYear(any(), anyInt())).thenReturn(Optional.empty());
        when(reportRepository.save(any(FinancialReport.class))).thenAnswer(i -> i.getArgument(0));

        PipelineContext ctx = new PipelineContext(company, new FinancialData(2024, "PLN",
                BigDecimal.valueOf(1000), null, null, BigDecimal.valueOf(100), null, null,
                BigDecimal.valueOf(500), null));
        ctx.setIndicators(new ComputedIndicators(null, null, null, null, null, null, null, null));
        ctx.setMarketData(new MarketData(BigDecimal.valueOf(12.5), BigDecimal.valueOf(2000),
                BigDecimal.valueOf(20), BigDecimal.valueOf(9.2), BigDecimal.valueOf(4),
                BigDecimal.valueOf(0.03)));

        step.execute(ctx);

        FinancialReport saved = ctx.getReport();
        assertThat(saved.getSharePrice()).isEqualByComparingTo("12.5");
        assertThat(saved.getMarketCap()).isEqualByComparingTo("2000");
        assertThat(saved.getPe()).isEqualByComparingTo("20");
        assertThat(saved.getPbv()).isEqualByComparingTo("4");
        assertThat(saved.getDividendYield()).isEqualByComparingTo("0.03");
    }
}
