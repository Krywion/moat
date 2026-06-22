package com.moat.esef;

import com.moat.pipeline.FinancialData;
import org.junit.jupiter.api.Test;

import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;

class EsefParserIntegrationTest {

    private final EsefParser parser = new EsefParser(
            new EsefPackageReader(),
            new IxbrlParser(),
            new EsefFinancialMapper(new ContextResolver(), new IxbrlValueParser()));

    @Test
    void parsesRealRainbowToursPackage() throws Exception {
        byte[] xbri;
        try (InputStream in = getClass().getResourceAsStream("/esef/rainbow-tours.xbri")) {
            assertThat(in).as("test resource present").isNotNull();
            xbri = in.readAllBytes();
        }

        ParsedEsef parsed = parser.parse(xbri);
        FinancialData d = parsed.data();

        assertThat(parsed.companyName()).isNotBlank();
        assertThat(d.currency()).isEqualTo("PLN");
        assertThat(d.fiscalYear()).isEqualTo(2025);
        assertThat(d.revenue()).isNotNull().matches(v -> v.signum() > 0);
        assertThat(d.equity()).isNotNull().matches(v -> v.signum() > 0);
        assertThat(d.netProfit()).isNotNull();
        assertThat(d.operatingCashFlow()).isNotNull();
        assertThat(d.totalDebt()).isNotNull().matches(v -> v.signum() > 0);
    }
}
