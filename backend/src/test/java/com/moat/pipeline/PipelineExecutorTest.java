package com.moat.pipeline;

import com.moat.company.Company;
import com.moat.api.model.FinancialForm;
import com.moat.report.FinancialReport;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;

class PipelineExecutorTest {

    private final AggregationStep aggregation = mock(AggregationStep.class);
    private final CalculationStep calculation = mock(CalculationStep.class);
    private final EnrichmentStep enrichment = mock(EnrichmentStep.class);
    private final PersistenceStep persistence = mock(PersistenceStep.class);
    private final PipelineExecutor executor =
            new PipelineExecutor(aggregation, calculation, enrichment, persistence);

    @Test
    void runsStepsInOrderAndReturnsReport() {
        Company company = new Company();
        FinancialReport expected = new FinancialReport();
        FinancialForm form = new FinancialForm().fiscalYear(2024).currency("PLN")
                .revenue(BigDecimal.valueOf(1000));
        PipelineContext ctx = new PipelineContext(company, form);
        doAnswer(inv -> {
            ((PipelineContext) inv.getArgument(0)).setReport(expected);
            return null;
        }).when(persistence).execute(ctx);

        FinancialReport result = executor.run(ctx);

        InOrder order = inOrder(aggregation, calculation, enrichment, persistence);
        order.verify(aggregation).execute(ctx);
        order.verify(calculation).execute(ctx);
        order.verify(enrichment).execute(ctx);
        order.verify(persistence).execute(ctx);
        assertThat(result).isSameAs(expected);
    }
}
