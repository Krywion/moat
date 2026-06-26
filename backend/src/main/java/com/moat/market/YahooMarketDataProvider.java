package com.moat.market;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Nieoficjalne Yahoo Finance (query1.finance.yahoo.com). Endpoint /v7/finance/quote
 * wymaga uwierzytelnionej sesji: cookie (z fc.yahoo.com) + crumb (z /v1/test/getcrumb).
 * Bez nich Yahoo zwraca 401/429. Best-effort: każdy błąd HTTP/parsowania → {@link Optional#empty()}.
 */
@Slf4j
@Component
public class YahooMarketDataProvider implements MarketDataProvider {

    /** Bootstrap cookie — fc.yahoo.com ustawia A1/A3 nawet odpowiadając 404. */
    private static final String COOKIE_URL = "https://fc.yahoo.com/";
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64)";

    private final RestClient restClient;
    /** Uwierzytelniona sesja Yahoo (cookie + crumb), cache'owana między wywołaniami. */
    private volatile Session session;

    private record Session(String cookie, String crumb) {}

    @Autowired
    public YahooMarketDataProvider(MarketProperties properties) {
        this(buildRestClient(properties));
    }

    /** Seam testowy — pozwala wpiąć RestClient z MockRestServiceServer. */
    YahooMarketDataProvider(RestClient restClient) {
        this.restClient = restClient;
    }

    private static RestClient buildRestClient(MarketProperties properties) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(properties.timeoutMs());
        factory.setReadTimeout(properties.timeoutMs());
        return RestClient.builder()
                .requestFactory(factory)
                .baseUrl(properties.baseUrl())
                .defaultHeader(HttpHeaders.USER_AGENT, USER_AGENT)
                .build();
    }

    @Override
    public Optional<MarketQuote> fetch(String symbol) {
        try {
            try {
                return parseQuote(requestQuote(symbol, session()));
            } catch (HttpClientErrorException.Unauthorized e) {
                // crumb/cookie wygasł — odśwież sesję raz i ponów
                session = null;
                return parseQuote(requestQuote(symbol, session()));
            }
        } catch (RuntimeException e) {
            log.warn("Pobranie danych rynkowych z Yahoo nie powiodło się dla symbol={}: {}",
                    symbol, e.toString());
            return Optional.empty();
        }
    }

    private Session session() {
        Session current = session;
        if (current == null) {
            String cookie = fetchCookie();
            current = new Session(cookie, fetchCrumb(cookie));
            session = current;
        }
        return current;
    }

    /** Bootstrap: pobiera cookie z Set-Cookie (fc.yahoo.com odpowiada 404, ale ustawia ciasteczka). */
    private String fetchCookie() {
        ResponseEntity<Void> response = restClient.get()
                .uri(COOKIE_URL)
                .retrieve()
                .onStatus(status -> true, (request, res) -> { }) // nie traktuj 4xx jako błędu
                .toBodilessEntity();
        List<String> setCookies = response.getHeaders().get(HttpHeaders.SET_COOKIE);
        if (setCookies == null || setCookies.isEmpty()) {
            throw new IllegalStateException("Yahoo nie zwrócił cookie z " + COOKIE_URL);
        }
        return setCookies.stream()
                .map(cookie -> cookie.split(";", 2)[0])
                .collect(Collectors.joining("; "));
    }

    private String fetchCrumb(String cookie) {
        return restClient.get()
                .uri("/v1/test/getcrumb")
                .header(HttpHeaders.COOKIE, cookie)
                .retrieve()
                .body(String.class);
    }

    private JsonNode requestQuote(String symbol, Session session) {
        return restClient.get()
                .uri("/v7/finance/quote?symbols={symbol}&crumb={crumb}", symbol, session.crumb())
                .header(HttpHeaders.COOKIE, session.cookie())
                .retrieve()
                .body(JsonNode.class);
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
