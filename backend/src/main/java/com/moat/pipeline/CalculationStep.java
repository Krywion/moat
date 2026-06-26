package com.moat.pipeline;

import com.moat.report.FinancialReportRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CalculationStep implements PipelineStep {

    private final FinancialReportRepository reportRepository;
    private final IndicatorCalculator calculator;
    private final WarningFlagEvaluator flagEvaluator;

    public CalculationStep(FinancialReportRepository reportRepository,
                           IndicatorCalculator calculator, WarningFlagEvaluator flagEvaluator) {
        this.reportRepository = reportRepository;
        this.calculator = calculator;
        this.flagEvaluator = flagEvaluator;
    }

    @Override
    public void execute(PipelineContext context) {
        FinancialData current = context.getFinancialData();
        FinancialData prior = reportRepository
                .findByCompanyIdAndFiscalYear(context.getCompany().getId(), current.fiscalYear() - 1)
                .map(FinancialData::from)
                .orElse(null);

        ComputedIndicators priorIndicators = prior == null ? null : calculator.calculate(prior, null);
        ComputedIndicators indicators = calculator.calculate(current, prior);

        var flags = flagEvaluator.evaluate(current, indicators, prior, priorIndicators);
        context.setIndicators(indicators);
        context.setFlags(flags);

        log.debug("Calculation: company={} year={} priorYear={} flags={}",
                context.getCompany().getId(), current.fiscalYear(),
                prior == null ? "absent" : current.fiscalYear() - 1, flags.size());
    }
}
