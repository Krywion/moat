package com.moat.market;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MarketEnricherTest {

    private final MarketDataProvider provider = mock(MarketDataProvider.class);
    private final MarketProperties props =
            new MarketProperties("https://example.com", ".WA", 3000, true);
    private final MarketEnricher enricher =
            new MarketEnricher(provider, new MarketValuationCalculator(), props);

    private final BigDecimal netProfit = BigDecimal.valueOf(100);
    private final BigDecimal equity = BigDecimal.valueOf(500);
    private final BigDecimal ebitda = BigDecimal.valueOf(250);
    private final BigDecimal netDebt = BigDecimal.valueOf(300);

    private MarketQuote quote() {
        return new MarketQuote(BigDecimal.valueOf(12.5), BigDecimal.valueOf(2000), BigDecimal.valueOf(0.03));
    }

    @Test
    void appendsDefaultSuffixToBareTicker() {
        when(provider.fetch("RBW.WA")).thenReturn(Optional.of(quote()));

        Optional<MarketData> result = enricher.enrich("RBW", netProfit, equity, ebitda, netDebt);

        assertThat(result).isPresent();
        assertThat(result.get().pe()).isEqualByComparingTo("20.0000");
    }

    @Test
    void usesTickerWithDotAsIs() {
        when(provider.fetch("RBW.WA")).thenReturn(Optional.of(quote()));

        assertThat(enricher.enrich("RBW.WA", netProfit, equity, ebitda, netDebt)).isPresent();
    }

    @Test
    void nullOrBlankTickerSkipsProvider() {
        assertThat(enricher.enrich(null, netProfit, equity, ebitda, netDebt)).isEmpty();
        assertThat(enricher.enrich("  ", netProfit, equity, ebitda, netDebt)).isEmpty();
        verify(provider, never()).fetch(any());
    }

    @Test
    void disabledReturnsEmpty() {
        MarketEnricher disabled = new MarketEnricher(provider, new MarketValuationCalculator(),
                new MarketProperties("https://example.com", ".WA", 3000, false));

        assertThat(disabled.enrich("RBW", netProfit, equity, ebitda, netDebt)).isEmpty();
        verify(provider, never()).fetch(any());
    }

    @Test
    void providerEmptyOrThrowsYieldsEmpty() {
        when(provider.fetch("EMPTY.WA")).thenReturn(Optional.empty());
        when(provider.fetch("BOOM.WA")).thenThrow(new RuntimeException("network"));

        assertThat(enricher.enrich("EMPTY", netProfit, equity, ebitda, netDebt)).isEmpty();
        assertThat(enricher.enrich("BOOM", netProfit, equity, ebitda, netDebt)).isEmpty();
    }
}
