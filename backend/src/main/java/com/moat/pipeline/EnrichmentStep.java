package com.moat.pipeline;

import com.moat.market.MarketEnricher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Krok 4 — wzbogacenie danymi rynkowymi (Etap 2). Best-effort: brak tickera lub
 * błąd API → dane rynkowe pozostają nieustawione (pola w raporcie zostaną null).
 * Wycena liczona bieżącym kursem dla przetwarzanego raportu (dla starszych lat
 * to przybliżenie).
 */
@Slf4j
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
        String ticker = context.getCompany().getTicker();
        var market = marketEnricher.enrich(ticker, d.netProfit(), d.equity(), i.ebitda(), d.netDebt());
        market.ifPresent(context::setMarketData);
        log.debug("Enrichment: company={} ticker={} marketData={}",
                context.getCompany().getId(), ticker, market.isPresent() ? "applied" : "absent");
    }
}
