package com.moat.pipeline;

import com.moat.market.MarketEnricher;
import org.springframework.stereotype.Component;

/**
 * Krok 4 — wzbogacenie danymi rynkowymi (Etap 2). Best-effort: brak tickera lub
 * błąd API → dane rynkowe pozostają nieustawione (pola w raporcie zostaną null).
 * Wycena liczona bieżącym kursem dla przetwarzanego raportu (dla starszych lat
 * to przybliżenie).
 */
@Component
public class EnrichmentStep implements PipelineStep {

    private final MarketEnricher marketEnricher;

    public EnrichmentStep(MarketEnricher marketEnricher) {
        this.marketEnricher = marketEnricher;
    }

    @Override
    public void execute(PipelineContext context) {
        FinancialData d = context.getFinancialData();
        ComputedIndicators i = context.getIndicators();
        marketEnricher.enrich(context.getCompany().getTicker(),
                        d.netProfit(), d.equity(), i.ebitda(), d.netDebt())
                .ifPresent(context::setMarketData);
    }
}
