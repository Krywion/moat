package com.moat.market;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class YahooMarketDataProviderTest {

    private final ObjectMapper mapper = new ObjectMapper();
    private final YahooMarketDataProvider provider = new YahooMarketDataProvider(
            new MarketProperties("https://query1.finance.yahoo.com", ".WA", 3000, true));

    private JsonNode json(String s) throws Exception {
        return mapper.readTree(s);
    }

    @Test
    void parsesQuoteResponse() throws Exception {
        JsonNode root = json("""
                {"quoteResponse":{"result":[
                  {"regularMarketPrice":12.5,"marketCap":2000000,"trailingAnnualDividendYield":0.03}
                ]}}""");

        Optional<MarketQuote> q = provider.parseQuote(root);

        assertThat(q).isPresent();
        assertThat(q.get().sharePrice()).isEqualByComparingTo("12.5");
        assertThat(q.get().marketCap()).isEqualByComparingTo("2000000");
        assertThat(q.get().dividendYield()).isEqualByComparingTo("0.03");
    }

    @Test
    void emptyResultArrayYieldsEmpty() throws Exception {
        assertThat(provider.parseQuote(json("{\"quoteResponse\":{\"result\":[]}}"))).isEmpty();
    }

    @Test
    void nullRootYieldsEmpty() {
        assertThat(provider.parseQuote(null)).isEmpty();
    }
}
