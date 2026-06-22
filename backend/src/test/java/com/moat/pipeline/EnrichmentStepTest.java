package com.moat.pipeline;

import com.moat.company.Company;
import com.moat.market.MarketData;
import com.moat.market.MarketEnricher;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class EnrichmentStepTest {

    private final MarketEnricher enricher = mock(MarketEnricher.class);
    private final EnrichmentStep step = new EnrichmentStep(enricher);

    private PipelineContext context() {
        Company company = new Company();
        company.setId(UUID.randomUUID());
        company.setTicker("RBW");
        PipelineContext ctx = new PipelineContext(company, new FinancialData(2024, "PLN",
                BigDecimal.valueOf(1000), BigDecimal.valueOf(200), BigDecimal.valueOf(50),
                BigDecimal.valueOf(100), BigDecimal.valueOf(300), BigDecimal.valueOf(150),
                BigDecimal.valueOf(500), BigDecimal.valueOf(120)));
        ctx.setIndicators(new ComputedIndicators(BigDecimal.valueOf(250), null, null, null, null, null, null, null));
        return ctx;
    }

    @Test
    void setsMarketDataWhenEnricherReturns() {
        MarketData m = new MarketData(BigDecimal.valueOf(12.5), BigDecimal.valueOf(2000),
                BigDecimal.valueOf(20), BigDecimal.valueOf(9.2), BigDecimal.valueOf(4), BigDecimal.valueOf(0.03));
        when(enricher.enrich(any(), any(), any(), any(), any())).thenReturn(Optional.of(m));

        PipelineContext ctx = context();
        step.execute(ctx);

        assertThat(ctx.getMarketData()).isSameAs(m);
    }

    @Test
    void leavesMarketDataNullWhenEnricherEmpty() {
        when(enricher.enrich(any(), any(), any(), any(), any())).thenReturn(Optional.empty());

        PipelineContext ctx = context();
        step.execute(ctx);

        assertThat(ctx.getMarketData()).isNull();
    }
}
