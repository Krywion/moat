package com.moat.market;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Nieoficjalne Yahoo Finance (query1.finance.yahoo.com). Best-effort: każdy błąd
 * HTTP/parsowania → {@link Optional#empty()}.
 */
@Component
public class YahooMarketDataProvider implements MarketDataProvider {

    private final RestClient restClient;

    public YahooMarketDataProvider(MarketProperties properties) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(properties.timeoutMs());
        factory.setReadTimeout(properties.timeoutMs());
        this.restClient = RestClient.builder()
                .requestFactory(factory)
                .baseUrl(properties.baseUrl())
                .build();
    }

    @Override
    public Optional<MarketQuote> fetch(String symbol) {
        try {
            JsonNode root = restClient.get()
                    .uri("/v7/finance/quote?symbols={symbol}", symbol)
                    .retrieve()
                    .body(JsonNode.class);
            return parseQuote(root);
        } catch (RuntimeException e) {
            return Optional.empty();
        }
    }

    /** Parsowanie odpowiedzi Yahoo (/v7/finance/quote). Wydzielone do testów. */
    Optional<MarketQuote> parseQuote(JsonNode root) {
        if (root == null) {
            return Optional.empty();
        }
        JsonNode result = root.path("quoteResponse").path("result");
        if (!result.isArray() || result.isEmpty()) {
            return Optional.empty();
        }
        JsonNode q = result.get(0);
        BigDecimal price = decimal(q, "regularMarketPrice");
        BigDecimal marketCap = decimal(q, "marketCap");
        BigDecimal dividendYield = decimal(q, "trailingAnnualDividendYield");
        if (price == null && marketCap == null) {
            return Optional.empty();
        }
        return Optional.of(new MarketQuote(price, marketCap, dividendYield));
    }

    private static BigDecimal decimal(JsonNode node, String field) {
        JsonNode value = node.get(field);
        return (value == null || value.isNull() || !value.isNumber()) ? null : value.decimalValue();
    }
}
