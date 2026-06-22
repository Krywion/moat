package com.moat.esef;

import com.moat.pipeline.FinancialData;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class EsefFinancialMapperTest {

    private final EsefFinancialMapper mapper =
            new EsefFinancialMapper(new ContextResolver(), new IxbrlValueParser());

    private static final Context CUR =
            new Context("C", false, LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31), false);

    private Fact f(String tag, String value) {
        return new Fact(tag, "C", "U", 0, false, "ixt:num-dot-decimal", value);
    }

    private ParsedDocument doc(List<Fact> facts) {
        return new ParsedDocument(facts, Map.of("C", CUR), Map.of("U", "PLN"),
                "Rainbow Tours", LocalDate.of(2025, 12, 31));
    }

    @Test
    void mapsTagsToFinancialData() {
        List<Fact> facts = new ArrayList<>(List.of(
                f("ifrs-full:Revenue", "1000"),
                f("ifrs-full:ProfitLossFromOperatingActivities", "200"),
                f("ifrs-full:AdjustmentsForDepreciationAndAmortisationExpense", "50"),
                f("ifrs-full:ProfitLoss", "100"),
                f("ifrs-full:Equity", "500"),
                f("ifrs-full:CashFlowsFromUsedInOperatingActivities", "120"),
                f("ifrs-full:CashAndCashEquivalents", "80"),
                f("ifrs-full:OtherCurrentFinancialLiabilities", "120"),
                f("ifrs-full:OtherNoncurrentFinancialLiabilities", "180")));

        FinancialData d = mapper.map(doc(facts));

        assertThat(d.fiscalYear()).isEqualTo(2025);
        assertThat(d.currency()).isEqualTo("PLN");
        assertThat(d.revenue()).isEqualByComparingTo("1000");
        assertThat(d.ebit()).isEqualByComparingTo("200");
        assertThat(d.netProfit()).isEqualByComparingTo("100");
        assertThat(d.equity()).isEqualByComparingTo("500");
        assertThat(d.operatingCashFlow()).isEqualByComparingTo("120");
        assertThat(d.totalDebt()).isEqualByComparingTo("300"); // 120 + 180
        assertThat(d.netDebt()).isEqualByComparingTo("220");    // 300 - 80
    }

    @Test
    void usesEbitFallbackTag() {
        List<Fact> facts = List.of(
                f("ifrs-full:Revenue", "1000"),
                f("ifrs-full:OperatingProfitLoss", "150"));

        FinancialData d = mapper.map(doc(facts));

        assertThat(d.ebit()).isEqualByComparingTo("150");
    }

    @Test
    void missingTagsAreNull() {
        FinancialData d = mapper.map(doc(List.of(f("ifrs-full:Revenue", "1000"))));

        assertThat(d.revenue()).isEqualByComparingTo("1000");
        assertThat(d.equity()).isNull();
        assertThat(d.totalDebt()).isNull();
        assertThat(d.netDebt()).isNull();
    }
}
