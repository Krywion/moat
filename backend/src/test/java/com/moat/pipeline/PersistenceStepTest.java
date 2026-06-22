package com.moat.pipeline;

import com.moat.company.Company;
import com.moat.company.dto.FinancialForm;
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
}
