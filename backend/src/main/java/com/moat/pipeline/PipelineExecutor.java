package com.moat.pipeline;

import com.moat.report.FinancialReport;
import org.springframework.stereotype.Component;

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
        aggregation.execute(context);
        calculation.execute(context);
        enrichment.execute(context);
        persistence.execute(context);
        return context.getReport();
    }
}
