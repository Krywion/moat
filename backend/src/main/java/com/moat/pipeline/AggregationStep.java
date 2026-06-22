package com.moat.pipeline;

import com.moat.company.dto.FinancialForm;
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
                f.fiscalYear(), f.currency(), f.revenue(), f.ebit(), f.depreciation(),
                f.netProfit(), f.totalDebt(), f.netDebt(), f.equity(), f.operatingCashFlow()));
    }
}
