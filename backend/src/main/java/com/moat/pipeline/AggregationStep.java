package com.moat.pipeline;

import com.moat.api.model.FinancialForm;
import org.springframework.stereotype.Component;

@Component
public class AggregationStep implements PipelineStep {

    @Override
    public void execute(PipelineContext context) {
        if (context.getFinancialData() != null) {
            return; // wariant ESEF: dane już zagregowane przez parser
        }
        FinancialForm f = context.getInput();
        context.setFinancialData(new FinancialData(
                f.getFiscalYear(), f.getCurrency(), f.getRevenue(), f.getEbit(), f.getDepreciation(),
                f.getNetProfit(), f.getTotalDebt(), f.getNetDebt(), f.getEquity(), f.getOperatingCashFlow()));
    }
}
