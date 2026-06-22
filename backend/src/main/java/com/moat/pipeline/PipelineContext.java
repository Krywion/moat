package com.moat.pipeline;

import com.moat.company.Company;
import com.moat.company.dto.FinancialForm;
import com.moat.market.MarketData;
import com.moat.report.FinancialReport;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
public class PipelineContext {

    private final Company company;
    private final FinancialForm input;

    @Setter private FinancialData financialData;
    @Setter private ComputedIndicators indicators;
    @Setter private List<WarningFlag> flags;
    @Setter private MarketData marketData;
    @Setter private FinancialReport report;

    public PipelineContext(Company company, FinancialForm input) {
        this.company = company;
        this.input = input;
    }

    public PipelineContext(Company company, FinancialData financialData) {
        this.company = company;
        this.input = null;
        this.financialData = financialData;
    }
}
