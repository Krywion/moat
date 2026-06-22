package com.moat.pipeline;

import com.moat.report.FinancialReportRepository;
import org.springframework.stereotype.Component;

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

        context.setIndicators(indicators);
        context.setFlags(flagEvaluator.evaluate(current, indicators, prior, priorIndicators));
    }
}
