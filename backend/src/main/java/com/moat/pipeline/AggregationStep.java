package com.moat.pipeline;

import com.moat.api.model.FinancialForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AggregationStep implements PipelineStep {

    @Override
    public void execute(PipelineContext context) {
        if (context.getFinancialData() != null) {
            // wariant ESEF: dane już zagregowane przez parser
            log.debug("Aggregation skipped (ESEF): company={} year={}",
                    context.getCompany().getId(), context.getFinancialData().fiscalYear());
            return;
        }
        FinancialForm f = context.getInput();
        context.setFinancialData(new FinancialData(
                f.getFiscalYear(), f.getCurrency(), f.getRevenue(), f.getEbit(), f.getDepreciation(),
                f.getNetProfit(), f.getTotalDebt(), f.getNetDebt(), f.getEquity(), f.getOperatingCashFlow()));
        log.debug("Aggregation from form: company={} year={} currency={}",
                context.getCompany().getId(), f.getFiscalYear(), f.getCurrency());
    }
}
