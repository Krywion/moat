package com.moat.market;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.ExpectedCount.times;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.queryParam;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class YahooMarketDataProviderTest {

    private static final String BASE_URL = "https://query1.finance.yahoo.com";
    private static final String QUOTE_JSON = """
            {"quoteResponse":{"result":[
              {"regularMarketPrice":12.5,"marketCap":2000000,"trailingAnnualDividendYield":0.03}
            ]}}""";

    private final ObjectMapper mapper = new ObjectMapper();

    private JsonNode json(String s) throws Exception {
        return mapper.readTree(s);
    }

    /** Buduje provider z RestClientem wpiętym w MockRestServiceServer. */
    private record Fixture(YahooMarketDataProvider provider, MockRestServiceServer server) {}

    private Fixture fixture() {
        RestClient.Builder builder = RestClient.builder().baseUrl(BASE_URL);
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        return new Fixture(new YahooMarketDataProvider(builder.build()), server);
    }

    private void expectCookieBootstrap(MockRestServiceServer server, String cookie, String crumb) {
        server.expect(requestTo(containsString("fc.yahoo.com")))
                .andRespond(withStatus(HttpStatus.NOT_FOUND).header(HttpHeaders.SET_COOKIE, cookie + "; Path=/"));
        server.expect(requestTo(containsString("/v1/test/getcrumb")))
                .andExpect(header(HttpHeaders.COOKIE, cookie))
                .andRespond(withSuccess(crumb, MediaType.TEXT_PLAIN));
    }

    @Test
    void fetchSendsCookieAndCrumbThenParsesQuote() {
        Fixture f = fixture();
        expectCookieBootstrap(f.server, "A1=token", "crumb123");
        f.server.expect(requestTo(containsString("/v7/finance/quote")))
                .andExpect(queryParam("symbols", "KTY.WA"))
                .andExpect(queryParam("crumb", "crumb123"))
                .andExpect(header(HttpHeaders.COOKIE, "A1=token"))
                .andRespond(withSuccess(QUOTE_JSON, MediaType.APPLICATION_JSON));

        Optional<MarketQuote> q = f.provider.fetch("KTY.WA");

        assertThat(q).isPresent();
        assertThat(q.get().sharePrice()).isEqualByComparingTo("12.5");
        assertThat(q.get().marketCap()).isEqualByComparingTo("2000000");
        f.server.verify();
    }

    @Test
    void reusesCookieAndCrumbAcrossCalls() {
        RestClient.Builder builder = RestClient.builder().baseUrl(BASE_URL);
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).ignoreExpectOrder(true).build();
        YahooMarketDataProvider provider = new YahooMarketDataProvider(builder.build());

        server.expect(once(), requestTo(containsString("fc.yahoo.com")))
                .andRespond(withStatus(HttpStatus.NOT_FOUND).header(HttpHeaders.SET_COOKIE, "A1=token; Path=/"));
        server.expect(once(), requestTo(containsString("/v1/test/getcrumb")))
                .andRespond(withSuccess("crumb123", MediaType.TEXT_PLAIN));
        server.expect(times(2), requestTo(containsString("/v7/finance/quote")))
                .andExpect(queryParam("crumb", "crumb123"))
                .andRespond(withSuccess(QUOTE_JSON, MediaType.APPLICATION_JSON));

        assertThat(provider.fetch("KTY.WA")).isPresent();
        assertThat(provider.fetch("RBW.WA")).isPresent();
        server.verify(); // bootstrap dokładnie raz, quote dwa razy
    }

    @Test
    void refreshesSessionAndRetriesOnUnauthorized() {
        Fixture f = fixture();
        // pierwszy bootstrap → crumb wygasły, quote zwraca 401
        expectCookieBootstrap(f.server, "A1=old", "stale");
        f.server.expect(requestTo(containsString("/v7/finance/quote")))
                .andExpect(queryParam("crumb", "stale"))
                .andRespond(withStatus(HttpStatus.UNAUTHORIZED));
        // odświeżenie sesji i ponowienie → sukces
        expectCookieBootstrap(f.server, "A1=fresh", "valid");
        f.server.expect(requestTo(containsString("/v7/finance/quote")))
                .andExpect(queryParam("crumb", "valid"))
                .andRespond(withSuccess(QUOTE_JSON, MediaType.APPLICATION_JSON));

        Optional<MarketQuote> q = f.provider.fetch("KTY.WA");

        assertThat(q).isPresent();
        f.server.verify();
    }

    @Test
    void springInstantiatesBeanDespiteTestSeamConstructor() {
        // dwa konstruktory (produkcyjny + seam testowy) → Spring musi wiedzieć, który wybrać
        try (AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext()) {
            ctx.registerBean(MarketProperties.class,
                    () -> new MarketProperties(BASE_URL, ".WA", 3000, true));
            ctx.register(YahooMarketDataProvider.class);
            ctx.refresh();

            assertThat(ctx.getBean(YahooMarketDataProvider.class)).isNotNull();
        }
    }

    // --- testy parsowania (bez sieci) ---

    @Test
    void parsesQuoteResponse() throws Exception {
        YahooMarketDataProvider provider = fixture().provider();
        Optional<MarketQuote> q = provider.parseQuote(json(QUOTE_JSON));

        assertThat(q).isPresent();
        assertThat(q.get().sharePrice()).isEqualByComparingTo("12.5");
        assertThat(q.get().marketCap()).isEqualByComparingTo("2000000");
        assertThat(q.get().dividendYield()).isEqualByComparingTo("0.03");
    }

    @Test
    void emptyResultArrayYieldsEmpty() throws Exception {
        YahooMarketDataProvider provider = fixture().provider();
        assertThat(provider.parseQuote(json("{\"quoteResponse\":{\"result\":[]}}"))).isEmpty();
    }

    @Test
    void nullRootYieldsEmpty() {
        YahooMarketDataProvider provider = fixture().provider();
        assertThat(provider.parseQuote(null)).isEmpty();
    }
}
