package com.moat.pipeline;

import com.moat.report.FinancialReport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PipelineExecutor {

    private final AggregationStep aggregation;
    private final CalculationStep calculation;
    private final EnrichmentStep enrichment;
    private final PersistenceStep persistence;

    public PipelineExecutor(AggregationStep aggregation, CalculationStep calculation,
                            EnrichmentStep enrichment, PersistenceStep persistence) {
        this.aggregation = aggregation;
        this.calculation = calculation;
        this.enrichment = enrichment;
        this.persistence = persistence;
    }

    public FinancialReport run(PipelineContext context) {
        var companyId = context.getCompany().getId();
        log.info("Pipeline start: company={}", companyId);
        long startedAt = System.nanoTime();
        try {
            runStep("aggregation", aggregation, context);
            runStep("calculation", calculation, context);
            runStep("enrichment", enrichment, context);
            runStep("persistence", persistence, context);

            FinancialReport report = context.getReport();
            log.info("Pipeline done: company={} year={} reportId={} elapsedMs={}",
                    companyId, report.getFiscalYear(), report.getId(), elapsedMs(startedAt));
            return report;
        } catch (RuntimeException e) {
            log.error("Pipeline failed: company={} elapsedMs={} reason={}",
                    companyId, elapsedMs(startedAt), e.toString());
            throw e;
        }
    }

    private void runStep(String name, PipelineStep step, PipelineContext context) {
        long startedAt = System.nanoTime();
        step.execute(context);
        log.debug("Step done: name={} company={} elapsedMs={}",
                name, context.getCompany().getId(), elapsedMs(startedAt));
    }

    private static long elapsedMs(long startedAtNanos) {
        return (System.nanoTime() - startedAtNanos) / 1_000_000;
    }
}
