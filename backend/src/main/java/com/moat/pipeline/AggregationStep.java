package com.moat.pipeline;

import com.moat.company.dto.FinancialForm;
import org.springframework.stereotype.Component;

@Component
public class AggregationStep implements PipelineStep {

    @Override
    public void execute(PipelineContext context) {
        FinancialForm f = context.getInput();
        context.setFinancialData(new FinancialData(
                f.fiscalYear(), f.currency(), f.revenue(), f.ebit(), f.depreciation(),
                f.netProfit(), f.totalDebt(), f.netDebt(), f.equity(), f.operatingCashFlow()));
    }
}
