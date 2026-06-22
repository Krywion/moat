package com.moat.pipeline;

import com.moat.company.Company;
import com.moat.api.model.FinancialForm;
import com.moat.report.FinancialReportRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CalculationStepTest {

    private final FinancialReportRepository reportRepository = mock(FinancialReportRepository.class);
    private final CalculationStep step = new CalculationStep(
            reportRepository, new IndicatorCalculator(), new WarningFlagEvaluator());

    private FinancialForm form(int year, long revenue, long ebit, long netProfit) {
        return new FinancialForm().fiscalYear(year).currency("PLN")
                .revenue(BigDecimal.valueOf(revenue)).ebit(BigDecimal.valueOf(ebit))
                .depreciation(BigDecimal.valueOf(10)).netProfit(BigDecimal.valueOf(netProfit))
                .totalDebt(BigDecimal.valueOf(100)).equity(BigDecimal.valueOf(500))
                .operatingCashFlow(BigDecimal.valueOf(50));
    }

    @Test
    void computesIndicatorsAndSetsThemOnContext() {
        Company company = new Company();
        company.setId(UUID.randomUUID());
        when(reportRepository.findByCompanyIdAndFiscalYear(any(), eq(2023)))
                .thenReturn(Optional.empty());

        PipelineContext ctx = new PipelineContext(company, form(2024, 1000, 200, 100));
        ctx.setFinancialData(new FinancialData(2024, "PLN", BigDecimal.valueOf(1000), BigDecimal.valueOf(200),
                BigDecimal.valueOf(10), BigDecimal.valueOf(100), BigDecimal.valueOf(100), null,
                BigDecimal.valueOf(500), BigDecimal.valueOf(50)));

        step.execute(ctx);

        assertThat(ctx.getIndicators().netMargin()).isEqualByComparingTo("0.1000");
        assertThat(ctx.getFlags()).isNotNull();
    }
}
